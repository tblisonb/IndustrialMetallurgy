package com.onlytanner.industrialmetallurgy.tileentity;

import com.onlytanner.industrialmetallurgy.init.ModTileEntityTypes;
import com.onlytanner.industrialmetallurgy.util.ModCapabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

// Deliberately not a smart network: every tick, every Conduit in a connected run floods out
// through its neighboring Conduits (bounded depth) to find every non-Conduit capability endpoint
// reachable that way, then round-robins whatever it can move (energy, then items) from the ones
// with something to give into the ones with room to take. Only the "leader" (lowest BlockPos in
// the connected set) actually moves anything each tick -- everyone else in the run finds the same
// set and no-ops -- so a long chain doesn't multiply its own throughput. No routing intelligence,
// no priority, no filters, and one physical run of Conduits carries both resources at once rather
// than needing separate energy/item pipe types -- just "move what's available to wherever has
// room," matching this being a first pass rather than a full logistics network (see ROADMAP Part 4).
public class ConduitBlockEntity extends BlockEntity {

    private static final int MAX_SEARCH_DEPTH = 64;
    public static final int MAX_ENERGY_TRANSFER_PER_TICK = 400;
    public static final int MAX_ITEM_TRANSFER_PER_TICK = 64;

    public ConduitBlockEntity(BlockPos pos, BlockState state) {
        super(ModTileEntityTypes.CONDUIT.get(), pos, state);
    }

    public void tick() {
        if (!(this.level instanceof ServerLevel serverLevel)) {
            return;
        }

        Set<BlockPos> connectedConduits = findConnectedConduits(serverLevel);
        BlockPos leader = connectedConduits.stream().min(BlockPos::compareTo).orElse(this.getBlockPos());
        if (!leader.equals(this.getBlockPos())) {
            return;
        }

        List<EnergyHandler> energyEndpoints = findEndpoints(serverLevel, connectedConduits, Capabilities.Energy.BLOCK);
        if (energyEndpoints.size() >= 2) {
            distributeEnergy(energyEndpoints);
        }

        List<IItemHandlerModifiable> itemEndpoints = findEndpoints(serverLevel, connectedConduits, ModCapabilities.ITEM_HANDLER);
        if (itemEndpoints.size() >= 2) {
            distributeItems(itemEndpoints);
        }
    }

    private Set<BlockPos> findConnectedConduits(ServerLevel level) {
        Set<BlockPos> visited = new LinkedHashSet<>();
        Deque<BlockPos> frontier = new ArrayDeque<>();
        frontier.add(this.getBlockPos());
        visited.add(this.getBlockPos());
        while (!frontier.isEmpty() && visited.size() < MAX_SEARCH_DEPTH) {
            BlockPos current = frontier.poll();
            for (Direction direction : Direction.values()) {
                BlockPos neighborPos = current.relative(direction);
                if (visited.contains(neighborPos)) {
                    continue;
                }
                if (level.getBlockEntity(neighborPos) instanceof ConduitBlockEntity) {
                    visited.add(neighborPos);
                    frontier.add(neighborPos);
                }
            }
        }
        return visited;
    }

    private <T> List<T> findEndpoints(ServerLevel level, Set<BlockPos> connectedConduits, BlockCapability<T, Direction> capability) {
        Set<BlockPos> endpointPositions = new LinkedHashSet<>();
        List<T> endpoints = new ArrayList<>();
        for (BlockPos conduitPos : connectedConduits) {
            for (Direction direction : Direction.values()) {
                BlockPos neighborPos = conduitPos.relative(direction);
                if (connectedConduits.contains(neighborPos) || !endpointPositions.add(neighborPos)) {
                    continue;
                }
                T handler = capability.getCapability(level, neighborPos, null, null, direction.getOpposite());
                if (handler != null) {
                    endpoints.add(handler);
                }
            }
        }
        return endpoints;
    }

    private void distributeEnergy(List<EnergyHandler> endpoints) {
        int budget = MAX_ENERGY_TRANSFER_PER_TICK;
        for (EnergyHandler source : endpoints) {
            if (budget <= 0) {
                return;
            }
            List<EnergyHandler> sinks = endpoints.stream().filter(e -> e != source).toList();
            if (sinks.isEmpty()) {
                continue;
            }
            int perSink = Math.max(1, budget / sinks.size());
            for (EnergyHandler sink : sinks) {
                if (budget <= 0) {
                    break;
                }
                int toMove = Math.min(perSink, budget);
                // Insert first (so we only ever take exactly as much as the sink can actually
                // accept), then pull that same amount out of the source, same pattern as
                // ThermoelectricGeneratorBlockEntity#providePowerToNeighbors.
                try (Transaction transaction = Transaction.openRoot()) {
                    int inserted = sink.insert(toMove, transaction);
                    if (inserted <= 0) {
                        continue;
                    }
                    int extracted = source.extract(inserted, transaction);
                    if (extracted == inserted) {
                        transaction.commit();
                        budget -= extracted;
                    }
                }
            }
        }
    }

    private void distributeItems(List<IItemHandlerModifiable> endpoints) {
        int budget = MAX_ITEM_TRANSFER_PER_TICK;
        for (IItemHandlerModifiable source : endpoints) {
            if (budget <= 0) {
                return;
            }
            List<IItemHandlerModifiable> sinks = endpoints.stream().filter(e -> e != source).toList();
            if (sinks.isEmpty()) {
                continue;
            }
            for (IItemHandlerModifiable sink : sinks) {
                if (budget <= 0) {
                    break;
                }
                budget -= moveItems(source, sink, budget);
            }
        }
    }

    // Pulls the first non-empty slot's contents from source (up to maxAmount) and offers them to
    // sink across all its slots, the same simulate-then-commit two-step every legacy IItemHandler
    // mover uses (there's no Transaction type for this older API). Moves at most one slot's worth
    // per source-sink pair per tick -- another deliberately unsmart choice, matching
    // distributeEnergy's own "one slice per pair, not a full drain loop."
    private int moveItems(IItemHandlerModifiable source, IItemHandlerModifiable sink, int maxAmount) {
        for (int slot = 0; slot < source.getSlots(); slot++) {
            ItemStack available = source.extractItem(slot, maxAmount, true);
            if (available.isEmpty()) {
                continue;
            }
            ItemStack remainder = ItemHandlerHelper.insertItem(sink, available, true);
            int insertable = available.getCount() - remainder.getCount();
            if (insertable <= 0) {
                continue;
            }
            ItemStack extracted = source.extractItem(slot, insertable, false);
            if (extracted.isEmpty()) {
                continue;
            }
            ItemStack notInserted = ItemHandlerHelper.insertItem(sink, extracted, false);
            if (!notInserted.isEmpty()) {
                // Shouldn't happen -- the insert was already simulated for this exact count --
                // but if the sink's state somehow changed between simulate and commit, put back
                // whatever didn't fit rather than dropping it.
                source.insertItem(slot, notInserted, false);
            }
            return extracted.getCount() - notInserted.getCount();
        }
        return 0;
    }

}

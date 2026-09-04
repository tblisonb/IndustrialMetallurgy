package com.onlytanner.industrialmetallurgy.blocks;

import com.onlytanner.industrialmetallurgy.init.ModTileEntityTypes;
import com.onlytanner.industrialmetallurgy.tileentity.ConduitBlockEntity;
import com.onlytanner.industrialmetallurgy.tileentity.IOPortBlockEntity;
import com.onlytanner.industrialmetallurgy.util.RegistryHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.Map;

// A wrench (see WrenchItem) cycles Input -> Output -> Both -> Disabled; any other right-click
// (including an empty hand) just reports the current mode without changing it -- no GUI needed
// for a single four-state toggle. MODE also drives which of the four io_port_<mode> textures
// renders on the block's small core (see blockstates/io_port.json), so the state is visible at a
// glance instead of only through chat.
//
// Shares Conduit's connecting-pipe visual language (same NORTH/EAST/SOUTH/WEST/UP/DOWN booleans,
// same arm models) for whichever faces touch a Conduit or another port, so a port reads as a node
// in the pipe network rather than a full block dropped in the middle of it -- a face touching the
// actual host machine (or nothing at all) just shows the small mode-colored core instead, the
// same "flat, per-direction" look Extra Utilities' equivalent block uses. Purely cosmetic (and
// collision-shape) bookkeeping, same caveat as ConduitBlock: IOPortBlockEntity's real host/port
// scan is completely independent of these properties and can never be affected by a visual desync.
public class IOPortBlock extends Block implements EntityBlock {

    public static final EnumProperty<IOPortBlockEntity.Mode> MODE = EnumProperty.create("mode", IOPortBlockEntity.Mode.class);
    // The direction this port's mode-colored core faces, away from whatever it's mounted against
    // -- set once at placement from the clicked face, same idea as a Dropper/Observer's FACING.
    // Purely which way the always-shown "host" connector arm points (see the unconditional
    // facing=... entries in blockstates/io_port.json); IOPortBlockEntity's real host search is
    // still a full 6-direction scan, completely unaffected by this.
    public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;

    private static final Map<Direction, BooleanProperty> PIPE_PROPERTIES = new EnumMap<>(Direction.class);
    private static final Map<Direction, VoxelShape> ARM_SHAPES = new EnumMap<>(Direction.class);

    static {
        PIPE_PROPERTIES.put(Direction.NORTH, BlockStateProperties.NORTH);
        PIPE_PROPERTIES.put(Direction.EAST, BlockStateProperties.EAST);
        PIPE_PROPERTIES.put(Direction.SOUTH, BlockStateProperties.SOUTH);
        PIPE_PROPERTIES.put(Direction.WEST, BlockStateProperties.WEST);
        PIPE_PROPERTIES.put(Direction.UP, BlockStateProperties.UP);
        PIPE_PROPERTIES.put(Direction.DOWN, BlockStateProperties.DOWN);

        ARM_SHAPES.put(Direction.NORTH, Block.box(5, 5, 0, 11, 11, 5));
        ARM_SHAPES.put(Direction.SOUTH, Block.box(5, 5, 11, 11, 11, 16));
        ARM_SHAPES.put(Direction.EAST, Block.box(11, 5, 5, 16, 11, 11));
        ARM_SHAPES.put(Direction.WEST, Block.box(0, 5, 5, 5, 11, 11));
        ARM_SHAPES.put(Direction.UP, Block.box(5, 11, 5, 11, 16, 11));
        ARM_SHAPES.put(Direction.DOWN, Block.box(5, 0, 5, 11, 5, 11));
    }

    private static final VoxelShape CORE_SHAPE = Block.box(5, 5, 5, 11, 11, 11);
    private static final VoxelShape[] SHAPE_CACHE = new VoxelShape[64];

    public IOPortBlock(Properties properties) {
        super(properties);
        BlockState state = this.stateDefinition.any()
                .setValue(MODE, IOPortBlockEntity.Mode.INPUT)
                .setValue(FACING, Direction.NORTH);
        for (BooleanProperty property : PIPE_PROPERTIES.values()) {
            state = state.setValue(property, false);
        }
        this.registerDefaultState(state);
    }

    public static BlockBehaviour.Properties newProperties() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.METAL)
                .strength(2.5f, 4.5f)
                .sound(SoundType.METAL)
                .requiresCorrectToolForDrops();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(MODE);
        builder.add(FACING);
        for (BooleanProperty property : PIPE_PROPERTIES.values()) {
            builder.add(property);
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        // Faces outward, away from whatever surface was clicked to place it -- context.getClickedFace()
        // is exactly that outward direction (same convention a Dropper's FACING uses).
        Direction facing = context.getClickedFace();
        BlockState state = this.defaultBlockState().setValue(FACING, facing);
        Direction hostDirection = facing.getOpposite();
        for (Direction direction : Direction.values()) {
            // The host direction always shows the slab model (see blockstates/io_port.json's
            // facing=... entries), regardless of what's actually there -- force its own pipe-arm
            // boolean false so a Conduit placed in that exact spot (unusual, but possible) doesn't
            // also draw an overlapping arm on top of it.
            boolean pipe = direction != hostDirection && connectsToPipe(level, pos.relative(direction));
            state = state.setValue(PIPE_PROPERTIES.get(direction), pipe);
        }
        return state;
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos,
                                      Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        if (!(level instanceof Level realLevel) || direction == state.getValue(FACING).getOpposite()) {
            return state;
        }
        return state.setValue(PIPE_PROPERTIES.get(direction), connectsToPipe(realLevel, neighborPos));
    }

    // Only a Conduit or another I/O Port counts as a pipe-network neighbor -- a real host machine
    // (or empty air) leaves that face showing the plain mode-colored core instead of an arm, which
    // is what actually distinguishes "the pipe side" from "the machine side" here.
    private static boolean connectsToPipe(Level level, BlockPos neighborPos) {
        BlockEntity neighbor = level.getBlockEntity(neighborPos);
        return neighbor instanceof ConduitBlockEntity || neighbor instanceof IOPortBlockEntity;
    }

    // A port with nothing real to talk to is pointless clutter, and looked actively wrong reaching
    // an arm at, say, plain dirt -- require the face it's mounted against to be a genuine host
    // (something with an Energy/Item capability of its own) or another Conduit/Port to extend an
    // existing network, same set of neighbor types ConduitBlock's own connectsTo checks.
    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        if (!(level instanceof Level realLevel)) {
            return true;
        }
        Direction facing = state.getValue(FACING);
        BlockPos hostPos = pos.relative(facing.getOpposite());
        BlockEntity host = realLevel.getBlockEntity(hostPos);
        if (host instanceof ConduitBlockEntity || host instanceof IOPortBlockEntity) {
            return true;
        }
        // facing is the direction from the host back toward this port -- the face of the host a
        // capability query needs to ask about, same "side touching us" convention findHostEnergyHandler
        // uses via direction.getOpposite().
        return Capabilities.Energy.BLOCK.getCapability(realLevel, hostPos, null, null, facing) != null
                || Capabilities.Item.BLOCK.getCapability(realLevel, hostPos, null, null, facing) != null;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction[] directions = Direction.values();
        int mask = 0;
        for (int i = 0; i < directions.length; i++) {
            if (state.getValue(PIPE_PROPERTIES.get(directions[i]))) {
                mask |= 1 << i;
            }
        }
        // The host connector arm (see blockstates/io_port.json's unconditional facing=... entries)
        // always renders regardless of the pipe booleans, so its collision needs to be included too.
        mask |= 1 << state.getValue(FACING).getOpposite().ordinal();
        VoxelShape cached = SHAPE_CACHE[mask];
        if (cached != null) {
            return cached;
        }
        VoxelShape shape = CORE_SHAPE;
        for (int i = 0; i < directions.length; i++) {
            if ((mask & (1 << i)) != 0) {
                shape = Shapes.or(shape, ARM_SHAPES.get(directions[i]));
            }
        }
        SHAPE_CACHE[mask] = shape;
        return shape;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new IOPortBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return null;
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!stack.getItem().equals(RegistryHandler.WRENCH.get())) {
            return reportMode(level, pos, player);
        }
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        if (!(level.getBlockEntity(pos) instanceof IOPortBlockEntity port)) {
            return InteractionResult.FAIL;
        }
        port.cycleMode();
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.sendOverlayMessage(Component.translatable("message.industrialmetallurgy.io_port_mode_set", port.getMode().label()));
        }
        return InteractionResult.CONSUME;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        return reportMode(level, pos, player);
    }

    private static InteractionResult reportMode(Level level, BlockPos pos, Player player) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        if (!(level.getBlockEntity(pos) instanceof IOPortBlockEntity port)) {
            return InteractionResult.FAIL;
        }
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.sendOverlayMessage(Component.translatable("message.industrialmetallurgy.io_port_mode_current", port.getMode().label()));
        }
        return InteractionResult.CONSUME;
    }

}

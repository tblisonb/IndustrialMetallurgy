package com.onlytanner.industrialmetallurgy.blocks;

import com.onlytanner.industrialmetallurgy.init.ModTileEntityTypes;
import com.onlytanner.industrialmetallurgy.tileentity.ConduitBlockEntity;
import com.onlytanner.industrialmetallurgy.tileentity.IOPortBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
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
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.Map;

// Renders as a thin pipe with a per-direction arm toward each neighbor it visually connects to
// (see blockstates/conduit.json's multipart model), rather than a full cube. The six
// BlockStateProperties.NORTH/EAST/SOUTH/WEST/UP/DOWN booleans this tracks are purely cosmetic
// (and collision-shape) bookkeeping, kept in sync with the world via getStateForPlacement/
// updateShape -- the real connectivity ConduitBlockEntity#tick moves resources through is
// recomputed fresh every tick from scratch and never reads these at all, so the two can never
// desync in a way that breaks actual item/energy transfer, only the visual.
public class ConduitBlock extends Block implements EntityBlock {

    private static final Map<Direction, BooleanProperty> PROPERTIES = new EnumMap<>(Direction.class);
    private static final Map<Direction, VoxelShape> ARM_SHAPES = new EnumMap<>(Direction.class);

    static {
        PROPERTIES.put(Direction.NORTH, BlockStateProperties.NORTH);
        PROPERTIES.put(Direction.EAST, BlockStateProperties.EAST);
        PROPERTIES.put(Direction.SOUTH, BlockStateProperties.SOUTH);
        PROPERTIES.put(Direction.WEST, BlockStateProperties.WEST);
        PROPERTIES.put(Direction.UP, BlockStateProperties.UP);
        PROPERTIES.put(Direction.DOWN, BlockStateProperties.DOWN);

        ARM_SHAPES.put(Direction.NORTH, Block.box(5, 5, 0, 11, 11, 5));
        ARM_SHAPES.put(Direction.SOUTH, Block.box(5, 5, 11, 11, 11, 16));
        ARM_SHAPES.put(Direction.EAST, Block.box(11, 5, 5, 16, 11, 11));
        ARM_SHAPES.put(Direction.WEST, Block.box(0, 5, 5, 5, 11, 11));
        ARM_SHAPES.put(Direction.UP, Block.box(5, 11, 5, 11, 16, 11));
        ARM_SHAPES.put(Direction.DOWN, Block.box(5, 0, 5, 11, 5, 11));
    }

    private static final VoxelShape CORE_SHAPE = Block.box(5, 5, 5, 11, 11, 11);
    // Indexed by a 6-bit mask (bit i = Direction.values()[i] connected); filled in lazily the
    // first time each of the (at most) 64 combinations is actually seen.
    private static final VoxelShape[] SHAPE_CACHE = new VoxelShape[64];

    public ConduitBlock(Properties properties) {
        super(properties);
        BlockState state = this.stateDefinition.any();
        for (BooleanProperty property : PROPERTIES.values()) {
            state = state.setValue(property, false);
        }
        this.registerDefaultState(state);
    }

    public static BlockBehaviour.Properties newProperties() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.METAL)
                .strength(2.0f, 4.0f)
                .sound(SoundType.METAL)
                .requiresCorrectToolForDrops();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        for (BooleanProperty property : PROPERTIES.values()) {
            builder.add(property);
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = this.defaultBlockState();
        for (Direction direction : Direction.values()) {
            state = state.setValue(PROPERTIES.get(direction), connectsTo(level, pos.relative(direction), direction.getOpposite()));
        }
        return state;
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos,
                                      Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        // WorldGenLevel-style LevelReaders that aren't a real Level can't resolve capabilities;
        // harmless to skip since conduits are never worldgen-placed, only ever by a player.
        if (!(level instanceof Level realLevel)) {
            return state;
        }
        return state.setValue(PROPERTIES.get(direction), connectsTo(realLevel, neighborPos, direction.getOpposite()));
    }

    // A neighbor "connects" visually if it's another Conduit/I-O Port, or if it exposes an
    // Energy/Item capability on the face touching this conduit -- the same test
    // ConduitBlockEntity#findEndpoints uses to decide what it'll actually move resources through,
    // just run once per block-state change instead of every tick.
    private static boolean connectsTo(Level level, BlockPos neighborPos, Direction sideOnNeighbor) {
        BlockEntity neighbor = level.getBlockEntity(neighborPos);
        if (neighbor instanceof ConduitBlockEntity || neighbor instanceof IOPortBlockEntity) {
            return true;
        }
        return Capabilities.Energy.BLOCK.getCapability(level, neighborPos, null, null, sideOnNeighbor) != null
                || Capabilities.Item.BLOCK.getCapability(level, neighborPos, null, null, sideOnNeighbor) != null;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction[] directions = Direction.values();
        int mask = 0;
        for (int i = 0; i < directions.length; i++) {
            if (state.getValue(PROPERTIES.get(directions[i]))) {
                mask |= 1 << i;
            }
        }
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
        return new ConduitBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) {
            return null;
        }
        return type == ModTileEntityTypes.CONDUIT.get() ? (lvl, pos, st, be) -> ((ConduitBlockEntity) be).tick() : null;
    }

}

package com.onlytanner.industrialmetallurgy.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * Base block entity for a multiblock controller: a single block that owns the machine's logic
 * and validates a {@link MultiblockPattern} of supporting blocks around itself before it will
 * do anything. This is deliberately just the detection/state-tracking half of the pattern --
 * subclasses decide what "formed" actually unlocks (processing, GUI access, whatever).
 *
 * Call {@link #refreshStructure()} from the owning block's {@code neighborChanged} (and once on
 * placement) to keep the formed state in sync with the world; nothing here re-checks on a timer.
 * Formed state is persisted but, for now, not synced to the client -- it's a server-side gate,
 * not something rendered yet.
 */
public abstract class MultiblockControllerBlockEntity extends BlockEntity {

    private boolean formed = false;

    protected MultiblockControllerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    /** The structure this controller requires, authored against {@link Direction#NORTH}; see {@link MultiblockPatternBuilder}. */
    protected abstract MultiblockPattern getMultiblockPattern();

    /** The controller's own current horizontal facing, used to rotate {@link #getMultiblockPattern()} onto the world. */
    protected abstract Direction getFacing();

    /** Called once the structure transitions from unformed to formed. No-op by default. */
    protected void onStructureFormed() {
    }

    /** Called once the structure transitions from formed to unformed (a supporting block changed or was removed). No-op by default. */
    protected void onStructureBroken() {
    }

    public final boolean isFormed() {
        return this.formed;
    }

    /** Re-checks the pattern against the world and fires {@link #onStructureFormed()}/{@link #onStructureBroken()} on a change. */
    public final void refreshStructure() {
        if (this.level == null) {
            return;
        }
        boolean matches = getMultiblockPattern().matches(this.level, this.worldPosition, getFacing());
        if (matches == this.formed) {
            return;
        }
        this.formed = matches;
        if (matches) {
            onStructureFormed();
        } else {
            onStructureBroken();
        }
        setChanged();
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.formed = input.getBooleanOr("Formed", false);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putBoolean("Formed", this.formed);
    }
}

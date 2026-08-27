package org.minecart.more_formula.compat.jei.category.sequenced;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Resolved tier machine visuals shared by the tiered sequenced-assembly sub categories.
 */
public record TieredMachineContext(BlockState pressBody, BlockState sawBody, BlockState spoutBody,
                                   PartialModel[] spoutPartials, BlockState deployerBody,
                                   BlockState depotState, BlockState basinState) {
}

package org.minecart.more_formula.compat.jei.category.sequenced;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Resolved tier machine visuals shared by the tiered sequenced-assembly sub categories.
 *
 * <p>不含锯：CMM 没有分级锯机器，锯切工序直接委托 Create 原版动画。
 */
public record TieredMachineContext(BlockState pressBody, BlockState spoutBody,
                                   PartialModel[] spoutPartials, BlockState deployerBody,
                                   BlockState depotState, BlockState basinState) {
}

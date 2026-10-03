package org.minecart.more_formula.compat.mekanicalcreate.mixin;

import mekanism.api.inventory.IInventorySlot;
import mekanism.common.inventory.slot.BasicInventorySlot;
import org.minecart.more_formula.compat.mekanicalcreate.MekanicalCreateSpeedConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

@Mixin(targets = "io.github.langqi99.mekanicalcreate.content.SimulationChamberBlockEntity", remap = false)
public abstract class SimulationChamberBlockEntityMixin {
    @Shadow(remap = false)
    private BasicInventorySlot moduleSlot;

    @ModifyExpressionValue(
            method = "getParallelProcessCount()I",
            at = @At(value = "INVOKE",
                    target = "Lio/github/langqi99/mekanicalcreate/content/SimulationChamberBlockEntity;processCountFor(Lmekanism/common/tier/FactoryTier;)I"),
            remap = false)
    private int moreFormula$applyCmmParallelMultiplier(int baseCount) {
        return MekanicalCreateSpeedConfig.scaleParallelCount(baseCount,
                MekanicalCreateSpeedConfig.getMultiplier(moduleSlot.getStack()));
    }
}

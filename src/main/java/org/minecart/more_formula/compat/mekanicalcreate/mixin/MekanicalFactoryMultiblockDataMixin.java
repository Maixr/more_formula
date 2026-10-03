package org.minecart.more_formula.compat.mekanicalcreate.mixin;

import mekanism.api.inventory.IInventorySlot;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import org.minecart.more_formula.compat.mekanicalcreate.MekanicalCreateRecipeGate;
import org.minecart.more_formula.compat.mekanicalcreate.MekanicalCreateSpeedConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import java.util.ArrayList;
import java.util.List;

@Mixin(targets = "io.github.langqi99.mekanicalcreate.content.MekanicalFactoryMultiblockData", remap = false)
public abstract class MekanicalFactoryMultiblockDataMixin {
    @Shadow(remap = false)
    public abstract List<IInventorySlot> getCatalystSlotsForPort();

    @ModifyExpressionValue(
            method = "tickWorkBudgetPlan(Lnet/minecraft/world/level/Level;Z)Z",
            at = @At(value = "INVOKE",
                    target = "Lio/github/langqi99/mekanicalcreate/content/FactorySpeedSchedule;workPerTick(I)J"),
            remap = false)
    private long moreFormula$applyCmmWorkMultiplier(long baseWork) {
        List<Integer> multipliers = new ArrayList<>();
        for (IInventorySlot slot : getCatalystSlotsForPort()) {
            if (slot == null || slot.getStack().isEmpty()) {
                continue;
            }
            ResourceLocation moduleId = BuiltInRegistries.ITEM.getKey(slot.getStack().getItem());
            if (MekanicalCreateRecipeGate.getModuleKind(moduleId)
                    != MekanicalCreateRecipeGate.ModuleKind.NONE) {
                multipliers.add(MekanicalCreateSpeedConfig.getMultiplier(moduleId));
            }
        }
        return MekanicalCreateSpeedConfig.scaleWorkBudget(baseWork,
                MekanicalCreateSpeedConfig.getHighestMultiplier(multipliers));
    }
}

package org.minecart.more_formula.compat.handmade.mixin;

import org.minecart.more_formula.compat.handmade.HandMadeRecipeGate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Gates sequenced and normal filling recipes selected by the infusion gun. */
@Mixin(targets = "com.alben.createhandmade.item.InfusionGunItem", remap = false)
public abstract class InfusionGunItemMixin {
    @Inject(method = "findFillingRecipe", at = @At("RETURN"), cancellable = true, remap = false)
    private static void moreFormula$onlyTierZero(CallbackInfoReturnable<?> cir) {
        if (cir.getReturnValue() != null && !HandMadeRecipeGate.isTierZeroResult(cir.getReturnValue())) {
            cir.setReturnValue(null);
        }
    }
}

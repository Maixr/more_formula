package org.minecart.more_formula.compat.handmade.mixin;

import org.minecart.more_formula.compat.handmade.HandMadeRecipeGate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Gates the crusher/mortar's selected recipe. */
@Mixin(targets = "com.alben.createhandmade.item.CrusherMortarItem", remap = false)
public abstract class CrusherMortarItemMixin {
    @Inject(method = "findRecipe", at = @At("RETURN"), cancellable = true, remap = false)
    private static void moreFormula$onlyTierZero(CallbackInfoReturnable<?> cir) {
        if (cir.getReturnValue() != null && !HandMadeRecipeGate.isTierZeroResult(cir.getReturnValue())) {
            cir.setReturnValue(null);
        }
    }
}

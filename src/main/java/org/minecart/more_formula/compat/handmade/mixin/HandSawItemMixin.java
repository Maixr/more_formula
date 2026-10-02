package org.minecart.more_formula.compat.handmade.mixin;

import org.minecart.more_formula.compat.handmade.HandMadeRecipeGate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

/** Gates sequenced and normal cutting recipes selected by the hand saw. */
@Mixin(targets = "com.alben.createhandmade.item.HandSawItem", remap = false)
public abstract class HandSawItemMixin {
    @Inject(method = "getCuttingRecipes", at = @At("RETURN"), cancellable = true, remap = false)
    private static void moreFormula$onlyTierZero(CallbackInfoReturnable<List<?>> cir) {
        cir.setReturnValue(HandMadeRecipeGate.onlyTierZero(cir.getReturnValue()));
    }
}

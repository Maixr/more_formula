package org.minecart.more_formula.compat.handmade.mixin;

import net.minecraft.world.item.crafting.RecipeHolder;
import org.minecart.more_formula.compat.handmade.HandMadeRecipeGate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

/** Filters the shared Hand Made candidate pool before tools or JEI consume it. */
@Mixin(targets = "com.alben.createhandmade.recipe.HandMadeRecipePool", remap = false)
public abstract class HandMadeRecipePoolMixin {
    @Inject(method = "getBaseRecipes", at = @At("RETURN"), cancellable = true, remap = false)
    private static void moreFormula$onlyTierZero(CallbackInfoReturnable<List<RecipeHolder<?>>> cir) {
        cir.setReturnValue(HandMadeRecipeGate.onlyTierZero(cir.getReturnValue()));
    }
}

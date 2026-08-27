package org.minecart.more_formula.mixin;

import com.simibubi.create.content.kinetics.press.MechanicalPressBlockEntity;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.minecart.more_formula.util.TierHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(MechanicalPressBlockEntity.class)
public abstract class MechanicalPressBlockEntityMixin {
    @Inject(method = "getRecipe", at = @At("RETURN"), cancellable = true)
    private void moreFormula$gatePressing(ItemStack item, CallbackInfoReturnable<Optional<RecipeHolder<PressingRecipe>>> cir) {
        Optional<RecipeHolder<PressingRecipe>> result = cir.getReturnValue();
        if (result.isPresent() && !TierHelper.isAllowed((BlockEntity) (Object) this, result.get().id())) {
            cir.setReturnValue(Optional.empty());
        }
    }
}

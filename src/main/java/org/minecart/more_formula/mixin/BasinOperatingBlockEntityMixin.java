package org.minecart.more_formula.mixin;

import com.simibubi.create.content.processing.basin.BasinOperatingBlockEntity;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.minecart.more_formula.util.TierHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(BasinOperatingBlockEntity.class)
public abstract class BasinOperatingBlockEntityMixin {
    @Inject(method = "getMatchingRecipes", at = @At("RETURN"), cancellable = true)
    private void moreFormula$gateBasinRecipes(CallbackInfoReturnable<List<Recipe<?>>> cir) {
        List<Recipe<?>> recipes = cir.getReturnValue();
        if (recipes == null || recipes.isEmpty()) {
            return;
        }
        BlockEntity machine = (BlockEntity) (Object) this;
        List<Recipe<?>> gated = new ArrayList<>();
        for (Recipe<?> recipe : recipes) {
            var id = TierHelper.findRecipeId(machine.getLevel(), recipe);
            if (id == null || TierHelper.isAllowed(machine, id)) {
                gated.add(recipe);
            }
        }
        cir.setReturnValue(gated);
    }
}

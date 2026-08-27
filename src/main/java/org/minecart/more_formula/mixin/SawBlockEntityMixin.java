package org.minecart.more_formula.mixin;

import com.simibubi.create.content.kinetics.saw.SawBlockEntity;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.minecart.more_formula.util.TierHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(SawBlockEntity.class)
public abstract class SawBlockEntityMixin {
    @Inject(method = "getRecipes", at = @At("RETURN"), cancellable = true)
    private void moreFormula$gateCutting(CallbackInfoReturnable<List<RecipeHolder<? extends Recipe<?>>>> cir) {
        var original = cir.getReturnValue();
        if (original == null || original.isEmpty()) {
            return;
        }
        BlockEntity machine = (BlockEntity) (Object) this;
        List<RecipeHolder<? extends Recipe<?>>> gated = new ArrayList<>();
        for (RecipeHolder<? extends Recipe<?>> holder : original) {
            if (TierHelper.isAllowed(machine, holder.id())) {
                gated.add(holder);
            }
        }
        cir.setReturnValue(gated);
    }
}

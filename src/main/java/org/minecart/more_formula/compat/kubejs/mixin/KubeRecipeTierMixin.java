package org.minecart.more_formula.compat.kubejs.mixin;

import org.minecart.more_formula.Config;
import dev.latvian.mods.kubejs.recipe.KubeRecipe;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = KubeRecipe.class, remap = false)
public abstract class KubeRecipeTierMixin {

    @Shadow
    public ResourceLocation id;

    @Unique
    private int more_formula$tier;

    @Unique
    public KubeRecipe tier(int tier) {
        this.more_formula$tier = tier;
        if (tier != 0 && this.id != null) {
            Config.addTier(this.id, tier, Config.Source.SERVER);
        }
        return (KubeRecipe) (Object) this;
    }

    @Unique
    public int more_formula$getTier() {
        return this.more_formula$tier;
    }

    @Inject(method = "save()V", at = @At("TAIL"))
    private void more_formula$registerTierOnSave(CallbackInfo ci) {
        if (this.more_formula$tier != 0 && this.id != null) {
            Config.addTier(this.id, this.more_formula$tier, Config.Source.SERVER);
        }
    }

    @Inject(method = "getOrCreateId()Lnet/minecraft/resources/ResourceLocation;", at = @At("TAIL"))
    private void more_formula$registerTierOnIdAssigned(CallbackInfoReturnable<net.minecraft.resources.ResourceLocation> cir) {
        if (this.more_formula$tier != 0) {
            net.minecraft.resources.ResourceLocation finalId = cir.getReturnValue();
            if (finalId != null) {
                Config.addTier(finalId, this.more_formula$tier, Config.Source.SERVER);
            }
        }
    }
}

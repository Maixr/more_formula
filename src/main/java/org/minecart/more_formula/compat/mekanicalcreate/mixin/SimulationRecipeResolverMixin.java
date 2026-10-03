package org.minecart.more_formula.compat.mekanicalcreate.mixin;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.minecart.more_formula.compat.mekanicalcreate.MekanicalCreateRecipeGate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(targets = "io.github.langqi99.mekanicalcreate.content.SimulationRecipeResolver", remap = false)
public abstract class SimulationRecipeResolverMixin {
    @Inject(
            method = "isSupportedModule(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Z)Z",
            at = @At("RETURN"), cancellable = true, remap = false)
    private static void moreFormula$allowTieredModules(
            Level level, ItemStack module, boolean allowFluidProcessing,
            CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue() && MekanicalCreateRecipeGate.isSupportedModule(
                module, allowFluidProcessing)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(
            method = "buildCandidates(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;Z)Ljava/util/List;",
            at = @At("RETURN"), cancellable = true, remap = false)
    private static void moreFormula$filterTieredCandidates(
            Level level, ItemStack module, ItemStack condition,
            boolean allowFluidProcessing, CallbackInfoReturnable<List<?>> cir) {
        if (!module.isEmpty()) {
            cir.setReturnValue(MekanicalCreateRecipeGate.filterCandidates(module, cir.getReturnValue()));
        }
    }

    @Redirect(
            method = "buildCandidates(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;Z)Ljava/util/List;",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;is(Lnet/minecraft/world/item/Item;)Z"),
            require = 1, remap = false)
    private static boolean moreFormula$matchCmmModuleType(
            ItemStack module, Item createModule) {
        if (module.is(createModule)) {
            return true;
        }
        return MekanicalCreateRecipeGate.matchesCreateModule(module, createModule, true);
    }

    @Redirect(
            method = "addSequenced(Ljava/util/List;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/crafting/RecipeManager;Lnet/minecraft/world/item/ItemStack;Z)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;isSameItemSameComponents(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"),
            require = 1, remap = false)
    private static boolean moreFormula$matchCmmSequencedModule(
            ItemStack selectedModule, ItemStack stepModule) {
        return ItemStack.isSameItemSameComponents(selectedModule, stepModule)
                || MekanicalCreateRecipeGate.matchesSequenceModule(selectedModule, stepModule);
    }
}

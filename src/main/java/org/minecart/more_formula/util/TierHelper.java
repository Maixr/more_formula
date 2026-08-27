package org.minecart.more_formula.util;

import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.fluids.transfer.FillingRecipe;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.fluids.FluidStack;
import net.yxiao233.createmoremachines.api.content.mechanical.deployer.CMMDeployerBlockEntity;
import net.yxiao233.createmoremachines.api.content.mechanical.mixer.CMMMechanicalMixerBlockEntity;
import net.yxiao233.createmoremachines.api.content.mechanical.press.CMMMechanicalPressBlockEntity;
import net.yxiao233.createmoremachines.api.content.saw.CMMSawBlockEntity;
import net.yxiao233.createmoremachines.api.content.spout.CMMSpoutBlockEntity;
import org.jetbrains.annotations.Nullable;
import org.minecart.more_formula.Config;

public class TierHelper {
    public static int getMachineTier(@Nullable BlockEntity machine) {
        if (machine == null) {
            return 0;
        }
        int cmmTier;
        if (machine instanceof CMMMechanicalPressBlockEntity press) {
            cmmTier = press.getTier().getTierValue();
        } else if (machine instanceof CMMMechanicalMixerBlockEntity mixer) {
            cmmTier = mixer.getTier().getTierValue();
        } else if (machine instanceof CMMDeployerBlockEntity deployer) {
            cmmTier = deployer.getTier().getTierValue();
        } else if (machine instanceof CMMSpoutBlockEntity spout) {
            cmmTier = spout.getTier().getTierValue();
        } else if (machine instanceof CMMSawBlockEntity saw) {
            cmmTier = saw.getTier().getTierValue();
        } else {
            return 0;
        }
        return toFormulaTier(cmmTier);
    }

    private static int toFormulaTier(int cmmTier) {
        if (cmmTier <= 1) {
            return cmmTier;
        }
        return cmmTier - 1;
    }

    public static boolean isAllowed(@Nullable BlockEntity machine, ResourceLocation recipeId) {
        int required = Config.getRequiredTier(recipeId);
        int tier = getMachineTier(machine);
        return isTierAllowed(tier, required);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Nullable
    public static ResourceLocation findRecipeId(Level level, Recipe<?> recipe) {
        RecipeType type = recipe.getType();
        for (Object entry : level.getRecipeManager().getAllRecipesFor(type)) {
            RecipeHolder<?> holder = (RecipeHolder<?>) entry;
            if (holder.value() == recipe) {
                return holder.id();
            }
        }
        return null;
    }

    public static boolean isFillingAllowed(BlockEntity machine, Level level, ItemStack stack, @Nullable FluidStack availableFluid) {
        int required = getRequiredFillingTier(level, stack, availableFluid);
        int tier = getMachineTier(machine);
        return isTierAllowed(tier, required);
    }

    private static boolean isTierAllowed(int machineTier, int requiredTier) {
        if (requiredTier == -1) {
            return machineTier == -1;
        }
        if (requiredTier <= 0) {
            return true;
        }
        return machineTier == -1 || machineTier >= requiredTier;
    }

    private static int getRequiredFillingTier(Level level, ItemStack stack, @Nullable FluidStack availableFluid) {
        SingleRecipeInput input = new SingleRecipeInput(stack);
        for (RecipeHolder<FillingRecipe> holder : SequencedAssemblyRecipe.getRecipes(
                level, stack, AllRecipeTypes.FILLING.getType(), FillingRecipe.class, r -> true)) {
            FillingRecipe recipe = holder.value();
            if (availableFluid != null && !recipe.getRequiredFluid().test(availableFluid)) {
                continue;
            }
            return Config.getRequiredTier(holder.id());
        }

        for (RecipeHolder<Recipe<SingleRecipeInput>> holder : level.getRecipeManager()
                .getRecipesFor(AllRecipeTypes.FILLING.getType(), input, level)) {
            FillingRecipe recipe = (FillingRecipe) holder.value();
            if (availableFluid != null && !recipe.getRequiredFluid().test(availableFluid)) {
                continue;
            }
            return Config.getRequiredTier(holder.id());
        }

        return 0;
    }
}

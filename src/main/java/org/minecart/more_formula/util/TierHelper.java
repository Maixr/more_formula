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
import net.yxiao233.createmoremachines.api.content.spout.CMMSpoutBlockEntity;
import org.jetbrains.annotations.Nullable;
import org.minecart.more_formula.Config;

public class TierHelper {

    /**
     * CMM 的等级值与 more_formula 的常量不同号：
     * brass=2 / netherite=3 / end=4 / beyond=5 / creative=-1，
     * 而本模组用 1..4 表示四级、-1 表示创造级。这里做一次平移，
     * {@code <= 1} 的（含 creative 的 -1）原样返回。
     */
    private static final int CMM_TIER_OFFSET = 1;

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
        } else {
            // 原版 Create 机器（或任何非 CMM 方块实体）视为 0 级。
            // 注意这里**不**处理 CMM 的锯：CMM 2.7 没有发布任何锯方块
            // （只有未启用的 API 类），因此不存在需要识别的锯机器。
            return 0;
        }
        return toFormulaTier(cmmTier);
    }

    private static int toFormulaTier(int cmmTier) {
        if (cmmTier <= 1) {
            return cmmTier;
        }
        return cmmTier - CMM_TIER_OFFSET;
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

    /**
     * 取出「这台喷口可能执行的浇注配方」所要求的门槛。
     *
     * <p>为什么取**最大**门槛而不是「第一个匹配到的配方的门槛」：
     * Create 自己选配方用的是 {@code RecipeManager.getRecipesFor(...)} 的返回顺序，
     * 取第一个流体匹配的配方（见 {@code FillingBySpout#fillItem}）。
     * 这个顺序不受我们控制，同一物品 + 同一流体命中多条配方时，
     * 「取第一条」很可能和 Create 实际执行的那条不是同一条 —— 旧实现就有这个漏洞：
     * 它先查序列装配展开出的配方、再查普通浇注配方，任取其一返回，
     * 一旦被取到的那条没设门槛（返回 0），整台机器就会直接放行，
     * 而 Create 真正执行的却可能是设了门槛的那条。
     *
     * <p>因此这里对**所有候选**取最大门槛：只要候选里存在设了门槛的配方，
     * 就按最严的那条来判定。代价是「同一物品+流体同时存在有门槛和无门槛配方」时
     * 会偏严；但本模组的职责就是拦截，宁可拦得严，也不能漏放。
     */
    private static int getRequiredFillingTier(Level level, ItemStack stack, @Nullable FluidStack availableFluid) {
        SingleRecipeInput input = new SingleRecipeInput(stack);
        int required = 0;

        // 序列装配展开出的浇注配方（Create 的 canItemBeFilled 优先查这一组）
        for (RecipeHolder<FillingRecipe> holder : SequencedAssemblyRecipe.getRecipes(
                level, stack, AllRecipeTypes.FILLING.getType(), FillingRecipe.class, r -> true)) {
            FillingRecipe recipe = holder.value();
            if (availableFluid != null && !recipe.getRequiredFluid().test(availableFluid)) {
                continue;
            }
            required = Math.max(required, Config.getRequiredTier(holder.id()));
        }

        // 普通浇注配方
        for (RecipeHolder<Recipe<SingleRecipeInput>> holder : level.getRecipeManager()
                .getRecipesFor(AllRecipeTypes.FILLING.getType(), input, level)) {
            if (!(holder.value() instanceof FillingRecipe recipe)) {
                continue;
            }
            if (availableFluid != null && !recipe.getRequiredFluid().test(availableFluid)) {
                continue;
            }
            required = Math.max(required, Config.getRequiredTier(holder.id()));
        }

        return required;
    }
}

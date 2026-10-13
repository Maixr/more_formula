package org.minecart.more_formula.compat.createmoremoremachines.mixin;

import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;
import net.tomorrow325.createmoremoremachines.api.content.crushing_wheel.CMMCrushingWheelControllerBlockEntity;
import org.minecart.more_formula.util.TierHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;

/**
 * 按配方门槛拦截 CMMM 的分级破碎轮控制器（该控制器同时承载 crushing 与
 * milling 回退查找，一处拦截两条通道都覆盖）。
 *
 * <p><b>同时负责「同输入多配方择优」</b>：CMMM 自己的 {@code findRecipe} 用的是
 * Create 的 {@code AllRecipeTypes.find}（= {@code byType().filter(matches).findFirst()}），
 * 只会拿到「注册顺序里第一条」配方。同一输入写了 t1/t2 两条 crushing 时，先注册的 t1
 * 永远先命中 —— 用户可见症状正是「给下界粉碎轮喂料，出来的却是黄铜粉碎轮的产物」。
 * 这里改成取该输入下<b>所有</b>匹配配方，滤掉等级不够的，再取门槛最高的一条。
 *
 * <p>CMMM 覆写版在找到配方后还会按等级放大产量（内部重新包一层 RecipeHolder），
 * 但那一步发生在<b>它自己的</b> {@code findRecipe} 返回之前；我们从 RETURN 处整体
 * 替换返回值，CMMM 的产量放大逻辑因此会被跳过。这是刻意的取舍：等级门槛是
 * more_formula 的语义，产量倍率属于 CMMM —— 现阶段优先保证「选的配方对」。
 *
 * <p>语义与原版破碎轮一致：{@code findRecipe} 返回空时，Create 的私有
 * {@code applyRecipe} 走 else 分支清空库存 —— 也就是说，等级不够的机器把带门槛的
 * 配方视为「无配方」，物品在倒计时结束后被研磨掉。这是破碎轮表达「处理不了」的
 * 原生方式（原版对无配方物品同样如此），文档中已明确标注。
 */
@Mixin(CMMCrushingWheelControllerBlockEntity.class)
public abstract class CMMMCrushingWheelControllerBlockEntityMixin {

    @Shadow
    private RecipeWrapper cmmWrapper;

    @Inject(method = "findRecipe", at = @At("RETURN"), cancellable = true)
    private void moreFormula$gateCrushing(
            CallbackInfoReturnable<Optional<RecipeHolder<StandardProcessingRecipe<RecipeWrapper>>>> cir) {
        BlockEntity machine = (BlockEntity) (Object) this;
        Level level = machine.getLevel();
        if (level == null) {
            return;
        }
        List<RecipeHolder<StandardProcessingRecipe<RecipeWrapper>>> candidates =
                TierHelper.crushingCandidates(level, cmmWrapper);
        if (candidates.isEmpty()) {
            return;
        }
        cir.setReturnValue(TierHelper.pickBestAllowed(machine, candidates));
    }
}

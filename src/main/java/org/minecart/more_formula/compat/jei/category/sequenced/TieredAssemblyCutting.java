package org.minecart.more_formula.compat.jei.category.sequenced;

import com.simibubi.create.compat.jei.category.sequencedAssembly.SequencedAssemblySubCategory;
import com.simibubi.create.content.processing.sequenced.SequencedRecipe;
import net.minecraft.client.gui.GuiGraphics;

/**
 * 分级序列装配里的「锯切」工序。
 *
 * <p>CMM 2.7 并没有发布任何分级锯机器 —— {@code CMMTierPlugin} 对
 * {@code BuiltInAdvancedMachineTypes.SAW} 直接调用了 {@code withoutAll()}，
 * 一个分级锯方块都不注册（jar 内也确实没有任何 saw 的 blockstate/模型/贴图）。
 * 因此这里不再假装有「分级锯」，直接委托 Create 原版的锯动画 ——
 * 原实现同样是渲染原版锯（{@code entry(SAW, tier)} 恒为 null，回退到
 * {@code AllBlocks.MECHANICAL_SAW}），只是绕了一层没有意义的「分级」外壳。
 *
 * <p>保留这个类是为了让分级序列装配在遇到锯切工序时仍走统一的分级子分类体系，
 * 将来 CMM 真加了分级锯，只需把委托换成对应的分级渲染。
 */
public class TieredAssemblyCutting extends TieredSequencedAssemblySubCategory {

    private final SequencedAssemblySubCategory vanilla = new SequencedAssemblySubCategory.AssemblyCutting();

    public TieredAssemblyCutting() {
        super(25);
    }

    @Override
    public void draw(SequencedRecipe<?> recipe, GuiGraphics graphics, double mouseX, double mouseY, int index) {
        this.vanilla.draw(recipe, graphics, mouseX, mouseY, index);
    }
}

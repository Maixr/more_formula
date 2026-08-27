package org.minecart.more_formula.compat.jei.category.sequenced;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.content.processing.sequenced.SequencedRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import net.minecraft.client.gui.GuiGraphics;
import org.minecart.more_formula.compat.jei.animation.TieredAnimatedSpout;

import java.util.Arrays;

public class TieredAssemblySpouting extends TieredSequencedAssemblySubCategory {
    private final TieredAnimatedSpout spout;

    public TieredAssemblySpouting(TieredMachineContext ctx) {
        super(25);
        this.spout = new TieredAnimatedSpout(ctx.spoutBody(), ctx.spoutPartials()[0], ctx.spoutPartials()[1], ctx.spoutPartials()[2], ctx.depotState());
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, SequencedRecipe<?> recipe, IFocusGroup focuses, int x) {
        CreateRecipeCategory.addFluidSlot(builder, x + 4, 15, recipe.getRecipe().getFluidIngredients().get(0));
    }

    @Override
    public void draw(SequencedRecipe<?> recipe, GuiGraphics graphics, double mouseX, double mouseY, int index) {
        PoseStack ms = graphics.pose();
        this.spout.offset = index;
        ms.pushPose();
        ms.translate(-7.0F, 50.0F, 0.0F);
        ms.scale(0.75F, 0.75F, 0.75F);
        this.spout
                .withFluids(Arrays.asList(recipe.getRecipe().getFluidIngredients().get(0).getFluids()))
                .draw(graphics, this.getWidth() / 2, 0);
        ms.popPose();
    }
}

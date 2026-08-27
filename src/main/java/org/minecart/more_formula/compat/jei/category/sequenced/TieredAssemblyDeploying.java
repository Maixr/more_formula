package org.minecart.more_formula.compat.jei.category.sequenced;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.content.kinetics.deployer.DeployerApplicationRecipe;
import com.simibubi.create.content.processing.sequenced.SequencedRecipe;
import com.simibubi.create.foundation.utility.CreateLang;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import org.minecart.more_formula.compat.jei.animation.TieredAnimatedDeployer;

public class TieredAssemblyDeploying extends TieredSequencedAssemblySubCategory {
    private final TieredAnimatedDeployer deployer;

    public TieredAssemblyDeploying(TieredMachineContext ctx) {
        super(25);
        this.deployer = new TieredAnimatedDeployer(ctx.deployerBody(), ctx.depotState());
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, SequencedRecipe<?> recipe, IFocusGroup focuses, int x) {
        IRecipeSlotBuilder slot = builder.addSlot(mezz.jei.api.recipe.RecipeIngredientRole.INPUT, x + 4, 15)
                .setBackground(CreateRecipeCategory.getRenderedSlot(), -1, -1)
                .addIngredients(recipe.getRecipe().getIngredients().get(1));
        if (recipe.getAsAssemblyRecipe() instanceof DeployerApplicationRecipe deployerRecipe && deployerRecipe.shouldKeepHeldItem()) {
            slot.addTooltipCallback(
                    (recipeSlotView, tooltip) -> tooltip.add(
                            1, CreateLang.translateDirect("recipe.deploying.not_consumed").withStyle(ChatFormatting.GOLD)
                    )
            );
        }
    }

    @Override
    public void draw(SequencedRecipe<?> recipe, GuiGraphics graphics, double mouseX, double mouseY, int index) {
        PoseStack ms = graphics.pose();
        this.deployer.offset = index;
        ms.pushPose();
        ms.translate(-7.0F, 50.0F, 0.0F);
        ms.scale(0.75F, 0.75F, 0.75F);
        this.deployer.draw(graphics, this.getWidth() / 2, 0);
        ms.popPose();
    }
}

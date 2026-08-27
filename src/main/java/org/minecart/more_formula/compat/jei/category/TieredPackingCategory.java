package org.minecart.more_formula.compat.jei.category;

import com.simibubi.create.compat.jei.category.BasinCategory;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory.Info;
import com.simibubi.create.compat.jei.category.animations.AnimatedBlazeBurner;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.level.block.state.BlockState;
import org.minecart.more_formula.compat.jei.animation.TieredAnimatedPress;

public class TieredPackingCategory extends BasinCategory {
    private final AnimatedBlazeBurner heater = new AnimatedBlazeBurner();
    private final TieredAnimatedPress press;

    public TieredPackingCategory(Info<BasinRecipe> info, BlockState body, BlockState basinState) {
        super(info, true);
        this.press = new TieredAnimatedPress(body, true, basinState);
    }

    @Override
    public void draw(BasinRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        super.draw(recipe, recipeSlotsView, graphics, mouseX, mouseY);
        HeatCondition requiredHeat = recipe.getRequiredHeat();
        if (requiredHeat != HeatCondition.NONE) {
            this.heater.withHeat(requiredHeat.visualizeAsBlazeBurner()).draw(graphics, this.getBackground().getWidth() / 2 + 3, 55);
        }
        this.press.draw(graphics, this.getBackground().getWidth() / 2 + 3, 34);
    }
}

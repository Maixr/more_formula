package org.minecart.more_formula.compat.jei.category;

import com.simibubi.create.compat.jei.category.PressingCategory;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory.Info;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.level.block.state.BlockState;
import org.minecart.more_formula.compat.jei.animation.TieredAnimatedPress;

public class TieredPressingCategory extends PressingCategory {
    private final TieredAnimatedPress press;

    public TieredPressingCategory(Info<PressingRecipe> info, BlockState body, BlockState basinState) {
        super(info);
        this.press = new TieredAnimatedPress(body, false, basinState);
    }

    @Override
    public void draw(PressingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        AllGuiTextures.JEI_SHADOW.render(graphics, 61, 41);
        AllGuiTextures.JEI_LONG_ARROW.render(graphics, 52, 54);
        this.press.draw(graphics, this.getBackground().getWidth() / 2 - 17, 22);
    }
}

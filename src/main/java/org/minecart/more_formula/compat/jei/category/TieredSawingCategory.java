package org.minecart.more_formula.compat.jei.category;

import com.simibubi.create.compat.jei.category.SawingCategory;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory.Info;
import com.simibubi.create.content.kinetics.saw.CuttingRecipe;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.level.block.state.BlockState;
import org.minecart.more_formula.compat.jei.animation.TieredAnimatedSaw;

public class TieredSawingCategory extends SawingCategory {
    private final TieredAnimatedSaw saw;

    public TieredSawingCategory(Info<CuttingRecipe> info, BlockState body) {
        super(info);
        this.saw = new TieredAnimatedSaw(body);
    }

    @Override
    public void draw(CuttingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        AllGuiTextures.JEI_DOWN_ARROW.render(graphics, 70, 6);
        AllGuiTextures.JEI_SHADOW.render(graphics, 55, 55);
        this.saw.draw(graphics, 72, 42);
    }
}

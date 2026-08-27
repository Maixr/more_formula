package org.minecart.more_formula.compat.jei.category.sequenced;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import net.minecraft.client.gui.GuiGraphics;
import com.simibubi.create.content.processing.sequenced.SequencedRecipe;

public abstract class TieredSequencedAssemblySubCategory {
    private final int width;

    protected TieredSequencedAssemblySubCategory(int width) {
        this.width = width;
    }

    public int getWidth() {
        return this.width;
    }

    public void setRecipe(IRecipeLayoutBuilder builder, SequencedRecipe<?> recipe, IFocusGroup focuses, int x) {
    }

    public abstract void draw(SequencedRecipe<?> recipe, GuiGraphics graphics, double mouseX, double mouseY, int index);
}

package org.minecart.more_formula.compat.jei.category;

import com.simibubi.create.compat.jei.category.BasinCategory;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory.Info;
import com.simibubi.create.compat.jei.category.animations.AnimatedBlazeBurner;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.level.block.state.BlockState;
import org.minecart.more_formula.compat.jei.animation.TieredAnimatedMixer;

public class TieredMixingCategory extends BasinCategory {
    private final AnimatedBlazeBurner heater = new AnimatedBlazeBurner();
    private final TieredAnimatedMixer mixer;

    public TieredMixingCategory(Info<BasinRecipe> info, BlockState body, BlockState basinState, PartialModel headPartial) {
        super(info, true);
        this.mixer = new TieredAnimatedMixer(body, basinState, headPartial);
    }

    @Override
    public void draw(BasinRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        super.draw(recipe, recipeSlotsView, graphics, mouseX, mouseY);
        HeatCondition requiredHeat = recipe.getRequiredHeat();
        if (requiredHeat != HeatCondition.NONE) {
            this.heater.withHeat(requiredHeat.visualizeAsBlazeBurner()).draw(graphics, this.getBackground().getWidth() / 2 + 3, 55);
        }
        this.mixer.draw(graphics, this.getBackground().getWidth() / 2 + 3, 34);
    }
}

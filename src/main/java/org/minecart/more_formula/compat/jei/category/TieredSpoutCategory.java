package org.minecart.more_formula.compat.jei.category;

import com.simibubi.create.compat.jei.category.SpoutCategory;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory.Info;
import com.simibubi.create.content.fluids.transfer.FillingRecipe;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import org.minecart.more_formula.compat.jei.animation.TieredAnimatedSpout;

import java.util.Arrays;

public class TieredSpoutCategory extends SpoutCategory {
    private final TieredAnimatedSpout spout;

    public TieredSpoutCategory(Info<FillingRecipe> info, BlockState body, PartialModel[] spoutPartials) {
        this(info, body, spoutPartials,
                com.simibubi.create.AllBlocks.DEPOT.getDefaultState());
    }

    public TieredSpoutCategory(Info<FillingRecipe> info, BlockState body, PartialModel[] spoutPartials, BlockState depotState) {
        super(info);
        this.spout = new TieredAnimatedSpout(body, spoutPartials[0], spoutPartials[1], spoutPartials[2], depotState);
    }

    @Override
    public void draw(FillingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        AllGuiTextures.JEI_SHADOW.render(graphics, 62, 57);
        AllGuiTextures.JEI_DOWN_ARROW.render(graphics, 126, 29);
        this.spout.withFluids(Arrays.asList(recipe.getRequiredFluid().getFluids())).draw(graphics, this.getBackground().getWidth() / 2 - 13, 22);
    }
}

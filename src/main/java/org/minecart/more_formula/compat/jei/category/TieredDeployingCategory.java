package org.minecart.more_formula.compat.jei.category;

import com.simibubi.create.compat.jei.category.DeployingCategory;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory.Info;
import com.simibubi.create.content.kinetics.deployer.DeployerApplicationRecipe;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.level.block.state.BlockState;
import org.minecart.more_formula.compat.jei.animation.TieredAnimatedDeployer;

public class TieredDeployingCategory extends DeployingCategory {
    private final TieredAnimatedDeployer deployer;

    public TieredDeployingCategory(Info<DeployerApplicationRecipe> info, BlockState body, BlockState depotState) {
        super(info);
        this.deployer = new TieredAnimatedDeployer(body, depotState);
    }

    @Override
    public void draw(DeployerApplicationRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        AllGuiTextures.JEI_SHADOW.render(graphics, 62, 57);
        AllGuiTextures.JEI_DOWN_ARROW.render(graphics, 126, 29 + (recipe.getRollableResults().size() > 2 ? -19 : 0));
        this.deployer.draw(graphics, this.getBackground().getWidth() / 2 - 13, 22);
    }
}

package org.minecart.more_formula.compat.jei.category.sequenced;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.processing.sequenced.SequencedRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.level.block.state.BlockState;
import org.minecart.more_formula.compat.jei.animation.TieredAnimatedPress;

public class TieredAssemblyPressing extends TieredSequencedAssemblySubCategory {
    private final TieredAnimatedPress press;

    public TieredAssemblyPressing(TieredMachineContext ctx) {
        super(25);
        this.press = new TieredAnimatedPress(ctx.pressBody(), false, ctx.basinState());
    }

    @Override
    public void draw(SequencedRecipe<?> recipe, GuiGraphics graphics, double mouseX, double mouseY, int index) {
        PoseStack ms = graphics.pose();
        this.press.offset = index;
        ms.pushPose();
        ms.translate(-5.0F, 50.0F, 0.0F);
        ms.scale(0.6F, 0.6F, 0.6F);
        this.press.draw(graphics, this.getWidth() / 2, 0);
        ms.popPose();
    }
}

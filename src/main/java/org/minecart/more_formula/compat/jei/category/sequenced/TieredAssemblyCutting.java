package org.minecart.more_formula.compat.jei.category.sequenced;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.processing.sequenced.SequencedRecipe;
import net.minecraft.client.gui.GuiGraphics;
import org.minecart.more_formula.compat.jei.animation.TieredAnimatedSaw;

public class TieredAssemblyCutting extends TieredSequencedAssemblySubCategory {
    private final TieredAnimatedSaw saw;

    public TieredAssemblyCutting(TieredMachineContext ctx) {
        super(25);
        this.saw = new TieredAnimatedSaw(ctx.sawBody());
    }

    @Override
    public void draw(SequencedRecipe<?> recipe, GuiGraphics graphics, double mouseX, double mouseY, int index) {
        PoseStack ms = graphics.pose();
        ms.pushPose();
        ms.translate(0.0F, 51.5F, 0.0F);
        ms.scale(0.6F, 0.6F, 0.6F);
        this.saw.draw(graphics, this.getWidth() / 2, 30);
        ms.popPose();
    }
}

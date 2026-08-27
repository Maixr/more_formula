package org.minecart.more_formula.compat.jei.animation;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import com.simibubi.create.content.kinetics.saw.SawBlock;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Copy of Create's {@link com.simibubi.create.compat.jei.category.animations.AnimatedSaw}
 * that renders a tier-specific saw body instead of the vanilla one.
 */
public class TieredAnimatedSaw extends AnimatedKinetics {
    private final BlockState body;

    public TieredAnimatedSaw(BlockState body) {
        this.body = body.hasProperty(SawBlock.FACING) ? body.setValue(SawBlock.FACING, Direction.UP) : body;
    }

    @Override
    public void draw(GuiGraphics graphics, int xOffset, int yOffset) {
        PoseStack matrixStack = graphics.pose();
        matrixStack.pushPose();
        matrixStack.translate((float) xOffset, (float) yOffset, 0.0F);
        matrixStack.translate(0.0F, 0.0F, 200.0F);
        matrixStack.translate(2.0F, 22.0F, 0.0F);
        matrixStack.mulPose(Axis.XP.rotationDegrees(-15.5F));
        matrixStack.mulPose(Axis.YP.rotationDegrees(112.5F));
        int scale = 25;
        this.blockElement(this.shaft(Direction.Axis.X))
                .rotateBlock((double) (-getCurrentAngle()), 0.0, 0.0)
                .scale((double) scale)
                .render(graphics);
        this.blockElement(this.body)
                .scale((double) scale)
                .render(graphics);
        this.blockElement(AllPartialModels.SAW_BLADE_VERTICAL_ACTIVE).rotateBlock(0.0, -90.0, -90.0).scale((double) scale).render(graphics);
        matrixStack.popPose();
    }
}

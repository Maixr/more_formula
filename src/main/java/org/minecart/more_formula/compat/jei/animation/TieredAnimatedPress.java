package org.minecart.more_formula.compat.jei.animation;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Copy of Create's {@link com.simibubi.create.compat.jei.category.animations.AnimatedPress}
 * that renders a tier-specific press body instead of the vanilla one.
 */
public class TieredAnimatedPress extends AnimatedKinetics {
    private final boolean basin;
    private final BlockState body;
    private final BlockState basinState;

    public TieredAnimatedPress(BlockState body, boolean basin, BlockState basinState) {
        this.body = body;
        this.basin = basin;
        this.basinState = basinState;
    }

    @Override
    public void draw(GuiGraphics graphics, int xOffset, int yOffset) {
        PoseStack matrixStack = graphics.pose();
        matrixStack.pushPose();
        matrixStack.translate((float) xOffset, (float) yOffset, 200.0F);
        matrixStack.mulPose(Axis.XP.rotationDegrees(-15.5F));
        matrixStack.mulPose(Axis.YP.rotationDegrees(22.5F));
        int scale = this.basin ? 23 : 24;
        this.blockElement(this.shaft(Direction.Axis.Z)).rotateBlock(0.0, 0.0, (double) getCurrentAngle()).scale((double) scale).render(graphics);
        this.blockElement(this.body).scale((double) scale).render(graphics);
        this.blockElement(AllPartialModels.MECHANICAL_PRESS_HEAD)
                .atLocal(0.0, (double) (-this.getAnimatedHeadOffset()), 0.0)
                .scale((double) scale)
                .render(graphics);
        if (this.basin) {
            this.blockElement(this.basinState).atLocal(0.0, 1.65, 0.0).scale((double) scale).render(graphics);
        }

        matrixStack.popPose();
    }

    private float getAnimatedHeadOffset() {
        float cycle = (AnimationTickHolder.getRenderTime() - (float) (this.offset * 8)) % 30.0F;
        if (cycle < 10.0F) {
            float progress = cycle / 10.0F;
            return -(progress * progress * progress);
        } else if (cycle < 15.0F) {
            return -1.0F;
        } else {
            return cycle < 20.0F ? -1.0F + (1.0F - (20.0F - cycle) / 5.0F) : 0.0F;
        }
    }
}

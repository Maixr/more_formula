package org.minecart.more_formula.compat.jei.animation;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Copy of Create's {@link com.simibubi.create.compat.jei.category.animations.AnimatedMixer}
 * that renders a tier-specific mixer body and head instead of the vanilla ones.
 */
public class TieredAnimatedMixer extends AnimatedKinetics {
    private final BlockState body;
    private final BlockState basinState;
    private final PartialModel headPartial;

    public TieredAnimatedMixer(BlockState body, BlockState basinState, PartialModel headPartial) {
        this.body = body;
        this.basinState = basinState;
        this.headPartial = headPartial;
    }

    @Override
    public void draw(GuiGraphics graphics, int xOffset, int yOffset) {
        PoseStack matrixStack = graphics.pose();
        matrixStack.pushPose();
        matrixStack.translate((float) xOffset, (float) yOffset, 200.0F);
        matrixStack.mulPose(Axis.XP.rotationDegrees(-15.5F));
        matrixStack.mulPose(Axis.YP.rotationDegrees(22.5F));
        int scale = 23;
        this.blockElement(this.cogwheel()).rotateBlock(0.0, (double) (getCurrentAngle() * 2.0F), 0.0).atLocal(0.0, 0.0, 0.0).scale((double) scale).render(graphics);
        this.blockElement(this.body).atLocal(0.0, 0.0, 0.0).scale((double) scale).render(graphics);
        float animation = (Mth.sin(AnimationTickHolder.getRenderTime() / 32.0F) + 1.0F) / 5.0F + 0.5F;
        this.blockElement(AllPartialModels.MECHANICAL_MIXER_POLE).atLocal(0.0, (double) animation, 0.0).scale((double) scale).render(graphics);
        this.blockElement(this.headPartial)
                .rotateBlock(0.0, (double) (getCurrentAngle() * 4.0F), 0.0)
                .atLocal(0.0, (double) animation, 0.0)
                .scale((double) scale)
                .render(graphics);
        this.blockElement(this.basinState).atLocal(0.0, 1.65, 0.0).scale((double) scale).render(graphics);
        matrixStack.popPose();
    }
}

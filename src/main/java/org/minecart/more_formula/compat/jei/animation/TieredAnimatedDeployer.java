package org.minecart.more_formula.compat.jei.animation;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import com.simibubi.create.content.kinetics.deployer.DeployerBlock;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Copy of Create's {@link com.simibubi.create.compat.jei.category.animations.AnimatedDeployer}
 * that renders a tier-specific deployer body instead of the vanilla one.
 */
public class TieredAnimatedDeployer extends AnimatedKinetics {
    private final BlockState body;
    private final BlockState depotState;

    public TieredAnimatedDeployer(BlockState body, BlockState depotState) {
        this.body = body;
        this.depotState = depotState;
    }

    @Override
    public void draw(GuiGraphics graphics, int xOffset, int yOffset) {
        PoseStack matrixStack = graphics.pose();
        matrixStack.pushPose();
        matrixStack.translate((float) xOffset, (float) yOffset, 100.0F);
        matrixStack.mulPose(Axis.XP.rotationDegrees(-15.5F));
        matrixStack.mulPose(Axis.YP.rotationDegrees(22.5F));
        int scale = 20;
        this.blockElement(this.shaft(Direction.Axis.Z)).rotateBlock(0.0, 0.0, (double) getCurrentAngle()).scale((double) scale).render(graphics);
        this.blockElement(this.pointedDown(this.body))
                .scale((double) scale)
                .render(graphics);
        float cycle = (AnimationTickHolder.getRenderTime() - (float) (this.offset * 8)) % 30.0F;
        float offset = cycle < 10.0F ? cycle / 10.0F : (cycle < 20.0F ? (20.0F - cycle) / 10.0F : 0.0F);
        matrixStack.pushPose();
        matrixStack.translate(0.0F, offset * 17.0F, 0.0F);
        this.blockElement(AllPartialModels.DEPLOYER_POLE).rotateBlock(90.0, 0.0, 0.0).scale((double) scale).render(graphics);
        this.blockElement(AllPartialModels.DEPLOYER_HAND_HOLDING).rotateBlock(90.0, 0.0, 0.0).scale((double) scale).render(graphics);
        matrixStack.popPose();
        this.blockElement(this.depotState).atLocal(0.0, 2.0, 0.0).scale((double) scale).render(graphics);
        matrixStack.popPose();
    }

    private BlockState pointedDown(BlockState state) {
        if (state.hasProperty(DeployerBlock.FACING)) {
            state = state.setValue(DeployerBlock.FACING, Direction.DOWN);
        }
        if (state.hasProperty(DeployerBlock.AXIS_ALONG_FIRST_COORDINATE)) {
            state = state.setValue(DeployerBlock.AXIS_ALONG_FIRST_COORDINATE, false);
        }
        return state;
    }
}

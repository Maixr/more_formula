package org.minecart.more_formula.compat.jei.animation;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.gui.UIRenderHelper;
import net.createmod.catnip.platform.NeoForgeCatnipServices;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

/**
 * Copy of Create's {@link com.simibubi.create.compat.jei.category.animations.AnimatedSpout}
 * that renders a tier-specific spout body and nozzle parts instead of the vanilla ones.
 */
public class TieredAnimatedSpout extends AnimatedKinetics {
    private final BlockState body;
    private final PartialModel top;
    private final PartialModel middle;
    private final PartialModel bottom;
    private final BlockState depotState;
    private List<FluidStack> fluids = List.of();

    public TieredAnimatedSpout(BlockState body, PartialModel top, PartialModel middle, PartialModel bottom, BlockState depotState) {
        this.body = body;
        this.top = top;
        this.middle = middle;
        this.bottom = bottom;
        this.depotState = depotState;
    }

    public TieredAnimatedSpout withFluids(List<FluidStack> fluids) {
        this.fluids = fluids == null ? List.of() : fluids;
        return this;
    }

    @Override
    public void draw(GuiGraphics graphics, int xOffset, int yOffset) {
        if (this.fluids.isEmpty()) {
            return;
        }
        PoseStack matrixStack = graphics.pose();
        matrixStack.pushPose();
        matrixStack.translate((float) xOffset, (float) yOffset, 100.0F);
        matrixStack.mulPose(Axis.XP.rotationDegrees(-15.5F));
        matrixStack.mulPose(Axis.YP.rotationDegrees(22.5F));
        int scale = 20;
        this.blockElement(this.body).scale((double) scale).render(graphics);
        float cycle = (AnimationTickHolder.getRenderTime() - (float) (this.offset * 8)) % 30.0F;
        float squeeze = cycle < 20.0F ? Mth.sin((float) ((double) (cycle / 20.0F) * Math.PI)) : 0.0F;
        squeeze *= 20.0F;
        matrixStack.pushPose();
        this.blockElement(this.top).scale((double) scale).render(graphics);
        matrixStack.translate(0.0F, -3.0F * squeeze / 32.0F, 0.0F);
        this.blockElement(this.middle).scale((double) scale).render(graphics);
        matrixStack.translate(0.0F, -3.0F * squeeze / 32.0F, 0.0F);
        this.blockElement(this.bottom).scale((double) scale).render(graphics);
        matrixStack.translate(0.0F, -3.0F * squeeze / 32.0F, 0.0F);
        matrixStack.popPose();
        this.blockElement(this.depotState).atLocal(0.0, 2.0, 0.0).scale((double) scale).render(graphics);
        AnimatedKinetics.DEFAULT_LIGHTING.applyLighting();
        matrixStack.pushPose();
        UIRenderHelper.flipForGuiRender(matrixStack);
        matrixStack.scale(16.0F, 16.0F, 16.0F);
        float from = 0.1875F;
        float to = 1.0625F;
        FluidStack fluidStack = this.fluids.getFirst();
        NeoForgeCatnipServices.FLUID_RENDERER
                .renderFluidBox(fluidStack, from, from, from, to, to, to, graphics.bufferSource(), matrixStack, 15728880, false, true);
        matrixStack.popPose();
        float width = 0.0078125F * squeeze;
        matrixStack.translate((float) scale / 2.0F, (float) scale * 1.5F, (float) scale / 2.0F);
        UIRenderHelper.flipForGuiRender(matrixStack);
        matrixStack.scale(16.0F, 16.0F, 16.0F);
        matrixStack.translate(-0.5F, 0.0F, -0.5F);
        from = -width / 2.0F + 0.5F;
        to = width / 2.0F + 0.5F;
        NeoForgeCatnipServices.FLUID_RENDERER
                .renderFluidBox(fluidStack, from, 0.0F, from, to, 2.0F, to, graphics.bufferSource(), matrixStack, 15728880, false, true);
        graphics.flush();
        Lighting.setupFor3DItems();
        matrixStack.popPose();
    }
}

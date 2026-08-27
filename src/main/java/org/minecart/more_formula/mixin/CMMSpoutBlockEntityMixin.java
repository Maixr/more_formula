package org.minecart.more_formula.mixin;

import com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.fluids.FluidStack;
import net.yxiao233.createmoremachines.api.content.spout.CMMSpoutBlockEntity;
import org.minecart.more_formula.util.TierHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CMMSpoutBlockEntity.class)
public abstract class CMMSpoutBlockEntityMixin {
    @Shadow
    protected abstract FluidStack getCurrentFluidInTank();

    @Inject(method = "onItemReceived", at = @At("HEAD"), cancellable = true)
    private void moreFormula$gateFillingOnReceived(TransportedItemStack transported, TransportedItemStackHandlerBehaviour handler, CallbackInfoReturnable<BeltProcessingBehaviour.ProcessingResult> cir) {
        if (!TierHelper.isFillingAllowed((BlockEntity) (Object) this, ((BlockEntity) (Object) this).getLevel(), transported.stack, null)) {
            cir.setReturnValue(BeltProcessingBehaviour.ProcessingResult.PASS);
        }
    }

    @Inject(method = "whenItemHeld", at = @At("HEAD"), cancellable = true)
    private void moreFormula$gateFillingWhileHeld(TransportedItemStack transported, TransportedItemStackHandlerBehaviour handler, CallbackInfoReturnable<BeltProcessingBehaviour.ProcessingResult> cir) {
        if (!TierHelper.isFillingAllowed((BlockEntity) (Object) this, ((BlockEntity) (Object) this).getLevel(), transported.stack, getCurrentFluidInTank())) {
            cir.setReturnValue(BeltProcessingBehaviour.ProcessingResult.PASS);
        }
    }
}

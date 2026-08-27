package org.minecart.more_formula;

import com.simibubi.create.content.kinetics.deployer.DeployerRecipeSearchEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import org.minecart.more_formula.util.TierHelper;

@EventBusSubscriber(modid = More_formula.MODID)
public class DeployerRecipeGate {
    @SubscribeEvent
    public static void onDeployerRecipeSearch(DeployerRecipeSearchEvent event) {
        var recipe = event.getRecipe();
        if (recipe != null && !TierHelper.isAllowed(event.getBlockEntity(), recipe.id())) {
            event.setCanceled(true);
        }
    }
}

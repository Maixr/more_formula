package org.minecart.more_formula.compat.kubejs.wrapper;

import org.minecart.more_formula.Config;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;

public class MoreFormulaKubeJSConfig {

    public static void setTier(String recipeId, int tier) {
        if (recipeId.endsWith("*")) {
            Config.addPrefixTier(recipeId, tier);
        } else {
            Config.addTier(ResourceLocation.parse(recipeId), tier);
        }
    }

    public static void setPrefixTier(String prefix, int tier) {
        Config.addPrefixTier(prefix, tier);
    }

    public static void removeTier(String recipeId) {
        Config.removeTier(ResourceLocation.parse(recipeId));
    }

    public static int getTier(String recipeId) {
        return Config.getRequiredTier(ResourceLocation.parse(recipeId));
    }

    public static Map<ResourceLocation, Integer> getAllTiers() {
        return Config.getAllTiers();
    }

    public static List<Integer> getKnownTiers() {
        return Config.getKnownTiers();
    }

}

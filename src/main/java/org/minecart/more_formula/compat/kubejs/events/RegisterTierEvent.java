package org.minecart.more_formula.compat.kubejs.events;

import dev.latvian.mods.kubejs.event.EventResult;
import dev.latvian.mods.kubejs.event.KubeEvent;
import org.minecart.more_formula.Config;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class RegisterTierEvent implements KubeEvent {
    private final Map<ResourceLocation, Integer> exactTiers = new HashMap<>();
    private final Map<String, Integer> prefixTiers = new HashMap<>();

    public void setTier(String recipeId, int tier) {
        if (recipeId.endsWith("*")) {
            prefixTiers.put(recipeId, tier);
        } else {
            setTier(ResourceLocation.parse(recipeId), tier);
        }
    }

    public void setTier(ResourceLocation recipeId, int tier) {
        exactTiers.put(recipeId, tier);
    }

    public void removeTier(String recipeId) {
        removeTier(ResourceLocation.parse(recipeId));
    }

    public void removeTier(ResourceLocation recipeId) {
        Config.removeTier(recipeId);
    }

    public int getTier(String recipeId) {
        return getTier(ResourceLocation.parse(recipeId));
    }

    public int getTier(ResourceLocation recipeId) {
        Integer tier = exactTiers.get(recipeId);
        return tier != null ? tier : Config.getRequiredTier(recipeId);
    }

    public Map<ResourceLocation, Integer> getAllTiers() {
        Map<ResourceLocation, Integer> all = new HashMap<>(Config.getAllTiers());
        all.putAll(exactTiers);
        return all;
    }

    @Override
    public void afterPosted(EventResult result) {
        for (var entry : exactTiers.entrySet()) {
            Config.addTier(entry.getKey(), entry.getValue());
        }
        for (var entry : prefixTiers.entrySet()) {
            Config.addPrefixTier(entry.getKey(), entry.getValue());
        }
    }
}

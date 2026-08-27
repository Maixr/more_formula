package org.minecart.more_formula;

import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Config {
    private static final Map<ResourceLocation, Integer> EXACT_TIERS = new HashMap<>();
    private static final List<PrefixTier> PREFIX_TIERS = new ArrayList<>();
    private static final Map<ResourceLocation, Integer> KUBEJS_EXACT_TIERS = new HashMap<>();
    private static final List<PrefixTier> KUBEJS_PREFIX_TIERS = new ArrayList<>();

    private record PrefixTier(String prefix, int tier) {
    }

    public static int getRequiredTier(ResourceLocation recipeId) {
        Integer exact = EXACT_TIERS.get(recipeId);
        if (exact != null) {
            return exact;
        }
        Integer kubejs = KUBEJS_EXACT_TIERS.get(recipeId);
        if (kubejs != null) {
            return kubejs;
        }
        String id = recipeId.toString();
        for (PrefixTier prefix : PREFIX_TIERS) {
            if (id.startsWith(prefix.prefix())) {
                return prefix.tier();
            }
        }
        for (PrefixTier prefix : KUBEJS_PREFIX_TIERS) {
            if (id.startsWith(prefix.prefix())) {
                return prefix.tier();
            }
        }
        return 0;
    }

    public static List<Integer> getKnownTiers() {
        var allTiers = new ArrayList<>(EXACT_TIERS.values());
        allTiers.addAll(KUBEJS_EXACT_TIERS.values());
        return allTiers.stream()
                .filter(tier -> tier != 0)
                .distinct()
                .sorted()
                .toList();
    }

    public static void addTier(ResourceLocation recipeId, int tier) {
        if (tier >= -1 && tier <= 4 && tier != 0) {
            EXACT_TIERS.put(recipeId, tier);
            KUBEJS_EXACT_TIERS.put(recipeId, tier);
        }
    }

    public static void addPrefixTier(String prefix, int tier) {
        if (tier >= -1 && tier <= 4 && tier != 0 && prefix != null && !prefix.isEmpty()) {
            if (prefix.endsWith("*")) {
                prefix = prefix.substring(0, prefix.length() - 1);
            }
            PREFIX_TIERS.add(new PrefixTier(prefix, tier));
            KUBEJS_PREFIX_TIERS.add(new PrefixTier(prefix, tier));
        }
    }

    public static void removeTier(ResourceLocation recipeId) {
        EXACT_TIERS.remove(recipeId);
        KUBEJS_EXACT_TIERS.remove(recipeId);
    }

    public static Map<ResourceLocation, Integer> getAllTiers() {
        var all = new HashMap<>(EXACT_TIERS);
        all.putAll(KUBEJS_EXACT_TIERS);
        return all;
    }

    public static void clear() {
        EXACT_TIERS.clear();
        PREFIX_TIERS.clear();
        KUBEJS_EXACT_TIERS.clear();
        KUBEJS_PREFIX_TIERS.clear();
    }
}

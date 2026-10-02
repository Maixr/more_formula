package org.minecart.more_formula.compat.handmade;

import net.minecraft.world.item.crafting.RecipeHolder;
import org.minecart.more_formula.Config;

import java.util.ArrayList;
import java.util.List;

/**
 * Runtime gate shared by the optional Create: Hand Made mixins.
 * Hand Made must only consume recipes that are explicitly at Tier.ZERO
 * (an absent tier entry is also Tier.ZERO in More Formula's model).
 */
public final class HandMadeRecipeGate {
    private HandMadeRecipeGate() {
    }

    public static boolean isTierZero(RecipeHolder<?> holder) {
        return holder != null && Config.getRequiredTier(holder.id()) == 0;
    }

    public static List<RecipeHolder<?>> onlyTierZero(List<?> recipes) {
        List<RecipeHolder<?>> filtered = new ArrayList<>();
        for (Object recipe : recipes) {
            if (recipe instanceof RecipeHolder<?> holder && isTierZero(holder)) {
                filtered.add(holder);
            }
        }
        return filtered;
    }

    public static boolean isTierZeroResult(Object result) {
        return result instanceof RecipeHolder<?> holder && isTierZero(holder);
    }
}

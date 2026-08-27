package org.minecart.more_formula.compat.jei;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.compat.jei.category.ItemApplicationCategory;
import com.tterrag.registrate.util.entry.BlockEntry;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.yxiao233.createmoremachines.api.registry.BuiltInAdvancedMachineTypes;
import org.jetbrains.annotations.NotNull;
import org.minecart.more_formula.Config;
import org.minecart.more_formula.More_formula;
import org.minecart.more_formula.compat.jei.category.TieredDeployingCategory;
import org.minecart.more_formula.compat.jei.category.TieredMixingCategory;
import org.minecart.more_formula.compat.jei.category.TieredPackingCategory;
import org.minecart.more_formula.compat.jei.category.TieredPressingCategory;
import org.minecart.more_formula.compat.jei.category.TieredSawingCategory;
import org.minecart.more_formula.compat.jei.category.TieredSequencedAssemblyCategory;
import org.minecart.more_formula.compat.jei.category.TieredSpoutCategory;
import org.minecart.more_formula.compat.jei.category.sequenced.TieredMachineContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

@JeiPlugin
public class MoreFormulaJeiPlugin implements IModPlugin {
    @SuppressWarnings("rawtypes")
    private interface CatFactory {
        CreateRecipeCategory<?> create(CreateRecipeCategory.Info<?> info, BlockEntry<? extends Block> machine,
                                        BlockEntry<? extends Block> basin, String tierName);
    }

    @SuppressWarnings("rawtypes")
    private record Kind(AllRecipeTypes type, String categoryPath, int bgWidth, int bgHeight,
                        BuiltInAdvancedMachineTypes.AdvancedMachineType<?>[] catalystTypes, CatFactory factory) {
    }

    private final List<CreateRecipeCategory<?>> tieredCategories = new ArrayList<>();
    private List<Kind> kinds;

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(More_formula.MODID, "jei");
    }

    private List<Kind> kinds() {
        if (kinds == null) {
            kinds = List.of(
                    new Kind(AllRecipeTypes.PRESSING, "pressing", 177, 70,
                            machines(BuiltInAdvancedMachineTypes.PRESS),
                            (info, machine, basin, tierName) -> new TieredPressingCategory(castInfo(info), state(machine), basinState(basin))),
                    new Kind(AllRecipeTypes.MIXING, "mixing", 177, 103,
                            machines(BuiltInAdvancedMachineTypes.MIXER, BuiltInAdvancedMachineTypes.BASIN),
                            (info, machine, basin, tierName) -> new TieredMixingCategory(castInfo(info), state(machine), basinState(basin), headPartial(tierName))),
                    new Kind(AllRecipeTypes.COMPACTING, "packing", 177, 103,
                            machines(BuiltInAdvancedMachineTypes.PRESS, BuiltInAdvancedMachineTypes.BASIN),
                            (info, machine, basin, tierName) -> new TieredPackingCategory(castInfo(info), state(machine), basinState(basin))),
                    new Kind(AllRecipeTypes.CUTTING, "sawing", 177, 70,
                            machines(BuiltInAdvancedMachineTypes.SAW),
                            (info, machine, basin, tierName) -> new TieredSawingCategory(castInfo(info), state(machine))),
                    new Kind(AllRecipeTypes.FILLING, "spout_filling", 177, 70,
                            machines(BuiltInAdvancedMachineTypes.SPOUT),
                            (info, machine, basin, tierName) -> new TieredSpoutCategory(castInfo(info), state(machine), spoutPartials(tierName), depotState(tierName))),
                    new Kind(AllRecipeTypes.DEPLOYING, "deploying", 177, 70,
                            machines(BuiltInAdvancedMachineTypes.DEPLOYER),
                            (info, machine, basin, tierName) -> new TieredDeployingCategory(castInfo(info), state(machine), depotState(tierName))),
                    new Kind(AllRecipeTypes.ITEM_APPLICATION, "item_application", 177, 60,
                            machines(BuiltInAdvancedMachineTypes.DEPLOYER),
                            (info, machine, basin, tierName) -> new ItemApplicationCategory(castInfo(info))),
                    new Kind(AllRecipeTypes.SEQUENCED_ASSEMBLY, "sequenced_assembly", 180, 115,
                            machines(BuiltInAdvancedMachineTypes.PRESS, BuiltInAdvancedMachineTypes.SPOUT, BuiltInAdvancedMachineTypes.DEPLOYER),
                            (info, machine, basin, tierName) -> new TieredSequencedAssemblyCategory(castInfo(info), sequencedContext(tierName)))
            );
        }
        return kinds;
    }

    @Override
    public void registerCategories(@NotNull IRecipeCategoryRegistration registration) {
        tieredCategories.clear();
        for (int tier : Config.getKnownTiers()) {
            for (Kind kind : kinds()) {
                BlockEntry<? extends Block> machine = machine(kind.type(), tier);
                if (machine == null) {
                    continue;
                }
                CreateRecipeCategory<?> category = build(kind, tier, machine, basin(tier));
                if (category != null) {
                    tieredCategories.add(category);
                }
            }
        }
        registration.addRecipeCategories(tieredCategories.toArray(new IRecipeCategory[0]));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private CreateRecipeCategory<?> build(Kind kind, int tier, BlockEntry<? extends Block> machine, BlockEntry<? extends Block> basin) {
        ResourceLocation uid = ResourceLocation.fromNamespaceAndPath(More_formula.MODID,
                "tiered/" + kind.categoryPath() + "_" + tier);

        Component title = Component.translatable("more_formula.jei.tiered_title",
                Component.translatable("more_formula.tier." + tier),
                Component.translatable("create.recipe." + kind.categoryPath()));

        Item iconItem = machine.get().asItem();
        CreateRecipeCategory.Info info = new CreateRecipeCategory.Info(
                mezz.jei.api.recipe.RecipeType.createRecipeHolderType(uid),
                title,
                new com.simibubi.create.compat.jei.EmptyBackground(kind.bgWidth(), kind.bgHeight()),
                new com.simibubi.create.compat.jei.ItemIcon(() -> new ItemStack(iconItem)),
                () -> gatedHolders(kind.type(), tier),
                unlockedCatalysts(kind.catalystTypes(), tier)
        );
        return kind.factory().create(info, machine, basin, tierName(tier));
    }

    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registration) {
        tieredCategories.forEach(category -> category.registerRecipes(registration));
    }

    @Override
    public void registerRecipeCatalysts(@NotNull IRecipeCatalystRegistration registration) {
        tieredCategories.forEach(category -> category.registerCatalysts(registration));
    }

    @Override
    public void onRuntimeAvailable(@NotNull IJeiRuntime runtime) {
        Level level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }
        IRecipeManager recipeManager = runtime.getRecipeManager();
        for (Kind kind : kinds()) {
            Collection<?> gated = gatedAboveTier(level, kind.type());
            if (gated.isEmpty()) {
                continue;
            }
            mezz.jei.api.recipe.RecipeType<?> vanillaCategory = mezz.jei.api.recipe.RecipeType.createRecipeHolderType(
                    ResourceLocation.fromNamespaceAndPath("create", kind.categoryPath()));
            if (!categoryExists(runtime, vanillaCategory)) {
                continue;
            }
            hideAll(recipeManager, vanillaCategory, gated);
        }
    }

    private static boolean categoryExists(IJeiRuntime runtime, mezz.jei.api.recipe.RecipeType<?> jeiType) {
        return runtime.getRecipeManager()
                .createRecipeCategoryLookup()
                .limitTypes(List.of(jeiType))
                .includeHidden()
                .get()
                .findAny()
                .isPresent();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static Collection<?> gatedAboveTier(Level level, AllRecipeTypes type) {
        RecipeType vanillaType = type.getType();
        List holders = level.getRecipeManager().getAllRecipesFor(vanillaType);
        return ((List<RecipeHolder<?>>) holders).stream()
                .filter(holder -> Config.getRequiredTier(holder.id()) != 0)
                .toList();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static List<RecipeHolder<?>> gatedHolders(AllRecipeTypes type, int tier) {
        Level level = Minecraft.getInstance().level;
        if (level == null) {
            return List.of();
        }
        RecipeType vanillaType = type.getType();
        List holders = level.getRecipeManager().getAllRecipesFor(vanillaType);
        return ((List<RecipeHolder<?>>) holders).stream()
                .filter(holder -> Config.getRequiredTier(holder.id()) == tier)
                .toList();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void hideAll(IRecipeManager manager, mezz.jei.api.recipe.RecipeType jeiType, Collection recipes) {
        manager.hideRecipes(jeiType, recipes);
    }

    @SuppressWarnings("unchecked")
    private static <T> T castInfo(Object info) {
        return (T) info;
    }

    private static BlockState state(BlockEntry<? extends Block> entry) {
        return entry == null ? null : entry.get().defaultBlockState();
    }

    private static BlockState basinState(BlockEntry<? extends Block> entry) {
        return entry != null ? entry.get().defaultBlockState() : AllBlocks.BASIN.getDefaultState();
    }

    private static BlockState depotState(String tierName) {
        return safeState(entry(BuiltInAdvancedMachineTypes.DEPOT, tierName), AllBlocks.DEPOT.getDefaultState());
    }

    private static BlockState safeState(BlockEntry<? extends Block> entry, BlockState fallback) {
        return entry == null ? fallback : entry.get().defaultBlockState();
    }

    private static PartialModel headPartial(String tierName) {
        if (entry(BuiltInAdvancedMachineTypes.MIXER, tierName) == null) {
            return AllPartialModels.MECHANICAL_MIXER_HEAD;
        }
        return PartialModel.of(ResourceLocation.fromNamespaceAndPath("createmoremachines",
                "block/mechanical_mixer/head/" + tierName + "_mechanical_mixer_head"));
    }

    private static PartialModel[] spoutPartials(String tierName) {
        if (entry(BuiltInAdvancedMachineTypes.SPOUT, tierName) == null) {
            return new PartialModel[]{AllPartialModels.SPOUT_TOP, AllPartialModels.SPOUT_MIDDLE, AllPartialModels.SPOUT_BOTTOM};
        }
        return new PartialModel[]{
                PartialModel.of(ResourceLocation.fromNamespaceAndPath("createmoremachines", "block/spout/top/" + tierName + "_spout_top")),
                PartialModel.of(ResourceLocation.fromNamespaceAndPath("createmoremachines", "block/spout/middle/" + tierName + "_spout_middle")),
                PartialModel.of(ResourceLocation.fromNamespaceAndPath("createmoremachines", "block/spout/bottom/" + tierName + "_spout_bottom"))
        };
    }

    private static TieredMachineContext sequencedContext(String tierName) {
        return new TieredMachineContext(
                safeState(entry(BuiltInAdvancedMachineTypes.PRESS, tierName), AllBlocks.MECHANICAL_PRESS.getDefaultState()),
                safeState(entry(BuiltInAdvancedMachineTypes.SAW, tierName), AllBlocks.MECHANICAL_SAW.getDefaultState()),
                safeState(entry(BuiltInAdvancedMachineTypes.SPOUT, tierName), AllBlocks.SPOUT.getDefaultState()),
                spoutPartials(tierName),
                safeState(entry(BuiltInAdvancedMachineTypes.DEPLOYER, tierName), AllBlocks.DEPLOYER.getDefaultState()),
                depotState(tierName),
                basinState(entry(BuiltInAdvancedMachineTypes.BASIN, tierName))
        );
    }

    private static BlockEntry<? extends Block> basin(int tier) {
        return entry(BuiltInAdvancedMachineTypes.BASIN, tierName(tier));
    }

    private static BlockEntry<? extends Block> machine(AllRecipeTypes type, int tier) {
        BuiltInAdvancedMachineTypes.AdvancedMachineType<?> advancedType = switch (type) {
            case PRESSING, COMPACTING, SEQUENCED_ASSEMBLY -> BuiltInAdvancedMachineTypes.PRESS;
            case MIXING -> BuiltInAdvancedMachineTypes.MIXER;
            case CUTTING -> BuiltInAdvancedMachineTypes.SAW;
            case FILLING -> BuiltInAdvancedMachineTypes.SPOUT;
            case DEPLOYING, ITEM_APPLICATION -> BuiltInAdvancedMachineTypes.DEPLOYER;
            default -> null;
        };
        if (advancedType == null) {
            return null;
        }
        return entry(advancedType, tierName(tier));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static BlockEntry<? extends Block> entry(BuiltInAdvancedMachineTypes.AdvancedMachineType machineType, String tierName) {
        Object entry = machineType.getAdvancedMechanicals()
                .get(ResourceLocation.fromNamespaceAndPath("createmoremachines", tierName));
        return entry instanceof BlockEntry<?> blockEntry ? blockEntry : null;
    }

    private static String tierName(int tier) {
        return switch (tier) {
            case -1 -> "creative";
            case 1 -> "brass";
            case 2 -> "netherite";
            case 3 -> "end";
            case 4 -> "beyond";
            default -> "";
        };
    }

    @SafeVarargs
    private static BuiltInAdvancedMachineTypes.AdvancedMachineType<?>[] machines(BuiltInAdvancedMachineTypes.AdvancedMachineType<?>... machineTypes) {
        return machineTypes;
    }

    private static List<Supplier<? extends ItemStack>> unlockedCatalysts(BuiltInAdvancedMachineTypes.AdvancedMachineType<?>[] machineTypes, int requiredTier) {
        List<Supplier<? extends ItemStack>> list = new ArrayList<>();
        if (requiredTier == -1) {
            addMachinesOfTier(machineTypes, "creative", list);
            return list;
        }
        for (int tier = requiredTier; tier <= 4; tier++) {
            addMachinesOfTier(machineTypes, tierName(tier), list);
        }
        addMachinesOfTier(machineTypes, "creative", list);
        return list;
    }

    private static void addMachinesOfTier(BuiltInAdvancedMachineTypes.AdvancedMachineType<?>[] machineTypes, String tierName, List<Supplier<? extends ItemStack>> out) {
        if (tierName.isEmpty()) {
            return;
        }
        for (BuiltInAdvancedMachineTypes.AdvancedMachineType<?> machineType : machineTypes) {
            BlockEntry<? extends Block> entry = entry(machineType, tierName);
            if (entry == null) {
                continue;
            }
            Item item = entry.get().asItem();
            if (item == Items.AIR) {
                continue;
            }
            out.add(() -> new ItemStack(item));
        }
    }
}

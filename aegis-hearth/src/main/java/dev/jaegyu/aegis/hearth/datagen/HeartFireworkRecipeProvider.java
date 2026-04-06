// aegis-hearth/src/main/java/dev/jaegyu/aegis/hearth/datagen/HeartFireworkRecipeProvider.java
package dev.jaegyu.aegis.hearth.datagen;

import dev.jaegyu.aegis.hearth.listener.HeartFireworkListener;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.core.component.DataComponents;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class HeartFireworkRecipeProvider extends RecipeProvider {

    // dye item → packed RGB int
    private static final Map<Item, Integer> DYE_COLORS = new LinkedHashMap<>();

    static {
        DYE_COLORS.put(Items.WHITE_DYE,      0xFFFFFF);
        DYE_COLORS.put(Items.ORANGE_DYE,     0xFF8000);
        DYE_COLORS.put(Items.MAGENTA_DYE,    0xFF55FF);
        DYE_COLORS.put(Items.LIGHT_BLUE_DYE, 0x55FFFF);
        DYE_COLORS.put(Items.YELLOW_DYE,     0xFFFF00);
        DYE_COLORS.put(Items.LIME_DYE,       0x00FF00);
        DYE_COLORS.put(Items.PINK_DYE,       0xFF69B4);
        DYE_COLORS.put(Items.GRAY_DYE,       0x808080);
        DYE_COLORS.put(Items.LIGHT_GRAY_DYE, 0xC0C0C0);
        DYE_COLORS.put(Items.CYAN_DYE,       0x008080);
        DYE_COLORS.put(Items.PURPLE_DYE,     0x800080);
        DYE_COLORS.put(Items.BLUE_DYE,       0x0000FF);
        DYE_COLORS.put(Items.BROWN_DYE,      0x8B4513);
        DYE_COLORS.put(Items.GREEN_DYE,      0x008000);
        DYE_COLORS.put(Items.RED_DYE,        0xFF0000);
        DYE_COLORS.put(Items.BLACK_DYE,      0x000000);
    }

    protected HeartFireworkRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes() {
        var itemGetter = this.registries.lookupOrThrow(Registries.ITEM);

        for (var entry : DYE_COLORS.entrySet()) {
            Item dye = entry.getKey();
            int rgb = entry.getValue();

            // Build a firework star with the color baked into custom_data
            CompoundTag tag = new CompoundTag();
            tag.putInt(HeartFireworkListener.NBT_KEY, rgb);

            ItemStack result = new ItemStack(Items.FIREWORK_STAR);
            result.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));

            String dyeName = dye.builtInRegistryHolder().key().identifier().getPath(); // e.g. "pink_dye"

            ShapelessRecipeBuilder.shapeless(itemGetter, RecipeCategory.MISC, result)
                    .requires(Items.GUNPOWDER)
                    .requires(Items.PINK_PETALS)
                    .requires(dye)
                    .unlockedBy("has_pink_petals", this.has(Items.PINK_PETALS))
                    .save(this.output, Identifier.fromNamespaceAndPath(
                            "aegis_hearth", "heart_firework_star_" + dyeName).toString());
        }
    }

    public static class Runner extends RecipeProvider.Runner {
        public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
            super(output, registries);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
            return new HeartFireworkRecipeProvider(registries, output);
        }

        @Override
        public String getName() {
            return "Aegis Hearth Heart Firework Recipes";
        }
    }
}
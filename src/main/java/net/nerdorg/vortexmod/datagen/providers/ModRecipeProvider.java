package net.nerdorg.vortexmod.datagen.providers;

import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.nerdorg.vortexmod.VortexMod;
import net.nerdorg.vortexmod.block.ModBlocks;
import net.nerdorg.vortexmod.datagen.loot.ModBlockLootTables;

import java.util.List;
import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider(PackOutput pOutput) {
        super(pOutput);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> pWriter) {
        ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, ModBlocks.TARDIS_THROTTLE.get())
                .pattern("BBB")
                .pattern("C C")
                .pattern("WWW")
                .define('W', ItemTags.WOODEN_SLABS)
                .define('C', Items.COBBLESTONE)
                .define('B', Items.IRON_BLOCK)
                .unlockedBy(getHasName(ModBlocks.TARDIS_CORE.get()), has(ModBlocks.TARDIS_CORE.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.TARDIS_CORE.get())
                .pattern("EQE")
                .pattern("ADA")
                .pattern("EQE")
                .define('D', Items.DIAMOND)
                .define('E', Items.ENDER_PEARL)
                .define('Q', Items.QUARTZ)
                .define('A', Items.AMETHYST_SHARD)
                .unlockedBy(getHasName(Items.ENDER_PEARL), has(Items.ENDER_PEARL))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, ModBlocks.TARDIS_COORDINATE_DESIGNATOR.get())
                .pattern("ICI")
                .pattern("RRR")
                .pattern("WWW")
                .define('R', Items.REDSTONE)
                .define('I', Items.IRON_BLOCK)
                .define('C', Items.COMPASS)
                .define('W', ItemTags.WOODEN_SLABS)
                .unlockedBy(getHasName(ModBlocks.TARDIS_CORE.get()), has(ModBlocks.TARDIS_CORE.get()))
                .save(pWriter);
    }

    protected static void oreSmelting(Consumer<FinishedRecipe> pFinishedRecipeConsumer, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult, float pExperience, int pCookingTime, String pGroup) {
        oreCooking(pFinishedRecipeConsumer, RecipeSerializer.SMELTING_RECIPE, pIngredients, pCategory, pResult, pExperience, pCookingTime, pGroup, "_from_smelting");
    }

    protected static void oreBlasting(Consumer<FinishedRecipe> pFinishedRecipeConsumer, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult, float pExperience, int pCookingTime, String pGroup) {
        oreCooking(pFinishedRecipeConsumer, RecipeSerializer.BLASTING_RECIPE, pIngredients, pCategory, pResult, pExperience, pCookingTime, pGroup, "_from_blasting");
    }

    protected static void oreCooking(Consumer<FinishedRecipe> pFinishedRecipeConsumer, RecipeSerializer<? extends AbstractCookingRecipe> pSerializer, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult, float pExperience, int pCookingTime, String pGroup, String pSuffix) {
        for(ItemLike itemlike : pIngredients) {
            SimpleCookingRecipeBuilder
                    .generic(Ingredient.of(itemlike), pCategory, pResult, pExperience, pCookingTime, pSerializer)
                    .group(pGroup).unlockedBy(getHasName(itemlike), has(itemlike))
                    .save(pFinishedRecipeConsumer, VortexMod.MODID + ":" + getItemName(pResult) + pSuffix + "_" + getItemName(itemlike));
        }

    }
}

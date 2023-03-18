package net.quantumgoddess.adventuring.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.book.CookingRecipeCategory;
import net.minecraft.util.Identifier;
import net.quantumgoddess.adventuring.item.QuantumItems;

public class DryingRackRecipe
        extends AbstractCookingRecipe {
    public DryingRackRecipe(Identifier id, String group, CookingRecipeCategory category, Ingredient input,
            ItemStack output, float experience, int cookTime) {
        super(QuantumRecipeType.DRYING_RACK, id, group, category, input, output, experience, cookTime);
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(QuantumItems.DRYING_RACK);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return QuantumRecipeSerializer.DRYING_RACK;
    }
}

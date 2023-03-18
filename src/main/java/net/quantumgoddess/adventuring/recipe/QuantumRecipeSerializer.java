package net.quantumgoddess.adventuring.recipe;

import net.minecraft.recipe.CookingRecipeSerializer;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class QuantumRecipeSerializer {
    public static final RecipeSerializer<DryingRackRecipe> DRYING_RACK = new CookingRecipeSerializer<DryingRackRecipe>(
            DryingRackRecipe::new, 24000);

    public static void registerAll() {
        Registry.register(Registries.RECIPE_SERIALIZER, new Identifier("quantumadventuring", "drying_rack"),
                DRYING_RACK);
    }
}

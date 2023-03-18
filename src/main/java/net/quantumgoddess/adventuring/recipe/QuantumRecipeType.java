package net.quantumgoddess.adventuring.recipe;

import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class QuantumRecipeType {

    public static final RecipeType<DryingRackRecipe> DRYING_RACK = new RecipeType<DryingRackRecipe>() {

        public String toString() {
            return "drying_rack";
        }
    };

    public static void registerAll() {
        Registry.register(Registries.RECIPE_TYPE,
                new Identifier("quantumadventuring", "drying_rack"), DRYING_RACK);
    }

}

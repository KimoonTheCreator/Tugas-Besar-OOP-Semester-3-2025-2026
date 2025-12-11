package org.example.model.recipe;

import java.util.ArrayList;
import java.util.List;

import org.example.model.items.Dish;

public class RecipeManager {
    private List<Recipe> recipes;

    public RecipeManager() {
        recipes = new ArrayList<>();
    }

    public void addRecipe(Recipe r) {
        recipes.add(r);
    }

    public Recipe findMatchingRecipe(Dish dish) {
        for (Recipe r : recipes) {
            if (r.getName().matches(dish.getName())) return r;
        }
        return null;
    }
}

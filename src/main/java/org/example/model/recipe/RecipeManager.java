package org.example.model.recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.example.model.items.Dish;
import org.example.model.items.Ingredient;
import org.example.model.enums.IngredientState;

public class RecipeManager {
    private static RecipeManager instance;
    private List<Recipe> recipes;

    public RecipeManager() {
        recipes = new ArrayList<>();
        initializeRecipes();
    }

    public static RecipeManager getInstance() {
        if (instance == null) {
            instance = new RecipeManager();
        }
        return instance;
    }

    public List<Recipe> getRecipes() {
        return recipes;
    }

    public Recipe getRandomRecipe() {
        if (recipes.isEmpty())
            return null;
        int index = (int) (Math.random() * recipes.size());
        return recipes.get(index);
    }

    private void initializeRecipes() {
        // Ganti nama resep jadi simpel: "Margherita", "Sausage", "Chicken"
        // Agar nanti pas bikin Pizza jadi: new Pizza("Margherita") -> Name: "Pizza Margherita"

        // 1. Margherita
        Recipe margherita = new Recipe("Margherita");
        margherita.addComponent("Adonan", IngredientState.CHOPPED);
        margherita.addComponent("Tomat", IngredientState.CHOPPED);
        margherita.addComponent("Keju", IngredientState.CHOPPED);
        recipes.add(margherita);

        // 2. Sosis (Sausage)
        Recipe sausage = new Recipe("Sausage");
        sausage.addComponent("Adonan", IngredientState.CHOPPED);
        sausage.addComponent("Tomat", IngredientState.CHOPPED);
        sausage.addComponent("Keju", IngredientState.CHOPPED);
        sausage.addComponent("Sosis", IngredientState.CHOPPED);
        recipes.add(sausage);

        // 3. Ayam (Chicken)
        Recipe chicken = new Recipe("Chicken");
        chicken.addComponent("Adonan", IngredientState.CHOPPED);
        chicken.addComponent("Tomat", IngredientState.CHOPPED);
        chicken.addComponent("Keju", IngredientState.CHOPPED);
        chicken.addComponent("Ayam", IngredientState.CHOPPED);
        recipes.add(chicken);
    }

    public void addRecipe(Recipe r) {
        recipes.add(r);
    }

    /**
     * Finds a recipe that matches the given list of ingredients.
     */
    public Recipe findMatchingRecipe(List<Ingredient> ingredients) {
        // Convert list to checkable map
        Map<String, IngredientState> inputMap = new HashMap<>();
        for (Ingredient ing : ingredients) {
            inputMap.put(ing.getName(), ing.getState());
        }

        for (Recipe r : recipes) {
            if (r.matches(inputMap)) {
                return r;
            }
        }
        return null;
    }

    public Recipe findMatchingRecipe(Dish dish) {
        for (Recipe r : recipes) {
            if (r.getName().matches(dish.getName()))
                return r;
        }
        return null;
    }
}

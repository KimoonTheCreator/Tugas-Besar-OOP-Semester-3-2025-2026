package org.example.model.recipe;

import java.util.HashMap;
import java.util.Map;

import org.example.model.enums.IngredientState;
import org.example.model.items.Ingredient;

public class Recipe {
    private String name;
    private Map<String, IngredientState> components;

    public Recipe(String name) {
        this.name = name;
        this.components = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public Map<String, IngredientState> getComponents() {
        return components;
    }

    public void setComponents(Map<String, IngredientState> components) {
        this.components = components;
    }

    public void addComponent(String ingredientName, IngredientState state) {
        components.put(ingredientName, state);
    }

    public boolean matches(Map<String, IngredientState> inputIngredients) {
        if (inputIngredients.size() != components.size()) {
            return false;
        }
        for (Map.Entry<String, IngredientState> entry : components.entrySet()) {
            String requiredName = entry.getKey();
            IngredientState requiredState = entry.getValue();

            if (!inputIngredients.containsKey(requiredName) || inputIngredients.get(requiredName) != requiredState) {
                return false;
            }
        }
        return true;
    }
}
package org.example.model.recipe;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.example.model.enums.IngredientState;
import org.example.model.items.Ingredient;

public class Recipe {
    private String name;
    private Map<String, IngredientState> components;

    public Recipe (String name){
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

    public void insertComponents(int n){
        Scanner sc = new Scanner(System.in);

        for (int i = 0; i < n; i++) {
            String ingreStr = sc.next();   // sii ingredientnya
            String stateStr = sc.next();  // requirementnya (harus chopped kah, cooked kah, dll)

            IngredientState state = IngredientState.valueOf(stateStr.toUpperCase());
            // Ingredient ingre = Ingredient.valueOf(ingreStr.toUpperCase());

            components.put(ingreStr, state);
        }
    }
}

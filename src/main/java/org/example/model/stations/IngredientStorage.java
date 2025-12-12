package org.example.model.stations;

import org.example.model.entities.Chef;
import org.example.model.items.Ingredient;
import org.example.model.map.Position;
import org.example.model.items.Plate;

public class IngredientStorage extends Station {
    private String ingredientName;

    public IngredientStorage(Position position, String ingredientName) {
        super("Ingredient Storage", position);
        this.ingredientName = ingredientName;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public void interact(Chef chef) {
        // Case 1: Empty Hand -> Take Ingredient
        if (!chef.isHoldingItem()) {
            Ingredient newIngredient = new Ingredient(ingredientName);
            chef.setInventory(newIngredient);
        }
        // Case 2: Holding Plate -> Add Ingredient to Plate
        else if (chef.getInventory() instanceof Plate) {
            Plate plate = (Plate) chef.getInventory();
            if (plate.isClean()) { // Validate if plate accepts ingredients?
                Ingredient newIngredient = new Ingredient(ingredientName);
                if (plate.canAccept(newIngredient)) {
                    plate.addItem(newIngredient);
                }
            }
        }
    }
}

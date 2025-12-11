package org.example.model.stations;

import org.example.model.entities.Chef;
import org.example.model.items.Ingredient;
import org.example.model.map.Position;


public class IngredientStorage extends Station{
    private String ingredientName;

    public IngredientStorage(String name, Position position,String ingredientName){
        super(name, position);
        this.ingredientName = ingredientName;
    }

    public void interact(Chef chef){
        if (!chef.isHoldingItem() && this.isEmpty()){
            Ingredient newIngredient = new Ingredient(ingredientName);
            chef.setInventory(newIngredient)   ;
        } else if (!chef.isHoldingItem() && !this.isEmpty()) {
            chef.setInventory(this.takeItem());
        } else if (chef.isHoldingItem() && this.isEmpty()) {
            this.addItem(chef.dropItem());
        }
    }
}

package org.example.model.stations;

import org.example.model.entities.Chef;
import org.example.model.interfaces.Preparable;
import org.example.model.map.Position;
import org.example.model.items.Item;
import org.example.model.items.Ingredient;
import org.example.model.items.Plate;
import org.example.model.items.Dish;
import org.example.model.items.KitchenUtensils;
import org.example.model.recipe.Recipe;
import org.example.model.recipe.RecipeManager;

import java.util.ArrayList;
import java.util.List;

public class AssemblyStation extends Station {

    public AssemblyStation(Position position) {
        super("Assembly Station", position);
    }

    public void interact(Chef chef) {
        if (chef.isHoldingItem() && this.isEmpty()) {
            this.addItem(chef.dropItem());
        } else if (!chef.isHoldingItem() && !isEmpty()) {
            chef.setInventory(this.takeItem());
        } else if (chef.isHoldingItem() && !isEmpty()) {
            mergeItems(chef);
        }
    }

    private void mergeItems(Chef chef) {
        Item chefItem = chef.getInventory();
        Item stationItem = this.getItem();

        Plate targetPlate = null;

        // Case 1: Station has Plate
        if (stationItem instanceof Plate) {
            targetPlate = (Plate) stationItem;
            if (chefItem instanceof Preparable) {
                targetPlate.addItem((Preparable) chefItem);
                chef.dropItem();
            } else if (chefItem instanceof KitchenUtensils) {
                KitchenUtensils utensil = (KitchenUtensils) chefItem;
                for (Preparable p : utensil.getContents()) {
                    targetPlate.addItem(p);
                }
                utensil.emptyContents();
            }
        }
        // Case 2: Chef has Plate
        else if (chefItem instanceof Plate) {
            targetPlate = (Plate) chefItem;
            if (stationItem instanceof Preparable) {
                targetPlate.addItem((Preparable) stationItem);
                this.takeItem();
            } else if (stationItem instanceof KitchenUtensils) {
                KitchenUtensils utensil = (KitchenUtensils) stationItem;
                for (Preparable p : utensil.getContents()) {
                    targetPlate.addItem(p);
                }
                utensil.emptyContents();
            }
        }

        // Check for Recipe Completion
        if (targetPlate != null) {
            checkAndAssembleRecipe(targetPlate);
        }
    }

    private void checkAndAssembleRecipe(Plate plate) {
        List<Ingredient> ingredients = new ArrayList<>();
        for (Preparable p : plate.getContents()) {
            if (p instanceof Ingredient) {
                ingredients.add((Ingredient) p);
            }
        }

        Recipe match = RecipeManager.getInstance().findMatchingRecipe(ingredients);
        if (match != null) {
            // Create the dish
            Dish newDish = new Dish(match.getName());
            newDish.setComponents(new ArrayList<>(ingredients));

            // Replace plate contents
            plate.emptyContents();
            plate.addItem(newDish);
            System.out.println("Assembled: " + match.getName());
        }
    }
}

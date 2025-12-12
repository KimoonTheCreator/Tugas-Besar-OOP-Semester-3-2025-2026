package org.example.model.items;

import org.example.model.interfaces.Preparable;
import org.example.model.enums.IngredientState;

public class Ingredient extends Item implements Preparable {
    // Represents an ingredient that can be processed
    private IngredientState state;

    private double cuttingProgress;
    private double cookingDuration;

    private final double MAX_CUTTING_PROGRESS = 100.0;
    private final double TIME_TO_COOK = 12.0;
    private final double TIME_TO_BURN = 24.0;

    public Ingredient(String name) {
        super(name);
        this.state = IngredientState.RAW;
        this.cuttingProgress = 0.0;
        this.cookingDuration = 0.0;
    }

    public IngredientState getState() {
        return state;
    }

    public void setState(IngredientState state) {
        this.state = state;
    }

    // Implement fungsi pada prepareable
    public boolean canBeChopped() {
        return this.state == IngredientState.RAW;
    }

    public boolean canBeCooked() {
        return this.state == IngredientState.RAW || this.state == IngredientState.CHOPPED
                || this.state == IngredientState.COOKED;
    }

    public boolean canBePlacedOnPlate() {
        return true;
    }

    public void chop() {
        if (canBeChopped()) {
            this.state = IngredientState.CHOPPED;
        }
    }

    public void cook() {
        if (this.state != IngredientState.COOKED && this.state != IngredientState.BURNED) {
            this.state = IngredientState.COOKED;
        }
    }

    public void burn() {
        if (this.state != IngredientState.BURNED) {
            this.state = IngredientState.BURNED;
        }
    }

    public void addCuttingProgress(double amount) {
        if (canBeChopped()) {
            this.cuttingProgress += amount;
            if (this.cuttingProgress >= MAX_CUTTING_PROGRESS) {
                this.cuttingProgress = MAX_CUTTING_PROGRESS;
                chop();
            }
        }
    }

    public void addCookingDuration(double time) {
        if (canBeCooked()) {
            this.cookingDuration += time;
            if (this.cookingDuration >= TIME_TO_BURN) {
                burn();
            } else if (this.cookingDuration >= TIME_TO_COOK) {
                cook();
            }
        }
    }

    public double getCuttingProgress() {
        return this.cuttingProgress;
    }

    public double getCookingDuration() {
        return this.cookingDuration;
    }
}
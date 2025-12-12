package org.example.model.items;

import java.util.ArrayList;
import java.util.List;

public class Dish extends Item implements org.example.model.interfaces.Preparable {
    // Represents a complete dish comprised of ingredients
    private List<Ingredient> components;
    private double cookingDuration;
    private boolean isCooked = false;
    private boolean isBurned = false;

    private final double TIME_TO_COOK = 10.0; // 10 seconds to cook pizza
    private final double TIME_TO_BURN = 20.0;

    public Dish(String name) {
        super(name);
        this.components = new ArrayList<>();
        this.cookingDuration = 0;
    }

    public void addComponent(Ingredient ingredient) {
        components.add(ingredient);
    }

    public List<Ingredient> getComponents() {
        return this.components;
    }

    public void setComponents(List<Ingredient> components) {
        this.components = components;
    }

    // --- Preparable Implementation ---

    @Override
    public boolean canBeChopped() {
        return false;
    }

    @Override
    public boolean canBeCooked() {
        return !isCooked && !isBurned && getName().contains("Uncooked");
    }

    @Override
    public boolean canBePlacedOnPlate() {
        return true;
    }

    @Override
    public void chop() {
        // Cannot chop a dish
    }

    @Override
    public void cook() {
        if (!isCooked && !isBurned) {
            isCooked = true;
            if (getName().startsWith("Uncooked ")) {
                setName(getName().replace("Uncooked ", ""));
            }
        }
    }

    public void burn() {
        if (!isBurned) {
            isBurned = true;
            isCooked = false; // It's burnt now
            setName("Burnt " + getName());
        }
    }

    @Override
    public void addCookingDuration(double deltaTime) {
        if (canBeCooked() || isCooked) { // Continue heating even if cooked (to burn)
            this.cookingDuration += deltaTime;

            if (this.cookingDuration >= TIME_TO_BURN) {
                burn();
            } else if (this.cookingDuration >= TIME_TO_COOK && !isCooked) {
                cook();
            }
        }
    }

    @Override
    public double getCookingDuration() {
        return cookingDuration;
    }
}

package org.example.model.order;

import org.example.model.items.Dish;

public class Order {
    private Dish dish;
    private int reward = 120;
    private int penalty = -50;
    private double remainingTime;
    private int time;

    public Order(Dish dish, int time) {
        this.dish = dish;
        this.time = time;
        this.remainingTime = time;
    }

    public void update(double deltaTime) {
        remainingTime -= deltaTime;
    }

    public boolean isExpired() {
        return remainingTime <= 0;
    }

    public String getName() {
        return dish.getName();
    }

    public int getPenalty() {
        return penalty;
    }

    public int getReward() {
        return reward;
    }

    public int getTime() {
        return time;
    }

    public double getRemainingTime() {
        return remainingTime;
    }

    public Dish getDish() {
        return dish;
    }

    public static final double ORDER_DURATION = 60.0;

    public org.example.model.recipe.Recipe getRecipe() {
        // Simple logic: remove "Pizza " from dish name to get recipe name
        String dishName = dish.getName();
        String recipeName = dishName.replace("Pizza ", "");

        for (org.example.model.recipe.Recipe r : org.example.model.recipe.RecipeManager.getInstance().getRecipes()) {
            if (r.getName().equalsIgnoreCase(recipeName)) {
                return r;
            }
        }
        return null;
    }
}
package org.example.model.items;

import org.example.model.interfaces.CookingDevice;
import org.example.model.interfaces.Preparable;
import org.example.model.items.Ingredient;

public class Oven extends KitchenUtensils implements CookingDevice {

    public Oven() {
        super("Oven");
    }

    // --- IMPLEMENTASI INTERFACE CookingDevice ---

    @Override
    public boolean isPortable() {
        return false;
    }

    @Override
    public int capacity() {
        return 1;
    }

    @Override
    public boolean canAccept(Preparable item) {
        if (item instanceof Dish) {
            String name = ((Dish) item).getName();
            return name.contains("Pizza") || name.contains("Uncooked");
        }
        // Allow raw ingredients if needed, usage specific. For now focusing on Dish.
        return item instanceof Ingredient;
    }

    @Override
    public void addIngredient(Preparable item) {
        if (canAccept(item) && this.contents.size() < capacity()) {
            super.addItem(item);
            System.out.println(((Item) item).getName() + " dimasukkan ke dalam Oven.");
        } else {
            System.out.println("Oven menolak! (Mungkin penuh atau bukan Pizza)");
        }
    }

    @Override
    public void update(double deltaTime) {
        for (Preparable item : this.contents) {
            item.addCookingDuration(deltaTime);
        }
    }
}
package org.example.model.stations;

import org.example.model.entities.Chef;
import org.example.model.items.Ingredient;
import org.example.model.map.Position;

public class IngredientStorage extends Station {

    private String ingredientName;

    public IngredientStorage(String name, Position position, String ingredientName) {
        super(name, position);
        this.ingredientName = ingredientName;
    }

    public String getIngredientName() { return ingredientName; }

    // GUNAKAN TOMBOL F (Pickup/Drop) UNTUK INI
    @Override
    public void interact(Chef chef) {
        if (!chef.isHoldingItem()) {
            chef.setInventory(new Ingredient(this.ingredientName));
            System.out.println("Mengambil " + ingredientName + " (Tombol F)");
        }
    }

    // GUNAKAN TOMBOL V (Action) UNTUK INI
    public void action(Chef chef) {
        // Peti bahan tidak bisa "dikerjai", jadi kosongkan saja
        System.out.println("Tidak ada aksi yang bisa dilakukan di peti ini.");
    }
}
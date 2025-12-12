package org.example.model.stations; // Pindah package ke stations!

import org.example.model.items.Item;
import org.example.model.items.Pizza;
import org.example.model.enums.IngredientState;
import org.example.model.map.Position;

// OVEN ADALAH STATION, BUKAN UTENSIL
public class Oven extends CookingStation {

    public Oven(String name, Position position) {
        // Set nama default jadi "Oven"
        super(name, position);
    }

    // --- ATURAN SPESIFIK OVEN ---
    @Override
    protected boolean shouldAcceptItem(Item item) {
        // Validasi 1: Harus Pizza
        if (item instanceof Pizza) {
            Pizza pizza = (Pizza) item;

            // Validasi 2: Harus Mentah (RAW)
            // Biar player gak masukin pizza gosong/matang balik ke oven
            if (pizza.getState() == IngredientState.RAW) {
                return true; // DITERIMA
            } else {
                System.out.println("Hanya Pizza MENTAH yang boleh dipanggang!");
                return false;
            }
        }

        // Item bukan Pizza
        System.out.println("Oven hanya menerima Pizza!");
        return false;
    }
}
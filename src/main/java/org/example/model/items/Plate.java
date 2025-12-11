package org.example.model.items;

import org.example.model.interfaces.Preparable;
import java.util.ArrayList; // Support List punya teman
import java.util.List;

// 1. Tetap extends KitchenUtensil (Supaya aman di TrashStation)
public class Plate extends KitchenUtensils {

    private boolean isClean;

    public Plate() {
        super("Plate");
        this.isClean = true;
    }

    // 2. VALIDASI KEBERSIHAN (Penting!)
    @Override
    public void addItem(Preparable item) {
        if (this.isClean) {
            super.addItem(item); // Simpan ke Set (Parent)
        } else {
            System.out.println("Gagal! Piring kotor.");
        }
    }

    // 3. Support Method Teman (Jembatan/Adapter)
    // Jika kode teman lain memanggil addIngredients, kita alihkan ke addItem
    public void addIngredients(Ingredient ingredient) {
        this.addItem(ingredient);
    }

    // 4. Method Cuci (Support kedua nama biar kompatibel)
    public void wash() {
        this.cleanPlate();
    }

    public void cleanPlate() {
        this.isClean = true;
        this.emptyContents();
    }

    // 5. Konversi ke Dish (Fitur Teman, tapi diperbaiki)
    // Ini opsional, bisa dipakai jika OrderManager butuh objek Dish
    public Dish createDish(String name) {
        Dish d = new Dish(name);
        // Konversi Set ke List agar sesuai kemauan teman
        List<Ingredient> ingredients = new ArrayList<>();
        for (Preparable p : this.getContents()) {
            if (p instanceof Ingredient) {
                ingredients.add((Ingredient) p);
            }
        }
        d.setComponents(ingredients);

        // Jangan dikotorin dulu disini, biarkan ServingCounter yang mengotorinya
        // this.isClean = false;

        return d;
    }

    public void markAsDirty() {
        this.isClean = false;
        this.emptyContents();
    }

    public boolean isClean() {
        return isClean;
    }
}
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

        // Kita cek dari namanya (Simple Logic)
        if (item instanceof Item) {
            String name = ((Item) item).getName();
            // Terima jika namanya mengandung "Pizza"
            // Dan pastikan belum gosong
            return name.contains("Pizza");
        }
        return false;
    }

    @Override
    public void addIngredient(Preparable ingredient) {
        if (canAccept(ingredient) && this.contents.size() < capacity()) {
            super.addItem(ingredient);
            System.out.println(ingredient + " dimasukkan ke dalam Oven.");
        } else {
            System.out.println("Oven menolak! (Mungkin penuh atau bukan Pizza)");
        }
    }

    @Override
    public void startCooking() {
        // Oven memanaskan Pizza di dalamnya
        for (Preparable item : this.contents) {
            // Panggil logika "Pintar" yang sudah kita buat di Ingredient
            // Asumsi: Oven cukup cepat, kita beri waktu 1.0 detik per tick
            item.cook();
            // ATAU jika Pizza menggunakan sistem durasi seperti Ingredient:
            // ((Ingredient) item).addCookingDuration(1.0);
        }
    }
}
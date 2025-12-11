package org.example.model.items;

import org.example.model.interfaces.Preparable;

// Pastikan nama class parent sesuai dengan file Anda (KitchenUtensil vs KitchenUtensils)
public class Plate extends KitchenUtensils {
    private boolean isClean;

    public Plate() {
        super("Plate");
        this.isClean = true; // Default bersih
    }

    public boolean isClean() {
        return isClean;
    }

    public void setClean(boolean clean) {
        this.isClean = clean;
    }

    // --- TAMBAHAN PENTING SESUAI SPEK ---

    // Menimpa method addItem milik Parent
    // Tujuannya: Mencegah pemain menaruh makanan di piring kotor
    @Override
    public void addItem(Preparable item) {
        if (this.isClean) {
            super.addItem(item); // Jika bersih, boleh isi (panggil logic Parent)
        } else {
            System.out.println("Gagal! Piring ini kotor.");
        }
    }

    // Helper untuk WashingStation nanti
    public void cleanPlate() {
        this.isClean = true;
        this.emptyContents(); // Kosongkan sisa kotoran (jika ada)
    }

    // Helper untuk ServingStation/Customer nanti
    public void markAsDirty() {
        this.isClean = false;
        this.emptyContents(); // Makanan habis, piring jadi kotor
    }
}

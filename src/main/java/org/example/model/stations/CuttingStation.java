package org.example.model.stations;

import org.example.model.entities.Chef;
import org.example.model.enums.ChefState;
import org.example.model.items.Ingredient;
import org.example.model.enums.IngredientState;
import org.example.model.map.Position;

public class CuttingStation extends Station {

    private Chef processingChef;
    private static final double REQUIRED_TIME = 3.0; // SPEK: 3 Detik

    public CuttingStation(String name, Position position) {
        super(name, position);
    }

    // KEY F (PICKUP/DROP) - Dulu V
    @Override
    public void interact(Chef chef) {
        // 1. Jika sedang memotong, batalkan dulu jangan langsung ambil
        if (processingChef != null) {
            cancelProcessing();
            System.out.println("Proses potong dibatalkan.");
            return;
        }

        // 2. Logika standar taruh/ambil
        // Jika Anda sudah punya method 'interactDefault' di Station.java,
        // Anda bisa pakai: super.interactDefault(chef);
        // Tapi kode manual di bawah ini juga BENAR:
        if (this.isEmpty() && chef.isHoldingItem()) {
            this.addItem(chef.dropItem());
        } else if (!this.isEmpty() && !chef.isHoldingItem()) {
            chef.setInventory(this.removeItem());
        }
    }


    // Tambahkan ini biar aman
    public void action(Chef chef) {
        if (!this.isEmpty() && this.getItem() instanceof Ingredient) {
            Ingredient ing = (Ingredient) this.getItem();

            // SPEK: Input Ingredient dengan state RAW
            if (ing.getState() == IngredientState.RAW) {

                // Mulai Memotong
                if (this.processingChef == null) {
                    this.processingChef = chef;
                    chef.setState(ChefState.CUTTING); // Kunci pergerakan Chef
                    System.out.println("Mulai memotong (3 detik)...");
                }
                // Stop kalau ditekan lagi oleh chef yang sama
                else if (this.processingChef == chef) {
                    cancelProcessing();
                }
            } else {
                System.out.println("Gagal: Bahan bukan RAW atau sudah dipotong.");
            }
        }
    }

    // UPDATE LOOP (Dipanggil GameController)
    public void update(double deltaTime) {
        // Hanya update jika ada chef yang sedang kerja
        if (processingChef != null && !this.isEmpty()) {

            // Safety check: pastikan item masih Ingredient
            if (this.getItem() instanceof Ingredient) {
                Ingredient ing = (Ingredient) this.getItem();

                // Hitung persentase progress
                double progressIncrement = (deltaTime / REQUIRED_TIME) * 100.0;
                ing.addCuttingProgress(progressIncrement);

                // Cek apakah ingredient sudah mengubah dirinya jadi CHOPPED
                if (ing.getState() == IngredientState.CHOPPED) {
                    System.out.println("Selesai memotong!");
                    cancelProcessing(); // Lepaskan Chef
                }
            }
        }
    }

    public void cancelProcessing() {
        if (processingChef != null) {
            processingChef.setState(ChefState.IDLE); // Bebaskan Chef
            this.processingChef = null;
            // Progress tidak di-reset (sesuai spek)
        }
    }
}
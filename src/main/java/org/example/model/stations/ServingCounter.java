package org.example.model.stations;

import org.example.model.entities.Chef;
import org.example.model.items.Plate;
import org.example.model.map.Position;

public class ServingCounter extends Station {

    private Plate servedPlate;

    public ServingCounter(String name, Position position) {
        super(name, position);
    }

    @Override
    public void interact(Chef chef) {
        // Cek apakah Chef membawa item
        if (chef.isHoldingItem()) {
            // Cek apakah item tersebut adalah Piring
            if (chef.getInventory() instanceof Plate) {
                Plate piring = (Plate) chef.getInventory();

                // Cek apakah piring berisi makanan (tidak kosong)
                if (!piring.getContents().isEmpty()) {
                    // Logic baru: Simpan piring di ServingCounter
                    // Agar GameController bisa mengambilnya via getServedPlate()
                    this.servedPlate = piring;

                    // Hapus dari inventory chef
                    chef.setInventory(null);

                    // Opsional: Langsung panggil logika process (jika perlu internal logic)
                    processServing(piring);
                }
            }
        }
    }

    private void processServing(Plate piring) {
        // Logic internal jika diperlukan, misalnya notifikasi visual atau sound
        System.out.println("Plate placed on Serving Counter!");
    }

    // --- Added Methods for GameController ---

    public Plate getServedPlate() {
        return servedPlate;
    }

    public void clearServedPlate() {
        this.servedPlate = null;
    }
}
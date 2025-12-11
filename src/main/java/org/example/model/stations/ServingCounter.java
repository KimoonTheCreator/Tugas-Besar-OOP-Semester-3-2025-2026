package org.example.model.stations;

import org.example.model.entities.Chef;
import org.example.model.interfaces.Preparable;
import org.example.model.items.Plate;
import org.example.model.map.Position;

import java.util.Set;

public class ServingCounter extends Station {

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
                    processServing(piring);
                }
            }
        }
    }

    private void processServing(Plate piring) {
        // Ambil isi makanan untuk divalidasi oleh Logic/Controller nanti
        Set<Preparable> dishServed = piring.getContents();

        // Di sini nantinya Controller akan mengambil data 'dishServed'
        // untuk dicocokkan dengan OrderManager.

        // Ubah state piring menjadi kotor dan kosongkan isinya
        piring.markAsDirty();
    }
}
package org.example.model.stations;

import org.example.model.entities.Chef;
import org.example.model.items.Plate;
import org.example.model.map.Position;

public class TrashStation extends Station {
    public TrashStation(String name, Position position) {
        super(name, position);
    }

    @Override
    public void interact(Chef chef) {
        if (chef.isHoldingItem()) {
            // SPEK: Jika Plate berisi makanan, buang makanannya saja (Plate jadi kotor/kosong)
            // Atau SPEK Trash Action: "Item dibuang".
            // Implementasi umum: Buang isi tangan.

            System.out.println("Membuang " + chef.getInventory().getName());
            chef.dropItem();
        }
    }
}
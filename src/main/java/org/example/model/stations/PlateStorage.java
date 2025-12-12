package org.example.model.stations;

import org.example.model.entities.Chef;
import org.example.model.items.Plate;
import org.example.model.map.Position;

public class PlateStorage extends Station {

    private java.util.Stack<Plate> storedPlates = new java.util.Stack<>();

    public PlateStorage(Position position) {
        super("Plate Storage", position);
        // Initialize with 3 clean plates
        for (int i = 0; i < 3; i++) {
            storedPlates.push(new Plate());
        }
    }

    public void addPlate(Plate plate) {
        if (plate != null) {
            storedPlates.push(plate);
        }
    }

    @Override
    public void interact(Chef chef) {
        // SKENARIO 1: MENGAMBIL PIRING (Chef Tangan Kosong)
        if (!chef.isHoldingItem()) {
            if (!storedPlates.isEmpty()) {
                chef.setInventory(storedPlates.pop());
                System.out.println("Chef mengambil piring dari tumpukan.");
            } else {
                System.out.println("Storage kosong! Tunggu piring dikembalikan.");
            }
        }

        // SKENARIO 2: MENARUH KEMBALI PIRING (Chef Bawa Piring)
        else if (chef.isHoldingItem() && chef.getInventory() instanceof Plate) {
            Plate piringDiTangan = (Plate) chef.getInventory();
            chef.dropItem();
            addPlate(piringDiTangan);
            System.out.println("Chef menaruh kembali piring ke storage.");
        }
    }
}
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
        if (!chef.isHoldingItem())
            return;
        if (!(chef.getInventory() instanceof Plate))
            return;

        Plate piring = (Plate) chef.getInventory();

        // Cek piring ada isinya
        if (!piring.getContents().isEmpty()) {
            this.servedPlate = piring;
            chef.setInventory(null);
            processServing(piring);
        }
    }

    private void processServing(Plate piring) {
        System.out.println("Plate placed on Serving Counter!");
    }

    public Plate getServedPlate() {
        return servedPlate;
    }

    public void clearServedPlate() {
        this.servedPlate = null;
    }
}
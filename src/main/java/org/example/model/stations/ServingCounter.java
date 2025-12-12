package org.example.model.stations;

import org.example.model.entities.Chef;
import org.example.model.interfaces.Preparable;
import org.example.model.items.Plate;
import org.example.model.map.Position;

import java.util.Set;

public class ServingCounter extends Station {

    private Plate servedPlate;

    public ServingCounter(Position position) {
        super("Serving Counter", position);
        this.servedPlate = null;
    }

    @Override
    public void interact(Chef chef) {
        // Cek apakah Chef membawa item
        if (chef.isHoldingItem()) {
            if (chef.getInventory() instanceof Plate) {
                Plate piring = (Plate) chef.getInventory();

                // Cek apakah piring berisi makanan
                if (!piring.getContents().isEmpty()) {
                    chef.dropItem(); // Chef drops it
                    this.servedPlate = piring; // Store for validation
                    // piring.markAsDirty(); // Controller will handle logic after validation
                }
            }
        }
    }

    public Plate getServedPlate() {
        return servedPlate;
    }

    public void clearServedPlate() {
        this.servedPlate = null;
    }
}
package org.example.model.stations;

import org.example.model.entities.Chef;
import org.example.model.enums.ChefState;
import org.example.model.items.Plate;
import org.example.model.map.Position;

public class WashingStation extends Station {

    private Chef currentChef;
    private double progress;
    private static final double WASHING_DURATION = 3.0; // Seconds

    private boolean isProcessRunning;

    public WashingStation(Position position) {
        super("Washing Station", position);
        this.isProcessRunning = false;
        this.progress = 0;
    }

    @Override
    public void action(Chef chef) {
        if (!this.isEmpty() && this.getItem() instanceof Plate) {
            Plate piringDiWastafel = (Plate) this.getItem();

            if (!piringDiWastafel.isClean() && !isProcessRunning) {
                startWashingProcess(chef);
            }
        }
    }

    @Override
    public void interact(Chef chef) {
        // SKENARIO 1: MENARUH PIRING KOTOR
        if (chef.isHoldingItem() && this.isEmpty()) {
            if (chef.getInventory() instanceof Plate) {
                // Allow placing any plate? User says "Input: (Beberapa) Plate kotor".
                // But usually allows placing.
                this.addItem(chef.dropItem());
            }
        }
        // SKENARIO 2: MENGAMBIL PIRING (BERSIH)
        else if (!chef.isHoldingItem() && !this.isEmpty()) {
            // If currently washing, maybe stop?
            if (isProcessRunning) {
                stopWashing();
            }
            chef.pickUpItem(this.takeItem());
        }
    }

    private void startWashingProcess(Chef chef) {
        isProcessRunning = true;
        currentChef = chef;
        progress = 0;
        chef.setState(ChefState.INTERACT);
    }

    private void stopWashing() {
        isProcessRunning = false;
        if (currentChef != null) {
            currentChef.setIdle();
            currentChef = null;
        }
        progress = 0;
    }

    @Override
    public void update(double deltaTime) {
        if (isProcessRunning && currentChef != null && this.getItem() instanceof Plate) {
            Plate p = (Plate) this.getItem();

            if (currentChef.getState() != ChefState.INTERACT) {
                stopWashing();
                return;
            }

            // Progress Washing
            progress += deltaTime;
            if (progress >= WASHING_DURATION) {
                p.cleanPlate();
                stopWashing();
            }
        }
    }
}
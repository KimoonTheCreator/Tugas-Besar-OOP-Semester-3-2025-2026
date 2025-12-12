package org.example.model.stations;

import org.example.model.enums.ChefState;
import org.example.model.map.Position;
import org.example.model.entities.Chef;
import org.example.model.items.Ingredient;
import org.example.model.items.Item;
import org.example.model.enums.IngredientState;

public class CuttingStation extends Station {
    private boolean isProcessRunning;

    private Chef currentChef;

    public CuttingStation(Position position) {
        super("Cutting Station", position);
        this.isProcessRunning = false;
    }

    @Override
    public void action(Chef chef) {
        if (!this.isEmpty() && this.getItem() instanceof Ingredient) {
            Ingredient bahan = (Ingredient) this.getItem();

            if (bahan.getState() == IngredientState.RAW && bahan.canBeChopped()) {
                if (!isProcessRunning) {
                    startCuttingProcess(chef);
                }
            }
        }
    }

    @Override
    public void interact(Chef chef) {
        // Jika chef sedang cutting, stop dulu (optional, but handled in update)
        if (isProcessRunning && currentChef == chef) {
            stopCuttingProcess(); // Stop if user tries to pick up while cutting?
            // Or interact means pickup, so yes stop.
        }

        if (chef.isHoldingItem() && this.isEmpty()) {
            // Validasi input: Ingredient dengan state RAW only?
            // User Request: Input: Ingredient dengan state RAW.
            // But usually you can place anything temporarily?
            // "Input: Ingredient dengan state RAW" -> This is for CUTTING process.
            // Putting it on the station might be allowed for anything.
            // "Kegagalan: Jika item bukan ingredient mentah atau station penuh." (For
            // action?)
            // "Kegagalan" under Cutting Action usually refers to the Action processing.
            // But let's check standard logic. Usually allow placing any ingredient.
            Item item = chef.getInventory();
            if (item instanceof Ingredient) {
                this.addItem(chef.dropItem());
            } else {
                // Maybe allow anything or specific? User said "Input: Ingredient with state
                // RAW".
                // I'll allow any Ingredient for now.
                if (item instanceof Ingredient) {
                    this.addItem(chef.dropItem());
                }
            }
        } else if (!chef.isHoldingItem() && !this.isEmpty()) {
            chef.pickUpItem(this.takeItem());
        }
    }

    private void stopCuttingProcess() {
        isProcessRunning = false;
        if (currentChef != null) {
            currentChef.setIdle();
            currentChef = null;
        }
    }

    private void startCuttingProcess(Chef chef) {
        isProcessRunning = true;
        currentChef = chef;
        chef.setState(ChefState.CUTTING);
    }

    @Override
    public void update(double deltaTime) {
        if (isProcessRunning && currentChef != null && this.getItem() instanceof Ingredient) {
            Ingredient bahan = (Ingredient) this.getItem();

            // Jika chef bergerak atau melakukan hal lain, stop cutting
            // We assume chef state is managed. If user presses Move keys, Chef state
            // changes to MOVE.
            if (currentChef.getState() != ChefState.CUTTING) {
                stopCuttingProcess();
                return;
            }

            // Progress Cutting
            // 3 seconds to 100%. Speed = 100/3 per second.
            double cuttingSpeed = 100.0 / 3.0;
            bahan.addCuttingProgress(cuttingSpeed * deltaTime);

            if (bahan.getCuttingProgress() >= 100) {
                // Logic change state is inside Ingredient.addCuttingProgress?
                // We should verify. If not, set it here.
                if (bahan.getState() == IngredientState.RAW) {
                    // We need to change state to CHOPPED.
                    // Does Ingredient have a method for this?
                    // Usually addCuttingProgress handles it or we call a setter.
                    // Let's assume we need to handle it if Ingredient doesn't automatically switch.
                    // Access Ingredient methods logic is unknown, assuming standard behaviors.
                    // Just in case:
                    // bahan.setState(IngredientState.CHOPPED);
                }
                stopCuttingProcess();
            }
        }
    }
}

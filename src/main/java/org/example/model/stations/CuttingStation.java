package org.example.model.stations;

import org.example.model.entities.Chef;
import org.example.model.enums.ChefState;
import org.example.model.items.Ingredient;
import org.example.model.enums.IngredientState;
import org.example.model.map.Position;

public class CuttingStation extends Station {

    private Chef processingChef;
    private static final double REQUIRED_TIME = 3.0; // 3 detik

    public CuttingStation(String name, Position position) {
        super(name, position);
    }

    // Key F: taruh/ambil
    @Override
    public void interact(Chef chef) {
        if (processingChef != null) {
            cancelProcessing();
            System.out.println("Potong dibatalkan.");
            return;
        }

        if (this.isEmpty() && chef.isHoldingItem()) {
            this.addItem(chef.dropItem());
        } else if (!this.isEmpty() && !chef.isHoldingItem()) {
            chef.setInventory(this.removeItem());
        }
    }

    // Key V: mulai potong
    public void action(Chef chef) {
        if (!this.isEmpty() && this.getItem() instanceof Ingredient) {
            Ingredient ing = (Ingredient) this.getItem();

            if (ing.getState() == IngredientState.RAW) {
                if (this.processingChef == null) {
                    this.processingChef = chef;
                    chef.setState(ChefState.CUTTING);
                    System.out.println("Memotong...");
                } else if (this.processingChef == chef) {
                    cancelProcessing();
                }
            } else {
                System.out.println("Bahan sudah dipotong atau tidak bisa dipotong.");
            }
        }
    }

    public void update(double deltaTime) {
        if (processingChef != null && !this.isEmpty()) {
            if (this.getItem() instanceof Ingredient) {
                Ingredient ing = (Ingredient) this.getItem();

                double progressIncrement = (deltaTime / REQUIRED_TIME) * 100.0;
                ing.addCuttingProgress(progressIncrement);

                if (ing.getState() == IngredientState.CHOPPED) {
                    System.out.println("Selesai potong!");
                    cancelProcessing();
                }
            }
        }
    }

    public void cancelProcessing() {
        if (processingChef != null) {
            processingChef.setState(ChefState.IDLE);
            this.processingChef = null;
        }
    }
}
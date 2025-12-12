package org.example.model.stations;

import org.example.model.entities.Chef;
import org.example.model.enums.ChefState;
import org.example.model.items.Plate;
import org.example.model.map.Position;

public class WashingStation extends Station {

    private Chef washerChef;
    private double washTimer = 0;
    private static final double REQUIRED_TIME = 3.0; // SPEK: 3 Detik

    public WashingStation(String name, Position position) {
        super(name, position);
    }

    // KEY V: TARUH / AMBIL
    @Override
    public void interact(Chef chef) {
        if (washerChef != null) {
            cancelWashing(); // Batal kalau diganggu
            return;
        }

        // Cek Piring Kotor
        if (isEmpty() && chef.isHoldingItem() && chef.getInventory() instanceof Plate) {
            Plate p = (Plate) chef.getInventory();
            if (!p.isClean()) {
               if (this.isEmpty() && chef.isHoldingItem()) {
                    this.addItem(chef.dropItem());
               } else if (!this.isEmpty() && !chef.isHoldingItem()) {
                    chef.setInventory(this.removeItem());
               }
            } else {
                System.out.println("Piring sudah bersih!");
            }
        }else if (this.isEmpty() && chef.isHoldingItem()) {
            this.addItem(chef.dropItem());
        } else if (!this.isEmpty() && !chef.isHoldingItem()) {
            chef.setInventory(this.removeItem());
        }
    }

    // KEY C: MENCUCI (TRIGGER)
    public void action(Chef chef) {
        if (!isEmpty() && getItem() instanceof Plate) {
            Plate p = (Plate) getItem();
            if (!p.isClean()) {
                if (washerChef == null) {
                    this.washerChef = chef;
                    chef.setState(ChefState.INTERACT); // Chef Busy
                    System.out.println("Mulai mencuci (3 detik)...");
                } else if (washerChef == chef) {
                    cancelWashing();
                }
            }
        }
    }

    public void update(double deltaTime) {
        if (washerChef != null && !isEmpty()) {
            washTimer += deltaTime;

            if (washTimer >= REQUIRED_TIME) {
                Plate p = (Plate) getItem();
                p.wash(); // Jadi Bersih
                System.out.println("Piring BERSIH!");
                cancelWashing();
            }
        }
    }

    public void cancelWashing() {
        if (washerChef != null) {
            washerChef.setState(ChefState.IDLE);
            washerChef = null;
            washTimer = 0; // Reset timer cuci (biasanya cuci ulang dari awal)
        }
    }
}
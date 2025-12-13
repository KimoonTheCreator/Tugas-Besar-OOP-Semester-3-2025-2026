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

        // 1. Jika Station KOSONG: Cuma terima Piring Kotor
        if (this.isEmpty()) {
            if (chef.isHoldingItem() && chef.getInventory() instanceof Plate) {
                Plate p = (Plate) chef.getInventory();
                if (!p.isClean()) {
                    this.addItem(chef.dropItem());
                } else {
                    System.out.println("Hanya piring kotor yang bisa dicuci!");
                }
            } else if (chef.isHoldingItem()) {
                System.out.println("Washing Station hanya menerima Piring Kotor!");
            }
        } 
        // 2. Jika Station ADA ITEM (Pasti Piring Kotor/Bersih): Ambil
        else if (!this.isEmpty() && !chef.isHoldingItem()) {
            chef.setInventory(this.removeItem());
            cancelWashing(); // Reset state jika diambil pas lagi dicuci
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
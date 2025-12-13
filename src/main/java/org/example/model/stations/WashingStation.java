package org.example.model.stations;

import org.example.model.entities.Chef;
import org.example.model.enums.ChefState;
import org.example.model.items.Plate;
import org.example.model.map.Position;

public class WashingStation extends Station {

    private Chef washerChef;
    private double washTimer = 0;
    private static final double REQUIRED_TIME = 3.0; // 3 detik

    public WashingStation(String name, Position position) {
        super(name, position);
    }

    // Key F: taruh/ambil
    @Override
    public void interact(Chef chef) {
        if (washerChef != null) {
            cancelWashing();
            return;
        }

        if (this.isEmpty()) {
            // Terima piring kotor saja
            if (chef.isHoldingItem() && chef.getInventory() instanceof Plate) {
                Plate p = (Plate) chef.getInventory();
                if (!p.isClean()) {
                    this.addItem(chef.dropItem());
                } else {
                    System.out.println("Hanya piring kotor yang bisa dicuci!");
                }
            }
        } else if (!chef.isHoldingItem()) {
            chef.setInventory(this.removeItem());
            cancelWashing();
        }
    }

    // Key V: mulai cuci
    public void action(Chef chef) {
        if (!isEmpty() && getItem() instanceof Plate) {
            Plate p = (Plate) getItem();
            if (!p.isClean()) {
                if (washerChef == null) {
                    this.washerChef = chef;
                    chef.setState(ChefState.INTERACT);
                    System.out.println("Mencuci...");
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
                p.wash();
                System.out.println("Piring bersih!");
                cancelWashing();
            }
        }
    }

    public void cancelWashing() {
        if (washerChef != null) {
            washerChef.setState(ChefState.IDLE);
            washerChef = null;
            washTimer = 0;
        }
    }
}
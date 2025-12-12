package org.example.model.stations;

import org.example.model.entities.Chef;
import org.example.model.interfaces.CookingDevice;
import org.example.model.interfaces.Preparable;
import org.example.model.items.Item;
import org.example.model.map.Position;

public class CookingStation extends Station {

    public CookingStation(Position position) {
        super("Cooking Station", position);
    }

    @Override
    public void interact(Chef chef) {
        // Drop logic:
        // 1. If holding Cooking Device (Pan/Pot) and Station Empty -> Place Device.
        // 2. If holding Ingredient and Station has Device -> Put Ingredient into
        // Device.
        // 3. If Empty Hand and Station has Device -> Pick up Device.

        Item chefItem = chef.getInventory();
        Item stationItem = this.getItem();

        // 1. Place Device
        if (chefItem instanceof CookingDevice && this.isEmpty()) {
            this.addItem(chef.dropItem());
        }
        // 2. Put Ingredient (Preparable)
        else if (chefItem instanceof Preparable && stationItem instanceof CookingDevice) {
            CookingDevice device = (CookingDevice) stationItem;
            Preparable bahan = (Preparable) chefItem;

            if (device.canAccept(bahan)) {
                device.addIngredient(bahan);
                chef.dropItem();
            }
        }
        // 3. Pick up Device
        else if (!chef.isHoldingItem() && !this.isEmpty()) {
            // Check portable
            if (stationItem instanceof CookingDevice) {
                if (!((CookingDevice) stationItem).isPortable()) {
                    return; // Cannot pick up Oven
                }
            }
            chef.pickUpItem(this.takeItem());
        }
    }

    @Override
    public void update(double deltaTime) {
        if (!this.isEmpty() && this.getItem() instanceof CookingDevice) {
            CookingDevice device = (CookingDevice) this.getItem();
            device.update(deltaTime);
        }
    }
}
package org.example.model.stations;

import org.example.model.entities.Chef;
import org.example.model.items.Item;
import org.example.model.items.KitchenUtensils;
import org.example.model.map.Position;

public class TrashStation extends Station {

    public TrashStation(String name, Position position) {
        super(name, position);
    }

    @Override
    public void interact(Chef chef) {
        // Hanya berinteraksi jika Chef membawa sesuatu
        if (chef.isHoldingItem()) {
            Item itemInHand = chef.getInventory();

            // SKENARIO 1: Chef membawa Wadah (Piring, Panci, Wajan, Oven)
            // Logika: Jangan buang pancinya, tapi buang makanan di dalamnya.
            if (itemInHand instanceof KitchenUtensils) {
                ((KitchenUtensils) itemInHand).emptyContents();
            }

            // SKENARIO 2: Chef membawa Bahan Makanan (Ingredient/Item biasa)
            // Logika: Hapus item dari tangan Chef (Musnahkan).
            else {
                chef.setInventory(null);
            }
        }
    }
}
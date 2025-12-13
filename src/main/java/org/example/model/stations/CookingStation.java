package org.example.model.stations;

import org.example.model.entities.Chef;
import org.example.model.items.Item;
import org.example.model.items.Pizza;
import org.example.model.map.Position;

// Base class untuk alat masak (Oven, Stove, dll)
public abstract class CookingStation extends Station {

    public CookingStation(String name, Position position) {
        super(name, position);
    }

    // Taruh atau ambil item (Key F)
    @Override
    public void interact(Chef chef) {
        // Chef taruh item
        if (this.isEmpty() && chef.isHoldingItem()) {
            Item itemOnHand = chef.getInventory();
            if (shouldAcceptItem(itemOnHand)) {
                this.addItem(chef.dropItem());
                System.out.println(itemOnHand.getName() + " masuk ke " + this.getName());
            } else {
                System.out.println(this.getName() + " menolak item ini!");
            }
        }
        // Chef ambil item
        else if (!this.isEmpty() && !chef.isHoldingItem()) {
            // Ambil barang dari station, kasih ke chef
            Item item = this.removeItem();
            chef.setInventory(item);
            System.out.println("Chef mengambil " + item.getName());
        }
    }

    // Action khusus (Key V) - default kosong, override kalau perlu
    @Override
    public void action(Chef chef) {
    }

    // Update tiap frame - proses masak otomatis
    public void update(double deltaTime) {
        if (!this.isEmpty()) {
            Item item = this.getItem();
            if (item instanceof Pizza) {
                ((Pizza) item).addCookingDuration(deltaTime);
            }
        }
    }

    // Aturan item yang diterima - diimplementasi di subclass (Oven, Stove, dll)
    protected abstract boolean shouldAcceptItem(Item item);
}
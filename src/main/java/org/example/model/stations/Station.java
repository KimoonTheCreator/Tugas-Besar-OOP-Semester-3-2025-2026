package org.example.model.stations;

import org.example.model.entities.Chef;
import org.example.model.entities.GameObject;
import org.example.model.items.Item;
import org.example.model.map.Position;

public abstract class Station extends GameObject {

    protected String name;
    protected Item item;

    public Station(String name, Position position) {
        super(position);
        this.name = name;
        this.item = null;
    }

    // Wajib diimplementasi subclass
    public abstract void interact(Chef chef);

    // Default kosong, override kalau perlu
    public void action(Chef chef) {
    }

    // Helper: tukar item antara chef dan station
    public void interactDefault(Chef chef) {
        if (this.isEmpty() && chef.isHoldingItem()) {
            this.addItem(chef.dropItem());
        } else if (!this.isEmpty() && !chef.isHoldingItem()) {
            chef.setInventory(this.removeItem());
        }
    }

    // Item management
    public boolean placeItem(Item item) {
        if (isEmpty() && item != null) {
            this.item = item;
            return true;
        }
        return false;
    }

    public void addItem(Item item) {
        this.placeItem(item);
    }

    public Item takeItem() {
        Item temp = this.item;
        this.item = null;
        return temp;
    }

    public Item removeItem() {
        return this.takeItem();
    }

    public Item getItem() {
        return this.item;
    }

    public Item getCurrentItem() {
        return this.item;
    }

    public boolean isEmpty() {
        return this.item == null;
    }

    public String getName() {
        return name;
    }
}
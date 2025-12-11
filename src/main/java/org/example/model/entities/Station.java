package org.example.model.entities;

import org.example.model.enums.StationState;
import org.example.model.items.Item;
import org.example.model.map.Position;

/**
 * Kelas abstrak Station sebagai parent untuk semua jenis stasiun kerja
 * Menggunakan konsep polymorphism untuk interaksi yang berbeda-beda
 */
public abstract class Station extends GameObject {
    protected String id;
    protected String name;
    protected StationState state;
    protected Item currentItem;

    public Station(String id, String name, Position position) {
        super(position);
        this.id = id;
        this.name = name;
        this.state = StationState.EMPTY;
        this.currentItem = null;
    }

    // Method abstract untuk interaksi (polymorphism)
    public abstract void interact(Chef chef);

    // Cek apakah station kosong
    public boolean isEmpty() {
        return this.state == StationState.EMPTY && this.currentItem == null;
    }

    // Cek apakah sedang memproses
    public boolean isProcessing() {
        return this.state == StationState.PROCESSING;
    }

    // Cek apakah sudah selesai
    public boolean isFinished() {
        return this.state == StationState.FINISHED;
    }

    // Taruh item ke station
    public boolean placeItem(Item item) {
        if (isEmpty() && item != null) {
            this.currentItem = item;
            this.state = StationState.OCCUPIED;
            return true;
        }
        return false;
    }

    // Ambil item dari station
    public Item takeItem() {
        if (this.currentItem != null) {
            Item item = this.currentItem;
            this.currentItem = null;
            this.state = StationState.EMPTY;
            return item;
        }
        return null;
    }

    // Getter dan Setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public StationState getState() {
        return state;
    }

    public void setState(StationState state) {
        this.state = state;
    }

    public Item getCurrentItem() {
        return currentItem;
    }

    public void setCurrentItem(Item currentItem) {
        this.currentItem = currentItem;
    }

    @Override
    public String toString() {
        return "Station[" + name + " at " + position + "]";
    }
}

package org.example.model.stations;

import org.example.model.entities.Chef;
import org.example.model.entities.GameObject; // Pastikan GameObject ada di package entities
import org.example.model.items.Item;
import org.example.model.map.Position;

// KITA GUNAKAN extends GameObject
public abstract class Station extends GameObject {

    protected String name;
    protected Item item;

    public Station(String name, Position position) {
        super(position); // Panggil constructor GameObject
        this.name = name;
        this.item = null;
    }

    // ==========================================
    // METHOD INTERAKSI UTAMA
    // ==========================================

    // Abstract: Wajib diisi oleh anak (CuttingStation, dll)
    public abstract void interact(Chef chef);

    // Concrete: Default kosong (untuk Trash/Storage yang tidak butuh aksi kerja)
    public void action(Chef chef) {
        // Default: Tidak melakukan apa-apa
    }

    // Helper: Logika default interaksi (Tukar barang)
    // Dipakai oleh CuttingStation/WashingStation saat tombol V ditekan
    public void interactDefault(Chef chef) {
        if (this.isEmpty() && chef.isHoldingItem()) {
            this.addItem(chef.dropItem());
        } else if (!this.isEmpty() && !chef.isHoldingItem()) {
            chef.setInventory(this.removeItem());
        }
    }

    // ==========================================
    // METHOD JEMBATAN (COMPATIBILITY)
    // Agar kode Anda (addItem) dan kode teman (placeItem) sama-sama jalan
    // ==========================================

    public boolean placeItem(Item item) {
        if (isEmpty() && item != null) {
            this.item = item;
            return true;
        }
        return false;
    }

    public void addItem(Item item) { this.placeItem(item); }

    public Item takeItem() {
        Item temp = this.item;
        this.item = null;
        return temp;
    }

    public Item removeItem() { return this.takeItem(); }

    public Item getItem() { return this.item; }
    public Item getCurrentItem() { return this.item; } // Alias

    public boolean isEmpty() { return this.item == null; }
    public String getName() { return name; }

    // TIDAK PERLU Getter/Setter Position karena sudah ada di GameObject
}
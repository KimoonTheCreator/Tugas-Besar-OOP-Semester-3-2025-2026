package org.example.model.entities;

import org.example.model.items.Item;
import org.example.model.map.Position;

// 1. Tambahkan 'extends GameObject'
public abstract class St extends GameObject {

    protected String name;
    protected Item item;

    // HAPUS baris ini (karena sudah ada di GameObject):
    // protected Position position;

    public St(String name, Position position) {
        super(position); // 2. Panggil constructor parent (GameObject)
        this.name = name;
        this.item = null;
        // Hapus ini: this.position = position;
    }

    // ... (Method interact, action, addItem, removeItem TETAP SAMA) ...

    public abstract void interact(Chef chef);

    public void action(Chef chef) {
        // Default: Tidak melakukan apa-apa
        System.out.println("Tidak ada aksi khusus di " + this.name);
    }

    // Jembatan method (seperti diskusi sebelumnya)
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

    public Item getItem() { return this.item; } // Ganti getCurrentItem jadi getItem biar konsisten
    public Item getCurrentItem() { return this.item; } // Jaga-jaga buat kompatibilitas

    public boolean isEmpty() { return this.item == null; }

    public String getName() { return name; }

    // HAPUS Getter Position & getX/getY di sini
    // (Karena GameObject sudah menyediakannya)
}
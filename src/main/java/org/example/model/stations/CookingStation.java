package org.example.model.stations;

import org.example.model.entities.Chef;
import org.example.model.items.Item;
import org.example.model.items.Pizza;
import org.example.model.map.Position;

// Abstract: Kita tidak menaruh "CookingStation" kosong di map, tapi "Oven" atau "Stove"
public abstract class CookingStation extends Station {

    public CookingStation(String name, Position position) {
        super(name, position);
    }

    // --- METHOD INTERAKSI UTAMA (Key V) ---
    @Override
    public void interact(Chef chef) {
        // 1. CHEF MENARUH BARANG KE STATION
        if (this.isEmpty() && chef.isHoldingItem()) {
            Item itemOnHand = chef.getInventory();

            // Tanya ke Child Class (Oven): "Apakah item ini boleh masuk?"
            if (shouldAcceptItem(itemOnHand)) {
                this.addItem(chef.dropItem());
                System.out.println(itemOnHand.getName() + " masuk ke " + this.getName());
            } else {
                System.out.println(this.getName() + " menolak item ini!");
            }
        }

        // 2. CHEF MENGAMBIL HASIL DARI STATION
        else if (!this.isEmpty() && !chef.isHoldingItem()) {
            // Ambil barang dari station, kasih ke chef
            Item item = this.removeItem();
            chef.setInventory(item);
            System.out.println("Chef mengambil " + item.getName());
        }
    }

    // --- METHOD ACTION (Key C) ---
    @Override
    public void action(Chef chef) {
        // Biasanya Oven otomatis, jadi kosong.
        // Tapi jika nanti ada "Wajan" yang harus diaduk manual, isinya di sini.
    }

    // --- GAME LOOP UPDATE (Otomatis Masak) ---
    // Dipanggil oleh GameManager setiap frame
    public void update(double deltaTime) {
        // Cek apakah ada barang di station
        if (!this.isEmpty()) {
            Item item = this.getItem();

            // Jika item tersebut adalah Pizza (atau Preparable lain yg bisa dimasak)
            if (item instanceof Pizza) {
                Pizza pizza = (Pizza) item;

                // JALANKAN TIMER DI CLASS PIZZA
                pizza.addCookingDuration(deltaTime);
            }
        }
    }

    // --- ABSTRACT METHOD ---
    // Setiap alat masak punya aturan beda (Oven cuma mau Pizza, Kompor cuma mau Daging)
    protected abstract boolean shouldAcceptItem(Item item);
}
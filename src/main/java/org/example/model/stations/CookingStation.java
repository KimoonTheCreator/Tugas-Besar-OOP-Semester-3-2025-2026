package org.example.model.stations;

import org.example.model.entities.Chef;
import org.example.model.interfaces.CookingDevice;
import org.example.model.interfaces.Preparable;
import org.example.model.items.Item;
import org.example.model.map.Position;

public class CookingStation extends Station {

    public CookingStation(String name, Position position) {
        super(name, position);
    }

    @Override
    public void interact(Chef chef) {
        Item chefItem = chef.getInventory();
        Item stationItem = this.getItem();

        // KASUS 1: MENARUH ALAT MASAK (Chef bawa Panci/Wajan, Meja Kosong)
        if (chefItem instanceof CookingDevice && this.isEmpty()) {
            this.addItem(chef.dropItem());
        }

        // KASUS 2: MENGAMBIL ALAT MASAK (Chef Kosong, Meja ada Panci/Wajan)
        else if (!chef.isHoldingItem() && !this.isEmpty()) {
            // Validasi Khusus: OVEN tidak boleh diambil (Non-Portable)
            if (stationItem instanceof CookingDevice) {
                if (!((CookingDevice) stationItem).isPortable()) {
                    return;
                }
            }
            chef.setInventory(this.takeItem());
        }

        // KASUS 3: MEMASUKKAN BAHAN KE ALAT MASAK (Chef bawa Bahan, Meja ada Panci)
        // Chef tidak perlu ambil panci dulu, bisa langsung cemplungin bahan.
        else if (chefItem instanceof Preparable && stationItem instanceof CookingDevice) {
            CookingDevice device = (CookingDevice) stationItem;
            Preparable bahan = (Preparable) chefItem;

            // Cek apakah alat masak mau menerima bahan ini? (Validasi Resep)
            if (device.canAccept(bahan)) {
                device.addIngredient(bahan); // Masukkan bahan
                chef.dropItem();             // Hilangkan dari tangan Chef
            } else {
            }
        }
    }

    // --- LOGIKA MEMASAK (PENTING) ---
    // Method ini HARUS dipanggil oleh GameLoop/Controller setiap detik/tick.
    // Inilah yang membuat "Kompor Menyala".
    public void update() {
        if (!this.isEmpty() && this.getItem() instanceof CookingDevice) {
            CookingDevice device = (CookingDevice) this.getItem();

            // Perintah alat masak untuk memproses bahan di dalamnya
            device.startCooking();
        }
    }
}
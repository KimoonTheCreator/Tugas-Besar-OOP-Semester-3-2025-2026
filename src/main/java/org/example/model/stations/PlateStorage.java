package org.example.model.stations;

import org.example.model.entities.Chef;
import org.example.model.items.Plate;
import org.example.model.map.Position;

public class PlateStorage extends Station {

    public PlateStorage(String name, Position position) {
        super(name, position);
    }

    @Override
    public void interact(Chef chef) {
        // SKENARIO 1: MENGAMBIL PIRING (Chef Tangan Kosong)
        if (!chef.isHoldingItem()) {
            // Spawn piring baru (Defaultnya piring baru pasti bersih)
            Plate piringBaru = new Plate();
            chef.setInventory(piringBaru);

            System.out.println("Chef mengambil piring bersih.");
        }

        // SKENARIO 2: MENARUH KEMBALI PIRING (Chef Bawa Piring Bersih)
        // Jika pemain tidak jadi pakai piring, bisa dikembalikan ke sini
        else if (chef.isHoldingItem() && chef.getInventory() instanceof Plate) {
            Plate piringDiTangan = (Plate) chef.getInventory();

            // Validasi: Hanya piring BERSIH yang boleh masuk storage
            if (piringDiTangan.isClean()) {
                chef.dropItem(); // Piring hilang dari tangan (masuk ke tumpukan storage)
                System.out.println("Chef menaruh kembali piring bersih ke storage.");
            } else {
                System.out.println("Gagal! Piring kotor tidak boleh masuk storage (Cuci dulu!).");
            }
        }

        // SKENARIO 3: Plating di atas tumpukan piring (Opsional)
        // Beberapa game membolehkan menaruh makanan langsung ke tumpukan piring di storage
        // Jika ingin fitur ini, Anda bisa tambahkan logika mirip AssemblyStation di sini.
    }
}
package org.example.model.stations;

import org.example.model.entities.Chef;
import org.example.model.items.Plate;
import org.example.model.map.Position;
import org.example.model.order.OrderManager;

public class ServingCounter extends Station {
    private OrderManager orderManager;

    public ServingCounter(String name, Position position, OrderManager orderManager) {
        super(name, position);
        this.orderManager = orderManager;
    }

    @Override
    public void interact(Chef chef) {
        // Trigger: Drop (Key V)
        if (chef.isHoldingItem() && chef.getInventory() instanceof Plate) {
            Plate plate = (Plate) chef.getInventory();

            // Validasi Order
            boolean isSuccess = orderManager.validateAndRemoveOrder(plate.getContents());

            if (isSuccess) {
                System.out.println("Order SUKSES! Skor bertambah.");
            } else {
                System.out.println("Order GAGAL! Penalti. Makanan dimakan Kak Jendra.");
            }

            // OUTPUT: Plate hilang dari tangan Chef (dimakan/disajikan)
            // Nanti Chef harus ambil piring baru di PlateStorage.
            // (Simulasi "Kembali ke Plate Storage" = destroy object di sini)
            chef.dropItem();

            // Catatan: Untuk fitur "Kembali ke storage 10 detik kotor",
            // itu butuh sistem Event Queue global yang rumit.
            // Solusi simple: Piring hilang, PlateStorage selalu sedia piring baru.
        }
    }
}
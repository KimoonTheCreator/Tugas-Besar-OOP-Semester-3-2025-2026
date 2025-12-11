package org.example.model.stations;

import org.example.model.entities.Chef;
import org.example.model.enums.ChefState;
import org.example.model.items.Plate;
import org.example.model.map.Position;

public class WashingStation extends Station {

    private boolean isProcessRunning; // Penanda agar thread tidak dobel

    public WashingStation(String name, Position position) {
        super(name, position);
        this.isProcessRunning = false;
    }

    @Override
    public void interact(Chef chef) {
        // SKENARIO 1: MENARUH PIRING KOTOR (Chef bawa Piring, Wastafel Kosong)
        if (chef.isHoldingItem() && this.isEmpty()) {
            if (chef.getInventory() instanceof Plate) {
                Plate piring = (Plate) chef.getInventory();

                // Hanya piring kotor yang boleh masuk wastafel
                if (!piring.isClean()) {
                    this.addItem(chef.dropItem());
                }
            }
        }

        // SKENARIO 2: MENCUCI / MENGAMBIL (Wastafel ada isinya)
        else if (!this.isEmpty() && this.item instanceof Plate) {
            Plate piringDiWastafel = (Plate) this.item;

            // Jika Piring MASIH KOTOR -> Cuci (Start Thread)
            if (!piringDiWastafel.isClean()) {
                if (!isProcessRunning) {
                    startWashingProcess(chef, piringDiWastafel);
                }
            }
            // Jika Piring SUDAH BERSIH -> Ambil (Pick Up)
            else if (piringDiWastafel.isClean() && !chef.isHoldingItem()) {
                chef.setInventory(this.takeItem());
            }
        }
    }

    private void startWashingProcess(Chef chef, Plate piring) {
        isProcessRunning = true;

        // Kunci Chef agar tidak bisa gerak (Busy State)
        chef.setState(ChefState.INTERACT);

        new Thread(() -> {
            try {
                int progress = 0;
                // Simulasi durasi mencuci (misal 3 detik)
                while (progress < 100) {
                    Thread.sleep(500); // Update setiap 0.5 detik

                    // Cek Pembatalan: Jika pemain memaksa gerak (State berubah)
                    if (chef.getState() != ChefState.INTERACT) {
                        isProcessRunning = false;
                        return; // Batalkan proses cuci
                    }

                    progress += 17; // ~100% dalam 3 detik
                }

                // JIKA SELESAI:
                piring.cleanPlate(); // Method di Plate untuk ubah isClean = true
                chef.setState(ChefState.IDLE); // Bebaskan Chef

            } catch (InterruptedException e) {
                chef.setState(ChefState.IDLE);
            } finally {
                isProcessRunning = false;
            }
        }).start();
    }
}
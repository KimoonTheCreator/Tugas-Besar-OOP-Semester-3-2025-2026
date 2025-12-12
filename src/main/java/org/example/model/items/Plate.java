package org.example.model.items;

import org.example.model.interfaces.Preparable;
import java.util.ArrayList;
import java.util.List;

public class Plate extends KitchenUtensils {

    private boolean isClean;

    public Plate() {
        // Asumsi: Parent punya constructor (String name, int capacity)
        // Kita set kapasitas 1, karena 1 piring hanya muat 1 hidangan (Pizza)
        super("Plate");
        this.isClean = true;
    }

    // ==========================================
    // 1. LOGIC STACKING & VALIDASI (SOLUSI BUG)
    // ==========================================
    @Override
    public void addItem(Preparable item) {
        // Cek 1: Piring Kotor -> Tolak
        if (!this.isClean) {
            System.out.println("Gagal! Piring kotor, tidak bisa menaruh makanan.");
            return;
        }

        // Cek 2: Piring Sudah Ada Isi -> Tolak
        // (Mencegah player menumpuk 2 pizza atau menumpuk bahan mentah ke pizza yang
        // sudah jadi)
        if (!this.getContents().isEmpty()) {
            System.out.println("Gagal! Piring sudah terisi.");
            return;
        }

        // Jika lolos, simpan item (Pizza/Ingredient)
        super.addItem(item);
    }

    // Adapter method untuk support kode teman (jika ada yang pass Ingredient
    // specific)
    public void addIngredients(Ingredient ingredient) {
        this.addItem(ingredient);
    }

    public void addContent(Preparable item) {
        this.addItem(item);
    }

    // ==========================================
    // 2. LOGIC WASHING (PEMBERSIHAN)
    // ==========================================
    public void wash() {
        this.cleanPlate();
    }

    public void cleanPlate() {
        this.isClean = true;
        this.emptyContents(); // Pastikan piring kosong saat bersih
        System.out.println("Piring sekarang BERSIH.");
    }

    // ==========================================
    // 3. LOGIC SERVING (SIKLUS HIDUP)
    // ==========================================

    // Dipanggil oleh ServingStation setelah skor dihitung
    public void markAsDirty() {
        this.isClean = false;
        this.emptyContents(); // Makanan dianggap sudah dimakan customer
        System.out.println("Piring sekarang KOTOR.");
    }

    public boolean isClean() {
        return isClean;
    }

    // Helper untuk mengecek apakah piring ada isinya (untuk UI atau Logic Player)
    public boolean hasFood() {
        return !this.getContents().isEmpty();
    }

    // ==========================================
    // 4. KONVERSI DATA (UNTUK ORDER MANAGER)
    // ==========================================
    public Dish createDish(String name) {
        // Method ini HANYA mengintip isi piring untuk dinilai manager
        Dish d = new Dish(name);
        List<Ingredient> ingredients = new ArrayList<>();

        for (Preparable p : this.getContents()) {
            // Jika isi piring langsung sebuah Dish (misal Pizza utuh)
            if (p instanceof Dish) {
                return (Dish) p;
            }
            // Jika isi piring adalah kumpulan Ingredients
            if (p instanceof Ingredient) {
                ingredients.add((Ingredient) p);
            }
        }

        d.setComponents(ingredients);
        return d;
    }
}
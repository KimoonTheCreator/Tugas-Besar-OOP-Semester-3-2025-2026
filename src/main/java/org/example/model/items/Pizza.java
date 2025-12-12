package org.example.model.items;

import org.example.model.interfaces.Preparable;
import org.example.model.enums.IngredientState;

public class Pizza extends Item implements Preparable {

    private IngredientState state;
    private String type; // Contoh: "Cheese", "Pepperoni"

    // Timer Masak
    private double cookingDuration;

    // Konstanta Waktu (Dalam detik)
    private final double TIME_TO_COOK = 12.0; // 12 Detik -> Matang
    private final double TIME_TO_BURN = 24.0; // 24 Detik -> Gosong

    public Pizza(String type) {
        // Nama item gabungan, misal: "Pizza Cheese"
        super("Pizza " + type);
        this.type = type;
        this.state = IngredientState.RAW; // Default: Mentah
        this.cookingDuration = 0;
    }

    // ==========================================
    // CORE LOGIC: TIMER MEMASAK
    // ==========================================

    /**
     * Method ini WAJIB dipanggil oleh CookingStation di dalam method update(deltaTime).
     * @param deltaTime Waktu yang berlalu sejak frame terakhir (detik).
     */
    public void addCookingDuration(double deltaTime) {
        // 1. Jika sudah gosong, stop proses masak.
        if (this.state == IngredientState.BURNED) {
            return;
        }

        // 2. Tambahkan durasi
        this.cookingDuration += deltaTime;

        // 3. Cek Transisi Status

        // Transisi: RAW -> COOKED
        if (this.state == IngredientState.RAW && this.cookingDuration >= TIME_TO_COOK) {
            cook(); // Panggil fungsi cook()
        }
        // Transisi: COOKED -> BURNED
        else if (this.state == IngredientState.COOKED && this.cookingDuration >= TIME_TO_BURN) {
            burn(); // Panggil fungsi burn()
        }
    }

    // ==========================================
    // IMPLEMENTASI METHOD PREPARABLE
    // ==========================================

    @Override
    public void cook() {
        // Ubah state hanya jika belum matang dan belum gosong
        if (this.state == IngredientState.RAW) {
            this.state = IngredientState.COOKED;
            System.out.println("TING! " + getName() + " sudah MATANG!");
        }
    }

    public void burn() {
        // Ubah state jadi gosong
        if (this.state != IngredientState.BURNED) {
            this.state = IngredientState.BURNED;
            System.out.println("WADUH! " + getName() + " GOSONG!");
        }
    }

    @Override
    public void chop() {
        // Pizza tidak bisa dicincang
        System.out.println("Pizza tidak bisa dipotong!");
    }

    public void addCuttingProgress(double amount) {
        // Tidak ada logic potong
    }

    // ==========================================
    // METHOD VALIDASI (PREPARABLE)
    // ==========================================

    @Override
    public boolean canBeChopped() {
        return false; // Pizza tidak bisa dipotong
    }

    @Override
    public boolean canBeCooked() {
        // Bisa dimasak selama belum gosong
        return this.state != IngredientState.BURNED;
    }

    @Override
    public boolean canBePlacedOnPlate() {
        return true; // Pizza valid ditaruh di piring
    }

    // ==========================================
    // GETTER & SETTER
    // ==========================================

    public IngredientState getState() {
        return state;
    }

    public void setState(IngredientState state) {
        this.state = state;
    }

    public String getType() {
        return type;
    }

    public double getCookingDuration() {
        return cookingDuration;
    }

    // Helper untuk UI/Render (Mendapatkan progress bar masak 0.0 - 1.0)
    public float getCookProgress() {
        if (state == IngredientState.BURNED) return 1.0f;
        return (float) Math.min(cookingDuration / TIME_TO_COOK, 1.0);
    }
}
package org.example.model.items;

import org.example.model.interfaces.Preparable;
import org.example.model.enums.IngredientState;

public class Pizza extends Item implements Preparable {

    private IngredientState state;
    private String type; // Contoh: "Cheese", "Pepperoni"

    // Timer masak
    private double cookingDuration;

    private final double TIME_TO_COOK = 12.0;  // Matang setelah 12 detik
    private final double TIME_TO_BURN = 24.0;  // Gosong setelah 24 detik

    public Pizza(String type) {
        super("Pizza " + type);
        this.type = type;
        this.state = IngredientState.RAW;
        this.cookingDuration = 0;
    }

    // Timer update - dipanggil setiap frame oleh CookingStation
    public void addCookingDuration(double deltaTime) {
        if (this.state == IngredientState.BURNED) return;

        this.cookingDuration += deltaTime;

        // Cek transisi status: RAW -> COOKED -> BURNED
        if (this.state == IngredientState.RAW && this.cookingDuration >= TIME_TO_COOK) {
            cook();
        } else if (this.state == IngredientState.COOKED && this.cookingDuration >= TIME_TO_BURN) {
            burn();
        }
    }

    // Preparable implementation

    @Override
    public void cook() {
        if (this.state == IngredientState.RAW) {
            this.state = IngredientState.COOKED;
            System.out.println(getName() + " sudah MATANG!");
        }
    }

    public void burn() {
        if (this.state != IngredientState.BURNED) {
            this.state = IngredientState.BURNED;
            System.out.println(getName() + " GOSONG!");
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

    // Validasi status

    @Override
    public boolean canBeChopped() {
        return false; // Pizza tidak bisa dipotong
    }

    @Override
    public boolean canBeCooked() {
        // Bisa dimasak selama belum gosong
        return this.state != IngredientState.BURNED;
    }

    public boolean canBePlacedOnPlate() {
        return true;
    }

    // Getters & Setters

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

    // Progress bar buat UI (0.0 - 1.0)
    public float getCookProgress() {
        if (state == IngredientState.BURNED)
            return 1.0f;
        return (float) Math.min(cookingDuration / TIME_TO_COOK, 1.0);
    }
}
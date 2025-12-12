package org.example.model.items;

import org.example.model.interfaces.Preparable;
import org.example.model.enums.IngredientState;

public class Ingredient extends Item implements Preparable {
    private IngredientState state;

    private double cuttingProgress;
    private double cookingDuration;

    // --- SESUAI SPEK ---
    private final double MAX_CUTTING_PROGRESS = 100.0;

    // Waktu Masak: 12 Detik Matang, >24 Detik Gosong
    private double timeToCook = 12.0;
    private double timeToBurn = 24.0;

    public Ingredient(String name){
        super(name);
        this.state = IngredientState.RAW;
        this.cuttingProgress = 0.0;
        this.cookingDuration = 0.0;
    }

    // --- LOGIKA CUTTING ---
    public void addCuttingProgress(double amount){
        if(canBeChopped()){
            this.cuttingProgress += amount;
            if(this.cuttingProgress >= MAX_CUTTING_PROGRESS){
                this.cuttingProgress = MAX_CUTTING_PROGRESS;
                chop();
            }
        }
    }

    public void chop(){
        if (canBeChopped()){
            this.state = IngredientState.CHOPPED;
            System.out.println(this.getName() + " menjadi CHOPPED!");
        }
    }

    // --- LOGIKA COOKING ---
    public void addCookingDuration(double deltaTime){
        if(canBeCooked()){
            this.cookingDuration += deltaTime;

            // Cek Gosong (> 24 detik)
            if(this.cookingDuration >= timeToBurn){
                burn();
            }
            // Cek Matang (>= 12 detik)
            else if(this.cookingDuration >= timeToCook){
                cook();
            }
        }
    }

    public void cook(){
        if(this.state != IngredientState.COOKED && this.state != IngredientState.BURNED){
            this.state = IngredientState.COOKED;
            System.out.println(this.getName() + " menjadi COOKED!");
        }
    }

    public void burn(){
        if(this.state != IngredientState.BURNED){
            this.state = IngredientState.BURNED;
            System.out.println(this.getName() + " menjadi BURNED!");
        }
    }

    // Getter Setter & Validasi
    public IngredientState getState() { return state; }

    public void setState(IngredientState state) {
        this.state = state;
        if (state == IngredientState.RAW) cuttingProgress = 0;
        if (state == IngredientState.RAW || state == IngredientState.CHOPPED) cookingDuration = 0;
    }

    public boolean canBePlacedOnPlate() {
        return true; // Semua ingredient bisa ditaruh di piring
    }
    public boolean canBeChopped(){ return this.state == IngredientState.RAW; }

    // Bahan yang bisa dimasak: RAW atau CHOPPED (Sesuai Spek Cooking Action)
    public boolean canBeCooked(){ return this.state != IngredientState.BURNED; }

    public double getCuttingProgress(){ return this.cuttingProgress; }
    public double getCookingDuration(){ return this.cookingDuration; }
}
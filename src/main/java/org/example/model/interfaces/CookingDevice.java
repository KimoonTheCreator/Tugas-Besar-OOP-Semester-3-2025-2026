package org.example.model.interfaces;

public interface CookingDevice {
    boolean isPortable();
    int capacity();
    boolean canAccept(Prepareable prepareable);
    void addIngredient(Prepareable prepareable);
    void startCooking();
}
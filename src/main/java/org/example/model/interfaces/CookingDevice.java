package org.example.model.interfaces;

public interface CookingDevice {
    boolean isPortable();

    int capacity();

    boolean canAccept(Preparable preparable);

    void addIngredient(Preparable preparable);

    void update(double deltaTime);
}
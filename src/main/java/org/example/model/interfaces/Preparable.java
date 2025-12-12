package org.example.model.interfaces;

public interface Preparable {
    // Interface for items that can be prepared (chopped, cooked, etc.)
    boolean canBeChopped();

    boolean canBeCooked();

    boolean canBePlacedOnPlate();

    void chop();

    void cook();

    void addCookingDuration(double deltaTime);

    double getCookingDuration();
}
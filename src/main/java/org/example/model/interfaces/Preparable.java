package org.example.model.interfaces;

public interface Preparable {
    boolean canBeChopped();
    boolean canBeCooked();
    boolean canBePlacedOnPlate();

    void chop();
    void cook();
}
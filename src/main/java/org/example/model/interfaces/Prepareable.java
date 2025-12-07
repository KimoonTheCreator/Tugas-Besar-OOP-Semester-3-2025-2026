package org.example.model.interfaces;

public interface Prepareable{
    boolean canBeChopped();
    boolean canBeCooked();
    boolean canBePlacedOnPlate();

    void chop();
    void cook();
}
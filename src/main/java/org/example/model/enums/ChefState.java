package org.example.model.enums;

/**
 * Enum untuk status chef
 */
public enum ChefState {
    IDLE, // Diam
    MOVE, // Bergerak
    HOLDING_ITEM, // Membawa item
    COOKING, // Memasak
    CUTTING, // Memotong
    INTERACT // Interaksi
}
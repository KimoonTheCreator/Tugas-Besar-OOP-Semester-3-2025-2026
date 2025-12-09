package org.example.model.enums;

/**
 * Enum untuk tombol keyboard yang digunakan dalam game
 */
public enum Key {
    // Movement
    W, // Gerak atas
    A, // Gerak kiri
    S, // Gerak bawah
    D, // Gerak kanan

    // Actions
    E, // Interact (general)
    Q, // Pickup item
    F, // Drop item
    C, // Cut action
    X, // Wash action
    R, // Use/Cook action

    // System
    TAB, // Switch chef
    P, // Pause
    SPACE, // Dash/Action

    // Legacy (untuk backward compatibility)
    V // Pickup/Drop (old)
}

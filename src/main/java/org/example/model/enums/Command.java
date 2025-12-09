package org.example.model.enums;

/**
 * Enum untuk command/perintah dalam game
 * Setiap action memiliki command yang berbeda
 */
public enum Command {
    // Movement commands
    MOVE_UP,
    MOVE_DOWN,
    MOVE_LEFT,
    MOVE_RIGHT,
    DASH, // Dash movement

    // Item handling
    PICKUP, // Ambil item dari station (Q)
    DROP, // Taruh item ke station (F)
    PICKUP_DROP, // Legacy: gabungan pickup/drop (V)

    // Station-specific actions
    CUT, // Potong ingredient di Cutting Station (C)
    COOK, // Masak di Cooking Station (R)
    WASH, // Cuci piring di Washing Station (X)

    // General
    INTERACT, // Interaksi umum (E)

    // System commands
    SWITCH_CHEF, // Ganti chef aktif (TAB)
    PAUSE, // Pause game (P)
    OPEN_MENU, // Buka menu (M/ESC)

    NONE // Tidak ada command
}

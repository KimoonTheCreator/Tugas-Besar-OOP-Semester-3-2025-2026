package org.example;

import org.example.view.GameWindow;
import javax.swing.SwingUtilities;

/**
 * Kelas utama untuk menjalankan game
 * Tugas Besar OOP - Cooking Game
 */
public class Main {
    public static void main(String[] args) {
        // Jalankan game window
        SwingUtilities.invokeLater(() -> {
            new GameWindow();
        });
    }
}

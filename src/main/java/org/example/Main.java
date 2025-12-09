package org.example;

import org.example.view.MainMenu;
import javax.swing.SwingUtilities;

/**
 * Kelas utama untuk menjalankan game
 * Tugas Besar OOP - Cooking Game
 */
public class Main {
    public static void main(String[] args) {
        // Jalankan main menu terlebih dahulu
        SwingUtilities.invokeLater(() -> {
            new MainMenu();
        });
    }
}

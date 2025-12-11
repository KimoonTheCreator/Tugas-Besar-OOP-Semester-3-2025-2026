package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane; // Gunakan AnchorPane/Parent generik untuk root Main Menu
import javafx.stage.Stage;
import javafx.scene.input.KeyCode;

import org.example.view.AssetManager;
//import org.example.view.AudioManager; // Tambahkan ini jika Anda sudah membuat AudioManager

import java.io.IOException;

public class Main extends Application {

    // Hapus GameController di sini, karena Controller utama adalah MainMenuController
    // private GameController controller;

    @Override
    public void start(Stage primaryStage) throws IOException {
        // 1. Panggil AssetManager (Wajib)
        AssetManager.loadAssets();
        // AudioManager.loadAudioAssets(); // <-- Tambahkan jika AudioManager sudah dibuat

        // 2. Muat FXML (View) - Sekarang memuat MAIN MENU VIEW
        FXMLLoader fxmlLoader = new FXMLLoader();

        fxmlLoader.setLocation(
                // UBAH PATH ke MainMenuView.fxml
                getClass().getResource("/org/example/view/MainMenuView.fxml")
        );

        // Root element FXML Anda harus sesuai dengan root element di MainMenuView.fxml (misalnya AnchorPane)
        AnchorPane root = fxmlLoader.load(); // <-- UBAH TIPE DARI BorderPane ke tipe root Main Menu Anda

        // Catatan: Anda tidak perlu mendapatkan controller di sini karena ini adalah Main Menu
        // dan GameController hanya diperlukan setelah tombol Play diklik.

        // Tentukan ukuran Scene (sesuai MainMenuView)
        Scene scene = new Scene(root, 700, 600);

        // 4. Setup Input Keyboard
        // HAPUS SEMUA LOGIKA INPUT KEYBOARD (WASD, TAB, E, V) DI SINI!
        // Input keyboard hanya dipasang di GameScene, setelah tombol Play diklik.

        // HANYA logik ESCAPE yang mungkin bisa dipertahankan jika ingin selalu bisa exit:
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                System.out.println("Application closed by ESC.");
                primaryStage.close();
            }
        });

        // 5. Setup Stage (Jendela)
        primaryStage.setTitle("Nimonscooked - Cooking Game");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
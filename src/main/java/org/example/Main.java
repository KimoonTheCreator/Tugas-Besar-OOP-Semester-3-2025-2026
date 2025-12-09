package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import org.example.controller.GameController;
import org.example.model.map.Direction; // Import objek Direction Anda
import org.example.view.AssetManager;
import org.example.model.enums.Key; // Jika masih diperlukan untuk InputHandler

import java.io.IOException;

public class Main extends Application {

    private GameController controller;

    @Override
    public void start(Stage primaryStage) throws IOException {
        // 1. Panggil AssetManager (Wajib)
        AssetManager.loadAssets();

        // 2. Muat FXML (View)
        FXMLLoader fxmlLoader = new FXMLLoader();

        fxmlLoader.setLocation(
            getClass().getResource("/org/example/view/GameView.fxml")
        );

        // Catatan: Ganti VBox dengan root element FXML Anda (misalnya BorderPane atau AnchorPane)
        BorderPane root = fxmlLoader.load();
        
        // 3. Ambil Controller yang sudah dibuat
        controller = fxmlLoader.getController();

        // Tentukan ukuran Scene (sesuaikan dengan ukuran map Anda: 14x10 tiles)
        // Lebar: 14 * 50 = 700. Tinggi: 10 * 50 = 500. Ditambah area info bar (misal 100)
        Scene scene = new Scene(root, 700, 600); 
        
        // 4. Setup Input Keyboard (Menggantikan KeyListener di GameWindow)
        scene.setOnKeyPressed(event -> {
            KeyCode code = event.getCode();
            
            // Logika seperti di GameWindow.keyPressed
            if (code == KeyCode.ESCAPE) {
                System.out.println("Game closed!");
                primaryStage.close();
            }

            // Panggil command handling di Controller
            switch (code) {
                case W:
                    controller.handleMoveCommand(Direction.UP);
                    break;
                case A:
                    controller.handleMoveCommand(Direction.LEFT);
                    break;
                case S:
                    controller.handleMoveCommand(Direction.DOWN);
                    break;
                case D:
                    controller.handleMoveCommand(Direction.RIGHT);
                    break;
                case TAB:
                    controller.switchChef();
                    break;
                case E:
                    controller.handleInteractCommand();
                    break;
                case V:
                    controller.handlePickupCommand();
                    break;
                default:
                    // Abaikan tombol lain
            }
            // Di JavaFX, tidak perlu panggil repaint() di sini, karena Controller yang akan meng-update View
        });

        // 5. Setup Stage (Jendela)
        primaryStage.setTitle("Nimonscooked - Cooking Game");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        // Panggilan launch() meluncurkan JavaFX dan memanggil start(Stage)
        launch(args); 
    }
}
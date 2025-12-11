package org.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class MainMenuController {

    @FXML private ImageView backgroundView;
    @FXML private ImageView titleView;
    @FXML private ImageView playButtonView;
    @FXML private ImageView tutorialButtonView;
    @FXML private ImageView quitButtonView;

    @FXML
    public void initialize() {
        try {
            // --- 1. MEMUAT ASSET BACKGROUND DAN TITLE ---
            
            // Background
            backgroundView.setImage(new Image(getClass().getResource("/res/ui/mainmenu.png").toExternalForm()));
            // Title
            titleView.setImage(new Image(getClass().getResource("/res/ui/title.png").toExternalForm()));


            // --- 2. MEMUAT ASSET TOMBOL NORMAL ---
            
            Image playNormal = loadButtonImage(playButtonView, "play");
            Image tutorialNormal = loadButtonImage(tutorialButtonView, "tutorial");
            Image quitNormal = loadButtonImage(quitButtonView, "quit");

            if (playNormal != null) playButtonView.setImage(playNormal);
            if (tutorialNormal != null) tutorialButtonView.setImage(tutorialNormal);
            if (quitNormal != null) quitButtonView.setImage(quitNormal);

            // --- 3. MEMASANG EFEK TOMBOL ---
            if (playNormal != null) setupButtonEffects(playButtonView, "play", playNormal);
            if (tutorialNormal != null) setupButtonEffects(tutorialButtonView, "tutorial", tutorialNormal);
            if (quitNormal != null) setupButtonEffects(quitButtonView, "quit", quitNormal);


        } catch (Exception e) {
            System.err.println("Terjadi kesalahan tak terduga saat inisialisasi Main Menu.");
            e.printStackTrace();
        }
    }

    private Image loadButtonImage(ImageView view, String id) {
        try {
            String path = "/res/ui/" + id + ".png";
            java.net.URL url = getClass().getResource(path);
            
            if (url == null) {
                System.err.println("FATAL ERROR: File tombol '" + id + ".png' tidak ditemukan di path: " + path);
                return null;
            }
            
            return new Image(url.toExternalForm());
            
        } catch (Exception e) {
            System.err.println("Gagal memuat asset '" + id + ".png'.");
            return null;
        }
    }

    private void setupButtonEffects(ImageView buttonView, String id, Image normalImage) {

        Image pressedImage;
        try {
            pressedImage = new Image(getClass().getResource("/res/ui/" + id + "_pressed.png").toExternalForm());
        } catch (Exception e) {
            pressedImage = normalImage; // Fallback
        }

        final Image finalPressedImage = pressedImage;

        buttonView.setOnMousePressed(event -> buttonView.setImage(finalPressedImage));
        buttonView.setOnMouseReleased(event -> buttonView.setImage(normalImage));
        buttonView.setOnMouseEntered(event -> buttonView.getScene().setCursor(javafx.scene.Cursor.HAND));
        buttonView.setOnMouseExited(event -> buttonView.getScene().setCursor(javafx.scene.Cursor.DEFAULT));
    }

    @FXML
    private void handlePlayButton(MouseEvent event) { 
        System.out.println("DEBUG: Tombol PLAY ditekan. Mencoba memuat Game Scene...");

        try {
            Stage stage = (Stage) playButtonView.getScene().getWindow();

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/view/GameView.fxml"));
            Parent gameRootNode = fxmlLoader.load(); 
            Scene gameScene = new Scene(gameRootNode, 700, 600);

            GameController gameController = fxmlLoader.getController(); 
            
            if (gameController == null) {
                throw new IllegalStateException("GameController tidak ditemukan setelah memuat GameView.fxml.");
            }
            System.out.println("DEBUG: GameController berhasil didapatkan.");

            // 2. PASANG INPUT KEYBOARD PADA GAME SCENE
            gameScene.setOnKeyPressed(e -> {
                javafx.scene.input.KeyCode code = e.getCode();
                switch (code) {
                    case W: gameController.handleMoveCommand(org.example.model.map.Direction.UP); break;
                    case A: gameController.handleMoveCommand(org.example.model.map.Direction.LEFT); break;
                    case S: gameController.handleMoveCommand(org.example.model.map.Direction.DOWN); break;
                    case D: gameController.handleMoveCommand(org.example.model.map.Direction.RIGHT); break;
                    case TAB: gameController.switchChef(); break;
                    case E: gameController.handleInteractCommand(); break;
                    case V: gameController.handlePickupCommand(); break;
                    case ESCAPE: gameController.handlePauseCommand(); break; // Handler Pause Menu
                    default: 
                }
            });

            // 3. Ganti Scene
            stage.setScene(gameScene);
            stage.show();
            System.out.println("DEBUG: Scene telah beralih ke Game View.");

        } catch (java.io.IOException e) {
            System.err.println("FATAL ERROR: Gagal memuat GameView.fxml. Cek path '/org/example/view/GameView.fxml' atau sintaks FXML.");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("FATAL ERROR: Terjadi error saat menginisialisasi GameController atau menyiapkan scene.");
            System.err.println("Penyebab utama: Cek @FXML fields, Asset Manager, atau 'initialize()' di GameController.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleTutorialButton(MouseEvent event) { 
        // Logika Tutorial
        System.out.println("Tutorial page will be loaded.");
    }

    @FXML
    private void handleQuitButton(MouseEvent event) { 
        Stage stage = (Stage) quitButtonView.getScene().getWindow();
        stage.close();
        System.out.println("Application closed from Main Menu.");
    }
}
package org.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

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
            try {
                backgroundView.setImage(new Image(getClass().getResource("/res/ui/mainmenu.png").toExternalForm()));
            } catch (NullPointerException e) {
                System.err.println("ERROR: Background (mainmenu.png) tidak ditemukan.");
                e.printStackTrace();
            }

            // Title
            try {
                titleView.setImage(new Image(getClass().getResource("/res/ui/title.png").toExternalForm()));
            } catch (NullPointerException e) {
                System.err.println("ERROR: Title (title.png) tidak ditemukan.");
                e.printStackTrace();
            }


            // --- 2. MEMUAT ASSET TOMBOL NORMAL ---
            
            Image playNormal = loadButtonImage(playButtonView, "playmainmenu");
            Image tutorialNormal = loadButtonImage(tutorialButtonView, "tutorialmainmenu");
            Image quitNormal = loadButtonImage(quitButtonView, "quitmainmenu");

            // Memasang gambar normal (Hanya jika berhasil dimuat)
            if (playNormal != null) playButtonView.setImage(playNormal);
            if (tutorialNormal != null) tutorialButtonView.setImage(tutorialNormal);
            if (quitNormal != null) quitButtonView.setImage(quitNormal);

            // --- 3. MEMASANG EFEK TOMBOL ---
            // Efek tombol akan menggunakan gambar normal sebagai fallback jika _pressed tidak ada
            if (playNormal != null) setupButtonEffects(playButtonView, "playmainmenu", playNormal);
            if (tutorialNormal != null) setupButtonEffects(tutorialButtonView, "tutorialmainmenu", tutorialNormal);
            if (quitNormal != null) setupButtonEffects(quitButtonView, "quitmainmenu", quitNormal);


        } catch (Exception e) {
            // Blok catch umum ini sekarang hanya menangkap error lain yang tidak terkait loading image
            System.err.println("Terjadi kesalahan tak terduga saat inisialisasi Main Menu.");
            e.printStackTrace();
        }
    }

    // Metode baru untuk memuat gambar tombol dan memberikan feedback
    private Image loadButtonImage(ImageView view, String id) {
        try {
            String path = "/res/ui/" + id + ".png";
            java.net.URL url = getClass().getResource(path);
            
            if (url == null) {
                System.err.println("FATAL ERROR: File tombol '" + id + ".png' tidak ditemukan di path: " + path);
                return null; // Gagal
            }
            
            System.out.println("DEBUG: Memuat " + path);
            return new Image(url.toExternalForm());
            
        } catch (Exception e) {
            System.err.println("Gagal memuat asset '" + id + ".png'.");
            e.printStackTrace();
            return null;
        }
    }
    // @FXML
    // public void initialize() {
    //     try {
    //         // Memuat Gambar Normal
    //         backgroundView.setImage(new Image(getClass().getResource("/res/ui/mainmenu.png").toExternalForm()));
    //         titleView.setImage(new Image(getClass().getResource("/res/ui/title.png").toExternalForm()));

    //         Image playNormal = new Image(getClass().getResource("/res/ui/playmainmenu.png").toExternalForm());
    //         Image tutorialNormal = new Image(getClass().getResource("/res/ui/tutorialmainmenu.png").toExternalForm());
    //         Image quitNormal = new Image(getClass().getResource("/res/ui/quitmainmenu.png").toExternalForm());

    //         playButtonView.setImage(playNormal);
    //         tutorialButtonView.setImage(tutorialNormal);
    //         quitButtonView.setImage(quitNormal);

    //         // 2. Memasang Efek Tombol (Panggil metode helper yang baru)
    //         setupButtonEffects(playButtonView, "playmainmenu", playNormal);
    //         setupButtonEffects(tutorialButtonView, "tutorialmainmenu", tutorialNormal);
    //         setupButtonEffects(quitButtonView, "quitmainmenu", quitNormal);


    //     } catch (Exception e) {
    //         System.err.println("Gagal memuat satu atau lebih aset PNG Main Menu. Pastikan path /res/ui/ sudah benar.");
    //         e.printStackTrace();
    //     }
    // }

    /**
     * Metode Helper untuk menangani efek visual tombol (Pressed State)
     * @param buttonView ImageView yang akan diberi efek
     * @param id ID tombol (misalnya "playmainmenu") untuk mencari file _pressed.png
     * @param normalImage Gambar tombol saat keadaan normal
     */
    private void setupButtonEffects(ImageView buttonView, String id, Image normalImage) {

        Image pressedImage;
        try {
            // Coba muat gambar versi _pressed.png
            pressedImage = new Image(getClass().getResource("/res/ui/" + id + "_pressed.png").toExternalForm());
        } catch (Exception e) {
            System.err.println("Warning: Pressed image not found for " + id + ". Using default image as fallback.");
            pressedImage = normalImage; // Fallback jika _pressed.png tidak ditemukan
        }

        final Image finalPressedImage = pressedImage;

        // 1. Mouse Pressed: Ganti ke gambar gelap/pressed
        buttonView.setOnMousePressed(event -> {
            buttonView.setImage(finalPressedImage);
        });

        // 2. Mouse Released: Kembali ke gambar normal
        // Ini akan dipicu sebelum onMouseClicked.
        buttonView.setOnMouseReleased(event -> {
            buttonView.setImage(normalImage);
        });

        // Opsional: Efek Hover (membuat kursor menjadi tangan)
        buttonView.setOnMouseEntered(event -> {
            buttonView.getScene().setCursor(javafx.scene.Cursor.HAND);
        });

        buttonView.setOnMouseExited(event -> {
            buttonView.getScene().setCursor(javafx.scene.Cursor.DEFAULT);
        });
    }

    // 3. Logic handler (Pastikan menggunakan referensi ImageView untuk mendapatkan Stage)

    @FXML
    private void handlePlayButton(MouseEvent event) throws IOException {
        // Dapatkan Stage dari Scene yang dimiliki oleh ImageView
        Stage stage = (Stage) playButtonView.getScene().getWindow();

        // 1. Muat GameView.fxml
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/view/GameView.fxml"));
        Scene gameScene = new Scene(fxmlLoader.load(), 700, 600);

        // Dapatkan GameController
        GameController gameController = fxmlLoader.getController(); // <-- PENTING: Ambil Controller Game!

        // 2. PASANG INPUT KEYBOARD PADA GAME SCENE
        gameScene.setOnKeyPressed(e -> {
            javafx.scene.input.KeyCode code = e.getCode();

            // Panggil command handling di GameController
            switch (code) {
                case W:
                    gameController.handleMoveCommand(org.example.model.map.Direction.UP);
                    break;
                case A:
                    gameController.handleMoveCommand(org.example.model.map.Direction.LEFT);
                    break;
                case S:
                    gameController.handleMoveCommand(org.example.model.map.Direction.DOWN);
                    break;
                case D:
                    gameController.handleMoveCommand(org.example.model.map.Direction.RIGHT);
                    break;
                case TAB:
                    gameController.switchChef();
                    break;
                case E:
                    gameController.handleInteractCommand();
                    break;
                case V:
                    gameController.handlePickupCommand();
                    break;
                case ESCAPE: // Tambahkan trigger pause di sini
                    gameController.handlePauseCommand();
                    break;
                default:
                    // Abaikan tombol lain
            }
        });

        // 3. Ganti Scene
        stage.setScene(gameScene);
        stage.show();
    }

    @FXML
    private void handleTutorialButton(MouseEvent event) { // <-- Perlu parameter MouseEvent
        Stage stage = (Stage) tutorialButtonView.getScene().getWindow();
        // TODO: Implementasi logika untuk berpindah ke TutorialView.fxml
        System.out.println("Tutorial page will be loaded.");
    }

    @FXML
    private void handleQuitButton(MouseEvent event) { // <-- Perlu parameter MouseEvent
        Stage stage = (Stage) quitButtonView.getScene().getWindow();
        stage.close();
        System.out.println("Application closed from Main Menu.");
    }
}
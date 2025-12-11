package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
// import java.io.IOException; // Tidak diperlukan karena tidak ada throws IOException

public class PauseMenuController {

    @FXML private ImageView backgroundView;
    @FXML private ImageView titleView;
    @FXML private ImageView resumeButtonView;
    @FXML private ImageView tutorialButtonView;
    @FXML private ImageView quitButtonView;
    
    // Perlu referensi ke GameController agar bisa mengontrol game loop dan stage
    private GameController gameController;

    public void setGameController(GameController controller) {
        this.gameController = controller;
    }

    @FXML
    public void initialize() {
        // --- 1. Memuat Gambar ---
        try {
            // Background Menu Pause
            backgroundView.setImage(new Image(getClass().getResource("/res/ui/menu_popup.png").toExternalForm())); 

            // Title Menu Pause
            Image title = new Image(getClass().getResource("/res/ui/paused.png").toExternalForm()); 
            
            // Tombol
            Image resumeNormal = loadButtonImage(resumeButtonView, "resume");
            Image quitNormal = loadButtonImage(quitButtonView, "quit");
            Image tutorialNormal = loadButtonImage(tutorialButtonView, "tutorial");

            titleView.setImage(title);
            if (resumeNormal != null) resumeButtonView.setImage(resumeNormal);
            if (quitNormal != null) quitButtonView.setImage(quitNormal);
            if (tutorialNormal != null) tutorialButtonView.setImage(tutorialNormal);

            // --- 2. Memasang Efek Tombol (DIKOMENTARI) ---
            /*
            if (resumeNormal != null) setupButtonEffects(resumeButtonView, "resume", resumeNormal);
            if (quitNormal != null) setupButtonEffects(quitButtonView, "quit", quitNormal);
            if (tutorialNormal != null) setupButtonEffects(tutorialButtonView, "tutorial", tutorialNormal);
            */
            
        } catch (Exception e) {
            System.err.println("Gagal memuat aset Pause Menu. Pastikan path /res/ui/ sudah benar.");
            // e.printStackTrace(); 
        }
    }
    
    // Metode Helper untuk memuat gambar normal
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
            return null;
        }
    }
    
    // Metode Helper untuk Efek Tombol (DIKOMENTARI)
    /*
    private void setupButtonEffects(ImageView buttonView, String id, Image normalImage) {

        Image pressedImage;
        try {
            pressedImage = new Image(getClass().getResource("/res/ui/" + id + "_pressed.png").toExternalForm());
        } catch (Exception e) {
            pressedImage = normalImage;
        }

        final Image finalPressedImage = pressedImage;

        buttonView.setOnMousePressed(event -> {
            buttonView.setImage(finalPressedImage);
        });

        buttonView.setOnMouseReleased(event -> {
            buttonView.setImage(normalImage);
        });

        buttonView.setOnMouseEntered(event -> {
            buttonView.getScene().setCursor(javafx.scene.Cursor.HAND);
        });

        buttonView.setOnMouseExited(event -> {
            buttonView.getScene().setCursor(javafx.scene.Cursor.DEFAULT);
        });
    }
    */


    @FXML
    private void handleResumeButton(MouseEvent event) {
        if (gameController != null) {
            gameController.resumeGame(); // Panggil metode resume di GameController
        }
    }
    
    @FXML
    private void handleTutorialButton(MouseEvent event) {
        // Logika membuka tutorial
        System.out.println("Membuka tutorial dari menu pause.");
    }

    @FXML
    private void handleQuitButton(MouseEvent event) {
        // Logika keluar ke Main Menu
        if (gameController != null) {
            gameController.quitToMainMenu(); 
        }
    }
}
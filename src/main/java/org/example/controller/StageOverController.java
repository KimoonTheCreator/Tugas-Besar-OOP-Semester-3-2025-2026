package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.Cursor;
import javafx.scene.layout.AnchorPane; // Import untuk root node

public class StageOverController {

    // NOTE: titleImageView menggantikan titleLabel yang lama untuk aset gambar judul
    @FXML
    private ImageView titleImageView; 
    @FXML
    private ImageView popupBackgroundView; // Untuk gambar menu_popup.png
    @FXML
    private AnchorPane stageOverRoot; // Untuk menghapus view dari parent
    
    @FXML
    private Label scoreLabel;
    @FXML
    private Label reasonLabel;
    @FXML
    private Label statusLabel;
    
    @FXML
    private ImageView restartButtonView;
    @FXML
    private ImageView mainMenuButtonView;
    
    // backgroundView Dihapus karena digantikan popupBackgroundView dan style FXML root

    private GameController gameController;

    public void setGameController(GameController controller) {
        this.gameController = controller;
    }

    // Metode legacy setScore Dihapus/Diabaikan karena diganti setStageResult
    
    /**
     * New method to display full stage result
     */
    public void setStageResult(int score, boolean passed, String reason, int minimumScore) {
        
        // Title: Diganti menggunakan Gambar di FXML, tidak perlu diupdate di sini.

        // Update Score
        if (scoreLabel != null) {
            scoreLabel.setText("Final Score: " + score + " / " + minimumScore + " (minimum)");
            scoreLabel.setStyle("-fx-text-fill: #530000; -fx-font-size: 16px; -fx-font-weight: bold;");
        }

        // Update Reason
        if (reasonLabel != null) {
            if (!reason.isEmpty()) {
                reasonLabel.setText(reason);
                reasonLabel.setStyle(passed ? "-fx-text-fill: #105e10ff; -fx-font-size: 18px; -fx-font-weight: bold;"
                        : "-fx-text-fill: #530000; -fx-font-size: 18px; -fx-font-weight: bold;");
            } else {
                reasonLabel.setText("");
            }
        }

        // Update Status message
        if (statusLabel != null) {
            if (passed) {
                statusLabel.setText("Great job! You've successfully completed this stage!");
                statusLabel.setStyle("-fx-text-fill: #105e10ff; -fx-font-size: 14px;");
            } else {
                statusLabel.setText("Try again! You need at least " + minimumScore + " points to pass.");
                statusLabel.setStyle("-fx-text-fill: #530000; -fx-font-size: 14px;");
            }
        }
    }

    @FXML
    public void initialize() {
        // Load default images and apply effects
        try {
            // --- 1. MEMUAT BACKGROUND POP-UP (menu_popup.png) ---
            try {
                // Di FXML kita sudah menggunakan ImageView untuk background pop-up, ini optional:
                Image popupBg = new Image(getClass().getResource("/res/ui/menu_popup.png").toExternalForm());
                if (popupBackgroundView != null) {
                    popupBackgroundView.setImage(popupBg);
                }
            } catch (Exception ignored) {
                 System.err.println("Failed to load menu_popup.png for background.");
            }
            
            // --- 2. MEMUAT JUDUL (stageover.png) ---
            try {
                Image titleImg = new Image(getClass().getResource("/res/ui/stageover.png").toExternalForm());
                if (titleImageView != null) {
                    titleImageView.setImage(titleImg);
                }
            } catch (Exception e) {
                System.err.println("Failed to load stageover.png for title. Error: " + e.getMessage());
            }

            // --- 3. MEMUAT TOMBOL RESTART (play.png / playagain.png) ---
            // Kita pakai play.png karena di FXML lama Anda menggunakan itu
            Image playImg = new Image(getClass().getResource("/res/ui/play.png").toExternalForm());
            if (restartButtonView != null) {
                restartButtonView.setImage(playImg);
                setupButtonEffects(restartButtonView, playImg);
            }

            // --- 4. MEMUAT TOMBOL MAIN MENU (quit.png) ---
            Image quitImg = new Image(getClass().getResource("/res/ui/quit.png").toExternalForm());
            if (mainMenuButtonView != null) {
                mainMenuButtonView.setImage(quitImg);
                setupButtonEffects(mainMenuButtonView, quitImg);
            }

        } catch (Exception e) {
            System.err.println("Error loading Stage Over assets: " + e.getMessage());
        }
    }

    private void setupButtonEffects(ImageView button, Image original) {
        // Efek Press/Released yang lebih baik (asumsi ada file _pressed.png)
        Image pressedImage = original; // Fallback
        try {
            // Coba muat versi _pressed.png
            String originalPath = original.getUrl();
            // Menghindari NullPointerException jika URL tidak valid (meskipun sudah dicek di initialize)
            if (originalPath != null) { 
                String pressedPath = originalPath.replace(".png", "_pressed.png");
                pressedImage = new Image(pressedPath); 
            }
        } catch (Exception ignored) {} 
        
        final Image finalPressedImage = pressedImage;
        final Image finalOriginal = original;

        button.setOnMousePressed(e -> button.setImage(finalPressedImage));
        button.setOnMouseReleased(e -> button.setImage(finalOriginal));
        button.setOnMouseEntered(e -> button.getScene().setCursor(Cursor.HAND));
        button.setOnMouseExited(e -> button.getScene().setCursor(Cursor.DEFAULT));
    } 

    private void closeStageOverView() {
        if (stageOverRoot != null && stageOverRoot.getParent() != null) {
            // Hapus node ini dari parent-nya
            ((javafx.scene.layout.Pane) stageOverRoot.getParent()).getChildren().remove(stageOverRoot);
            System.out.println("DEBUG: Stage Over View berhasil dihapus dari Game Scene.");
        }
    }

    @FXML
    private void handleRestart(MouseEvent event) {
        if (gameController != null) {
            closeStageOverView();
            gameController.restartGame();
        }
    }

    @FXML
    private void handleMainMenu(MouseEvent event) {
        if (gameController != null) {
            closeStageOverView();
            gameController.quitToMainMenu();
        }
    }
}
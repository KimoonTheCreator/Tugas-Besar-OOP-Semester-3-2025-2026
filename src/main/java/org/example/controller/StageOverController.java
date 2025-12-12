package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.Cursor;

public class StageOverController {

    @FXML
    private Label titleLabel;
    @FXML
    private Label scoreLabel;

    // We can use ImageViews for buttons if we have assets, or just Buttons.
    // Since PauseMenu used ImageView buttons, I'll assume we might want to stay
    // consistent
    // or fallback to JavaFX Buttons if assets are missing.
    // For now, I'll use ImageView to match the user's style, but I'll use text if
    // images fail?
    // Actually, let's use Labels styled as buttons for simplicity and guaranteed
    // visibility if assets are missing,
    // OR ImageView if we assume we can reuse or use a generic "button" image.
    // Let's stick to ImageView to be consistent with PauseMenu, but maybe use
    // "quit" and "resume" (or similar) assets if available?
    // "restart" might not exist. I'll check assets later. For now, let's use
    // ImageView but be ready to error handle.

    @FXML
    private ImageView restartButtonView; // We'll try to load "restart.png" or reuse something
    @FXML
    private ImageView mainMenuButtonView; // Reuse "quit.png"

    private GameController gameController;

    public void setGameController(GameController controller) {
        this.gameController = controller;
    }

    public void setScore(int score) {
        if (scoreLabel != null) {
            scoreLabel.setText("Final Score: " + score);
        }
    }

    @FXML
    private ImageView backgroundView;

    @FXML
    public void initialize() {
        // Load default images if valid
        try {
            // Background
            try {
                backgroundView.setImage(new Image(getClass().getResource("/res/ui/menu_popup.png").toExternalForm()));
            } catch (Exception ignored) {
            }

            // Re-use quit button for Main Menu
            Image quitImg = new Image(getClass().getResource("/res/ui/quit.png").toExternalForm());
            mainMenuButtonView.setImage(quitImg);
            setupButtonEffects(mainMenuButtonView, quitImg);

            // Use PLAY button for Restart (Play Again)
            try {
                Image playImg = new Image(getClass().getResource("/res/ui/play.png").toExternalForm());
                restartButtonView.setImage(playImg);
                setupButtonEffects(restartButtonView, playImg);
            } catch (Exception e) {
                System.err.println("Failed to load play.png for restart button");
            }

        } catch (Exception e) {
            System.err.println("Error loading Stage Over assets: " + e.getMessage());
        }
    }

    private void setupButtonEffects(ImageView button, Image original) {
        button.setOnMouseEntered(e -> button.getScene().setCursor(Cursor.HAND));
        button.setOnMouseExited(e -> button.getScene().setCursor(Cursor.DEFAULT));
    }

    @FXML
    private void handleRestart(MouseEvent event) {
        if (gameController != null) {
            gameController.restartGame();
        }
    }

    @FXML
    private void handleMainMenu(MouseEvent event) {
        if (gameController != null) {
            gameController.quitToMainMenu();
        }
    }
}

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
    @FXML
    private Label reasonLabel;
    @FXML
    private Label statusLabel;

    @FXML
    private ImageView restartButtonView;
    @FXML
    private ImageView mainMenuButtonView;
    @FXML
    private ImageView backgroundView;

    private GameController gameController;

    public void setGameController(GameController controller) {
        this.gameController = controller;
    }

    /**
     * Legacy method for backwards compatibility
     */
    public void setScore(int score) {
        if (scoreLabel != null) {
            scoreLabel.setText("Final Score: " + score);
        }
    }

    /**
     * New method to display full stage result
     */
    public void setStageResult(int score, boolean passed, String reason, int minimumScore) {
        // Update Title based on pass/fail
        if (titleLabel != null) {
            titleLabel.setText(passed ? "🎉 STAGE PASSED!" : "💀 STAGE FAILED!");
            titleLabel.setStyle(passed ? "-fx-text-fill: #00ff00; -fx-font-size: 28px; -fx-font-weight: bold;"
                    : "-fx-text-fill: #ff4444; -fx-font-size: 28px; -fx-font-weight: bold;");
        }

        // Update Score
        if (scoreLabel != null) {
            scoreLabel.setText("Final Score: " + score + " / " + minimumScore + " (minimum)");
            scoreLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");
        }

        // Update Reason
        if (reasonLabel != null) {
            if (!reason.isEmpty()) {
                reasonLabel.setText(reason);
                reasonLabel.setStyle(passed ? "-fx-text-fill: #ffcc00; -fx-font-size: 14px;"
                        : "-fx-text-fill: #ff6666; -fx-font-size: 14px;");
            } else {
                reasonLabel.setText("");
            }
        }

        // Update Status message
        if (statusLabel != null) {
            if (passed) {
                statusLabel.setText("Great job! You've successfully completed this stage!");
                statusLabel.setStyle("-fx-text-fill: #aaffaa; -fx-font-size: 12px;");
            } else {
                statusLabel.setText("Try again! You need at least " + minimumScore + " points to pass.");
                statusLabel.setStyle("-fx-text-fill: #ffaaaa; -fx-font-size: 12px;");
            }
        }
    }

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

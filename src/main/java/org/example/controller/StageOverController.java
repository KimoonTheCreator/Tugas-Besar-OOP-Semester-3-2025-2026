package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.Cursor;
import javafx.scene.layout.AnchorPane;

public class StageOverController {

    @FXML
    private ImageView titleImageView;
    @FXML
    private ImageView popupBackgroundView;
    @FXML
    private AnchorPane stageOverRoot;
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

    private GameController gameController;

    public void setGameController(GameController controller) {
        this.gameController = controller;
    }

    public void setStageResult(int score, boolean passed, String reason, int minimumScore) {
        if (scoreLabel != null) {
            scoreLabel.setText("Final Score: " + score + " / " + minimumScore + " (minimum)");
            scoreLabel.setStyle("-fx-text-fill: #530000; -fx-font-size: 16px; -fx-font-weight: bold;");
        }

        if (reasonLabel != null) {
            if (!reason.isEmpty()) {
                reasonLabel.setText(reason);
                reasonLabel.setStyle(passed
                        ? "-fx-text-fill: #105e10ff; -fx-font-size: 18px; -fx-font-weight: bold;"
                        : "-fx-text-fill: #530000; -fx-font-size: 18px; -fx-font-weight: bold;");
            } else {
                reasonLabel.setText("");
            }
        }

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
        try {
            // Load background
            try {
                Image popupBg = new Image(getClass().getResource("/res/ui/menu_popup.png").toExternalForm());
                if (popupBackgroundView != null)
                    popupBackgroundView.setImage(popupBg);
            } catch (Exception e) {
                System.err.println("Gagal load menu_popup.png");
            }

            // Load title image
            try {
                Image titleImg = new Image(getClass().getResource("/res/ui/stageover.png").toExternalForm());
                if (titleImageView != null)
                    titleImageView.setImage(titleImg);
            } catch (Exception e) {
                System.err.println("Gagal load stageover.png: " + e.getMessage());
            }

            // Tombol restart
            Image playImg = new Image(getClass().getResource("/res/ui/play.png").toExternalForm());
            if (restartButtonView != null) {
                restartButtonView.setImage(playImg);
                setupButtonEffects(restartButtonView, playImg);
            }

            // Tombol main menu
            Image quitImg = new Image(getClass().getResource("/res/ui/quit.png").toExternalForm());
            if (mainMenuButtonView != null) {
                mainMenuButtonView.setImage(quitImg);
                setupButtonEffects(mainMenuButtonView, quitImg);
            }

        } catch (Exception e) {
            System.err.println("Error loading assets: " + e.getMessage());
        }
    }

    private void setupButtonEffects(ImageView button, Image original) {
        Image pressedImage = original;
        try {
            String originalPath = original.getUrl();
            if (originalPath != null) {
                String pressedPath = originalPath.replace(".png", "_pressed.png");
                pressedImage = new Image(pressedPath);
            }
        } catch (Exception ignored) {
        }

        final Image finalPressed = pressedImage;
        final Image finalOriginal = original;

        button.setOnMousePressed(e -> button.setImage(finalPressed));
        button.setOnMouseReleased(e -> button.setImage(finalOriginal));
        button.setOnMouseEntered(e -> button.getScene().setCursor(Cursor.HAND));
        button.setOnMouseExited(e -> button.getScene().setCursor(Cursor.DEFAULT));
    }

    private void closeStageOverView() {
        if (stageOverRoot != null && stageOverRoot.getParent() != null) {
            ((javafx.scene.layout.Pane) stageOverRoot.getParent()).getChildren().remove(stageOverRoot);
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
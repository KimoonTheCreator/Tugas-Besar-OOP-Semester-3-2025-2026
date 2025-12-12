package org.example.controller;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import org.example.model.entities.Chef;
import org.example.model.map.Direction;
import org.example.model.map.GameMap; // Pastikan pakai GameMap
import org.example.model.map.Position;
import org.example.model.map.Tile;
import org.example.model.enums.TileType;
import org.example.model.stations.*;
import org.example.view.AssetManager;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GameController {

    @FXML private GridPane mapGrid;
    @FXML private Label chefInfoLabel;
    @FXML private Label positionInfoLabel;
    @FXML private Label scoreLabel;
    @FXML private AnchorPane gameRoot;

    private boolean isPaused = false;
    private Parent pauseMenu;
    private PauseMenuController pauseMenuController;

    private final GameMap gameMap;
    private final List<Chef> chefs;
    private int activeChefIndex = 0;

    // Game Loop Timer
    private AnimationTimer gameLoop;

    // Visual References
    private final java.util.Map<Chef, ImageView> chefViews = new HashMap<>();

    // PENTING: Map untuk menyimpan akses ke ImageView Item di atas meja
    private final java.util.Map<Position, ImageView> stationItemViews = new HashMap<>();

    public GameController() {
        this.gameMap = new GameMap();
        this.chefs = new ArrayList<>();

        // Setup Chef
        List<Position> spawnPoints = gameMap.getSpawnPoints();
        Position spawn1 = (!spawnPoints.isEmpty()) ? spawnPoints.get(0) : new Position(1, 1);
        Chef chef1 = new Chef("bob", "Bob", new Position(spawn1.getX(), spawn1.getY()));
        chef1.setDirection(Direction.DOWN);
        chef1.setIsActive(true);
        chefs.add(chef1);

        Position spawn2 = (spawnPoints.size() > 1) ? spawnPoints.get(1) : new Position(2, 1);
        Chef chef2 = new Chef("kebin", "Kebin", new Position(spawn2.getX(), spawn2.getY()));
        chef2.setDirection(Direction.DOWN);
        chef2.setIsActive(false);
        chefs.add(chef2);
    }

    @FXML
    public void initialize() {
        drawInitialMap();    // Gambar Map Layering
        setupChefsViews();   // Gambar Chef
        updateInfoBar();     // UI Teks
        startGameLoop();     // Mulai Loop Animasi
        System.out.println("Game Engine Started.");
    }

    // ==========================================
    // 1. RENDERING AWAL (LAYERING STRATEGY)
    // ==========================================
    private void drawInitialMap() {
        mapGrid.getChildren().clear();

        for (int y = 0; y < gameMap.getHeight(); y++) {
            for (int x = 0; x < gameMap.getWidth(); x++) {
                Tile tile = gameMap.getTile(x, y);
                Position pos = new Position(x, y);

                StackPane stack = new StackPane();

                // LAYER 1: LANTAI
                ImageView floorView = new ImageView(AssetManager.getTileImage(TileType.FLOOR));
                resizeView(floorView);
                stack.getChildren().add(floorView);

                // LAYER 2: STATION
                if (tile.getStation() != null) {
                    ImageView stationView = new ImageView(AssetManager.getStationImage(tile.getStation()));
                    resizeView(stationView);
                    stack.getChildren().add(stationView);

                    // LAYER 3: ITEM CONTAINER
                    ImageView itemView = new ImageView();
                    resizeView(itemView);
                    stationItemViews.put(pos, itemView); // Simpan referensi
                    stack.getChildren().add(itemView);
                }
                else if (tile.getType() == TileType.WALL) {
                    ImageView wallView = new ImageView(AssetManager.getTileImage(TileType.WALL));
                    resizeView(wallView);
                    stack.getChildren().add(wallView);
                }

                mapGrid.add(stack, x, y);
            }
        }
    }

    private void resizeView(ImageView view) {
        view.setFitWidth(AssetManager.TILE_SIZE);
        view.setFitHeight(AssetManager.TILE_SIZE);
    }

    // ==========================================
    // 2. GAME LOOP
    // ==========================================
    private void startGameLoop() {
        gameLoop = new AnimationTimer() {
            long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (isPaused) return;

                if (lastUpdate == 0) { lastUpdate = now; return; }
                double deltaTime = (now - lastUpdate) / 1_000_000_000.0;
                lastUpdate = now;

                updateGameLogic(deltaTime);
                updateVisuals();
            }
        };
        gameLoop.start();
    }

    private void updateGameLogic(double deltaTime) {
        for (int y = 0; y < gameMap.getHeight(); y++) {
            for (int x = 0; x < gameMap.getWidth(); x++) {
                Tile tile = gameMap.getTile(x, y);
                if (tile.getStation() != null) {
                    // Update Oven
                    if (tile.getStation() instanceof CookingStation) {
                        ((CookingStation) tile.getStation()).update(deltaTime);
                    }
                    // Update Sink
                    else if (tile.getStation() instanceof WashingStation) {
                        ((WashingStation) tile.getStation()).update(deltaTime);
                    }
                    // --- TAMBAHAN PENTING ---
                    // Update Cutting Station
                    else if (tile.getStation() instanceof CuttingStation) {
                        ((CuttingStation) tile.getStation()).update(deltaTime);
                    }
                }
            }
        }
    }

    private void updateVisuals() {
        for (java.util.Map.Entry<Position, ImageView> entry : stationItemViews.entrySet()) {
            Position pos = entry.getKey();
            ImageView itemView = entry.getValue();

            Tile tile = gameMap.getTile(pos.getX(), pos.getY());
            // Cek null safety
            if (tile != null && tile.getStation() != null) {
                Station station = tile.getStation();
                Image newItemImg = AssetManager.getItemImage(station.getItem());
                itemView.setImage(newItemImg);
            }
        }
    }

    // ==========================================
    // 3. INPUT HANDLING
    // ==========================================

    // --- METHOD SWITCH CHEF (YANG HILANG) ---
    public void switchChef() {
        Chef oldChef = getActiveChef();
        oldChef.setIsActive(false);

        activeChefIndex = (activeChefIndex + 1) % chefs.size();
        Chef newChef = getActiveChef();
        newChef.setIsActive(true);

        // Update Visual agar chef aktif ada di paling depan (z-index)
        ImageView activeView = chefViews.get(newChef);
        if (activeView != null) {
            activeView.toFront();
        }

        System.out.println("Switched to: " + newChef.getName());
        updateInfoBar();
    }

    public void handleMoveCommand(Direction dir) {
        Chef activeChef = getActiveChef();
        int newX = activeChef.getX() + dir.getDx();
        int newY = activeChef.getY() + dir.getDy();

        if (!activeChef.getDirection().equals(dir)) {
            activeChef.setDirection(dir);
            updateChefVisual(activeChef);
            updateInfoBar(); // Update agar info bar menampilkan arah yang benar
            return;
        }

        if (gameMap.isWalkable(newX, newY) && !isOccupied(newX, newY)) {
            activeChef.move(dir);
            updateChefPosition(activeChef);
        }
        updateInfoBar();
    }

    // --- METHOD PICKUP (YANG HILANG) ---
    public void handlePickupCommand() {
        Chef activeChef = getActiveChef();
        Position facingPos = activeChef.getPosition().getFacingPosition(activeChef.getDirection());
        Tile targetTile = gameMap.getTile(facingPos.getX(), facingPos.getY());

        if (targetTile != null && targetTile.getStation() != null) {
            // Panggil .interact() untuk transfer barang
            targetTile.getStation().interact(activeChef);
            System.out.println("Pickup/Drop di " + targetTile.getStation().getName());
        }
    }

    public void handleInteractCommand() {
        Chef activeChef = getActiveChef();
        Position facingPos = activeChef.getPosition().getFacingPosition(activeChef.getDirection());
        Tile targetTile = gameMap.getTile(facingPos.getX(), facingPos.getY());

        if (targetTile != null && targetTile.getStation() != null) {
            // GANTI .interact() MENJADI .action()
            targetTile.getStation().action(activeChef);
            // System.out.println("Action at " + targetTile.getStation().getName());
        }
    }

    // ==========================================
    // 4. PAUSE & MENU HANDLING (YANG HILANG)
    // ==========================================

    public void handlePauseCommand() {
        if (isPaused) {
            resumeGame();
        } else {
            pauseGame();
        }
    }

    public void pauseGame() {
        if (isPaused) return;

        isPaused = true;
        System.out.println("Game Paused.");

        // Load Pause Menu
        if (pauseMenu == null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/view/PauseMenuView.fxml"));
                pauseMenu = loader.load();
                pauseMenuController = loader.getController();
                // Hubungkan Controller Pause dengan GameController ini
                pauseMenuController.setGameController(this);
            } catch (IOException e) {
                System.err.println("Gagal memuat PauseMenuView.fxml");
                e.printStackTrace();
                return;
            }
        }

        // Tampilkan Pause Menu di atas game
        gameRoot.getChildren().add(pauseMenu);
        AnchorPane.setTopAnchor(pauseMenu, 0.0);
        AnchorPane.setBottomAnchor(pauseMenu, 0.0);
        AnchorPane.setLeftAnchor(pauseMenu, 0.0);
        AnchorPane.setRightAnchor(pauseMenu, 0.0);
    }

    public void resumeGame() {
        if (!isPaused) return;

        gameRoot.getChildren().remove(pauseMenu);
        isPaused = false;
        // Game Loop akan otomatis jalan lagi karena di handle() ada cek if(!isPaused)
        System.out.println("Game Resumed.");
    }

    public void quitToMainMenu() {
        try {
            // Stop loop sebelum keluar
            gameLoop.stop();

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/view/MainMenuView.fxml"));
            Parent mainMenuRoot = fxmlLoader.load();

            Stage stage = (Stage) gameRoot.getScene().getWindow();
            stage.setScene(new Scene(mainMenuRoot));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ==========================================
    // 5. HELPER METHODS
    // ==========================================

    private Chef getActiveChef() { return chefs.get(activeChefIndex); }

    private boolean isOccupied(int x, int y) {
        for (int i = 0; i < chefs.size(); i++) {
            if (i != activeChefIndex && chefs.get(i).getX() == x && chefs.get(i).getY() == y) return true;
        }
        return false;
    }

    private void setupChefsViews() {
        for (Chef chef : chefs) {
            ImageView view = new ImageView(AssetManager.getChefImage(chef.getId(), chef.getDirection()));
            resizeView(view);
            chefViews.put(chef, view);
            mapGrid.add(view, chef.getX(), chef.getY());
            if (chef.getIsActive()) view.toFront();
        }
    }

    private void updateChefPosition(Chef chef) {
        ImageView view = chefViews.get(chef);
        GridPane.setColumnIndex(view, chef.getX());
        GridPane.setRowIndex(view, chef.getY());
    }

    private void updateChefVisual(Chef chef) {
        ImageView view = chefViews.get(chef);
        view.setImage(AssetManager.getChefImage(chef.getId(), chef.getDirection()));
    }

    private void updateInfoBar() {
        Chef activeChef = getActiveChef();
        chefInfoLabel.setText("Chef: " + activeChef.getName());
        positionInfoLabel.setText("Pos: " + activeChef.getPosition());
    }

}
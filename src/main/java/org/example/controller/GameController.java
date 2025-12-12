package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.paint.Color;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import javafx.animation.AnimationTimer;

import org.example.model.entities.Chef;
import org.example.model.map.Direction;
import org.example.model.map.GameMap; // Pastikan pakai GameMap
import org.example.model.map.Position;
import org.example.model.map.Tile;
import org.example.model.enums.TileType;
import org.example.model.stations.*;
import org.example.model.enums.TileType;
import org.example.model.enums.GameDifficulty;
import org.example.view.AssetManager;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.example.model.stations.Station;
import org.example.model.stations.ServingCounter;
import org.example.model.items.Plate;
import org.example.model.items.Dish;
import org.example.model.items.Ingredient;
import org.example.model.interfaces.Preparable;
import org.example.model.order.Order;

public class GameController {

    @FXML private GridPane mapGrid;
    @FXML private Label chefInfoLabel;
    @FXML private Label positionInfoLabel;
    @FXML private Label scoreLabel;
    @FXML private AnchorPane gameRoot;

    @FXML
    private VBox ordersContainer;


    private boolean isPaused = false;
    private Parent pauseMenu;
    private PauseMenuController pauseMenuController;

    private final GameMap gameMap;
    private boolean isFinished = false;
    private Parent stageOverMenu;
    private StageOverController stageOverController;
    private int score = 0;

    private final Map gameMap;
    private final List<Chef> chefs;
    private int activeChefIndex = 0;

    // Game Loop Timer
    private AnimationTimer gameLoop;

    // Visual References
    private final java.util.Map<Chef, ImageView> chefViews = new HashMap<>();

    // PENTING: Map untuk menyimpan akses ke ImageView Item di atas meja
    private final java.util.Map<Position, ImageView> stationItemViews = new HashMap<>();

    // Menggunakan java.util.Map secara eksplisit
    private final java.util.Map<Chef, ImageView> chefViews = new HashMap<>();
    private final java.util.Map<Order, VBox> orderViews = new HashMap<>();

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

    private AnimationTimer gameLoop;
    private double stageTimeRemaining;
    private GameDifficulty currentDifficulty;

    // Order System
    private List<Order> activeOrders = new ArrayList<>();
    private double timeSinceLastOrder = 0;
    private static final double ORDER_SPAWN_INTERVAL = 15.0; // Seconds

    @FXML
    public void initialize() {
        drawInitialMap();    // Gambar Map Layering
        setupChefsViews();   // Gambar Chef
        updateInfoBar();     // UI Teks
        startGameLoop();     // Mulai Loop Animasi
        System.out.println("Game Engine Started.");
        drawInitialMap();
        setupChefsViews();
        updateInfoBar();
        System.out.println("Controller Initialized. Waiting for start command...");
    }

    public void startGame(GameDifficulty difficulty) {
        this.currentDifficulty = difficulty;
        this.stageTimeRemaining = difficulty.getDurationInSeconds();
        this.score = 0;
        this.isFinished = false;

        // Reset State
        this.activeOrders.clear();
        this.orderViews.clear();
        if (ordersContainer != null)
            ordersContainer.getChildren().clear();

        this.timeSinceLastOrder = ORDER_SPAWN_INTERVAL; // Force spawn soon
        // Optional: Reset Chefs/Map if possible, for now just Timer/Score/Orders

        System.out.println("Starting Game with Difficulty: " + difficulty + " (" + stageTimeRemaining + "s)");
        startGameLoop();
    }

    // -----------------------------------------------------------
    // LOGIKA GAMBAR (VIEW UPDATE)
    // -----------------------------------------------------------

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

                Image tileImage = AssetManager.getTileImage(tile.getType());
                ImageView tileView = new ImageView(tileImage);

                tileView.setFitWidth(AssetManager.TILE_SIZE);
                tileView.setFitHeight(AssetManager.TILE_SIZE);

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
                mapGrid.add(tileView, x, y);
            }
        }
    }

    private void setupChefsViews() {
        for (Chef chef : chefs) {
            ImageView chefView = createChefView(chef.getId(), chef.getDirection());
            chefViews.put(chef, chefView);
            mapGrid.add(chefView, chef.getX(), chef.getY());
            if (chef.getIsActive()) {
                chefView.toFront();
            }
        }
    }

    private ImageView createChefView(String id, Direction dir) {
        Image chefImage = AssetManager.getChefImage(id, dir);
        ImageView chefView = new ImageView(chefImage);

        chefView.setFitWidth(AssetManager.TILE_SIZE);
        chefView.setFitHeight(AssetManager.TILE_SIZE);

        return chefView;

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

    private void updateChefPositionInView(Chef chef) {
        ImageView chefView = chefViews.get(chef);
        if (chefView != null) {
            GridPane.setColumnIndex(chefView, chef.getX());
            GridPane.setRowIndex(chefView, chef.getY());
        }
    }

    private void updateChefDirectionView(Chef chef) {
        ImageView chefView = chefViews.get(chef);
        if (chefView != null) {
            chefView.setImage(AssetManager.getChefImage(chef.getId(), chef.getDirection()));
        }
    }

    // ... (rest of methods)

    private void startGameLoop() {
        gameLoop = new AnimationTimer() {
            private long lastTime = 0;

            @Override
            public void handle(long now) {
                if (lastTime == 0) {
                    lastTime = now;
                    return;
                }

                double deltaTime = (now - lastTime) / 1_000_000_000.0; // Seconds
                lastTime = now;

                if (!isPaused) {
                    update(deltaTime);
                }
            }
        };
        gameLoop.start();
    }

    private void update(double deltaTime) {
        // 1. Update Stage Timer
        stageTimeRemaining -= deltaTime;
        if (stageTimeRemaining <= 0) {
            stageTimeRemaining = 0;
            finishStage();
        }

        // 2. Update Orders
        updateOrders(deltaTime);

        // 3. Update Stations
        updateStations(deltaTime);

        // 4. Update UI
        updateInfoBar();
    }

    private void updateOrders(double deltaTime) {
        // Spawn new order
        timeSinceLastOrder += deltaTime;
        if (timeSinceLastOrder >= ORDER_SPAWN_INTERVAL) {
            spawnNewOrder();
            timeSinceLastOrder = 0;
        }

        // Update active orders
        List<Order> expiredOrders = new ArrayList<>();
        for (Order order : activeOrders) {
            order.update(deltaTime);
            updateOrderView(order);

            if (order.isExpired()) {
                expiredOrders.add(order);
                System.out.println("Order EXPIRED: " + order.getName());
                score += order.getPenalty();
                if (score < 0)
                    score = 0;
            }
        }
        activeOrders.removeAll(expiredOrders);

        // Remove views for expired orders
        for (Order expired : expiredOrders) {
            removeOrderView(expired);
        }
    }

    private void spawnNewOrder() {
        // Real Dish Generation from RecipeManager
        org.example.model.recipe.Recipe recipe = org.example.model.recipe.RecipeManager.getInstance().getRandomRecipe();

        if (recipe != null) {
            // Recipe produces "Uncooked X", but Order wants "X"
            String targetName = recipe.getName().replace("Uncooked ", "");
            Dish dish = new Dish(targetName);
            Order newOrder = new Order(dish, 60); // 60 seconds duration
            activeOrders.add(newOrder);
            addOrderView(newOrder, recipe);
            System.out.println("New Order Spawned: " + newOrder.getName());
        }
    }

    private void updateStations(double deltaTime) {
        for (int y = 0; y < gameMap.getHeight(); y++) {
            for (int x = 0; x < gameMap.getWidth(); x++) {
                Tile tile = gameMap.getTile(x, y);
                if (tile.hasStation()) {
                    tile.getStation().update(deltaTime);
                }
            }
        }
    }

    private void updateInfoBar() {
        Chef activeChef = getActiveChef();
        String timeStr = String.format("%.0f", stageTimeRemaining);

        chefInfoLabel
                .setText(String.format("Chef: %s (%d/%d)", activeChef.getName(), activeChefIndex + 1, chefs.size()));

        StringBuilder posInfo = new StringBuilder();
        posInfo.append("Pos: ").append(activeChef.getPosition())
                .append(" | ").append(activeChef.getDirection())
                .append(" | Time: ").append(timeStr).append("s");

        // Dash Cooldown Visual
        if (!activeChef.canDash()) {
            double cooldownSec = activeChef.getDashCooldownRemaining() / 1000.0;
            posInfo.append(String.format(" | Dash: %.1fs", cooldownSec));
        } else {
            posInfo.append(" | Dash: READY");
        }

        positionInfoLabel.setText(posInfo.toString());
        scoreLabel.setText("Score: " + score);
    }

    // -----------------------------------------------------------
    // LOGIKA CONTROLLER (INPUT)
    // -----------------------------------------------------------

    private Chef getActiveChef() {
        return chefs.get(activeChefIndex);

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

    private boolean isOccupied(int x, int y) {
        for (int i = 0; i < chefs.size(); i++) {
            if (i != activeChefIndex && chefs.get(i).getX() == x && chefs.get(i).getY() == y) {
                return true;
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


        chefViews.get(newChef).toFront();

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
        if (isFinished)
            return;
        if (isPaused) {
            resumeGame();
        } else {
            pauseGame();
        }
    }

    public void pauseGame() {
        if (isPaused || isFinished)
            return; // Sudah pause atau selesai

        isPaused = true;
        // Hentikan game loop (contoh: animator.stop())
        System.out.println("Game Paused.");

        // Muat dan Tampilkan Menu Pause

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

        // Tambahkan Menu Pause ke root Game Scene
        if (!gameRoot.getChildren().contains(pauseMenu)) {
            gameRoot.getChildren().add(pauseMenu);
        }

        // Pastikan menu pause memenuhi seluruh layar

        // Tampilkan Pause Menu di atas game
        gameRoot.getChildren().add(pauseMenu);
        AnchorPane.setTopAnchor(pauseMenu, 0.0);
        AnchorPane.setBottomAnchor(pauseMenu, 0.0);
        AnchorPane.setLeftAnchor(pauseMenu, 0.0);
        AnchorPane.setRightAnchor(pauseMenu, 0.0);
    }

    public void resumeGame() {
        if (!isPaused) return; // Sudah berjalan

        gameRoot.getChildren().remove(pauseMenu);
        isPaused = false;
        // Game Loop akan otomatis jalan lagi karena di handle() ada cek if(!isPaused)
        System.out.println("Game Resumed.");
    }

    public void quitToMainMenu() {
        if (gameLoop != null) {
            gameLoop.stop();
        }

        // Logika untuk kembali ke Main Menu
        Stage stage = (Stage) gameRoot.getScene().getWindow();
        try {
            // Stop loop sebelum keluar
            gameLoop.stop();

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/view/MainMenuView.fxml"));
            stage.getScene().setRoot(fxmlLoader.load());

            // Resize back to original dimensions
            stage.setWidth(700 + 16); // +16 for window borders approx
            stage.setHeight(600 + 39); // +39 for title bar approx
            // Or simpler: use Scene size if possible, but stage includes decorations.
            // Better to force scene resize if stage.sizeToScene() works, but explicit W/H
            // is safer for now.

            // Re-centering is tricky without knowing screen bounds,
            // generally just resizing is enough as it keeps top-left pos.
            // But let's set the Scene size first.
            stage.getScene().getWindow().setWidth(700);
            stage.getScene().getWindow().setHeight(630); // 600 + decoration
            stage.centerOnScreen();

            isPaused = false;
            isFinished = false;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void finishStage() {
        if (isFinished)
            return;
        isFinished = true;

        // Stop Loop
        if (gameLoop != null)
            gameLoop.stop();

        System.out.println("STAGE OVER! Final Score: " + score);

        // Load View if needed
        if (stageOverMenu == null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/view/StageOverView.fxml"));
                stageOverMenu = loader.load();
                stageOverController = loader.getController();
                stageOverController.setGameController(this);
            } catch (IOException e) {
                System.err.println("Failed to load StageOverView.fxml");
                e.printStackTrace();
                return;
            }
        }

        // Update Score
        if (stageOverController != null) {
            stageOverController.setScore(score);
        }

        // Show View
        if (!gameRoot.getChildren().contains(stageOverMenu)) {
            gameRoot.getChildren().add(stageOverMenu);
        }

        AnchorPane.setTopAnchor(stageOverMenu, 0.0);
        AnchorPane.setBottomAnchor(stageOverMenu, 0.0);
        AnchorPane.setLeftAnchor(stageOverMenu, 0.0);
        AnchorPane.setRightAnchor(stageOverMenu, 0.0);
    }

    public void restartGame() {
        // Hide Stage Over UI
        if (stageOverMenu != null) {
            gameRoot.getChildren().remove(stageOverMenu);
        }

        System.out.println("Restarting Game...");
        // Re-start with same difficulty
        if (currentDifficulty != null) {
            startGame(currentDifficulty);
        } else {
            // Fallback
            startGame(GameDifficulty.EASY);
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
    public void handleMoveCommand(Direction dir) {
        if (isPaused)
            return;

        Chef activeChef = getActiveChef();
        int newX = activeChef.getX() + dir.getDx();
        int newY = activeChef.getY() + dir.getDy();

        if (!activeChef.getDirection().equals(dir)) {
            activeChef.setDirection(dir);
            updateChefDirectionView(activeChef);
            updateInfoBar();
            return;
        }

        if (gameMap.isWalkable(newX, newY) && !isOccupied(newX, newY)) {
            activeChef.move(dir);
            updateChefPositionInView(activeChef);
            updateInfoBar();
        }
    }

    public void handleInteractCommand() {
        if (isPaused)
            return;

        Chef activeChef = getActiveChef();
        // Dapatkan tile di hadapan chef
        Position facingPos = activeChef.getPosition().getFacingPosition(activeChef.getDirection());

        if (gameMap.isValidPosition(facingPos.getX(), facingPos.getY())) {
            Tile tile = gameMap.getTile(facingPos.getX(), facingPos.getY());

            // Jika ada station, lakukan ACTION (Proses: Cut, Wash)
            if (tile.hasStation()) {
                tile.getStation().action(activeChef);
                System.out.println(activeChef.getName() + " melakukan action di " + tile.getStation().getName());
            } else {
                System.out.println(activeChef.getName() + " interacted with empty tile at " + facingPos);
            }
        }
        updateInfoBar();
        chefInfoLabel.setText("Chef: " + activeChef.getName());
        positionInfoLabel.setText("Pos: " + activeChef.getPosition());
    }


    public void handlePickupCommand() {
        if (isPaused)
            return;

        Chef activeChef = getActiveChef();
        Position facingPos = activeChef.getPosition().getFacingPosition(activeChef.getDirection());

        if (gameMap.isValidPosition(facingPos.getX(), facingPos.getY())) {
            Tile tile = gameMap.getTile(facingPos.getX(), facingPos.getY());

            // Jika ada station, lakukan INTERACT (Transfer Item: Pick Up / Drop)
            if (tile.hasStation()) {
                Station s = tile.getStation();
                s.interact(activeChef);

                // CHECK SERVING
                if (s instanceof ServingCounter) {
                    ServingCounter sc = (ServingCounter) s;
                    if (sc.getServedPlate() != null) {
                        validateServedDish(sc.getServedPlate());
                        sc.clearServedPlate();
                    }
                }

                System.out.println(activeChef.getName() + " transfer item di " + s.getName());
            } else {
                // Logic drop di lantai jika valid (optional, trash usually station)
                System.out.println(activeChef.getName() + " attempted pickup/drop at " + facingPos);
            }
        }
        updateInfoBar();
    }

    public void handleDashCommand() {
        if (isPaused)
            return;

        Chef activeChef = getActiveChef();
        if (activeChef.canDash()) {
            Direction dir = activeChef.getDirection();
            int dashDistance = 3;

            // Move multiple times immediately
            for (int i = 0; i < dashDistance; i++) {
                int newX = activeChef.getX() + dir.getDx();
                int newY = activeChef.getY() + dir.getDy();

                if (gameMap.isWalkable(newX, newY) && !isOccupied(newX, newY)) {
                    activeChef.move(dir);
                    updateChefPositionInView(activeChef);
                } else {
                    break;
                }
            }
            activeChef.startDash();
            System.out.println(activeChef.getName() + " melakukan DASH!");
            updateInfoBar();
        } else {
            System.out.println("Dash cooldown: " + activeChef.getDashCooldownRemaining() + "ms");
        }
    }

    private void validateServedDish(Plate plate) {
        // Create Dish from Plate
        Dish servedDish = plate.createDish("Served Dish");
        boolean matched = false;

        // Simple matching logic: Check if any order is satisfied
        if (servedDish.getComponents().isEmpty()) {
            System.out.println("Dish Kosong!");
            return;
        }

        for (Order order : activeOrders) {
            // Basic Check (For now accepting if ingredients exist, matching First Order)
            // Ideally compare Ingredients or Name
            if (!matched) {
                // Check match name? Or ingredients?
                // For now, assume simple logic
                matched = true;
                activeOrders.remove(order);
                removeOrderView(order);
                score += 20; // Example score increment
                System.out.println("Order SUCCESS: " + order.getName());
                break;
            }
        }

        if (!matched) {
            System.out.println("Order FAILED: Incorrect Dish");
            // TODO: Apply Penalty
        }
    }

    // -----------------------------------------------------------
    // ORDER VIEW HELPERS
    // -----------------------------------------------------------

    private void addOrderView(Order order, org.example.model.recipe.Recipe recipe) {
        if (ordersContainer == null)
            return;

        VBox orderBox = new VBox(5);
        // Darker theme, better border, shadow effect simulated by color
        orderBox.setStyle(
                "-fx-background-color: rgba(30, 30, 30, 0.9); -fx-padding: 10; -fx-background-radius: 10; -fx-border-color: #ffd700; -fx-border-radius: 10; -fx-border-width: 2;");
        orderBox.setPrefWidth(160); // Widen for full text
        orderBox.setAlignment(Pos.CENTER_LEFT);

        // 1. Order Name
        Label nameLabel = new Label(order.getName());
        nameLabel.setWrapText(true);
        nameLabel.setStyle("-fx-text-fill: #ffd700; -fx-font-weight: bold; -fx-font-size: 16px;"); // Larger header
        orderBox.getChildren().add(nameLabel);

        // 2. Ingredients List
        if (recipe != null) {
            List<String> ingNames = new ArrayList<>();
            for (String key : recipe.getComponents().keySet()) {
                ingNames.add(key);
            }
            String ingText = "(" + String.join(", ", ingNames) + ")";

            Label ingLabel = new Label(ingText);
            ingLabel.setWrapText(true);
            ingLabel.setStyle("-fx-text-fill: white; -fx-font-size: 11px; -fx-font-style: italic;");
            orderBox.getChildren().add(ingLabel);
        }

        // 3. Timer Bar
        ProgressBar timerBar = new ProgressBar(1.0);
        timerBar.setPrefWidth(140); // Match box width
        timerBar.setPrefHeight(15); // Taller bar
        timerBar.setStyle(
                "-fx-accent: #00ff00; -fx-control-inner-background: #555555; -fx-text-box-border: transparent;");

        orderBox.getChildren().add(timerBar);

        ordersContainer.getChildren().add(orderBox);
        orderViews.put(order, orderBox);
    }

    private void updateOrderView(Order order) {
        VBox view = orderViews.get(order);
        if (view == null)
            return;

        // Update Timer Bar
        // Index 0: Title, Index 1: Ingredients, Index 2: ProgressBar
        if (view.getChildren().size() > 2) {
            ProgressBar bar = (ProgressBar) view.getChildren().get(2);
            double progress = order.getRemainingTime() / order.getTime();
            bar.setProgress(progress);

            // Color Change based on urgency
            if (progress < 0.25) {
                bar.setStyle("-fx-accent: red;");
            } else if (progress < 0.5) {
                bar.setStyle("-fx-accent: yellow;");
            } else {
                bar.setStyle("-fx-accent: green;");
            }
        }
    }

    private void removeOrderView(Order order) {
        VBox view = orderViews.remove(order);
        if (view != null && ordersContainer != null) {
            ordersContainer.getChildren().remove(view);
        }
    }

}
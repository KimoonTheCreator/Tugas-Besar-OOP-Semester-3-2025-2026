package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.animation.AnimationTimer;

import org.example.model.entities.Chef;
import org.example.model.map.Direction;
import org.example.model.map.Map;
import org.example.model.map.Position;
import org.example.model.map.Tile;
import org.example.model.enums.TileType;
import org.example.model.enums.GameDifficulty;
import org.example.view.AssetManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;

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

/**
 * Controller utama untuk GameView.fxml.
 */
public class GameController {

    @FXML
    private GridPane mapGrid;

    @FXML
    private Label chefInfoLabel;

    @FXML
    private Label positionInfoLabel;

    @FXML
    private Label scoreLabel;

    @FXML
    private AnchorPane gameRoot;

    private boolean isPaused = false;
    private Parent pauseMenu;
    private PauseMenuController pauseMenuController;

    private final Map gameMap;
    private final List<Chef> chefs;
    private int activeChefIndex = 0;

    // Menggunakan java.util.Map secara eksplisit
    private final java.util.Map<Chef, ImageView> chefViews = new HashMap<>();

    public GameController() {
        // Inisialisasi Model
        this.gameMap = new Map();
        this.chefs = new ArrayList<>();

        List<Position> spawnPoints = gameMap.getSpawnPoints();

        // Chef 1 (Bob)
        Position spawn1 = spawnPoints.size() > 0 ? spawnPoints.get(0) : new Position(1, 1);
        Chef chef1 = new Chef("bob", "Bob", new Position(spawn1.getX(), spawn1.getY()));
        chef1.setDirection(Direction.DOWN);
        chef1.setIsActive(true);
        chefs.add(chef1);

        // Chef 2 (Kebin)
        Position spawn2 = spawnPoints.size() > 1 ? spawnPoints.get(1) : new Position(2, 1);
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
        drawInitialMap();
        setupChefsViews();
        updateInfoBar();
        System.out.println("Controller Initialized. Waiting for start command...");
    }

    public void startGame(GameDifficulty difficulty) {
        this.currentDifficulty = difficulty;
        this.stageTimeRemaining = difficulty.getDurationInSeconds();
        System.out.println("Starting Game with Difficulty: " + difficulty + " (" + stageTimeRemaining + "s)");
        startGameLoop();
    }

    // -----------------------------------------------------------
    // LOGIKA GAMBAR (VIEW UPDATE)
    // -----------------------------------------------------------

    private void drawInitialMap() {
        for (int y = 0; y < gameMap.getHeight(); y++) {
            for (int x = 0; x < gameMap.getWidth(); x++) {
                Tile tile = gameMap.getTile(x, y);

                Image tileImage = AssetManager.getTileImage(tile.getType());
                ImageView tileView = new ImageView(tileImage);

                tileView.setFitWidth(AssetManager.TILE_SIZE);
                tileView.setFitHeight(AssetManager.TILE_SIZE);

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
            // TODO: Trigger Game Over Event
            System.out.println("GAME OVER - Time's Up!");
            pauseGame();
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
            if (order.isExpired()) {
                expiredOrders.add(order);
                System.out.println("Order EXPIRED: " + order.getName());
                // TODO: Apply Penalty
            }
        }
        activeOrders.removeAll(expiredOrders);
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
        // TODO: Implementasi logika update score
    }

    // -----------------------------------------------------------
    // LOGIKA CONTROLLER (INPUT)
    // -----------------------------------------------------------

    private Chef getActiveChef() {
        return chefs.get(activeChefIndex);
    }

    private boolean isOccupied(int x, int y) {
        for (int i = 0; i < chefs.size(); i++) {
            if (i != activeChefIndex && chefs.get(i).getX() == x && chefs.get(i).getY() == y) {
                return true;
            }
        }
        return false;
    }

    public void switchChef() {
        Chef oldChef = getActiveChef();
        oldChef.setIsActive(false);

        activeChefIndex = (activeChefIndex + 1) % chefs.size();
        Chef newChef = getActiveChef();
        newChef.setIsActive(true);

        chefViews.get(newChef).toFront();

        System.out.println("Switched to: " + newChef.getName());
        updateInfoBar();
    }

    public void handlePauseCommand() {
        if (isPaused) {
            resumeGame();
        } else {
            pauseGame();
        }
    }

    public void pauseGame() {
        if (isPaused)
            return; // Sudah pause

        isPaused = true;
        // Hentikan game loop (contoh: animator.stop())
        System.out.println("Game Paused.");

        // Muat dan Tampilkan Menu Pause
        if (pauseMenu == null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/view/PauseMenuView.fxml"));
                pauseMenu = loader.load();
                pauseMenuController = loader.getController();
                pauseMenuController.setGameController(this); // Beri referensi balik
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
        AnchorPane.setTopAnchor(pauseMenu, 0.0);
        AnchorPane.setBottomAnchor(pauseMenu, 0.0);
        AnchorPane.setLeftAnchor(pauseMenu, 0.0);
        AnchorPane.setRightAnchor(pauseMenu, 0.0);
    }

    public void resumeGame() {
        if (!isPaused)
            return; // Sudah berjalan

        // Hapus Menu Pause
        gameRoot.getChildren().remove(pauseMenu);

        isPaused = false;
        // Lanjutkan game loop (contoh: animator.start())
        System.out.println("Game Resumed.");
    }

    public void quitToMainMenu() {
        if (gameLoop != null) {
            gameLoop.stop();
        }

        // Logika untuk kembali ke Main Menu
        Stage stage = (Stage) gameRoot.getScene().getWindow();
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/view/MainMenuView.fxml"));
            stage.getScene().setRoot(fxmlLoader.load());

            isPaused = false;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

                // Cek collision per step
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
                matched = true;
                activeOrders.remove(order);
                // TODO: Add Score
                System.out.println("Order SUCCESS: " + order.getName());
                break;
            }
        }

        if (!matched) {
            System.out.println("Order FAILED: Incorrect Dish");
            // TODO: Apply Penalty
        }
    }
}
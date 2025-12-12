package org.example.controller;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Pos;

import org.example.model.entities.Chef;
import org.example.model.map.Direction;
import org.example.model.map.GameMap;
import org.example.model.map.Position;
import org.example.model.map.Tile;
import org.example.model.enums.TileType;
import org.example.model.enums.GameDifficulty;
import org.example.model.stations.*;
import org.example.model.items.Plate;
import org.example.model.items.Dish;
import org.example.model.order.Order;
import org.example.view.AssetManager;
import org.example.controller.PauseMenuController;
import org.example.controller.StageOverController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameController {

    // --- FXML UI COMPONENTS ---
    @FXML private GridPane mapGrid;
    @FXML private Label chefInfoLabel;
    @FXML private Label positionInfoLabel;
    @FXML private Label scoreLabel;
    @FXML private AnchorPane gameRoot;
    @FXML private VBox ordersContainer;

    // --- GAME STATE ---
    private final GameMap gameMap;
    private final List<Chef> chefs;
    private int activeChefIndex = 0;
    private boolean isPaused = false;
    private boolean isFinished = false;
    private int score = 0;

    // --- DIFFICULTY & TIMER ---
    private double stageTimeRemaining;
    private GameDifficulty currentDifficulty;

    // --- ORDER SYSTEM ---
    private List<Order> activeOrders = new ArrayList<>();
    private double timeSinceLastOrder = 0;
    private static final double ORDER_SPAWN_INTERVAL = 15.0; // Seconds

    // --- ENGINE & VISUALS ---
    private AnimationTimer gameLoop;
    // Map untuk visual Chef
    private final Map<Chef, ImageView> chefViews = new HashMap<>();
    // Map untuk visual Item di atas meja (Station)
    private final Map<Position, ImageView> stationItemViews = new HashMap<>();
    // Map untuk visual Order UI
    private final Map<Order, VBox> orderViews = new HashMap<>();

    // --- SUB-CONTROLLERS ---
    private Parent pauseMenu;
    private PauseMenuController pauseMenuController;
    private Parent stageOverMenu;
    private StageOverController stageOverController;

    // =========================================================
    // 1. INITIALIZATION
    // =========================================================

    public GameController() {
        this.gameMap = new GameMap();
        this.chefs = new ArrayList<>();

        // Setup Chefs dari Spawn Point
        List<Position> spawnPoints = gameMap.getSpawnPoints();

        // Chef 1 (Bob)
        Position spawn1 = (!spawnPoints.isEmpty()) ? spawnPoints.get(0) : new Position(1, 1);
        Chef chef1 = new Chef("bob", "Bob", new Position(spawn1.getX(), spawn1.getY()));
        chef1.setDirection(Direction.DOWN);
        chef1.setIsActive(true);
        chefs.add(chef1);

        // Chef 2 (Kebin)
        Position spawn2 = (spawnPoints.size() > 1) ? spawnPoints.get(1) : new Position(2, 1);
        Chef chef2 = new Chef("kebin", "Kebin", new Position(spawn2.getX(), spawn2.getY()));
        chef2.setDirection(Direction.DOWN);
        chef2.setIsActive(false);
        chefs.add(chef2);
    }

    @FXML
    public void initialize() {
        System.out.println("Initializing Game Controller...");
        drawInitialMap();    // Gambar Tile & Station
        setupChefsViews();   // Gambar Chef
        updateInfoBar();     // UI Teks Awal
        System.out.println("Game Ready. Waiting for Start command.");
    }

    public void startGame(GameDifficulty difficulty) {
        this.currentDifficulty = difficulty;
        this.stageTimeRemaining = difficulty.getDurationInSeconds();
        this.score = 0;
        this.isFinished = false;
        this.isPaused = false;

        // Reset Order State
        this.activeOrders.clear();
        this.orderViews.clear();
        if (ordersContainer != null) ordersContainer.getChildren().clear();
        this.timeSinceLastOrder = ORDER_SPAWN_INTERVAL - 3.0; // Spawn order pertama dalam 3 detik

        System.out.println("Starting Game: " + difficulty + " (" + stageTimeRemaining + "s)");
        startGameLoop();
    }

    // =========================================================
    // 2. MAIN GAME LOOP
    // =========================================================

    private void startGameLoop() {
        if (gameLoop != null) gameLoop.stop();

        gameLoop = new AnimationTimer() {
            private long lastTime = 0;

            @Override
            public void handle(long now) {
                if (isPaused || isFinished) return;

                if (lastTime == 0) {
                    lastTime = now;
                    return;
                }

                double deltaTime = (now - lastTime) / 1_000_000_000.0; // Convert to seconds
                lastTime = now;

                // --- UPDATE LOGIC ---
                updateGameLogic(deltaTime);

                // --- UPDATE VISUALS ---
                updateVisuals();
            }
        };
        gameLoop.start();
    }

    private void updateGameLogic(double deltaTime) {
        // 1. Stage Timer
        stageTimeRemaining -= deltaTime;
        if (stageTimeRemaining <= 0) {
            stageTimeRemaining = 0;
            finishStage();
        }

        // 2. Chef Cooldowns (Dash, etc)
        for(Chef c : chefs) {
            c.update(deltaTime);
        }

        // 3. Orders Logic
        updateOrders(deltaTime);

        // 4. Stations Logic (Cooking, Washing, Cutting)
        for (int y = 0; y < gameMap.getHeight(); y++) {
            for (int x = 0; x < gameMap.getWidth(); x++) {
                Tile tile = gameMap.getTile(x, y);
                if (tile.hasStation()) {
                    Station station = tile.getStation();

                    // Update spesifik per tipe station
                    if (station instanceof CookingStation) {
                        ((CookingStation) station).update(deltaTime);
                    } else if (station instanceof WashingStation) {
                        ((WashingStation) station).update(deltaTime);
                    } else if (station instanceof CuttingStation) {
                        ((CuttingStation) station).update(deltaTime);
                    }
                }
            }
        }

        // 5. Update UI Text
        updateInfoBar();
    }

    private void updateVisuals() {
        // Update Items on Stations
        for (Map.Entry<Position, ImageView> entry : stationItemViews.entrySet()) {
            Position pos = entry.getKey();
            ImageView itemView = entry.getValue();

            Tile tile = gameMap.getTile(pos.getX(), pos.getY());
            if (tile != null && tile.getStation() != null) {
                Station station = tile.getStation();
                // Ambil gambar item jika ada, jika null set null
                Image newItemImg = AssetManager.getItemImage(station.getItem());
                itemView.setImage(newItemImg);
            }
        }

        // Update Visual Chef (Posisi smooth jika perlu, tapi disini grid based)
        for (Chef chef : chefs) {
            updateChefPositionInView(chef);
        }
    }

    // =========================================================
    // 3. INPUT HANDLING
    // =========================================================

    public void handleMoveCommand(Direction dir) {
        if (isPaused || isFinished) return;

        Chef activeChef = getActiveChef();
        int newX = activeChef.getX() + dir.getDx();
        int newY = activeChef.getY() + dir.getDy();

        // 1. Ganti Arah
        if (!activeChef.getDirection().equals(dir)) {
            activeChef.setDirection(dir);
            updateChefDirectionView(activeChef);
            return;
        }

        // 2. Cek Collision & Move
        if (gameMap.isWalkable(newX, newY) && !isOccupied(newX, newY)) {
            activeChef.move(dir);
            updateChefPositionInView(activeChef);
        }
    }

    public void handleDashCommand() {
        if (isPaused || isFinished) return;

        Chef activeChef = getActiveChef();
        if (activeChef.canDash()) {
            Direction dir = activeChef.getDirection();
            int dashDistance = 3;

            // Dash logic: Instant move multiple tiles
            for (int i = 0; i < dashDistance; i++) {
                int newX = activeChef.getX() + dir.getDx();
                int newY = activeChef.getY() + dir.getDy();

                if (gameMap.isWalkable(newX, newY) && !isOccupied(newX, newY)) {
                    activeChef.move(dir);
                } else {
                    break;
                }
            }
            activeChef.startDash();
            updateChefPositionInView(activeChef);
            System.out.println(activeChef.getName() + " DASHED!");
        }
    }

    // INTERACT = KEY 'V' (Pick Up / Drop Item)
    public void handlePickupCommand() {
        if (isPaused || isFinished) return;

        Chef activeChef = getActiveChef();
        Position facingPos = activeChef.getPosition().getFacingPosition(activeChef.getDirection());

        if (gameMap.isValidPosition(facingPos.getX(), facingPos.getY())) {
            Tile tile = gameMap.getTile(facingPos.getX(), facingPos.getY());

            if (tile.hasStation()) {
                Station s = tile.getStation();
                s.interact(activeChef); // Logic Pick/Drop ada di Station.interact

                // Khusus Serving Counter: Cek Validasi Makanan
                if (s instanceof ServingCounter) {
                    ServingCounter sc = (ServingCounter) s;
                    if (sc.getServedPlate() != null) {
                        validateServedDish(sc.getServedPlate());
                        sc.clearServedPlate();
                    }
                }
            }
        }
    }

    // ACTION = KEY 'C' (Chop / Wash / Cook Process)
    public void handleInteractCommand() {
        if (isPaused || isFinished) return;

        Chef activeChef = getActiveChef();
        Position facingPos = activeChef.getPosition().getFacingPosition(activeChef.getDirection());

        if (gameMap.isValidPosition(facingPos.getX(), facingPos.getY())) {
            Tile tile = gameMap.getTile(facingPos.getX(), facingPos.getY());

            if (tile.hasStation()) {
                // Panggil method action() untuk memproses item (misal: potong)
                tile.getStation().action(activeChef);
            }
        }
    }

    public void switchChef() {
        if (isPaused || isFinished) return;

        Chef oldChef = getActiveChef();
        oldChef.setIsActive(false);

        activeChefIndex = (activeChefIndex + 1) % chefs.size();

        Chef newChef = getActiveChef();
        newChef.setIsActive(true);

        // Update Z-Index visual agar chef aktif di depan
        ImageView activeView = chefViews.get(newChef);
        if (activeView != null) {
            activeView.toFront();
        }

        System.out.println("Switched to: " + newChef.getName());
        updateInfoBar();
    }

    // =========================================================
    // 4. ORDER LOGIC
    // =========================================================

    private void updateOrders(double deltaTime) {
        // Spawn Order
        timeSinceLastOrder += deltaTime;
        if (timeSinceLastOrder >= ORDER_SPAWN_INTERVAL) {
            spawnNewOrder();
            timeSinceLastOrder = 0;
        }

        // Update Active Orders
        List<Order> expiredOrders = new ArrayList<>();
        for (Order order : activeOrders) {
            order.update(deltaTime);
            updateOrderView(order);

            if (order.isExpired()) {
                expiredOrders.add(order);
                score += order.getPenalty(); // Kurangi skor
                if (score < 0) score = 0;
                System.out.println("Order EXPIRED: " + order.getName());
            }
        }

        // Remove Expired
        activeOrders.removeAll(expiredOrders);
        for (Order expired : expiredOrders) {
            removeOrderView(expired);
        }
    }

    private void spawnNewOrder() {
        // Ambil resep acak dari RecipeManager (Asumsi class ini ada)
        org.example.model.recipe.Recipe recipe = org.example.model.recipe.RecipeManager.getInstance().getRandomRecipe();

        if (recipe != null) {
            String targetName = recipe.getName().replace("Uncooked ", "");
            Dish dish = new Dish(targetName);

            // Buat order baru
            Order newOrder = new Order(dish, 60); // 60 detik durasi
            activeOrders.add(newOrder);
            addOrderView(newOrder, recipe);
        }
    }

    private void validateServedDish(Plate plate) {
        Dish servedDish = plate.createDish("Served Dish");
        boolean matched = false;

        // Logic sederhana: Mencocokkan dengan order pertama yang sesuai
        if (servedDish.getComponents().isEmpty()) return;

        for (Order order : activeOrders) {
            // TODO: Implementasi deep check ingredients
            // Disini asumsi sederhana match nama atau komponen
            // Logic validasi resep harusnya ada di Order.matches(Dish)

            // Simulasi match sukses:
            matched = true;
            activeOrders.remove(order);
            removeOrderView(order);
            score += 20; // Tambah skor
            System.out.println("Order SUCCESS: " + order.getName());
            break;
        }

        if (!matched) {
            System.out.println("Order FAILED: Salah menu!");
            score -= 10;
        }
    }

    // =========================================================
    // 5. RENDERING HELPERS
    // =========================================================

    private void drawInitialMap() {
        mapGrid.getChildren().clear();

        for (int y = 0; y < gameMap.getHeight(); y++) {
            for (int x = 0; x < gameMap.getWidth(); x++) {
                Tile tile = gameMap.getTile(x, y);
                Position pos = new Position(x, y);

                StackPane stack = new StackPane();

                // Layer 1: Floor
                ImageView floorView = new ImageView(AssetManager.getTileImage(TileType.FLOOR));
                resizeView(floorView);
                stack.getChildren().add(floorView);

                // Layer 2: Station / Wall
                if (tile.getStation() != null) {
                    ImageView stationView = new ImageView(AssetManager.getStationImage(tile.getStation()));
                    resizeView(stationView);
                    stack.getChildren().add(stationView);

                    // Layer 3: Item on Station (Empty at start)
                    ImageView itemView = new ImageView();
                    resizeView(itemView);
                    stationItemViews.put(pos, itemView);
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
        resizeView(chefView);
        return chefView;
    }

    private void resizeView(ImageView view) {
        view.setFitWidth(AssetManager.TILE_SIZE);
        view.setFitHeight(AssetManager.TILE_SIZE);
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

    // =========================================================
    // 6. UI ORDER HELPERS
    // =========================================================

    private void addOrderView(Order order, org.example.model.recipe.Recipe recipe) {
        if (ordersContainer == null) return;

        VBox orderBox = new VBox(5);
        orderBox.setStyle("-fx-background-color: rgba(30, 30, 30, 0.9); -fx-padding: 10; -fx-background-radius: 10; -fx-border-color: #ffd700; -fx-border-width: 2;");
        orderBox.setPrefWidth(160);
        orderBox.setAlignment(Pos.CENTER_LEFT);

        Label nameLabel = new Label(order.getName());
        nameLabel.setStyle("-fx-text-fill: #ffd700; -fx-font-weight: bold; -fx-font-size: 14px;");
        orderBox.getChildren().add(nameLabel);

        ProgressBar timerBar = new ProgressBar(1.0);
        timerBar.setPrefWidth(140);
        orderBox.getChildren().add(timerBar);

        ordersContainer.getChildren().add(orderBox);
        orderViews.put(order, orderBox);
    }

    private void updateOrderView(Order order) {
        VBox view = orderViews.get(order);
        if (view == null) return;

        if (view.getChildren().size() > 1) {
            ProgressBar bar = (ProgressBar) view.getChildren().get(1);
            double progress = order.getRemainingTime() / order.getTime();
            bar.setProgress(progress);

            // Ubah warna bar sesuai sisa waktu
            if (progress < 0.25) bar.setStyle("-fx-accent: red;");
            else if (progress < 0.5) bar.setStyle("-fx-accent: yellow;");
            else bar.setStyle("-fx-accent: #00ff00;");
        }
    }

    private void removeOrderView(Order order) {
        VBox view = orderViews.remove(order);
        if (view != null && ordersContainer != null) {
            ordersContainer.getChildren().remove(view);
        }
    }

    // =========================================================
    // 7. PAUSE & SYSTEM
    // =========================================================

    public void handlePauseCommand() {
        if (isFinished) return;
        if (isPaused) resumeGame();
        else pauseGame();
    }

    public void pauseGame() {
        if (isPaused) return;
        isPaused = true;

        // Load Pause Menu jika belum ada
        if (pauseMenu == null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/view/PauseMenuView.fxml"));
                pauseMenu = loader.load();
                pauseMenuController = loader.getController();
                pauseMenuController.setGameController(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (!gameRoot.getChildren().contains(pauseMenu)) {
            gameRoot.getChildren().add(pauseMenu);
            AnchorPane.setTopAnchor(pauseMenu, 0.0);
            AnchorPane.setBottomAnchor(pauseMenu, 0.0);
            AnchorPane.setLeftAnchor(pauseMenu, 0.0);
            AnchorPane.setRightAnchor(pauseMenu, 0.0);
        }
    }

    public void resumeGame() {
        if (!isPaused) return;
        gameRoot.getChildren().remove(pauseMenu);
        isPaused = false;
        // Game Loop handle(now) otomatis jalan lagi karena isPaused false
    }

    public void finishStage() {
        if (isFinished) return;
        isFinished = true;

        System.out.println("STAGE OVER! Score: " + score);

        if (stageOverMenu == null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/view/StageOverView.fxml"));
                stageOverMenu = loader.load();
                stageOverController = loader.getController();
                stageOverController.setGameController(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (stageOverController != null) stageOverController.setScore(score);

        if (!gameRoot.getChildren().contains(stageOverMenu)) {
            gameRoot.getChildren().add(stageOverMenu);
            AnchorPane.setTopAnchor(stageOverMenu, 0.0);
            AnchorPane.setBottomAnchor(stageOverMenu, 0.0);
            AnchorPane.setLeftAnchor(stageOverMenu, 0.0);
            AnchorPane.setRightAnchor(stageOverMenu, 0.0);
        }
    }

    public void restartGame() {
        if (stageOverMenu != null) gameRoot.getChildren().remove(stageOverMenu);
        startGame(currentDifficulty != null ? currentDifficulty : GameDifficulty.EASY);
    }

    public void quitToMainMenu() {
        if (gameLoop != null) gameLoop.stop();
        Stage stage = (Stage) gameRoot.getScene().getWindow();
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/view/MainMenuView.fxml"));
            stage.getScene().setRoot(fxmlLoader.load());
            stage.setWidth(700);
            stage.setHeight(630);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // =========================================================
    // 8. HELPERS
    // =========================================================

    private Chef getActiveChef() {
        return chefs.get(activeChefIndex);
    }

    private boolean isOccupied(int x, int y) {
        for (int i = 0; i < chefs.size(); i++) {
            // Cek chef lain selain diri sendiri
            if (i != activeChefIndex && chefs.get(i).getX() == x && chefs.get(i).getY() == y) {
                return true;
            }
        }
        return false;
    }

    private void updateInfoBar() {
        Chef activeChef = getActiveChef();
        String timeStr = String.format("%.0f", stageTimeRemaining);

        chefInfoLabel.setText(String.format("Chef: %s (%d/%d)", activeChef.getName(), activeChefIndex + 1, chefs.size()));

        StringBuilder posInfo = new StringBuilder();
        posInfo.append("Pos: ").append(activeChef.getPosition())
                .append(" | Time: ").append(timeStr).append("s");

        if (!activeChef.canDash()) {
            double cooldownSec = activeChef.getDashCooldownRemaining() / 1000.0;
            posInfo.append(String.format(" | Dash: %.1fs", cooldownSec));
        } else {
            posInfo.append(" | Dash: READY");
        }

        positionInfoLabel.setText(posInfo.toString());
        scoreLabel.setText("Score: " + score);
    }
}

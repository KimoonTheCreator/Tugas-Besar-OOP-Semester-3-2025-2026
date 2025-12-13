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
import javafx.scene.layout.StackPane; // Explicit import just in case
import javafx.stage.Stage;
import javafx.geometry.Pos;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;
import java.util.HashSet;
import java.util.Set;

import org.example.model.entities.Chef;
import org.example.model.map.Direction;
import org.example.model.map.GameMap;
import org.example.model.map.Position;
import org.example.model.map.Tile;
import org.example.model.enums.TileType;
import org.example.model.enums.GameDifficulty;
import org.example.model.items.Pizza;
import org.example.model.items.Ingredient;
import org.example.model.items.Plate;
import org.example.model.items.Dish;
import org.example.model.order.Order;
import org.example.model.recipe.RecipeManager;
import org.example.model.stations.*;
import org.example.model.stations.Station;
import org.example.model.stations.CookingStation;
import org.example.model.stations.WashingStation;
import org.example.model.stations.CuttingStation;
import org.example.model.stations.ServingCounter;
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
    @FXML
    private VBox ordersContainer;

    // --- INPUT STATE ---
    private final Set<KeyCode> activeKeys = new HashSet<>();

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

    // --- STAGE END CONDITIONS ---
    private int consecutiveFailedOrders = 0;
    private String stageEndReason = "";
    private boolean stagePassed = false;

    // --- ENGINE & VISUALS ---
    private AnimationTimer gameLoop;
    // Map untuk visual Chef (Sekarang StackPane biar bisa ada Item di atas Chef)
    private final Map<Chef, StackPane> chefViews = new HashMap<>();
    // Map untuk visual Item di atas meja (Station)
    private final Map<Position, ImageView> stationItemViews = new HashMap<>();
    // Map untuk visual Order
    private final Map<Order, VBox> orderViews = new HashMap<>();

    // Pending Returns (Plate kotor kembali ke storage)
    private final List<PendingPlateReturn> pendingReturns = new ArrayList<>();

    // Inner class helper
    private class PendingPlateReturn {
        Plate plate;
        double timer;

        public PendingPlateReturn(Plate plate, double timer) {
            this.plate = plate;
            this.timer = timer;
        }
    }

    // ... [existing code] ...

    private void setupChefsViews() {
        for (Chef chef : chefs) {
            StackPane chefView = createChefView(chef.getId(), chef.getDirection());
            chefViews.put(chef, chefView);
            mapGrid.add(chefView, chef.getX(), chef.getY());
            if (chef.getIsActive()) {
                chefView.toFront();
                // Add highlight/border for active chef logic if needed
            }
        }
    }

    private StackPane createChefView(String id, Direction dir) {
        StackPane stack = new StackPane();
        stack.setAlignment(Pos.CENTER);

        // Layer 1: Chef Body
        Image chefImage = AssetManager.getChefImage(id, dir);
        ImageView chefImageView = new ImageView(chefImage);
        resizeView(chefImageView); // Preserves Aspect Ratio? setFit 50x50

        // Layer 2: Item Held (Default hidden/empty)
        ImageView itemImageView = new ImageView();
        itemImageView.setFitWidth(30); // Lebih kecil dari tile
        itemImageView.setFitHeight(30);
        itemImageView.setTranslateY(10); // Agak turun biar kayak dipegang

        stack.getChildren().addAll(chefImageView, itemImageView);
        return stack;
    }

    private void resizeView(ImageView view) {
        view.setFitWidth(AssetManager.TILE_SIZE);
        view.setFitHeight(AssetManager.TILE_SIZE);
    }

    private void updateChefPositionInView(Chef chef) {
        StackPane chefView = chefViews.get(chef);
        if (chefView != null) {
            GridPane.setColumnIndex(chefView, chef.getX());
            GridPane.setRowIndex(chefView, chef.getY());
        }
    }

    private void updateChefDirectionView(Chef chef) {
        StackPane chefView = chefViews.get(chef);
        if (chefView != null && !chefView.getChildren().isEmpty()) {
            // Update Chef Body Image (Index 0)
            ImageView body = (ImageView) chefView.getChildren().get(0);
            body.setImage(AssetManager.getChefImage(chef.getId(), chef.getDirection()));
        }
    }

    // UPDATE VISUALS (Termasuk Item di Tangan)
    private void updateVisuals() {
        // 1. Update Items on Stations
        for (Map.Entry<Position, ImageView> entry : stationItemViews.entrySet()) {
            Position pos = entry.getKey();
            ImageView itemView = entry.getValue();

            Tile tile = gameMap.getTile(pos.getX(), pos.getY());
            if (tile != null && tile.getStation() != null) {
                Station station = tile.getStation();
                Image newItemImg = AssetManager.getItemImage(station.getItem());
                itemView.setImage(newItemImg);
            }
        }

        // 2. Update Visual Chef (Posisi & Item Held)
        for (Chef chef : chefs) {
            updateChefPositionInView(chef);
            updateChefHeldItem(chef); // New Helper
        }
    }

    private void updateChefHeldItem(Chef chef) {
        StackPane chefView = chefViews.get(chef);
        if (chefView != null && chefView.getChildren().size() > 1) {
            ImageView itemView = (ImageView) chefView.getChildren().get(1);

            if (chef.isHoldingItem()) {
                Image img = AssetManager.getItemImage(chef.getInventory());
                itemView.setImage(img);
                itemView.setVisible(true);
            } else {
                itemView.setImage(null);
                itemView.setVisible(false);
            }
        }
    }

    // Map untuk visual Order UI
    // private final Map<Order, VBox> orderViews = new HashMap<>(); // REMOVED
    // DUPLICATE

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
        drawInitialMap(); // Gambar Tile & Station
        setupChefsViews(); // Gambar Chef
        updateInfoBar(); // UI Teks Awal
        System.out.println("Game Ready. Waiting for Start command.");
    }

    public void startGame(GameDifficulty difficulty) {
        this.currentDifficulty = difficulty;
        this.stageTimeRemaining = difficulty.getDurationInSeconds();
        this.score = 0;
        this.isFinished = false;
        this.isPaused = false;
        this.consecutiveFailedOrders = 0;
        this.stageEndReason = "";
        this.stagePassed = false;

        // RESET MAP STATE (Fix Bug: Map not resetting)
        for (int y = 0; y < gameMap.getHeight(); y++) {
            for (int x = 0; x < gameMap.getWidth(); x++) {
                org.example.model.map.Tile tile = gameMap.getTile(x, y);
                if (tile.hasStation()) {
                    // Clear logical item
                    tile.getStation().removeItem();

                    // Clear visual item
                    ImageView itemView = stationItemViews.get(new Position(x, y));
                    if (itemView != null) {
                        itemView.setImage(null);
                    }
                }
            }
        }

        // Reset Chefs Position
        List<Position> spawnPoints = gameMap.getSpawnPoints();
        if (!chefs.isEmpty() && !spawnPoints.isEmpty()) {
            Chef c1 = chefs.get(0);
            Position s1 = spawnPoints.get(0);
            c1.setPosition(new Position(s1.getX(), s1.getY()));
            c1.setDirection(Direction.DOWN);
            c1.setInventory(null);
            updateChefPositionInView(c1);
            updateChefDirectionView(c1);
            updateChefHeldItem(c1);

            if (chefs.size() > 1 && spawnPoints.size() > 1) {
                Chef c2 = chefs.get(1);
                Position s2 = spawnPoints.get(1);
                c2.setPosition(new Position(s2.getX(), s2.getY()));
                c2.setDirection(Direction.DOWN);
                c2.setInventory(null);
                updateChefPositionInView(c2);
                updateChefDirectionView(c2);
                updateChefHeldItem(c2);
            }
        }

        // Reset Order State
        this.activeOrders.clear();
        this.orderViews.clear();
        if (ordersContainer != null)
            ordersContainer.getChildren().clear();
        this.timeSinceLastOrder = ORDER_SPAWN_INTERVAL - 3.0; // Spawn order pertama dalam 3 detik

        System.out.println("Starting Game: " + difficulty + " (" + stageTimeRemaining + "s)");
        startGameLoop();
    }

    // =========================================================
    // 2. MAIN GAME LOOP
    // =========================================================

    private void startGameLoop() {
        if (gameLoop != null)
            gameLoop.stop();

        gameLoop = new AnimationTimer() {
            private long lastTime = 0;

            @Override
            public void handle(long now) {
                if (isPaused || isFinished) {
                    lastTime = 0; // Reset to prevent time jump when resume
                    return;
                }

                if (lastTime == 0) {
                    lastTime = now;
                    return;
                }

                double deltaTime = (now - lastTime) / 1_000_000_000.0; // Convert to seconds
                lastTime = now;

                // --- UPDATE LOGIC ---
                processInput(deltaTime);
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
            stageEndReason = "TIME'S UP!";
            evaluateStageResult();
        }

        // 2. Chef Cooldowns (Dash, etc)
        for (Chef c : chefs) {
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

                    // Logic Serving Counter Validation
                    if (station instanceof ServingCounter) {
                        ServingCounter sc = (ServingCounter) station;
                        Plate served = sc.getServedPlate();
                        // DEBUG: Uncomment below to trace
                        // System.out.println("Checking ServingCounter, served plate: " + served);
                        if (served != null) {
                            System.out.println("FOUND PLATE ON SERVING COUNTER!");
                            validateServedPlate(served);
                            sc.clearServedPlate();
                        }
                    }
                }
            }
        }

        // 6. Validasi Pending Returns (Dirty Plates)
        List<PendingPlateReturn> returned = new ArrayList<>();
        for (PendingPlateReturn pr : pendingReturns) {
            pr.timer -= deltaTime;
            if (pr.timer <= 0) {
                returnPlateToStorage(pr.plate);
                returned.add(pr);
            }
        }
        pendingReturns.removeAll(returned);

        // 7. Update UI info
        updateInfoBar();
        updateOrderVisuals();
    }

    private void updateOrderVisuals() {
        // Update setiap order view (timer bar)
        for (Order order : activeOrders) {
            updateOrderView(order);
        }
    }

    private void returnPlateToStorage(Plate plate) {
        // Cari PlateStorage di map
        for (int y = 0; y < gameMap.getHeight(); y++) {
            for (int x = 0; x < gameMap.getWidth(); x++) {
                Tile tile = gameMap.getTile(x, y);
                if (tile.getStation() instanceof PlateStorage) {
                    ((PlateStorage) tile.getStation()).addPlate(plate);
                    return;
                }
            }
        }
    }

    private void validateServedPlate(Plate plate) {
        if (plate.getContents().isEmpty())
            return;

        // Asumsi piring cuma punya 1 pizza
        Object content = plate.getContents().iterator().next();
        if (content instanceof Pizza) {
            Pizza servedPizza = (Pizza) content;
            Order matchingOrder = null;

            // Cari order yang cocok
            System.out
                    .println("VALIDATING SERVED PLATE: " + servedPizza.getName() + " State: " + servedPizza.getState());
            System.out.println("Active Orders Count: " + activeOrders.size());

            for (Order order : activeOrders) {
                System.out.println(" - Checking against: " + order.getDish().getName());

                // servedPizza.getName() returns "Pizza Margherita"
                // order.getDish().getName() returns "Margherita"
                // So we need to match: "Pizza " + orderName == servedName
                String expectedPizzaName = "Pizza " + order.getDish().getName();

                if (expectedPizzaName.equals(servedPizza.getName())
                        && servedPizza.getState() == org.example.model.enums.IngredientState.COOKED) {
                    matchingOrder = order;
                    break;
                }
            }

            if (matchingOrder != null) {
                // SUCCESS - Reset consecutive fails
                consecutiveFailedOrders = 0;
                score += matchingOrder.getReward();
                activeOrders.remove(matchingOrder);
                removeOrderView(matchingOrder);
                System.out
                        .println("ORDER SERVED! +Points: " + matchingOrder.getReward() + " | Consecutive Fails Reset!");
            } else {
                // WRONG ORDER / BURNT - No matching order found
                System.out.println("VALIDATION FAILED for " + servedPizza.getName());
                int wrongDishPenalty = -50; // Fixed penalty for serving wrong dish
                score += wrongDishPenalty;
                if (score < 0)
                    score = 0;

                // Track consecutive fails for wrong dish
                consecutiveFailedOrders++;
                System.out.println("WRONG DISH! Penalty: " + wrongDishPenalty + " | Consecutive Fails: "
                        + consecutiveFailedOrders);

                // Check if too many consecutive fails
                if (currentDifficulty != null
                        && consecutiveFailedOrders >= currentDifficulty.getMaxConsecutiveFails()) {
                    stageEndReason = "TOO MANY FAILED ORDERS!";
                    stagePassed = false;
                    finishStage();
                }
            }

            // Logic Return:
            plate.markAsDirty();
            pendingReturns.add(new PendingPlateReturn(plate, 10.0));
            System.out.println("Plate is dirty and will return to storage in 10s.");

            updateOrderVisuals();
            updateInfoBar();
        }
    }

    // =========================================================
    // 3. INPUT HANDLING
    // =========================================================

    public void handleKeyPressed(KeyEvent event) {
        if (activeKeys.contains(event.getCode()))
            return; // Prevent repeated triggers from OS key repeat if desired, or remove this line
        activeKeys.add(event.getCode());

        switch (event.getCode()) {
            case TAB:
                switchChef();
                break;
            case F:
                handlePickupCommand();
                break;
            case V:
                handleInteractCommand();
                break;
            case SPACE:
                handleDashCommand();
                break;
            case ESCAPE:
                handlePauseCommand();
                break;
            default:
                break;
        }
    }

    public void handleKeyReleased(KeyEvent event) {
        activeKeys.remove(event.getCode());
    }

    private void processInput(double deltaTime) {
        if (isPaused || isFinished)
            return;

        Chef activeChef = getActiveChef();
        if (activeChef.isMoving())
            return; // Don't interrupt movement

        Direction moveDir = null;
        if (activeKeys.contains(KeyCode.W) || activeKeys.contains(KeyCode.UP)) {
            moveDir = Direction.UP;
        } else if (activeKeys.contains(KeyCode.S) || activeKeys.contains(KeyCode.DOWN)) {
            moveDir = Direction.DOWN;
        } else if (activeKeys.contains(KeyCode.A) || activeKeys.contains(KeyCode.LEFT)) {
            moveDir = Direction.LEFT;
        } else if (activeKeys.contains(KeyCode.D) || activeKeys.contains(KeyCode.RIGHT)) {
            moveDir = Direction.RIGHT;
        }

        if (moveDir != null) {
            handleMoveCommand(moveDir);
        }
    }

    public void handleMoveCommand(Direction dir) {
        if (isPaused || isFinished)
            return;

        Chef activeChef = getActiveChef();

        // 1. Ganti Arah (Always update direction first)
        if (!activeChef.getDirection().equals(dir)) {
            activeChef.setDirection(dir);
            updateChefDirectionView(activeChef);
            // Don't return here, allow movement if possible
        }

        if (activeChef.isMoving())
            return;

        int newX = activeChef.getX() + dir.getDx();
        int newY = activeChef.getY() + dir.getDy();

        // 2. Cek Collision & Move
        if (gameMap.isWalkable(newX, newY) && !isOccupied(newX, newY)) {
            moveChefSmoothly(activeChef, dir);
        }
    }

    private void moveChefSmoothly(Chef chef, Direction dir) {
        chef.setMoving(true);

        // 1. Update Logical Position Immediately (to reserve the tile)
        int oldX = chef.getX();
        int oldY = chef.getY();
        chef.move(dir); // Updates logical X,Y

        // 2. Visual Update: Move StackPane to NEW grid position
        updateChefPositionInView(chef);

        // 3. Animate: Translate from OLD position (relative to new) to 0
        StackPane chefView = chefViews.get(chef);
        if (chefView != null) {
            // Calculate offset: (Old - New) * TILE_SIZE
            double startX = -dir.getDx() * AssetManager.TILE_SIZE;
            double startY = -dir.getDy() * AssetManager.TILE_SIZE;

            chefView.setTranslateX(startX);
            chefView.setTranslateY(startY);

            TranslateTransition tt = new TranslateTransition(Duration.millis(200), chefView);
            tt.setToX(0);
            tt.setToY(0);
            tt.setOnFinished(e -> chef.setMoving(false));
            tt.play();
        } else {
            chef.setMoving(false); // Fallback
        }
    }

    public void handleDashCommand() {
        if (isPaused || isFinished)
            return;

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
        if (isPaused || isFinished)
            return;

        Chef activeChef = getActiveChef();
        Position facingPos = activeChef.getPosition().getFacingPosition(activeChef.getDirection());

        if (gameMap.isValidPosition(facingPos.getX(), facingPos.getY())) {
            Tile tile = gameMap.getTile(facingPos.getX(), facingPos.getY());

            if (tile.hasStation()) {
                Station s = tile.getStation();
                s.interact(activeChef); // Logic Pick/Drop ada di Station.interact
                // ServingCounter validation is now handled in updateGameLogic loop
            }
        }
    }

    // ACTION = KEY 'C' (Chop / Wash / Cook Process)
    public void handleInteractCommand() {
        if (isPaused || isFinished)
            return;

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
        if (isPaused || isFinished)
            return;

        Chef oldChef = getActiveChef();
        oldChef.setIsActive(false);

        activeChefIndex = (activeChefIndex + 1) % chefs.size();

        Chef newChef = getActiveChef();
        newChef.setIsActive(true);

        // Update Z-Index visual agar chef aktif di depan
        StackPane activeView = chefViews.get(newChef);
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
                if (score < 0)
                    score = 0;

                // Track consecutive fails
                consecutiveFailedOrders++;
                System.out.println(
                        "Order EXPIRED: " + order.getName() + " | Consecutive Fails: " + consecutiveFailedOrders);

                // Check if too many consecutive fails
                if (currentDifficulty != null
                        && consecutiveFailedOrders >= currentDifficulty.getMaxConsecutiveFails()) {
                    stageEndReason = "TOO MANY FAILED ORDERS!";
                    stagePassed = false;
                    finishStage();
                    return;
                }
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
        if (servedDish.getComponents().isEmpty())
            return;

        for (Order order : activeOrders) {
            // TODO: Implementasi deep check ingredients
            // Disini asumsi sederhana match nama atau komponen
            // Logic validasi resep harusnya ada di Order.matches(Dish)

            // Simulasi match sukses:
            matched = true;
            activeOrders.remove(order);
            removeOrderView(order);
            score += 120; // Tambah skor
            System.out.println("Order SUCCESS: " + order.getName());
            break;
        }

        if (!matched) {
            System.out.println("Order FAILED: Salah menu!");
            score -= 50;
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
                } else if (tile.getType() == TileType.WALL) {
                    ImageView wallView = new ImageView(AssetManager.getTileImage(TileType.WALL));
                    resizeView(wallView);
                    stack.getChildren().add(wallView);
                }

                mapGrid.add(stack, x, y);
            }
        }
    }

    // Old methods removed to fix duplication

    // =========================================================
    // 6. UI ORDER HELPERS
    // =========================================================

    private void addOrderView(Order order, org.example.model.recipe.Recipe recipe) {
        if (ordersContainer == null)
            return;

        VBox orderBox = new VBox(5);
        orderBox.setStyle(
                "-fx-background-color: rgba(30, 30, 30, 0.9); -fx-padding: 10; -fx-background-radius: 10; -fx-border-color: #ffd700; -fx-border-width: 2;");
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
        VBox orderBox = orderViews.get(order);
        if (orderBox != null) {
            orderBox.getChildren().clear(); // Reset konten

            // 1. Progress Bar Timer
            ProgressBar pb = new ProgressBar(order.getRemainingTime() / Order.ORDER_DURATION);
            pb.setPrefWidth(60);

            // Warna bar: Hijau -> Kuning -> Merah
            if (order.getRemainingTime() < 5) {
                pb.setStyle("-fx-accent: red;");
            } else if (order.getRemainingTime() < 10) {
                pb.setStyle("-fx-accent: orange;");
            } else {
                pb.setStyle("-fx-accent: green;");
            }

            // 2. Icon Produk Utuh (Pizza)
            ImageView productIcon = new ImageView(AssetManager.getItemImage(new Pizza(order.getRecipe().getName())));
            productIcon.setFitWidth(40);
            productIcon.setFitHeight(40);

            // 3. Nama Resep (Label)
            Label nameLabel = new Label(order.getRecipe().getName());
            nameLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: white;");

            // 4. Daftar Bahan (Recipe Ingredients) - NEW FEATURE
            HBox ingredientsBox = new HBox(2);
            ingredientsBox.setAlignment(Pos.CENTER);
            for (String ingredientName : order.getRecipe().getComponents().keySet()) {
                // Load gambar kecil untuk setiap bahan
                // Asumsi nama bahan di resep match dengan key di AssetManager (e.g.,
                // "tomat_chopped")
                // Kalau di resep "Tomat", kita harus convert ke "tomat" atau "tomat_chopped"
                // Sederhananya, kita coba load langsung lowercase.
                ImageView ingImg = new ImageView(AssetManager.getItemImage(new Ingredient(ingredientName)));
                // Atau lebih aman pakai string key kalau ada mappingnya.
                // Karena Ingredient constructor perlu nama, kita coba create dummy.

                ingImg.setFitWidth(15);
                ingImg.setFitHeight(15);
                ingredientsBox.getChildren().add(ingImg);
            }

            orderBox.getChildren().addAll(pb, productIcon, nameLabel, ingredientsBox);
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
        if (isFinished)
            return;
        if (isPaused)
            resumeGame();
        else
            pauseGame();
    }

    public void pauseGame() {
        if (isPaused)
            return;
        isPaused = true;
        System.out.println("GAME PAUSED!");

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
        if (!isPaused)
            return;
        gameRoot.getChildren().remove(pauseMenu);
        isPaused = false;
        // Game Loop handle(now) otomatis jalan lagi karena isPaused false
    }

    private void evaluateStageResult() {
        // Evaluate if stage is passed based on minimum score
        if (currentDifficulty != null) {
            stagePassed = score >= currentDifficulty.getMinimumScoreToPass();
        } else {
            stagePassed = score >= 100; // Default minimum
        }
        finishStage();
    }

    public void finishStage() {
        if (isFinished)
            return;
        isFinished = true;

        // Stop accepting new orders
        System.out.println("========================================");
        System.out.println("STAGE OVER!");
        System.out.println("Reason: " + (stageEndReason.isEmpty() ? "Game Complete" : stageEndReason));
        System.out.println("Final Score: " + score);
        if (currentDifficulty != null) {
            System.out.println("Minimum Score to Pass: " + currentDifficulty.getMinimumScoreToPass());
        }
        System.out.println("Result: " + (stagePassed ? "PASSED!" : "FAILED!"));
        System.out.println("========================================");

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

        if (stageOverController != null) {
            stageOverController.setStageResult(score, stagePassed, stageEndReason,
                    currentDifficulty != null ? currentDifficulty.getMinimumScoreToPass() : 100);
        }

        if (!gameRoot.getChildren().contains(stageOverMenu)) {
            gameRoot.getChildren().add(stageOverMenu);
            AnchorPane.setTopAnchor(stageOverMenu, 0.0);
            AnchorPane.setBottomAnchor(stageOverMenu, 0.0);
            AnchorPane.setLeftAnchor(stageOverMenu, 0.0);
            AnchorPane.setRightAnchor(stageOverMenu, 0.0);
        }
    }

    public void restartGame() {
        if (stageOverMenu != null)
            gameRoot.getChildren().remove(stageOverMenu);
        startGame(currentDifficulty != null ? currentDifficulty : GameDifficulty.EASY);
    }

    public void quitToMainMenu() {
        if (gameLoop != null)
            gameLoop.stop();
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

        chefInfoLabel
                .setText(String.format("Chef: %s (%d/%d)", activeChef.getName(), activeChefIndex + 1, chefs.size()));

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

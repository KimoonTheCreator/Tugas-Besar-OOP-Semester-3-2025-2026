package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import org.example.model.entities.Chef; 
import org.example.model.map.Direction;
import org.example.model.map.Map;
import org.example.model.map.Position;
import org.example.model.map.Tile;
import org.example.model.enums.TileType; 
import org.example.view.AssetManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
// JANGAN import java.util.Map, gunakan nama lengkapnya di bawah

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

    @FXML
    public void initialize() {
        drawInitialMap();
        setupChefsViews();
        updateInfoBar();
        System.out.println("Controller Initialized. Game ready.");
    }
    
    // -----------------------------------------------------------
    //                  LOGIKA GAMBAR (VIEW UPDATE)
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
    
    private void updateInfoBar() {
        Chef activeChef = getActiveChef();
        chefInfoLabel.setText("Chef: " + activeChef.getName() + " (" + (activeChefIndex + 1) + "/" + chefs.size() + ")");
        positionInfoLabel.setText("Position: " + activeChef.getPosition() + " | Facing: " + activeChef.getDirection());
        // TODO: Implementasi logika update score
    }

    // -----------------------------------------------------------
    //                  LOGIKA CONTROLLER (INPUT)
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
    
    public void handleMoveCommand(Direction dir) {
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
        Chef activeChef = getActiveChef();
        Position facingPos = activeChef.getPosition().getFacingPosition(activeChef.getDirection());
        
        System.out.println(activeChef.getName() + " interacted with tile at " + facingPos);
        // Tambahkan logika interaksi di sini
    }
    
    public void handlePickupCommand() {
        System.out.println(getActiveChef().getName() + " attempted to pickup item.");
        // Tambahkan logika pickup di sini
    }

    public void handlePauseCommand() {
        System.out.println("Pause command received. Implementasi pop-up pause akan diletakkan di sini.");
        // Logika Pop-up Pause dan menghentikan game loop akan menyusul di sini

        // Nanti, Anda akan memanggil metode untuk menampilkan Pop-up Pause di sini.
        // Contoh: showPausePopUp();
    }
}
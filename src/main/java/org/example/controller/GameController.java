package org.example.controller;

import org.example.model.entities.Chef;
import org.example.model.enums.Command;
import org.example.model.enums.Key;
import org.example.model.input.InputHandler;
import org.example.model.map.Direction;
import org.example.model.map.GameMap;
import org.example.model.map.Position;
import org.example.model.map.Tile;
import org.example.model.stations.Station;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller class that manages game logic, state, and input handling.
 * Separates the logic from the view (GameWindow).
 */
public class GameController {

    private GameMap gameMap;
    private List<Chef> chefs;
    private int activeChefIndex;
    private InputHandler inputHandler;

    public GameController() {
        initGame();
    }

    private void initGame() {
        // Inisialisasi map
        this.gameMap = new GameMap();

        // Inisialisasi chefs
        this.chefs = new ArrayList<>();
        this.activeChefIndex = 0;
        this.inputHandler = new InputHandler();

        // Ambil spawn points dari map
        List<Position> spawnPoints = gameMap.getSpawnPoints();

        // Buat Chef 1
        Position spawn1 = spawnPoints.size() > 0 ? spawnPoints.get(0) : new Position(1, 1);
        Chef chef1 = new Chef("chef1", "Gordon", new Position(spawn1.getX(), spawn1.getY()));
        chef1.setIsActive(true);
        chefs.add(chef1);

        // Buat Chef 2
        Position spawn2 = spawnPoints.size() > 1 ? spawnPoints.get(1) : new Position(2, 1);
        Chef chef2 = new Chef("chef2", "Jamie", new Position(spawn2.getX(), spawn2.getY()));
        chef2.setIsActive(false);
        chefs.add(chef2);
    }

    // Main input handling method called by View
    public void processInput(Key key) {
        if (key == null)
            return;

        Command command = inputHandler.handleInput(key);
        Chef activeChef = getActiveChef();

        if (command == Command.SWITCH_CHEF) {
            switchChef();
        } else if (command == Command.INTERACT) {
            handleInteract(activeChef);
        } else if (command == Command.PICKUP) {
            handlePickup(activeChef);
        } else if (command == Command.DROP) {
            handleDrop(activeChef);
        } else if (command == Command.PICKUP_DROP) {
            if (activeChef.isHoldingItem())
                handleDrop(activeChef);
            else
                handlePickup(activeChef);
        } else if (command == Command.CUT || command == Command.WASH || command == Command.COOK) {
            handleStationAction(activeChef, command);
        } else if (inputHandler.isMovementCommand(command)) {
            handleMovement(activeChef, command);
        } else if (command == Command.DASH) {
            handleDash(activeChef);
        }
    }

    public void releaseKey() {
        getActiveChef().setIdle();
    }

    private void handleMovement(Chef chef, Command command) {
        Direction dir = inputHandler.getDirectionFromCommand(command);
        int newX = chef.getX() + dir.getDx();
        int newY = chef.getY() + dir.getDy();

        // Cek arah dulu
        if (!chef.getDirection().equals(dir)) {
            chef.setDirection(dir);
        } else if (gameMap.isWalkable(newX, newY) && !isOccupied(newX, newY)) {
            chef.move(dir);
        }
    }

    private void handleDash(Chef chef) {
        if (chef.canDash()) {
            Direction dir = chef.getDirection();
            int dashDistance = 3; // Dash 3 tiles

            // Move multiple times immediately
            for (int i = 0; i < dashDistance; i++) {
                int newX = chef.getX() + dir.getDx();
                int newY = chef.getY() + dir.getDy();

                // Cek collision per step
                if (gameMap.isWalkable(newX, newY) && !isOccupied(newX, newY)) {
                    chef.move(dir);
                } else {
                    // Berhenti jika nabrak
                    break;
                }
            }
            chef.startDash();
            System.out.println(chef.getName() + " melakukan DASH!");
        } else {
            System.out.println("Dash cooldown: " + chef.getDashCooldownRemaining() + "ms");
        }
    }

    private void switchChef() {
        chefs.get(activeChefIndex).setIsActive(false);
        activeChefIndex = (activeChefIndex + 1) % chefs.size();
        chefs.get(activeChefIndex).setIsActive(true);
        System.out.println("Switched to: " + getActiveChef().getName());
    }

    private void handleInteract(Chef chef) {
        Position frontPos = chef.getFrontPosition();
        Station station = gameMap.getStation(frontPos);

        if (station != null) {
            System.out.println(chef.getName() + " berinteraksi dengan " + station.getName());
            station.interact(chef);
        } else {
            Tile frontTile = gameMap.getTile(frontPos);
            if (frontTile != null) {
                System.out.println("Tidak ada station di depan. Tile: " + frontTile.getType().getDisplayName());
            }
        }
    }

    private void handlePickup(Chef chef) {
        Position frontPos = chef.getFrontPosition();
        Station station = gameMap.getStation(frontPos);

        if (!chef.isHoldingItem()) {
            if (station != null && !station.isEmpty()) {
                chef.pickUpItem(station.takeItem());
                System.out.println(chef.getName() + " mengambil item dari " + station.getName());
            } else {
                System.out.println("Tidak ada item untuk diambil!");
            }
        } else {
            System.out.println("Tangan penuh!");
        }
    }

    private void handleDrop(Chef chef) {
        Position frontPos = chef.getFrontPosition();
        Station station = gameMap.getStation(frontPos);

        if (chef.isHoldingItem()) {
            if (station != null && station.isEmpty()) {
                station.addItem(chef.dropItem());
                System.out.println(chef.getName() + " menaruh item di " + station.getName());
            } else {
                System.out.println("Tidak bisa menaruh item di sini!");
            }
        } else {
            System.out.println("Tangan kosong!");
        }
    }

    private void handleStationAction(Chef chef, Command command) {
        Position frontPos = chef.getFrontPosition();
        Station station = gameMap.getStation(frontPos);

        if (station != null) {
            System.out.println("Melakukan aksi " + command + " pada " + station.getName());
            station.interact(chef);
        }
    }

    private boolean isOccupied(int x, int y) {
        for (int i = 0; i < chefs.size(); i++) {
            if (i != activeChefIndex && chefs.get(i).getX() == x && chefs.get(i).getY() == y) {
                return true;
            }
        }
        return false;
    }

    // Getters for View
    public Chef getActiveChef() {
        return chefs.get(activeChefIndex);
    }

    public List<Chef> getChefs() {
        return chefs;
    }

    public GameMap getGameMap() {
        return gameMap;
    }

    public int getActiveChefIndex() {
        return activeChefIndex;
    }

    public InputHandler getInputHandler() {
        return inputHandler;
    }
}

package org.example.model.entities;

import org.example.model.enums.ChefState;
import org.example.model.items.Item;
import org.example.model.map.Direction;
import org.example.model.map.Position;
import org.example.util.GameTimer;

/**
 * Kelas Chef yang merepresentasikan karakter pemain
 * Chef bisa bergerak, mengambil item, dan berinteraksi dengan station
 */
public class Chef extends GameObject {
    private String id;
    private String name;
    private Direction direction;
    private Item inventory;
    private ChefState state;
    private Boolean isActive;

    // Constructor
    public Chef(String id, String name, Position position) {
        super(position);
        this.id = id;
        this.name = name;
        this.direction = Direction.DOWN;
        this.state = ChefState.IDLE;
        this.inventory = null;
        this.isActive = false;
    }

    public Chef(String id, String name, int x, int y) {
        super(x, y);
        this.id = id;
        this.name = name;
        this.direction = Direction.DOWN;
        this.state = ChefState.IDLE;
        this.inventory = null;
        this.isActive = false;
    }

    // Method untuk menggerakkan chef
    public void move(Direction direction) {
        if (direction == null)
            return;

        this.direction = direction;
        this.state = ChefState.MOVE;
        this.position.translate(direction.getDx(), direction.getDy());
    }

    // Method untuk mencoba bergerak (cek arah dulu)
    public boolean tryMove(Direction direction, int mapWidth, int mapHeight) {
        if (direction == null)
            return false;

        // Jika belum menghadap arah tersebut, hanya ubah arah
        if (!this.direction.equals(direction)) {
            this.direction = direction;
            return false;
        }

        // Cek batas map
        int newX = this.position.getX() + direction.getDx();
        int newY = this.position.getY() + direction.getDy();

        if (newX >= 0 && newX < mapWidth && newY >= 0 && newY < mapHeight) {
            this.state = ChefState.MOVE;
            this.position.translate(direction.getDx(), direction.getDy());
            return true;
        }

        return false;
    }

    // Method untuk mengambil item
    public void pickUpItem(Item item) {
        if (!isHoldingItem() && item != null) {
            this.inventory = item;
            this.state = ChefState.HOLDING_ITEM;
        }
    }

    // Method untuk meletakkan item
    public Item dropItem() {
        if (isHoldingItem()) {
            Item droppedItem = this.inventory;
            this.inventory = null;
            this.state = ChefState.IDLE;
            return droppedItem;
        }
        return null;
    }

    // Method untuk interaksi dengan station
    public void interactStation(Station station) {
        if (station != null) {
            station.interact(this);
        }
    }

    // Method untuk mendapatkan posisi di depan chef
    public Position getFrontPosition() {
        int frontX = this.position.getX() + this.direction.getDx();
        int frontY = this.position.getY() + this.direction.getDy();
        return new Position(frontX, frontY);
    }

    // Cek apakah chef sedang sibuk
    public boolean isBusy() {
        return this.state == ChefState.COOKING || this.state == ChefState.CUTTING;
    }

    // Set state ke IDLE
    public void setIdle() {
        this.state = ChefState.IDLE;
    }

    // Cek apakah sedang memegang item
    public boolean isHoldingItem() {
        return this.inventory != null;
    }

    // Dash Logic
    private GameTimer dashTimer = new GameTimer(5000); // 5 seconds cooldown

    public boolean canDash() {
        return dashTimer.isReady();
    }

    public void startDash() {
        dashTimer.start();
    }

    public long getDashCooldownRemaining() {
        return dashTimer.getRemaining();
    }

    public long getTotalDashCooldown() {
        return dashTimer.getDuration();
    }

    // Getter dan Setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public ChefState getState() {
        return state;
    }

    public void setState(ChefState state) {
        this.state = state;
    }

    public Item getInventory() {
        return inventory;
    }

    public void setInventory(Item inventory) {
        this.inventory = inventory;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    @Override
    public String toString() {
        return "Chef[" + name + " at " + position + "]";
    }
}
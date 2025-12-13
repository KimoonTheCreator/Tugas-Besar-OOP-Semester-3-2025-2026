package org.example.model.entities;

import org.example.model.enums.ChefState;
import org.example.model.items.Item;
import org.example.model.map.Direction;
import org.example.model.map.Position;

import org.example.model.stations.Station;
import org.example.model.entities.GameObject;

/**
 * Class Chef - karakter yang dikontrol player
 */
public class Chef extends GameObject {
    private String id;
    private String name;
    private Direction direction;
    private Item inventory;
    private ChefState state;
    private boolean isActive;
    private boolean isMoving = false;

    // Dash properties
    private long lastDashTime = 0;
    private static final long DASH_COOLDOWN = 1000; // 1 second cooldown

    // Constructor
    public Chef(String id, String name, Position position) {
        super(position);
        this.id = id;
        this.name = name;
        this.direction = Direction.DOWN;
        this.inventory = null;
        this.state = ChefState.IDLE;
        this.isActive = false;
    }

    // Movement
    public void move(Direction dir) {
        int newX = this.position.getX() + dir.getDx();
        int newY = this.position.getY() + dir.getDy();
        this.position = new Position(newX, newY);
        this.state = ChefState.MOVE;
    }

    // Item handling
    public void setInventory(Item item) {
        this.inventory = item;
        this.state = ChefState.HOLDING_ITEM;
    }

    // Method Helper: Taruh item (Drop)
    public Item dropItem() {
        Item item = this.inventory;
        this.inventory = null;
        this.state = ChefState.IDLE;
        return item;
    }

    public boolean isHoldingItem() {
        return this.inventory != null;
    }

    // Getters & Setters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Position getPosition() {
        return position;
    }

    public int getX() {
        return position.getX();
    }

    public int getY() {
        return position.getY();
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public Item getInventory() {
        return inventory;
    }

    public ChefState getState() {
        return state;
    }

    public void setState(ChefState state) {
        this.state = state;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    // Dash methods
    public boolean canDash() {
        return System.currentTimeMillis() - lastDashTime >= DASH_COOLDOWN;
    }

    public void startDash() {
        this.lastDashTime = System.currentTimeMillis();
    }

    public long getDashCooldownRemaining() {
        long diff = System.currentTimeMillis() - lastDashTime;
        return diff >= DASH_COOLDOWN ? 0 : DASH_COOLDOWN - diff;
    }

    @Override
    public String toString() {
        return "Chef[" + name + " at " + position + "]";
    }

    public boolean isMoving() {
        return isMoving;
    }

    public void setMoving(boolean moving) {
        isMoving = moving;
    }

    public void update(double deltaTime) {
        // Per-frame update (animasi, dll)
    }
}
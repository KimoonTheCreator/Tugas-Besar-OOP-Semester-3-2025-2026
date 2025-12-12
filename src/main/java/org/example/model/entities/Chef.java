package org.example.model.entities;

import org.example.model.enums.ChefState;
import org.example.model.items.Item;
import org.example.model.map.Direction;
import org.example.model.map.Position;
import org.example.model.entities.GameObject;

public class Chef extends GameObject {
    private String id;
    private String name;
    private Direction direction;
    private Item inventory; // Barang yang dipegang
    private ChefState state;
    private boolean isActive;

    public Chef(String id, String name, Position position) {
        super(position);
        this.id = id;
        this.name = name;
        this.direction = Direction.DOWN;
        this.inventory = null;
        this.state = ChefState.IDLE;
        this.isActive = false;
    }

    // ==========================================
    // 1. FITUR MOVEMENT (Dari Fathan) - YANG HILANG
    // ==========================================
    public void move(Direction dir) {
        // Update koordinat posisi
        int newX = this.position.getX() + dir.getDx();
        int newY = this.position.getY() + dir.getDy();

        // Kita update object position-nya
        this.position = new Position(newX, newY);

        // Update state visual
        this.state = ChefState.MOVE;
    }

    // ==========================================
    // 2. FITUR INTERAKSI (Dari Anda)
    // ==========================================

    // Method Helper: Ambil item (Pickup)
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

    // ==========================================
    // 3. GETTERS & SETTERS (Wajib Ada)
    // ==========================================
    public String getId() { return id; }
    public String getName() { return name; }

    public Position getPosition() { return position; }

    // Helper untuk GameController biar gampang akses X/Y
    public int getX() { return position.getX(); }
    public int getY() { return position.getY(); }

    public Direction getDirection() { return direction; }
    public void setDirection(Direction direction) { this.direction = direction; }

    public Item getInventory() { return inventory; }

    public ChefState getState() { return state; }
    public void setState(ChefState state) { this.state = state; }

    public boolean getIsActive() { return isActive; }
    public void setIsActive(boolean isActive) { this.isActive = isActive; }
}
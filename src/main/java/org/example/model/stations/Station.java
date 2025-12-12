package org.example.model.stations;

import org.example.model.entities.Chef;
import org.example.model.items.Item;
import org.example.model.map.Position;

public abstract class Station {
    private String name;
    private Position position;
    protected Item item;

    public Station(String name, Position position) {
        this.name = name;
        this.position = position;
        this.item = null;
    }

    /**
     * Handles item movement (Pick Up / Drop).
     * Triggered by 'F' (or default Interact key).
     */
    public abstract void interact(Chef chef);

    /**
     * Handles processing actions (Cut, Wash).
     * Triggered by 'V' (Action key).
     */
    public void action(Chef chef) {
        // Default: Do nothing
    }

    /**
     * Updates the station state based on time.
     * 
     * @param deltaTime Time elapsed since last frame in seconds.
     */
    public void update(double deltaTime) {
        // Default behavior: do nothing
    }

    public boolean isEmpty() {
        return this.item == null;
    }

    public boolean addItem(Item item) {
        if (isEmpty()) {
            this.item = item;
            return true;
        }
        return false;
    }

    public Item takeItem() {
        Item temp = this.item;
        this.item = null;
        return temp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

}
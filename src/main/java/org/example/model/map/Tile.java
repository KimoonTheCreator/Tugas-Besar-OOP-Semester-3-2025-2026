package org.example.model.map;

import org.example.model.enums.TileType;

/**
 * Kelas Tile untuk merepresentasikan satu kotak dalam map
 */
public class Tile {
    private TileType type;
    private Position position;

    public Tile(TileType type, int x, int y) {
        this.type = type;
        this.position = new Position(x, y);
    }

    public Tile(TileType type, Position position) {
        this.type = type;
        this.position = position;
    }

    // Getter
    public TileType getType() {
        return type;
    }

    public void setType(TileType type) {
        this.type = type;
    }

    public Position getPosition() {
        return position;
    }

    public boolean isWalkable() {
        return type.isWalkable();
    }

    public char getSymbol() {
        return type.getSymbol();
    }

    @Override
    public String toString() {
        return String.valueOf(type.getSymbol());
    }
}

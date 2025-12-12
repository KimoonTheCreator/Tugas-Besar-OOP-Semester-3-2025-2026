package org.example.model.map;

import org.example.model.enums.TileType;
import org.example.model.stations.*;

/**
 * Kelas Tile untuk merepresentasikan satu kotak dalam map
 */
public class Tile {
    private TileType type;
    private Position position;
    private Station station;

    public Tile(TileType type, int x, int y) {
        this.type = type;
        this.position = new Position(x, y);
        initStation();
    }

    public Tile(TileType type, Position position) {
        this.type = type;
        this.position = position;
        initStation();
    }

    private void initStation() {
        switch (type) {
            case CUTTING_STATION:
                this.station = new CuttingStation(position);
                break;
            case COOKING_STATION:
                this.station = new CookingStation(position);
                break;
            case ASSEMBLY_STATION:
                this.station = new AssemblyStation(position);
                break;
            case SERVING_COUNTER:
                this.station = new ServingCounter(position);
                break;
            case WASHING_STATION:
                this.station = new WashingStation(position);
                break;
            case INGREDIENT_STORAGE:
                String[] ingredients = { "Tomato", "Lettuce", "Meat", "Bread", "Cheese" };
                // Use a deterministic way to assign ingredients to storage crates
                // e.g., position based
                int index = (position.getX() + position.getY()) % ingredients.length;
                this.station = new IngredientStorage(position, ingredients[index]);
                break;
            case PLATE_STORAGE:
                this.station = new PlateStorage(position);
                break;
            case TRASH_STATION:
                this.station = new TrashStation(position);
                break;
            default:
                this.station = null;
                break;
        }
    }

    // Getter
    public TileType getType() {
        return type;
    }

    public void setType(TileType type) {
        this.type = type;
        // Update station if type changes? Maybe reset
        initStation();
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

    public boolean hasStation() {
        return station != null;
    }

    public Station getStation() {
        return station;
    }

    @Override
    public String toString() {
        return String.valueOf(type.getSymbol());
    }
}

package org.example.model.enums;

/**
 * Enum untuk jenis-jenis tile dalam map
 */
public enum TileType {
    FLOOR('.', "Floor", true),
    WALL('X', "Wall", false),
    CUTTING_STATION('C', "Cutting Station", false),
    COOKING_STATION('R', "Cooking Station", false),
    ASSEMBLY_STATION('A', "Assembly Station", false),
    SERVING_COUNTER('S', "Serving Counter", false),
    WASHING_STATION('W', "Washing Station", false),
    INGREDIENT_STORAGE('I', "Ingredient Storage", false),
    PLATE_STORAGE('P', "Plate Storage", false),
    TRASH_STATION('T', "Trash Station", false),
    SPAWN_POINT('V', "Spawn Point", true);

    private final char symbol;
    private final String displayName;
    private final boolean walkable;

    TileType(char symbol, String displayName, boolean walkable) {
        this.symbol = symbol;
        this.displayName = displayName;
        this.walkable = walkable;
    }

    public char getSymbol() {
        return symbol;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isWalkable() {
        return walkable;
    }

    // Method untuk mendapatkan TileType dari karakter
    public static TileType fromSymbol(char symbol) {
        for (TileType type : values()) {
            if (type.symbol == symbol) {
                return type;
            }
        }
        return FLOOR;
    }
}

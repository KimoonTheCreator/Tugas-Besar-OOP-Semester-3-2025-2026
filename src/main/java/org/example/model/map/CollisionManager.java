package org.example.model.map; 
import org.example.model.enums.TileType;

public class CollisionManager {

    private final Map map;

    public CollisionManager(Map map) {
        this.map = map; 
    }

    public boolean isMovementValid(Position targetPos) {
        return map.isWalkable(targetPos); 
    }

    public boolean isStation(Position targetPos) {
        Tile targetTile = map.getTile(targetPos);

        if (targetTile != null) {
            TileType type = targetTile.getType();
            return type == TileType.CUTTING_STATION ||
                   type == TileType.COOKING_STATION ||
                   type == TileType.ASSEMBLY_STATION ||
                   type == TileType.SERVING_COUNTER ||
                   type == TileType.WASHING_STATION ||
                   type == TileType.INGREDIENT_STORAGE ||
                   type == TileType.PLATE_STORAGE ||
                   type == TileType.TRASH_STATION;
        }
        return false;
    }
}
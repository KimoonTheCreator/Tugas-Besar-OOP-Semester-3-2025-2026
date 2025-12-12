package org.example.model.map;

import org.example.model.enums.TileType;

public class CollisionManager {

    // UBAH TIPE DATA DI SINI
    private final GameMap map;

    // UBAH CONSTRUCTOR DI SINI
    public CollisionManager(GameMap map) {
        this.map = map;
    }

    public boolean isMovementValid(Position targetPos) {
        // Pastikan GameMap memiliki method isWalkable(Position pos)
        return map.isWalkable(targetPos);
    }

    public boolean isStation(Position targetPos) {
        Tile targetTile = map.getTile(targetPos);

        if (targetTile != null) {
            TileType type = targetTile.getType();
            // Cek apakah tipe tile tersebut adalah salah satu station
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
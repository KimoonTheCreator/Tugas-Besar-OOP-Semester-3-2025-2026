package org.example.model.map;

import org.example.model.enums.TileType;
import java.util.ArrayList;
import java.util.List;

/**
 * Kelas GameMap untuk mengelola peta permainan
 */
public class Map {
    private Tile[][] tiles;
    private int width;
    private int height;
    private List<Position> spawnPoints;

    // Layout map default (14 kolom x 10 baris)
    private static final String[] DEFAULT_MAP = {
            "XATACAAACAAAXX", // Row 1
            "X...........XX", // Row 2
            "X.....A.V...SX", // Row 3
            "X...........SX", // Row 4
            "XWWAIAIAIAIAPX", // Row 5
            "X............X", // Row 6
            "XXXX..A...XXXX", // Row 7
            "XR...V......RX", // Row 8
            "XXXX......XXXX", // Row 9
            "XXXXAAIAAAXXXX" // Row 10
    };

    public Map() {
        this(DEFAULT_MAP);
    }

    public Map(String[] mapLayout) {
        this.height = mapLayout.length;
        this.width = mapLayout[0].length();
        this.tiles = new Tile[width][height];
        this.spawnPoints = new ArrayList<>();
        parseMap(mapLayout);
    }

    // Parse string map menjadi array Tile
    private void parseMap(String[] mapLayout) {
        for (int y = 0; y < height; y++) {
            String row = mapLayout[y];
            for (int x = 0; x < width && x < row.length(); x++) {
                char symbol = row.charAt(x);
                TileType type = TileType.fromSymbol(symbol);
                tiles[x][y] = new Tile(type, x, y);

                // Simpan spawn point
                if (type == TileType.SPAWN_POINT) {
                    spawnPoints.add(new Position(x, y));
                }
            }
            // Isi sisa dengan wall jika row lebih pendek
            for (int x = row.length(); x < width; x++) {
                tiles[x][y] = new Tile(TileType.WALL, x, y);
            }
        }
    }

    // Dapatkan tile pada posisi tertentu
    public Tile getTile(int x, int y) {
        if (isValidPosition(x, y)) {
            return tiles[x][y];
        }
        return null;
    }

    public Tile getTile(Position pos) {
        return getTile(pos.getX(), pos.getY());
    }

    // Cek posisi valid
    public boolean isValidPosition(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    // Cek posisi bisa dilewati
    public boolean isWalkable(int x, int y) {
        if (!isValidPosition(x, y))
            return false;
        return tiles[x][y].isWalkable();
    }

    public boolean isWalkable(Position pos) {
        return isWalkable(pos.getX(), pos.getY());
    }

    // Getter
    public List<Position> getSpawnPoints() {
        return spawnPoints;
    }

    public Position getFirstSpawnPoint() {
        if (spawnPoints.isEmpty()) {
            return new Position(1, 1);
        }
        return spawnPoints.get(0);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    // Print map untuk debug
    public void printMap() {
        System.out.println("Map (" + width + "x" + height + "):");
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                System.out.print(tiles[x][y].getSymbol());
            }
            System.out.println();
        }
    }
}

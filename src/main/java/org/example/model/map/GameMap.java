package org.example.model.map;

import org.example.model.enums.TileType;
import org.example.model.stations.*;
import org.example.model.order.OrderManager;
import org.example.model.items.Oven;

import java.util.ArrayList;
import java.util.List;

// GANTI NAMA CLASS JADI GameMap
public class GameMap {
    private Tile[][] tiles;
    private int width;
    private int height;
    private List<Position> spawnPoints;
    private OrderManager orderManager;

    private static final String[] DEFAULT_MAP = {
            "XATACAAACAAAXX",
            "X...........XX",
            "X.....A.V...SX",
            "X...........SX",
            "XWWAIAIAIAIAPX",
            "X............X",
            "XXXX..A...XXXX",
            "XR...V......RX",
            "XXXX......XXXX",
            "XXXXAAIAAAXXXX"
    };

    public GameMap() {
        this(DEFAULT_MAP);
    }

    public GameMap(String[] mapLayout) {
        this.height = mapLayout.length;
        this.width = mapLayout[0].length();
        this.tiles = new Tile[width][height];
        this.spawnPoints = new ArrayList<>();
        this.orderManager = new OrderManager();

        parseMap(mapLayout);
    }

    // ... (method parseMap dan initializeStationLogic SAMA PERSIS seperti
    // sebelumnya) ...
    // Copy-paste isi method parseMap & initializeStationLogic Anda di sini

    private void parseMap(String[] mapLayout) {
        for (int y = 0; y < height; y++) {
            String row = mapLayout[y];
            for (int x = 0; x < width && x < row.length(); x++) {
                char symbol = row.charAt(x);
                TileType type = TileType.fromSymbol(symbol);
                Tile tile = new Tile(type, x, y);
                tiles[x][y] = tile;
                if (type == TileType.SPAWN_POINT)
                    spawnPoints.add(new Position(x, y));
                initializeStationLogic(tile, type, symbol, x, y);
            }
            for (int x = row.length(); x < width; x++) {
                tiles[x][y] = new Tile(TileType.WALL, x, y);
            }
        }
    }

    private void initializeStationLogic(Tile tile, TileType type, char symbol, int x, int y) {
        Position pos = new Position(x, y);
        Station station = null;

        switch (type) {
            case INGREDIENT_STORAGE:
                String ingredientName = "Tomat"; // Default

                // LOGIKA PEMETAAN BAHAN (Hardcoded Coordinates)
                // Baris ke-5 (Index 4) -> x=4, 6, 8, 10
                if (y == 4) {
                    if (x == 4)
                        ingredientName = "Tomat";
                    else if (x == 6)
                        ingredientName = "Keju";
                    else if (x == 8)
                        ingredientName = "Sosis";
                    else if (x == 10)
                        ingredientName = "Ayam";
                }
                // Baris ke-10 (Index 9) -> x=6
                else if (y == 9) {
                    if (x == 6)
                        ingredientName = "Adonan"; // Adonan
                }

                System.out.println("Spawn Storage di (" + x + "," + y + ") isinya: " + ingredientName);
                station = new IngredientStorage("Crate (" + ingredientName + ")", pos, ingredientName);
                break;

            // ... Case station lain (Cutting, Cooking, dll) SAMA SEPERTI SEBELUMNYA ...
            case CUTTING_STATION:
                station = new CuttingStation("CuttingBoard", pos);
                break;
            case COOKING_STATION:
                station = new Oven("Oven", pos);
                break;
            case ASSEMBLY_STATION:
                station = new AssemblyStation("Assembly", pos);
                break;
            case WASHING_STATION:
                station = new WashingStation("Sink", pos);
                break;
            case TRASH_STATION:
                station = new TrashStation("Trash", pos);
                break;
            case SERVING_COUNTER:
                station = new ServingCounter("Serving", pos);
                break;
            case PLATE_STORAGE:
                station = new PlateStorage(pos);
                break;

            default:
                station = null;
                break;
        }

        if (station != null)
            tile.setStation(station);
    }

    // --- PERBAIKAN ISWALKABLE ---

    // Versi 1: Cek koordinat Integer
    public boolean isWalkable(int x, int y) {
        if (!isValidPosition(x, y))
            return false;

        // Pastikan tile tidak null sebelum memanggil methodnya
        if (tiles[x][y] == null)
            return false;

        return tiles[x][y].isWalkable();
    }

    // Versi 2: Cek koordinat Position (Yang dipanggil CollisionManager)
    public boolean isWalkable(Position pos) {
        return isWalkable(pos.getX(), pos.getY());
    }

    public boolean isValidPosition(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    public Tile getTile(int x, int y) {
        if (isValidPosition(x, y))
            return tiles[x][y];
        return null;
    }

    public Tile getTile(Position pos) {
        return getTile(pos.getX(), pos.getY());
    }

    public OrderManager getOrderManager() {
        return orderManager;
    }

    public List<Position> getSpawnPoints() {
        return spawnPoints;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
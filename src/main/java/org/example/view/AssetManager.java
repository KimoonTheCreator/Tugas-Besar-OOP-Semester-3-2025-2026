package org.example.view;

import javafx.scene.image.Image;
import org.example.model.enums.IngredientState;
import org.example.model.enums.TileType;
import org.example.model.items.*;
import org.example.model.map.Direction;
import org.example.model.stations.*;
import org.example.model.interfaces.CookingDevice;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class AssetManager {

    private static final Map<TileType, Image> STATIC_IMAGES = new HashMap<>();
    private static final Map<String, Image> DYNAMIC_IMAGES = new HashMap<>();
    private static final Map<String, Image> ITEM_IMAGES = new HashMap<>();
    private static final Map<String, Image> STATION_IMAGES = new HashMap<>();

    public static final int TILE_SIZE = 50;

    private static Image loadImage(String path) {
        InputStream is = AssetManager.class.getResourceAsStream(path);
        if (is == null) {
            // System.err.println("FATAL: Asset not found at path: " + path);
            return null;
        }
        return new Image(is, TILE_SIZE, TILE_SIZE, true, true);
    }

    public static void loadAssets() {
        try {
            // 1. ASET MAP UTAMA
            STATIC_IMAGES.put(TileType.FLOOR, loadImage("/res/mapelements/tile.png"));
            STATIC_IMAGES.put(TileType.WALL, loadImage("/res/mapelements/wall.png"));
            STATIC_IMAGES.put(TileType.CUTTING_STATION, loadImage("/res/station/cutting.png"));
            STATIC_IMAGES.put(TileType.ASSEMBLY_STATION, loadImage("/res/station/assembly.png"));
            STATIC_IMAGES.put(TileType.TRASH_STATION, loadImage("/res/station/trash.png"));
            STATIC_IMAGES.put(TileType.PLATE_STORAGE, loadImage("/res/station/platetable.png"));
            STATIC_IMAGES.put(TileType.SERVING_COUNTER, loadImage("/res/station/serving.png"));
            STATIC_IMAGES.put(TileType.COOKING_STATION, loadImage("/res/station/ovenoff.png"));
            STATIC_IMAGES.put(TileType.WASHING_STATION, loadImage("/res/station/sinkoff.png"));
            STATIC_IMAGES.put(TileType.INGREDIENT_STORAGE, loadImage("/res/station/ingredient.png"));

            // 2. ASET CRATE (Sesuai kode Anda)
            STATION_IMAGES.put("crate_tomat", loadImage("/res/ingretable/table_tomat.png"));
            STATION_IMAGES.put("crate_ayam", loadImage("/res/ingretable/table_ayam.png"));
            STATION_IMAGES.put("crate_sosis", loadImage("/res/ingretable/table_sosis.png"));
            STATION_IMAGES.put("crate_keju", loadImage("/res/ingretable/table_keju.png"));
            STATION_IMAGES.put("crate_adonan", loadImage("/res/ingretable/table_dough.png"));
            STATION_IMAGES.put("crate_default", loadImage("/res/station/ingredient.png"));

            // 3. ASET CHEF
            DYNAMIC_IMAGES.put("bob_down", loadImage("/res/player/bob_down.png"));
            DYNAMIC_IMAGES.put("bob_left", loadImage("/res/player/bob_left.png"));
            DYNAMIC_IMAGES.put("bob_right", loadImage("/res/player/bob_right.png"));
            DYNAMIC_IMAGES.put("bob_up", loadImage("/res/player/bob_up.png"));
            DYNAMIC_IMAGES.put("kebin_down", loadImage("/res/player/kebin_down.png"));
            DYNAMIC_IMAGES.put("kebin_left", loadImage("/res/player/kebin_left.png"));
            DYNAMIC_IMAGES.put("kebin_right", loadImage("/res/player/kebin_right.png"));
            DYNAMIC_IMAGES.put("kebin_up", loadImage("/res/player/kebin_up.png"));

            // 4. ASET INGREDIENT (Bahan Dasar)
            ITEM_IMAGES.put("tomat", loadImage("/res/ingredients/tomat.png"));
            ITEM_IMAGES.put("tomat_chopped", loadImage("/res/ingredients/tomat_chopped.png"));

            ITEM_IMAGES.put("chicken", loadImage("/res/ingredients/ayam.png"));
            ITEM_IMAGES.put("ayam", loadImage("/res/ingredients/ayam.png")); // Mapping ID-EN
            ITEM_IMAGES.put("ayam_chopped", loadImage("/res/ingredients/ayam_chopped.png"));

            ITEM_IMAGES.put("cheese", loadImage("/res/ingredients/keju.png"));
            ITEM_IMAGES.put("keju", loadImage("/res/ingredients/keju.png"));
            ITEM_IMAGES.put("keju_chopped", loadImage("/res/ingredients/keju_chopped.png"));

            ITEM_IMAGES.put("sausage", loadImage("/res/ingredients/sosis.png"));
            ITEM_IMAGES.put("sosis", loadImage("/res/ingredients/sosis.png"));
            ITEM_IMAGES.put("sosis_chopped", loadImage("/res/ingredients/sosis_chopped.png"));

            ITEM_IMAGES.put("dough", loadImage("/res/ingredients/adonan.png"));
            ITEM_IMAGES.put("adonan", loadImage("/res/ingredients/adonan.png"));
            ITEM_IMAGES.put("adonan_chopped", loadImage("/res/ingredients/dough_chopped.png"));

            // 5. ASET PIZZA (BARU - WAJIB ADA)
            // Path ini asumsi, silakan sesuaikan dengan lokasi file Anda

            // Margherita
            ITEM_IMAGES.put("pizza_margherita_raw", loadImage("/res/pizza/margherita_raw.png"));
            ITEM_IMAGES.put("pizza_margherita_cooked", loadImage("/res/pizza/margherita_good.png"));
            ITEM_IMAGES.put("pizza_margherita_burned", loadImage("/res/pizza/margherita_gosong.png"));

            // Chicken
            ITEM_IMAGES.put("pizza_chicken_raw", loadImage("/res/pizza/chicken_raw.png"));
            ITEM_IMAGES.put("pizza_chicken_cooked", loadImage("/res/pizza/ayam_good.png"));
            ITEM_IMAGES.put("pizza_chicken_burned", loadImage("/res/pizza/chicken_gosong.png"));

            // Sausage
            ITEM_IMAGES.put("pizza_sausage_raw", loadImage("/res/pizza/sosis_raw.png"));
            ITEM_IMAGES.put("pizza_sausage_cooked", loadImage("/res/pizza/sosis_good.png"));
            ITEM_IMAGES.put("pizza_sausage_burned", loadImage("/res/pizza/sosis_gosong.png"));

            // 6. PIRING & ALAT
            ITEM_IMAGES.put("plate_clean", loadImage("/res/piring/piring_clean.png"));
            ITEM_IMAGES.put("plate_dirty", loadImage("/res/piring/piring_dirty.png"));
            ITEM_IMAGES.put("plate_full", loadImage("/res/piring/piring_full.png"));

            System.out.println("Assets loaded successfully.");
        } catch (Exception e) {
            System.err.println("ERROR: Failed to load assets.");
            e.printStackTrace();
        }
    }

    // --- GETTER GAMBAR ---

    public static Image getTileImage(TileType type) {
        return STATIC_IMAGES.getOrDefault(type, STATIC_IMAGES.get(TileType.FLOOR));
    }

    public static Image getStationImage(Station station) {
        if (station == null)
            return null;

        if (station instanceof IngredientStorage) {
            IngredientStorage storage = (IngredientStorage) station;
            String ingName = storage.getIngredientName().toLowerCase();
            String key = "crate_" + ingName;
            return STATION_IMAGES.getOrDefault(key, STATION_IMAGES.get("crate_default"));
        }

        if (station instanceof CuttingStation)
            return STATIC_IMAGES.get(TileType.CUTTING_STATION);
        if (station instanceof CookingStation)
            return STATIC_IMAGES.get(TileType.COOKING_STATION);
        if (station instanceof AssemblyStation)
            return STATIC_IMAGES.get(TileType.ASSEMBLY_STATION);
        if (station instanceof WashingStation)
            return STATIC_IMAGES.get(TileType.WASHING_STATION);
        if (station instanceof TrashStation)
            return STATIC_IMAGES.get(TileType.TRASH_STATION);
        if (station instanceof PlateStorage)
            return STATIC_IMAGES.get(TileType.PLATE_STORAGE);
        if (station instanceof ServingCounter)
            return STATIC_IMAGES.get(TileType.SERVING_COUNTER);

        return STATIC_IMAGES.get(TileType.FLOOR);
    }

    public static Image getItemImage(Item item) {
        if (item == null)
            return null;

        // A. JIKA ITU PIZZA (LOGIC VISUAL PIZZA)
        if (item instanceof Pizza) {
            Pizza pizza = (Pizza) item;
            // Key format: pizza_[tipe]_[state]
            // Contoh: pizza_chicken_raw
            String key = "pizza_" + pizza.getType().toLowerCase();

            if (pizza.getState() == IngredientState.RAW)
                key += "_raw";
            else if (pizza.getState() == IngredientState.COOKED)
                key += "_cooked";
            else if (pizza.getState() == IngredientState.BURNED)
                key += "_burned";

            // Return gambar spesifik, kalau ga ada pake gambar plate_full sebagai fallback
            return ITEM_IMAGES.getOrDefault(key, ITEM_IMAGES.get("plate_full"));
        }

        // B. JIKA ITU INGREDIENT BIASA (Bahan Dasar)
        else if (item instanceof Ingredient) {
            Ingredient ing = (Ingredient) item;
            String key = ing.getName().toLowerCase();

            if (ing.getState() == IngredientState.CHOPPED) {
                key += "_chopped";
            }
            // Ingredient biasa ga punya state COOKED (karena dimasak pas udah jadi Pizza)
            // Tapi kalau ada logic lain, bisa ditambah else if

            return ITEM_IMAGES.getOrDefault(key, ITEM_IMAGES.get(ing.getName().toLowerCase()));
        }

        // C. JIKA ITU PLATE
        else if (item instanceof Plate) {
            Plate plate = (Plate) item;
            if (!plate.isClean()) {
                return ITEM_IMAGES.get("plate_dirty");
            }
            if (!plate.getContents().isEmpty()) {
                // Return Image Contentnya Langsung (Pizza Matang)
                // Kita ambil item pertama dari piring
                org.example.model.interfaces.Preparable content = plate.getContents().iterator().next();
                if (content instanceof Item) {
                    return getItemImage((Item) content);
                }
                return ITEM_IMAGES.get("plate_full");
            }
            return ITEM_IMAGES.get("plate_clean");
        }

        // D. JIKA ALAT LAIN
        else if (item instanceof CookingDevice) {
            return ITEM_IMAGES.get(item.getName().toLowerCase());
        }

        return ITEM_IMAGES.get(item.getName().toLowerCase());
    }

    public static Image getChefImage(String chefId, Direction direction) {
        String directionString = direction.toString();
        String key = chefId + "_" + directionString.toLowerCase();
        return DYNAMIC_IMAGES.getOrDefault(key, DYNAMIC_IMAGES.get(chefId + "_down"));
    }
}
package org.example.view;

import javafx.scene.image.Image;
import org.example.model.enums.TileType; 
import org.example.model.map.Direction;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class AssetManager {

    private static final Map<TileType, Image> STATIC_IMAGES = new HashMap<>();
    private static final Map<String, Image> DYNAMIC_IMAGES = new HashMap<>();
    
    public static final int TILE_SIZE = 50; 

    private static Image loadImage(String path) {
        InputStream is = AssetManager.class.getResourceAsStream(path);
        if (is == null) {
            System.err.println("FATAL: Asset not found at path: " + path);
            return null; 
        }
        return new Image(is, TILE_SIZE, TILE_SIZE, true, true);
    }

    /**
     * Metode utama untuk memuat semua aset saat aplikasi dimulai.
     */
    public static void loadAssets() {
        try {
            // 1. Aset Map Statis
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
            
            // 2. Aset Map Dinamis
            DYNAMIC_IMAGES.put("bob_down", loadImage("/res/player/bob_down.png"));
            DYNAMIC_IMAGES.put("bob_left", loadImage("/res/player/bob_left.png"));
            DYNAMIC_IMAGES.put("bob_right", loadImage("/res/player/bob_right.png"));
            DYNAMIC_IMAGES.put("bob_up", loadImage("/res/player/bob_up.png"));            
            DYNAMIC_IMAGES.put("kebin_down", loadImage("/res/player/kebin_down.png"));
            DYNAMIC_IMAGES.put("kebin_left", loadImage("/res/player/kebin_left.png"));
            DYNAMIC_IMAGES.put("kebin_right", loadImage("/res/player/kebin_right.png"));
            DYNAMIC_IMAGES.put("kebin_up", loadImage("/res/player/kebin_up.png"));
            
            System.out.println("Assets loaded successfully.");
        } catch (Exception e) {
            System.err.println("ERROR: Failed to load assets.");
            e.printStackTrace();
        }
    }

    public static Image getTileImage(TileType type) {
        return STATIC_IMAGES.getOrDefault(type, STATIC_IMAGES.get(TileType.FLOOR)); 
    }

    public static Image getChefImage(String chefId, Direction direction) {
        // 1. Ambil nama arah dalam string (misalnya "DOWN")
        String directionString = direction.toString(); 
        
        // 2. Buat key dengan mengonversi bagian arah menjadi huruf kecil
        // Output: "bob_down" atau "kebin_left"
        String key = chefId + "_" + directionString.toLowerCase(); // <--- INI PERBAIKANNYA!
        
        // Fallback: Gunakan key DOWN dalam huruf kecil jika gambar spesifik tidak ada
        return DYNAMIC_IMAGES.getOrDefault(key, DYNAMIC_IMAGES.get(chefId + "_down")); 
    }
}
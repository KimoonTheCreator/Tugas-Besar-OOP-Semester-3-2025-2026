package org.example.model.map;

import org.example.model.enums.TileType;
import org.example.model.stations.Station; // Tambahkan Import ini

/**
 * Kelas Tile untuk merepresentasikan satu kotak dalam map
 */
public class Tile {
    private TileType type;
    private Position position;

    // --- TAMBAHAN PENTING ---
    // Menyimpan referensi ke objek Station logic (jika ada)
    private Station station;

    public Tile(TileType type, int x, int y) {
        this.type = type;
        this.position = new Position(x, y);
        this.station = null; // Default kosong
    }

    public Tile(TileType type, Position position) {
        this.type = type;
        this.position = position;
        this.station = null; // Default kosong
    }

    // --- GETTER & SETTER BARU (Agar GameController tidak Error) ---
    public Station getStation() {
        return station;
    }

    public void setStation(Station station) {
        this.station = station;
        // Opsional: Jika station diset, ubah tipe tile otomatis (jika perlu)
        // if (station != null) this.type = TileType.STATION;
    }
    // -------------------------------------------------------------

    // Getter & Method Lama (Tetap Dipertahankan)
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
        // Tile bisa dilewati HANYA JIKA lantai dan tidak ada station menghalangi
        // (Kecuali jika Anda ingin Chef bisa jalan tembus meja, return true)
        return type.isWalkable() && station == null;
    }

    public char getSymbol() {
        return type.getSymbol();
    }

    @Override
    public String toString() {
        return String.valueOf(type.getSymbol());
    }
}
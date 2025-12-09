package org.example.model.map; 

public class Grid {
    
    private final int numRows; 
    private final int numCols;
    
    private final Tile[][] tiles; 

    /**
     * Konstruktor Grid.
     * @param numCols Jumlah kolom map (dimensi X).
     * @param numRows Jumlah baris map (dimensi Y).
     */
    public Grid(int numCols, int numRows) {
        this.numRows = numRows;
        this.numCols = numCols;
        this.tiles = new Tile[numRows][numCols]; 
    }

    /**
     * Menambahkan atau mengatur objek Tile pada posisi tertentu di Grid.
     * @param pos Objek Position (x, y) tempat Tile diletakkan.
     * @param tile Objek Tile yang akan diletakkan.
     */
    public void setTile(Position pos, Tile tile) {
        int x = pos.getX();
        int y = pos.getY();
        
        if (y >= 0 && y < numRows && x >= 0 && x < numCols) {
            this.tiles[y][x] = tile;
        } else {
            System.err.println("Error: Mencoba menempatkan Tile di luar batas Grid.");
        }
    }

    /**
     * Mendapatkan objek Tile pada posisi tertentu.
     * @param pos Objek Position (x, y) yang dicari.
     * @return Objek Tile di posisi tersebut, atau null jika di luar batas.
     */
    public Tile getTile(Position pos) {
        int x = pos.getX();
        int y = pos.getY();
        
        if (y >= 0 && y < numRows && x >= 0 && x < numCols) {
            return this.tiles[y][x];
        }
        return null;
    }

    public int getNumRows() {
        return numRows;
    }

    public int getNumCols() {
        return numCols;
    }
}
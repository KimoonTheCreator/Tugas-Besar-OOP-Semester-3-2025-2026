package org.example.model.map;

/**
 * Kelas Direction untuk menyimpan arah pergerakan (dx, dy)
 */
public class Direction {
    // Konstanta arah
    public static final Direction UP = new Direction(0, -1);
    public static final Direction DOWN = new Direction(0, 1);
    public static final Direction LEFT = new Direction(-1, 0);
    public static final Direction RIGHT = new Direction(1, 0);

    private final int dx;
    private final int dy;

    // Constructor
    public Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    // Getter
    public int getDx() {
        return dx;
    }

    public int getDy() {
        return dy;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Direction other = (Direction) obj;
        return dx == other.dx && dy == other.dy;
    }

    @Override
    public String toString() {
        if (this.equals(UP))
            return "UP";
        if (this.equals(DOWN))
            return "DOWN";
        if (this.equals(LEFT))
            return "LEFT";
        if (this.equals(RIGHT))
            return "RIGHT";
        return "Direction(" + dx + ", " + dy + ")";
    }
}
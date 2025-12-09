package org.example.model.entities;
import org.example.model.map.Position;

/**
 * Kelas abstrak GameObject sebagai parent dari semua objek dalam game
 */
public abstract class GameObject {
    protected Position position;

    public GameObject() {
        this.position = new Position(0, 0);
    }

    public GameObject(Position position) {
        this.position = position;
    }

    public GameObject(int x, int y) {
        this.position = new Position(x, y);
    }

    // Getter dan Setter
    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public int getX() {
        return position.getX();
    }

    public int getY() {
        return position.getY();
    }
}

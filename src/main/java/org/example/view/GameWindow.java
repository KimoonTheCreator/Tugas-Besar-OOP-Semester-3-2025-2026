package org.example.view;

import org.example.model.entities.Chef;
import org.example.model.enums.Command;
import org.example.model.enums.Key;
import org.example.model.enums.TileType;
import org.example.model.input.InputHandler;
import org.example.model.map.Direction;
import org.example.model.map.GameMap;
import org.example.model.map.Position;
import org.example.model.map.Tile;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Kelas GameWindow untuk tampilan game
 * Menampilkan map dan chef yang bisa dikontrol
 */
public class GameWindow extends JPanel implements KeyListener {

    private static final int TILE_SIZE = 50;

    // Game objects
    private List<Chef> chefs;
    private int activeChefIndex;
    private GameMap gameMap;
    private InputHandler inputHandler;
    private JFrame frame;

    public GameWindow() {
        // Inisialisasi map
        this.gameMap = new GameMap();

        // Inisialisasi chefs
        this.chefs = new ArrayList<>();
        this.activeChefIndex = 0;

        // Ambil spawn points dari map
        List<Position> spawnPoints = gameMap.getSpawnPoints();

        // Buat Chef 1
        Position spawn1 = spawnPoints.size() > 0 ? spawnPoints.get(0) : new Position(1, 1);
        Chef chef1 = new Chef("chef1", "Gordon", new Position(spawn1.getX(), spawn1.getY()));
        chef1.setIsActive(true);
        chefs.add(chef1);

        // Buat Chef 2
        Position spawn2 = spawnPoints.size() > 1 ? spawnPoints.get(1) : new Position(2, 1);
        Chef chef2 = new Chef("chef2", "Jamie", new Position(spawn2.getX(), spawn2.getY()));
        chef2.setIsActive(false);
        chefs.add(chef2);

        this.inputHandler = new InputHandler();

        // Setup panel
        int panelWidth = gameMap.getWidth() * TILE_SIZE;
        int panelHeight = gameMap.getHeight() * TILE_SIZE + 100;
        setPreferredSize(new Dimension(panelWidth, panelHeight));
        setBackground(new Color(30, 30, 30));
        setFocusable(true);
        setFocusTraversalKeysEnabled(false); // Agar TAB bisa dipakai
        addKeyListener(this);

        // Setup frame
        frame = new JFrame("Cooking Game - WASD: Move | TAB: Switch Chef | ESC: Exit");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);

        // Print map untuk debug
        gameMap.printMap();
        System.out.println("Spawn points: " + spawnPoints);

        requestFocusInWindow();
    }

    // Dapatkan chef yang aktif
    private Chef getActiveChef() {
        return chefs.get(activeChefIndex);
    }

    // Switch ke chef berikutnya
    private void switchChef() {
        chefs.get(activeChefIndex).setIsActive(false);
        activeChefIndex = (activeChefIndex + 1) % chefs.size();
        chefs.get(activeChefIndex).setIsActive(true);
        System.out.println("Switched to: " + getActiveChef().getName());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawMap(g2d);

        // Gambar semua chef
        for (int i = 0; i < chefs.size(); i++) {
            drawChef(g2d, chefs.get(i), i == activeChefIndex);
        }

        drawInfo(g2d);
    }

    // Gambar map
    private void drawMap(Graphics2D g) {
        for (int x = 0; x < gameMap.getWidth(); x++) {
            for (int y = 0; y < gameMap.getHeight(); y++) {
                Tile tile = gameMap.getTile(x, y);
                drawTile(g, tile, x, y);
            }
        }
    }

    // Gambar satu tile
    private void drawTile(Graphics2D g, Tile tile, int x, int y) {
        int screenX = x * TILE_SIZE;
        int screenY = y * TILE_SIZE;
        TileType type = tile.getType();

        // Warna background berdasarkan tipe
        g.setColor(getTileColor(type));
        g.fillRect(screenX, screenY, TILE_SIZE, TILE_SIZE);

        // Detail tile
        drawTileDetails(g, type, screenX, screenY);

        // Grid
        g.setColor(new Color(60, 60, 60));
        g.drawRect(screenX, screenY, TILE_SIZE, TILE_SIZE);
    }

    // Warna untuk setiap tipe tile
    private Color getTileColor(TileType type) {
        switch (type) {
            case WALL:
                return new Color(60, 50, 40);
            case FLOOR:
                return new Color(180, 160, 140);
            case SPAWN_POINT:
                return new Color(150, 200, 150);
            case CUTTING_STATION:
                return new Color(200, 150, 100);
            case COOKING_STATION:
                return new Color(200, 80, 60);
            case ASSEMBLY_STATION:
                return new Color(100, 150, 200);
            case SERVING_COUNTER:
                return new Color(150, 100, 180);
            case WASHING_STATION:
                return new Color(100, 180, 220);
            case INGREDIENT_STORAGE:
                return new Color(180, 200, 100);
            case PLATE_STORAGE:
                return new Color(220, 220, 220);
            case TRASH_STATION:
                return new Color(100, 100, 100);
            default:
                return new Color(150, 150, 150);
        }
    }

    // Gambar detail tile
    private void drawTileDetails(Graphics2D g, TileType type, int screenX, int screenY) {
        int centerX = screenX + TILE_SIZE / 2;
        g.setFont(new Font("Arial", Font.BOLD, 10));

        switch (type) {
            case WALL:
                g.setColor(new Color(80, 70, 60));
                for (int i = 0; i < 3; i++) {
                    g.drawRect(screenX + (i % 2) * 12 + 2, screenY + i * 16 + 2, 20, 14);
                }
                break;
            case CUTTING_STATION:
                g.setColor(Color.WHITE);
                g.drawString("CUT", centerX - 12, screenY + TILE_SIZE - 5);
                break;
            case COOKING_STATION:
                g.setColor(new Color(255, 150, 50));
                g.fillOval(centerX - 8, screenY + 15, 16, 20);
                g.setColor(Color.WHITE);
                g.drawString("STOVE", centerX - 18, screenY + TILE_SIZE - 5);
                break;
            case ASSEMBLY_STATION:
                g.setColor(Color.WHITE);
                g.fillOval(centerX - 12, screenY + 15, 24, 16);
                g.setColor(new Color(50, 50, 50));
                g.drawString("PREP", centerX - 14, screenY + TILE_SIZE - 5);
                break;
            case SERVING_COUNTER:
                g.setColor(Color.WHITE);
                g.drawString("SERVE", centerX - 18, screenY + TILE_SIZE - 5);
                break;
            case WASHING_STATION:
                g.setColor(new Color(50, 150, 255));
                g.fillOval(centerX - 8, screenY + 12, 16, 20);
                g.setColor(Color.WHITE);
                g.drawString("SINK", centerX - 14, screenY + TILE_SIZE - 5);
                break;
            case INGREDIENT_STORAGE:
                g.setColor(new Color(139, 90, 43));
                g.fillRect(centerX - 12, screenY + 12, 24, 20);
                g.setColor(Color.WHITE);
                g.drawString("INGR", centerX - 14, screenY + TILE_SIZE - 5);
                break;
            case PLATE_STORAGE:
                g.setColor(Color.WHITE);
                g.fillOval(centerX - 10, screenY + 14, 20, 8);
                g.fillOval(centerX - 10, screenY + 20, 20, 8);
                g.setColor(new Color(50, 50, 50));
                g.drawString("PLATE", centerX - 18, screenY + TILE_SIZE - 5);
                break;
            case TRASH_STATION:
                g.setColor(new Color(70, 70, 70));
                g.fillRect(centerX - 10, screenY + 12, 20, 20);
                g.setColor(Color.WHITE);
                g.drawString("TRASH", centerX - 18, screenY + TILE_SIZE - 5);
                break;
            case SPAWN_POINT:
                g.setColor(new Color(50, 150, 50));
                g.drawString("SPAWN", centerX - 20, screenY + TILE_SIZE / 2 + 5);
                break;
        }
    }

    // Gambar chef
    private void drawChef(Graphics2D g, Chef chef, boolean isActive) {
        int screenX = chef.getX() * TILE_SIZE;
        int screenY = chef.getY() * TILE_SIZE;

        // Highlight chef aktif
        if (isActive) {
            g.setColor(new Color(255, 255, 0, 80));
            g.fillRect(screenX, screenY, TILE_SIZE, TILE_SIZE);
            g.setColor(new Color(255, 200, 0));
            g.setStroke(new BasicStroke(3));
            g.drawRect(screenX + 1, screenY + 1, TILE_SIZE - 2, TILE_SIZE - 2);
            g.setStroke(new BasicStroke(1));
        }

        // Body
        Color bodyColor = isActive ? Color.WHITE : new Color(200, 200, 200);
        g.setColor(bodyColor);
        g.fillOval(screenX + 12, screenY + 20, TILE_SIZE - 24, TILE_SIZE - 28);

        // Topi chef
        Color hatColor = chef.getId().equals("chef1") ? Color.WHITE : new Color(255, 200, 200);
        g.setColor(hatColor);
        g.fillRoundRect(screenX + 10, screenY + 5, TILE_SIZE - 20, 20, 8, 8);

        // Wajah
        g.setColor(new Color(255, 210, 170));
        g.fillOval(screenX + 15, screenY + 22, TILE_SIZE - 30, 18);

        // Mata
        g.setColor(Color.BLACK);
        Direction dir = chef.getDirection();
        int eyeOffsetX = 0, eyeOffsetY = 0;
        if (dir.equals(Direction.LEFT))
            eyeOffsetX = -2;
        else if (dir.equals(Direction.RIGHT))
            eyeOffsetX = 2;
        else if (dir.equals(Direction.UP))
            eyeOffsetY = -2;
        else if (dir.equals(Direction.DOWN))
            eyeOffsetY = 2;
        g.fillOval(screenX + 18 + eyeOffsetX, screenY + 27 + eyeOffsetY, 4, 4);
        g.fillOval(screenX + 28 + eyeOffsetX, screenY + 27 + eyeOffsetY, 4, 4);

        // Arrow arah
        g.setColor(isActive ? new Color(255, 80, 80) : new Color(150, 80, 80));
        drawArrow(g, screenX, screenY, dir);

        // Nama
        g.setFont(new Font("Arial", Font.BOLD, 9));
        g.setColor(isActive ? Color.YELLOW : Color.GRAY);
        g.drawString(chef.getName().substring(0, Math.min(5, chef.getName().length())),
                screenX + 8, screenY + TILE_SIZE + 10);
    }

    // Gambar panah arah
    private void drawArrow(Graphics2D g, int screenX, int screenY, Direction dir) {
        int centerX = screenX + TILE_SIZE / 2;
        int centerY = screenY + TILE_SIZE / 2;
        int size = 6;

        if (dir.equals(Direction.UP)) {
            g.fillPolygon(new int[] { centerX, centerX - size, centerX + size },
                    new int[] { screenY + 2, screenY + size + 6, screenY + size + 6 }, 3);
        } else if (dir.equals(Direction.DOWN)) {
            g.fillPolygon(new int[] { centerX, centerX - size, centerX + size },
                    new int[] { screenY + TILE_SIZE - 2, screenY + TILE_SIZE - size - 6,
                            screenY + TILE_SIZE - size - 6 },
                    3);
        } else if (dir.equals(Direction.LEFT)) {
            g.fillPolygon(new int[] { screenX + 2, screenX + size + 6, screenX + size + 6 },
                    new int[] { centerY, centerY - size, centerY + size }, 3);
        } else if (dir.equals(Direction.RIGHT)) {
            g.fillPolygon(
                    new int[] { screenX + TILE_SIZE - 2, screenX + TILE_SIZE - size - 6,
                            screenX + TILE_SIZE - size - 6 },
                    new int[] { centerY, centerY - size, centerY + size }, 3);
        }
    }

    // Gambar info bar
    private void drawInfo(Graphics2D g) {
        int infoY = gameMap.getHeight() * TILE_SIZE;
        Chef activeChef = getActiveChef();

        // Background
        g.setColor(new Color(30, 30, 30));
        g.fillRect(0, infoY, gameMap.getWidth() * TILE_SIZE, 100);

        // Info chef
        g.setColor(Color.YELLOW);
        g.setFont(new Font("Consolas", Font.BOLD, 14));
        g.drawString("Chef: " + activeChef.getName() + " (" + (activeChefIndex + 1) + "/" + chefs.size() + ")", 10,
                infoY + 18);

        // Posisi
        g.setColor(Color.WHITE);
        g.drawString("Position: " + activeChef.getPosition() + " | Facing: " + activeChef.getDirection(), 10,
                infoY + 38);

        // Tile saat ini
        Tile currentTile = gameMap.getTile(activeChef.getPosition());
        if (currentTile != null) {
            g.setColor(new Color(100, 200, 100));
            g.drawString("Standing on: " + currentTile.getType().getDisplayName(), 10, infoY + 55);
        }

        // Kontrol
        g.setColor(new Color(120, 120, 120));
        g.setFont(new Font("Consolas", Font.PLAIN, 11));
        g.drawString("WASD=Move | TAB=Switch | E=Interact | V=Pickup | ESC=Exit", 10, infoY + 75);
    }

    // Konversi KeyEvent ke enum Key
    private Key convertKey(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
                return Key.W;
            case KeyEvent.VK_A:
                return Key.A;
            case KeyEvent.VK_S:
                return Key.S;
            case KeyEvent.VK_D:
                return Key.D;
            case KeyEvent.VK_E:
                return Key.E;
            case KeyEvent.VK_V:
                return Key.V;
            case KeyEvent.VK_P:
                return Key.P;
            case KeyEvent.VK_TAB:
                return Key.TAB;
            default:
                return null;
        }
    }

    // Cek apakah posisi ditempati chef lain
    private boolean isOccupied(int x, int y) {
        for (int i = 0; i < chefs.size(); i++) {
            if (i != activeChefIndex && chefs.get(i).getX() == x && chefs.get(i).getY() == y) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            System.out.println("Game closed!");
            System.exit(0);
        }

        Key key = convertKey(e);
        if (key == null)
            return;

        Command command = inputHandler.handleInput(key);
        Chef activeChef = getActiveChef();

        if (command == Command.SWITCH_CHEF) {
            switchChef();
        } else if (inputHandler.isMovementCommand(command)) {
            Direction dir = inputHandler.getDirectionFromCommand(command);
            int newX = activeChef.getX() + dir.getDx();
            int newY = activeChef.getY() + dir.getDy();

            // Cek arah dulu
            if (!activeChef.getDirection().equals(dir)) {
                activeChef.setDirection(dir);
            } else if (gameMap.isWalkable(newX, newY) && !isOccupied(newX, newY)) {
                activeChef.move(dir);
            }
        } else {
            System.out.println("Command: " + command);
        }

        repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        getActiveChef().setIdle();
        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GameWindow::new);
    }
}

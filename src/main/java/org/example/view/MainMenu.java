package org.example.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

/**
 * MainMenu - Cartoon Game UI Style
 */
public class MainMenu extends JPanel {

    private JFrame frame;
    private JButton playButton;
    private JButton howToPlayButton;
    private JButton exitButton;

    // Cartoon Game Color Palette
    private static final Color BG_ORANGE_TOP = new Color(255, 180, 100);
    private static final Color BG_ORANGE_BOTTOM = new Color(200, 90, 50);
    private static final Color BUTTON_YELLOW_TOP = new Color(255, 220, 80);
    private static final Color BUTTON_YELLOW_BOTTOM = new Color(255, 180, 50);
    private static final Color BUTTON_BORDER = new Color(120, 60, 30);
    private static final Color BUTTON_BORDER_INNER = new Color(180, 90, 40);
    private static final Color TEXT_WHITE = new Color(255, 255, 255);
    private static final Color TEXT_OUTLINE = new Color(100, 50, 25);
    private static final Color MOUNTAIN_COLOR = new Color(255, 160, 80);
    private static final Color MOUNTAIN_DARK = new Color(230, 130, 60);
    private static final Color SUN_GLOW = new Color(255, 240, 200, 100);

    private static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 58);
    private static final Font BUTTON_FONT = new Font("SansSerif", Font.BOLD, 26);

    public MainMenu() {
        setPreferredSize(new Dimension(800, 600));
        setLayout(null);

        setupFrame();
        setupButtons();
    }

    private void setupFrame() {
        frame = new JFrame("NIMONSCOOKED");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
    }

    private void setupButtons() {
        int buttonWidth = 300;
        int buttonHeight = 65;
        int centerX = (800 - buttonWidth) / 2;
        int startY = 280;
        int gap = 85;

        playButton = createGameButton("PLAY", centerX, startY, buttonWidth, buttonHeight);
        playButton.addActionListener(e -> startGame());
        add(playButton);

        howToPlayButton = createGameButton("HOW TO PLAY", centerX, startY + gap, buttonWidth, buttonHeight);
        howToPlayButton.addActionListener(e -> showHowToPlay());
        add(howToPlayButton);

        exitButton = createGameButton("EXIT", centerX, startY + gap * 2, buttonWidth, buttonHeight);
        exitButton.addActionListener(e -> exitGame());
        add(exitButton);
    }

    private JButton createGameButton(String text, int x, int y, int width, int height) {
        JButton button = new JButton(text) {
            private boolean isHovered = false;
            private boolean isPressed = false;

            {
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        isHovered = true;
                        repaint();
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        isHovered = false;
                        isPressed = false;
                        repaint();
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {
                        isPressed = true;
                        repaint();
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        isPressed = false;
                        repaint();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int yOffset = isPressed ? 3 : 0;

                // Outer dark border (shadow effect)
                g2d.setColor(BUTTON_BORDER);
                g2d.fill(new RoundRectangle2D.Float(0, 4, getWidth(), getHeight() - 4, 40, 40));

                // Main button body
                GradientPaint buttonGradient = new GradientPaint(
                        0, yOffset, isHovered ? brighter(BUTTON_YELLOW_TOP, 20) : BUTTON_YELLOW_TOP,
                        0, getHeight() - 8 + yOffset,
                        isHovered ? brighter(BUTTON_YELLOW_BOTTOM, 20) : BUTTON_YELLOW_BOTTOM);
                g2d.setPaint(buttonGradient);
                g2d.fill(new RoundRectangle2D.Float(4, yOffset + 4, getWidth() - 8, getHeight() - 12, 35, 35));

                // Inner border
                g2d.setColor(BUTTON_BORDER_INNER);
                g2d.setStroke(new BasicStroke(3));
                g2d.draw(new RoundRectangle2D.Float(4, yOffset + 4, getWidth() - 9, getHeight() - 13, 35, 35));

                // Glossy highlight at top
                GradientPaint glossy = new GradientPaint(
                        0, yOffset + 8, new Color(255, 255, 255, 120),
                        0, getHeight() / 2 + yOffset, new Color(255, 255, 255, 0));
                g2d.setPaint(glossy);
                g2d.fill(new RoundRectangle2D.Float(8, yOffset + 8, getWidth() - 16, getHeight() / 2 - 10, 30, 30));

                // Text with outline
                g2d.setFont(BUTTON_FONT);
                FontMetrics fm = g2d.getFontMetrics();
                int textX = (getWidth() - fm.stringWidth(getText())) / 2;
                int textY = (getHeight() + fm.getAscent() - fm.getDescent()) / 2 - 2 + yOffset;

                // Draw outline (multiple directions)
                g2d.setColor(TEXT_OUTLINE);
                for (int dx = -2; dx <= 2; dx++) {
                    for (int dy = -2; dy <= 2; dy++) {
                        if (dx != 0 || dy != 0) {
                            g2d.drawString(getText(), textX + dx, textY + dy);
                        }
                    }
                }

                // Main text
                g2d.setColor(TEXT_WHITE);
                g2d.drawString(getText(), textX, textY);

                g2d.dispose();
            }
        };

        button.setBounds(x, y, width, height);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    private Color brighter(Color c, int amount) {
        return new Color(
                Math.min(255, c.getRed() + amount),
                Math.min(255, c.getGreen() + amount),
                Math.min(255, c.getBlue() + amount));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        drawBackground(g2d);
        drawTitle(g2d);
        drawFooter(g2d);
    }

    private void drawBackground(Graphics2D g2d) {
        // Main gradient background
        GradientPaint bgGradient = new GradientPaint(0, 0, BG_ORANGE_TOP, 0, getHeight(), BG_ORANGE_BOTTOM);
        g2d.setPaint(bgGradient);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // Sun glow
        for (int i = 5; i > 0; i--) {
            g2d.setColor(new Color(255, 240, 200, 15 * i));
            int size = 200 + i * 40;
            g2d.fillOval(getWidth() / 2 - size / 2, 30 - size / 3, size, size);
        }

        // Background mountains/hills
        g2d.setColor(MOUNTAIN_COLOR);

        // Left mountain
        int[] xPoints1 = { -50, 150, 350 };
        int[] yPoints1 = { getHeight(), 180, getHeight() };
        g2d.fillPolygon(xPoints1, yPoints1, 3);

        // Center-left mountain
        g2d.setColor(MOUNTAIN_DARK);
        int[] xPoints2 = { 100, 280, 460 };
        int[] yPoints2 = { getHeight(), 220, getHeight() };
        g2d.fillPolygon(xPoints2, yPoints2, 3);

        // Right mountain
        g2d.setColor(MOUNTAIN_COLOR);
        int[] xPoints3 = { 400, 600, 800 };
        int[] yPoints3 = { getHeight(), 200, getHeight() };
        g2d.fillPolygon(xPoints3, yPoints3, 3);

        // Far right mountain
        g2d.setColor(MOUNTAIN_DARK);
        int[] xPoints4 = { 550, 750, 950 };
        int[] yPoints4 = { getHeight(), 250, getHeight() };
        g2d.fillPolygon(xPoints4, yPoints4, 3);

        // Ground hill
        g2d.setColor(BG_ORANGE_BOTTOM);
        g2d.fillOval(-100, getHeight() - 80, getWidth() + 200, 200);
    }

    private void drawTitle(Graphics2D g2d) {
        String line1 = "NIMONS";
        String line2 = "COOKED";
        g2d.setFont(TITLE_FONT);
        FontMetrics fm = g2d.getFontMetrics();

        int line1X = (getWidth() - fm.stringWidth(line1)) / 2;
        int line1Y = 100;
        int line2X = (getWidth() - fm.stringWidth(line2)) / 2;
        int line2Y = 170;

        // Draw title with thick outline
        drawOutlinedText(g2d, line1, line1X, line1Y);
        drawOutlinedText(g2d, line2, line2X, line2Y);
    }

    private void drawOutlinedText(Graphics2D g2d, String text, int x, int y) {
        // Dark brown outline
        g2d.setColor(TEXT_OUTLINE);
        for (int dx = -3; dx <= 3; dx++) {
            for (int dy = -3; dy <= 3; dy++) {
                if (dx != 0 || dy != 0) {
                    g2d.drawString(text, x + dx, y + dy);
                }
            }
        }

        // White fill with slight yellow tint
        GradientPaint textGradient = new GradientPaint(
                x, y - 40, new Color(255, 255, 255),
                x, y + 10, new Color(255, 240, 200));
        g2d.setPaint(textGradient);
        g2d.drawString(text, x, y);
    }

    private void drawFooter(Graphics2D g2d) {
        g2d.setFont(new Font("SansSerif", Font.BOLD, 12));
        g2d.setColor(new Color(150, 80, 40));
        String footer = "Tugas Besar OOP - Semester 3 - 2025/2026";
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(footer, (getWidth() - fm.stringWidth(footer)) / 2, getHeight() - 15);
    }

    private void startGame() {
        frame.dispose();
        SwingUtilities.invokeLater(() -> new GameWindow());
    }

    private void showHowToPlay() {
        HowToPlayDialog dialog = new HowToPlayDialog(frame);
        dialog.setVisible(true);
    }

    private void exitGame() {
        System.exit(0);
    }

    // How to Play Dialog - Matching cartoon style
    public static class HowToPlayDialog extends JDialog {
        private static final Color BUTTON_BORDER = new Color(120, 60, 30);
        private static final Color BUTTON_YELLOW_TOP = new Color(255, 220, 80);
        private static final Color BUTTON_YELLOW_BOTTOM = new Color(255, 180, 50);
        private static final Color TEXT_OUTLINE = new Color(100, 50, 25);

        public HowToPlayDialog(Window parent) {
            super(parent, "How to Play", ModalityType.APPLICATION_MODAL);
            setSize(420, 450);
            setLocationRelativeTo(parent);
            setResizable(false);
            setUndecorated(true);
            setBackground(new Color(0, 0, 0, 0));

            JPanel contentPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    // Outer border
                    g2d.setColor(BUTTON_BORDER);
                    g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 30, 30));

                    // Inner background
                    GradientPaint bg = new GradientPaint(
                            0, 0, new Color(255, 230, 180),
                            0, getHeight(), new Color(255, 200, 140));
                    g2d.setPaint(bg);
                    g2d.fill(new RoundRectangle2D.Float(5, 5, getWidth() - 10, getHeight() - 10, 25, 25));

                    // Header area
                    g2d.setColor(new Color(255, 180, 100));
                    g2d.fill(new RoundRectangle2D.Float(5, 5, getWidth() - 10, 55, 25, 25));
                    g2d.fillRect(5, 35, getWidth() - 10, 25);
                }
            };
            contentPanel.setLayout(new BorderLayout(10, 10));
            contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
            contentPanel.setOpaque(false);

            // Title with outline
            JLabel titleLabel = new JLabel("HOW TO PLAY", SwingConstants.CENTER) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setFont(new Font("SansSerif", Font.BOLD, 24));
                    FontMetrics fm = g2d.getFontMetrics();
                    int x = (getWidth() - fm.stringWidth(getText())) / 2;
                    int y = fm.getAscent() + 5;

                    // Outline
                    g2d.setColor(TEXT_OUTLINE);
                    for (int dx = -2; dx <= 2; dx++) {
                        for (int dy = -2; dy <= 2; dy++) {
                            if (dx != 0 || dy != 0)
                                g2d.drawString(getText(), x + dx, y + dy);
                        }
                    }
                    g2d.setColor(Color.WHITE);
                    g2d.drawString(getText(), x, y);
                }
            };
            titleLabel.setPreferredSize(new Dimension(380, 40));
            contentPanel.add(titleLabel, BorderLayout.NORTH);

            // Instructions
            JPanel instructionsPanel = new JPanel();
            instructionsPanel.setLayout(new BoxLayout(instructionsPanel, BoxLayout.Y_AXIS));
            instructionsPanel.setOpaque(false);
            instructionsPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

            String[][] instructions = {
                    { "Movement", "W A S D" },
                    { "Switch Chef", "TAB" },
                    { "Interact", "E" },
                    { "Pickup / Drop", "Q / F" },
                    { "Cut / Wash", "C / X" },
                    { "Cook", "R" },
                    { "Menu", "M / ESC" },
                    { "", "" },
                    { "STATIONS", "" },
                    { "Cutting", "Cut ingredients (C)" },
                    { "Cooking", "Cook on stove (R)" },
                    { "Washing", "Clean plates (X)" },
                    { "Serving", "Drop dish (F)" }
            };

            for (String[] ins : instructions) {
                if (ins[0].isEmpty() && ins[1].isEmpty()) {
                    instructionsPanel.add(Box.createVerticalStrut(10));
                    continue;
                }

                JPanel row = new JPanel(new BorderLayout(10, 0));
                row.setOpaque(false);
                row.setMaximumSize(new Dimension(380, 26));

                JLabel left = new JLabel(ins[0]);
                left.setFont(new Font("SansSerif", ins[1].isEmpty() ? Font.BOLD : Font.BOLD, 14));
                left.setForeground(ins[1].isEmpty() ? new Color(180, 80, 30) : new Color(120, 60, 30));
                left.setPreferredSize(new Dimension(110, 22));
                row.add(left, BorderLayout.WEST);

                JLabel right = new JLabel(ins[1]);
                right.setFont(new Font("SansSerif", Font.PLAIN, 14));
                right.setForeground(new Color(80, 50, 30));
                row.add(right, BorderLayout.CENTER);

                instructionsPanel.add(row);
                instructionsPanel.add(Box.createVerticalStrut(4));
            }

            JScrollPane scroll = new JScrollPane(instructionsPanel);
            scroll.setOpaque(false);
            scroll.getViewport().setOpaque(false);
            scroll.setBorder(null);
            contentPanel.add(scroll, BorderLayout.CENTER);

            // Close button
            JButton closeBtn = createDialogButton("GOT IT!");
            closeBtn.addActionListener(e -> dispose());

            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            btnPanel.setOpaque(false);
            btnPanel.add(closeBtn);
            contentPanel.add(btnPanel, BorderLayout.SOUTH);

            setContentPane(contentPanel);
        }

        private JButton createDialogButton(String text) {
            JButton button = new JButton(text) {
                private boolean isHovered = false;
                {
                    addMouseListener(new MouseAdapter() {
                        public void mouseEntered(MouseEvent e) {
                            isHovered = true;
                            repaint();
                        }

                        public void mouseExited(MouseEvent e) {
                            isHovered = false;
                            repaint();
                        }
                    });
                }

                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    // Border
                    g2d.setColor(BUTTON_BORDER);
                    g2d.fill(new RoundRectangle2D.Float(0, 3, getWidth(), getHeight() - 3, 20, 20));

                    // Body
                    GradientPaint gp = new GradientPaint(0, 0,
                            isHovered ? new Color(255, 230, 100) : BUTTON_YELLOW_TOP,
                            0, getHeight() - 6,
                            isHovered ? new Color(255, 190, 60) : BUTTON_YELLOW_BOTTOM);
                    g2d.setPaint(gp);
                    g2d.fill(new RoundRectangle2D.Float(3, 3, getWidth() - 6, getHeight() - 9, 18, 18));

                    // Text
                    g2d.setFont(new Font("SansSerif", Font.BOLD, 16));
                    FontMetrics fm = g2d.getFontMetrics();
                    int tx = (getWidth() - fm.stringWidth(getText())) / 2;
                    int ty = (getHeight() + fm.getAscent() - fm.getDescent()) / 2 - 2;

                    g2d.setColor(TEXT_OUTLINE);
                    for (int dx = -2; dx <= 2; dx++) {
                        for (int dy = -2; dy <= 2; dy++) {
                            if (dx != 0 || dy != 0)
                                g2d.drawString(getText(), tx + dx, ty + dy);
                        }
                    }
                    g2d.setColor(Color.WHITE);
                    g2d.drawString(getText(), tx, ty);

                    g2d.dispose();
                }
            };
            button.setPreferredSize(new Dimension(140, 45));
            button.setFocusPainted(false);
            button.setBorderPainted(false);
            button.setContentAreaFilled(false);
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            return button;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainMenu::new);
    }
}

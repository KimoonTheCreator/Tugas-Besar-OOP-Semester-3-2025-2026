package org.example.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

/**
 * InGameMenu - Cartoon Game UI Style (matching MainMenu)
 */
public class InGameMenu extends JDialog {

    private static final Color BUTTON_BORDER = new Color(120, 60, 30);
    private static final Color BUTTON_YELLOW_TOP = new Color(255, 220, 80);
    private static final Color BUTTON_YELLOW_BOTTOM = new Color(255, 180, 50);
    private static final Color TEXT_OUTLINE = new Color(100, 50, 25);

    private Runnable onResume;
    private Runnable onMainMenu;

    public InGameMenu(JFrame parent, Runnable onResume, Runnable onMainMenu) {
        super(parent, "Menu", ModalityType.APPLICATION_MODAL);
        this.onResume = onResume;
        this.onMainMenu = onMainMenu;

        setSize(350, 400);
        setLocationRelativeTo(parent);
        setResizable(false);
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));

        setupContent();
        setupKeyBindings();
    }

    private void setupContent() {
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

                // Header
                g2d.setColor(new Color(255, 180, 100));
                g2d.fill(new RoundRectangle2D.Float(5, 5, getWidth() - 10, 60, 25, 25));
                g2d.fillRect(5, 40, getWidth() - 10, 25);
            }
        };
        contentPanel.setLayout(null);
        contentPanel.setOpaque(false);

        // Title
        JLabel title = new JLabel("PAUSED", SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setFont(new Font("SansSerif", Font.BOLD, 28));
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = fm.getAscent() + 8;

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
        title.setBounds(0, 10, 350, 45);
        contentPanel.add(title);

        int buttonWidth = 220;
        int centerX = (350 - buttonWidth) / 2;
        int startY = 85;
        int gap = 65;

        JButton resumeBtn = createButton("RESUME", centerX, startY, buttonWidth);
        resumeBtn.addActionListener(e -> resumeGame());
        contentPanel.add(resumeBtn);

        JButton helpBtn = createButton("HOW TO PLAY", centerX, startY + gap, buttonWidth);
        helpBtn.addActionListener(e -> showHowToPlay());
        contentPanel.add(helpBtn);

        JButton menuBtn = createButton("MAIN MENU", centerX, startY + gap * 2, buttonWidth);
        menuBtn.addActionListener(e -> goToMainMenu());
        contentPanel.add(menuBtn);

        JButton exitBtn = createButton("EXIT GAME", centerX, startY + gap * 3, buttonWidth);
        exitBtn.addActionListener(e -> exitGame());
        contentPanel.add(exitBtn);

        // Hint
        JLabel hint = new JLabel("Press M or ESC to resume", SwingConstants.CENTER);
        hint.setBounds(0, 360, 350, 20);
        hint.setFont(new Font("SansSerif", Font.PLAIN, 11));
        hint.setForeground(new Color(120, 70, 40));
        contentPanel.add(hint);

        setContentPane(contentPanel);
    }

    private void setupKeyBindings() {
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "close");
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_M, 0), "close");
        getRootPane().getActionMap().put("close", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resumeGame();
            }
        });
    }

    private JButton createButton(String text, int x, int y, int width) {
        JButton button = new JButton(text) {
            private boolean isHovered = false;
            private boolean isPressed = false;

            {
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) {
                        isHovered = true;
                        repaint();
                    }

                    public void mouseExited(MouseEvent e) {
                        isHovered = false;
                        isPressed = false;
                        repaint();
                    }

                    public void mousePressed(MouseEvent e) {
                        isPressed = true;
                        repaint();
                    }

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

                int yOffset = isPressed ? 2 : 0;

                // Border/shadow
                g2d.setColor(BUTTON_BORDER);
                g2d.fill(new RoundRectangle2D.Float(0, 3, getWidth(), getHeight() - 3, 25, 25));

                // Body
                Color top = isHovered ? new Color(255, 230, 100) : BUTTON_YELLOW_TOP;
                Color bottom = isHovered ? new Color(255, 190, 60) : BUTTON_YELLOW_BOTTOM;
                GradientPaint gp = new GradientPaint(0, yOffset, top, 0, getHeight() - 6 + yOffset, bottom);
                g2d.setPaint(gp);
                g2d.fill(new RoundRectangle2D.Float(3, yOffset + 3, getWidth() - 6, getHeight() - 9, 22, 22));

                // Glossy
                GradientPaint glossy = new GradientPaint(
                        0, yOffset + 5, new Color(255, 255, 255, 100),
                        0, getHeight() / 2 + yOffset, new Color(255, 255, 255, 0));
                g2d.setPaint(glossy);
                g2d.fill(new RoundRectangle2D.Float(6, yOffset + 6, getWidth() - 12, getHeight() / 2 - 8, 20, 20));

                // Text
                g2d.setFont(new Font("SansSerif", Font.BOLD, 18));
                FontMetrics fm = g2d.getFontMetrics();
                int tx = (getWidth() - fm.stringWidth(getText())) / 2;
                int ty = (getHeight() + fm.getAscent() - fm.getDescent()) / 2 - 1 + yOffset;

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
        button.setBounds(x, y, width, 52);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void resumeGame() {
        dispose();
        if (onResume != null)
            onResume.run();
    }

    private void showHowToPlay() {
        MainMenu.HowToPlayDialog dialog = new MainMenu.HowToPlayDialog(this);
        dialog.setVisible(true);
    }

    private void goToMainMenu() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Return to main menu? Progress will be lost.",
                "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            if (onMainMenu != null)
                onMainMenu.run();
        }
    }

    private void exitGame() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Exit game?", "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
}

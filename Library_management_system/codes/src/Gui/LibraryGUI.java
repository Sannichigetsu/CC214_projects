package Gui;

import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class LibraryGUI extends JFrame {
    private BufferedImage bookshelfImage;
    private static final Color DARK_GREEN = new Color(40, 54, 44);
    private static final Color GOLD = new Color(184, 157, 102);

    public LibraryGUI() {
        setTitle("Library Inventory System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        // Load the image from resources
        try {
            // Ensure bookshelf.jpg is located in the "resources" folder of your project
            bookshelfImage = ImageIO.read(getClass().getResource("/Images/bookshelf.jpg"));
        } catch (IOException e) {
            System.err.println("Bookshelf image could not be loaded: " + e.getMessage());
            bookshelfImage = null; // Fallback
        }

        // Create main panel with custom painting
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Fill background with dark green
                g.setColor(DARK_GREEN);
                g.fillRect(0, 0, getWidth(), getHeight());

                // Draw bookshelf image on the right side (smaller portion)
                if (bookshelfImage != null) {
                    g.drawImage(bookshelfImage, (int) (getWidth() * 0.6), 0,
                            (int) (getWidth() * 0.4), getHeight(), null);
                }
            }
        };
        mainPanel.setLayout(null); // Using null layout for precise positioning

        // Add "LIBRARY INVENTORY SYSTEM" text
        JLabel systemLabel = new JLabel("LIBRARY INVENTORY SYSTEM");
        systemLabel.setForeground(GOLD);
        systemLabel.setFont(new Font("Serif", Font.PLAIN, 16));
        systemLabel.setBounds(50, 30, 300, 30);
        mainPanel.add(systemLabel);

        // Add main title
        JLabel titleLabel = new JLabel("<html>Start Your<br>BookWorm Era!</html>");
        titleLabel.setForeground(GOLD);
        titleLabel.setFont(new Font("Serif", Font.PLAIN, 48));
        titleLabel.setBounds(50, 100, 400, 150);
        mainPanel.add(titleLabel);

        // Add buttons with new styling
        JButton aboutUsButton = createStyledButton("About Us");
        aboutUsButton.setBounds(50, 400, 180, 50);
        aboutUsButton.addActionListener(e -> {
            AboutUs aboutUs = new AboutUs(this);
            aboutUs.setVisible(true);
            this.setVisible(false);
        });
        mainPanel.add(aboutUsButton);

        JButton signInButton = createStyledButton("Sign In");
        signInButton.setBounds(250, 400, 180, 50);
        signInButton.addActionListener(e -> {
            new LoginPage().setVisible(true);
            this.dispose();
        });
        mainPanel.add(signInButton);

        add(mainPanel);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isPressed()) {
                    g2d.setColor(GOLD.darker());
                } else {
                    g2d.setColor(GOLD);
                }

                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 25, 25);

                FontMetrics metrics = g2d.getFontMetrics();
                int x = (getWidth() - metrics.stringWidth(getText())) / 2;
                int y = ((getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();
                g2d.drawString(getText(), x, y);
            }
        };
        button.setForeground(GOLD);
        button.setFont(new Font("Serif", Font.PLAIN, 16));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setForeground(GOLD.brighter());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setForeground(GOLD);
            }
        });

        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new LibraryGUI().setVisible(true);
        });
    }
}

package Gui.Admin;

import DSA.Admin.DB_Connection;
import Gui.LibraryGUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class AdminDashboard extends JFrame {
    private static final Color BACKGROUND_COLOR = new Color(240, 240, 240);
    private static final Color BUTTON_COLOR = new Color(184, 207, 229);
    private static final Color DARK_GREEN = new Color(40, 54, 44);
    private static final Color GOLD = new Color(184, 157, 102);

    public AdminDashboard() {
        setTitle("Admin Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1170, 600);
        setLocationRelativeTo(null);

        // Create main panel
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Fill background
                g.setColor(BACKGROUND_COLOR);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(null);

        // Add dashboard title
        JLabel titleLabel = new JLabel("Admin Dashboard");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 24));
        titleLabel.setForeground(DARK_GREEN);
        titleLabel.setBounds(20, 20, 300, 40);
        mainPanel.add(titleLabel);

        // Create buttons panel
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridLayout(3, 1, 0, 20)); // Changed from 4 to 3 rows
        buttonsPanel.setBounds(20, 100, 250, 400);
        buttonsPanel.setOpaque(false);

        // Create and add buttons
        JButton bookInventoryBtn = createStyledButton("Book Inventory Management");
        JButton userAccountBtn = createStyledButton("User Account Management");
        JButton reportsBtn = createStyledButton("Reports and Logs");

        // Add buttons to panel
        buttonsPanel.add(bookInventoryBtn);
        buttonsPanel.add(userAccountBtn);
        buttonsPanel.add(reportsBtn);

        // Add buttons panel to main panel
        mainPanel.add(buttonsPanel);

        // Create content panel (right side)
        JPanel contentPanel = new JPanel();
        contentPanel.setBounds(290, 100, 840, 400);
        contentPanel.setBorder(BorderFactory.createLineBorder(DARK_GREEN));
        contentPanel.setBackground(Color.WHITE);
        mainPanel.add(contentPanel);

        // Add action listeners
        bookInventoryBtn.addActionListener(e -> {
            contentPanel.removeAll();  // Clear existing content
            BookInventoryDashboard bookDashboard = new BookInventoryDashboard();
            contentPanel.add(bookDashboard, BorderLayout.CENTER);
            contentPanel.revalidate();
            contentPanel.repaint();
        });

        userAccountBtn.addActionListener(e -> {
            contentPanel.removeAll();  // Clear existing content
            UserAccountDashboard userAccountDashboard = new UserAccountDashboard();
            contentPanel.add(userAccountDashboard, BorderLayout.CENTER);
            contentPanel.revalidate();
            contentPanel.repaint();
        });

        reportsBtn.addActionListener(e -> {
            contentPanel.removeAll();  // Clear existing content
            ReportsDashboard reportsDashboard = new ReportsDashboard();
            contentPanel.add(reportsDashboard, BorderLayout.CENTER);
            contentPanel.revalidate();
            contentPanel.repaint();
        });

        // Add logout button
        JButton logoutButton = createStyledButton("Logout");
        logoutButton.setBounds(1025, 20, 100, 40);
        logoutButton.addActionListener(e -> {
            dispose();
            // Add code to return to login page
            LibraryGUI libraryGUI = new LibraryGUI();
            libraryGUI.setVisible(true);
        });
        mainPanel.add(logoutButton);
        add(mainPanel);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isPressed()) {
                    g2d.setColor(BUTTON_COLOR.darker());
                } else {
                    g2d.setColor(BUTTON_COLOR);
                }

                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                g2d.setColor(DARK_GREEN);
                FontMetrics metrics = g2d.getFontMetrics();
                int x = (getWidth() - metrics.stringWidth(getText())) / 2;
                int y = ((getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();
                g2d.drawString(getText(), x, y);
            }
        };

        button.setPreferredSize(new Dimension(200, 40));
        button.setFont(new Font("Serif", Font.PLAIN, 14));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setForeground(DARK_GREEN.brighter());
            }

            public void mouseExited(MouseEvent e) {
                button.setForeground(DARK_GREEN);
            }
        });

        return button;
    }

    public static boolean testConnection() {
        try (Connection conn = DriverManager.getConnection(DB_Connection.book, DB_Connection.user, DB_Connection.pass)) {
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        testConnection();
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new AdminDashboard().setVisible(true);
        });
    }
}
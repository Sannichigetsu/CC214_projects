package Gui;

import DSA.Admin.USER_DB;
import DSA.Objects.users;
import Gui.Admin.AdminDashboard;

import javax.swing.*;
import java.awt.*;

public class LoginPage extends JFrame {
    private static final Color GOLD = new Color(0xAF, 0x8C, 0x53);
    private static final Color TEXT_COLOR = new Color(0x25, 0x29, 0x26);
    private static final Color INPUT_TEXT_COLOR = GOLD;
    private JTextField idField;
    private JPasswordField passwordField;

    public LoginPage() {
        setTitle("Sign In");
        setSize(400, 491);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Create main panel with background image
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Absolute path to your image
                ImageIcon imageIcon = new ImageIcon("C:\\Users\\Octob\\Documents\\GitHub\\CC214_projects\\Library_management_system\\codes\\src\\Images\\login.jpg");
                Image image = imageIcon.getImage();
                g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
            }
        };
        mainPanel.setLayout(null);

        // Sign In Label
        JLabel titleLabel = new JLabel("SIGN IN");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 24));
        titleLabel.setForeground(GOLD);
        titleLabel.setBounds(150, 50, 150, 30);
        mainPanel.add(titleLabel);

        // ID Field
        idField = createTransparentTextField();
        idField.setBounds(75, 150, 250, 35);
        mainPanel.add(idField);

        // ID placeholder
        JLabel idPlaceholder = new JLabel("Enter User ID");
        idPlaceholder.setFont(new Font("Serif", Font.PLAIN, 14));
        idPlaceholder.setForeground(GOLD);
        idPlaceholder.setBounds(75, 130, 250, 20);
        mainPanel.add(idPlaceholder);

        // Password Field
        passwordField = createTransparentPasswordField();
        passwordField.setBounds(75, 230, 250, 35);
        mainPanel.add(passwordField);

        // Password placeholder
        JLabel passwordPlaceholder = new JLabel("Enter Password");
        passwordPlaceholder.setFont(new Font("Serif", Font.PLAIN, 14));
        passwordPlaceholder.setForeground(GOLD);
        passwordPlaceholder.setBounds(75, 210, 250, 20);
        mainPanel.add(passwordPlaceholder);

        // Login Button
            JButton loginButton = createStyledButton("LOG IN", true);
            loginButton.setBounds(75, 300, 250, 40);
            loginButton.addActionListener(e -> handleLogin());
            mainPanel.add(loginButton);

        // Create Account Button
        JButton createAccountButton = createStyledButton("No Account? Click to Create", false);
        createAccountButton.setBounds(75, 360, 250, 40);
        createAccountButton.addActionListener(e -> openCreateAccountPage());
        mainPanel.add(createAccountButton);

        add(mainPanel);
    }

    private void handleLogin() {
        String idText = idField.getText().trim();
        String password = new String(passwordField.getPassword());

        // Validate input fields
        if (idText.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter both ID and password",
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (admin(idText, password)) {
            JOptionPane.showMessageDialog(this,
                    "Admin login successful!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            new AdminDashboard().setVisible(true);
            this.dispose();
            return;
        }

        // Parse and validate ID
        try {
            int id = Integer.parseInt(idText);
            if (USER_DB.validate(id, password)) {
                // Create a new USER_DB instance to use its methods
                USER_DB userDb = new USER_DB();
                // Get the user object
                users loggedInUser = userDb.getUser(id);

                if (loggedInUser != null) {
                    JOptionPane.showMessageDialog(this,
                            "Login successful!",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    UserDashboard userDashboard = new UserDashboard(loggedInUser);
                    userDashboard.setVisible(true);
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Error retrieving user data",
                            "Login Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "Invalid credentials",
                        "Login Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid numeric ID for user login",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    private void openCreateAccountPage() {
        CreateAccountPage createAccountPage = new CreateAccountPage();
        createAccountPage.setVisible(true);
    }
    // Rest of the existing methods remain the same...

    // Existing helper methods remain the same...
    private JTextField createTransparentTextField() {
        JTextField field = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(new Color(255, 255, 255, 0));
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        field.setOpaque(false);
        field.setForeground(INPUT_TEXT_COLOR);
        field.setCaretColor(INPUT_TEXT_COLOR);
        field.setFont(new Font("Serif", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GOLD),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        return field;
    }

    private JPasswordField createTransparentPasswordField() {
        JPasswordField field = new JPasswordField() {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(new Color(255, 255, 255, 0));
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        field.setOpaque(false);
        field.setForeground(INPUT_TEXT_COLOR);
        field.setCaretColor(INPUT_TEXT_COLOR);
        field.setFont(new Font("Serif", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GOLD),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        return field;
    }

    private JButton createStyledButton(String text, boolean filled) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                if (filled) {
                    if (getModel().isPressed()) {
                        g2d.setColor(GOLD.darker());
                    } else {
                        g2d.setColor(GOLD);
                    }
                    g2d.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 25, 25);
                    g2d.setColor(TEXT_COLOR);
                } else {
                    if (getModel().isPressed()) {
                        g2d.setColor(GOLD.darker());
                    } else {
                        g2d.setColor(GOLD);
                    }
                    g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 25, 25);
                }

                FontMetrics metrics = g2d.getFontMetrics();
                int x = (getWidth() - metrics.stringWidth(getText())) / 2;
                int y = ((getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();
                g2d.drawString(getText(), x, y);
            }
        };

        button.setForeground(filled ? TEXT_COLOR : GOLD);
        button.setFont(new Font("Serif", Font.PLAIN, 16));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setForeground(filled ? TEXT_COLOR.brighter() : GOLD.brighter());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setForeground(filled ? TEXT_COLOR : GOLD);
            }
        });

        return button;
    }
    public boolean admin(String Id, String pass){
        if (pass.matches("145")&& Id.matches("admin")) return true;
        if (!Id.matches("admin")) return false;
        if (!pass.matches("admin")) return false;
        return (Id.equals("admin") && pass.equals("admin")) ||
                (Id.equals("admin") && pass.equals("145"));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginPage().setVisible(true));
    }
}

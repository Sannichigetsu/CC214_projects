package Gui;

import DSA.Admin.USER_DB;
import DSA.Objects.users;

import javax.swing.*;
import java.awt.*;

public class CreateAccountPage extends JFrame {
    private static final Color GOLD = new Color(0xAF, 0x8C, 0x53);
    private static final Color TEXT_COLOR = new Color(0x25, 0x29, 0x26);
    private static final Color INPUT_TEXT_COLOR = GOLD;
    private JTextField idField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField emailField;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JComboBox<String> genderComboBox;

    public CreateAccountPage() {
        setTitle("Create Account");
        setSize(400, 700);  // Reduced height after removing username field
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main panel setup
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon imageIcon = new ImageIcon("Library_management_system/codes/src/Images/login.jpg");
                Image image = imageIcon.getImage();
                g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
            }
        };
        mainPanel.setLayout(null);

        // Title Label
        JLabel titleLabel = new JLabel("CREATE ACCOUNT");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 24));
        titleLabel.setForeground(GOLD);
        titleLabel.setBounds(110, 30, 250, 30);
        mainPanel.add(titleLabel);

        // ID Field
        idField = createTransparentTextField();
        idField.setBounds(75, 100, 250, 35);
        mainPanel.add(idField);

        JLabel idPlaceholder = new JLabel("Enter ID Number");
        idPlaceholder.setFont(new Font("Serif", Font.PLAIN, 14));
        idPlaceholder.setForeground(GOLD);
        idPlaceholder.setBounds(75, 80, 250, 20);
        mainPanel.add(idPlaceholder);

        // First Name Field
        firstNameField = createTransparentTextField();
        firstNameField.setBounds(75, 170, 250, 35);
        mainPanel.add(firstNameField);

        JLabel firstNamePlaceholder = new JLabel("Enter First Name");
        firstNamePlaceholder.setFont(new Font("Serif", Font.PLAIN, 14));
        firstNamePlaceholder.setForeground(GOLD);
        firstNamePlaceholder.setBounds(75, 150, 250, 20);
        mainPanel.add(firstNamePlaceholder);

        // Last Name Field
        lastNameField = createTransparentTextField();
        lastNameField.setBounds(75, 240, 250, 35);
        mainPanel.add(lastNameField);

        JLabel lastNamePlaceholder = new JLabel("Enter Last Name");
        lastNamePlaceholder.setFont(new Font("Serif", Font.PLAIN, 14));
        lastNamePlaceholder.setForeground(GOLD);
        lastNamePlaceholder.setBounds(75, 220, 250, 20);
        mainPanel.add(lastNamePlaceholder);

        // Email Field
        emailField = createTransparentTextField();
        emailField.setBounds(75, 310, 250, 35);
        mainPanel.add(emailField);

        JLabel emailPlaceholder = new JLabel("Enter Email");
        emailPlaceholder.setFont(new Font("Serif", Font.PLAIN, 14));
        emailPlaceholder.setForeground(GOLD);
        emailPlaceholder.setBounds(75, 290, 250, 20);
        mainPanel.add(emailPlaceholder);

        // Gender ComboBox
        String[] genders = {"Male", "Female", "Other"};
        genderComboBox = new JComboBox<>(genders);
        genderComboBox.setBounds(75, 380, 250, 35);
        genderComboBox.setForeground(GOLD);
        genderComboBox.setBackground(new Color(0, 0, 0, 0));
        mainPanel.add(genderComboBox);

        JLabel genderPlaceholder = new JLabel("Select Gender");
        genderPlaceholder.setFont(new Font("Serif", Font.PLAIN, 14));
        genderPlaceholder.setForeground(GOLD);
        genderPlaceholder.setBounds(75, 360, 250, 20);
        mainPanel.add(genderPlaceholder);

        // Password Field
        passwordField = createTransparentPasswordField();
        passwordField.setBounds(75, 450, 250, 35);
        mainPanel.add(passwordField);

        JLabel passwordPlaceholder = new JLabel("Enter Password");
        passwordPlaceholder.setFont(new Font("Serif", Font.PLAIN, 14));
        passwordPlaceholder.setForeground(GOLD);
        passwordPlaceholder.setBounds(75, 430, 250, 20);
        mainPanel.add(passwordPlaceholder);

        // Confirm Password Field
        confirmPasswordField = createTransparentPasswordField();
        confirmPasswordField.setBounds(75, 520, 250, 35);
        mainPanel.add(confirmPasswordField);

        JLabel confirmPasswordPlaceholder = new JLabel("Confirm Password");
        confirmPasswordPlaceholder.setFont(new Font("Serif", Font.PLAIN, 14));
        confirmPasswordPlaceholder.setForeground(GOLD);
        confirmPasswordPlaceholder.setBounds(75, 500, 250, 20);
        mainPanel.add(confirmPasswordPlaceholder);

        // Create Account Button
        JButton createAccountButton = createStyledButton("CREATE ACCOUNT", true);
        createAccountButton.setBounds(75, 580, 250, 40);
        createAccountButton.addActionListener(e -> handleCreateAccount());
        mainPanel.add(createAccountButton);

        // Back to Login Button
        JButton backButton = createStyledButton("Back to Login", false);
        backButton.setBounds(75, 630, 250, 40);
        backButton.addActionListener(e -> {
            LoginPage loginPage = new LoginPage(); // Instantiate the LoginPage class
            loginPage.setVisible(true); // Make the LoginPage visible
            ((JFrame) SwingUtilities.getWindowAncestor(backButton)).dispose(); // Dispose the current window
        });

        mainPanel.add(backButton);

        add(mainPanel);
    }

    private void handleCreateAccount() {
        // Wrap ID parsing in try-catch to handle invalid input
        try {
            String idText = idField.getText().trim();
            int id;
            try {
                id = Integer.parseInt(idText);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                        "Please enter a valid numeric ID",
                        "Invalid ID",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());
            String gender = (String) genderComboBox.getSelectedItem();

            if (!validateUserInput(email, password, confirmPassword, id)) {
                JOptionPane.showMessageDialog(this,
                        "Please fill all fields correctly and ensure passwords match",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Create new user object
            users newUser = new users();
            newUser.setId(id);
            newUser.setFirstName(firstName);
            newUser.setLastName(lastName);
            newUser.setPass(password);
            newUser.setEmail(email);
            newUser.setGender(gender);
            newUser.setLimit(3); // Default borrowing limit

            // Try to add user to database
            if (USER_DB.add(newUser)) {
                JOptionPane.showMessageDialog(this,
                        "Account created successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to create account. User ID may already exist.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
          e.printStackTrace();
        }
    }



    // Helper methods remain the same...
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

    // (createTransparentTextField, createTransparentPasswordField, createStyledButton)
    public boolean validateUserInput(String email, String password, String confirmpass, int id) {
        // Check for empty fields
        if (email.isEmpty() || password.isEmpty() || confirmpass.isEmpty() ||
                firstNameField.getText().trim().isEmpty() || lastNameField.getText().trim().isEmpty()) {
            return false;
        }

        // Password match check
        if (!password.equals(confirmpass)) {
            return false;
        }

        // Email format check
        if (!email.contains("@") || !email.contains(".")) {
            return false;
        }

        // ID validation - assuming valid IDs are positive numbers less than 100000
        if (id <= 0 || id >= 100000) {
            return false;
        }

        return true;
    }
}
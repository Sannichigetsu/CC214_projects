package Gui.Admin;
import DSA.Admin.USER_DB;
import DSA.Objects.users;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.Hashtable;

public class UserAccountDashboard extends JPanel {
    private final JTable table;
    private final DefaultTableModel tableModel;
    private final JPanel buttonPanel;
    private final JPanel topPanel;
    private final JTextField searchField;
    private final USER_DB userDB;
    private static final Color DARK_GREEN = new Color(40, 54, 44);
    private static final Color BUTTON_COLOR = new Color(184, 207, 229);

    public UserAccountDashboard() {
        userDB = new USER_DB();
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create top panel for search and buttons
        topPanel = new JPanel(new BorderLayout(10, 0));
        topPanel.setBackground(Color.WHITE);

        // Create search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(Color.WHITE);

        JLabel searchLabel = new JLabel("Search:");
        searchField = new JTextField(20);
        JButton searchButton = createStyledButton("Search");
        JButton reloadButton = createStyledButton("↻ Reload");

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(reloadButton);

        // Create button panel for CRUD operations
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        JButton addButton = createStyledButton("Add User");
        JButton removeButton = createStyledButton("Remove User");
        JButton updateButton = createStyledButton("Update User");

        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(updateButton);

        // Add both panels to top panel
        topPanel.add(searchPanel, BorderLayout.WEST);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        // Create table model with columns
        String[] columns = {"ID", "First Name", "Last Name", "Email", "Gender", "Borrowing Limit"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Create and configure table
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(25);
        table.getTableHeader().setBackground(DARK_GREEN);
        table.getTableHeader().setForeground(Color.BLACK);
        table.setGridColor(Color.BLACK);

        // Configure table appearance
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(DARK_GREEN));
        table.setFillsViewportHeight(true);

        // Add components to main panel
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Add button listeners
        addButton.addActionListener(e -> addUser());
        removeButton.addActionListener(e -> removeUser());
        updateButton.addActionListener(e -> updateUser());
        reloadButton.addActionListener(e -> refreshTableData());

        // Add search functionality
        searchButton.addActionListener(e -> performSearch());
        searchField.addActionListener(e -> performSearch());

        // Load initial data
        refreshTableData();
    }

    private void performSearch() {
        String searchTerm = searchField.getText().trim().toLowerCase();
        if (searchTerm.isEmpty()) {
            refreshTableData();
            return;
        }
        tableModel.setRowCount(0);
        Hashtable<Integer, users> userList = userDB.loadAllUsers();
        for (users user : userList.values()) {
            if (matchesSearch(user, searchTerm)) {
                addUserToTable(user);
            }
        }
    }

    private boolean matchesSearch(users user, String searchTerm) {
        return user.getFirstName().toLowerCase().contains(searchTerm) ||
                user.getLastName().toLowerCase().contains(searchTerm) ||
                user.getEmail().toLowerCase().contains(searchTerm) ||
                String.valueOf(user.getId()).contains(searchTerm);
    }

    private void refreshTableData() {
        tableModel.setRowCount(0);
        Hashtable<Integer, users> userList = userDB.loadAllUsers();

        for (users user : userList.values()) {
            addUserToTable(user);
        }
    }

    private void addUserToTable(users user) {
        Object[] row = {
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getGender(),
                user.getLimit()
        };
        tableModel.addRow(row);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(BUTTON_COLOR);
        button.setForeground(DARK_GREEN);
        button.setFocusPainted(false);
        button.setFont(new Font("Serif", Font.PLAIN, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DARK_GREEN),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return button;
    }

    private void addUser() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New User", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(400, 450);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(7, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create form fields
        JTextField idField = new JTextField();
        JTextField firstNameField = new JTextField();
        JTextField lastNameField = new JTextField();
        JTextField emailField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JComboBox<String> genderCombo = new JComboBox<>(new String[]{"Male", "Female"});

        // Add form components
        formPanel.add(new JLabel("ID:"));
        formPanel.add(idField);
        formPanel.add(new JLabel("Last Name:"));
        formPanel.add(lastNameField);
        formPanel.add(new JLabel("First Name:"));
        formPanel.add(firstNameField);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Password:"));
        formPanel.add(passwordField);
        formPanel.add(new JLabel("Sex:"));
        formPanel.add(genderCombo);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = createStyledButton("Save");
        JButton cancelButton = createStyledButton("Cancel");

        saveButton.addActionListener(e -> {
            try {
                int id = Integer.parseInt(idField.getText());
                String lastName = lastNameField.getText();
                String firstName = firstNameField.getText();
                String email = emailField.getText();
                String password = new String(passwordField.getPassword());
                String gender = (String) genderCombo.getSelectedItem();

                users newUser = new users(id, lastName, firstName, password, email, gender, 3);

                if (userDB.add(newUser)) {
                    JOptionPane.showMessageDialog(dialog, "User added successfully!");
                    dialog.dispose();
                    refreshTableData();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to add user. Please try again.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid ID number.",
                        "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void updateUser() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to update.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int userId = (int) table.getValueAt(selectedRow, 0);
        users currentUser = userDB.getUser(userId);
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "Error loading user data.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Update User", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(400, 450);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(7, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create form fields with current values
        JTextField idField = new JTextField(String.valueOf(currentUser.getId()));
        idField.setEditable(false); // ID should not be editable
        JTextField firstNameField = new JTextField(currentUser.getFirstName());
        JTextField lastNameField = new JTextField(currentUser.getLastName());
        JTextField emailField = new JTextField(currentUser.getEmail());
        JPasswordField passwordField = new JPasswordField(currentUser.getPass());
        JComboBox<String> genderCombo = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        genderCombo.setSelectedItem(currentUser.getGender());



        // Add form components
        formPanel.add(new JLabel("ID:"));
        formPanel.add(idField);
        formPanel.add(new JLabel("First Name:"));
        formPanel.add(firstNameField);
        formPanel.add(new JLabel("Last Name:"));
        formPanel.add(lastNameField);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Password:"));
        formPanel.add(passwordField);
        formPanel.add(new JLabel("Gender:"));
        formPanel.add(genderCombo);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = createStyledButton("Save");
        JButton cancelButton = createStyledButton("Cancel");

        saveButton.addActionListener(e -> {
            try {
                // Form validation
                String firstName = firstNameField.getText().trim();
                String lastName = lastNameField.getText().trim();
                String email = emailField.getText().trim();
                String password = new String(passwordField.getPassword());
                String gender = (String) genderCombo.getSelectedItem();

                // Basic validation
                if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog,
                            "All fields must be filled out.",
                            "Validation Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                users updatedUser = new users(
                        userId,
                        lastName,
                        firstName,
                        password,
                        email,
                        gender,
                        3
                );

//                if (userDB.updateUser(updatedUser)) {
//                    JOptionPane.showMessageDialog(dialog,
//                            "User updated successfully!",
//                            "Success",
//                            JOptionPane.INFORMATION_MESSAGE);
//                    dialog.dispose();
//                    refreshTableData();
//                } else {
//                    JOptionPane.showMessageDialog(dialog,
//                            "Failed to update user. Please try again.",
//                            "Update Error",
//                            JOptionPane.ERROR_MESSAGE);
//                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Error updating user: " + ex.getMessage(),
                        "Update Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });


        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void removeUser() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a user to remove.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) table.getValueAt(selectedRow, 0);
        String lastName = (String) table.getValueAt(selectedRow, 1);
        String firstName = (String) table.getValueAt(selectedRow, 2);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to remove the user:\n" +
                        "Name: " + firstName + " " + lastName + "\nID: " + id,
                "Confirm Removal",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (userDB.deleteUser(id)) {
                refreshTableData();
                JOptionPane.showMessageDialog(this,
                        "User removed successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to remove the user. They may have associated records.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
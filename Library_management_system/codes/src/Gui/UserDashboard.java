package Gui;

import DSA.Admin.AdminControls;
import DSA.Admin.Borrowed_requests;
import DSA.Admin.BorrowingHistory;
import DSA.Admin.MySQLbookDb;
import DSA.Objects.Books;
import DSA.Objects.users;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class UserDashboard extends JFrame {
    private JTextField searchField;
    private JComboBox<String> sortByComboBox;
    private JPanel mainPanel;
    private JTable bookTable;
    private DefaultTableModel tableModel;
    private final users currentUser;

    public UserDashboard(users currentUser) {
        this.currentUser = currentUser;
        setTitle("Library Management System - Welcome " +
                (currentUser != null ? currentUser.getFirstName() + " " + currentUser.getLastName() : "Guest"));
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create main panel with BorderLayout
        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create left panel for buttons
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        leftPanel.setPreferredSize(new Dimension(150, 0));

        // Profile section
        JPanel profilePanel = new JPanel();
        profilePanel.setLayout(new BoxLayout(profilePanel, BoxLayout.Y_AXIS));
        JLabel profilePicture = new JLabel(new ImageIcon()); // Placeholder for profile picture
        profilePicture.setPreferredSize(new Dimension(100, 100));
        profilePicture.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        JTextField nameField = new JTextField("Name");
        nameField.setMaximumSize(new Dimension(150, 25));
        if (currentUser != null) {
            nameField.setText(currentUser.getFirstName() + " " + currentUser.getLastName());
            nameField.setEditable(false);
        }

        profilePanel.add(profilePicture);
        profilePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        profilePanel.add(nameField);
        profilePanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Create buttons
        JButton borrowButton = new JButton("Borrow");
        JButton returnButton = new JButton("Return");
        JButton profileButton = new JButton("Profile");
        JButton historyButton = new JButton("History");
        JButton logoutButton = new JButton("Logout");

        // Add buttons to left panel
        leftPanel.add(profilePanel);
        leftPanel.add(borrowButton);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        leftPanel.add(returnButton);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        leftPanel.add(profileButton);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        leftPanel.add(historyButton);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        leftPanel.add(logoutButton);

        // Create top panel for search and sort
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        String[] sortOptions = {"Genre", "Alphabetical Order", "Date", "ISBN"};
        sortByComboBox = new JComboBox<>(sortOptions);
        sortByComboBox.setPreferredSize(new Dimension(150, 25));

        topPanel.add(searchField);
        topPanel.add(searchButton);
        topPanel.add(new JLabel("Sort by:"));
        topPanel.add(sortByComboBox);

        // Create center panel for book table
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createTitledBorder("Available Books"));

        // Create table with column names
        String[] columnNames = {"ISBN", "Title", "Author", "Genre", "Available Copies", "Total Copies"};
        tableModel = new DefaultTableModel(columnNames, 0);
        bookTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(bookTable);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        // Add panels to main panel
        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Add main panel to frame
        add(mainPanel);


        // Add action listeners
        searchButton.addActionListener(e -> searchBooks());
        borrowButton.addActionListener(e -> {
            if (currentUser != null) {
                borrowBook(null, currentUser);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Please log in to borrow books.",
                        "Login Required",
                        JOptionPane.WARNING_MESSAGE);
            }
        }); // Example usage of borrowBook
        returnButton.addActionListener(e -> returnBook());
        profileButton.addActionListener(e -> showProfile());
        historyButton.addActionListener(e -> showHistory());
        logoutButton.addActionListener(e -> logout());
        sortByComboBox.addActionListener(e -> sortBooks());
        loadBooks();
    }

    private void loadBooks() {
        try {
            // Clear previous data from table
            tableModel.setRowCount(0);

            // Get AdminControls instance and fetch books
            List<Books> books = MySQLbookDb.LoadBooks();
            if (books != null) {
                for (Books book : books) {
                    if (book.isAvailable()) {  // Only show available books
                        tableModel.addRow(new Object[]{
                                book.getISBN(),
                                book.getTitle(),
                                book.getAuthor(),
                                book.getGenre(),
                                book.getAvailableCopy(),
                                book.getTotalCopy()
                        });
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading books: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error loading books. Please try again later.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    private void sortBooks() {
        String selectedSort = (String) sortByComboBox.getSelectedItem();
        List<Books> books = MySQLbookDb.LoadBooks();

        switch (selectedSort) {
            case "Genre":
                books.sort((b1, b2) -> b1.getGenre().compareTo(b2.getGenre()));
                break;
            case "Alphabetical Order":
                books.sort((b1, b2) -> b1.getTitle().compareTo(b2.getTitle()));
                break;
            case "Date":
                books.sort((b1, b2) -> b1.getDatePublished().compareTo(b2.getDatePublished()));
                break;
            case "ISBN":
                books.sort((b1, b2) -> Integer.compare(b1.getISBN(), b2.getISBN()));
                break;
        }

        // Update table after sorting
        tableModel.setRowCount(0); // Clear the existing rows
        for (Books book : books) {
            if (book.isAvailable()) {
                tableModel.addRow(new Object[]{
                        book.getISBN(),
                        book.getTitle(),
                        book.getAuthor(),
                        book.getGenre(),
                        book.getAvailableCopy(),
                        book.getTotalCopy()
                });
            }
        }
    }

    private void searchBooks() {
        String searchTerm = searchField.getText().toLowerCase();
        List<Books> allBooks = MySQLbookDb.LoadBooks();

        tableModel.setRowCount(0); // Clear previous search results
        for (Books book : allBooks) {
            if (book.getTitle().toLowerCase().contains(searchTerm) ||
                    book.getAuthor().toLowerCase().contains(searchTerm) ||
                    book.getGenre().toLowerCase().contains(searchTerm) ||
                    String.valueOf(book.getISBN()).contains(searchTerm)) {

                tableModel.addRow(new Object[]{
                        book.getISBN(),
                        book.getTitle(),
                        book.getAuthor(),
                        book.getGenre(),
                        book.getAvailableCopy(),
                        book.getTotalCopy()
                });
            }
        }
    }

    // In UserDashboard.java, update the borrowBook method:
    private void borrowBook(Books book, users user) {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a book to borrow.");
            return;
        }

        int isbn = (int) tableModel.getValueAt(selectedRow, 0);
        String title = (String) tableModel.getValueAt(selectedRow, 1);
        String author = (String) tableModel.getValueAt(selectedRow, 2);
        int availableCopies = (int) tableModel.getValueAt(selectedRow, 4);

        // Verify copies are available
        if (availableCopies <= 0) {
            JOptionPane.showMessageDialog(this,
                    "Sorry, this book is currently unavailable.",
                    "No Copies Available",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            boolean success = AdminControls.borrowBook(
                    user.getId(),
                    title,
                    user.getLastName(),
                    author,
                    1  // Borrowing one copy at a time
            );

            if (success) {
                // Immediately refresh the book list to show updated counts
                loadBooks();

                JOptionPane.showMessageDialog(this,
                        "Book borrowed successfully!\nPlease return within 14 days to avoid late fees.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to borrow book. Please try again.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "An error occurred: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    private void returnBook() {
        // First, load user's borrowed books
        List<Borrowed_requests.BorrowRequest> borrowedBooks = BorrowingHistory.LoadHistoryByUser(currentUser.getLastName())
                .stream()
                .filter(h -> h.getStatus().equals("BORROWED"))
                .toList();

        if (borrowedBooks.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "You don't have any books to return.",
                    "No Books to Return",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Create a list of book titles for the dropdown
        String[] bookTitles = borrowedBooks.stream()
                .map(Borrowed_requests.BorrowRequest::getTitle)
                .toArray(String[]::new);

        // Show book selection dialog
        String selectedTitle = (String) JOptionPane.showInputDialog(
                this,
                "Select a book to return:",
                "Return Book",
                JOptionPane.QUESTION_MESSAGE,
                null,
                bookTitles,
                bookTitles[0]
        );

        if (selectedTitle != null) {
            try {
                // Find the full book details from the borrowed list
                Borrowed_requests.BorrowRequest borrowedBook = borrowedBooks.stream()
                        .filter(b -> b.getTitle().equals(selectedTitle))
                        .findFirst()
                        .orElse(null);

                if (borrowedBook != null) {
                    boolean success = AdminControls.returnBook(
                            currentUser.getId(),
                            selectedTitle,
                            currentUser.getLastName(),
                            borrowedBook.getAuthor()
                    );

                    if (success) {
                        // Refresh the book list
                        loadBooks();

                        JOptionPane.showMessageDialog(this,
                                "Book returned successfully!",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "Failed to return book. Please try again.",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "An error occurred: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    private void showProfile() {
        JOptionPane.showMessageDialog(this, "Profile view to be implemented");
    }

    private void showHistory() {
        // Create a new JFrame to show the history
        JFrame historyFrame = new JFrame("Borrowing History");
        historyFrame.setSize(800, 400);
        historyFrame.setLocationRelativeTo(null);
        historyFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);  // Ensure the window can be closed properly

        // Set layout to BorderLayout to ensure components are added correctly
        historyFrame.setLayout(new BorderLayout());

        // Create a table model with relevant columns for history
        DefaultTableModel historyTableModel = new DefaultTableModel();
        historyTableModel.setColumnIdentifiers(new String[]{"ID", "User Name", "Book Name", "Author", "Copies", "Status"});

        // Create JTable and JScrollPane
        JTable historyTable = new JTable(historyTableModel);
        JScrollPane scrollPane = new JScrollPane(historyTable);

        // Fetch user's borrowing history from BorrowingHistory or equivalent
        try {
            // Retrieve borrowing history of the logged-in user
            List<Borrowed_requests.BorrowRequest> userHistory = BorrowingHistory.LoadHistoryByUser(currentUser.getLastName());
            System.out.println("User history size: " + userHistory.size());

            // Iterate over each borrowed request
            for (Borrowed_requests.BorrowRequest request : userHistory) {
                Books book = request.getBook();  // Assuming the BorrowRequest has a getBook() method
                if (book != null && request != null) {
                    // Add a row to the history table for each borrow entry
                    historyTableModel.addRow(new Object[]{
                            request.getId(),                // Book ID or Borrow Request ID
                            request.getUser(),          // User Name
                            book.getTitle(),                // Book Title
                            book.getAuthor(),               // Book Author
                            request.getCopies(),            // Number of copies borrowed
                            request.getStatus()             // Status can be "BORROWED", "RETURNED", etc.
                    });
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading borrowing history: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        // Add the table to the frame
        historyFrame.add(scrollPane, BorderLayout.CENTER);

        // Make sure the history frame is visible in the EDT
        SwingUtilities.invokeLater(() -> {
            historyFrame.setVisible(true);
        });
    }





    private void logout() {
        int response = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION);
        if (response == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        users loggedInUser = null;
        SwingUtilities.invokeLater(() -> {
            UserDashboard userDashboard = new UserDashboard(loggedInUser);
            userDashboard.setVisible(true);
        });
    }
}

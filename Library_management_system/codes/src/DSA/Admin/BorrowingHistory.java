package DSA.Admin;

import DSA.Objects.Books;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BorrowingHistory {

    // Method to add borrowed history record
    public static boolean BorrowedHistory(int ID, String userName, String booktitle, String Author, int copies, String Status) {
        // Check if this borrow record already exists
        if (checkBorrowExists(ID, booktitle)) {
            System.out.println("This book has already been borrowed by the user.");
            return false;  // Skip or return false to indicate the record was not inserted
        }

        try (Connection connection = DriverManager.getConnection(DB_Connection.BorrowedHistory, DB_Connection.user, DB_Connection.pass);
             PreparedStatement register = connection.prepareStatement("INSERT INTO " + DB_Connection.HistoryTable +
                     "(Id, UserName, BookName, Author, Copies, Status) VALUES(?, ?, ?, ?, ?, ?)")) {

            register.setInt(1, ID);
            register.setString(2, userName);
            register.setString(3, booktitle);
            register.setString(4, Author);
            register.setInt(5, copies);
            register.setString(6, Status);

            register.executeUpdate();
            return true;  // Successfully added record

        } catch (SQLException e) {
            System.err.println("Error inserting borrowed history: " + e.getMessage());
            throw new RuntimeException(e);  // Rethrow exception if an error occurs
        }
    }


    // Load all borrowing and return history
    public static List<Borrowed_requests.BorrowRequest> LoadAllHistory() {
        List<Borrowed_requests.BorrowRequest> historyList = new ArrayList<>();
        String query = "SELECT * FROM " + DB_Connection.HistoryTable;

        try (Connection connection = DriverManager.getConnection(DB_Connection.BorrowedHistory, DB_Connection.user, DB_Connection.pass);
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                // Fetch the Book object using ISBN or ID
                int isbn = resultSet.getInt("Id");  // Assuming 'Id' is ISBN, or adjust accordingly.
                String bookTitle = resultSet.getString("BookName");
                String author = resultSet.getString("Author");
                int copies = resultSet.getInt("Copies");
                String status = resultSet.getString("Status");
                String userName = resultSet.getString("UserName");

                // Fetch the Books object
                Books book = fetchBookByISBN(isbn);

                // Create BorrowRequest if book is found
                if (book != null) {
                    Borrowed_requests.BorrowRequest borrowedRequest = new Borrowed_requests.BorrowRequest(
                            book, userName, copies, status, LocalDateTime.now()  // Use current date or add borrowReqDate if available
                    );
                    historyList.add(borrowedRequest);
                }
            }
            return historyList;

        } catch (SQLException e) {
            System.err.println("Error loading borrow history: " + e.getMessage());
            throw new RuntimeException("Failed to load borrow history", e);
        }
    }

    // Load borrowing history by user
    public static List<Borrowed_requests.BorrowRequest> LoadHistoryByUser(String userName) {
        List<Borrowed_requests.BorrowRequest> userHistory = new ArrayList<>();
        String query = "SELECT * FROM " + DB_Connection.HistoryTable + " WHERE UserName = ?";

        try (Connection connection = DriverManager.getConnection(DB_Connection.BorrowedHistory, DB_Connection.user, DB_Connection.pass);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, userName);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int isbn = resultSet.getInt("Id");
                    String bookTitle = resultSet.getString("BookName");
                    String author = resultSet.getString("Author");
                    int copies = resultSet.getInt("Copies");
                    String status = resultSet.getString("Status");

                    Books book = fetchBookByISBN(isbn);

                    if (book != null) {
                        Borrowed_requests.BorrowRequest borrowedRequest = new Borrowed_requests.BorrowRequest(
                                book, userName, copies, status, LocalDateTime.now()
                        );
                        userHistory.add(borrowedRequest);
                    }
                }
            }
            return userHistory;

        } catch (SQLException e) {
            System.err.println("Error loading user borrow history: " + e.getMessage());
            throw new RuntimeException("Failed to load user borrow history", e);
        }
    }

    // Load borrowing history by status (Borrowed or Returned)
    public static List<Borrowed_requests.BorrowRequest> LoadHistoryByStatus(String status) {
        List<Borrowed_requests.BorrowRequest> sortedHistory = new ArrayList<>();
        String query = "SELECT * FROM " + DB_Connection.HistoryTable + " WHERE Status = ?";

        try (Connection connection = DriverManager.getConnection(DB_Connection.BorrowedHistory, DB_Connection.user, DB_Connection.pass);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, status);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int isbn = resultSet.getInt("Id");
                    String bookTitle = resultSet.getString("BookName");
                    String author = resultSet.getString("Author");
                    int copies = resultSet.getInt("Copies");

                    Books book = fetchBookByISBN(isbn);

                    if (book != null) {
                        Borrowed_requests.BorrowRequest borrowedRequest = new Borrowed_requests.BorrowRequest(
                                book, resultSet.getString("UserName"), copies, status, LocalDateTime.now()
                        );
                        sortedHistory.add(borrowedRequest);
                    }
                }
            }
            return sortedHistory;

        } catch (SQLException e) {
            System.err.println("Error loading sorted borrow history: " + e.getMessage());
            throw new RuntimeException("Failed to load sorted borrow history", e);
        }
    }

    // Utility method to check if a borrow record exists
    public static boolean checkBorrowExists(int ID, String bookName) {
        String query = "SELECT COUNT(*) FROM " + DB_Connection.HistoryTable + " WHERE Id = ? AND BookName = ?";

        try (Connection connection = DriverManager.getConnection(DB_Connection.BorrowedHistory, DB_Connection.user, DB_Connection.pass);
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setInt(1, ID);
            stmt.setString(2, bookName);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
            return false;

        } catch (SQLException e) {
            System.err.println("Error checking borrow record: " + e.getMessage());
            throw new RuntimeException("Failed to check borrow record", e);
        }
    }

    // Fetch Book object by ISBN (or ID)
    public static Books fetchBookByISBN(int isbn) {
        String query = "SELECT * FROM " + DB_Connection.BookTable + " WHERE ISBN = ?";

        try (Connection connection = DriverManager.getConnection(DB_Connection.BorrowedHistory, DB_Connection.user, DB_Connection.pass);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, isbn);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Date publishedDate = resultSet.getDate("DatePublished");
                    // Check if DatePublished is null
                    if (publishedDate == null) {
                        publishedDate = Date.valueOf("1900-01-01"); // Or some default value
                    }

                    return new Books(
                            resultSet.getInt("ISBN"),
                            resultSet.getString("Title"),
                            resultSet.getString("Genre"),
                            resultSet.getString("Author"),
                            publishedDate,
                            resultSet.getInt("AvailableCopy"),
                            resultSet.getInt("TotalCopy")
                    );
                }
            }

        } catch (SQLException e) {
            System.err.println("Error fetching book: " + e.getMessage());
            throw new RuntimeException("Failed to fetch book", e);
        }
        return null;  // Return null if no book found
    }
}

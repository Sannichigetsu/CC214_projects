    package DSA.Admin;

    import DSA.Objects.Books;

    import java.sql.*;
    import java.time.LocalDateTime;
    import java.util.ArrayList;
    import java.util.List;

    public class MySQLbookDb {
        public static boolean AddBooks(int Id, String title, String genre, String author, LocalDateTime Datepub, int copies, int totalCopies) {
            try (Connection connection = DriverManager.getConnection(DB_Connection.book, DB_Connection.user, DB_Connection.pass);
                 PreparedStatement register = connection.prepareStatement("INSERT INTO " + DB_Connection.BookTable +
                         "(Id, Title, Genre, Author, Publish_date, Copies, Total_copies) VALUES(?, ?, ?, ?, ?, ?, ?)")) {

                register.setInt(1, Id);
                register.setString(2, title);
                register.setString(3, genre);
                register.setString(4, author);
                // Convert LocalDateTime to SQL Timestamp
                register.setTimestamp(5, Timestamp.valueOf(Datepub));
                register.setInt(6, copies);
                register.setInt(7, totalCopies);

                // Use executeUpdate() for INSERT, not executeQuery()
                register.executeUpdate();
                return true;

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        public static boolean validateaddedBooks(int Id, String Title) {
            try (Connection connection = DriverManager.getConnection(DB_Connection.book, DB_Connection.user, DB_Connection.pass);
                 PreparedStatement valid = connection.prepareStatement("SELECT * FROM " + DB_Connection.BookTable +
                         " WHERE Id = ? AND Title = ?")) {

                valid.setInt(1, Id);
                valid.setString(2, Title);

                // Store the result to check if the query returned any rows
                ResultSet rs = valid.executeQuery();
                return rs.next(); // Returns true if book exists, false otherwise

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        //todo applly this in the admin control class


        public static boolean delete(int ID) {
            try (Connection connection = DriverManager.getConnection(DB_Connection.book, DB_Connection.user, DB_Connection.pass);
                 // Remove the * from DELETE query - it's not needed
                 PreparedStatement del = connection.prepareStatement("DELETE FROM " + DB_Connection.BookTable +
                         " WHERE Id = ?")) {

                del.setInt(1, ID);
                int rowsAffected = del.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Row deleted Successfully");
                    return true;
                } else {
                    System.out.println("No rows found with ID: " + ID);
                    return false;
                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        public static List<Books> LoadBooks() {
            List<Books> books = new ArrayList<>();

            try (Connection connection = DriverManager.getConnection(DB_Connection.book, DB_Connection.user, DB_Connection.pass);
                 Statement stmt = connection.createStatement();
                 ResultSet resultSet = stmt.executeQuery("SELECT * FROM " + DB_Connection.BookTable)) {

                while (resultSet.next()) {
                    int id = resultSet.getInt("Id");
                    String title = resultSet.getString("Title");
                    String genre = resultSet.getString("Genre");
                    String author = resultSet.getString("Author");
                    Date datepub = resultSet.getDate("Publish_Date");
                    int copies = resultSet.getInt("Copies");
                    int totalCopies = resultSet.getInt("Total_Copies");

                    Books book = new Books(id, title, genre, author, datepub, copies, totalCopies);
                    books.add(book);
                }
            } catch (SQLException e) {
                System.err.println("Error loading books: " + e.getMessage());
                e.printStackTrace();
            }
            return books;
        }


    public static boolean UpdateBook(int Id, String title, String genre, String author, LocalDateTime Datepub, int copies, int totalCopies) {
            try (Connection connection = DriverManager.getConnection(DB_Connection.book, DB_Connection.user, DB_Connection.pass);
                 PreparedStatement update = connection.prepareStatement("UPDATE " + DB_Connection.BookTable +
                         " SET Title=?, Genre=?, Author=?, Publish_date=?, Copies=?, Total_copies=? WHERE Id=?")) {

                update.setString(1, title);
                update.setString(2, genre);
                update.setString(3, author);
                update.setTimestamp(4, Timestamp.valueOf(Datepub));
                update.setInt(5, copies);
                update.setInt(6, totalCopies);
                update.setInt(7, Id);  // WHERE clause parameter

                int rowsAffected = update.executeUpdate();
                return rowsAffected > 0;

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        public static void updateBookCopies(int isbn, int availableCopies) {
            System.out.println("Updating book with ISBN: " + isbn + ", New copies: " + availableCopies);

            if (isbn <= 0 || availableCopies < 0) {
                throw new IllegalArgumentException("Invalid ISBN or copy count");
            }

            try (Connection connection = DriverManager.getConnection(DB_Connection.book, DB_Connection.user, DB_Connection.pass);
                 PreparedStatement update = connection.prepareStatement(
                         "UPDATE " + DB_Connection.BookTable + " SET Copies = ? WHERE Id = ?")) {

                update.setInt(1, availableCopies);
                update.setInt(2, isbn);

                int rowsUpdated = update.executeUpdate();
                if (rowsUpdated == 0) {
                    throw new SQLException("Book with ISBN " + isbn + " not found");
                }

            } catch (SQLException e) {
                System.err.println("Database error updating copies: " + e.getMessage());
                throw new RuntimeException("Failed to update book copies: " + e.getMessage(), e);
            }
        }

    }
package DSA.Admin;

import DSA.Objects.users;
import java.sql.*;
import java.util.Hashtable;

public class USER_DB {
    private static Hashtable<Integer, users> userCache = new Hashtable<>();  // Initialize here

    public USER_DB() {
        // Constructor can be empty now since we initialize the cache above
    }
    public static boolean add(users user) {
        try (Connection connection = DriverManager.getConnection(DB_Connection.url, DB_Connection.user, DB_Connection.pass);
             PreparedStatement register = connection.prepareStatement(
                     "INSERT INTO " + DB_Connection.tab +
                             "(Id, lastName, firstName, password, email, Gender, `Limit`) VALUES(?, ?, ?, ?, ?, ?, ?)")) {

            register.setInt(1, user.getId());
            register.setString(2, user.getLastName());
            register.setString(3, user.getFirstName());
            register.setString(4, user.getPass());
            register.setString(5, user.getEmail());
            register.setString(6, user.getGender());
            register.setInt(7, user.getLimit());

            int rowsAffected = register.executeUpdate();

            if (rowsAffected > 0) {
                userCache.put(user.getId(), user);
                return true;
            }
            return false;

        } catch (SQLException e) {
            if (e.getSQLState().equals("23000")) {
                System.out.println("User with this ID already exists");
            } else {
                System.out.println("Error adding user: " + e.getMessage());
            }
            return false;
        }
    }

    public boolean updateUser(users user) {
        try (Connection connection = DriverManager.getConnection(DB_Connection.url, DB_Connection.user, DB_Connection.pass);
             PreparedStatement update = connection.prepareStatement(
                     "UPDATE " + DB_Connection.tab +
                             " SET lastName = ?, firstName = ?, password = ?, email = ?, Gender = ?, `limit` = ? " +
                             "WHERE Id = ?")) {

            update.setString(1, user.getLastName());
            update.setString(2, user.getFirstName());
            update.setString(3, user.getPass());
            update.setString(4, user.getEmail());
            update.setString(5, user.getGender());
            update.setInt(6, user.getLimit());
            update.setInt(7, user.getId());

            int rowsAffected = update.executeUpdate();

            if (rowsAffected > 0) {
                // Update cache if database update was successful
                userCache.put(user.getId(), user);
                return true;
            }
            return false;

        } catch (SQLException e) {
            System.out.println("Error updating user: " + e.getMessage());
            return false;
        }
    }

    public static boolean validate(int id, String password) {
        try (Connection connection = DriverManager.getConnection(DB_Connection.url, DB_Connection.user, DB_Connection.pass);
             PreparedStatement valid = connection.prepareStatement(
                     "SELECT * FROM " + DB_Connection.tab +
                             " WHERE Id = ? AND password = ?")) {

            valid.setInt(1, id);
            valid.setString(2, password);

            ResultSet rs = valid.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.out.println("Error validating user: " + e.getMessage());
            return false;
        }
    }

        public Hashtable<Integer, users> loadAllUsers() {
            userCache.clear(); // Clear the cache before reloading

        try (Connection connection = DriverManager.getConnection(DB_Connection.url, DB_Connection.user, DB_Connection.pass);
             PreparedStatement stmt = connection.prepareStatement("SELECT * FROM " + DB_Connection.tab);
             ResultSet result = stmt.executeQuery()) {

            while (result.next()) {
                users user = new users(
                        result.getInt("Id"),
                        result.getString("lastName"),  // Fixed column name
                        result.getString("firstName"), // Fixed column name
                        result.getString("password"),
                        result.getString("email"),
                        result.getString("Gender"),
                        result.getInt("limit")
                );
                userCache.put(user.getId(), user);
            }

        } catch (SQLException e) {
            System.out.println("Error loading users: " + e.getMessage());
        }
        return userCache;
    }

    public users getUser(int id) {
        if (userCache.containsKey(id)) {
            return userCache.get(id);
        }

        try (Connection connection = DriverManager.getConnection(DB_Connection.url, DB_Connection.user, DB_Connection.pass);
             PreparedStatement stmt = connection.prepareStatement(
                     "SELECT * FROM " + DB_Connection.tab + " WHERE Id = ?")) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                users user = new users(
                        rs.getInt("Id"),
                        rs.getString("lastName"),  // Fixed column name
                        rs.getString("firstName"), // Fixed column name
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getString("Gender"),
                        rs.getInt("limit")
                );
                userCache.put(id, user);
                return user;
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving user: " + e.getMessage());
        }

        return null;
    }

    public boolean deleteUser(int id) {
        try (Connection connection = DriverManager.getConnection(DB_Connection.url, DB_Connection.user, DB_Connection.pass);
             PreparedStatement del = connection.prepareStatement("DELETE FROM " + DB_Connection.tab + " WHERE Id = ?")) {

            del.setInt(1, id);
            int rowsAffected = del.executeUpdate();

            if (rowsAffected > 0) {
                userCache.remove(id);
                System.out.println("User deleted successfully");
                return true;
            } else {
                System.out.println("Failed to delete user with ID: " + id);
                return false;
            }

        } catch (SQLException e) {
            if (e.getSQLState().equals("23000")) {
                System.out.println("Cannot delete user: User has associated records");
            } else {
                System.out.println("Error deleting user: " + e.getMessage());
            }
            return false;
        }
    }
}
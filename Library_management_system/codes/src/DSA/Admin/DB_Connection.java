package DSA.Admin;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class DB_Connection {
    private static final Properties properties = new Properties();
    static {
        try {
            // Load properties from file
            loadProperties();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadProperties() throws IOException {
        String env = System.getProperty("env", "local"); // Default to local if not specified
        String configFile = String.format("Library_management_system/config.properties", env);

        try (FileInputStream input = new FileInputStream(configFile)) {
            properties.load(input);
        }
    }
    //Book db connections
    public static String book = properties.getProperty("db.book");
    public static String BookTable = properties.getProperty("db.booktable");
    //DataBase Acccess
    public static String user = properties.getProperty("db.user");
    public static String pass = properties.getProperty("db.password");
    //User Database connections
    public static String url = properties.getProperty("db.url");
    public static String tab = properties.getProperty("db.table");
    //Borrowed History Connections
    public static String BorrowedHistory = properties.getProperty("db.history");
    public static String HistoryTable = properties.getProperty("db.historytable");


}

package dataaccess;

import java.sql.*;
import java.util.Properties;

/**
 * Manages database connections and initial database creation.
 */
public class DatabaseManager {

    private static final String DATABASE;
    private static final String USER;
    private static final String PASSWORD;
    private static final String URL;

    static {
        Properties props = new Properties();
        try (var propStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties")) {
            if (propStream == null) {
                throw new RuntimeException("Could not find db.properties. Please resolve the issue.");
            }
            props.load(propStream);
            DATABASE = props.getProperty("db.name");
            USER = props.getProperty("db.user");
            PASSWORD = props.getProperty("db.password");

            String host = props.getProperty("db.host");
            int port = Integer.parseInt(props.getProperty("db.port"));
            URL = String.format("jdbc:mysql://%s:%d", host, port);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to load db.properties: " + ex.getMessage(), ex);
        }
    }

    /**
     * Creates the database if it does not exist.
     *
     * @throws DataAccessException If an error occurs during database creation
     */
    public static void createDatabase() throws DataAccessException {
        String statement = "CREATE DATABASE IF NOT EXISTS " + DATABASE;
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement preparedStatement = conn.prepareStatement(statement)) {

            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            throw new DataAccessException("Error creating database: " + exception.getMessage());
        }
    }

    /**
     * Gets a connection to the database.
     *
     * @return A connection to the database
     * @throws DataAccessException If an error occurs during database connection
     */
    public static Connection getConnection() throws DataAccessException {
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            conn.setCatalog(DATABASE);
            return conn;
        } catch (SQLException exception) {
            throw new DataAccessException("Error connecting to the database: " + exception.getMessage());
        }
    }
}
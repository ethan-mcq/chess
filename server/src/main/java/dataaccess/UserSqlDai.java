package dataaccess;

import model.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Data Access Implementation for managing user-related SQL operations.
 */
public class UserSqlDai extends BaseSqlDai implements UserDao {

    /**
     * Initializes a new instance of the UserSqlDai class.
     *
     * @throws DataAccessException If an error occurs during initialization
     */
    public UserSqlDai() throws DataAccessException {
        super();
    }

    /**
     * Initializes database tables specific to user Data.
     *
     * @throws DataAccessException If an error occurs during initialization
     */
    @Override
    protected void initializeDatabaseTables() throws DataAccessException {
        String sqlInjection = "CREATE TABLE IF NOT EXISTS userData (" +
                "id INTEGER PRIMARY KEY AUTO_INCREMENT, " +
                "username VARCHAR(255) NOT NULL UNIQUE, " +
                "email VARCHAR(255) NOT NULL, " +
                "password VARCHAR(255) NOT NULL)";

        executeSqlUpdate(sqlInjection);
    }

    /**
     * Retrieves a user by username.
     *
     * @param username The username of the user to retrieve
     * @return The user Data or null if not found
     * @throws DataAccessException If an error occurs during retrieval
     */
    @Override
    public UserM getUser(String username) throws DataAccessException {
        String sqlInjection = "SELECT email, password FROM userData WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sqlInjection)) {

            preparedStatement.setString(1, username);

            try (var resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String email = resultSet.getString("email");
                    String password = resultSet.getString("password");
                    return new UserM(username, password, email);
                }
            }
        } catch (SQLException exception) {
            throw new DataAccessException("Error retrieving user: " + exception.getMessage());
        }
        return null; // If no user is found, return null
    }

    /**
     * Inserts a new user into the database.
     *
     * @param user The user Data to insert
     * @return The inserted user Data
     * @throws DataAccessException If an error occurs during insertion
     */
    @Override
    public UserM insertUser(UserM user) throws DataAccessException {
        String sqlInjection = "INSERT INTO userData (username, email, password) VALUES (?, ?, ?)";
        executeSqlUpdate(sqlInjection, user.username(), user.email(), user.password());
        return user;
    }

    /**
     * Deletes all users from the database.
     *
     * @throws DataAccessException If an error occurs during deletion
     */
    @Override
    public void deleteAllUsers() throws DataAccessException {
        String sqlInjection = "TRUNCATE TABLE userData";
        executeSqlUpdate(sqlInjection);
    }
}
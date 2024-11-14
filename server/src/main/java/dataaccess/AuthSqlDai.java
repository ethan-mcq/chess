package dataaccess;

import model.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Data Access Implementation for managing authentication-related SQL operations.
 */
public class AuthSqlDai extends BaseSqlDai implements AuthDao {

    /**
     * Initializes a new instance of the AuthSqlDai class.
     *
     * @throws DataAccessException If an error occurs during initialization
     */
    public AuthSqlDai() throws DataAccessException {
        super();
    }

    /**
     * Initializes database tables specific to authentication.
     *
     * @throws DataAccessException If an error occurs during initialization
     */
    @Override
    protected void initializeDatabaseTables() throws DataAccessException {
        String sqlInjection = "CREATE TABLE IF NOT EXISTS sessions (" +
                "id INTEGER PRIMARY KEY AUTO_INCREMENT, " +
                "username VARCHAR(255) NOT NULL, " +
                "authToken VARCHAR(512) NOT NULL UNIQUE)";

        executeSqlUpdate(sqlInjection);
    }

    /**
     * Inserts a new authentication session.
     *
     * @param auth The authentication Data to insert
     * @return The inserted authentication Data or null if failed
     * @throws DataAccessException If an error occurs during insertion
     */
    @Override
    public Auth insertAuth(Auth auth) throws DataAccessException {
        String sqlInjection = "INSERT INTO sessions (username, authToken) VALUES (?, ?)";
        try {
            executeSqlUpdate(sqlInjection, auth.username(), auth.authToken());
            return auth;
        } catch (DataAccessException exception) {
            // Optionally log the error here
            return null;
        }
    }

    /**
     * Removes an authentication session by token.
     *
     * @param authToken The token of the session to remove
     * @return The removed authentication Data
     * @throws DataAccessException If an error occurs during removal
     */
    @Override
    public Auth removeAuth(String authToken) throws DataAccessException {
        Auth auth = getAuth(authToken);

        if (auth != null) {
            String sqlInjection = "DELETE FROM sessions WHERE authToken = ?";
            executeSqlUpdate(sqlInjection, authToken);
        }

        return auth;
    }

    /**
     * Retrieves an authentication session by token.
     *
     * @param authToken The token of the session to retrieve
     * @return The authentication Data or null if not found
     * @throws DataAccessException If an error occurs during retrieval
     */
    @Override
    public Auth getAuth(String authToken) throws DataAccessException {
        String sqlInjection = "SELECT username FROM sessions WHERE authToken = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sqlInjection)) {

            preparedStatement.setString(1, authToken);

            try (var resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String username = resultSet.getString("username");
                    return new Auth(authToken, username);
                }
            }
        } catch (SQLException exception) {
            throw new DataAccessException("Error retrieving Auth Data: " + exception.getMessage());
        }
        return null;
    }

    /**
     * Removes all authentication sessions.
     *
     * @throws DataAccessException If an error occurs during removal
     */
    @Override
    public void removeAuth() throws DataAccessException {
        String sqlInjection = "TRUNCATE TABLE sessions";
        executeSqlUpdate(sqlInjection);
    }
}
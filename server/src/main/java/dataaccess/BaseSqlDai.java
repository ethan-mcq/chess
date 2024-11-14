package dataaccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Abstract base class for handling SQL Data Access operations.
 */
public abstract class BaseSqlDai {

    /**
     * Constructor which initializes the database tables.
     *
     * @throws DataAccessException If an error occurs during initialization
     */
    protected BaseSqlDai() throws DataAccessException {
        initializeDatabaseTables();
    }

    /**
     * Initializes the database tables. Subclasses should override
     * this method to provide specific table initialization logic.
     *
     * @throws DataAccessException If an error occurs during initialization
     */
    protected void initializeDatabaseTables() throws DataAccessException {}

    /**
     * Executes a SQL update statement.
     *
     * @param sqlInjection The SQL statement to execute
     * @param params The parameters to set in the SQL statement
     * @throws DataAccessException If an error occurs during execution
     */
    protected void executeSqlUpdate(String sqlInjection, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sqlInjection)) {
            setParameters(preparedStatement, params);
            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            throw new DataAccessException("Error executing SQL update: " + exception.getMessage());
        }
    }

    /**
     * Executes a SQL update statement and returns the generated key.
     *
     * @param sql The SQL statement to execute
     * @param params The parameters to set in the SQL statement
     * @return The generated key from the update statement
     * @throws DataAccessException If an error occurs during execution
     */
    protected int executeSqlUpdateAndGetId(String sqlInjection, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sqlInjection, Statement.RETURN_GENERATED_KEYS)) {
            setParameters(preparedStatement, params);
            preparedStatement.executeUpdate();

            try (var generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new DataAccessException("No generated key returned.");
                }
            }
        } catch (SQLException exception) {
            throw new DataAccessException("Error executing SQL update and retrieving generated key: " + exception.getMessage());
        }
    }

    /**
     * Sets the parameters of a PreparedStatement.
     *
     * @param preparedStatement The PreparedStatement to set parameters on
     * @param params The parameters to set
     * @throws SQLException If an error occurs while setting the parameters
     */
    private void setParameters(PreparedStatement preparedStatement, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            if (params[i] == null) {
                preparedStatement.setNull(i + 1, java.sql.Types.VARCHAR);
            } else if (params[i] instanceof String) {
                preparedStatement.setString(i + 1, (String) params[i]);
            } else if (params[i] instanceof Integer) {
                preparedStatement.setInt(i + 1, (Integer) params[i]);
            } else {
                throw new SQLException("Unknown parameter type at index " + i);
            }
        }
    }
}
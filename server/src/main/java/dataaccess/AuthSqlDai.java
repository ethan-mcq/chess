package dataaccess;

import model.*;

import java.sql.SQLException;

public class AuthSqlDai extends BaseSqlDai implements authDAO {
    public AuthSqlDai() throws DataAccessException{
        super();

    }

    @Override
    protected void initalizeDatabaseTables() throws DataAccessException{
        String sql = "CREATE TABLE IF NOT EXISTS sessions (";
        sql += "id INTEGER PRIMARY KEY AUTO_INCREMENT,";
        sql += "username VARCHAR(255) NOT NULL,";
        sql += "authToken VARCHAR(512) NOT NULL UNIQUE)";

        this.executeSqlUpdate(sql);
    }

    @Override
    public auth insertAuth(auth auth) throws DataAccessException {
        String sql = "INSERT INTO sessions (username, authToken) VALUES (?, ?)";
        try {
            this.executeSqlUpdate(sql, auth.username(), auth.authToken());
        } catch (DataAccessException e) {
            return null;
        }
        return auth;
    }

    @Override
    public auth removeAuth(String authToken) throws DataAccessException {
        auth auth = getAuth(authToken);

        String sql = "DELETE FROM sessions WHERE authToken = ?";
        this.executeSqlUpdate(sql, authToken);
        return auth;
    }

    @Override
    public auth getAuth(String authToken) throws DataAccessException {
        String sql = "SELECT username FROM sessions WHERE authToken = ?";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(sql)) {
                preparedStatement.setString(1, authToken);
                try (var resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        String username = resultSet.getString("username");
                        return new auth(authToken, username);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        return null;
    }

    @Override
    public void removeAuth() throws DataAccessException {
        String sql = "TRUNCATE sessions";
        this.executeSqlUpdate(sql);
    }
}

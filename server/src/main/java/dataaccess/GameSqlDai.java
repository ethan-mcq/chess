package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.*;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Data Access Implementation for managing Game-related SQL operations.
 */
public class GameSqlDai extends BaseSqlDai implements GameDao {

    /**
     * Initializes a new instance of the GameSqlDai class.
     *
     * @throws DataAccessException If an error occurs during initialization
     */
    public GameSqlDai() throws DataAccessException {
        super();
    }

    /**
     * Initializes database tables specific to games.
     *
     * @throws DataAccessException If an error occurs during initialization
     */
    @Override
    protected void initializeDatabaseTables() throws DataAccessException {
        String sqlInjection = "CREATE TABLE IF NOT EXISTS games (" +
                "id INTEGER PRIMARY KEY AUTO_INCREMENT," +
                "name VARCHAR(255) NOT NULL," +
                "white_username VARCHAR(255) DEFAULT NULL," +
                "black_username VARCHAR(255) DEFAULT NULL," +
                "game TEXT NOT NULL)";

        executeSqlUpdate(sqlInjection);
    }

    /**
     * Retrieves all games from the database.
     *
     * @return A list of all games
     * @throws DataAccessException If an error occurs during retrieval
     */
    @Override
    public GameList getAllGames() throws DataAccessException {
        String sqlInjection = "SELECT id, name, white_username, black_username, game FROM games";
        ArrayList<GameResponse> games = new ArrayList<>();

        try (var conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(sqlInjection);
             var resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String whiteUsername = resultSet.getString("white_username");
                String blackUsername = resultSet.getString("black_username");
                games.add(new GameResponse(id, whiteUsername, blackUsername, name));
            }
        } catch (SQLException exception) {
            throw new DataAccessException("Error retrieving games: " + exception.getMessage());
        }
        return new GameList(games);
    }

    /**
     * Retrieves game Data for a specified game ID.
     *
     * @param gameId The ID of the game to retrieve
     * @return The game Data
     * @throws DataAccessException If an error occurs during retrieval
     */
    @Override
    public GameData getGames(int gameId) throws DataAccessException {
        String sqlInjection = "SELECT id, name, white_username, black_username, game FROM games WHERE id = ?";
        try (var conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(sqlInjection)) {
            preparedStatement.setInt(1, gameId);
            try (var resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String name = resultSet.getString("name");
                    String whiteUsername = resultSet.getString("white_username");
                    String blackUsername = resultSet.getString("black_username");
                    ChessGame game = deserializeGameData(resultSet.getString("game"));
                    return new GameData(id, name, whiteUsername, blackUsername, game);
                }
            }
        } catch (SQLException exception) {
            throw new DataAccessException("Error retrieving game: " + exception.getMessage());
        }
        return null; // If no game is found, return null
    }

    /**
     * Adds a player to an existing game.
     *
     * @param join Information about the player joining the game
     * @return The updated game Data or null if unsuccessful
     * @throws DataAccessException If an error occurs during the Join operation
     */
    @Override
    public GameData joinGame(Join join) throws DataAccessException {
        String sqlInjection;
        if ("WHITE".equals(join.playerColor())) {
            sqlInjection = "UPDATE games SET white_username = ? WHERE id = ? AND white_username IS NULL";
        } else {
            sqlInjection = "UPDATE games SET black_username = ? WHERE id = ? AND black_username IS NULL";
        }

        try (var conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(sqlInjection)) {
            preparedStatement.setString(1, join.username());
            preparedStatement.setInt(2, join.gameID());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                return getGames(join.gameID());
            } else {
                return null; // If no rows were affected, the Join operation was unsuccessful
            }
        } catch (SQLException exception) {
            throw new DataAccessException("Error joining game: " + exception.getMessage());
        }
    }

    /**
     * Creates a new game.
     *
     * @param gameData The Data for the game to be created
     * @return The created game Data
     * @throws DataAccessException If an error occurs during the creation
     */
    @Override
    public GameData createGame(GameData gameData) throws DataAccessException {
        String sqlInjection = "INSERT INTO games (name, white_username, black_username, game) VALUES(?, ?, ?, ?)";
        int gameId = executeSqlUpdateAndGetId(sqlInjection,
                gameData.gameName(),
                gameData.whiteUsername() == null ? null : gameData.whiteUsername(),
                gameData.blackUsername() == null ? null : gameData.blackUsername(),
                new Gson().toJson(gameData.game(), ChessGame.class)
        );
        return getGames(gameId);
    }

    @Override
    public GameData updateGame(int gameID, GameData game) throws DataAccessException {
        if (game == null) {
            throw new DataAccessException("game cant be null");
        }
        var statement = "UPDATE games SET name = ?, white_username = ?, black_username = ?, game = ? WHERE id = ?";
        var gameJSON = new Gson().toJson(game.game());
        executeSqlUpdate(statement, game.gameName(), game.whiteUsername(), game.blackUsername(), gameJSON, gameID);
        return getGames(gameID);
    }

    /**
     * Deletes all games from the database.
     *
     * @throws DataAccessException If an error occurs during the deletion
     */
    @Override
    public void deleteAllGames() throws DataAccessException {
        String sqlInjection = "TRUNCATE games";
        executeSqlUpdate(sqlInjection);
    }

    /**
     * Deserializes the game Data from a JSON string.
     *
     * @param json The JSON string representing the game Data
     * @return The deserialized ChessGame object
     */
    private ChessGame deserializeGameData(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, ChessGame.class);
    }
}
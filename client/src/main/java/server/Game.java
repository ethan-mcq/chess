package server;

/**
 * Represents a Game with basic details such as index, name, game ID, and the players involved.
 */
public class Game {

    private int index;            // The index of the game in the list
    private String name;          // The name of the game
    private int gameId;           // Unique identifier for the game
    private String whitePlayer;   // Username of the player playing with white pieces
    private String blackPlayer;   // Username of the player playing with black pieces

    /**
     * Constructs a Game instance with specified details.
     *
     * @param index      The index of the game in the list.
     * @param name       The name of the game.
     * @param gameId     Unique identifier for the game.
     * @param white      Username of the white player.
     * @param black      Username of the black player.
     */
    public Game(int index, String name, int gameId, String white, String black) {
        this.index = index;
        this.name = name;
        this.gameId = gameId;
        this.whitePlayer = white;
        this.blackPlayer = black;
    }

    // Getters for retrieving game details

    public int getIndex() {
        return index;
    }

    public int getGameID() {
        return gameId;
    }

    public String getName() {
        return name;
    }

    public String getWhitePlayer() {
        return whitePlayer;
    }

    public String getBlackPlayer() {
        return blackPlayer;
    }

    // Setters for updating player information

    /**
     * Sets the username of the white player.
     *
     * @param userName The new username for the white player.
     */
    public void setWhite(String userName) {
        whitePlayer = userName;
    }

    /**
     * Sets the username of the black player.
     *
     * @param userName The new username for the black player.
     */
    public void setBlack(String userName) {
        blackPlayer = userName;
    }

    /**
     * Returns a string representation of the game, including player details.
     * Displays "____" if a player is not assigned.
     *
     * @return A string describing the game.
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("Game ID - ").append(index).append("   Game Name - ").append(name).append("   White Player: ");
        s.append(whitePlayer != null ? whitePlayer : "____");
        s.append("    Black Player: ");
        s.append(blackPlayer != null ? blackPlayer : "____");
        return s.toString();
    }
}
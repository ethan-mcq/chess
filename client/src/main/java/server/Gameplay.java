package server;

import chess.*;
import exception.InputException;
import model.GameData;
import websocket.messages.ServerMessage;
import static server.EscapeSequences.*;

import java.util.Arrays;
import java.util.Collection;

public class Gameplay implements Client {
    private final String serverUrl;
    private final Repl repl;
    public GameData chessGame;
    private Boolean flipBoard = true;
    private WebSocketFacade ws;

    public Gameplay(String serverUrl, Repl repl) {
        this.serverUrl = serverUrl;
        this.repl = repl;
    }

    public void upgradeToWebsocket() {
        try {
            ws = new WebSocketFacade(serverUrl, repl);
            ws.connectToGame(chessGame.gameID(), repl.getAuthData().authToken());
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Override
    public String checker(String command) {
        try {
            var tokens = command.split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            if(repl.getState() == State.OBSERVING) {
                return switch (cmd) {
                    case "help" -> helper();
                    case "redraw", "r" -> printChessboard(null);
                    case "leave", "b" -> leave();
                    case "quit", "q" -> quit();
                    default -> helper();
                };
            } else {
                return switch (cmd) {
                    case "help" -> helper();
                    case "redraw", "r" -> printChessboard(null);
                    case "select", "s" -> select(params[0]);
                    case "move", "m" -> makeMove(params[0], params[1]);
                    case "resign", "res" -> resign();
                    case "leave", "b" -> leave();
                    case "quit", "q" -> quit();
                    default -> helper();
                };
            }
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private String resign() {
        try {
            ws.resign(chessGame.gameID(), repl.getAuthData().authToken());
            return "You have resigned";
        } catch (Exception e) {
            System.out.println(e);
            return "An error has occurred during your resignation";
        }
    }

    private String quit() {
        leave();
        return "quit";
    }

    public String printChessboard(ChessPosition selected) {
        boolean flip = chessGame.blackUsername() != null && chessGame.blackUsername().equals(repl.getAuthData().username());
        Collection<ChessMove> validMoves = selected != null ?
                chessGame.game().getBoard().getPiece(selected).pieceMoves(chessGame.game().getBoard(), selected) :
                null; // Fetch valid moves only if there's a selected piece

        return "In Game " + chessGame.gameName() +
                "\n\n" +
                renderBoard(chessGame.game().getBoard(), flip ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE, selected, validMoves);
    }

    private static String renderBoard(ChessBoard board, ChessGame.TeamColor perspective, ChessPosition selected, Collection<ChessMove> validMoves) {
        StringBuilder sb = new StringBuilder();
        int start = perspective == ChessGame.TeamColor.WHITE ? 7 : 0;
        int end = perspective == ChessGame.TeamColor.WHITE ? 0 : 7;
        int increment = perspective == ChessGame.TeamColor.WHITE ? -1 : 1;

        appendColumnHeaders(sb, perspective);
        for (int i = start; i != end + increment; i += increment) {
            sb.append(RESET_BG_COLOR).append("\n").append(i + 1).append(" ");
            for (int j = 0; j <= 7; j++) {
                int colIndex = perspective == ChessGame.TeamColor.WHITE ? j : 7 - j;
                ChessPosition currentPos = new ChessPosition(i + 1, colIndex + 1);
                boolean isLight = (i + colIndex) % 2 == 0;
                String backgroundColor;

                if (selected != null && selected.equals(currentPos)) {
                    backgroundColor = SET_BG_COLOR_DARK_GREEN; // Highlight selected space in green
                } else if (validMoves != null && validMoves.stream().anyMatch(move -> move.getEndPosition().equals(currentPos))) {
                    backgroundColor = SET_BG_COLOR_DARK_GREEN; // Highlight possible move positions in green
                } else {
                    backgroundColor = isLight ? SET_BG_COLOR_LIGHT_GREY : SET_BG_COLOR_DARK_GREY;
                }
                sb.append(backgroundColor).append(getPieceSymbol(board.getPiece(currentPos)));
            }
            sb.append(RESET_BG_COLOR).append(" ").append(i + 1).append(" ");
        }
        sb.append("\n");
        appendColumnHeaders(sb, perspective);
        sb.append(RESET_BG_COLOR).append("\n");
        return sb.toString();
    }

    private static void appendColumnHeaders(StringBuilder sb, ChessGame.TeamColor perspective) {
        if (perspective == ChessGame.TeamColor.BLACK) {
            sb.append("   h  g  f  e  d  c  b  a     ");
        } else {
            sb.append("   a  b  c  d  e  f  g  h     ");
        }
    }

    public static String getPieceSymbol(ChessPiece piece) {
        if (piece == null) {
            return "   ";
        }
        boolean isWhite = piece.getTeamColor() == ChessGame.TeamColor.WHITE;
        return switch (piece.getPieceType()) {
            case KING -> isWhite ? BLACK_KING : WHITE_KING;
            case QUEEN -> isWhite ? BLACK_QUEEN : WHITE_QUEEN;
            case ROOK -> isWhite ? BLACK_ROOK : WHITE_ROOK;
            case BISHOP -> isWhite ? BLACK_BISHOP : WHITE_BISHOP;
            case KNIGHT -> isWhite ? BLACK_KNIGHT : WHITE_KNIGHT;
            case PAWN -> isWhite ? BLACK_PAWN : WHITE_PAWN;
            default -> "   "; // Default for unrecognized pieces
        };
    }

    private String leave() {
        try {
            ws.leaveGame(chessGame.gameID(), repl.getAuthData().authToken());
            return backToMenu();
        } catch (Exception e) {
            System.out.println(e);
        }
        return "You couldn't leave for some unknown reason";
    }

    private void setBackGroundColor(StringBuilder stringBuilder,
                                    int i,
                                    int j,
                                    ChessPiece[][] board,
                                    Collection<ChessMove> validMoves,
                                    ChessPosition selected) {
        ChessMove move = new ChessMove(selected, new ChessPosition(i, j), null);
        if (validMoves != null && validMoves.contains(move)) {
            stringBuilder.append(SET_BG_COLOR_MAGENTA);
        } else if (selected != null && selected.getColumn() == j && selected.getRow() == i) {
            stringBuilder.append(SET_BG_COLOR_YELLOW);
        } else if(j == 0 || j == board.length-1 || i == 0 || i == board.length-1){
            stringBuilder.append(SET_BG_COLOR_DARK_GREY);
        } else if (j % 2 == 1 && i % 2 == 0) {
            stringBuilder.append(SET_BG_COLOR_LIGHT_GREY);
        } else if (j % 2 == 0 && i % 2 == 1) {
            stringBuilder.append(SET_BG_COLOR_LIGHT_GREY);
        } else {
            stringBuilder.append(SET_BG_COLOR_DARK_GREEN);
        }
    }

    public void setChessGame(GameData chessGame) {
        this.chessGame = chessGame;
    }

    private String backToMenu() {
        this.repl.changeState(State.SIGNEDIN);
        return "Welcome Back";
    }

    private String select(String selection) {
        try {
            int row = Character.getNumericValue(selection.charAt(1));
            int col = convertColToNumber(selection.charAt(0));
            return printChessboard(new ChessPosition(row,col));
        } catch (Exception e) {
            return "Invalid Command";
        }
    }

    private Integer convertColToNumber(char letter){
        return switch(letter) {
            case 'A', 'a' -> 1;
            case 'B', 'b' -> 2;
            case 'C', 'c' -> 3;
            case 'D', 'd' -> 4;
            case 'E', 'e' -> 5;
            case 'F', 'f' -> 6;
            case 'G', 'g' -> 7;
            case 'H', 'h' -> 8;
            default -> throw new RuntimeException(" ");
        };
    }

    private String makeMove(String startingPosition, String endingPosition){
        try {
            int startRow = (Character.getNumericValue(startingPosition.charAt(1)));
            int startCol = convertColToNumber(startingPosition.charAt(0));
            int endRow = (Character.getNumericValue(endingPosition.charAt(1)));
            int endCol = convertColToNumber(endingPosition.charAt(0));
            ChessPosition startPos = new ChessPosition(startRow, startCol);
            ChessPosition endPos = new ChessPosition(endRow, endCol);
            ChessMove move = new ChessMove(startPos, endPos, null);
            ws.makeMove(chessGame.gameID(), repl.getAuthData().authToken(), move);

        } catch (Exception e) {
            return "Invalid Command";
        }
        return "";
    }

    @Override
    public String helper() {
        if (repl.getState().equals(State.OBSERVING)) {
            return """
                - help - lists all possible commands
                - redraw - draws the chessboard again
                - leave - stop observing the game
                - quit - ends the client
                """;
        } else {
            return """
                - help - lists all possible commands
                - redraw - draws the chessboard again
                - select <A-H1-8> - selects a piece a the indicated row and column to see its moves
                - move <A-H1-8> <A-H1-8>- moves the piece in the first row
                    column selection to the second row and column selection
                - leave - removes player from the game leaving their spot open
                - resign - forfeits the match to the other player
                - quit - ends the client
                """;
        }
    }
}
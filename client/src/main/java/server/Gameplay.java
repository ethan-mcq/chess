package server;

import chess.*;
import model.GameData;
import static server.EscapeSequences.*;

import java.util.Arrays;
import java.util.Collection;

public class Gameplay implements Client {
    private final String serverUrl;
    private final Repl repl;
    public GameData chessGame;
    private WebSocketFacade webSocketFacade;

    public Gameplay(String serverUrl, Repl repl) {
        this.repl = repl;
        this.serverUrl = serverUrl;
    }

    public void changeToWs() {
        try {
            webSocketFacade = new WebSocketFacade(serverUrl, repl);
            webSocketFacade.connectToGame(chessGame.gameID(), repl.getAuthData().authToken());
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
                    case "HELP" -> helper();
                    case "DISPLAY", "r" -> printChessboard(null);
                    case "LEAVE", "b" -> leave();
                    case "QUIT", "q" -> quit();
                    default -> helper();
                };
            } else {
                return switch (cmd) {
                    case "HELP" -> helper();
                    case "DISPLAY", "r" -> printChessboard(null);
                    case "SELECT", "s" -> select(params[0]);
                    case "MOVE", "m" -> makeMove(params[0], params[1]);
                    case "RESIGN", "res" -> resign();
                    case "LEAVE", "b" -> leave();
                    case "QUIT", "q" -> quit();
                    default -> helper();
                };
            }
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private String resign() {
        try {
            webSocketFacade.resign(chessGame.gameID(), repl.getAuthData().authToken());
            return "FORFEITED";
        } catch (Exception e) {
            System.out.println(e);
            return "ERROR FORFEITING";
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

        return "IN GAME" + chessGame.gameName() +
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
            webSocketFacade.leaveGame(chessGame.gameID(), repl.getAuthData().authToken());
            return backToMenu();
        } catch (Exception e) {
            System.out.println(e);
        }
        return "LEAVING IS BROKEN, RESTART CLI OR TRY AGAIN";
    }

    public void setChessGame(GameData chessGame) {
        this.chessGame = chessGame;
    }

    private String backToMenu() {
        this.repl.changeState(State.SIGNEDIN);
        return "HOME SCREEN";
    }

    private String select(String selection) {
        try {
            int row = Character.getNumericValue(selection.charAt(1));
            int col = convertColToNumber(selection.charAt(0));
            return printChessboard(new ChessPosition(row,col));
        } catch (Exception e) {
            return "WRONG, AGAIN";
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
            webSocketFacade.makeMove(chessGame.gameID(), repl.getAuthData().authToken(), move);

        } catch (Exception e) {
            return "WRONG, AGAIN";
        }
        return "";
    }

    @Override
    public String helper() {
        if (repl.getState().equals(State.OBSERVING)) {
            return """
                HELP                               | Get help!
                DISPLAY                            | Display board
                LEAVE                              | Leave game
                QUIT                               | QUIT
                """;
        } else {
            return """
                HELP                               | Get help!
                DISPLAY                            | Display board
                SELECT  <A-H1-8>                   | Displays piece all possible moves
                MOVE <A-H1-8> <A-H1-8>             | Move Piece <START> <END>
                LEAVE                              | Leave game
                RESIGN                             | Forfeit game
                QUIT                               | QUIT
                """;
        }
    }
}
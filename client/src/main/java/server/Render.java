package server;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import ui.EscapeSequences;

/**
 * The Render class provides functionality to render a chess board to the console.
 */
public class Render {

    /**
     * Generates a string representation of the chess board from a given color perspective.
     *
     * @param board           The current state of the chess board.
     * @param perspective The team color perspective (WHITE or BLACK) for viewing the board.
     * @return A pretty-printed string representation of the board.
     */
    public static String RenderBoard(ChessBoard board, ChessGame.TeamColor perspective) {
        StringBuilder sb = new StringBuilder();

        // Define the start, end, and increment based on color perspective
        int start = perspective == ChessGame.TeamColor.BLACK ? 0 : 7;
        int end = perspective == ChessGame.TeamColor.BLACK ? 7 : 0;
        int increment = perspective == ChessGame.TeamColor.BLACK ? 1 : -1;

        // Append column headers
        appendColumnHeaders(sb, perspective);

        // Loop through each row and column to draw the board
        for (int i = start; i != end + increment; i += increment) {
            sb.append(EscapeSequences.RESET_BG_COLOR).append("\n").append(i + 1).append(" ");
            for (int j = start; j != end + increment; j += increment) {
                boolean isLight = (i + j) % 2 == 0;
                String backgroundColor = isLight ? EscapeSequences.SET_BG_COLOR_LIGHT_GREY : EscapeSequences.SET_BG_COLOR_DARK_GREY;
                sb.append(backgroundColor).append(getPieceSymbol(board.getPiece(new ChessPosition(i + 1, j + 1))));
            }
            sb.append(EscapeSequences.RESET_BG_COLOR).append(" ").append(i + 1).append(" ");
        }

        sb.append("\n");

        // Append column headers again at the bottom
        appendColumnHeaders(sb, perspective);

        sb.append(EscapeSequences.RESET_BG_COLOR).append("\n");

        return sb.toString();
    }

    /**
     * Appends column headers to the StringBuilder based on the color perspective of the board view.
     *
     * @param sb               The StringBuilder to append headers to.
     * @param perspective The color perspective for the board view.
     */
    private static void appendColumnHeaders(StringBuilder sb, ChessGame.TeamColor perspective) {
        if (perspective == ChessGame.TeamColor.BLACK) {
            sb.append("   a  b  c  d  e  f  g  h     ");
        } else {
            sb.append("   h  g  f  e  d  c  b  a     ");
        }
    }

    /**
     * Retrieves the symbol for a chess piece, color-coded and formatted for console rendering.
     *
     * @param piece The chess piece to get the symbol for.
     * @return A string symbol of the chess piece.
     */
    public static String getPieceSymbol(ChessPiece piece) {
        if (piece == null) {
            return "   ";
        }

        boolean isWhite = piece.getTeamColor() == ChessGame.TeamColor.WHITE;

        // Determine the symbol based on piece type and color
        return switch (piece.getPieceType()) {
            case KING -> isWhite ? EscapeSequences.WHITE_KING : EscapeSequences.BLACK_KING;
            case QUEEN -> isWhite ? EscapeSequences.WHITE_QUEEN : EscapeSequences.BLACK_QUEEN;
            case ROOK -> isWhite ? EscapeSequences.WHITE_ROOK : EscapeSequences.BLACK_ROOK;
            case BISHOP -> isWhite ? EscapeSequences.WHITE_BISHOP : EscapeSequences.BLACK_BISHOP;
            case KNIGHT -> isWhite ? EscapeSequences.WHITE_KNIGHT : EscapeSequences.BLACK_KNIGHT;
            case PAWN -> isWhite ? EscapeSequences.WHITE_PAWN : EscapeSequences.BLACK_PAWN;
        };
    }
}
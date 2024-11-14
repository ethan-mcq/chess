package chess;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMoves implements PieceMoves {

    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();
        int[][] directions = {
                {1, 1},  // Up right
                {1, -1}, // Up left
                {-1, 1}, // Down right
                {-1, -1} // Down left
        };
        for (int[] direction : directions) {
            addMovesToArray(board, position, moves, direction[0], direction[1]);
        }
        return moves;
    }

    private void addMovesToArray(ChessBoard board, ChessPosition startPosition, Collection<ChessMove> moves, int rowDir, int colDir) {
        int boardSize = 9;
        expandInDirection(board, startPosition, moves, rowDir, colDir, boardSize);
    }

    private void expandInDirection(ChessBoard board, ChessPosition startPosition, Collection<ChessMove> moves, int rowDir, int colDir, int boardSize) {
        ChessPosition currentPosition = startPosition;

        while (isWithinBoardLimits(currentPosition, rowDir, colDir, boardSize)) {
            currentPosition = new ChessPosition(currentPosition.getRow() + rowDir, currentPosition.getColumn() + colDir);

            ChessPiece pieceAtNewPosition = board.getPiece(currentPosition);
            addMoveOrCapture(board, startPosition, moves, currentPosition, pieceAtNewPosition);

            if (pieceAtNewPosition != null) {
                break; // Stop if a piece is encountered
            }
        }
    }

    private boolean isWithinBoardLimits(ChessPosition position, int rowDir, int colDir, int boardSize) {
        int newRow = position.getRow() + rowDir;
        int newCol = position.getColumn() + colDir;
        return newRow > 0 && newRow < boardSize && newCol > 0 && newCol < boardSize;
    }

    private void addMoveOrCapture(ChessBoard board, ChessPosition startPosition, Collection<ChessMove> moves, ChessPosition newPosition, ChessPiece pieceAtNewPosition) {
        if (pieceAtNewPosition == null) {
            moves.add(new ChessMove(startPosition, newPosition, null));
        } else if (pieceAtNewPosition.getTeamColor() != board.getPiece(startPosition).getTeamColor()) {
            moves.add(new ChessMove(startPosition, newPosition, null));
        }
    }
}
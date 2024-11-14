package chess;

import java.util.ArrayList;
import java.util.Collection;

public class QueenMoves implements PieceMoves {

    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();
        int[][] directions = {
                {1, 1}, // Up right
                {1, -1}, // Up left
                {-1, 1}, // Down right
                {-1, -1}, // Down left
                {1, 0}, // Up
                {-1, 0}, // Down
                {0, 1}, // right
                {0, -1} // left
        };
        for (int[] direction : directions) {
            exploreDirection(board, position, moves, direction[0], direction[1]);
        }

        return moves;
    }

    private void exploreDirection(ChessBoard board, ChessPosition initialPosition, Collection<ChessMove> moves, int rowIncrement, int colIncrement) {
        int currentRow = initialPosition.getRow() + rowIncrement;
        int currentCol = initialPosition.getColumn() + colIncrement;
        int boardLimit = 9;

        while (isWithinBoardLimits(currentRow, currentCol, boardLimit)) {
            ChessPosition newPosition = new ChessPosition(currentRow, currentCol);
            ChessPiece pieceAtNewPosition = board.getPiece(newPosition);
            ChessPiece pieceAtStart = board.getPiece(initialPosition);

            if (pieceAtNewPosition == null) {
                moves.add(new ChessMove(initialPosition, newPosition, null));
            } else {
                if (pieceAtNewPosition.getTeamColor() != pieceAtStart.getTeamColor()) {
                    moves.add(new ChessMove(initialPosition, newPosition, null));
                }
                break;
            }

            currentRow += rowIncrement;
            currentCol += colIncrement;
        }
    }

    private boolean isWithinBoardLimits(int row, int col, int limit) {
        return row > 0 && row < limit && col > 0 && col < limit;
    }
}
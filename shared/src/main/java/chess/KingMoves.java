package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KingMoves implements PieceMoves {

    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> potentialMoves = new ArrayList<>();
        int[][] moveDirections = {
                {1, 1}, // Up right
                {1, -1}, // Up left
                {-1, 1}, // Down right
                {-1, -1}, // Down left
                {1, 0}, // Up
                {-1, 0}, // Down
                {0, 1}, // right
                {0, -1} // left
        };
        for (int[] moveVector : moveDirections) {
            evaluateAndAddMove(board, position, potentialMoves, moveVector[0], moveVector[1]);
        }

        return potentialMoves;
    }

    private void evaluateAndAddMove(ChessBoard board, ChessPosition currentPos, Collection<ChessMove> potentialMoves, int deltaRow, int deltaCol) {
        int boardBoundary = 9;
        int targetRow = currentPos.getRow() + deltaRow;
        int targetCol = currentPos.getColumn() + deltaCol;

        if (withinBounds(targetRow, targetCol, boardBoundary)) {
            ChessPosition targetPosition = new ChessPosition(targetRow, targetCol);
            ChessPiece pieceAtTarget = board.getPiece(targetPosition);
            ChessPiece pieceAtCurrent = board.getPiece(currentPos);

            if (pieceAtTarget == null || pieceAtTarget.getTeamColor() != pieceAtCurrent.getTeamColor()) {
                potentialMoves.add(new ChessMove(currentPos, targetPosition, null));
            }
        }
    }

    private boolean withinBounds(int row, int col, int maxBoundary) {
        return col > 0 && col < maxBoundary && row > 0 && row < maxBoundary;
    }
}
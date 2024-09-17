package chess;
import java.util.ArrayList;
import java.util.Collection;

public class KnightMoves implements PieceMoves {

    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();
        int[][] directions = {
                {2, 1}, // Up 2 right 1
                {2, -1}, // Up 2 left 1
                {1, 2}, // Up 1 right 2
                {1, -2}, // Up 1 left 2
                {-1, 2}, // Down 1 right 2
                {-1, -2}, // Down 1 left 2
                {-2, 1}, // Down 2 right 1
                {-2, -1} // Down 2 left 1
        };
        for (int[] direction : directions) {
            addMovesToArray(board, position, moves, direction[0], direction[1]);
        }

        return moves;
    }
    private void addMovesToArray(ChessBoard board, ChessPosition startPosition, Collection<ChessMove> moves, int rowDir, int colDir) {
        int boardWidthLen = 9;
        int startRow = startPosition.getRow();
        int startCol = startPosition.getColumn();
        int moveRow = startRow + rowDir;
        int moveCol = startCol + colDir;

        if (moveCol > 0 && moveCol < boardWidthLen && moveRow > 0 && moveRow < boardWidthLen) {
            ChessPosition newPosition = new ChessPosition(moveRow, moveCol);
            ChessPiece targetPiece = board.getPiece(newPosition);
            if (targetPiece == null || targetPiece.getTeamColor() != board.getPiece(startPosition).getTeamColor()) {
                moves.add(new ChessMove(startPosition, newPosition, null));
            }
        }
    }
}
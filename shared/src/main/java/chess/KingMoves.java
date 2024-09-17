package chess;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ScheduledExecutorService;

public class KingMoves implements PieceMoves {

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
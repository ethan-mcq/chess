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
            addMovesToArray(board, position, moves, direction[0], direction[1]);
        }

        return moves;
    }
    private void addMovesToArray(ChessBoard board, ChessPosition startPosition, Collection<ChessMove> moves, int rowDir, int colDir) {
        int boardWidthLen = 9;
        int row = startPosition.getRow();
        int col = startPosition.getColumn();
        while (true) {
            row += rowDir;
            col += colDir;

            //checking to see if the position is outside the acutal usable size of the board
            if (col <= 0 || col >= boardWidthLen || row <= 0 || row >= boardWidthLen) {
                break;
            }

            ChessPosition newPosition = new ChessPosition(row, col);
            // this is checking to see if there is a piece at the position that it can move to
            ChessPiece piece = board.getPiece(newPosition);

            // if after checking the spot == null we can move on and add that to the list of possible moves
            if (piece == null) {
                moves.add(new ChessMove(startPosition, newPosition, null)); //promotion piece is not implemented right now I dont think
            } else {
                if (piece.getTeamColor() != board.getPiece(startPosition).getTeamColor()) {
                    moves.add(new ChessMove(startPosition, newPosition, null));
                }
                break;
            }
        }
    }
}
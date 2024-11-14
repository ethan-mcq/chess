package chess;
import java.util.ArrayList;
import java.util.Collection;

public class PawnMoves implements PieceMoves {

    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();
        // we need to get the piece so we can know what color it is
        ChessPiece piece = board.getPiece(position);

        // lets just make sure its real
        if (piece == null) {
            return moves;
        }

        int colorDirectionAssignment = piece.getTeamColor() == ChessGame.TeamColor.WHITE ? 1 : -1;

        //forward/promotion
        forwardMoves(board, position, moves, colorDirectionAssignment);

        //if capture/promotion
        captureMoves(board, position,moves,colorDirectionAssignment);

        return moves;
    }
    private void forwardMoves(ChessBoard board, ChessPosition position, Collection<ChessMove> moves, int colorDirectionAssignment) {
        int boardWidthLen = 9;
        int row = position.getRow();
        int col = position.getColumn();
        int moveRow = row + colorDirectionAssignment; // we only move rows

        if (moveRow > 0 && moveRow < boardWidthLen && board.getPiece(new ChessPosition(moveRow, col)) == null) {
            promotionMoves(position, new ChessPosition(moveRow, col), moves, colorDirectionAssignment);

            // Is it first move
            int startPosition = (colorDirectionAssignment == 1) ? 2 : 7;
            int secondMoveRow = row + 2 * colorDirectionAssignment;
            if (row == startPosition && board.getPiece(new ChessPosition(moveRow, col)) == null
                    && board.getPiece(new ChessPosition(secondMoveRow, col)) == null) {
                moves.add(new ChessMove(position, new ChessPosition(secondMoveRow, col), null));
            }
        }
    }
    private void captureMoves(ChessBoard board, ChessPosition position, Collection<ChessMove> moves, int colorDirectionAssignment) {
        int boardWidthLen = 9;
        int row = position.getRow();
        int col = position.getColumn();
        int moveRow = row + colorDirectionAssignment;
        int[] captureCols = { col + 1, col -1 }; // we can capture diagonally on either side
        for (int captureCol : captureCols) {
            if (captureCol > 0 && captureCol < boardWidthLen && moveRow > 0 && moveRow < boardWidthLen) {
                ChessPosition newPosition = new ChessPosition(moveRow, captureCol);
                ChessPiece targetPiece = board.getPiece(newPosition);

                if (targetPiece != null && targetPiece.getTeamColor() != board.getPiece(position).getTeamColor()) {
                    promotionMoves(position, newPosition, moves, colorDirectionAssignment);
                }
            }
        }
    }
    private void promotionMoves(ChessPosition position, ChessPosition newPosition, Collection<ChessMove> moves, int colorDirectionAssignment) {
        int promoRow = colorDirectionAssignment == 1 ? 8 : 1; // if its white then it will be in row 8, 1 if black
        if (newPosition.getRow() == promoRow) {
            moves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.QUEEN));
            moves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.KNIGHT));
            moves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.ROOK));
            moves.add(new ChessMove(position, newPosition, ChessPiece.PieceType.BISHOP));
        } else {
            moves.add(new ChessMove(position, newPosition, null));
        }
    }
}
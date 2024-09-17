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
    }
    private void captureMoves(ChessBoard board, ChessPosition position, Collection<ChessMove> moves, int colorDirectionAssignment) {
        int boardWidthLen = 9;
        int row = position.getRow();
        int col = position.getColumn();
        int moveRow = row + colorDirectionAssignment;
        int[] captureCols = { col + 1, col -1 }; // we can capture diagonally on either side
    }
    private void promotionMoves(ChessBoard board, ChessPosition position, Collection<ChessMove> moves, int colorDirectionAssignment) {
        int promoRow = colorDirectionAssignment == 1 ? 8 : 1; // if its white then it will be in row 8, 1 if black
        if (position.getRow() == promoRow) {
            moves.add(new ChessMove(position, null, ChessPiece.PieceType.QUEEN));
            moves.add(new ChessMove(position, null, ChessPiece.PieceType.KNIGHT));
            moves.add(new ChessMove(position, null, ChessPiece.PieceType.ROOK));
            moves.add(new ChessMove(position, null, ChessPiece.PieceType.BISHOP));
        }
    }
}
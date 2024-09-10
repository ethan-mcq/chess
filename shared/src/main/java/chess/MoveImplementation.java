package chess;

import java.util.HashMap;
import java.util.Map;
import java.util.Collection;
import java.util.ArrayList;

public class MoveImplementation {

    private static final Map<ChessPiece.PieceType, PieceMoves> moveStrategies = new HashMap<>();

    static {
        moveStrategies.put(ChessPiece.PieceType.BISHOP, new BishopMoves());
    }

    public static Collection<ChessMove> calculatePieceMoves(ChessPiece piece, ChessBoard board, ChessPosition position) {
        PieceMoves movesStrategy = moveStrategies.get(piece.getPieceType());
        if (movesStrategy != null) {
            return movesStrategy.calculateMoves(board, position);
        }
        return new ArrayList<>();
    }
}
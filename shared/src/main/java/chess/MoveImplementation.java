package chess;

import java.util.HashMap;
import java.util.Map;
import java.util.Collection;
import java.util.ArrayList;

public class MoveImplementation {

    private static final Map<ChessPiece.PieceType, PieceMoves> MOVE_STRATEGIES = new HashMap<>();

    static {
        //Calculate possible Bishop moves if piece is BISHOP
        MOVE_STRATEGIES.put(ChessPiece.PieceType.BISHOP, new BishopMoves());
        MOVE_STRATEGIES.put(ChessPiece.PieceType.KING, new KingMoves());
        MOVE_STRATEGIES.put(ChessPiece.PieceType.KNIGHT, new KnightMoves());
        MOVE_STRATEGIES.put(ChessPiece.PieceType.QUEEN, new QueenMoves());
        MOVE_STRATEGIES.put(ChessPiece.PieceType.ROOK, new RookMoves());
        MOVE_STRATEGIES.put(ChessPiece.PieceType.PAWN, new PawnMoves());

    }

    public static Collection<ChessMove> calculatePieceMoves(ChessPiece piece, ChessBoard board, ChessPosition position) {
        PieceMoves movesStrategy = MOVE_STRATEGIES.get(piece.getPieceType());

        // If the movesStrategy has not been implemented for x piece, then it just returns the current board position.
        if (movesStrategy != null) {
            return movesStrategy.calculateMoves(board, position);
        }
        //Return ArrayList of moveset
        return new ArrayList<>();
    }
}
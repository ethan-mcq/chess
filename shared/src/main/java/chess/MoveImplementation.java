package chess;

import java.util.HashMap;
import java.util.Map;
import java.util.Collection;
import java.util.ArrayList;

public class MoveImplementation {

    private static final Map<ChessPiece.PieceType, PieceMoves> MoveStrategies = new HashMap<>();

    static {
        //Calculate possible Bishop moves if piece is BISHOP
        MoveStrategies.put(ChessPiece.PieceType.BISHOP, new BishopMoves());
        MoveStrategies.put(ChessPiece.PieceType.KING, new KingMoves());
        MoveStrategies.put(ChessPiece.PieceType.KNIGHT, new KnightMoves());
        MoveStrategies.put(ChessPiece.PieceType.QUEEN, new QueenMoves());
        MoveStrategies.put(ChessPiece.PieceType.ROOK, new RookMoves());
        MoveStrategies.put(ChessPiece.PieceType.PAWN, new PawnMoves());

    }

    public static Collection<ChessMove> calculatePieceMoves(ChessPiece piece, ChessBoard board, ChessPosition position) {
        PieceMoves movesStrategy = MoveStrategies.get(piece.getPieceType());

        // If the movesStrategy has not been implemented for x piece, then it just returns the current board position.
        if (movesStrategy != null) {
            return movesStrategy.calculateMoves(board, position);
        }
        //Return ArrayList of moveset
        return new ArrayList<>();
    }
}
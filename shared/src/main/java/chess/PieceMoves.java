package chess;
import java.util.Collection;

public interface PieceMoves {
    //interface with calculateMoves between each piece implementation (BISHOP, KING, etc.)
    Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position);
}
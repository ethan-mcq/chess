package chess;
import java.util.Collection;

public interface PieceMoves {
    Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position);
}
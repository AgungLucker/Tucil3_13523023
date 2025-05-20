package src;

import java.util.Map;

public class MoveablePieces implements Heuristic {
    public int calculateHeuristic(State state) {
        int exitDistance = state.calculateDistanceToExit();
        int blockingMoves = 0;
        for (Map.Entry<Character, Piece> entry : state.getPieces().entrySet()) {
            Piece piece = entry.getValue();
            if (piece.getPieceID() == 'P') continue;
            if (state.isBlockingExit(piece)) {
                if (piece.isHorizontal) {
                    if (piece.canMove(state, 0, 1) || piece.canMove(state, 0, -1)) {
                        blockingMoves++;
                    } else {
                        blockingMoves += 2;
                    }
                } else {
                    if (piece.canMove(state, 1, 0) || piece.canMove(state, -1, 0)) {
                        blockingMoves++;
                    } else {
                        blockingMoves += 2;
                    }
                }
            }
        }
        return exitDistance + blockingMoves; 
    }
}

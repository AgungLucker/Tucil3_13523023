package src;

public class MinBlock implements Heuristic {
    private boolean manhattanDistance;

    public MinBlock(boolean manhattanDistance) {
        this.manhattanDistance = manhattanDistance;
    }

    public int calculateHeuristic(State state) {
        Piece primaryPiece = state.getPieces().get('P');
        int exitDistance = 0;

        if (manhattanDistance) {
            exitDistance = state.calculateDistanceToExit();
        } 

        int blockingPieces = 0;
        if (state.getStateBoard().getExitX()== 0) {
            int row = primaryPiece.getPieceRow();
            for (int col = 1; col < primaryPiece.getPieceCol(); col++) {
                if (col < state.getStateBoard().getCols() && state.getStateBoard().getBoard()[row][col] != '.' 
                && state.getStateBoard().getBoard()[row][col] != 'K') {
                    blockingPieces++;
                }
            }
        } else if (state.getStateBoard().getExitX() == state.getStateBoard().getCols()  - 1) {
            int row = primaryPiece.getPieceRow();
            for (int col = state.getStateBoard().getCols() - 2; col >= primaryPiece.getPieceCol() + primaryPiece.getPieceSize(); col--) {
                if (col > 0 && state.getStateBoard().getBoard()[row][col] != '.' 
                && state.getStateBoard().getBoard()[row][col] != 'K') {
                    blockingPieces++;
                }
            }
        } else if (state.getStateBoard().getExitY() == 0) {
            int col = primaryPiece.getPieceCol();
            for (int row = 1; row < primaryPiece.getPieceRow(); row++) {
               if (row < state.getStateBoard().getRows() && state.getStateBoard().getBoard()[row][col] != '.' 
               && state.getStateBoard().getBoard()[row][col] != 'K') {
                   blockingPieces++;
               }
            }
        } else if (state.getStateBoard().getExitY() == state.getStateBoard().getRows() - 1) {
            int col = primaryPiece.getPieceCol();
            for (int row = state.getStateBoard().getRows() - 2; row >= primaryPiece.getPieceRow() + primaryPiece.getPieceSize(); row--) {
               if (row > 0 && state.getStateBoard().getBoard()[row][col] != '.' 
               && state.getStateBoard().getBoard()[row][col] != 'K') {
                   blockingPieces++;
               }
            }
        }
        System.out.println("Exit distance: " + exitDistance + ", Blocking pieces: " + blockingPieces);
        return exitDistance + blockingPieces;
    }
    
}

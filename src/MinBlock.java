package src;

public class MinBlock implements Heuristic {
    private boolean manhattanDistance;

    public MinBlock(boolean manhattanDistance) {
        this.manhattanDistance = manhattanDistance;
    }

    public int calculateHeuristic(State state) {
        Piece primaryPiece = state.getPieces().get('P');
        char[][] board = state.getStateBoard().getBoard();
        int rows = state.getStateBoard().getRows();
        int cols = state.getStateBoard().getCols();
        int exitY = state.getStateBoard().getExitY();
        int exitX = state.getStateBoard().getExitX();
        int exitDistance = 0;

        if (manhattanDistance) {
            exitDistance = state.calculateDistanceToExit();
            System.out.println("Manhattan distance: " + exitDistance);
        } 

        int blockingPieces = 0;
        if (exitX == 0) {
            int row = primaryPiece.getPieceRow();
            for (int col = 1; col < primaryPiece.getPieceCol(); col++) {
                if (col < state.getStateBoard().getCols() && state.getStateBoard().getBoard()[row][col] != '.' 
                && state.getStateBoard().getBoard()[row][col] != 'K') {
                    blockingPieces++;
                }
            }
        } else if (exitX == cols  - 1) {
            int row = primaryPiece.getPieceRow();
            for (int col = cols - 2; col >= primaryPiece.getPieceCol() + primaryPiece.getPieceSize(); col--) {
                if (col > 0 && board[row][col] != '.' 
                && board[row][col] != 'K') {
                    blockingPieces++;
                }
            }
        } else if (exitY == 0) {
            int col = primaryPiece.getPieceCol();
            for (int row = 1; row < primaryPiece.getPieceRow(); row++) {
               if (row < rows && board[row][col] != '.' 
               && board[row][col] != 'K') {
                   blockingPieces++;
               }
            }
        } else if (exitY == rows - 1) {
            int col = primaryPiece.getPieceCol();
            for (int row = rows - 2; row >= primaryPiece.getPieceRow() + primaryPiece.getPieceSize(); row--) {
               if (row > 0 && board[row][col] != '.' 
               && board[row][col] != 'K') {
                   blockingPieces++;
               }
            }
        }
        return exitDistance + blockingPieces;
    }
    
}

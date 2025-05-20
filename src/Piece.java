package src;
import java.util.ArrayList;
import java.util.List;

public class Piece {
    private char pieceID;
    private int row, col;
    private int size;
    public boolean isHorizontal;

    public Piece(char id, int row, int col, int size, boolean isHorizontal) {
        this.pieceID = id;
        this.row = row;
        this.col = col;
        this.size = size;
        this.isHorizontal = isHorizontal;
    }

    public char getPieceID() {
        return pieceID;
    }
    public int getPieceRow() {
        return row;
    }
    public int getPieceCol() {
        return col;
    }
    public int getPieceSize() {
        return size;
    }
    
    public Piece copy() {
        return new Piece(pieceID, row, col, size, isHorizontal);
    }

    public List<int[]> getOccupiedCells() {
        List<int[]> cells = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            if (isHorizontal) {
                cells.add(new int[]{row, col + i});
            } else {
                cells.add(new int[]{row + i, col});
            }
        }
        return cells;
    }

    public Piece move(int moveRow, int moveCol) {
        return new Piece(pieceID, row + moveRow, col + moveCol, size, isHorizontal);
    }

    public boolean canMove(State state, int moveRow, int moveCol) {
        int rows = state.getStateBoard().getRows();
        int cols = state.getStateBoard().getCols(); 
        List<int[]> occupiedCells = getOccupiedCells();
        char[][] board = state.getStateBoard().getBoard();
        
        for (int[] cell : occupiedCells) {
            int newRow = cell[0] + moveRow;
            int newCol = cell[1] + moveCol;

            if (newRow < 0 || newRow >= rows
                || newCol < 0 || newCol >= cols) {
                    return false;
                }

            boolean isOriginalCell = false;
            for (int[] originalCell : occupiedCells) {
                if (newRow == originalCell[0] &&  newCol == originalCell[1]) {
                    isOriginalCell = true;
                    break;
                }
            }
            char destCell = board[newRow][newCol];
            if (!isOriginalCell && destCell != '.' && destCell != 'K') {
                return false;
            }
        }
        return true;
        
    }


}

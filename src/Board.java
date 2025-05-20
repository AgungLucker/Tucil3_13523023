package src;

public class Board {
    private char[][] board;
    private int exitY, exitX;
    private int rows, cols;
    
    public Board(char[][] board, int exitY, int exitX) {
        this.rows = board.length;
        this.cols = board[0].length;
        this.exitY = exitY;
        this.exitX = exitX;
        this.board = new char[board.length][board[0].length];
        for (int i = 0; i < board.length; i++) {
            this.board[i] = board[i].clone();
        }
    }

    public char[][] getBoard() {
        return board;
    }
    public int getExitY() {
        return exitY;
    }
    public int getExitX() {
        return exitX;
    }
    public int getRows() {
        return rows;
    }
    public int getCols() {
        return cols;
    }
    public Board updateBoard(Piece original, Piece moved) {
        char[][] newBoard = new char[this.rows][this.cols];
        for (int i = 0; i < this.rows; i++) {
            newBoard[i] = board[i].clone();
        }
        for (int[] cell : original.getOccupiedCells()) {
            newBoard[cell[0]][cell[1]] = '.';
        }

        for (int[] cell : moved.getOccupiedCells()) {
            newBoard[cell[0]][cell[1]] = moved.getPieceID();
        }

        return new Board(newBoard, exitY, exitX);
    }




}

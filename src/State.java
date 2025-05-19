package src;
import java.util.*;

public class State {
    public char[][] board;
    private Map<Character, Piece> pieces;
    private State parent;
    private List<String> moveLog;
    private int cost;
    private int heuristic;

    public State(char[][] board, Map<Character, Piece> pieces) {
        this.board = board;
        this.pieces = new HashMap<>();
        for (Map.Entry<Character, Piece> entry : pieces.entrySet()) {
            Piece p = entry.getValue();
            this.pieces.put(entry.getKey(), new Piece(p.getPieceID(), p.getPieceRow(), p.getPieceCol(), p.getPieceSize(), p.isHorizontal));
        }
        this.parent = null;
        this.cost = 0;
        this.heuristic = 0;
        this.moveLog = new ArrayList<>();

    }

    public State(char[][] board, Map<Character, Piece> pieces, State parent, int cost, List<String> moveLog) {
        this.board = board;
        this.pieces = new HashMap<>();
        for (Map.Entry<Character, Piece> entry : pieces.entrySet()) {
            Piece p = entry.getValue();
            this.pieces.put(entry.getKey(), new Piece(p.getPieceID(), p.getPieceRow(), p.getPieceCol(), p.getPieceSize(), p.isHorizontal));
        }
        this.parent = parent;
        this.cost = cost;
        this.heuristic = 0;
        this.moveLog = moveLog;
    }

    public State getParent() {
        return parent;
    }
    public void setParent(State newParent) {
        this.parent = newParent;
    }
    public int getCost() {
        return cost;
    }
    public int getHeuristic() {
        return heuristic;
    }
    public int getAstarTotalCost() {
        return heuristic + cost;
    }
    public Map<Character, Piece> getPieces() {
        return pieces;
    }
    public char[][] getBoard() {
        return board;
    }
    public List<String> getMoveLog() {
        return moveLog;
    }
    public String getUniqueStateID() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                sb.append(board[i][j]);
            }
        }
        return sb.toString();
    }

    private char[][] updateBoard(char[][] board, Piece originalPiece, Piece movedPiece) {
        // printDebugBoard(board);
        char[][] newBoard = new char[board.length][board[0].length];
        for (int i = 0; i < board.length; i++) {
            newBoard[i] = board[i].clone();
        }

        // Hapus posisi awal
        for (int[] cell : originalPiece.getOccupiedCells()) {
            newBoard[cell[0]][cell[1]] = '.';
        }
        for (int[] cell : movedPiece.getOccupiedCells()) {
            newBoard[cell[0]][cell[1]] = movedPiece.getPieceID();
        }
        
        return newBoard;
    }

    public List<State> generateSuccessors() {
        List<State> successors = new ArrayList<>();
        for (Piece piece : pieces.values()) {
            int[] PossibleMovePoints = {-1, 1};
            for (int possibleMovePoint : PossibleMovePoints) {
                int step = 1;
                while (true) {
                    int moveRow = 0;
                    int moveCol = 0;
                    if (piece.isHorizontal) {
                        moveCol= possibleMovePoint * step;
                    } else {
                        moveRow = possibleMovePoint * step;
                    }

                    if (!piece.canMove(this, moveRow, moveCol)) {
                        break;
                    }
                    Piece movedPiece = piece.move(moveRow, moveCol);
                    Map<Character, Piece> nextPieces = new HashMap<>(this.pieces);
                    nextPieces.put(piece.getPieceID(), movedPiece);
                    char[][] nextBoard = updateBoard(board, piece, movedPiece);
                    List<String> newMoveLog = new ArrayList<>(this.moveLog);
                    String direction = "";
                    if (possibleMovePoint < 0) {
                        if (piece.isHorizontal) {
                            direction = "kiri";
                        } else {
                            direction = "atas";
                        }
                    } else if (possibleMovePoint > 0) {
                        if (piece.isHorizontal) {
                            direction = "kanan";
                        } else {
                            direction = "bawah";
                        }
                    }
                    newMoveLog.add(piece.getPieceID() + "-" + direction);
                    successors.add(new State(nextBoard, nextPieces, this, this.cost + 1, newMoveLog));
                    step++;
                }
            }
        }
        return successors;
    }

    public boolean isGoal(int exitY, int exitX) {
        // printDebugBoard(board);
        Piece primaryPiece = pieces.get('P');
        if (primaryPiece == null) {
            return false;
        }

        for(int[] cell : primaryPiece.getOccupiedCells()) {
            if (cell[0] == exitY && cell[1] == exitX) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o ) {
            return true;
        }
        if (!(o instanceof State)) {
            return false;
        }
        State other = (State) o;

        if (this.board.length != other.board.length 
            || this.board[0].length != other.board[0].length) {
            return false;
        }

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if (board[i][j] != other.board[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    

    // private void printDebugBoard(char[][] debugBoard) {
    // if (debugBoard == null || debugBoard.length == 0 || debugBoard[0].length == 0) {
        // System.out.println("Papan kosong atau tidak valid.");
        // return;
    // }
    // for (int i = 0; i < debugBoard.length; i++) {
        // for (int j = 0; j < debugBoard[0].length; j++) {
            // System.out.print(debugBoard[i][j]);
        // }
        // System.out.println();
    // }
// }

}

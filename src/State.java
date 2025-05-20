package src;
import java.util.*;

public class State {
    private Board board;
    private Map<Character, Piece> pieces;
    private State parent;
    private List<String> moveLog;
    private int cost;
    private String uniqueID = null;
    
    // CTOR awal
    public State(char[][] board, Map<Character, Piece> pieces, int exitY, int exitX) {
        this.board = new Board(board, exitY, exitX);
        this.pieces = new HashMap<>();
        for (Map.Entry<Character, Piece> entry : pieces.entrySet()) {
            Piece p = entry.getValue();
            this.pieces.put(entry.getKey(), new Piece(p.getPieceID(), p.getPieceRow(), p.getPieceCol(), p.getPieceSize(), p.isHorizontal));
        }
        this.parent = null;
        this.cost = 0;
        this.moveLog = new ArrayList<>();

    }

    // CTOR buat successor state
    public State(Board stateBoard, Map<Character, Piece> pieces, State parent, int cost, List<String> moveLog) {
        this.board = stateBoard;
        this.pieces = new HashMap<>();
        for (Map.Entry<Character, Piece> entry : pieces.entrySet()) {
            Piece p = entry.getValue();
            this.pieces.put(entry.getKey(), new Piece(p.getPieceID(), p.getPieceRow(), p.getPieceCol(), p.getPieceSize(), p.isHorizontal));
        }
        this.parent = parent;
        this.cost = cost;
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
    public Map<Character, Piece> getPieces() {
        return pieces;
    }
    public Board getStateBoard() {
        return board;
    }
    public List<String> getMoveLog() {
        return moveLog;
    }
    public String getUniqueStateID() {
        if (uniqueID == null) { 
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < board.getRows(); i++) {
                for (int j = 0; j < board.getCols(); j++) {
                    sb.append(board.getBoard()[i][j]);
                }
            }
            uniqueID = sb.toString();
        }
        return uniqueID;
    }

    public int calculateDistanceToExit() {
        Piece primaryPiece = pieces.get('P');
        if (board.getExitX() == 0) {
            return primaryPiece.getPieceCol();
        } else if (board.getExitX()  == board.getCols() - 1) {
            return board.getCols() - 1 - (primaryPiece.getPieceCol() + primaryPiece.getPieceSize() - 1);
        } else if (board.getExitY() == 0) {
            return primaryPiece.getPieceRow();
        } else if (board.getExitY() == board.getRows() - 1) {
            return board.getRows() - 1 - (primaryPiece.getPieceRow() + primaryPiece.getPieceSize() - 1); 
        }
        return 0;
    }

    public boolean isBlockingExit(Piece piece) {
        Piece primaryPiece = getPieces().get('P');
        if (board.getExitX() == 0) {
            int row = primaryPiece.getPieceRow();
            for (int col = 1; col < primaryPiece.getPieceCol(); col++) {
                if (board.getBoard()[row][col] == piece.getPieceID()) {
                    return true;
                }
            }
        } else if (board.getExitX() == board.getCols() - 1) {
            int row = primaryPiece.getPieceRow();
            for (int col = board.getCols() - 2; col >= primaryPiece.getPieceCol() + primaryPiece.getPieceSize(); col--) {
                if (board.getBoard()[row][col] == piece.getPieceID()) {
                    return true;
                }
            }
        } else if (board.getExitY() == 0) {
            int col = primaryPiece.getPieceCol();
            for (int row = 1; row < primaryPiece.getPieceRow(); row++) {
                if (board.getBoard()[row][col] == piece.getPieceID()) {
                    return true;
                }
            }
        } else if (board.getExitY() == board.getRows() - 1) {
            int col = primaryPiece.getPieceCol();
            for (int row = board.getRows() - 2; row >= primaryPiece.getPieceRow() +  + primaryPiece.getPieceSize(); row--) {
                if (board.getBoard()[row][col] == piece.getPieceID()) {
                    return true;
                }
            }
        }
        return false;
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
                    Board nextStateBoard = board.updateBoard(piece, movedPiece);
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
                    successors.add(new State(nextStateBoard, nextPieces, this, this.cost + 1, newMoveLog));
                    step++;
                }
            }
        }
        return successors;
    }

    public boolean isGoal(int exitY, int exitX) {
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


}

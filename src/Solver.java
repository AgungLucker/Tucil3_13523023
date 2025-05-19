package src;
import java.io.*;
import java.util.*;
import java.util.function.Consumer;

public class Solver {
    private int boardWidth, boardHeight;
    private int superBoardWidth, superBoardHeight;
    private int commonPieceCount;
    private char[][] initialBoard;
    private Map<Character, Piece> piecesMap;
    private int exitY, exitX;
    private List<State> solutionPath;

    public int getExitY() {
        return exitY;
    }
    public int getExitX() {
        return exitX;
    }
    // public char[][] getEmptyBoard() {
        // char[][] emptyBoard = new char[8][8];
        // for (int i = 0; i < 8; i++) {
            // for (int j = 0; j < 8; j++) {
                // if (i == 0 || i == 7 || j == 0 || j == 7) {
                    // emptyBoard[i][j] = '#';
                // } else {
                    // emptyBoard[i][j] = '.';
                // }
            // } 
        // }
        // return emptyBoard;
    // }
    public char[][] getInitialBoard() {
        return initialBoard;
    }

    public List<State> getSolutionPath() {
        return solutionPath;
    }

    private void copyRowtoInitialBoard(String row, int targetRow, boolean isExitLeft) throws IOException {
        if (row.length() > boardWidth + 1) {
            throw new IOException("Konfigurasi papan (panjang baris) tidak valid.");
        }
        for (int j = 0; j <= this.boardWidth; j++) {
            if (j == this.boardWidth && !isExitLeft) {
                continue;
            }
            char ch = row.charAt(j);
            // System.out.println("i: " + targetRow + ", j: " + j);
            // System.out.println("ch: " + ch);
            // System.out.println();
            if (ch != 'K' && ch != ' ') {
                // System.out.println("ADD");
                if (isExitLeft) {
                    initialBoard[targetRow][j] = ch;
                    // System.out.println("LEFT");
                } else {
                    initialBoard[targetRow][j+1] = ch;
                }
                // printBoard(initialBoard);
            } else if (ch == 'K' && j > 0 && j < boardWidth) {
                throw new IOException("Konfigurasi papan (terdapat pintu keluar dalam papan) tidak valid.");
            }
        }
    }
    public void setupSolver(String filepath) throws IOException {
        readInputFile(filepath);
        parsePieces();
        if (checkBoardUnsolvable()) {
            throw new IOException("Papan tidak dapat diselesaikan.");
        }

    }
    private void readInputFile(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String[] sizeLine = reader.readLine().split(" ");
            try {
                this.boardHeight = Integer.parseInt(sizeLine[0]);
                this.superBoardHeight = this.boardHeight + 2;
                this.boardWidth = Integer.parseInt(sizeLine[1]);
                this.superBoardWidth = this.boardWidth + 2;
                this.initialBoard = new char[this.superBoardHeight][this.superBoardWidth];
                if (this.boardWidth <= 0 || this.boardHeight <= 0) {
                    throw new IOException("Dimensi papan (width dan height) harus positif.");
                }
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                throw new IOException("Dimensi papan tidak valid.");
            }
            
            try {
                this.commonPieceCount = Integer.parseInt(reader.readLine());
                if (this.commonPieceCount < 0) {
                    throw new IOException("Jumlah non-primary pieces tidak boleh 0 atau negatif.");
                } 
            } catch (NumberFormatException e) {
                throw new IOException("Format jumlah non-primary pieces tidak valid.");
            }
            
            for (int i = 0; i < this.superBoardHeight; i++) {
                for (int j = 0; j < this.superBoardWidth; j++) {
                    if (i == 0 || i == this.superBoardHeight - 1 || j == 0 || j == this.superBoardWidth - 1) {
                        this.initialBoard[i][j] = '#';
                    } else {
                        this.initialBoard[i][j] = '.';
                    }
                } 
            }
            List<String> boardLines = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isBlank()) {
                    boardLines.add(line);
                }
            }

            if (boardLines.size() < this.boardHeight) {
                throw new IOException("Konfigurasi papan (kolom) tidak valid");
            }

            if (boardLines.get(0).contains("K")) { // pintu keluar di atas
                String topString = boardLines.get(0);
                for (int j = 0; j < topString.length(); j++) {
                    System.out.println("j: " + j);
                    if (topString.charAt(j) != 'K' && topString.charAt(j) != ' ') {
                        throw new IOException("Konfigurasi papan (baris tidak sesuai) tidak valid.");
                    }
                    if (topString.charAt(j) == 'K') {
                        initialBoard[0][j+1] = 'K';
                        exitY = 0;
                        exitX = j+1;
                    }
                    printBoard(initialBoard);
                }
                for (int i = 0; i < boardHeight; i++) {
                    copyRowtoInitialBoard(boardLines.get(i+1), i+1, false);
                }
            } else if (boardLines.size() == this.boardHeight + 1 && 
                boardLines.get(boardLines.size() - 1).contains("K")) { // pintu keluar di bawah
                    String bottomString = boardLines.get(boardLines.size() - 1);
                    for (int j = 0; j < bottomString.length(); j++) {
                        if (bottomString.charAt(j) != 'K' && bottomString.charAt(j) != ' ') {
                            throw new IOException("Konfigurasi papan (baris bawah tidak sesuai) tidak valid.");
                        }
                        if (bottomString.charAt(j) == 'K') {
                            initialBoard[superBoardHeight - 1][j + 1] = 'K';
                            exitY = superBoardHeight - 1;
                            exitX = j + 1;
                        }
                    }

                    for (int i = 0; i < boardHeight; i++) {
                        copyRowtoInitialBoard(boardLines.get(i), i + 1, false);
                    }

            } else {
                boolean isExitLeft = false;
                // printBoard(initialBoard);

                for (int i = 0; i < this.boardHeight; i++) {
                    String row = boardLines.get(i);
                    System.out.println("row: " + row);
                    if (row.length() > 0 && row.charAt(0) == 'K') { // pintu keluar di kiri
                        System.out.println("pintu keluar di kiri");
                        initialBoard[i+1][0] = 'K';
                        isExitLeft = true;
                        // printBoard(initialBoard);
                        // System.out.println();
                        exitY = i + 1;
                        exitX = 0;
                    } else if (row.length() == boardWidth + 1 && row.charAt(boardWidth) == 'K') { // pintu keluar di kanan
                        initialBoard[i + 1][superBoardWidth - 1] = 'K';
                        exitY = i + 1;
                        exitX = superBoardWidth - 1;
                    } 
                }

                for (int i = 0; i < this.boardHeight; i++) {
                    if (i + 1 != exitY && boardLines.get(i).length() > boardWidth && !boardLines.get(i).contains(" ")) {
                        System.out.println("i: " + i + ", exitY: " + exitY);
                        System.out.println("length: " + boardLines.get(i).length() + ", boardWidth: " + boardWidth);
                        throw new IOException("Konfigurasi papan (panjang baris berlebih) tidak valid.");
                    }
                    copyRowtoInitialBoard(boardLines.get(i), i + 1, isExitLeft);
                }
            }
        }
    }

    private void parsePieces() {
        piecesMap = new HashMap<>();
        boolean[][] visited = new boolean[superBoardHeight][superBoardWidth];
        for (int i = 0; i < superBoardHeight; i++) {
            for (int j = 0; j < superBoardWidth; j++) {
                char ch = initialBoard[i][j];
                if (ch != '.' && ch != '#' && ch != 'K' && !visited[i][j]) {
                    int size = 0;
                    
                    // cek orientasi horizontal
                    if (j + 1 < superBoardWidth && initialBoard[i][j + 1] == ch) {
                        int k = j;
                        while (k < superBoardWidth && initialBoard[i][k] == ch) {
                            visited[i][k] = true;
                            size++;
                            k++;
                        }
                        if (size < 2 || size > 3) {
                            throw new IllegalArgumentException("Panjang piece tidak valid.");
                        }
                        piecesMap.put(ch, new Piece(ch, i, j, size, true)); //cell piece paling kiri
                    }
                    // cek orientasi vertikal
                    else if (i + 1 < superBoardHeight && initialBoard[i + 1][j] == ch) {
                        int k = i;
                        while (k < superBoardHeight && initialBoard[k][j] == ch) {
                            visited[k][j] = true;
                            size++;
                            k++;
                        }
                        if (size < 2 || size > 3) {
                            throw new IllegalArgumentException("Panjang piece tidak valid.");
                        }
                        piecesMap.put(ch, new Piece(ch, i, j, size,  false)); //cell piece paling atas
                    }
                }
            }
        }
    }

    private boolean checkBoardUnsolvable() {
        Piece primaryPiece = piecesMap.get('P');
        if (primaryPiece == null) {
            return true; // ga ada P
        }

        int pRow = primaryPiece.getPieceRow();
        int pCol = primaryPiece.getPieceCol(); 
        boolean isHorizontal = primaryPiece.isHorizontal;
        int pSize = primaryPiece.getPieceSize();

        // cek piece yang menghalangi memiliki orientasi sama dengan P
        if (exitX == 0) { 
            if (isHorizontal && pRow == exitY) {
                for (int col = pCol - 1; col >= 1; col--) {
                    char blockingPieceID = initialBoard[pRow][col];
                    if (blockingPieceID != '.' && blockingPieceID != 'K') {
                        Piece blockingPiece = piecesMap.get(blockingPieceID);
                        if (blockingPiece != null && blockingPiece.isHorizontal) {
                            return true;
                        }
                    } 
                }
            } else {
                return true;
            }
        } else if (exitX == superBoardWidth - 1) {
            if (isHorizontal && pRow == exitY) {
                for (int col = pCol + pSize; col < superBoardWidth - 1; col++) {
                    char blockingPieceID = initialBoard[pRow][col];
                    if (blockingPieceID != '.' && blockingPieceID != 'K') {
                        Piece blockingPiece = piecesMap.get(blockingPieceID);
                        if (blockingPiece != null && blockingPiece.isHorizontal) {
                            return true;
                        }
                    }
                }
            } else {
                return true;
            }
        } else if (exitY == 0) {
            if (!isHorizontal && pCol == exitX) {
                for (int row = pRow - 1; row >= 1; row--) {
                    char blockingPieceID = initialBoard[row][pCol];
                    if (blockingPieceID != '.' && blockingPieceID != 'K') {
                        Piece blockingPiece = piecesMap.get(blockingPieceID);
                        if (blockingPiece != null && !blockingPiece.isHorizontal) {
                            return true;
                        }
                    }
                }
            } else {
                return true;
            }
        } else if(exitY == superBoardHeight - 1) {
            if (!isHorizontal && pCol == exitX) {
                for (int row = pRow + pSize; row < superBoardHeight - 1; row++) {
                    char blockingPieceID = initialBoard[row][pCol];
                    if (blockingPieceID != '.' && blockingPieceID != 'K') {
                        Piece blockingPiece = piecesMap.get(blockingPieceID);
                        if (blockingPiece != null && !blockingPiece.isHorizontal) {
                            return true;
                        }
                    }
                }
            } else {
                return true;
            }
        }

        return false; 
        
    }

    public State getInitialState() {
        char[][] copiedBoard = new char[superBoardHeight][superBoardWidth];
        for (int i = 0; i < superBoardHeight; i++) {
            for (int j = 0; j < superBoardWidth; j++) {
                copiedBoard[i][j] = initialBoard[i][j];
            }
        }
        Map<Character, Piece> copiedMap = new HashMap<>();
        for (Map.Entry<Character, Piece> entry : piecesMap.entrySet()) {
            copiedMap.put(entry.getKey(), entry.getValue().copy());
        }
        return new State(copiedBoard, copiedMap);
    }

    public int calculateHeuristic(State state, int heuristicType) {
        switch (heuristicType) {
            case 1:
                return sumBlockingPieceHeuristic(state);
            case 2:
                return possibleMoveBlockingHeuristic(state);
            default:
                throw new IllegalArgumentException("Tipe heuristik tidak valid.");
        }
    }
    private int calculateDistanceToExit(Piece primaryPiece) {
        if (exitX == 0) {
            return primaryPiece.getPieceCol();
        } else if (exitX == superBoardWidth - 1) {
            return superBoardWidth - 1 - (primaryPiece.getPieceCol() + primaryPiece.getPieceSize() - 1);
        } else if (exitY == 0) {
            return primaryPiece.getPieceRow();
        } else if (exitY == superBoardHeight - 1) {
            return superBoardHeight - 1 - (primaryPiece.getPieceRow() + primaryPiece.getPieceSize() - 1); 
        }
        return 0;
    }
    private int sumBlockingPieceHeuristic(State state) { // jarak Manhattan + jumlah cell yang blocking
        Piece primaryPiece = state.getPieces().get('P');
        int exitDistance = calculateDistanceToExit(primaryPiece);

        int blockingPieces = 0;
        if (exitX == 0) {
            int row = primaryPiece.getPieceRow();
            for (int col = 1; col < primaryPiece.getPieceCol(); col++) {
                if (col < superBoardWidth && state.getBoard()[row][col] != '.' && state.getBoard()[row][col] != 'K') {
                    blockingPieces++;
                }
            }
        } else if (exitX == superBoardWidth - 1) {
            int row = primaryPiece.getPieceRow();
            for (int col = superBoardWidth - 2; col > primaryPiece.getPieceCol(); col--) {
                if (col > 0 && state.getBoard()[row][col] != '.' && state.getBoard()[row][col] != 'K') {
                    blockingPieces++;
                }
            }
        } else if (exitY == 0) {
            int col = primaryPiece.getPieceCol();
            for (int row = 1; row < primaryPiece.getPieceRow(); row++) {
               if (row < superBoardHeight && state.getBoard()[row][col] != '.' && state.getBoard()[row][col] != 'K') {
                   blockingPieces++;
               }
            }
        } else if (exitY == superBoardWidth - 1) {
            int col = primaryPiece.getPieceCol();
            for (int row = superBoardHeight - 2; row > primaryPiece.getPieceRow(); row--) {
               if (row > 0 && state.getBoard()[row][col] != '.' && state.getBoard()[row][col] != 'K') {
                   blockingPieces++;
               }
            }
        }

        return exitDistance + blockingPieces;
    }

    private boolean isBlockingExit(State state, Piece piece) {
        Piece primaryPiece = state.getPieces().get('P');
        if (exitX == 0) {
            int row = primaryPiece.getPieceRow();
            for (int col = 1; col < primaryPiece.getPieceCol(); col++) {
                if (state.getBoard()[row][col] == piece.getPieceID()) {
                    return true;
                }
            }
        } else if (exitX == superBoardWidth - 1) {
            int row = primaryPiece.getPieceRow();
            for (int col = superBoardWidth - 2; col > primaryPiece.getPieceCol(); col--) {
                if (state.getBoard()[row][col] == piece.getPieceID()) {
                    return true;
                }
            }
        } else if (exitY == 0) {
            int col = primaryPiece.getPieceCol();
            for (int row = 1; row < primaryPiece.getPieceRow(); row++) {
                if (state.getBoard()[row][col] == piece.getPieceID()) {
                     return true;
                }
            }
        } else if (exitY == superBoardWidth - 1) {
            int col = primaryPiece.getPieceCol();
            for (int row = superBoardHeight - 2; row > primaryPiece.getPieceRow(); row--) {
                if (state.getBoard()[row][col] == piece.getPieceID()) {
                    return true;
                }
            }
        }

        return false;
    }

    private int possibleMoveBlockingHeuristic(State state) { 
        Piece primaryPiece = state.getPieces().get('P');
        int exitDistance = calculateDistanceToExit(primaryPiece);
        int blockingMoves = 0;
        for (Map.Entry<Character, Piece> entry : state.getPieces().entrySet()) {
            Piece piece = entry.getValue();
            if (piece.getPieceID() == 'P') continue;
            if (isBlockingExit(state, piece)) {
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
   
    public boolean solveWithGreedyBFS(int heuristicType) {
        return solveHelper(Comparator.comparingInt(s -> calculateHeuristic(s, heuristicType)));
    }

    public boolean solveWithUCS() {
        return solveHelper(Comparator.comparingInt(s -> s.getCost()));
    }

    public boolean solveWithAStar(int heuristicType) {
        return solveHelper(Comparator.comparingInt(s -> s.getCost() + calculateHeuristic(s, heuristicType)));
    }

    private boolean solveHelper(Comparator<State> comparator) {
        State initialState = getInitialState();
        PriorityQueue<State> frontier = new PriorityQueue<>(comparator);
        Set<String> visited = new HashSet<>();

        frontier.add(initialState);
        visited.add(initialState.getUniqueStateID());

        State goalState = null;
        this.solutionPath = null;

        while (!frontier.isEmpty()) {
            State currentState = frontier.poll();

            if (currentState.isGoal(exitY, exitX)) {
                // printBoard(currentState.getBoard());
                // System.out.println();
                // System.out.println("HALA MADRID");
                // System.out.println();
                goalState = currentState;
                break;
            }


            List<State> successors = currentState.generateSuccessors();
            for (State nextState : successors) {
                if (!visited.contains((nextState.getUniqueStateID()))) {
                    visited.add(nextState.getUniqueStateID());
                    nextState.setParent(currentState);
                    frontier.add(nextState);
                    // printBoard(nextState.getBoard());
                    // System.out.println();
                    // System.out.println("yes");
                }
            }

        }
        if (goalState != null) {
            this.solutionPath = reconstructPath(goalState);
            return true;
        } else {
            return false;
        }

    }
    
    private List<State> reconstructPath(State goalState) {
        System.out.println();
        LinkedList<State> path = new LinkedList<>();
        State currentState = goalState;
        while (currentState != null) {
            // printBoard(currentState.getBoard());
            // System.out.println();
            path.addFirst((currentState));
            currentState = currentState.getParent();
        }
        return path;
    }

    public void printSolution() {
        for (int i = 1; i < this.solutionPath.size(); i++) {
            State currentState = this.solutionPath.get(i);
            if (!currentState.getMoveLog().isEmpty()) {
                String lastMove = currentState.getMoveLog().get(currentState.getMoveLog().size() - 1);
                System.out.println("Gerakan " + i + ": " + lastMove);
            } else {
                System.out.println("Gerakan " + i + ": Tidak ada gerakan yang dilakukan.");
            }
            printBoard(currentState.getBoard());
            System.out.println();
        }
        System.out.println("Jumlah Gerakan: " + (this.solutionPath.size() - 1));
    }

    public void printBoard(char[][] board) {
        for (int i = 0; i < superBoardHeight; i++) {
            for (int j = 0; j < superBoardWidth; j++) {
                if (board[i][j] == '#') {
                    System.out.print("#");
                } else {
                    System.out.print(board[i][j]);
                }
            }
            System.out.println();
        }
    }

    public void animateSolution(Consumer<char[][]> drawCallback) {
    if (solutionPath == null || solutionPath.isEmpty()) return;
    
    for (State state : solutionPath) {
        char[][] board = state.getBoard();
        drawCallback.accept(board);
        try {
            Thread.sleep(500); // jeda 500 ms antara tiap frame animasi
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            break;
        }
    }
}

}

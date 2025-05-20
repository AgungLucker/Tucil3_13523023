package src;

import java.io.*;
import java.util.*;

public class InputOutput {
    private int boardWidth, boardHeight;
    private int superBoardWidth, superBoardHeight;
    private int commonPieceCount;
    private char[][] initialBoard;
    private State initialState;
    private int exitY, exitX;

    public char[][] getInitialBoard() {
        return initialBoard;
    }
    public State getInitialState() {
        return initialState;
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
        this.initialState = parsePieces();

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
                    // printBoard(initialBoard);
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
                    if ((exitX == superBoardWidth -1 && i + 1 != exitY && boardLines.get(i).length() > boardWidth && !boardLines.get(i).contains("K")) 
                    || (exitX == 0 && i + 1 != exitY && boardLines.get(i).length() > boardWidth && !boardLines.get(i).contains("K") && !boardLines.get(i).contains(" "))) {
                        System.out.println("i: " + i + ", exitY: " + exitY);
                        System.out.println("length: " + boardLines.get(i).length() + ", boardWidth: " + boardWidth);
                        throw new IOException("Konfigurasi papan (panjang baris berlebih) tidak valid.");
                    }
                    System.out.println("i: " + i + ", width: " + superBoardWidth + ", exitY: " + exitY);
                    System.out.println("boardLines.get(i): " + boardLines.get(i));
                    System.out.println("boardLines.get(i).length(): " + boardLines.get(i).length());
                    copyRowtoInitialBoard(boardLines.get(i), i + 1, isExitLeft);
                }
            }
        }
    }

    private State parsePieces() {
        Map<Character, Piece> piecesMap = new HashMap<>();
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
        return new State(initialBoard, piecesMap, exitY, exitX);

    }

    public void saveSolutionToFile(String filepath, List<State> solutionPath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filepath))) {
            for (int i = 1; i < solutionPath.size(); i++) {
                State currentState = solutionPath.get(i);
                if (!currentState.getMoveLog().isEmpty()) {
                    String lastMove = currentState.getMoveLog().get(currentState.getMoveLog().size() - 1);
                    writer.write("Gerakan " + i + ": " + lastMove + "\n");
                } else {
                    writer.write("Gerakan " + i + ": Tidak ada gerakan yang dilakukan.\n");
                }

                // Tulis papan
                char[][] board = currentState.getStateBoard().getBoard();
                for (int r = 0; r < board.length; r++) {
                    for (int c = 0; c < board[r].length; c++) {
                        if (board[r][c] == '#') {
                            writer.write(" ");
                        } else {
                            writer.write(board[r][c]);
                        }
                    }
                    writer.write("\n");
                }
                writer.write("\n");
            }
            writer.write("Jumlah Gerakan: " + (solutionPath.size() - 1) + "\n");
        }
    }
}

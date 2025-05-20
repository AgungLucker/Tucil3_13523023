package src;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Main extends JFrame {
    private InputOutput inputOutput;
    private Solver solver;
    private JPanel boardPanel;
    private JComboBox<String> algorithmDropdown;
    private JComboBox<String> heuristicDropdown;
    private JLabel statusLabel;
    private JLabel statsLabel;
    private JButton solveButton;
    private JButton replayButton;
    private JButton saveButton;
    private Map<Character, Color> colorMap = new HashMap<>();
    private File lastDirectory = null;
    private String algo;
    private int heuristicType;
    private Thread animationThread = null;
    private long duration;


    public Main() {
        super("Rush Hour Game Solver");
        initializeColorMap();
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());

        // Top Bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(173, 216, 230)); // biru muda
        
        JLabel topLabel = new JLabel("<html><span style='color:#B22222;'>RUSH</span> <span style='color:orange;'>HOUR</span> GAME SOLVER</html>", SwingConstants.CENTER);
        topLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        topBar.add(topLabel, BorderLayout.CENTER);
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        this.add(topBar, BorderLayout.NORTH);
        // Sidebar
        JPanel sideBar = new JPanel();
        sideBar.setLayout(new BoxLayout(sideBar, BoxLayout.Y_AXIS));

        JButton loadButton = new JButton("Load File");
        loadButton.setMaximumSize(new Dimension(130, 35));
        loadButton.setBackground(new Color(70, 130, 180));
        loadButton.setForeground(Color.WHITE);
        loadButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        algorithmDropdown = new JComboBox<>(new String[]{"GBFS", "UCS", "A STAR", "IDS"});
        algorithmDropdown.setBackground(Color.WHITE);
        algorithmDropdown.setMaximumSize(new Dimension(180, 30));
        algorithmDropdown.setAlignmentX(Component.CENTER_ALIGNMENT);
        String[] heuristics = {
            "<html>1. Manhattan Distance</html>",
            "<html>2. Min Blocking Pieces</html>",
            "<html>3. Manhattan Distance<br>+ Min Blocking Pieces</html>",
            "<html>4. Min Moveable Blockers</html>"
        };

        heuristicDropdown = new JComboBox<>(heuristics);
        heuristicDropdown.setBackground(Color.WHITE);
        heuristicDropdown.setMaximumSize(new Dimension(180, 30));
        heuristicDropdown.setAlignmentX(Component.CENTER_ALIGNMENT);

        solveButton = new JButton("Solve");
        solveButton.setMaximumSize(new Dimension(140, 35));
        solveButton.setBackground(Color.DARK_GRAY);
        solveButton.setForeground(Color.WHITE);
        solveButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        solveButton.setEnabled(false);

        replayButton = new JButton("Replay Animation");
        replayButton.setMaximumSize(new Dimension(140, 35));
        replayButton.setBackground(Color.yellow);
        replayButton.setForeground(Color.DARK_GRAY);

        replayButton.setEnabled(false);
        replayButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        saveButton = new JButton("Save");
        saveButton.setMaximumSize(new Dimension(160, 35));
        saveButton.setBackground(Color.yellow);
        saveButton.setForeground(Color.DARK_GRAY);
        saveButton.setEnabled(false);
        saveButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            
        JLabel algorithmLabel = new JLabel("Algorithm");
        algorithmLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel heuristicLabel = new JLabel("Heuristic");
        heuristicLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel speedLabel = new JLabel("Animation Speed (ms)");
        speedLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JSlider speedSlider = new JSlider(JSlider.HORIZONTAL, 50, 1000, 500);
        speedSlider.setMajorTickSpacing(250);
        speedSlider.setMinorTickSpacing(50);
        speedSlider.setPaintTicks(true);
        speedSlider.setPaintLabels(true);


        sideBar.add(loadButton);
        sideBar.add(Box.createRigidArea(new Dimension(0, 30)));
        sideBar.add(algorithmLabel);
        sideBar.add(Box.createRigidArea(new Dimension(0, 4)));
        sideBar.add(algorithmDropdown);
        sideBar.add(Box.createRigidArea(new Dimension(0, 10)));
        sideBar.add(heuristicLabel);
        sideBar.add(Box.createRigidArea(new Dimension(0, 4)));
        sideBar.add(heuristicDropdown);
        sideBar.add(Box.createRigidArea(new Dimension(0, 20)));
        sideBar.add(solveButton);
        sideBar.add(Box.createRigidArea(new Dimension(0, 10)));
        sideBar.add(replayButton);
        sideBar.add(Box.createRigidArea(new Dimension(0, 30)));
        sideBar.add(speedLabel);
        sideBar.add(Box.createRigidArea(new Dimension(0, 15)));
        sideBar.add(speedSlider);
        sideBar.add(Box.createRigidArea(new Dimension(0, 20)));
        sideBar.add(saveButton);
        sideBar.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));

        
        this.add(sideBar, BorderLayout.WEST);

        // Board Panel
        boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(6, 6)); 
        this.add(boardPanel, BorderLayout.CENTER);

        // papan kosong awal
        char[][] emptyBoard = new char[8][8];

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (i == 0 || i == 7 || j == 0 || j == 7) {
                    emptyBoard[i][j] = '#';
                } else {
                    emptyBoard[i][j] = '.';
                }
                
            }
        }

        drawBoard(emptyBoard);

        // Bottom Bar
        JPanel bottomBar = new JPanel(new BorderLayout());
        bottomBar.setPreferredSize(new Dimension(0, 40));
        bottomBar.setBackground(Color.red); 
        statusLabel = new JLabel("Silakan load file konfigurasi.");
        statsLabel = new JLabel("Jumlah Gerakan: 0 | Waktu: 0ms");

        statusLabel.setVerticalAlignment(SwingConstants.CENTER);
        statsLabel.setVerticalAlignment(SwingConstants.CENTER);
        bottomBar.add(statusLabel, BorderLayout.WEST);
        bottomBar.add(statsLabel, BorderLayout.EAST);
        bottomBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        this.add(bottomBar, BorderLayout.SOUTH);

        // Load Button Action
        loadButton.addActionListener((ActionEvent e) -> {
            JFileChooser fileChooser = new JFileChooser();

            if (lastDirectory != null) {
                fileChooser.setCurrentDirectory(lastDirectory);
            }
        
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    this.algo = "";
                    this.heuristicType = 0;
                    inputOutput = new InputOutput();
                    inputOutput.setupSolver(fileChooser.getSelectedFile().getAbsolutePath());
                    // solver = new Solver();
                    // solver.setupSolver(fileChooser.getSelectedFile().getAbsolutePath());
                    statusLabel.setText("Loaded: " + fileChooser.getSelectedFile().getName());
                    drawBoard(inputOutput.getInitialBoard());
                    solveButton.setEnabled(true);
                
                    lastDirectory = fileChooser.getSelectedFile().getParentFile();
                    solver = new Solver();
                
                } catch (IOException | IllegalArgumentException ex) {
                    statusLabel.setText("Error: " + ex.getMessage());
                    boardPanel.removeAll();
                    boardPanel.repaint();
                    drawBoard(emptyBoard);
                    solveButton.setEnabled(false);
                    lastDirectory = fileChooser.getSelectedFile().getParentFile();
                }
            }
        });


        // Solve Button Action
        solveButton.addActionListener((ActionEvent e) -> {
            if (inputOutput == null) {
                statusLabel.setText("File belum di-load.");
                return;
            }
            this.algo = "";
            this.heuristicType = 0;
            solveButton.setEnabled(false);
            replayButton.setEnabled(false);
            statusLabel.setText("Solving...");
        
            SwingUtilities.invokeLater(() -> {
                this.algo = (String) algorithmDropdown.getSelectedItem();
                this.heuristicType = heuristicDropdown.getSelectedIndex() + 1;
                long startTime = System.currentTimeMillis();
            
                try {
                    boolean solved = switch (algo) {
                        case "GBFS" -> solver.solveWithGreedyBFS(heuristicType, inputOutput.getInitialState());
                        case "UCS" -> solver.solveWithUCS(inputOutput.getInitialState());
                        case "A STAR" -> solver.solveWithAStar(heuristicType, inputOutput.getInitialState());
                        case "IDS" -> solver.solveWithIDS(inputOutput.getInitialState());
                        default -> false;
                    };
                
                    long endTime = System.currentTimeMillis();
                
                    if (solved) {
                        statusLabel.setText("Solusi Ditemukan!");
                        this.duration = endTime - startTime;
                        statsLabel.setText("Jumlah Gerakan: " + (solver.getSolutionPath().size()-1) + " | Waktu: " + (endTime - startTime) + "ms");
                        if (solver != null && solver.getSolutionPath() != null) {
                            if (animationThread != null && animationThread.isAlive()) {
                                animationThread.interrupt();
                                try {
                                    animationThread.join(); // Tunggu sampai thread selesai berhenti
                                } catch (InterruptedException ex) {
                                    ex.printStackTrace();
                                }
                            }
                            animationThread = new Thread(() -> solver.animateSolution(
                                this::drawBoard, 
                                () -> speedSlider.getValue()));
                            animationThread.start();
                        }
                        solver.printSolution();
                        replayButton.setEnabled(true);

                    } else {
                        statusLabel.setText("Solusi Tidak Ditemukan.");
                        statsLabel.setText("Jumlah Gerakan: 0 | Waktu: " + (endTime - startTime) + "ms");
                    }
                
                } catch (IllegalArgumentException | IllegalStateException ex) {
                    statusLabel.setText(ex.getMessage());
                    ex.printStackTrace();
                } catch (Exception ex) {
                    statusLabel.setText("Unexpected error: " + ex.getClass().getSimpleName());
                    ex.printStackTrace();
                } finally {
                    solveButton.setEnabled(true);
                    saveButton.setEnabled(true);
                }
            });
        });

        // Replay Animation Button
        replayButton.addActionListener((ActionEvent e) -> {
            solveButton.setEnabled(false);
            if (solver != null && solver.getSolutionPath() != null) {
                 if (animationThread != null && animationThread.isAlive()) {
                    animationThread.interrupt();
                    try {
                        animationThread.join(); 
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
                animationThread = new Thread(() -> solver.animateSolution(
                    this::drawBoard, 
                    () -> speedSlider.getValue()));
                animationThread.start();    
            }
            solveButton.setEnabled(true);
        });

        saveButton.addActionListener((ActionEvent e) -> {
            if (solver != null) {
                JFileChooser fileChooser = new JFileChooser();
                if (lastDirectory != null) {
                    fileChooser.setCurrentDirectory(lastDirectory);
                }
                fileChooser.setDialogTitle("Save Solution");
            
                if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    try {
                        inputOutput.saveSolutionToFile(selectedFile.getAbsolutePath(), solver.getSolutionPath(), algo, heuristicType, duration);
                        lastDirectory = selectedFile.getParentFile();
                        String folder = selectedFile.getParentFile().getName();  
                        String filename = selectedFile.getName();                
                        statusLabel.setText("Solusi berhasil disimpan ke: " + folder + "/" + filename);

                    } catch (IOException ex) {
                        statusLabel.setText("Gagal menyimpan solusi: " + ex.getMessage());
                        ex.printStackTrace();
                    }
                }
            } else {
                statusLabel.setText("Belum ada solusi untuk disimpan.");
            }
        });


        this.setSize(800, 600);
        this.setVisible(true);
    }
    private void initializeColorMap() {
        Color[] palette = {
            Color.BLUE, Color.GREEN, Color.CYAN, Color.ORANGE, Color.MAGENTA, Color.YELLOW,
            new Color(102, 0, 153), new Color(0, 153, 153), new Color(255, 153, 51), 
            new Color(153, 0, 0), new Color(204, 0, 204), new Color(0, 102, 204), 
            new Color(255, 102, 102), new Color(153, 153, 0), new Color(51, 51, 255), 
            new Color(0, 153, 0), new Color(255, 204, 0), new Color(204, 0, 0), 
            new Color(0, 204, 204), new Color(102, 102, 255), new Color(255, 153, 153), 
            new Color(102, 204, 0), new Color(0, 102, 102), new Color(255, 102, 0), 
            new Color(153, 204, 255) 
        };

        int index = 0;
        for (char c = 'A'; c <= 'Z'; c++) {
            if (c == 'P') {
                colorMap.put('P', Color.RED); 
            } else {
                colorMap.put(c, palette[index % palette.length]);
                index++;
            }
        }
        colorMap.put('#', Color.DARK_GRAY);
    }


    private void drawBoard(char[][] board) {
        SwingUtilities.invokeLater(() -> {
            boardPanel.removeAll();
            boardPanel.setLayout(new GridLayout(board.length, board[0].length));
            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board[0].length; j++) {
                    char c = board[i][j];
                    JLabel cell = new JLabel("" + c, SwingConstants.CENTER);
                    cell.setOpaque(true);
                    cell.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                    if (c == '.') {
                        cell.setBackground(Color.WHITE);
                    } else {
                        Color bg = colorMap.getOrDefault(c, Color.LIGHT_GRAY);
                        cell.setBackground(bg);
                        cell.setForeground((bg.getRed() + bg.getGreen() + bg.getBlue()) < 400 ? Color.WHITE : Color.BLACK);
                    }
                    boardPanel.add(cell);
                }
            }
            boardPanel.revalidate();
            boardPanel.repaint();
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}
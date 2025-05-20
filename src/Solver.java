package src;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Solver {
    private List<State> solutionPath;

    public List<State> getSolutionPath() {
        return solutionPath;
    }

    public Heuristic setHeuristic(int heuristicType) {
        switch (heuristicType) {
            case 1:
                return new ManhattanDistance();
            case 2:
                return new MinBlock(false);
            case 3:
                return new MinBlock(true);
            case 4:
                return new MoveablePieces();
            default:
                return null;
        }
    }

    public boolean solveWithIDS(State initialState) {
        int depthLimit = 0;
        int MAX_DEPTH = 1000; // kasus unsolvable
        this.solutionPath = null;
        while (depthLimit < MAX_DEPTH) {
            System.out.println("Depth Limit: " + depthLimit);
            Set<String> visited = new HashSet<>();
            
            State resultState = depthLimitedSearch(initialState, depthLimit, visited);
            if (resultState != null) {
                this.solutionPath = reconstructPath(resultState);
                return true;
            }
            depthLimit++;
        }
        return false;
    }
    private State depthLimitedSearch(State currentState, int depthLimit, Set<String> visited) {
        if (currentState.isGoal(currentState.getStateBoard().getExitY(), currentState.getStateBoard().getExitX())) {
            return currentState;
        }
        visited.add(currentState.getUniqueStateID());

        for (State nextState : currentState.generateSuccessors()) {
             if (!visited.contains((nextState.getUniqueStateID()))) {
                nextState.setParent(currentState);
                State resultState = depthLimitedSearch(nextState, depthLimit - 1, visited);
                if (resultState != null) {
                    return resultState;
                }
            }
        }
        return null;
    }

    public boolean solveWithGreedyBFS(int heuristicType, State initialState) {
        Heuristic heuristic = setHeuristic(heuristicType);
        if (heuristic == null) {
            System.out.println("Tipe heuristik tidak valid.");
            return false;
        }
        return solveHelper(Comparator.comparingInt(s -> heuristic.calculateHeuristic(s)), initialState, true);
    }

    public boolean solveWithUCS(State initialState) {
        return solveHelper(Comparator.comparingInt(s -> s.getCost()), initialState, false);
    }

    public boolean solveWithAStar(int heuristicType, State initialState) {
        Heuristic heuristic = setHeuristic(heuristicType);
        if (heuristic == null) {
            System.out.println("Tipe heuristik tidak valid.");
            return false;
        }
        return solveHelper(Comparator.comparingInt(s -> s.getCost() + heuristic.calculateHeuristic(s)), initialState, false);
    }

    private boolean solveHelper(Comparator<State> comparator, State initialState, boolean isGBFS) {
        PriorityQueue<State> frontier = new PriorityQueue<>(comparator);
        Set<String> visited = new HashSet<>();

        frontier.add(initialState);
        if (isGBFS) {
            visited.add(initialState.getUniqueStateID());
        }

        State goalState = null;
        this.solutionPath = null;
        while (!frontier.isEmpty()) {
            State currentState = frontier.poll();
            if (!isGBFS) {
                visited.add(currentState.getUniqueStateID());
            }



            if (currentState.isGoal(currentState.getStateBoard().getExitY(), currentState.getStateBoard().getExitX())) {
                goalState = currentState;
                break;
            }


            List<State> successors = currentState.generateSuccessors();
            for (State nextState : successors) {
                if (!visited.contains((nextState.getUniqueStateID()))) {
                    if (isGBFS) {
                        visited.add(nextState.getUniqueStateID());
                    }
                    nextState.setParent(currentState);
                    frontier.add(nextState);
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
            path.addFirst((currentState));
            currentState = currentState.getParent();
        }
        return path;
    }

    // debugger
    public void printSolution() {
        for (int i = 1; i < this.solutionPath.size(); i++) {
            State currentState = this.solutionPath.get(i);
            if (!currentState.getMoveLog().isEmpty()) {
                String lastMove = currentState.getMoveLog().get(currentState.getMoveLog().size() - 1);
                System.out.println("Gerakan " + i + ": " + lastMove);
            } else {
                System.out.println("Gerakan " + i + ": Tidak ada gerakan yang dilakukan.");
            }
            printBoard(currentState.getStateBoard().getBoard());
            System.out.println();
        }
        System.out.println("Jumlah Gerakan: " + (this.solutionPath.size() - 1));
    }
    // debugger
    public void printBoard(char[][] board) { 
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if (board[i][j] == '#') {
                    System.out.print("#");
                } else {
                    System.out.print(board[i][j]);
                }
            }
            System.out.println();
        }
    }

    public void animateSolution(Consumer<char[][]> drawCallback, Supplier<Integer> delayEffect) {
        if (solutionPath == null || solutionPath.isEmpty()) return;
        
        for (State state : solutionPath) {
            if (Thread.currentThread().isInterrupted()) {
                return; 
            }
            char[][] board = state.getStateBoard().getBoard();
            drawCallback.accept(board);
            try {
                Thread.sleep(delayEffect.get()); 
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

}

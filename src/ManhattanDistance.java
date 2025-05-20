package src;

public class ManhattanDistance implements Heuristic {
    
    public int calculateHeuristic(State state) {
        return state.calculateDistanceToExit();
    }
}

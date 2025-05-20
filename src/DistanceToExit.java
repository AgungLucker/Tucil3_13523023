package src;

public class DistanceToExit implements Heuristic {
    
    public int calculateHeuristic(State state) {
        return state.calculateDistanceToExit();
    }
}

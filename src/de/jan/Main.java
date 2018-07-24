package de.jan;

import static de.jan.SCutil.launchStarcraft;

public class Main {
    protected static final int THREADS = 8;
    protected static final boolean ADD_DELETE = true;
    protected static final boolean SINGLE_POINT = false;
    protected static final boolean RETAIN_BEST = true;

    protected static Process[] starcrafts = new Process[THREADS];

    public static void main(String[] args) {
        launchStarcraft();
        Population pop = new Population("ackCondition\"   : [ [\"Self\", \"Marine", (float) 1, 32, true);
        pop.evaluateParallel();
        pop.findBest();
        pop.logGeneration();
        while (true /*!pop.isFinished()*/) {
            pop.naturalSelection();
            pop.generate();
            pop.evaluateParallel();
            pop.findBest();
            pop.logGeneration();
        }
    }
}
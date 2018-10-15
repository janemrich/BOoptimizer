package de.jan;
import static de.jan.SCutil.killStarcraft;
import static de.jan.SCutil.launchStarcraft;

public class Main {
    protected static int THREADS;

    protected static boolean MULTI;
    protected static boolean ADD_DELETE;
    protected static boolean SINGLE_POINT;
    protected static boolean RETAIN_BEST;
    protected static int POP_SIZE;

    protected static boolean RANDOM;

    protected static boolean CONVERGE;
    protected static boolean CONVERGE_PERCENTAGE;
    protected static double CONVERGE_THRESHOLD;

    protected static String LOG_LOCATION;
    protected static String SC2_LOCATION;
    protected static String BOT_LOCATION;
    protected static Process[] starcrafts;

    public static void main(String[] args) {
        THREADS = Integer.parseInt(args[0]);

        MULTI = Boolean.parseBoolean(args[1]);
        ADD_DELETE = Boolean.parseBoolean(args[2]);
        SINGLE_POINT = Boolean.parseBoolean(args[3]);
        RETAIN_BEST = Boolean.parseBoolean(args[4]);
        POP_SIZE = Integer.parseInt(args[5]);
        RANDOM = Boolean.parseBoolean(args[6]);

        CONVERGE = Boolean.parseBoolean(args[7]);
        CONVERGE_PERCENTAGE = Boolean.parseBoolean(args[8]);
        CONVERGE_THRESHOLD = Double.parseDouble(args[9]);
        int stop = Integer.parseInt(args[10]) / POP_SIZE;

        LOG_LOCATION = args[11];
        SC2_LOCATION = args[12];
        BOT_LOCATION = args[13];

        starcrafts = new Process[THREADS];
        launchStarcraft();
        Population pop = new Population("ackCondition\"   : [ [\"Self\", \"Marine", (float) 1, POP_SIZE, MULTI);
        pop.evaluateParallel();
        pop.findBest();
        pop.logGeneration();
        while (((CONVERGE && !pop.isFinished()) || !CONVERGE) && (pop.getGenerations()+1) < stop) {
            pop.naturalSelection();
            pop.generate();
            pop.evaluateParallel();
            pop.findBest();
            pop.logGeneration();
        }
        killStarcraft();
    }
}
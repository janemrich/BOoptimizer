package de.jan;

public class Main {
    public static void main(String[] args) {
        Population pop = new Population("ackCondition\"   : [ [\"Self\", \"Marine", (float) 0.2, 2);
        while (true /*!pop.isFinished()*/) {
            pop.naturalSelection();
            pop.generate();
            pop.calcFitness();
            pop.evaluate();
            pop.logGeneration();
        }
        //System.out.println(pop.getBest());
    }
}

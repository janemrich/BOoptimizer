package de.jan;

public class Main {
    public static void main(String[] args) {
        Population pop = new Population("hi", (float) 0.01, 100);
        while (!pop.isFinished()) {
            pop.naturalSelection();
            pop.generate();
            pop.calcFitness();
            pop.evaluate();
        }
        System.out.println(pop.getBest());
    }
}

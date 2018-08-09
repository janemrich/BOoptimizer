package de.jan;

import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static de.jan.Main.*;
import static java.util.Arrays.*;

public class Population {

    private Chromosome[] population;
    private ArrayList<Chromosome> matingPool;
    private int generations;
    private String target;
    private boolean finished = false;
    private double mutationRate;
    private double perfectScore = 2500;
    private Chromosome best;
    private boolean multi;

    public Population(String target, double mutationRate, int num, boolean multi) {
        this.target = target;
        this.mutationRate = mutationRate;

        this.population = new Chromosome[num];
        for (int i = 0; i < this.population.length; i++) {
            if (multi) {
                population[i] = new MultiChromosome(this.target.length());
            } else {
                population[i] = new SingleChromosome(this.target.length());
            }
        }
        this.matingPool = new ArrayList<Chromosome>();
        this.multi = multi;
    }

    public void calcFitness() {
        for (int i = 0; i < population.length; i++) {
            population[i].evaluate(this.generations, i, "\"AttackCondition\"   : [ [\"Self\", \"Marine\"], \">=\", [ 10] ]");
        }
    }

    /**
     * findBest fitness in parallel
     */
    public void evaluateParallel() {
        try {
            /*
            ExecutorService[] executers = new ExecutorService[THREADS];
            for (int i = 0; i < executers.length; i++) {
                executers[i] = Executors.newSingleThreadExecutor();
            }
            for (int i = 0; i < population.length; i++) {
                population[i].setParams(generations, i, "\"AttackCondition\"   : [ [\"Self\", \"Marine\"], \">=\", [ 10] ]");
                executers[i % THREADS].submit(population[i]);
                System.out.println(Integer.toString(i) + " submitted\n");
            }
            for (ExecutorService e :
                    executers) {
                e.shutdown();
            }
            for (ExecutorService e :
                    executers) {
                e.awaitTermination(population.length / 2, TimeUnit.MINUTES);
            }*/
            /*
            for (int j = 0; j < ((population.length / THREADS) + 1); j++) {
                ExecutorService executor = Executors.newFixedThreadPool(THREADS);
                int from = j * THREADS;
                int to = Math.min(from + THREADS, population.length);
                for (int i = from; i < to; i++) {
                    population[i].setParams(generations, i, "\"AttackCondition\"   : [ [\"Self\", \"Marine\"], \">=\", [ 10] ]");
                    executor.submit(this.population[i]);
                    System.out.println(Integer.toString(i) + " submitted\n");
                }
                executor.shutdown();
                executor.awaitTermination(2, TimeUnit.MINUTES);
            }
            */
            int k = 0;
            do {
                ExecutorService executor = Executors.newFixedThreadPool(THREADS);
                for (int i = 0; i < population.length; i++) {
                    population[i].setFail(false
                    );
                    population[i].setParams(generations, i, "\"AttackCondition\"   : [ [\"Self\", \"Marine\"], \">=\", [ 10] ]");
                    executor.submit(population[i]);
                    System.out.print(Integer.toString(i) + ", ");
                }
                System.out.println(" submitted");
                executor.shutdown();
                System.out.println("awaitTermination");
                executor.awaitTermination(population.length / 2, TimeUnit.MINUTES);
                k++;
                /*
                int fails = 0;
                for (Chromosome c :
                        population) {
                    if (c.isFail()) fails++;
                }
                if (fails > population.length/5) {
                    System.err.println("\n large evaluation fails");
                    relaunchStarcraft();
                } else {
                //    break;
                }
                */
            } while (stream(population).map(x -> x.isFail()).reduce((b1, b2) -> b1 || b2).get() && k < 4);
            //*/
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void naturalSelection() {
        //Clear mating pool
        this.matingPool.clear();

        double minFitness = Double.MAX_VALUE;
        for (Chromosome dna :
                this.population) {
            Double fitness = dna.getFitness();

            if (dna.getGameSteps() == null) continue; // if evaluation failed

            if (fitness < minFitness) {
                minFitness = fitness;
            }
        }

        // fill mating pool. members with better fitness are likely to be picked often
        for (int i = 0; i < population.length; i++) {
            Chromosome member = population[i];

            if (member.getGameSteps() == null) continue; // if evaluation failed

            double fitness = member.getFitness() / minFitness;
            int n = (int) fitness * 100;
            for (int j = 0; j < n; j++) {
                this.matingPool.add(population[i]);
            }
        }
    }

    // create new generation
    public void generate() {
        if (!RANDOM) {
            Random rn = new Random();
            Object[] arrayOfBest = null;
            if (multi) for (Chromosome c :
                    population) {
                ((MultiChromosome) c).trim();
            }
            if (RETAIN_BEST) {
                arrayOfBest = stream(population)
                        .filter(c -> c.getGameSteps() != null)
                        .sorted((x, y) -> (y.getGameSteps() - x.getGameSteps()))
                        .toArray();
            }
            for (int i = 0; i < this.population.length; i++) {
                if (RETAIN_BEST && i < population.length / 7) {
                    this.population[i] = (Chromosome) arrayOfBest[i];
                } else {
                    int a = rn.nextInt(this.matingPool.size());
                    int b = rn.nextInt(this.matingPool.size());
                    Chromosome partnerA = this.matingPool.get(a);
                    Chromosome partnerB = this.matingPool.get(b);
                    Chromosome[] children = partnerA.crossover(partnerB);
                    Chromosome[] mutatedChildren = new Chromosome[2];
                    for (int j = 0; j < 2; j++) {
                        mutatedChildren[j] = children[j].clone();
                        mutatedChildren[j].mutate(this.mutationRate);
                        while (!mutatedChildren[j].valid()) {
                            mutatedChildren[j] = children[j].clone();
                            mutatedChildren[j].mutate(1);
                        }
                    }
                    this.population[i] = mutatedChildren[0];
                    i++;
                    if (!(i < population.length)) {
                        generations++;
                        return;
                    }
                    population[i] = mutatedChildren[1];
                }
            }
            this.generations++;
        } else {
            this.population = new Chromosome[population.length];
            for (int i = 0; i < this.population.length; i++) {
                if (multi) {
                    population[i] = new MultiChromosome(this.target.length());
                } else {
                    population[i] = new SingleChromosome(this.target.length());
                }
            }
            this.generations++;
        }
    }

    public Chromosome getBest() {
        return best;
    }

    public void findBest() {
        int worldrecord = Integer.MAX_VALUE;
        int index = 0;
        for (int i = 0; i < this.population.length; i++) {
            Integer steps = population[i].getGameSteps();
            if (steps != null && steps < worldrecord) {
                index = i;
                worldrecord = steps;
            }
        }

        this.best = this.population[index];
        if (worldrecord == this.perfectScore) {
            this.finished = true;
        }
    }

    public boolean isFinished() {
        return this.finished;
    }

    public int getGenerations() {
        return generations;
    }

    public double getAverageFitness() {
        double total = 0;
        int i = 0;
        for (Chromosome dna :
                this.population) {
            if (dna.getGameSteps() != null) {
                total += dna.getFitness();
                i++;
            }
        }
        return total / i;
    }

    public int getAverageSteps() {
        int total = 0;
        int i = 0;
        for (Chromosome dna :
                population) {
            Integer steps = dna.getGameSteps();
            if (steps != null) {
                total += steps;
                i++;
            }
        }
        return total / i;
    }

    void logGeneration() {
        try {
            PrintWriter writer = new PrintWriter(new FileOutputStream(new File(LOG_LOCATION + "/aggregation.log"), true));
            writer.append(this.getGenerations() + ",");
            // average
            writer.append(Integer.toString(this.getAverageSteps()));
            writer.append(",");
            writer.append(Double.toString(this.getAverageFitness()));
            // best
            writer.append(",");
            writer.append(Integer.toString(this.best.getGameSteps()));
            writer.append(",");
            writer.append(Double.toString(this.best.getFitness()));
            writer.append(",");
            writer.append(Double.toString(this.best.getNumber()));
            // values
            for (int i = 0; i < this.population.length; i++) {
                writer.append(",");
                if (!(this.population[i].getGameSteps() == null)) writer.append(Integer.toString(this.population[i].getGameSteps()));
            }
            //writer.append("," + this.getBest().getBuildOrderJSON() + "\n");
            writer.append("\n");
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}

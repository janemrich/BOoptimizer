package de.jan;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Population {

    private DNA[] population;
    private ArrayList<DNA> matingPool;
    private int generations;
    private String target;
    private boolean finished = false;
    private double mutationRate;
    private double perfectScore = 1;
    private String best = "";

    public Population(String target, double mutationRate, int num) {
        this.target = target;
        this.mutationRate = mutationRate;

        this.population = new DNA[num];
        for (int i = 0; i < this.population.length; i++) {
            population[i] = new DNA(this.target.length());
        }
        this.matingPool = new ArrayList<DNA>();
        calcFitness();
        logGeneration();
    }

    public void calcFitness() {
        for (int i = 0; i < population.length; i++) {
            population[i].calcFitness(this.generations, i, "\"AttackCondition\"   : [ [\"Self\", \"Marine\"], \">=\", [ 10] ]");
        }
    }

    /**
     * evaluate fitness in parallel
     */
    public void calcFitnessParallel() {
        ExecutorService executor = Executors.newFixedThreadPool(1);
        for (int i = 0; i < this.population.length; i++) {
            population[i].setParams(generations, i, "\"AttackCondition\"   : [ [\"Self\", \"Marine\"], \">=\", [ 10] ]");
            executor.submit(this.population[i]);
        }
        //executor.shutdown();
        try {
            System.out.println(executor.awaitTermination(10, TimeUnit.MINUTES));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void naturalSelection() {
        //Clear mating pool
        this.matingPool.clear();

        double minFitness = Double.MAX_VALUE;
        for (DNA dna :
                this.population) {
            if (dna.getFitness() < minFitness) {
                minFitness = dna.getFitness();
            }
        }

        // fill mating pool. members with better fitness are likely to be picked often
        for (int i = 0; i < population.length; i++) {
            double fitness = population[i].getFitness() / minFitness;
            int n = (int) fitness * 100;
            for (int j = 0; j < n; j++) {
                this.matingPool.add(population[i]);
            }
        }
    }

    // create new generation
    public void generate() {
        Random rn = new Random();
        for (int i = 0; i < this.population.length; i++) {
            int a = rn.nextInt(this.matingPool.size());
            int b = rn.nextInt(this.matingPool.size());
            DNA partnerA = this.matingPool.get(a);
            DNA partnerB = this.matingPool.get(b);
            DNA child = partnerA.crossover(partnerB);
            int j = 0;
            while (!child.valid() && i < 100) {
                child = partnerA.crossover(partnerB);
                j++;
            }
            DNA mutatedChild = child;
            mutatedChild.mutate(this.mutationRate);
            while (!mutatedChild.valid()) {
                mutatedChild = child;
                mutatedChild.mutate(1);
            }
            this.population[i] = child;
        }
        this.generations++;
    }

    public String getBest() {
        return best;
    }

    public void evaluate() {
        double worldrecord = 0;
        int index = 0;
        for (int i = 0; i < this.population.length; i++) {
            if (this.population[i].getFitness() > worldrecord) {
                index = i;
                worldrecord = this.population[i].getFitness();
            }
        }

        this.best = this.population[index].getBuildOrderJSON();
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
        for (DNA dna :
                this.population) {
            total += dna.getFitness();
        }
        return total / this.population.length;
    }

    void logGeneration() {
        try {
            PrintWriter writer = new PrintWriter(new FileOutputStream(new File("/home/jan/Documents/Starcraft/Log/aggregation.log"),true));
            writer.append(this.getGenerations() + ",");
            // average
            writer.append(Double.toString(this.getAverageFitness()));
            // values
            for (int i = 0; i < this.population.length; i++) {
                writer.append("," + this.population[i].getFitness());
            }
            writer.append("," + this.getBest() + "\n");
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}

package de.jan;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import static de.jan.Main.*;
import static de.jan.SCutil.relaunchStarcraft;

public abstract class Chromosome implements Runnable {

    private double fitness;
    private Integer gameSteps;

    private int generation;
    private int number;
    private String target;

    public boolean isFail() {
        return fail;
    }

    private boolean fail = false;

    public boolean isDone() {
        return done;
    }

    private boolean done = false;


    public void run() {
        evaluate(generation, number, target);
    }

    /**
     * set data to run in this generation
     */
    public void setParams(int generation, int number, String target) {
        this.generation = generation;
        this.number = number;
        this.target = target;
    }

    public abstract Chromosome clone();

    ;

    /**
     * creates genes randomly
     *
     * @param length
     */
    protected abstract void createGenes(int length);

    /**
     * checks if genes are valid for a 10-marine push
     *
     * @return
     */
    public abstract boolean valid();

    public abstract String getBuildOrderJSON();

    /**
     * findBest fitness
     *  @param generation
     * @param dna
     * @param target
     */
    public void evaluate(int generation, int dna, String target) {
        try {
            done = false;
            int threadId = (int) (Thread.currentThread().getId() % THREADS);
            String directory = "/home/jan/Documents/Starcraft/Log/" + Integer.toString(generation);
            Files.createDirectories(Paths.get(directory));
            String[] params = {
                    "-e",
                    "/home/jan/StarCraftII/Versions/Base59877/SC2_x64",
                    //"--port", Integer.toString(8167+threadId),
                    "{\"SC2API Strategy\"           : {" +
                            "\"Terran\"             : \"Terran_MarineRush\"," +
                            "\"ScoutHarassEnemy\"   : true," +
                            "\"Strategies\"         : {" +
                            "\"Terran_MarineRush\"     : {" +
                            "\"Race\"              : \"Terran\"," +
                            "\"OpeningBuildOrder\" : " +
                            this.getBuildOrderJSON() + "," +
                            //"[\"SCV\", \"SCV\", \"SupplyDepot\", \"SCV\", \"SCV\", \"Barracks\", \"Barracks\", \"Barracks\", \"Barracks\", \"SupplyDepot\", \"SupplyDepot\", \"Marine\", \"Marine\", \"Marine\", \"Marine\", \"Marine\", \"Marine\", \"Marine\", \"Marine\", \"Marine\", \"Marine\", \"Marine\", \"Marine\", \"Marine\", \"Marine\", \"Marine\", \"Marine\", \"Marine\", \"Marine\", \"Marine\", \"Marine\"]," +
                            "\"ScoutCondition\"    : [ [\"Self\", \"SupplyDepot\"], \">\", [ 0 ] ]," +
                            target +
                            "}  }   }   }",
                    "1"};
            String commandcenter = "/home/jan/Documents/Starcraft/commandcenter/bin/CommandCenter";
            Process process = new ProcessBuilder(commandcenter, params[0], params[1], params[2],
                    Integer.toString(generation), Integer.toString(dna), Integer.toString(threadId)).start();
            if (process.waitFor(100, TimeUnit.SECONDS)) {
                done = true;
                if (process.exitValue() != 0) {
                    System.err.println("\nStarcraft " + dna + " / " + threadId + " relaunched after Error code " + process.exitValue());
                    relaunchStarcraft(threadId);
                    this.gameSteps = null;
                    if (process.exitValue() == 1) fail = true;
                    else fail = false;
                }
                // commandcenter exited normally
                System.out.print(".");
                String path = directory + "/" + Integer.toString(dna) + ".log";
                String log;
                log = new String(Files.readAllBytes(Paths.get(path)));
                String[] logs = log.split("\n");
                this.gameSteps = Integer.parseInt(logs[1]);
                this.fitness =  1 / (Math.pow(gameSteps, 4) / Math.pow(3800, 4));
                fail = false;
            } else {
                // overtime
                done = true;
                System.err.println("\nBot " + dna + " / " + threadId + " killed due to overtime ");
                process.destroy();
                relaunchStarcraft(threadId);
                this.gameSteps = null;
                fail = false;
            }
        } catch (IOException e) {
            System.out.println(this.getBuildOrderJSON());
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public abstract Chromosome[] crossover(Chromosome partner);

    public abstract void mutate(double mutationRate);

    public double getFitness() {
        return this.fitness;
    }

    public Integer getGameSteps() {
        return gameSteps;
    }
}

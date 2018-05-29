package de.jan;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class DNA {

    private Unit[] genes;
    private int fitness = 0;

    /**
     * constructs DNA with valid genes
     *
     * @param length
     */
    public DNA(int length) {
        createGenes(length);
        while (!this.valid()) {
            createGenes(length);
        }
    }

    /**
     * creates genes randomly
     *
     * @param length
     */
    private void createGenes(int length) {
        genes = new Unit[length];
        for (int i = 0; i < length; i++)
            genes[i] = newUnit();
    }

    /**
     * checks if genes are valid for a 10-marine push
     *
     * @return
     */
    public boolean valid() {
        boolean barracks = false;
        boolean supply = false;
        int marines = 0;
        for (Unit u :
                this.genes) {
            switch (u) {
                case MARINE:
                    if (!barracks) return false;
                    marines++;
                    break;
                case BARRACKS:
                    if (!supply) return false;
                    barracks = true;
                    break;
                case SUPPLY_DEPOT:
                    supply = true;
            }
        }
        if (marines < 10 || !barracks || !supply) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * @return random Unit
     */
    private Unit newUnit() {
        Random rn = new Random();

        int i = rn.nextInt(Unit.values().length);
        return Unit.values()[i];
    }

    public String getBuildOrderJSON() {
        StringBuilder sb = new StringBuilder();
        sb.append("[\"");
        sb.append(genes[0].getUnitDescription());
        if (genes.length > 1) {
            for (int i = 1; i < genes.length; i++) {
                sb.append("\", \"");
                sb.append(genes[i].getUnitDescription());
            }
        }
        sb.append("\"]");
        return sb.toString();
    }

    /**
     * evaluate fitness
     *
     * @param generation
     * @param dna
     * @param target
     */
    public void calcFitness(int generation, int dna, String target) {
        try {
            String directory = "/home/jan/Documents/Starcraft/Log/" + Integer.toString(generation);
            Files.createDirectories(Paths.get(directory));
            String[] params = {
                    "-e",
                    "/home/jan/StarCraftII/Versions/Base59877/SC2_x64",
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
            System.out.println(params[2]);
            String commandcenter = "/home/jan/Documents/Starcraft/commandcenter/bin/CommandCenter";
            Process process = new ProcessBuilder(commandcenter, params[0], params[1], params[2],
                    Integer.toString(generation), Integer.toString(dna)).start();
            if (process.waitFor(100, TimeUnit.SECONDS)) {
                // commandcenter exited normally
                String path = directory + "/" + Integer.toString(dna) + ".log";
                String log;
                log = new String(Files.readAllBytes(Paths.get(path)));
                String[] logs = log.split("\n");
                this.fitness = Integer.parseInt(logs[1]);
            } else {
                // commandcenter run into problems
                process = new ProcessBuilder(commandcenter, params[0], params[1], params[2],
                        Integer.toString(generation), Integer.toString(dna)).start();
                if (process.waitFor(100, TimeUnit.SECONDS)) {
                    // commandcenter exited normally
                    String path = directory + "/" + Integer.toString(dna) + ".log";
                    String log;
                    log = new String(Files.readAllBytes(Paths.get(path)));
                    String[] logs = log.split("\n");
                    this.fitness = Integer.parseInt(logs[1]);
                } else {
                    // give up
                    process.destroyForcibly();
                    this.fitness = Integer.MAX_VALUE;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public DNA crossover(DNA partner) {
        // A new child
        DNA child = new DNA(this.genes.length);

        Random rn = new Random();
        int midpoint = rn.nextInt(this.genes.length); // pick a midpoint

        // First half from this, second from partner
        for (int i = 0; i < this.genes.length; i++) {
            if (i < midpoint) child.genes[i] = this.genes[i];
            else child.genes[i] = partner.genes[i];
        }
        return child;
    }

    public void mutate(float mutationRate) {
        Random rn = new Random();
        for (int i = 0; i < this.genes.length; i++) {
            if (rn.nextFloat() < mutationRate) {
                this.genes[i] = newUnit();
            }
        }
    }

    public int getFitness() {
        return this.fitness;
    }
}

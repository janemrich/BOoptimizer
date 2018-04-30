package de.jan;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

public class DNA {

    private Unit[] genes;
    private float fitness = 0;

    public DNA(int length) {
        genes = new Unit[length];
        for (int i = 0; i < length; i++)
            genes[i] = newUnit();
    }

    private Unit newUnit() {
        Random rn = new Random();

        int i = rn.nextInt(Unit.values().length - 1);
        return Unit.values()[i];
    }

    public String getPhrase() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < genes.length; i++) {
            sb.append(genes[i].getUnitDescription());
        }
        return sb.toString();
    }

    public void calcFitness(int run) {
        try {
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
                            "[\"SCV\", \"SCV\", \"SupplyDepot\", \"SCV\", \"SCV\", \"Barracks\", \"Barracks\", \"Barracks\", \"Barracks\", \"SupplyDepot\", \"SupplyDepot\", \"Marine\", \"Marine\", \"Marine\", \"Marine\", \"Marine\", \"Marine\", \"Marine\", \"Marine\", \"Marine\", \"Marine\", \"Marine\", \"Marine\", \"Marine\", \"Marine\", \"Marine\", \"Marine\", \"Marine\", \"Marine\", \"Marine\", \"Marine\"]," +
                            "\"ScoutCondition\"    : [ [\"Self\", \"SupplyDepot\"], \">\", [ 0 ] ]," +
                            "\"AttackCondition\"   : [ [\"Self\", \"Marine\"], \">=\", [ 10] ]" +
                            "}  }   }   }",
                    "1"};
            String commandcenter = "/home/jan/Documents/Starcraft/commandcenter/bin/CommandCenter";
            Process process = new ProcessBuilder(commandcenter, params[0], params[1], params[2], Integer.toString(run)).start();
            process.waitFor();
            String path = "/home/jan/Documents/Starcraft/Log/" + Integer.toString(run) + ".log";
            String log;
            log = new String(Files.readAllBytes(Paths.get(path)));
            this.fitness = Float.parseFloat(log);
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

    public float getFitness() {
        return this.fitness;
    }
}

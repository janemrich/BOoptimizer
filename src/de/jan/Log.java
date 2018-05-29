package de.jan;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class Log {
    void logGeneration(DNA[] population, int generation) {
        try {
            PrintWriter writer = new PrintWriter("/home/jan/Documents/Starcraft/Log/aggregation.log");
            writer.println("generation: " + generation);
            // average
            writer.print("average: ");
            float average = 0;
            for (DNA dna :
                    population) {
                average =+ dna.getFitness();
            }
            average = average / population.length;
            writer.println(average);
            // values
            for (int i = 0; i < population.length; i++) {
                writer.println(population[i].getFitness());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}

package de.jan;

import java.util.Random;

public class DNA {

    private char[] genes;
    private float fitness = 0;

    public DNA(int length) {
        genes = new char[length];
        for (int i = 0; i < length; i++)
            genes[i] = newChar();
    }

    private Character newChar() {
        Random rn = new Random();

        int c = rn.nextInt(57) + 65;
        return (char) c;
    }

    public String getPhrase() {
        return new String(this.genes);
    }

    public void calcFitness(String target) {
        int score = 0;
        for (int i = 0; i < this.genes.length; i++) {
            if (this.genes[i] == (target.charAt(i))) {
                score++;
            }
        }
        this.fitness = score / (float) target.length();
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
                this.genes[i] = newChar();
            }
        }
    }

    public float getFitness() {
        return this.fitness;
    }
}

package de.jan;

import com.sun.istack.internal.NotNull;

import java.util.Random;

import static de.jan.Main.ADD_DELETE;
import static de.jan.Main.SINGLE_POINT;

public class SingleChromosome extends Chromosome {

    private Unit[] genes;

    /**
     * constructs SingleChromosome with valid genes
     *
     * @param length
     */
    public SingleChromosome(int length) {
        createGenes(length);
        while (!this.valid()) {
            createGenes(length);
        }
    }

    public SingleChromosome clone() {
        SingleChromosome chr = new SingleChromosome(genes.length);
        chr.genes = this.genes.clone();
        return chr;
    }

    /**
     * creates genes randomly
     *
     * @param length
     */
    protected void createGenes(int length) {
        genes = new Unit[length];
        for (int i = 0; i < length; i++)
            genes[i] = Unit.randomUnit();
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

    public Chromosome[] crossover(Chromosome partner) {
        SingleChromosome child = new SingleChromosome(this.genes.length);
        SingleChromosome child2 = new SingleChromosome(this.genes.length);
        boolean firsttime = true;
        while (!child.valid() || !child2.valid() || firsttime) {
            firsttime = false;
            Random rn = new Random();
            int midpoint = rn.nextInt(this.genes.length); // pick a midpoint

            if (SINGLE_POINT) {
                // First half from this, second from partner
                for (int i = 0; i < this.genes.length; i++) {
                    if (i < midpoint) {
                        child.genes[i] = this.genes[i];
                        child2.genes[i] = ((SingleChromosome) partner).genes[i];
                    } else {
                        child.genes[i] = ((SingleChromosome) partner).genes[i];
                        child2.genes[i] = this.genes[i];
                    }
                }
            } else {
                int secondpoint = rn.nextInt(genes.length);
                if (midpoint > secondpoint) {
                    int swap = midpoint;
                    midpoint = secondpoint;
                    secondpoint = swap;
                }
                for (int i = 0; i < this.genes.length; i++) {
                    if (i < midpoint || i > secondpoint) {
                        child.genes[i] = this.genes[i];
                        child2.genes[i] = ((SingleChromosome) partner).genes[i];
                    } else {
                        child.genes[i] = ((SingleChromosome) partner).genes[i];
                        child2.genes[i] = this.genes[i];
                    }
                }
            }
        }
        return new Chromosome[]{child, child2};
    }

    public void mutate(double mutationRate) {
        Random rn = new Random();
        double mutation = rn.nextDouble();
        if (ADD_DELETE) {
            if (rn.nextDouble() < mutationRate) {
                if (mutation < (1 / (double)3)) {
                    for (int i = 0; i < this.genes.length; i++) {
                        if (rn.nextDouble() < 0.01) {
                            genes[i] = Unit.randomUnit();
                        }
                    }
                } else if (mutation < (2 / (double)3)) {
                    int toRemove = (int) rn.nextDouble() * genes.length;
                    for (int i = toRemove; i < (genes.length - 1); i++) {
                        genes[i] = genes[i + 1];
                    }
                    genes[genes.length - 1] = Unit.randomUnit();
                } else {
                    int toAdd = (int) rn.nextDouble() * genes.length;
                    for (int i = genes.length; i < toAdd; i--) {
                        genes[i] = genes[i - 1];
                    }
                    genes[toAdd] = Unit.randomUnit();
                }
            }
        } else {
            for (int i = 0; i < this.genes.length; i++) {
                if (rn.nextDouble() < 0.01) {
                    this.genes[i] = Unit.randomUnit();
                }
            }
        }
    }
}

package de.jan;

import java.util.ArrayList;
import java.util.Random;

import static de.jan.Main.ADD_DELETE;

public class MultiChromosome extends Chromosome {

    private ArrayList<Unit> first;
    private ArrayList<Unit> second;
    private ArrayList<Unit> third;

    /**
     * constructs SingleChromosome with valid genes
     *
     * @param length
     */
    public MultiChromosome(int length) {
        createGenes(length);
    }

    @Override
    public MultiChromosome clone() {
        MultiChromosome chr = new MultiChromosome(length());
        chr.first = (ArrayList<Unit>) first.clone();
        chr.second = (ArrayList<Unit>) second.clone();
        chr.third = (ArrayList<Unit>) third.clone();
        return chr;
    }

    @Override
    protected void createGenes(int length) {
        first = new ArrayList<>();
        while (!first.contains(Unit.SUPPLY_DEPOT))
            first.add(Unit.firstUnit());

        second = new ArrayList<>();
        while (!second.contains(Unit.BARRACKS)) second.add(Unit.secondUnit());

        third = new ArrayList<>();
        while (count(third, Unit.MARINE) < 10) third.add(Unit.randomUnit());
    }

    public void trim() {
        int index = 0;
        int marines = 0;
        for (int i = 0; i < third.size(); i++) {
            if (third.get(i) == Unit.MARINE) marines++;
            if (marines == 10) index = i;
        }
        third = new ArrayList<>(third.subList(0, index+1));
    }

    /**
     * total length of the chromosomes in this member
     *
     * @return length
     */
    private int length() {
        return first.size() + second.size() + third.size();
    }

    /**
     * get the total gene
     *
     * @return
     */
    private ArrayList<Unit> getAll() {
        ArrayList<Unit> all = new ArrayList<>();
        all.addAll(first);
        all.addAll(second);
        all.addAll(third);
        return all;
    }

    /**
     * count Unit type
     *
     * @param list
     * @param unit
     * @return number
     */
    private int count(ArrayList<Unit> list, Unit unit) {
        return (int) list.stream().filter(u -> u == unit).count();
    }

    private int countFirst(Unit unit) {
        return count(first, unit);
    }

    private int countFirstTwo(Unit unit) {
        return count(first, unit) + count(second, unit);
    }


    @Override
    public boolean valid() {
        return countFirst(Unit.SUPPLY_DEPOT) > 0
                && countFirst(Unit.BARRACKS) == 0 && count(second, Unit.BARRACKS) > 0
                && countFirstTwo(Unit.MARINE) == 0 && count(third, Unit.MARINE) >= 10;
    }

    @Override
    public String getBuildOrderJSON() {
        StringBuilder sb = new StringBuilder();
        sb.append("[\"");
        for (int i = 0; i < first.size(); i++) {
            sb.append(first.get(i).getUnitDescription());
            sb.append("\", \"");
        }
        for (int i = 0; i < second.size(); i++) {
            sb.append(second.get(i).getUnitDescription());
            sb.append("\", \"");
        }
        sb.append(third.get(0).getUnitDescription());
        for (int i = 1; i < third.size(); i++) {
            sb.append("\", \"");
            sb.append(third.get(i).getUnitDescription());
        }
        sb.append("\"]");
        return sb.toString();
    }

    @Override
    public Chromosome[] crossover(Chromosome partner) {
        MultiChromosome other = (MultiChromosome) partner;

        // new children
        MultiChromosome child = new MultiChromosome(getAll().size());
        MultiChromosome child2 = new MultiChromosome(getAll().size());

        Random rn = new Random();
        int midpoint = rn.nextInt(3); // pick a midpoint

        switch (midpoint) {
            case 0:
                child.first = first;
                child.second = other.second;
                child.third = other.third;

                child2.first = other.first;
                child2.second = second;
                child2.third = third;
                break;
            case 1:
                child.first = other.first;
                child.second = second;
                child.third = other.third;

                child2.first = first;
                child2.second = other.second;
                child2.third = third;
                break;
            case 2:
                child.first = other.first;
                child.second = other.second;
                child.third = third;

                child2.first = first;
                child2.second = second;
                child2.third = other.third;
                break;
        }

        return new Chromosome[]{child, child2};
    }

    @Override
    public void mutate(double mutationRate) {
        Random rn = new Random();
        double mutation = rn.nextDouble();
        if (ADD_DELETE) {
            if (rn.nextDouble() < mutationRate) {
                if (mutation < (1 / (double) 3)) {
                    for (Unit u :
                            first) {
                        if (rn.nextDouble() < 0.02) u = Unit.firstUnit();
                    }
                    for (Unit u :
                            second) {
                        if (rn.nextDouble() < 0.02) u = Unit.secondUnit();
                    }
                    for (Unit u :
                            third) {
                        if (rn.nextDouble() < 0.02) u = Unit.randomUnit();
                    }
                } else if (mutation < (2 / (double) 3)) {
                    int toRemove = (int) (rn.nextDouble() * length());
                    if (toRemove < first.size()) {
                        first.remove(toRemove);
                    } else if (toRemove < first.size() + second.size()) {
                        second.remove(toRemove - first.size());
                    } else {
                        third.remove(toRemove - (first.size() + second.size()));
                    }
                } else {
                    int toAdd = (int) (rn.nextDouble() * length());
                    if (toAdd < first.size()) {
                        first.add(toAdd, Unit.firstUnit());
                    } else if (toAdd < first.size() + second.size()) {
                        second.add(toAdd - first.size(), Unit.secondUnit());
                    } else {
                        third.add(toAdd - (first.size() + second.size()), Unit.randomUnit());
                    }
                }
            }
        } else {
            for (Unit u :
                    first) {
                if (rn.nextDouble() < 0.02) u = Unit.firstUnit();
            }
            for (Unit u :
                    second) {
                if (rn.nextDouble() < 0.02) u = Unit.secondUnit();
            }
            for (Unit u :
                    third) {
                if (rn.nextDouble() < 0.02) u = Unit.randomUnit();
            }
        }
    }
}
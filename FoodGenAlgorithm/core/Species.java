package core;

import java.util.ArrayList;

/**
 * A single species: one integer amount per food in the problem's food list.
 * <p>
 * {@code chromosome[i]} is how many 100g portions of the ith food this
 * species chose. The fitness field is set by GenAlgorithm
 * and reflects how close the diet is to the norms. The lower, the better.
 */
public class Species {
    private final int[] chromosome;
    public double fitness;

    public Species(int[] chr) { this.chromosome = chr.clone(); }

    public int getGenAt(int ind) { return chromosome[ind]; }

    public void setGenAt(int ind, int newVal) { chromosome[ind] = newVal; }

    public int[] getChromosome() { return chromosome; }

    public ArrayList<Integer> getIndOfActiveGenes() {
        ArrayList<Integer> genes = new ArrayList<>();

        for (int i = 0; i < chromosome.length; i++) {
            if (chromosome[i] != 0) {
                genes.add(i);
            }
        }

        return genes;
    }
}
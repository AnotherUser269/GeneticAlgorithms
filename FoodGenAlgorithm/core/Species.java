package core;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A basic class, representing a single species.
 * <p>
 * A chromosome is an arraylist, where chromosome[i] is
 * the answer to the question:
 * "How many times should you take a data[i] food from the GenAlgorithm class,
 * in order to follow the norms as much as possible?"
 */
public class Species {
    private final ArrayList<BigDecimal> chromosome;
    public double fitness;

    public Species(ArrayList<BigDecimal> chr) {
        this.chromosome = new ArrayList<>(chr);
    }

    public BigDecimal getGenAt(int ind) { return chromosome.get(ind); }

    public void setGenAt(int ind, BigDecimal newVal) { chromosome.set(ind, newVal); }

    public List<BigDecimal> getChromosome() { return Collections.unmodifiableList(chromosome); }
}
import core.Food;

import java.math.BigDecimal;
import java.util.*;

/**
 * A basic class, representing a single species.
 * <p>
 * A chromosome is an arraylist, where chromosome[i] is
 * the answer to the question:
 * "How many times should you take a data[i] food from the GenAlgorithm class,
 * in order to follow the norms as much as possible?"
 */
class Species {
    private final ArrayList<BigDecimal> chromosome;
    double fitness;

    Species(ArrayList<BigDecimal> chr) {
        this.chromosome = new ArrayList<>(chr);
    }

    BigDecimal getGenAt(int ind) { return chromosome.get(ind); }

    void setGenAt(int ind, BigDecimal newVal) { chromosome.set(ind, newVal); }

    List<BigDecimal> getChromosome() { return Collections.unmodifiableList(chromosome); }
}

public class GenAlgorithm {
    private final Random rng = new Random();

    // Algorithm settings
    private final int populationSize;
    private final int iterations;
    private final double mutationRate;
    private final int distinctProductRestriction;
    private final int genValueRestriction;
    private final BigDecimal ccalNorm;
    private final BigDecimal fatNorm;
    private final BigDecimal proteinNorm;
    private final BigDecimal carbsNorm;
    private final int ccalFactor;
    private final int fatFactor;
    private final int proteinFactor;
    private final int carbsFactor;
    private final int penaltyFactor;
    private final ArrayList<Food> data;

    private GenAlgorithm(Builder builder) {
        this.populationSize = builder.populationSize;
        this.iterations = builder.iterations;
        this.mutationRate = builder.mutationRate;
        this.distinctProductRestriction = builder.distinctProductRestriction;
        this.genValueRestriction = builder.genValueRestriction;
        this.ccalNorm = builder.ccalNorm;
        this.fatNorm = builder.fatNorm;
        this.proteinNorm = builder.proteinNorm;
        this.carbsNorm = builder.carbsNorm;
        this.ccalFactor = builder.ccalFactor;
        this.fatFactor = builder.fatFactor;
        this.proteinFactor = builder.proteinFactor;
        this.carbsFactor = builder.carbsFactor;
        this.penaltyFactor = builder.penaltyFactor;

        this.data = builder.data;
    }

    public static class Builder {
        private int populationSize = 100;
        private int iterations = 1000;
        private double mutationRate = 0.3;
        private int distinctProductRestriction = 5;
        private int genValueRestriction = 6;
        private BigDecimal ccalNorm;
        private BigDecimal fatNorm;
        private BigDecimal proteinNorm;
        private BigDecimal carbsNorm;
        private int ccalFactor = 1;
        private int fatFactor = 1;
        private int proteinFactor = 1;
        private int carbsFactor = 1;
        private int penaltyFactor = 10_000;
        private ArrayList<Food> data;

        public Builder setPopulation(int population) {
            // 0 or fewer species
            if(BigDecimal.valueOf(population).compareTo(BigDecimal.ZERO) <= 0) {
                System.err.println("[Error] Wrong population value provided. Expected an integer above zero. Using default value.");
                return this;
            }

            this.populationSize = population;
            return this;
        }

        public Builder setIterations(int iterations) {
            // <= 0 iterations
            if(iterations <= 0) {
                System.err.println("[Error] Wrong iterations value provided. Expected an integer above zero. Using default value.");
                return this;
            }

            this.iterations = iterations;
            return this;
        }

        public Builder setMutationRate(double mutationRate) {
            // < 0% mutation
            if(mutationRate < 0) {
                System.err.println("[Error] Wrong mutation rate provided. Expected a double above or equal to zero. Using default value.");
                return this;
            }

            this.mutationRate = mutationRate;
            return this;
        }

        public Builder setProductAmountRestriction(int restriction) {
            // 0 or fewer products to take
            if(restriction <= 0) {
                System.err.println("[Error] Wrong restriction value for food provided. Expected an integer above zero. Using default value.");
                return this;
            }

            this.distinctProductRestriction = restriction;
            return this;
        }

        public Builder setGenValueRestriction(int restriction) {
            // 0 or fewer for each gen
            if(restriction <= 0) {
                System.err.println("[Error] Wrong restriction value for gen provided. Expected an integer above zero. Using default value.");
                return this;
            }

            this.genValueRestriction = restriction;
            return this;
        }

        public Builder setNorms(BigDecimal ccal, BigDecimal fat, BigDecimal protein, BigDecimal carbs) {
            // < 0 ccal
            if(ccal.compareTo(BigDecimal.ZERO) < 0) {
                System.err.println("[Error] Wrong ccal norm provided. Expected a BigDecimal above or equal to zero. Using default value.");
            } else {
                this.ccalNorm = ccal;
            }

            // < 0 fat
            if(fat.compareTo(BigDecimal.ZERO) < 0) {
                System.err.println("[Error] Wrong fat norm provided. Expected a BigDecimal above or equal to zero. Using default value.");
            } else {
                this.fatNorm = fat;
            }

            // < 0 protein
            if(protein.compareTo(BigDecimal.ZERO) < 0) {
                System.err.println("[Error] Wrong protein norm provided. Expected a BigDecimal above or equal to zero. Using default value.");
            } else {
                this.proteinNorm = protein;
            }

            // < 0 carbs
            if(carbs.compareTo(BigDecimal.ZERO) < 0) {
                System.err.println("[Error] Wrong carbs norm provided. Expected a BigDecimal above or equal to zero. Using default value.");
            } else {
                this.carbsNorm = carbs;
            }

            return this;
        }

        public Builder setFactors(int ccalFactor, int fatFactor, int proteinFactor, int carbsFactor) {
            this.ccalFactor = ccalFactor;
            this.fatFactor = fatFactor;
            this.proteinFactor = proteinFactor;
            this.carbsFactor = carbsFactor;
            return this;
        }

        public Builder setPenaltyFactor(int penaltyFactor) {
            this.penaltyFactor = penaltyFactor;
            return this;
        }

        public Builder setData(ArrayList<Food> data) {
            this.data = data;
            return this;
        }

        public GenAlgorithm build() {
            return new GenAlgorithm(this);
        }
    }

    /**
     * Makes {@link #iterations} steps of simulation.
     * A one step could be described as:
     * 1. Reproduction
     * 2. Mutation
     * 3. Selection
     * @return ArrayList<BigDecimal> - the best species as a result of the whole simulation.
     */
    public List<BigDecimal> startSimulation() {
        ArrayList<Species> population = this.initializePopulation();

        for (int i=0; i<iterations; i++) {
            // Adding children species to the population
            ArrayList<Species> children = this.reproduce(population);
            this.mutation(children);
            population.addAll(children);

            // After that, we are choosing only populationSize best
            population = this.selectNBest(population, populationSize);
        }

        return population.getFirst().getChromosome();
    }

    /**
     * Initializes a population of {@link #populationSize} species
     * with random chromosomes that would not trigger penalty.
     * @return ArrayList<Species> - a population
     */
    private ArrayList<Species> initializePopulation() {
        ArrayList<Species> population = new ArrayList<>();

        for(int i=0; i<populationSize; i++) {
            ArrayList<BigDecimal> chromosome = new ArrayList<>();

            // Filling with zeros
            for(int j=0; j<data.size(); j++) {
                chromosome.add(BigDecimal.ZERO);
            }

            // Generating valid not punished species
            for(int j=0; j<distinctProductRestriction; j++) {
                int ind = rng.nextInt(data.size());
                // Collisions are allowed on purpose
                chromosome.set(ind, BigDecimal.valueOf(rng.nextInt(genValueRestriction + 1)));
            }

            population.add(new Species(chromosome));
        }

        return population;
    }

    /**
     * Chooses for every child species if it should mutate or not.
     * Every species mutates with {@link #mutationRate} rate.
     * @param children - a link to the ArrayList of children
     */
    private void mutation(ArrayList<Species> children) {
        for(Species sp: children) {
            if (rng.nextDouble() < mutationRate) {
                BigDecimal newValue = BigDecimal.valueOf(rng.nextInt(genValueRestriction + 1));
                int ind = rng.nextInt(data.size());

                sp.setGenAt(ind, newValue);
            }
        }
    }

    /**
     * Pairs up individuals and produces offspring via crossover.
     * @param population - a link to the ArrayList of population.
     * @return ArrayList<Species> - children
     */
    private ArrayList<Species> reproduce(ArrayList<Species> population) {
        Species parentA;
        Species parentB;
        ArrayList<Species> children = new ArrayList<>();

        if (populationSize < 2) return children;

        for(int i=0; i<populationSize/2; i++) {
            parentA = population.get(rng.nextInt(populationSize));
            do {
                parentB = population.get(rng.nextInt(populationSize));
            } while (parentA == parentB);

            Species child = new Species(this.crossover(parentA, parentB));
            children.add(child);
        }

        return children;
    }

    /**
     * Get a child of two parents.
     * @param parentA - first parent
     * @param parentB - second parent
     * @return ArrayList<BigDecimal> - their child
     */
    private ArrayList<BigDecimal> crossover(Species parentA, Species parentB) {
        int crInd = rng.nextInt(data.size()-1) + 1;  // crosspoint > 0
        ArrayList<BigDecimal> newSpecies = new ArrayList<>();

        for(int i=0;i<=crInd;i++) {
            newSpecies.add(parentA.getGenAt(i));
        }

        for(int i=crInd+1;i<data.size();i++) {
            newSpecies.add(parentB.getGenAt(i));
        }

        return newSpecies;
    }

    /**
     * Updates fitness fields for every species across pool
     * @param pool - Pool to update fitness in
     */
    private void updateFitness(ArrayList<Species> pool) {
        for (Species sp : pool) {
            sp.fitness = evaluate(sp);
        }
    }

    /**
     * Selects n species from a pool with the lowest fitness func value.
     * @param pool - Pool to get species from
     * @param n - how much species we take
     * @return ArrayList<Species> - a list of best species
     */
    private ArrayList<Species> selectNBest(ArrayList<Species> pool, int n) {
        this.updateFitness(pool);

        pool.sort(Comparator.comparingDouble(a -> a.fitness));
        return new ArrayList<>(pool.subList(0, n));
    }

    /**
     * Evaluate a fitness formula for a species. The formula is:
     * f(chr) = ccalFactor*|ccalNorm-ccalCurr|^2 + fatFactor*|fatNorm-fatCurr|^2 +
     * + proteinFactor*|proteinNorm-proteinCurr|^2 + carbsFactor*|carbsNorm-carbsCurr|^2 + penalty
     * @param sp - chosen species
     * @return double - fitness func value for a sp species
     */
    private double evaluate(Species sp) {
        // Getting total ccal, fat, protein, carbs of a species
        BigDecimal ccal = BigDecimal.ZERO;
        BigDecimal fat = BigDecimal.ZERO;
        BigDecimal protein = BigDecimal.ZERO;
        BigDecimal carbs = BigDecimal.ZERO;

        List<BigDecimal> chromosome = sp.getChromosome();

        for (int i = 0; i<data.size(); i++) {
            BigDecimal amount = chromosome.get(i);
            if (amount.signum() == 0) continue;

            Food currFood = data.get(i);

            ccal = ccal.add(currFood.ccal().multiply(amount));
            fat = fat.add(currFood.fat().multiply(amount));
            protein = protein.add(currFood.protein().multiply(amount));
            carbs = carbs.add(currFood.carbs().multiply(amount));
        }

        // Calculating the fitness
        BigDecimal penalty = getPenalty(chromosome);

        BigDecimal dCcal    = ccalNorm.subtract(ccal).pow(2);
        BigDecimal dFat     = fatNorm.subtract(fat).pow(2);
        BigDecimal dProtein = proteinNorm.subtract(protein).pow(2);
        BigDecimal dCarbs   = carbsNorm.subtract(carbs).pow(2);

        BigDecimal err = dCcal.multiply(BigDecimal.valueOf(ccalFactor))
                .add(dFat.multiply(BigDecimal.valueOf(fatFactor)))
                .add(dProtein.multiply(BigDecimal.valueOf(proteinFactor)))
                .add(dCarbs.multiply(BigDecimal.valueOf(carbsFactor)));

        return err.add(penalty).doubleValue();
    }

    /**
     * Calculates a total penalty for choosing a lot of distinct food.
     * @param chr - chromosome of a species
     * @return BigDecimal - Penalty value * factor
     */
    private BigDecimal getPenalty(List<BigDecimal> chr) {
        // Counting taken food
        int counter = 0;

        for (BigDecimal gen: chr) {
            if(gen.compareTo(BigDecimal.ZERO) != 0) counter++;
        }

        if(counter < distinctProductRestriction) {
            // The rule was not broken - no penalty
            return BigDecimal.ZERO;
        }

        // The rule was broken
        return BigDecimal.valueOf(counter - distinctProductRestriction)
                .multiply(BigDecimal.valueOf(penaltyFactor));
    }
}

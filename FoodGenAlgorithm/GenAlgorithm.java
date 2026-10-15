import core.Food;
import core.Species;

import java.math.BigDecimal;
import java.util.*;

public class GenAlgorithm {
    private static final int DEFAULT_ACTIVE_GENES = 10;
    private final Random rng = new Random();

    // Algorithm settings
    private final int populationSize;
    private final int iterations;
    private final double mutationRate;
    private final int maxDistinctFoods;
    private final int minDistinctFoods;
    private final int maxPortionsPerFood;
    private final int migrationInterval;
    private final BigDecimal kcalNorm;
    private final BigDecimal fatNorm;
    private final BigDecimal proteinNorm;
    private final BigDecimal carbsNorm;
    private final int kcalFactor;
    private final int fatFactor;
    private final int proteinFactor;
    private final int carbsFactor;
    private final int penaltyFactor;
    private final ArrayList<Food> foods;

    private final double[] foodKcal;
    private final double[] foodFat;
    private final double[] foodProtein;
    private final double[] foodCarbs;

    private GenAlgorithm(Builder builder) {
        this.populationSize = builder.populationSize;
        this.iterations = builder.iterations;
        this.mutationRate = builder.mutationRate;
        this.maxDistinctFoods = builder.maxDistinctFoods;
        this.minDistinctFoods = builder.minDistinctFoods;
        this.maxPortionsPerFood = builder.maxPortionsPerFood;
        this.migrationInterval = builder.migrationInterval;
        this.kcalNorm = builder.kcalNorm;
        this.fatNorm = builder.fatNorm;
        this.proteinNorm = builder.proteinNorm;
        this.carbsNorm = builder.carbsNorm;
        this.kcalFactor = builder.kcalFactor;
        this.fatFactor = builder.fatFactor;
        this.proteinFactor = builder.proteinFactor;
        this.carbsFactor = builder.carbsFactor;
        this.penaltyFactor = builder.penaltyFactor;

        this.foods = builder.foods;

        // Performance improvement
        int n = builder.foods.size();
        this.foodKcal    = new double[n];
        this.foodFat     = new double[n];
        this.foodProtein = new double[n];
        this.foodCarbs   = new double[n];

        for (int i = 0; i < n; i++) {
            Food f = builder.foods.get(i);
            foodKcal[i]    = f.kcal().doubleValue();
            foodFat[i]     = f.fat().doubleValue();
            foodProtein[i] = f.protein().doubleValue();
            foodCarbs[i]   = f.carbs().doubleValue();
        }
    }

    public static class Builder {
        private int populationSize = 100;
        private int iterations = 1000;
        private double mutationRate = 0.3;
        private int maxDistinctFoods = 10;
        private int minDistinctFoods = 5;
        private int maxPortionsPerFood = 6;
        private int migrationInterval = -1;
        private BigDecimal kcalNorm;
        private BigDecimal fatNorm;
        private BigDecimal proteinNorm;
        private BigDecimal carbsNorm;
        private int kcalFactor = 1;
        private int fatFactor = 1;
        private int proteinFactor = 1;
        private int carbsFactor = 1;
        private int penaltyFactor = 10_000;
        private ArrayList<Food> foods;

        public Builder setPopulation(int population) {
            // 0 or fewer species
            if (population <= 0) {
                System.err.println("[Error] Wrong population value provided. Expected an integer above zero. Using default value.");
                return this;
            }

            this.populationSize = population;
            return this;
        }

        public Builder setIterations(int iterations) {
            // <= 0 iterations
            if (iterations <= 0) {
                System.err.println("[Error] Wrong iterations value provided. Expected an integer above zero. Using default value.");
                return this;
            }

            this.iterations = iterations;
            return this;
        }

        public Builder setMutationRate(double mutationRate) {
            // < 0% mutation
            if (mutationRate < 0) {
                System.err.println("[Error] Wrong mutation rate provided. Expected a double above or equal to zero. Using default value.");
                return this;
            }

            this.mutationRate = mutationRate;
            return this;
        }

        public Builder setMaxDistinctFoods(int restriction) {
            // 0 or fewer products to take
            if (restriction <= 0 && restriction != -1) {
                System.err.println("[Error] Wrong restriction value for food provided. Expected an integer above zero or -1. Using no max restriction.");
                return this;
            }

            this.maxDistinctFoods = restriction;
            return this;
        }

        public Builder setMinDistinctFoods(int restriction) {
            // 0 or fewer products to take
            if (restriction <= 0 && restriction != -1) {
                System.err.println("[Error] Wrong restriction value for food provided. Expected an integer above zero or -1. Using no min restriction.");
                return this;
            }

            this.minDistinctFoods = restriction;
            return this;
        }

        public Builder setMaxPortionsPerFood(int restriction) {
            // 0 or fewer for each gen
            if (restriction <= 0) {
                System.err.println("[Error] Wrong restriction value for gen provided. Expected an integer above zero. Using default value.");
                return this;
            }

            this.maxPortionsPerFood = restriction;
            return this;
        }

        public Builder setMigrationInterval(int amount) {
            if (amount <= 0 && amount != -1) {
                System.err.println("[Error] Wrong migration interval provided. Expected an integer above zero or -1. Using no migration mode.");
                return this;
            }

            this.migrationInterval = amount;
            return this;
        }

        public Builder setNorms(BigDecimal kcal, BigDecimal fat, BigDecimal protein, BigDecimal carbs) {
            // < 0 kcal
            if (kcal.compareTo(BigDecimal.ZERO) < 0) {
                System.err.println("[Error] Wrong kcal norm provided. Expected a BigDecimal above or equal to zero. Using default value.");
            } else {
                this.kcalNorm = kcal;
            }

            // < 0 fat
            if (fat.compareTo(BigDecimal.ZERO) < 0) {
                System.err.println("[Error] Wrong fat norm provided. Expected a BigDecimal above or equal to zero. Using default value.");
            } else {
                this.fatNorm = fat;
            }

            // < 0 protein
            if (protein.compareTo(BigDecimal.ZERO) < 0) {
                System.err.println("[Error] Wrong protein norm provided. Expected a BigDecimal above or equal to zero. Using default value.");
            } else {
                this.proteinNorm = protein;
            }

            // < 0 carbs
            if (carbs.compareTo(BigDecimal.ZERO) < 0) {
                System.err.println("[Error] Wrong carbs norm provided. Expected a BigDecimal above or equal to zero. Using default value.");
            } else {
                this.carbsNorm = carbs;
            }

            return this;
        }

        public Builder setFactors(int kcalFactor, int fatFactor, int proteinFactor, int carbsFactor) {
            this.kcalFactor = kcalFactor;
            this.fatFactor = fatFactor;
            this.proteinFactor = proteinFactor;
            this.carbsFactor = carbsFactor;
            return this;
        }

        public Builder setPenaltyFactor(int penaltyFactor) {
            this.penaltyFactor = penaltyFactor;
            return this;
        }

        public Builder setData(ArrayList<Food> foods) {
            this.foods = foods;
            return this;
        }

        public GenAlgorithm build() {
            return new GenAlgorithm(this);
        }
    }

    /**
     * Runs the algorithm for {@link #iterations} generations and returns the
     * chromosome of the best species found.
     * <p>
     * A one step could be described as:
     * 1. Reproduction
     * 2. Mutation
     * 3. Migration
     * 4. Selection
     *
     * @return the chromosome of the best species found
     */
    public int[] startSimulation() {
        ArrayList<Species> population = this.initializePopulation();

        for (int i = 0; i < iterations; i++) {
            // Adding children species to the population
            ArrayList<Species> children = this.reproduce(population);
            this.mutate(children);

            this.updateFitness(children);
            population.addAll(children);

            // Migration
            if (migrationInterval != -1 && i % migrationInterval == 0 && i != 0) {
                migration(population);
            }

            // After that, we are choosing only populationSize best
            population = this.selectNBest(population, populationSize);
        }

        return population.getFirst().getChromosome();
    }

    /**
     * Replaces 10% of worst species with a random ones.
     * @param population the current generation
     */
    private void migration(ArrayList<Species> population) {
        int immigrantCount = populationSize / 10;  // 10%
        for (int j = 0; j < immigrantCount; j++) {
            int[] chrom = new int[foods.size()];
            int activeCount = Math.min(maxDistinctFoods, foods.size());

            for (int k = 0; k < activeCount; k++) {
                int idx = rng.nextInt(foods.size());
                chrom[idx] = rng.nextInt(maxPortionsPerFood) + 1 ;
            }

            Species immigrant = new Species(chrom);
            immigrant.fitness = evaluate(immigrant);
            population.set(populationSize - 1 - j, immigrant);
        }
    }

    /**
     * Creates the initial population. Each chromosome is generated with at most
     * {@code maxDistinctFoods} active foods to reduce
     *
     * @return the initial population
     */
    private ArrayList<Species> initializePopulation() {
        ArrayList<Species> population = new ArrayList<>();

        for (int i = 0; i < populationSize; i++) {
            int[] chromosome = new int[foods.size()];

            // Generating valid not punished species
            int activeGenes = (maxDistinctFoods == -1)
                    ? Math.min(DEFAULT_ACTIVE_GENES, foods.size())
                    : Math.min(maxDistinctFoods, foods.size());
            for (int j = 0; j < activeGenes; j++) {
                int ind = rng.nextInt(foods.size());
                // Collisions are allowed on purpose
                chromosome[ind] = rng.nextInt(maxPortionsPerFood + 1);
            }

            population.add(new Species(chromosome));
        }

        this.updateFitness(population);

        return population;
    }

    /**
     * Mutates each child independently with probability {@link #mutationRate}.
     * A mutation replaces one randomly chosen gene with a new value in
     * {@code [0, maxPortionsPerFood]}.
     *
     * @param children the offspring to potentially mutate
     */
    private void mutate(ArrayList<Species> children) {
        for (Species sp : children) {
            if (rng.nextDouble() < mutationRate) {
                sp.setGenAt(
                        rng.nextInt(foods.size()),
                        rng.nextInt(maxPortionsPerFood + 1)
                );
            }
        }
    }

    /**
     * Creates {@code populationSize / 2} offspring by selecting two
     * distinct parents randomly and combining them via {@link #uniformCrossover}.
     *
     * @param population the current generation
     * @return the offspring
     */
    private ArrayList<Species> reproduce(ArrayList<Species> population) {
        Species parentA;
        Species parentB;
        ArrayList<Species> children = new ArrayList<>();

        if (populationSize < 2) return children;

        for (int i = 0; i < populationSize / 2; i++) {
            parentA = population.get(rng.nextInt(populationSize));
            do {
                parentB = population.get(rng.nextInt(populationSize));
            } while (parentA == parentB);

            Species child = new Species(this.uniformCrossover(parentA, parentB));
            children.add(child);
        }

        return children;
    }

    /**
     * Produces a child chromosome by uniform crossover.
     *
     * @param parentA the first parent
     * @param parentB the second parent
     * @return their child
     */
    private int[] uniformCrossover(Species parentA, Species parentB) {
        if (foods.size() < 2) return new int[foods.size()];
        int[] childChromosome = new int[foods.size()];

        for (int i = 0; i < foods.size(); i++) {
            if (rng.nextBoolean()) {
                childChromosome[i] = parentA.getGenAt(i);
            } else {
                childChromosome[i] = parentB.getGenAt(i);
            }
        }

        return childChromosome;
    }

    /**
     * Updates {@link Species#fitness} for every species in the pool.
     *
     * @param pool to update fitness in
     */
    private void updateFitness(ArrayList<Species> pool) {
        for (Species sp : pool) {
            sp.fitness = evaluate(sp);
        }
    }

    /**
     * Returns the {@code n} species with the lowest fitness values.
     *
     * @param pool the species to choose from
     * @param n    how many species to take
     * @return the {@code n} best species
     */
    private ArrayList<Species> selectNBest(ArrayList<Species> pool, int n) {
        pool.sort(Comparator.comparingDouble(a -> a.fitness));
        return new ArrayList<>(pool.subList(0, n));
    }

    /**
     * Computes the fitness of a species. The formula is:
     * <p>
     *  f(sp) = kcalFactor     * (kcalNorm    - kcal(sp))^2
     *         + fatFactor     * (fatNorm     - fat(sp))^2
     *         + proteinFactor * (proteinNorm - protein(sp))^2
     *         + carbsFactor   * (carbsNorm   - carbs(sp))^2
     *         + penalty(sp)
     *
     * @param sp the species to evaluate
     * @return the fitness value
     */
    private double evaluate(Species sp) {
        // Getting total kcal, fat, protein, carbs of a species
        double kcal = 0.0;
        double fat = 0.0;
        double protein = 0.0;
        double carbs = 0.0;

        int[] chromosome = sp.getChromosome();

        for (int i = 0; i < foods.size(); i++) {
            int amount = chromosome[i];
            if (amount == 0) continue;

            kcal    += this.foodKcal[i]    * amount;
            fat     += this.foodFat[i]     * amount;
            protein += this.foodProtein[i] * amount;
            carbs   += this.foodCarbs[i]   * amount;
        }

        // Calculating the fitness
        double penalty = getPenalty(chromosome);

        double dKcal = Math.pow(kcalNorm.doubleValue() - kcal, 2);
        double dFat = Math.pow(fatNorm.doubleValue() - fat, 2);
        double dProtein = Math.pow(proteinNorm.doubleValue() - protein, 2);
        double dCarbs = Math.pow(carbsNorm.doubleValue() - carbs, 2);

        double err = dKcal * kcalFactor + dFat * fatFactor + dProtein * proteinFactor + dCarbs * carbsFactor;

        return err + penalty;
    }

    /**
     * Computes the penalty for violating the min/max distinct-food constraints.
     * Returns zero if the count is within bounds, or if both constraints are
     * disabled ({@code -1}).
     *
     * @param chromosome the chromosome to inspect
     * @return the total penalty value
     */
    private double getPenalty(int[] chromosome) {
        if (maxDistinctFoods == -1 && minDistinctFoods == -1) return 0;

        int distinctCount = 0;
        for (int gen : chromosome) if (gen != 0) distinctCount++;

        double penalty = 0;

        if (maxDistinctFoods != -1 && distinctCount > maxDistinctFoods) {
            penalty += (distinctCount - maxDistinctFoods) * penaltyFactor;
        }
        if (minDistinctFoods != -1 && distinctCount < minDistinctFoods) {
            penalty += (minDistinctFoods - distinctCount) * penaltyFactor;
        }
        return penalty;
    }
}

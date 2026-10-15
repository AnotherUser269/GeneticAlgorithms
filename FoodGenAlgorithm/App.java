import core.Food;
import core.Species;
import enums.SexType;
import utils.FoodParser;
import utils.NormCalculator;

import java.math.BigDecimal;
import java.util.ArrayList;

public class App {
    private static final int GRAMS_PER_PORTION = 100;

    // Constants
    private static final BigDecimal WEIGHT_KG = new BigDecimal("87");
    private static final BigDecimal HEIGHT_CM = new BigDecimal("173");
    private static final int AGE = 19;
    private static final SexType SEX = SexType.MALE;
    private static final BigDecimal ACTIVITY_FACTOR = new BigDecimal("1.5");
    private static final String FILE_PATH = "input2ru.txt";

    public static void main(String[] args) {
        // Calculating norms
        BigDecimal kcalNorm = NormCalculator.getCaloriesNorm(WEIGHT_KG, HEIGHT_CM, AGE, SEX, ACTIVITY_FACTOR);
        BigDecimal fatNorm = NormCalculator.getFatNorm(kcalNorm);
        BigDecimal proteinNorm = NormCalculator.getProteinNorm(kcalNorm);
        BigDecimal carbsNorm = NormCalculator.getCarbsNorm(kcalNorm);

        System.out.printf("""
                        (i) Calculated norms:
                        - Calories: %s kcal
                        - Fats: %s g
                        - Proteins: %s g
                        - Carbohydrates: %s g
                        %s%n""",
                kcalNorm, fatNorm, proteinNorm, carbsNorm, "~".repeat(30));

        // Getting data from a file
        ArrayList<Food> foods = FoodParser.getFromTxt(FILE_PATH);

        // Starting a genetic algorithm
        GenAlgorithm genAlgorithm = new GenAlgorithm.Builder()
                .setPopulation(2000)
                .setIterations(10000)
                .setMutationRate(0.7)
                .setMaxDistinctFoods(10)   // more than N distinct foods is penalized
                .setMinDistinctFoods(5)    // fewer than N distinct foods is penalized
                .setMaxPortionsPerFood(4)
                .setMigrationInterval(500)
                .setNorms(kcalNorm, fatNorm, proteinNorm, carbsNorm)
                .setFactors(2, 8, 15, 4)
                .setPenaltyFactor(10000)
                .setData(foods)
                .build();

        Species best = genAlgorithm.startSimulation();
        int[] chrOfBest = getChromosome(best);

        // Outputting the result
        int counter = 0;

        BigDecimal currKcal = BigDecimal.ZERO;
        BigDecimal currFat = BigDecimal.ZERO;
        BigDecimal currProtein = BigDecimal.ZERO;
        BigDecimal currCarbs = BigDecimal.ZERO;

        System.out.println("(i) The algorithm decided to choose:");
        for (int i = 0; i < chrOfBest.length; i++) {
            if (chrOfBest[i] != 0) {
                counter++;

                Food curFood = foods.get(i);
                int curAmount = chrOfBest[i];

                System.out.printf("%d. %s - %s g%n",
                        counter, curFood.label(), curAmount * GRAMS_PER_PORTION
                );

                currKcal = currKcal.add(curFood.kcal().multiply(BigDecimal.valueOf(curAmount)));
                currFat = currFat.add(curFood.fat().multiply(BigDecimal.valueOf(curAmount)));
                currProtein = currProtein.add(curFood.protein().multiply(BigDecimal.valueOf(curAmount)));
                currCarbs = currCarbs.add(curFood.carbs().multiply(BigDecimal.valueOf(curAmount)));
            }
        }
        if (counter == 0) {
            System.out.println("Nothing.");
        }
        System.out.println("~".repeat(30));

        System.out.printf("""
                        [RESULT] The algorithm's total result:
                        - Calories: %s kcal
                        - Fats: %s g
                        - Proteins: %s g
                        - Carbohydrates: %s g
                        %n""",
                currKcal, currFat, currProtein, currCarbs);
    }

    private static int[] getChromosome(Species sp) {
        int length = sp.getChromosomeSize();
        int[] chr = new int[length];

        for (int i = 0; i < length; i++) {
            chr[i] = sp.getGenAt(i);
        }

        return chr;
    }
}

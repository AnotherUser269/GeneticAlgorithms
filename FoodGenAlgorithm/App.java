import core.Food;
import enums.SexType;
import utils.FoodParser;
import utils.NormCalculator;

import java.math.BigDecimal;
import java.util.ArrayList;

public class App {
    // Constants
    private static final BigDecimal WEIGHT_KG       =   new BigDecimal("87");
    private static final BigDecimal HEIGHT_CM       =   new BigDecimal("173");
    private static final int AGE                    =   19;
    private static final SexType SEX                =   SexType.MALE;
    private static final BigDecimal ACTIVITY_FACTOR =   new BigDecimal("1.5");
    public static final String FILE_PATH            =   "input.txt";

    public static void main(String[] args) {
        // Calculating norms
        BigDecimal ccalNorm = NormCalculator.getCaloriesNorm(WEIGHT_KG, HEIGHT_CM, AGE, SEX, ACTIVITY_FACTOR);
        BigDecimal fatNorm = NormCalculator.getFatNorm(ccalNorm);
        BigDecimal proteinNorm = NormCalculator.getProteinNorm(ccalNorm);
        BigDecimal carbsNorm = NormCalculator.getCarbsNorm(ccalNorm);

        System.out.printf("""
                (i) Calculated norms:
                - Calories: %s ccal
                - Fats: %s g
                - Proteins: %s g
                - Carbohydrates: %s g
                %s%n""",
                ccalNorm, fatNorm, proteinNorm, carbsNorm, "~".repeat(30));

        // Getting data from a file
        ArrayList<Food> food = FoodParser.getFromTxt(FILE_PATH);

        // Starting a genetic algorithm
        GenAlgorithm genAlgorithm = new GenAlgorithm.Builder()
                .setPopulation(2000)
                .setIterations(30000)
                .setMutationRate(0.12)
                .setProductAmountRestriction(12)    // > N products will punish the algorithm
                .setGenValueRestriction(3)
                .setNorms(ccalNorm, fatNorm, proteinNorm, carbsNorm)
                .setFactors(5,5,10,5)
                .setPenaltyFactor(10000)
                .setData(food)
                .build();

        int[] best = genAlgorithm.startSimulation();

        // Outputting the result
        int counter = 0;

        BigDecimal currCcal = BigDecimal.ZERO;
        BigDecimal currFat = BigDecimal.ZERO;
        BigDecimal currProtein = BigDecimal.ZERO;
        BigDecimal currCarbs = BigDecimal.ZERO;

        System.out.println("(i) The algorithm decided to choose:");
        for(int i=0; i< best.length; i++) {
            if (best[i] != 0) {
                counter++;

                Food curFood = food.get(i);
                int curAmount = best[i];

                System.out.printf("%d. %s - %s g%n",
                        counter, curFood.label(), curAmount * 100
                );

                currCcal = currCcal.add(curFood.kcal().multiply(BigDecimal.valueOf(curAmount)));
                currFat = currFat.add(curFood.fat().multiply(BigDecimal.valueOf(curAmount)));
                currProtein = currProtein.add(curFood.protein().multiply(BigDecimal.valueOf(curAmount)));
                currCarbs = currCarbs.add(curFood.carbs().multiply(BigDecimal.valueOf(curAmount)));
            }
        }
        System.out.println("~".repeat(30));

        System.out.printf("""
                [RESULT] The algorithm's total result:
                - Calories: %s ccal
                - Fats: %s g
                - Proteins: %s g
                - Carbohydrates: %s g
                %n""",
                currCcal, currFat, currProtein, currCarbs);
    }
}

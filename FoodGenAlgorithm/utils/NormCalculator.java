package utils;

import enums.SexType;
import java.math.BigDecimal;
import java.math.RoundingMode;

public final class NormCalculator {
    private NormCalculator() {}

    private static final BigDecimal PROTEIN_SHARE = new BigDecimal("0.15");
    private static final BigDecimal FAT_SHARE     = new BigDecimal("0.30");
    private static final BigDecimal CARBS_SHARE   = new BigDecimal("0.55");

    private static final BigDecimal CALORIES_PER_G_PROTEIN = new BigDecimal("4.0");
    private static final BigDecimal CALORIES_PER_G_FAT     = new BigDecimal("9.0");
    private static final BigDecimal CALORIES_PER_G_CARBS   = new BigDecimal("4.0");

    /**
     * Calculates daily norm of calories
     * using Mifflin formula and activity factor:
     * <p>
     * M: (10*weight+6.25*height-5*age+5)*actFactor
     * F: (10*weight+6.25*height-5*age-161)*actFactor
     * <p>
     * Params:
     *      BigDecimal weight - weight in kg
     *      BigDecimal height - height in cm
     *      int age - age of a person
     *      SexType sType - Male or Female
     *      BigDecimal actFactor - an activity factor
     * Returns:
     *      BigDecimal - Daily norm of calories
     */
    public static BigDecimal getCaloriesNorm(BigDecimal weight, BigDecimal height, int age, SexType sex, BigDecimal actFactor) {
        // Mifflin formula
        return new BigDecimal("10").multiply(weight)
                .add(new BigDecimal("6.25").multiply(height))
                .subtract(new BigDecimal("5").multiply(BigDecimal.valueOf(age)))
                .add(switch(sex) {
                    case MALE -> new BigDecimal("5");
                    case FEMALE -> new BigDecimal("-161");
                })
                .multiply(actFactor);
    }

    /* Get a protein norm in g. 15% of provided calories. */
    public static BigDecimal getProteinNorm(BigDecimal calories) {
        return calories.multiply(PROTEIN_SHARE)
                .divide(CALORIES_PER_G_PROTEIN, 2, RoundingMode.HALF_UP);
    }

    /* Get a fat norm in g. 30% of provided calories. */
    public static BigDecimal getFatNorm(BigDecimal calories) {
        return calories.multiply(FAT_SHARE)
                .divide(CALORIES_PER_G_FAT, 2, RoundingMode.HALF_UP);
    }

    /* Get a carbohydrates norm. 55% of provided calories */
    public static BigDecimal getCarbsNorm(BigDecimal calories) {
        return calories.multiply(CARBS_SHARE)
                .divide(CALORIES_PER_G_CARBS, 2, RoundingMode.HALF_UP);
    }
}
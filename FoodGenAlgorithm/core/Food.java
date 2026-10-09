package core;

import java.math.BigDecimal;

/**
 * Immutable nutrition data for a single food object.
 * <p>
 * All values are per 100g of the food, as is standard on nutrition labels.
 *
 * @param label   the food's name
 * @param kcal    calories (kcal) per 100g
 * @param fat     fat (g) per 100g
 * @param protein protein (g) per 100g
 * @param carbs   carbohydrates (g) per 100g
 */
public record Food(String label, BigDecimal kcal, BigDecimal fat, BigDecimal protein, BigDecimal carbs) {
    public Food {
        if (label == null || label.isBlank()) throw new IllegalArgumentException("Label required.");

        if (kcal.signum() < 0 || fat.signum() < 0 || protein.signum() < 0 || carbs.signum() < 0) {
            throw new IllegalArgumentException("Nutrition values cannot be negative.");
        }
    }
}

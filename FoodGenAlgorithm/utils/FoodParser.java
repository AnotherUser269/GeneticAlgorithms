package utils;

import core.Food;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Scanner;

public final class FoodParser {
    private FoodParser() {}

    /**
     * Reads a file where each non-blank line has the format:
     *   [label words...] [kcal] [fat] [protein] [carbs]
     * Numeric values are per 100g of the food.
     *
     * @param filePath path to the input file
     * @return list of parsed Food entries
     */
    public static ArrayList<Food> getFromTxt(String filePath) {
        ArrayList<Food> food = new ArrayList<>();

        File file = new File(filePath);
        try (Scanner sc = new Scanner(file)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (line.isBlank()) continue;

                String[] data = line.trim().split("\\s+");
                Food newFood = getFood(data);

                food.add(newFood);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException("File not found: " + filePath, e);
        }

        return food;
    }

    /**
     * Returns a food record object based on a split line parts.
     *
     * @param data an array of split line parts
     * @return Food record
     */
    private static Food getFood(String[] data) {
        if (data.length < 5) {
            throw new IllegalArgumentException(
                    "Bad line (need label + 4 numbers): " + String.join(" ", data)
            );
        }

        // Building label
        StringBuilder label = new StringBuilder(data[0]);
        for(int i = 1; i< data.length-4; i++) {
            label.append(' ');
            label.append(data[i]);
        }

        // Kcal fat protein carbs
        BigDecimal kcal      = new BigDecimal(data[data.length-4]);
        BigDecimal fat       = new BigDecimal(data[data.length-3]);
        BigDecimal protein   = new BigDecimal(data[data.length-2]);
        BigDecimal carbs     = new BigDecimal(data[data.length-1]);

        return new Food(label.toString(), kcal, fat, protein, carbs);
    }
}

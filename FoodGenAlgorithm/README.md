# FoodGenAlgorithm

Finds a daily diet matching given calorie/fat/protein/carb norms, using a genetic algorithm over a list of foods.

## How to run
1. Download the project
```
git clone github.com/AnotherUser269/GeneticAlgorithms/FoodGenAlgorithm
```
2. Open App.java in your IDE and change constants (weight, height, name of the input file, etc).
3. Setup your algorithm, if you want
4. Run the App.java

## How it works
1. Each gene in a chromosome represents a number of 100g portions of foods[i]. 0 means, that algorithm decided not to take this food.
2. Fitness is evaluated with the next formula:
```
f(sp) = kcalFactor * (kcalNorm - kcal(sp))^2 + fatFactor * (fatNorm - fat(sp))^2 + proteinFactor * (proteinNorm - protein(sp))^2 + carbsFactor * (carbsNorm - carbs(sp))^2 + penalty(sp)
```
3. Penalty is added if the chromosome violates the distinct-food constraints.
4. Uniform crossover.
5. Three-way mutation.
6. Periodic migration.

You can learn more about each step from JavaDocs.

## Example
For the next configuration:

```
GenAlgorithm genAlgorithm = new GenAlgorithm.Builder()
                .setRandomSeed(12345)
                .setPopulation(1000)
                .setIterations(10000)
                .setMutationRate(0.7)
                .setMaxDistinctFoods(10) 
                .setMinDistinctFoods(5) 
                .setMaxPortionsPerFood(4)
                .setMigrationInterval(500)
                .setNorms(kcalNorm, fatNorm, proteinNorm, carbsNorm)
                .setFactors(2, 8, 15, 4)
                .setPenaltyFactor(10000)
                .setData(foods)
                .build();
```

The output:

```
(i) Calculated norms:
- Calories: 2791.875 kcal
- Fats: 93.06 g
- Proteins: 104.70 g
- Carbohydrates: 383.88 g
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
(i) The algorithm decided to choose:
1. Крабовые палочки Снежный краб Ашан - 300 g
2. Булочка Кайзерка Ашан - 400 g
3. Биточки Картофельные Перекресток - 300 g
4. Сэндвич Крок-месье ВкусВилл - 300 g
5. Данонино Пауч Данон - 300 g
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
[RESULT] The algorithm's total result:
- Calories: 2790.3 kcal
- Fats: 93.2 g
- Proteins: 103.6 g
- Carbohydrates: 384.5 g
```

# What's next
You can add requirements for water, vitamins, and so on. It is also possible to add an automatic classification of the product by type (fruit, vegetables, etc.). However, at the moment, the project fulfills its task and is considered a complete example.
package core;

import java.math.BigDecimal;

public record Food(String label, BigDecimal ccal, BigDecimal fat, BigDecimal protein, BigDecimal carbs) { }

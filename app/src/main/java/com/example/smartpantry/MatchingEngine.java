package com.example.smartpantry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Core business logic: STRICT matching.
 * A recipe is suggested only if EVERY ingredient exists in the pantry
 * in at least the required quantity (after name and unit normalisation).
 */
public class MatchingEngine {

    /** tomatoes -> tomato, berries -> berry, eggs -> egg, but keeps "grass"/"molasses". */
    public static String normaliseName(String raw) {
        String s = raw.trim().toLowerCase().replaceAll("\\s+", " ");
        if (s.endsWith("oes")) return s.substring(0, s.length() - 2);
        if (s.endsWith("ies")) return s.substring(0, s.length() - 3) + "y";
        if (s.endsWith("s") && !s.endsWith("ss")) return s.substring(0, s.length() - 1);
        return s;
    }

    /** mass / volume / count. Units from different families can never match. */
    public static String unitFamily(String unit) {
        switch (unit.trim().toLowerCase()) {
            case "g": case "kg": return "mass";
            case "ml": case "l": return "volume";
            default: return "count";
        }
    }

    /** Convert to base unit: grams, millilitres, or pieces. */
    public static double toBase(double qty, String unit) {
        switch (unit.trim().toLowerCase()) {
            case "kg": case "l": return qty * 1000;
            default: return qty;
        }
    }

    private static String key(String name, String unit) {
        return normaliseName(name) + "|" + unitFamily(unit);
    }

    /** Sum pantry quantities per (normalised name, unit family) so duplicates combine. */
    private static Map<String, Double> buildStock(List<PantryItem> pantry) {
        Map<String, Double> stock = new HashMap<>();
        for (PantryItem p : pantry) {
            String k = key(p.name, p.unit);
            Double current = stock.get(k);
            stock.put(k, (current == null ? 0 : current) + toBase(p.quantity, p.unit));
        }
        return stock;
    }

    /** True only if every ingredient is available in sufficient quantity. */
    public static boolean canMake(Recipe recipe, Map<String, Double> stock) {
        return missingCount(recipe, stock) == 0;
    }

    /** How many ingredients are missing or insufficient (used for the optional "Almost There" list). */
    public static int missingCount(Recipe recipe, Map<String, Double> stock) {
        int missing = 0;
        for (RecipeIngredient ing : recipe.ingredients) {
            Double have = stock.get(key(ing.name, ing.unit));
            if (have == null || have < toBase(ing.quantity, ing.unit)) missing++;
        }
        return missing;
    }

    public static List<Recipe> getSuggested(List<Recipe> recipes, List<PantryItem> pantry) {
        Map<String, Double> stock = buildStock(pantry);
        List<Recipe> result = new ArrayList<>();
        for (Recipe r : recipes) if (canMake(r, stock)) result.add(r);
        return result;
    }

    /** Optional stretch: recipes missing exactly ONE ingredient. Show separately from strict list. */
    public static List<Recipe> getAlmostThere(List<Recipe> recipes, List<PantryItem> pantry) {
        Map<String, Double> stock = buildStock(pantry);
        List<Recipe> result = new ArrayList<>();
        for (Recipe r : recipes) if (missingCount(r, stock) == 1) result.add(r);
        return result;
    }
}

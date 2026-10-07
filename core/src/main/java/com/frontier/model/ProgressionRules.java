package com.frontier.model;

/** Kleine levelbeloning, onafhankelijk van hoeveel cash of bankgeld je bezit. */
public final class ProgressionRules {
    public static final int FIRST_LEVEL_BONUS = 5;
    public static final int BONUS_GROWTH_PERCENT = 10;
    public static final int MAX_LEVEL_BONUS = 50;
    private ProgressionRules() {}
    public static int bonusForLevel(int level) {
        if (level < 2) return 0;
        return (int) Math.min(MAX_LEVEL_BONUS, Math.round(FIRST_LEVEL_BONUS * Math.pow(1 + BONUS_GROWTH_PERCENT / 100.0, level - 2)));
    }
    public static int bonusBetween(int previous, int next) {
        int total = 0;
        for (int level = previous + 1; level <= next; level++) total = Math.addExact(total, bonusForLevel(level));
        return total;
    }
}

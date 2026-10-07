package com.frontier.model;

public record Job(String id, Location location, String name, int basePay, int payVariation,
                  int staminaCost, int minutes, int xp) {
    public Job {
        if (id == null || id.isBlank() || location == null || name == null || name.isBlank()
            || basePay < payVariation || payVariation < 0 || staminaCost <= 0
            || staminaCost > 100 || minutes <= 0 || xp <= 0)
            throw new IllegalArgumentException("Ongeldige werkzaamheid.");
    }
}

package com.frontier.model;

public record Job(String id, Location location, String name, int basePay, int payVariation,
                  int staminaCost, int minutes, int xp) {
    public Job {
        if (id == null || id.isBlank() || location == null || name == null || name.isBlank()
            || basePay < payVariation || payVariation < 0 || staminaCost <= 0
            || staminaCost > 100 || minutes <= 0 || xp <= 0)
            throw new IllegalArgumentException("Ongeldige werkzaamheid.");
    }
    /** Betaling en energie zijn gebaseerd op de referentieduur van deze klus. */
    public int scaledPay(int pay, WorkDuration duration) { return pay == 0 ? 0 : Math.max(1, (int) ((long) pay * duration.seconds() / (minutes * 60L))); }
    public int staminaCost(WorkDuration duration) { return Math.max(1, (int) Math.ceil(staminaCost * duration.seconds() / (minutes * 60.0))); }
    public int xp(WorkDuration duration) { return Math.max(1, (int) ((long) xp * duration.seconds() / (minutes * 60L))); }
}

package com.frontier.model;

/** Beschikbare echte wachttijden; dezelfde seconden worden aan de spelklok toegevoegd. */
public enum WorkDuration {
    QUICK("15 seconden", 15), SHORT("10 minuten", 10 * 60), LONG("1 uur", 60 * 60);
    private final String label;
    private final int seconds;
    WorkDuration(String label, int seconds) { this.label = label; this.seconds = seconds; }
    public String displayName() { return label; }
    public int seconds() { return seconds; }
}

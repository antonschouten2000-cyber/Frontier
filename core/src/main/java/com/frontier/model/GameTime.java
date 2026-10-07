package com.frontier.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class GameTime {
    public static final LocalDateTime START = LocalDateTime.of(1880, 4, 1, 8, 0);
    private static final DateTimeFormatter DISPLAY = DateTimeFormatter.ofPattern("d MMMM uuuu  HH:mm", Locale.forLanguageTag("nl-NL"));
    private LocalDateTime value;
    public GameTime() { this(START); }
    public GameTime(LocalDateTime value) {
        if (value == null || value.isBefore(START) || value.getYear() > 9999)
            throw new IllegalArgumentException("Ongeldige speldatum.");
        this.value = value;
    }
    public LocalDateTime value() { return value; }
    public String display() { return value.format(DISPLAY); }
    public void advanceMinutes(int minutes) {
        if (minutes < 0) throw new IllegalArgumentException("De tijd kan niet achteruitgaan.");
        value = value.plusMinutes(minutes);
    }
}

package com.frontier.model;

import java.time.LocalDateTime;
import java.util.Objects;

/** Vast verslag van een afgeronde klus, inclusief de werkelijk toegekende beloningen. */
public record Telegram(String jobName, Location location, int durationSeconds, int money, int xp,
                       Item found, LocalDateTime dateTime, boolean read) {
    public Telegram {
        if (jobName == null || jobName.isBlank() || jobName.length() > 120 || durationSeconds <= 0 || money < 0 || xp < 0)
            throw new IllegalArgumentException("Ongeldig telegram.");
        Objects.requireNonNull(location); new GameTime(dateTime);
    }
    public Telegram markRead() { return new Telegram(jobName, location, durationSeconds, money, xp, found, dateTime, true); }
}

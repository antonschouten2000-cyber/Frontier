package com.frontier.model;

import java.util.Objects;

/** Een echte werktimer; tijdstempels worden ook in de save bewaard. */
public record ActiveWork(Job job, WorkDuration duration, long startedAt, long endsAt) {
    public ActiveWork {
        Objects.requireNonNull(job); Objects.requireNonNull(duration);
        if (startedAt < 0 || endsAt <= startedAt || endsAt - startedAt != duration.seconds() * 1000L)
            throw new IllegalArgumentException("Ongeldige werktimer.");
    }
    public long remainingMillis(long now) { return Math.max(0, endsAt - now); }
    public float progress(long now) { return Math.max(0, Math.min(1, (now - startedAt) / (float) (endsAt - startedAt))); }
}

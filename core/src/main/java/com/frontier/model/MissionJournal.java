package com.frontier.model;

import java.util.EnumMap;
import java.util.Map;

public final class MissionJournal {
    public record Entry(int progress, boolean claimed) {}
    private final EnumMap<Mission, Entry> entries = new EnumMap<>(Mission.class);
    public Entry entry(Mission mission) { return entries.get(mission); }
    public Map<Mission, Entry> entries() { return Map.copyOf(entries); }
    public void restore(Mission mission, int progress, boolean claimed) {
        if (progress < 0 || progress > mission.target() || claimed && progress != mission.target() || entries.containsKey(mission))
            throw new IllegalArgumentException("Ongeldige opdrachtvoortgang.");
        entries.put(mission, new Entry(progress, claimed));
    }
    public void accept(Mission mission) { if (!entries.containsKey(mission)) entries.put(mission, new Entry(0, false)); }
    public void completedWork(Location location) {
        for (Mission mission : Mission.values()) {
            Entry entry = entries.get(mission);
            if (entry != null && !entry.claimed() && mission.location() == location)
                entries.put(mission, new Entry(Math.min(mission.target(), entry.progress()+1), false));
        }
    }
    public void claim(Mission mission) { entries.put(mission, new Entry(mission.target(), true)); }
}

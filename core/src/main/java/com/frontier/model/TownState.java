package com.frontier.model;

import java.util.EnumMap;
import java.util.Map;

/** Red Creek begint met vijf gebouwen op level 1. */
public final class TownState {
    private final EnumMap<Building, Integer> levels = new EnumMap<>(Building.class);
    public TownState() { for (Building building : Building.values()) levels.put(building, 1); }
    public TownState(Map<Building, Integer> saved) {
        this();
        if (saved == null || saved.size() != Building.values().length) throw new IllegalArgumentException("Gebouwlevels ontbreken.");
        for (Building building : Building.values()) {
            Integer level = saved.get(building);
            if (level == null || level < 1 || level > Building.MAX_LEVEL) throw new IllegalArgumentException("Ongeldig gebouwlevel.");
            levels.put(building, level);
        }
        for (Building building : Building.values())
            if (level(building) > level(Building.TOWN_HALL)) throw new IllegalArgumentException("Gebouw is hoger dan het stadhuis.");
    }
    public int level(Building building) { return levels.get(building); }
    public Map<Building, Integer> levels() { return Map.copyOf(levels); }
    public void upgrade(Building building) {
        int current = level(building);
        if (current >= Building.MAX_LEVEL || building != Building.TOWN_HALL && current >= level(Building.TOWN_HALL))
            throw new IllegalArgumentException("Dit gebouw kan niet worden uitgebreid.");
        levels.put(building, current + 1);
    }
    public int bankCapacity() { int level = level(Building.BANK); return 500 * level * level; }
    public int sleepMinutes() {
        double[] factors = {1, .75, .5, .375, .25};
        return Math.max(1, (int) Math.round(TimeRules.SLEEP_HOURS * 60 * factors[level(Building.INN) - 1]));
    }
}

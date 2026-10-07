package com.frontier.logic;

import com.frontier.model.*;

/** Bouwmateriaal is een vaste werkopbrengst; extra vondsten blijven willekeurig. */
public final class WorkMaterials {
    private WorkMaterials() {}
    public static Item item(Job job) {
        return switch (job.location()) {
            case PINE_FOREST, NORTH_WOODS -> Item.WOOD;
            case OLD_MINE -> Item.ORE;
            case QUARRY -> Item.STONE;
            case COTTON_FARM -> Item.COTTON;
            default -> null;
        };
    }
    public static int quantity(Job job, WorkDuration duration) {
        return item(job) == null ? 0 : switch (duration) { case QUICK -> 1; case SHORT -> 3; case LONG -> 12; };
    }
}

package com.frontier.logic;

import com.frontier.model.Item;
import com.frontier.model.Location;
import com.frontier.model.WorkDuration;
import java.util.Optional;
import java.util.random.RandomGenerator;

/** Per uur werk 30% vondstkans: producten zijn gewoner dan kleding en wapens. */
public final class LootManager {
    public static final int FIND_CHANCE_PERCENT = 30;
    private final RandomGenerator random;
    public LootManager(RandomGenerator random) { this.random = random; }
    public static double chancePercent(WorkDuration duration) {
        return Math.min(100, FIND_CHANCE_PERCENT * duration.seconds() / 3600.0);
    }
    public Optional<Item> find(Location location) { return find(location, WorkDuration.LONG); }
    public Optional<Item> find(Location location, WorkDuration duration) {
        boolean found = duration.seconds() == 3600 ? random.nextInt(100) < FIND_CHANCE_PERCENT
            : random.nextInt(360000) < Math.min(360000L, FIND_CHANCE_PERCENT * (long) duration.seconds());
        if (!found) return Optional.empty();
        int category = random.nextInt(100);
        if (category < 60) return Optional.of(switch (location) {
            case PINE_FOREST, NORTH_WOODS -> Item.WOOD;
            case OLD_MINE -> Item.ORE;
            case QUARRY -> Item.STONE;
            case COTTON_FARM -> Item.COTTON;
            default -> Item.COFFEE;
        });
        Item[] pool = category < 90 ? new Item[]{Item.HAT, Item.BOOTS, Item.COAT}
                                    : new Item[]{Item.REVOLVER, Item.RIFLE, Item.KNIFE};
        return Optional.of(pool[random.nextInt(pool.length)]);
    }
}

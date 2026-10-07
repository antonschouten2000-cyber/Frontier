package com.frontier.logic;

import com.frontier.model.Item;
import com.frontier.model.Location;
import java.util.Optional;
import java.util.random.RandomGenerator;

/** Per afgeronde klus 30% kans: producten zijn gewoner dan kleding en wapens. */
public final class LootManager {
    public static final int FIND_CHANCE_PERCENT = 30;
    private final RandomGenerator random;
    public LootManager(RandomGenerator random) { this.random = random; }
    public Optional<Item> find(Location location) {
        if (random.nextInt(100) >= FIND_CHANCE_PERCENT) return Optional.empty();
        int category = random.nextInt(100);
        if (category < 60) return Optional.of(switch (location) {
            case PINE_FOREST -> Item.WOOD;
            case OLD_MINE -> Item.ORE;
            default -> Item.COFFEE;
        });
        Item[] pool = category < 90 ? new Item[]{Item.HAT, Item.BOOTS, Item.COAT}
                                    : new Item[]{Item.REVOLVER, Item.RIFLE, Item.KNIFE};
        return Optional.of(pool[random.nextInt(pool.length)]);
    }
}

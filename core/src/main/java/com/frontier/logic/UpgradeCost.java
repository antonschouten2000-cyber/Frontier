package com.frontier.logic;

import com.frontier.model.*;
import java.util.EnumMap;
import java.util.Map;

public record UpgradeCost(int money, Map<Item, Integer> materials) {
    public UpgradeCost { materials = Map.copyOf(materials); }
    public static UpgradeCost forBuilding(Building building, int currentLevel) {
        if (currentLevel < 1 || currentLevel >= Building.MAX_LEVEL) throw new IllegalArgumentException("Geen upgrade beschikbaar.");
        int scale = currentLevel * currentLevel;
        EnumMap<Item, Integer> materials = new EnumMap<>(Item.class);
        materials.put(Item.WOOD, 2 * scale); materials.put(Item.STONE, scale);
        if (building == Building.GUNSMITH) materials.put(Item.ORE, currentLevel);
        if (building == Building.TAILOR) materials.put(Item.COTTON, currentLevel);
        return new UpgradeCost(building.upgradePrice() * scale, materials);
    }
}

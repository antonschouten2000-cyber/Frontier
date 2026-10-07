package com.frontier.model;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/** Kleine gestapelde inventaris; zonder UI-afhankelijkheid. */
public final class Inventory {
    private final EnumMap<Item, Integer> contents = new EnumMap<>(Item.class);
    public void add(Item item) { add(item, 1); }
    public void add(Item item, int quantity) {
        Objects.requireNonNull(item);
        if (quantity <= 0) throw new IllegalArgumentException("Aantal moet positief zijn.");
        contents.put(item, Math.addExact(count(item), quantity));
    }
    public void remove(Item item, int quantity) {
        Objects.requireNonNull(item);
        if (quantity <= 0 || count(item) < quantity) throw new IllegalArgumentException("Te weinig voorwerpen.");
        int remaining = count(item) - quantity;
        if (remaining == 0) contents.remove(item); else contents.put(item, remaining);
    }
    public int count(Item item) { return contents.getOrDefault(item, 0); }
    public long totalCount() { return contents.values().stream().mapToLong(Integer::longValue).sum(); }
    public Map<Item, Integer> contents() { return Collections.unmodifiableMap(contents); }
}

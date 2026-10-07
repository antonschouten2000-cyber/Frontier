package com.frontier.model;

import java.util.Objects;

public final class GameState {
    private final Player player;
    private final GameTime time;
    private Location location;
    private final Inventory inventory;
    public GameState() { this(new Player(), new GameTime(), Location.RED_CREEK); }
    public GameState(Player player, GameTime time, Location location) {
        this(player, time, location, new Inventory());
    }
    public GameState(Player player, GameTime time, Location location, Inventory inventory) {
        this.inventory = Objects.requireNonNull(inventory);
        this.player = Objects.requireNonNull(player);
        this.time = Objects.requireNonNull(time);
        this.location = Objects.requireNonNull(location);
        if (player.stamina() < location.travelStaminaTo(Location.RED_CREEK))
            throw new IllegalArgumentException("Te weinig energie voor de terugweg naar Red Creek.");
    }
    public Inventory inventory() { return inventory; }
    public Player player() { return player; }
    public GameTime time() { return time; }
    public Location location() { return location; }
    public void moveTo(Location location) { this.location = Objects.requireNonNull(location); }
}

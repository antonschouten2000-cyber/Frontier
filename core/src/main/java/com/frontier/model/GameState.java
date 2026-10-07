package com.frontier.model;

import java.util.Objects;

public final class GameState {
    private final Player player;
    private final GameTime time;
    private Location location;
    private final Inventory inventory;
    private ActiveWork activeWork;
    private TownState town = new TownState();
    private final Mailbox mailbox = new Mailbox();
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
    public ActiveWork activeWork() { return activeWork; }
    public void setActiveWork(ActiveWork work) {
        if (work != null && work.job().location() != location)
            throw new IllegalArgumentException("Werklocatie komt niet overeen met de spelerlocatie.");
        activeWork = work;
    }
    public TownState town() { return town; }
    public void setTown(TownState town) {
        Objects.requireNonNull(town);
        if (player.bankMoney() > town.bankCapacity()) throw new IllegalArgumentException("Bankrekening boven de limiet.");
        this.town = town;
    }
    public Mailbox mailbox() { return mailbox; }
    public Inventory inventory() { return inventory; }
    public Player player() { return player; }
    public GameTime time() { return time; }
    public Location location() { return location; }
    public void moveTo(Location location) { this.location = Objects.requireNonNull(location); }
}

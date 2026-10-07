package com.frontier.model;

public final class Player {
    public static final int MAX_STAMINA = 100;
    private final String name;
    private int money, stamina, xp, level;

    public Player() { this("Reiziger", 20, 100, 0, 1); }
    public Player(String name, int money, int stamina, int xp, int level) {
        if (name == null || name.isBlank() || name.length() > 40 || money < 0 ||
            stamina < 0 || stamina > MAX_STAMINA || xp < 0 || level != levelForXp(xp))
            throw new IllegalArgumentException("Ongeldige spelergegevens.");
        this.name = name;
        this.money = money;
        this.stamina = stamina;
        this.xp = xp;
        this.level = level;
    }
    public static int levelForXp(int xp) {
        if (xp < 0) throw new IllegalArgumentException("Ervaring kan niet negatief zijn.");
        // Total XP thresholds: 50, 150, 300, 500, ...
        return (int) Math.floor((1 + Math.sqrt(1 + 8.0 * xp / 50)) / 2);
    }
    public long nextLevelXp() { return (long) level * (level + 1) * 25; }
    public String name() { return name; }
    public int money() { return money; }
    public int stamina() { return stamina; }
    public int xp() { return xp; }
    public int level() { return level; }
    public void spendStamina(int amount) {
        if (amount < 0 || amount > stamina) throw new IllegalArgumentException("Te weinig energie.");
        stamina -= amount;
    }
    public void reward(int dollars, int experience) {
        if (dollars < 0 || experience < 0) throw new IllegalArgumentException("Ongeldige beloning.");
        int newMoney = Math.addExact(money, dollars);
        int newXp = Math.addExact(xp, experience);
        money = newMoney;
        xp = newXp;
        level = levelForXp(xp);
    }
    public void rest() { stamina = MAX_STAMINA; }
}

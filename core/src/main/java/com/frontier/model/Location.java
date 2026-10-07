package com.frontier.model;

public enum Location {
    RED_CREEK("Red Creek", .22f, .35f),
    PINE_FOREST("Dennenbos", .28f, .75f),
    OLD_MINE("Oude mijn", .70f, .72f),
    LONELY_RANCH("Verlaten ranch", .80f, .25f);

    private final String displayName;
    private final float x, y;
    Location(String displayName, float x, float y) {
        this.displayName = displayName;
        this.x = x;
        this.y = y;
    }
    public String displayName() { return displayName; }
    public float x() { return x; }
    public float y() { return y; }
    public int travelStaminaTo(Location target) {
        if (this == target) return 0;
        return 4 + (int) Math.ceil(Math.hypot(x - target.x, y - target.y) * 24);
    }
    public int travelMinutesTo(Location target) { return travelStaminaTo(target) * 8; }
}

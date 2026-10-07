package com.frontier.model;

/** Wereldpixels; de bestaande streek blijft op haar oorspronkelijke schaal. */
public final class WorldMap {
    public static final int WIDTH = 1600, HEIGHT = 960;
    public static final int REGION_X = 400, REGION_Y = 240;
    public static final int REGION_WIDTH = 800, REGION_HEIGHT = 440;
    private WorldMap() {}
    public static float x(Location location) { return REGION_X + location.x() * REGION_WIDTH; }
    public static float y(Location location) { return HEIGHT - (REGION_Y + (1 - location.y()) * REGION_HEIGHT); }
}

package com.frontier.ui;

import com.badlogic.gdx.graphics.*;
import com.frontier.model.*;
import java.util.Random;

/** Meer terrein op 1:1-schaal; de oorspronkelijke streek wordt nooit uitvergroot. */
final class WorldArtwork {
    private final Pixmap p = new Pixmap(WorldMap.WIDTH, WorldMap.HEIGHT, Pixmap.Format.RGBA8888);
    private final Random random = new Random(1881);
    private WorldArtwork() {}
    static Texture create() {
        WorldArtwork art = new WorldArtwork(); art.paint();
        Texture texture = new Texture(art.p); art.p.dispose(); return texture;
    }
    private void color(String hex) { p.setColor(Color.valueOf(hex)); }
    private void paint() {
        color("c9b27e"); p.fill();
        for (int i = 0; i < 480; i++) {
            color(i % 2 == 0 ? "c3ac78" : "cfb987");
            ellipse(random.nextInt(WorldMap.WIDTH), random.nextInt(WorldMap.HEIGHT), 35 + random.nextInt(95), 12 + random.nextInt(30));
        }
        for (int i = 0; i < 260000; i++) {
            color(i % 2 == 0 ? "bda16d" : "d5bf8b"); p.drawPixel(random.nextInt(WorldMap.WIDTH), random.nextInt(WorldMap.HEIGHT));
        }
        color("bba16f");
        for (int x = 0; x < WorldMap.WIDTH; x += 80) p.drawLine(x, 0, x, WorldMap.HEIGHT - 1);
        for (int y = 0; y < WorldMap.HEIGHT; y += 80) p.drawLine(0, y, WorldMap.WIDTH - 1, y);
        river();
        // Verspreide bossen, heuvelruggen en boomgroepen vullen de hele wereld.
        for (int[] grove : new int[][]{{450, 480}, {960, 280}, {1700, 260}, {2670, 620}, {2700, 1420}, {600, 1450}, {1800, 1640}}) {
            color("a5a775"); ellipse(grove[0], grove[1], 230, 120);
            for (int i = 0; i < 190; i++) tree(grove[0] - 210 + random.nextInt(420), grove[1] - 100 + random.nextInt(200));
        }
        for (int i = 0; i < 400; i++) tree(30 + random.nextInt(WorldMap.WIDTH - 60), 30 + random.nextInt(WorldMap.HEIGHT - 60));
        for (int i = 0; i < 75; i++) {
            int x = 2150 + random.nextInt(850), y = 180 + random.nextInt(850);
            color("a18f6e"); p.fillTriangle(x, y, x - 18, y + 38, x + 23, y + 38);
            color("887e65"); p.fillTriangle(x, y, x + 5, y + 38, x + 23, y + 38);
        }
        for (int i = 0; i < 1800; i++) {
            int x = 15 + random.nextInt(WorldMap.WIDTH - 30), y = 15 + random.nextInt(WorldMap.HEIGHT - 30);
            color("8b965c"); p.drawLine(x, y, x - 2, y - 4); p.drawLine(x, y, x + 2, y - 5);
        }
        // Kopieer zonder schaling: huizen, bomen en mijn blijven dezelfde grootte.
        Pixmap region = MapArtwork.createRegion();
        try { p.drawPixmap(region, WorldMap.REGION_X, WorldMap.REGION_Y); }
        finally { region.dispose(); }
        for (Location location : Location.values()) {
            if (location.ordinal() <= Location.LONELY_RANCH.ordinal()) continue;
            int x = (int) WorldMap.x(location), y = (int) (WorldMap.HEIGHT - WorldMap.y(location));
            Location connection = switch (location) {
                case WILLOW_FARM, RIVER_FARM -> Location.PINE_FOREST;
                case SUNRISE_FARM, COTTON_FARM -> Location.LONELY_RANCH;
                case NORTH_WOODS, QUARRY -> Location.OLD_MINE;
                case TRADING_POST -> Location.WILLOW_FARM;
                default -> Location.RED_CREEK;
            };
            trail((int) WorldMap.x(connection), (int) (WorldMap.HEIGHT - WorldMap.y(connection)), x, y);
            switch (location) {
                case WILLOW_FARM, SUNRISE_FARM, RIVER_FARM, COTTON_FARM -> farm(x, y + 34, location.ordinal());
                case NORTH_WOODS -> { for (int i = 0; i < 100; i++) tree(x - 110 + random.nextInt(220), y - 35 + random.nextInt(150)); cabin(x - 45, y + 35, 44, 28); }
                case QUARRY -> quarry(x, y + 30);
                case RIVERBANK -> { color("715034"); p.fillRectangle(x - 55, y + 35, 85, 12); for (int n = -50; n < 30; n += 6) { color("b89562"); p.drawLine(x + n, y + 35, x + n, y + 47); } cabin(x + 35, y + 30, 31, 24); }
                case TRADING_POST -> { cabin(x - 50, y + 35, 58, 38); cabin(x + 30, y + 40, 38, 27); color("9a7951"); for (int n = 0; n < 4; n++) p.fillRectangle(x - 35 + n * 22, y + 85, 17, 14); }
                default -> { }
            }
        }
        // Kleine erven buiten de werkplekken maken de buitengebieden levendiger.
        for (int[] erf : new int[][]{{420, 880}, {1070, 1690}, {2920, 1100}, {2280, 1740}}) {
            Location nearby = nearestLocation(erf[0], erf[1]);
            trail(erf[0], erf[1], (int) WorldMap.x(nearby), (int) (WorldMap.HEIGHT - WorldMap.y(nearby)));
            farm(erf[0], erf[1], erf[0]);
        }
        for (Landmark site : Landmark.values()) {
            int y = WorldMap.HEIGHT - site.y();
            Location nearby = nearestLocation(site.x(), y);
            trail((int) WorldMap.x(nearby), (int) (WorldMap.HEIGHT - WorldMap.y(nearby)), site.x(), y);
            if (site.isFort()) fort(site.x(), y + 28); else ghostTown(site.x(), y + 22);
        }
        color("6f5436"); p.drawRectangle(3, 3, WorldMap.WIDTH - 6, WorldMap.HEIGHT - 6);
        p.drawRectangle(6, 6, WorldMap.WIDTH - 12, WorldMap.HEIGHT - 12);
    }
    private void river() {
        for (int y = 0; y < WorldMap.HEIGHT; y++) {
            int x = WorldMap.REGION_X + 381 + (int) (Math.sin((y - WorldMap.REGION_Y) / 55.0) * 40);
            color("98a382"); p.fillRectangle(x - 5, y, 28, 1);
            color("69908e"); p.fillRectangle(x, y, 17, 1);
            color("9ab7ab"); p.fillRectangle(x + 2, y, 3, 1);
        }
        // Twee zijrivieren door het boerenland, met riet langs de oevers.
        branch(0, 1280, WorldMap.REGION_X + 350, WorldMap.REGION_Y + 370);
        branch(WorldMap.REGION_X + 390, WorldMap.REGION_Y + 50, WorldMap.WIDTH, 490);
    }
    private void branch(int x0, int y0, int x1, int y1) {
        for (int x = x0; x < x1; x++) {
            double t = (x - x0) / (double) (x1 - x0);
            int y = (int) (y0 + (y1 - y0) * t + Math.sin(t * Math.PI * 3) * 42);
            color("98a382"); p.fillRectangle(x, y - 8, 1, 21);
            color("69908e"); p.fillRectangle(x, y - 4, 1, 12);
            color("9ab7ab"); p.drawPixel(x, y - 2);
            if (x % 37 == 0) { color("73865c"); p.drawLine(x, y + 15, x - 3, y + 9); }
        }
    }

    private void tree(int x, int y) {
        color("6f5437"); p.fillRectangle(x - 1, y + 10, 3, 8);
        color("536447"); p.fillTriangle(x, y - 12, x - 8, y + 14, x + 8, y + 14);
        color("6d7c50"); p.fillTriangle(x, y - 12, x - 8, y + 14, x, y + 14);
    }
    private void trail(int x0, int y0, int x1, int y1) {
        int steps = (int) Math.hypot(x1 - x0, y1 - y0);
        for (int i = 0; i <= steps; i++) {
            double t = i / (double) steps;
            int x = (int) (x0 + (x1 - x0) * t), y = (int) (y0 + (y1 - y0) * t + Math.sin(t * Math.PI) * 45);
            color("b69763"); p.fillCircle(x, y, 3);
            color("ddc591"); p.fillCircle(x, y, 2);
            if (i % 14 < 5) { color("a28151"); p.drawPixel(x, y); }
        }
    }
    private Location nearestLocation(int x, int y) {
        Location nearest = Location.RED_CREEK; double distance = Double.MAX_VALUE;
        for (Location location : Location.values()) {
            double candidate = Math.hypot(WorldMap.x(location) - x, WorldMap.HEIGHT - WorldMap.y(location) - y);
            if (candidate < distance) { distance = candidate; nearest = location; }
        }
        return nearest;
    }
    private void cabin(int x, int y, int w, int h) {
        color("967047"); p.fillRectangle(x, y, w, h);
        color("694931"); p.fillTriangle(x - 4, y, x + w / 2, y - 15, x + w + 4, y);
        color("bc965f"); p.fillTriangle(x - 4, y, x + w / 2, y - 15, x + w / 2, y);
        color("3e3328"); p.fillRectangle(x + w / 2 - 4, y + h - 14, 8, 14);
        color("dbc694"); p.fillRectangle(x + 7, y + 6, 7, 7);
    }
    private void farm(int x, int y, int seed) {
        color("b4a36c"); p.fillRectangle(x - 110, y - 12, 220, 122);
        color(seed % 2 == 0 ? "858553" : "a89665"); p.fillRectangle(x + 8, y + 40, 98, 65);
        for (int row = 43; row < 104; row += 8) {
            color("786139"); p.drawLine(x + 10, y + row, x + 103, y + row);
            color(seed % 2 == 0 ? "d4c98f" : "899353");
            for (int col = 14; col < 104; col += 7) p.fillRectangle(x + col, y + row - 3, 3, 3);
        }
        cabin(x - 88, y + 5, 48, 33); cabin(x - 82, y + 61, 39, 26);
        color("bfa04e"); p.fillCircle(x - 18, y + 66, 12); p.drawLine(x - 18, y + 55, x - 18, y + 76);
        color("6f5135"); for (int col = -112; col <= 112; col += 16) p.fillRectangle(x + col, y + 116, 3, 14);
        p.fillRectangle(x - 112, y + 120, 226, 2); p.fillRectangle(x - 112, y + 125, 226, 2);
        color("777653"); p.fillCircle(x - 16, y + 20, 8); color("413b2d"); p.fillCircle(x - 16, y + 20, 4);
    }
    private void quarry(int x, int y) {
        color("a6977a"); ellipse(x, y + 36, 110, 68);
        for (int i = 0; i < 30; i++) {
            int rx = x - 95 + random.nextInt(190), ry = y + random.nextInt(90);
            color(i % 2 == 0 ? "797867" : "c2baa0"); p.fillRectangle(rx, ry, 13 + random.nextInt(15), 7 + random.nextInt(10));
        }
        cabin(x - 107, y + 81, 34, 23);
    }
    private void ghostTown(int x, int y) {
        color("baa172"); ellipse(x, y + 16, 100, 50);
        color("d2b985"); p.fillRectangle(x - 86, y + 32, 172, 9);
        for (int i = 0; i < 6; i++) {
            int bx = x - 77 + (i % 3) * 56, by = y + (i / 3) * 49;
            color("88745a"); p.fillRectangle(bx, by, 31, 23);
            color("665641");
            if (i % 2 == 0) p.fillTriangle(bx - 2, by, bx + 15, by - 12, bx + 33, by);
            else { p.drawLine(bx, by - 2, bx + 31, by - 2); p.drawLine(bx, by - 2, bx + 10, by - 10); }
            color("343128"); p.fillRectangle(bx + 11, by + 8, 7, 15);
            color("bba57e"); p.drawLine(bx + 7, by + 7, bx + 22, by + 17); p.drawLine(bx + 7, by + 17, bx + 22, by + 7);
        }
        color("786a50"); p.fillCircle(x + 15, y + 35, 7); color("413b2e"); p.fillCircle(x + 15, y + 35, 3);
        color("7c684a"); for (int i = 0; i < 6; i++) p.drawLine(x - 93 + i * 34, y + 86, x - 89 + i * 34, y + 75);
    }
    private void fort(int x, int y) {
        color("b29968"); ellipse(x, y + 46, 92, 72);
        color("776046"); p.fillRectangle(x - 69, y, 138, 102);
        color("cab183"); p.fillRectangle(x - 61, y + 8, 122, 82);
        color("5a4733");
        for (int n = -68; n <= 68; n += 6) { p.fillRectangle(x + n, y - 4, 3, 12); p.fillRectangle(x + n, y + 93, 3, 12); }
        for (int n = 8; n < 93; n += 6) { p.fillRectangle(x - 72, y + n, 11, 3); p.fillRectangle(x + 61, y + n, 11, 3); }
        for (int tx : new int[]{x - 72, x + 54}) for (int ty : new int[]{y - 9, y + 86}) {
            color("9a7951"); p.fillRectangle(tx, ty, 21, 22);
            color("604732"); p.fillTriangle(tx - 3, ty, tx + 10, ty - 9, tx + 24, ty);
        }
        color("94754f"); p.fillRectangle(x - 35, y + 19, 69, 19); p.fillRectangle(x - 38, y + 45, 27, 21);
        color("644b32"); p.fillRectangle(x - 12, y + 92, 24, 12);
        color("41372a"); p.fillRectangle(x - 8, y + 92, 16, 12);
        color("66533b"); p.drawLine(x + 26, y + 65, x + 26, y + 37);
        color("b08b57"); p.fillTriangle(x + 27, y + 38, x + 45, y + 44, x + 27, y + 50);
    }
    private void ellipse(int cx, int cy, int rx, int ry) {
        for (int y = -ry; y <= ry; y++) {
            int x = (int) (rx * Math.sqrt(Math.max(0, 1 - y * y / (double) (ry * ry))));
            p.drawLine(cx - x, cy + y, cx + x, cy + y);
        }
    }
}

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
        for (int i = 0; i < 120; i++) {
            color(i % 2 == 0 ? "c3ac78" : "cfb987");
            ellipse(random.nextInt(WorldMap.WIDTH), random.nextInt(WorldMap.HEIGHT), 35 + random.nextInt(95), 12 + random.nextInt(30));
        }
        for (int i = 0; i < 65000; i++) {
            color(i % 2 == 0 ? "bda16d" : "d5bf8b"); p.drawPixel(random.nextInt(WorldMap.WIDTH), random.nextInt(WorldMap.HEIGHT));
        }
        color("bba16f");
        for (int x = 0; x < WorldMap.WIDTH; x += 80) p.drawLine(x, 0, x, WorldMap.HEIGHT - 1);
        for (int y = 0; y < WorldMap.HEIGHT; y += 80) p.drawLine(0, y, WorldMap.WIDTH - 1, y);
        river();
        // Noordelijke bossen en oostelijke rotsvelden voegen nieuw terrein toe.
        color("a5a775"); ellipse(630, 121, 210, 82);
        for (int i = 0; i < 150; i++) tree(425 + random.nextInt(405), 28 + random.nextInt(174));
        for (int i = 0; i < 18; i++) {
            int x = 1120 + random.nextInt(350), y = 350 + random.nextInt(240);
            color("a18f6e"); p.fillTriangle(x, y, x - 18, y + 38, x + 23, y + 38);
            color("887e65"); p.fillTriangle(x, y, x + 5, y + 38, x + 23, y + 38);
        }
        for (int i = 0; i < 450; i++) {
            int x = 15 + random.nextInt(WorldMap.WIDTH - 30), y = 15 + random.nextInt(WorldMap.HEIGHT - 30);
            color("8b965c"); p.drawLine(x, y, x - 2, y - 4); p.drawLine(x, y, x + 2, y - 5);
        }
        // Kopieer zonder schaling: huizen, bomen en mijn blijven dezelfde grootte.
        Pixmap region = MapArtwork.createRegion();
        try { p.drawPixmap(region, WorldMap.REGION_X, WorldMap.REGION_Y); }
        finally { region.dispose(); }
        for (Landmark site : Landmark.values()) {
            int y = WorldMap.HEIGHT - site.y();
            trail((int) WorldMap.x(site.x() < WorldMap.WIDTH / 2 ? Location.RED_CREEK : Location.OLD_MINE),
                (int) (WorldMap.HEIGHT - WorldMap.y(site.x() < WorldMap.WIDTH / 2 ? Location.RED_CREEK : Location.OLD_MINE)), site.x(), y);
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
        // Oude zijtak richting Verloren Kreek.
        for (int i = 0; i < 560; i++) {
            int x = 775 - i, y = 720 + (int) (i * .20 + Math.sin(i / 65.0) * 17);
            color("8aa28c"); p.fillCircle(x, y, 5);
            color("b1b69a"); p.drawPixel(x, y - 2);
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
            int x = (int) (x0 + (x1 - x0) * t), y = (int) (y0 + (y1 - y0) * t + Math.sin(t * Math.PI) * 20);
            color("d8bf8b"); p.fillCircle(x, y, 2);
            if (i % 14 < 5) { color("a28151"); p.drawPixel(x, y); }
        }
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

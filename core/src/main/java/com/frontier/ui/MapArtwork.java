package com.frontier.ui;

import com.badlogic.gdx.graphics.*;
import com.frontier.model.Location;
import com.frontier.model.WorldMap;
import java.util.Random;

/** Een getekende streekkaart met bos, mijn, dorpsstraten en boerenland. */
final class MapArtwork {
    static final int WIDTH = WorldMap.REGION_WIDTH, HEIGHT = WorldMap.REGION_HEIGHT;
    private final Pixmap p = new Pixmap(WIDTH, HEIGHT, Pixmap.Format.RGBA8888);
    private final Random random = new Random(1880);
    private MapArtwork() {}
    static Pixmap createRegion() {
        MapArtwork art = new MapArtwork(); art.paint(); return art.p;
    }
    private void color(String hex) { p.setColor(Color.valueOf(hex)); }
    private void paint() {
        color("c9b27e"); p.fill();
        // Subtiele heuvels en contourlijnen onder de getekende landschapselementen.
        for (int i = 0; i < 35; i++) {
            int x = random.nextInt(WIDTH), y = random.nextInt(HEIGHT);
            color(i % 2 == 0 ? "c3ac78" : "cfb987");
            ellipse(x, y, 30 + random.nextInt(80), 8 + random.nextInt(24), true);
        }
        for (int i = 0; i < 18000; i++) {
            color(i % 2 == 0 ? "bda16d" : "d5bf8b"); p.drawPixel(random.nextInt(WIDTH), random.nextInt(HEIGHT));
        }
        color("bba16f");
        for (int x = 0; x < WIDTH; x += 80) p.drawLine(x, 0, x, HEIGHT);
        for (int y = 0; y < HEIGHT; y += 80) p.drawLine(0, y, WIDTH, y);
        forest(); mountains(); river(); trails(); village(); ranch();
        for (int i = 0; i < 90; i++) {
            int x = 18 + random.nextInt(765), y = 170 + random.nextInt(250);
            if (Math.abs(x - riverX(y)) < 35 || x < 250 && y > 265 || x > 585 && y > 280) continue;
            color("8b965c"); p.drawLine(x, y, x - 2, y - 4); p.drawLine(x, y, x + 2, y - 5);
            if (i % 4 == 0) { color("a28b64"); ellipse(x + 4, y, 4, 2, true); }
        }
        // Kompasroos van de centrale streek.
        color("79623f"); p.drawCircle(49, 379, 21); p.drawCircle(49, 379, 24);
        p.fillTriangle(49, 349, 43, 379, 55, 379); p.drawLine(19, 379, 79, 379); p.drawLine(49, 379, 49, 409);
        color("ead8a5"); p.fillTriangle(49, 409, 44, 379, 49, 379);
    }
    private int riverX(int y) { return 381 + (int) (Math.sin(y / 55.0) * 40); }
    private void river() {
        for (int y = 0; y < HEIGHT; y++) {
            int x = riverX(y);
            color("98a382"); p.fillRectangle(x - 5, y, 28, 1);
            color("69908e"); p.fillRectangle(x, y, 17, 1);
            color("9ab7ab"); p.fillRectangle(x + 2, y, 3, 1);
            if (y % 13 == 0) { color("b7ccc0"); p.drawLine(x + 6, y, x + 12, y); }
        }
    }
    private void trails() {
        for (Location target : Location.values()) {
            if (target == Location.RED_CREEK) continue;
            int sx = x(Location.RED_CREEK), sy = y(Location.RED_CREEK), tx = x(target), ty = y(target);
            for (int i = 0; i <= 200; i++) {
                double t = i / 200.0;
                int x = sx + (int) ((tx - sx) * t), y = sy + (int) ((ty - sy) * t + Math.sin(t * Math.PI) * 14);
                color("d8bf8b"); p.fillCircle(x, y, 5);
                color("a28151"); if (i % 10 < 5) p.fillCircle(x, y, 1);
                if (Math.abs(x - riverX(y) - 8) < 3) bridge(riverX(y) - 8, y);
            }
        }
    }
    private void bridge(int x, int y) {
        color("735132"); p.fillRectangle(x, y - 7, 37, 14);
        color("b28e5a"); for (int n = 2; n < 36; n += 4) p.drawLine(x + n, y - 5, x + n, y + 5);
        color("493c29"); p.drawLine(x, y - 7, x + 37, y - 7); p.drawLine(x, y + 7, x + 37, y + 7);
    }
    private void forest() {
        color("adb079"); ellipse(199, 73, 163, 62, true);
        color("969b68"); ellipse(130, 71, 80, 41, true);
        for (int i = 0; i < 95; i++) tree(55 + random.nextInt(280), 20 + random.nextInt(125), 10 + random.nextInt(9));
        color("8c6942"); p.fillRectangle(295, 123, 23, 12);
        color("d5ba7f"); for (int x = 295; x < 319; x += 5) p.drawLine(x, 123, x, 135);
        building(85, 136, 26, 18, "815a3b");
    }
    private void tree(int x, int y, int size) {
        color("6f5437"); p.fillRectangle(x - 2, y + size, 4, size / 2);
        color("859267"); ellipse(x + 4, y + size + 7, size, 3, true);
        color("536447"); p.fillTriangle(x, y - size, x - size / 2, y + size, x + size / 2, y + size);
        color("6d7c50"); p.fillTriangle(x, y - size, x - size / 2, y + size, x, y + size);
        color("8a9861"); p.drawLine(x - 2, y - size + 7, x - size / 3, y + size / 2);
    }
    private void mountains() {
        for (int i = 0; i < 9; i++) {
            int x = 478 + i * 31, y = 28 + random.nextInt(44), size = 32 + random.nextInt(24);
            color("9b9073"); p.fillTriangle(x, y, x - size, y + size * 2, x + size, y + size * 2);
            color("807c64"); p.fillTriangle(x, y, x + 7, y + size * 2, x + size, y + size * 2);
            color("e0cfaa"); p.fillTriangle(x, y, x - 9, y + 18, x + 9, y + 18);
        }
        color("665740"); p.fillCircle(638, 115, 19); p.fillRectangle(619, 114, 38, 16);
        color("302b24"); p.fillCircle(638, 116, 12); p.fillRectangle(626, 116, 24, 15);
        color("a07c4d"); p.fillRectangle(621, 99, 5, 33); p.fillRectangle(650, 99, 5, 33); p.fillRectangle(621, 99, 34, 5);
        color("6f6452"); for (int y = 130; y < 168; y += 7) p.drawLine(629, y, 661, y);
        p.drawLine(631, 130, 637, 168); p.drawLine(647, 130, 653, 168);
        color("716040"); p.fillRectangle(662, 145, 20, 12); color("302a21"); p.fillCircle(666, 158, 3); p.fillCircle(678, 158, 3);
        color("b29964"); ellipse(706, 154, 24, 10, true);
    }
    private void village() {
        color("bfa271"); p.fillRectangle(100, 277, 136, 107);
        color("dcc28a"); p.fillRectangle(105, 317, 137, 12); p.fillRectangle(160, 283, 14, 112);
        for (int i = 0; i < 7; i++) {
            building(108 + i % 4 * 29, 293 + i / 4 * 55, 23, 23, i % 2 == 0 ? "ae8050" : "996e46");
        }
        building(187, 344, 33, 29, "a57b51");
        color("d9c48e"); p.fillRectangle(194, 354, 20, 5);
        color("725137"); p.fillRectangle(233, 346, 26, 12);
        color("342b21"); p.fillCircle(238, 360, 3); p.fillCircle(253, 360, 3);
        color("8c7651"); p.fillCircle(129, 384, 7); color("4d5042"); p.fillCircle(129, 384, 4);
    }
    private void ranch() {
        color("afa06a"); p.fillRectangle(590, 346, 163, 62);
        color("806b41"); for (int y = 350; y < 402; y += 7) p.drawLine(678, y, 746, y);
        color("91905a"); for (int y = 352; y < 404; y += 7) for (int x = 680; x < 744; x += 6) p.fillRectangle(x, y, 3, 2);
        building(596, 351, 36, 27, "9b6942"); building(638, 380, 24, 18, "b18b58");
        color("c5a14f"); ellipse(650, 355, 13, 7, true); color("96763e"); p.drawLine(650, 349, 650, 361);
        color("715034"); for (int x = 584; x <= 754; x += 15) p.fillRectangle(x, 414, 3, 15);
        p.fillRectangle(584, 418, 172, 2); p.fillRectangle(584, 424, 172, 2);
        color("7c7051"); p.fillCircle(576, 377, 6); color("c4ad7c"); p.fillCircle(576, 377, 3);
    }
    private void building(int x, int y, int w, int h, String fill) {
        color("a98e60"); p.fillRectangle(x + 3, y + 4, w, h);
        color(fill); p.fillRectangle(x, y, w, h);
        color("704330"); p.fillTriangle(x - 3, y, x + w / 2, y - 12, x + w + 3, y);
        color("8b593c"); p.fillTriangle(x - 3, y, x + w / 2, y - 12, x + w / 2, y);
        color("403428"); p.fillRectangle(x + w / 2 - 3, y + h - 10, 6, 10);
        color("dacb9c"); p.fillRectangle(x + 4, y + 5, 4, 5);
        color("806d50"); p.fillRectangle(x + w - 5, y - 12, 4, 8);
    }
    private void ellipse(int cx, int cy, int rx, int ry, boolean fill) {
        for (int y = -ry; y <= ry; y++) {
            int x = (int) (rx * Math.sqrt(Math.max(0, 1 - y * y / (double) (ry * ry))));
            if (fill) p.drawLine(cx - x, cy + y, cx + x, cy + y);
            else { p.drawPixel(cx - x, cy + y); p.drawPixel(cx + x, cy + y); }
        }
    }
    private static int x(Location l) { return (int) (l.x() * WIDTH); }
    private static int y(Location l) { return (int) ((1 - l.y()) * HEIGHT); }
}

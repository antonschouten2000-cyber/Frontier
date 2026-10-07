package com.frontier.ui;

import com.badlogic.gdx.graphics.*;
import java.util.Random;

/** Aparte straatillustratie, zonder werkzaamheden of gedeelde wereldkaart. */
final class TownArtwork {
    static final int WIDTH = 960, HEIGHT = 350;
    private final Pixmap p = new Pixmap(WIDTH, HEIGHT, Pixmap.Format.RGBA8888);
    private TownArtwork() {}
    static Texture create() {
        TownArtwork art = new TownArtwork(); art.paint(); Texture t = new Texture(art.p); art.p.dispose(); return t;
    }
    private void color(String hex) { p.setColor(Color.valueOf(hex)); }
    private void paint() {
        color("bad0c1"); p.fill();
        color("d1dccb"); p.fillCircle(820, 37, 21);
        color("99a781"); p.fillTriangle(0, 110, 160, 45, 340, 130); p.fillTriangle(250, 130, 470, 65, 660, 130);
        color("869475"); p.fillTriangle(600, 140, 780, 62, 959, 140);
        color("bea274"); p.fillRectangle(0, 121, WIDTH, HEIGHT - 121);
        color("d3b985"); p.fillRectangle(0, 245, WIDTH, 64);
        Random random = new Random(1880);
        for (int i = 0; i < 5000; i++) {
            color(i % 2 == 0 ? "b29769" : "d6bd8d"); p.drawPixel(random.nextInt(WIDTH), 130 + random.nextInt(220));
        }
        for (int i = 0; i < 5; i++) building(26 + i * 187, i);
        // Dorpsstraat, houten trottoir en karrensporen.
        color("9d7f54"); p.fillRectangle(0, 298, WIDTH, 5);
        color("ad8f61"); p.drawLine(0, 323, WIDTH, 323); p.drawLine(0, 330, WIDTH, 330);
        for (int x = 9; x < WIDTH; x += 37) {
            color("88734f"); p.fillRectangle(x, 301, 4, 15); p.drawLine(x - 8, 303, x + 10, 303);
        }
        color("74563c"); p.fillRectangle(844, 321, 55, 18);
        color("342e24"); p.drawCircle(854, 340, 7); p.drawCircle(889, 340, 7);
        color("756043"); p.fillCircle(198, 325, 9); color("b69d6d"); p.drawCircle(198, 325, 6);
        color("684e34"); p.drawRectangle(2, 2, WIDTH - 4, HEIGHT - 4);
    }
    private void building(int x, int index) {
        int y = index == 1 ? 82 : 127, width = 159, height = 112;
        color("a58b63"); p.fillRectangle(x + 7, y + 8, width, height + 8);
        color(switch (index) { case 0 -> "a97448"; case 1 -> "c3b794"; case 2 -> "ab936d"; case 3 -> "a06341"; default -> "8c927d"; });
        p.fillRectangle(x, y, width, height);
        color("5b422e"); p.fillTriangle(x - 8, y, x + width / 2, y - 37, x + width + 8, y);
        color("7e5840"); p.fillTriangle(x - 8, y, x + width / 2, y - 37, x + width / 2, y);
        color("c8b591"); p.fillRectangle(x + 4, y + 20, width - 8, 6);
        color("7a6245"); for (int line = y + 31; line < y + height; line += 10) p.drawLine(x + 2, line, x + width - 3, line);
        color("493a2b"); p.fillRectangle(x + 64, y + 56, 32, 56);
        color("9eb4a6"); p.fillRectangle(x + 16, y + 45, 25, 29); p.fillRectangle(x + 119, y + 45, 25, 29);
        color("e0d0a5"); p.drawRectangle(x + 15, y + 44, 27, 31); p.drawRectangle(x + 118, y + 44, 27, 31);
        p.drawLine(x + 28, y + 45, x + 28, y + 73); p.drawLine(x + 131, y + 45, x + 131, y + 73);
        if (index == 1) {
            color("c1b28f"); p.fillRectangle(x + 58, y - 57, 42, 28);
            color("59432d"); p.fillTriangle(x + 54, y - 57, x + 79, y - 77, x + 104, y - 57);
            color("40382d"); p.fillCircle(x + 79, y - 43, 8);
            color("cabb94"); p.drawLine(x + 79, y - 43, x + 79, y - 49); p.drawLine(x + 79, y - 43, x + 84, y - 43);
            color("e0d0a5"); p.fillRectangle(x + 8, y + 33, 9, 79); p.fillRectangle(x + 142, y + 33, 9, 79);
        }
        color("81603f"); p.fillRectangle(x - 5, y + height, width + 10, 7);
        color("a28659"); p.fillRectangle(x + 47, y + height + 7, 65, 5);
        color("73543b"); p.fillRectangle(x + 137, y - 28, 10, 20);
    }
}

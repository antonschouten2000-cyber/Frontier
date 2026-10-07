package com.frontier.ui;

import com.badlogic.gdx.graphics.*;
import java.util.Random;

/** Houtnerven, planken, knoesten en spijkers als kleine procedurele textuur. */
final class WoodTexture {
    private WoodTexture() {}
    static Texture create(int width, int height, String base, String rim, boolean planks) {
        Pixmap p = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        Color color = Color.valueOf(base);
        Random random = new Random(1880 + width);
        for (int y = 0; y < height; y++) {
            float grain = (float) (Math.sin(y * .75) * .018 + Math.sin(y * .18) * .015);
            for (int x = 0; x < width; x++) {
                float noise = (random.nextFloat() - .5f) * .014f;
                float wave = (float) Math.sin(x * .026 + y * .12) * .01f;
                p.setColor(Math.max(0, color.r + grain + noise + wave),
                    Math.max(0, color.g + grain + noise + wave), Math.max(0, color.b + grain + noise + wave), 1);
                p.drawPixel(x, y);
            }
        }
        p.setColor(new Color(color).mul(.65f));
        for (int y = 9; y < height; y += 17) {
            for (int x = 8; x < width - 8; x++) {
                int offset = (int) (Math.sin(x * .032 + y) * 2);
                p.drawPixel(x, y + offset);
            }
        }
        for (int knot = 0; knot < (planks ? 7 : 1); knot++) {
            int cx = 15 + random.nextInt(Math.max(1, width - 30));
            int cy = 12 + random.nextInt(Math.max(1, height - 24));
            for (int ring = 2; ring < 10; ring += 2) {
                for (int a = 0; a < 360; a++) {
                    double angle = Math.toRadians(a);
                    p.drawPixel(cx + (int) (Math.cos(angle) * ring * 2), cy + (int) (Math.sin(angle) * ring / 2));
                }
            }
        }
        if (planks) {
            for (int y = 0; y < height; y += 80) {
                p.setColor(Color.valueOf("160d08")); p.fillRectangle(0, y, width, 2);
                p.setColor(new Color(color).mul(1.25f)); p.drawLine(0, y + 2, width - 1, y + 2);
                for (int x : new int[]{12, width - 13}) {
                    p.setColor(Color.valueOf("18120d")); p.fillCircle(x, y + 12, 2);
                    p.setColor(Color.valueOf("938166")); p.drawPixel(x - 1, y + 11);
                }
            }
        }
        if (rim != null) {
            p.setColor(Color.valueOf(rim)); p.drawRectangle(0, 0, width, height); p.drawRectangle(1, 1, width - 2, height - 2);
            for (int x : new int[]{6, width - 7}) for (int y : new int[]{6, height - 7}) {
                p.setColor(Color.valueOf("231b13")); p.fillCircle(x, y, 2);
                p.setColor(Color.valueOf("bea171")); p.drawPixel(x - 1, y - 1);
            }
        }
        Texture texture = new Texture(p); p.dispose(); return texture;
    }
}

package com.frontier.ui;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.*;
import com.frontier.model.Item;

/** Eigen herkenbare placeholder-sprites, gedeeld door inventaris, winkels en upgrades. */
final class ItemIcons {
    private static final int SIZE = 64;
    private final Pixmap p = new Pixmap(SIZE, SIZE, Pixmap.Format.RGBA8888);
    private ItemIcons() {}
    static void install(Skin skin) {
        Pixmap atlas = new Pixmap(Item.values().length * SIZE, SIZE, Pixmap.Format.RGBA8888);
        ItemIcons art = new ItemIcons();
        try {
            for (Item item : Item.values()) {
                art.p.setColor(Color.CLEAR); art.p.fill(); art.paint(item);
                atlas.drawPixmap(art.p, item.ordinal() * SIZE, 0);
            }
            Texture texture = new Texture(atlas); texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
            skin.add("item-icon-atlas", texture);
            for (Item item : Item.values()) skin.add("icon-" + item.name(),
                new TextureRegionDrawable(new TextureRegion(texture, item.ordinal() * SIZE, 0, SIZE, SIZE)), Drawable.class);
        } finally { art.p.dispose(); atlas.dispose(); }
    }
    static Drawable get(Skin skin, Item item) { return skin.getDrawable("icon-" + item.name()); }
    private void color(String hex) { p.setColor(Color.valueOf(hex)); }
    private void paint(Item item) {
        switch (item) {
            case WOOD -> {
                for (int n = 0; n < 3; n++) {
                    int y = 15 + n * 13; color("60452d"); p.fillRectangle(9, y, 39, 11);
                    color("b9854f"); p.fillCircle(48, y + 5, 7); color("6b4a30"); p.drawCircle(48, y + 5, 4);
                    color("9e6a40"); p.drawLine(12, y + 3, 38, y + 3);
                }
            }
            case STONE, ORE -> {
                color(item == Item.STONE ? "756f62" : "716e70"); p.fillTriangle(9, 46, 18, 16, 45, 12); p.fillTriangle(9, 46, 45, 12, 56, 44);
                color("b4afa0"); p.fillTriangle(18, 16, 45, 12, 34, 35); color("928b7a"); p.fillTriangle(9, 46, 34, 35, 56, 44);
                if (item == Item.ORE) { color("bb8755"); p.fillCircle(27, 26, 5); p.fillCircle(44, 35, 6); }
            }
            case COTTON -> {
                color("84623a"); p.fillRectangle(29, 35, 5, 22); color("879255"); p.fillTriangle(31, 48, 11, 37, 21, 50);
                color("d2c9af"); p.fillCircle(20, 29, 12); p.fillCircle(41, 29, 12); color("f5ead4"); p.fillCircle(30, 19, 13); p.fillCircle(30, 34, 12);
            }
            case COFFEE -> {
                color("b99a66"); p.fillTriangle(16, 20, 48, 20, 55, 53); p.fillTriangle(16, 20, 55, 53, 9, 53);
                color("765335"); p.fillRectangle(18, 13, 28, 7); p.fillCircle(28, 36, 7); p.fillCircle(39, 43, 6);
                color("ba8b57"); p.drawLine(27, 31, 29, 41); p.drawLine(38, 39, 40, 47);
            }
            case SLINGSHOT -> {
                color("8b5934"); p.fillRectangle(28, 29, 8, 28); p.fillTriangle(29, 38, 8, 13, 17, 10); p.fillTriangle(35, 38, 46, 10, 55, 13);
                color("d3b687"); p.drawLine(12, 13, 32, 31); p.drawLine(52, 13, 32, 31); color("655446"); p.fillRectangle(27, 27, 11, 7);
            }
            case KNIFE -> {
                color("70462e"); p.fillRectangle(12, 39, 10, 20); color("ddcfb1"); p.fillTriangle(17, 8, 23, 38, 12, 38);
                color("aaa99b"); p.fillTriangle(17, 8, 17, 38, 23, 38); color("b99c58"); p.fillRectangle(8, 36, 18, 4);
            }
            case REVOLVER -> {
                color("535652"); p.fillRectangle(10, 18, 45, 8); p.fillRectangle(10, 22, 23, 12); p.fillCircle(25, 26, 8);
                color("a5a69c"); p.drawLine(12, 18, 53, 18); p.fillRectangle(19, 21, 11, 8);
                color("99643c"); p.fillTriangle(10, 31, 24, 34, 18, 55); p.fillTriangle(10, 31, 18, 55, 8, 53);
                color("c6b58b"); p.drawCircle(31, 36, 6);
            }
            case RIFLE, REPEATER -> {
                color("705136"); p.fillRectangle(4, 35, 19, 13); p.fillTriangle(21, 32, 29, 32, 20, 48);
                color(item == Item.REPEATER ? "b68b4c" : "8f633d"); p.fillRectangle(22, 31, 25, 6);
                color("535652"); p.fillRectangle(29, 26, 31, 5); color("bbb9a7"); p.drawLine(31, 26, 60, 26);
                color("7c8279"); p.fillRectangle(24, 30, 15, 7);
                if (item == Item.REPEATER) { color("c2a662"); p.drawCircle(28, 40, 7); }
            }
            case HAT -> {
                color("8c6843"); p.fillRectangle(19, 17, 28, 25); p.fillCircle(33, 19, 13); color("b8945a"); p.fillRectangle(6, 39, 53, 10);
                color("4a3929"); p.fillRectangle(19, 33, 28, 6);
            }
            case BOOTS -> {
                for (int x : new int[]{10, 35}) { color("9b7247"); p.fillRectangle(x, 10, 13, 34); p.fillRectangle(x, 37, 22, 13); color("49382b"); p.fillRectangle(x, 49, 23, 4); color("c5a171"); p.drawLine(x + 2, 14, x + 10, 14); }
            }
            case WORK_SHIRT, COAT, DUSTER -> {
                int bottom = item == Item.WORK_SHIRT ? 45 : 57;
                color(item == Item.WORK_SHIRT ? "b9ad8d" : item == Item.COAT ? "708176" : "8a674a");
                p.fillRectangle(19, 17, 27, bottom - 17); p.fillTriangle(19, 17, 8, 38, 23, 30); p.fillTriangle(46, 17, 57, 38, 42, 30);
                color("d6c5a0"); p.fillTriangle(23, 16, 32, 25, 29, 14); p.fillTriangle(43, 16, 32, 25, 36, 14);
                color("4c473a"); p.drawLine(32, 27, 32, bottom - 2); p.fillRectangle(23, 34, 5, 3); p.fillRectangle(38, 34, 5, 3);
                if (item == Item.DUSTER) { color("c19e67"); p.fillRectangle(20, 39, 25, 3); }
            }
        }
    }
}

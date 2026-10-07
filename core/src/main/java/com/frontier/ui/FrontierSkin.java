package com.frontier.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.*;

/** Gegenereerde houtstijl en meegeleverd, glad gerenderd lettertype. */
public final class FrontierSkin {
    private FrontierSkin() {}
    public static Skin create() {
        Skin skin = new Skin();
        Pixmap pixel = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixel.setColor(Color.WHITE); pixel.fill();
        skin.add("pixel", new Texture(pixel)); pixel.dispose();
        BitmapFont font = font("fonts/DejaVuSans.ttf", 17);
        skin.add("default-font", font);
        BitmapFont title = font("fonts/DejaVuSans-Bold.ttf", 32);
        skin.add("title-font", title);
        Color cream = Color.valueOf("f5e4bf");
        skin.add("default", new Label.LabelStyle(font, cream));
        skin.add("muted", new Label.LabelStyle(font, Color.valueOf("dbccb2")));
        skin.add("accent", new Label.LabelStyle(font, Color.valueOf("e9b75f")));
        skin.add("title", new Label.LabelStyle(title, Color.valueOf("e9b75f")));
        Drawable up = panel(skin, "button", "55412d", "a17b46");
        Drawable over = panel(skin, "hover", "705436", "e9b75f");
        Drawable down = panel(skin, "pressed", "35291e", "e9b75f");
        Drawable disabled = panel(skin, "disabled", "3d352c", "655848");
        TextButton.TextButtonStyle button = new TextButton.TextButtonStyle(up, down, null, font);
        button.over = over; button.disabled = disabled;
        button.fontColor = cream; button.disabledFontColor = Color.valueOf("93836c");
        skin.add("default", button);
        TextButton.TextButtonStyle current = new TextButton.TextButtonStyle(button);
        current.disabled = panel(skin, "current", "7c4a27", "f0c46e");
        current.disabledFontColor = cream;
        skin.add("current", current);
        TextButton.TextButtonStyle selected = new TextButton.TextButtonStyle(button);
        selected.up = current.disabled;
        skin.add("selected", selected);
        Texture wood = WoodTexture.create(1200, 800, "382418", null, true);
        skin.add("wood-texture", wood);
        TextureRegionDrawable woodBackground = new TextureRegionDrawable(wood);
        woodBackground.setMinWidth(0); woodBackground.setMinHeight(0);
        skin.add("wood", woodBackground, Drawable.class);
        ScrollPane.ScrollPaneStyle scroll = new ScrollPane.ScrollPaneStyle();
        scroll.vScroll = skin.newDrawable("pixel", Color.valueOf("251a12")); scroll.vScroll.setMinWidth(8);
        scroll.vScrollKnob = skin.newDrawable("pixel", Color.valueOf("ab8556")); scroll.vScrollKnob.setMinWidth(8);
        scroll.hScroll = skin.newDrawable("pixel", Color.valueOf("251a12")); scroll.hScroll.setMinHeight(6);
        scroll.hScrollKnob = skin.newDrawable("pixel", Color.valueOf("ab8556")); scroll.hScrollKnob.setMinHeight(6);
        skin.add("default", scroll);
        skin.add("card", panel(skin, "card-texture", "30271e", "655038"), Drawable.class);
        Window.WindowStyle window = new Window.WindowStyle(font, cream, skin.getDrawable("card"));
        window.stageBackground = skin.newDrawable("pixel", new Color(0, 0, 0, .65f));
        skin.add("default", window);
        ProgressBar.ProgressBarStyle stamina = new ProgressBar.ProgressBarStyle();
        stamina.background = skin.newDrawable("pixel", Color.valueOf("181813"));
        stamina.background.setMinHeight(10);
        stamina.knobBefore = skin.newDrawable("pixel", Color.valueOf("93a56a"));
        stamina.knobBefore.setMinHeight(10);
        skin.add("default-horizontal", stamina);
        ProgressBar.ProgressBarStyle xp = new ProgressBar.ProgressBarStyle(stamina);
        xp.knobBefore = skin.newDrawable("pixel", Color.valueOf("b58b43")); xp.knobBefore.setMinHeight(18);
        xp.background.setMinHeight(18); stamina.knobBefore.setMinHeight(18);
        skin.add("experience-horizontal", xp);
        return skin;
    }
    private static BitmapFont font(String path, int size) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(path));
        try {
            FreeTypeFontGenerator.FreeTypeFontParameter params = new FreeTypeFontGenerator.FreeTypeFontParameter();
            params.size = size; params.characters = FreeTypeFontGenerator.DEFAULT_CHARS;
            params.minFilter = Texture.TextureFilter.Linear; params.magFilter = Texture.TextureFilter.Linear;
            return generator.generateFont(params);
        } finally { generator.dispose(); }
    }
    private static Drawable panel(Skin skin, String name, String fill, String border) {
        Texture texture = WoodTexture.create(256, 96, fill, border, false);
        skin.add(name, texture);
        NinePatchDrawable drawable = new NinePatchDrawable(new NinePatch(texture, 9, 9, 9, 9));
        drawable.setMinWidth(0); drawable.setMinHeight(0);
        return drawable;
    }
}

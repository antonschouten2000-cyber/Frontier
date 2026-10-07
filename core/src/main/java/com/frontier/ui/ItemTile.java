package com.frontier.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.frontier.model.Item;
import com.badlogic.gdx.utils.Align;

/** Klikbare voorwerpslot met afbeelding, naam en hoeveelheid. */
final class ItemTile extends Button {
    ItemTile(Skin skin, Item item, String caption, String name, Runnable onSelect) {
        super(new ButtonStyle(skin.get(TextButton.TextButtonStyle.class))); setName(name); pad(8);
        Image image = new Image(ItemIcons.get(skin, item)); image.setName("icon-" + name);
        add(image).size(48).row();
        Label title = new Label(item.displayName(), skin); title.setWrap(true); title.setAlignment(Align.center);
        add(title).width(116).height(38).padTop(4).row();
        add(new Label(caption, skin, "accent")).padTop(3);
        addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) { onSelect.run(); }
        });
    }
}

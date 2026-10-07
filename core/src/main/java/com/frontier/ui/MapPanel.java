package com.frontier.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.frontier.logic.GameSession;
import com.frontier.model.Location;
import java.util.EnumMap;
import java.util.function.Consumer;

final class MapPanel extends WidgetGroup {
    private final Texture artwork = MapArtwork.create();
    private final Image background = new Image(artwork);
    private final EnumMap<Location, TextButton> markers = new EnumMap<>(Location.class);
    private final Skin skin;
    MapPanel(Skin skin, Consumer<Location> onTravel) {
        this.skin = skin;
        addActor(background);
        for (Location location : Location.values()) {
            TextButton marker = new TextButton(location.displayName(), skin);
            marker.setName("travel-" + location.name());
            marker.addListener(new ChangeListener() {
                @Override public void changed(ChangeEvent event, Actor actor) { onTravel.accept(location); }
            });
            markers.put(location, marker); addActor(marker);
        }
    }
    @Override public float getPrefWidth() { return MapArtwork.WIDTH; }
    @Override public float getPrefHeight() { return MapArtwork.HEIGHT; }
    @Override public void layout() {
        background.setBounds(0, 0, getWidth(), getHeight());
        markers.forEach((location, marker) -> marker.setBounds(
            location.x() * getWidth() - 80, location.y() * getHeight() - 25, 160, 50));
    }
    void refresh(GameSession game, Location selected) {
        markers.forEach((location, marker) -> {
            boolean current = game.state().location() == location;
            marker.setStyle(skin.get(location == selected ? "selected" : "default", TextButton.TextButtonStyle.class));
            marker.setDisabled(false);
            marker.setText(location.displayName() + (current ? "\nJe bent hier" : ""));
        });
    }
    void dispose() { artwork.dispose(); }
}

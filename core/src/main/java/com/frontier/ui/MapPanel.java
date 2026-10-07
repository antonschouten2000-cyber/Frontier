package com.frontier.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.frontier.logic.GameSession;
import com.frontier.model.Location;
import java.util.EnumMap;
import java.util.function.Consumer;

/** ScrollPane annuleert een locatieklik zodra de muis een echte sleepbeweging maakt. */
final class MapPanel extends ScrollPane {
    private final MapCanvas canvas;
    private boolean centered;
    MapPanel(Skin skin, Consumer<Location> onSelect) { this(new MapCanvas(skin, onSelect), skin); }
    private MapPanel(MapCanvas canvas, Skin skin) {
        super(canvas, skin); this.canvas = canvas;
        setName("world-map"); setFadeScrollBars(false); setOverscroll(false, false);
        setScrollingDisabled(false, false); setSmoothScrolling(false); setFlickScroll(true);
    }
    @Override public void layout() {
        super.layout();
        if (!centered) { center(); centered = true; }
    }
    void center() { fling(0, 0, 0); setScrollPercentX(.5f); setScrollPercentY(.5f); updateVisualScroll(); }
    void refresh(GameSession game, Location selected) { canvas.refresh(game, selected); }
    void dispose() { canvas.artwork.dispose(); }

    private static final class MapCanvas extends WidgetGroup {
        private static final float WIDTH = 1200, HEIGHT = 660;
        private final Texture artwork = MapArtwork.create();
        private final Image background = new Image(artwork);
        private final EnumMap<Location, TextButton> markers = new EnumMap<>(Location.class);
        private final Skin skin;
        MapCanvas(Skin skin, Consumer<Location> onSelect) {
            this.skin = skin; setName("map-canvas"); addActor(background);
            for (Location location : Location.values()) {
                TextButton marker = Ui.button(skin, location.displayName(), "travel-" + location.name(), () -> onSelect.accept(location));
                markers.put(location, marker); addActor(marker);
            }
        }
        @Override public float getPrefWidth() { return WIDTH; }
        @Override public float getPrefHeight() { return HEIGHT; }
        @Override public void layout() {
            background.setBounds(0, 0, getWidth(), getHeight());
            markers.forEach((location, marker) -> marker.setBounds(
                location.x() * getWidth() - 88, location.y() * getHeight() - 26, 176, 52));
        }
        void refresh(GameSession game, Location selected) {
            markers.forEach((location, marker) -> {
                marker.setStyle(skin.get(location == selected ? "selected" : "default", TextButton.TextButtonStyle.class));
                marker.setText(location.displayName() + (game.state().location() == location ? "\nJe bent hier" : ""));
            });
        }
    }
}

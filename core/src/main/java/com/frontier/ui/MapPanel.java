package com.frontier.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.frontier.logic.GameSession;
import com.frontier.model.Location;
import com.frontier.model.Landmark;
import com.frontier.model.WorldMap;
import java.util.EnumMap;
import java.util.function.Consumer;

/** ScrollPane annuleert een locatieklik zodra de muis een echte sleepbeweging maakt. */
final class MapPanel extends ScrollPane {
    private final MapCanvas canvas;
    private boolean centered;
    MapPanel(Skin skin, Consumer<Location> onSelect, Consumer<Landmark> onLandmark) { this(new MapCanvas(skin, onSelect, onLandmark), skin); }
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
        private static final float WIDTH = WorldMap.WIDTH, HEIGHT = WorldMap.HEIGHT;
        private final Texture artwork = WorldArtwork.create();
        private final Image background = new Image(artwork);
        private final EnumMap<Landmark, TextButton> landmarks = new EnumMap<>(Landmark.class);
        private final EnumMap<Location, TextButton> markers = new EnumMap<>(Location.class);
        private final Skin skin;
        MapCanvas(Skin skin, Consumer<Location> onSelect, Consumer<Landmark> onLandmark) {
            this.skin = skin; setName("map-canvas"); addActor(background);
            for (Location location : Location.values()) {
                TextButton marker = Ui.button(skin, location.displayName(), "travel-" + location.name(), () -> onSelect.accept(location));
                markers.put(location, marker); addActor(marker);
            }
            for (Landmark site : Landmark.values()) {
                TextButton marker = Ui.button(skin, site.displayName() + "\n" + site.kind(), "landmark-" + site.name(), () -> onLandmark.accept(site));
                landmarks.put(site, marker); addActor(marker);
            }
        }
        @Override public float getPrefWidth() { return WIDTH; }
        @Override public float getPrefHeight() { return HEIGHT; }
        @Override public void layout() {
            background.setBounds(0, 0, getWidth(), getHeight());
            markers.forEach((location, marker) -> marker.setBounds(
                WorldMap.x(location) - 88, WorldMap.y(location) - 26, 176, 52));
            landmarks.forEach((site, marker) -> marker.setBounds(site.x() - 105, site.y() - 28, 210, 56));
        }
        void refresh(GameSession game, Location selected) {
            markers.forEach((location, marker) -> {
                marker.setStyle(skin.get(location == selected ? "selected" : "default", TextButton.TextButtonStyle.class));
                marker.setText(location.displayName() + (game.state().location() == location ? "\nJe bent hier" : ""));
            });
        }
    }
}

package com.frontier.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.frontier.logic.GameSession;
import com.frontier.model.Location;
import com.frontier.model.Landmark;
import com.frontier.model.WorldMap;
import com.frontier.model.VillageProject;
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
    void dispose() { canvas.artwork.dispose(); canvas.bridgeTexture.dispose(); canvas.wellTexture.dispose(); }

    private static final class MapCanvas extends WidgetGroup {
        private static final float SCALE = .75f;
        private static final float WIDTH = WorldMap.WIDTH * SCALE, HEIGHT = WorldMap.HEIGHT * SCALE;
        private final Texture artwork = WorldArtwork.create();
        private final Image background = new Image(artwork);
        private final Texture bridgeTexture=MapProjectArtwork.create(true),wellTexture=MapProjectArtwork.create(false);
        private final Image bridge=new Image(bridgeTexture),well=new Image(wellTexture);
        private final EnumMap<Landmark, TextButton> landmarks = new EnumMap<>(Landmark.class);
        private final EnumMap<Location, TextButton> markers = new EnumMap<>(Location.class);
        private final Skin skin;
        MapCanvas(Skin skin, Consumer<Location> onSelect, Consumer<Landmark> onLandmark) {
            this.skin = skin; setName("map-canvas"); addActor(background);
            bridge.setName("restored-bridge");well.setName("restored-well");bridge.setTouchable(com.badlogic.gdx.scenes.scene2d.Touchable.disabled);well.setTouchable(com.badlogic.gdx.scenes.scene2d.Touchable.disabled);addActor(bridge);addActor(well);
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
            float offsetX = (getWidth() - WIDTH) / 2, offsetY = (getHeight() - HEIGHT) / 2;
            background.setBounds(offsetX, offsetY, WIDTH, HEIGHT);
            float bx=(WorldMap.x(Location.RED_CREEK)+WorldMap.x(Location.FORGOTTEN_STOP))/2;
            float by=(WorldMap.y(Location.RED_CREEK)+WorldMap.y(Location.FORGOTTEN_STOP))/2;
            bridge.setBounds(offsetX+bx*SCALE-90,offsetY+by*SCALE-40,180,105);
            well.setBounds(offsetX+WorldMap.x(Location.WILLOW_FARM)*SCALE-80,offsetY+WorldMap.y(Location.WILLOW_FARM)*SCALE-120,180,105);
            markers.forEach((location, marker) -> marker.setBounds(
                offsetX + WorldMap.x(location) * SCALE - 88, offsetY + WorldMap.y(location) * SCALE - 26, 176, 52));
            landmarks.forEach((site, marker) -> marker.setBounds(offsetX + site.x() * SCALE - 105, offsetY + site.y() * SCALE - 28, 210, 56));
        }
        void refresh(GameSession game, Location selected) {
            bridge.setVisible(game.state().journal().done(VillageProject.BRIDGE));well.setVisible(game.state().journal().done(VillageProject.WELL));
            markers.forEach((location, marker) -> {
                marker.setStyle(skin.get(location == selected ? "selected" : "default", TextButton.TextButtonStyle.class));
                marker.setText(location.displayName() + (location==Location.FORGOTTEN_STOP && !game.state().journal().done(VillageProject.BRIDGE)?"\nBrug ingestort":"") + (game.state().location() == location ? "\nJe bent hier" : ""));
            });
        }
    }
}

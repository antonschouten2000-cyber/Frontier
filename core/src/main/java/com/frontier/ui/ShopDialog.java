package com.frontier.ui;

import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.frontier.logic.*;
import com.frontier.model.*;
import java.util.function.Consumer;

final class ShopDialog extends Dialog {
    private final Skin skin;
    private final GameSession game;
    private final Building building;
    private final Consumer<String> onAction;
    private final Table offers = new Table();
    private final Label summary, note;
    ShopDialog(Skin skin, GameSession game, Building building, Consumer<String> onAction) {
        super(building.displayName() + " - Winkel", skin); this.skin = skin; this.game = game; this.building = building; this.onAction = onAction;
        setName("shop-window"); getContentTable().pad(20);
        summary = new Label("", skin, "accent"); summary.setName("shop-summary");
        getContentTable().add(summary).left().padBottom(14).row();
        ScrollPane scroll = new ScrollPane(offers, skin); scroll.setFadeScrollBars(false); scroll.setScrollingDisabled(true, false);
        getContentTable().add(scroll).width(620).height(340).row();
        note = new Label("Kies een afbeelding voor de beschrijving. Betere voorwerpen komen vrij bij hogere gebouwlevels.", skin, "muted"); note.setWrap(true);
        getContentTable().add(note).width(620).height(64).padTop(12).row();
        button("Sluiten"); Ui.nameDialogButtons(this, "shop-close"); getButtonTable().pad(14); refresh();
    }
    private void refresh() {
        int level = game.state().town().level(building);
        summary.setText(building.displayName() + " level " + level + "  |  Cash $" + game.state().player().money());
        offers.clearChildren(); offers.top(); int column = 0;
        for (ShopOffer offer : ShopOffer.at(building)) {
            Table card = new Table(); card.setBackground(skin.getDrawable("card")); card.pad(10);
            ItemTile tile = new ItemTile(skin, offer.item(), "Kwaliteit " + offer.quality(), "shop-item-" + offer.item().name(),
                () -> note.setText(offer.item().description()));
            card.add(tile).width(270).height(128).row();
            card.add(new Label("$" + offer.price() + "  |  Gebouwlevel " + offer.requiredLevel(), skin, "accent")).padTop(8).row();
            TextButton buy = Ui.button(skin, level < offer.requiredLevel() ? "Nog vergrendeld" : "Kopen", "buy-" + offer.item().name(), () -> {
                String result = game.town().buy(offer); note.setText(result); onAction.accept(result); refresh();
            });
            buy.setDisabled(!game.town().buyReason(offer).isEmpty()); card.add(buy).growX().height(38).padTop(8);
            offers.add(card).width(294).pad(4); if (++column % 2 == 0) offers.row();
        }
    }
}

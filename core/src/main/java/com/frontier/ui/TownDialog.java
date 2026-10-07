package com.frontier.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.frontier.logic.GameSession;
import com.frontier.model.Location;
import java.util.function.Consumer;

/** Een apart in-game venster; alleen de herberg voert een spelactie uit. */
final class TownDialog extends Dialog {
    private final GameSession game;
    private final Label description, summary;
    private final TextButton sleep;
    TownDialog(Skin skin, GameSession game, Consumer<String> onAction) {
        super("Red Creek - Stad", skin); this.game = game; setName("town-window");
        if (!skin.has("town-art", Texture.class)) skin.add("town-art", TownArtwork.create());
        Table contents = getContentTable(); contents.pad(20);
        contents.add(new Label("RED CREEK", skin, "title")).left().padBottom(8).row();
        contents.add(new Label("Kies een gebouw om het te bezoeken.", skin, "muted")).left().padBottom(14).row();
        Stack street = new Stack(); street.add(new Image(skin.get("town-art", Texture.class)));
        Table signs = new Table(); signs.bottom().padBottom(23); signs.defaults().width(174).height(42).pad(6);
        for (TownBuilding building : TownBuilding.values()) {
            signs.add(Ui.button(skin, building.name, "building-" + building.name(), () -> select(building)));
        }
        street.add(signs); contents.add(street).width(960).height(350).row();
        description = new Label("Welkom in Red Creek. Hier kun je gebouwen bezoeken en uitrusten in de herberg.", skin);
        description.setWrap(true); description.setName("town-description");
        summary = new Label("", skin, "muted"); summary.setName("town-summary");
        sleep = Ui.button(skin, "Acht uur slapen", "sleep", () -> {
            onAction.accept(game.sleep()); updateSummary();
        });
        Table info = new Table(); info.add(description).width(705).height(72).left();
        info.add(sleep).width(200).height(44).padLeft(20);
        contents.add(info).growX().padTop(12).row();
        contents.add(summary).left().padTop(6).row();
        button("Terug naar de wereldkaart"); Ui.nameDialogButtons(this, "town-close"); getButtonTable().pad(16);
        sleep.setVisible(false); updateSummary();
    }
    private void select(TownBuilding building) {
        description.setText(building.name + "\n" + building.description);
        sleep.setVisible(building == TownBuilding.INN);
        sleep.setDisabled(game.state().location() != Location.RED_CREEK);
    }
    private void updateSummary() {
        summary.setText(game.state().time().display() + "   |   Energie " + game.state().player().stamina()
            + "/100   |   Geld bij je: $" + game.state().player().money());
    }
}

package com.frontier.ui;

import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.frontier.model.Landmark;

final class LandmarkDialog extends Dialog {
    LandmarkDialog(Skin skin, Landmark site) {
        super(site.displayName(), skin); setName("landmark-window");
        Table content = getContentTable(); content.pad(24);
        content.add(new Label(site.kind().toUpperCase(java.util.Locale.ROOT), skin, "accent")).left().padBottom(14).row();
        Label description = new Label(site.description(), skin); description.setWrap(true);
        content.add(description).width(510).left().padBottom(20).row();
        content.add(new Label("Prijs nog te bepalen", skin, "accent")).left().padBottom(12).row();
        Label future = new Label("Deze plek is gereserveerd voor een latere versie. Dan kun je haar mogelijk kopen. Bekijken kost nu geen tijd, energie of geld.", skin, "muted");
        future.setWrap(true); content.add(future).width(510).left().padBottom(18).row();
        TextButton buy = new TextButton("Kopen - in een volgende versie", skin);
        buy.setName("landmark-buy"); buy.setDisabled(true); content.add(buy).width(360).height(44).left();
        button("Terug naar de kaart"); Ui.nameDialogButtons(this, "landmark-close"); getButtonTable().pad(16);
    }
}

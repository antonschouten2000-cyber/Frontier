package com.frontier.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.*;

final class SettingsDialog extends Dialog {
    SettingsDialog(Skin skin, Runnable newGame, Runnable save, Runnable load) {
        super("Instellingen", skin);
        Table contents = getContentTable(); contents.pad(24); contents.defaults().width(300).height(46).padBottom(10);
        contents.add(Ui.button(skin, "Nieuw spel", "new-game", () -> { hide(null); newGame.run(); })).row();
        contents.add(Ui.button(skin, "Spel opslaan", "save-game", () -> { save.run(); hide(); })).row();
        contents.add(Ui.button(skin, "Spel laden", "load-game", () -> { load.run(); hide(); })).row();
        contents.add(Ui.button(skin, "Afsluiten", "quit", () -> Gdx.app.exit())).row();
        button("Terug naar de kaart"); Ui.nameDialogButtons(this, "settings-close"); getButtonTable().pad(16);
    }
}

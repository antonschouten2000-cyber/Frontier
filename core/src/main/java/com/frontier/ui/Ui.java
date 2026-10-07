package com.frontier.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

final class Ui {
    private Ui() {}
    static TextButton button(Skin skin, String text, String name, Runnable action) {
        TextButton button = new TextButton(text, skin); button.setName(name);
        button.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) { action.run(); }
        });
        return button;
    }
    static String duration(int minutes) {
        if (minutes < 60) return minutes + " min";
        return minutes / 60 + " uur" + (minutes % 60 == 0 ? "" : " " + minutes % 60 + " min");
    }
    static void nameDialogButtons(Dialog dialog, String... names) {
        for (int i = 0; i < names.length; i++) dialog.getButtonTable().getChildren().get(i).setName(names[i]);
    }
}

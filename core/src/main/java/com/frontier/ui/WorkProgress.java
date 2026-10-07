package com.frontier.ui;

import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.frontier.logic.GameSession;

/** Blijvende voortgang van de actieve klus, los van het werkkeuzevenster. */
final class WorkProgress extends Table {
    private final Label title, remaining;
    private final ProgressBar progress;
    WorkProgress(Skin skin) {
        title = new Label("", skin, "accent"); title.setWrap(true); title.setName("active-work-title");
        remaining = new Label("", skin, "muted"); remaining.setName("active-work-remaining");
        progress = new ProgressBar(0, 1, .001f, false, skin, "experience-horizontal"); progress.setName("active-work-bar");
        add(title).width(254).left().row(); add(progress).growX().height(20).padTop(6).row();
        add(remaining).left().padTop(5);
    }
    void refresh(GameSession game) {
        setVisible(game.isWorking());
        if (!game.isWorking()) return;
        title.setText(game.state().activeWork().job().name()); progress.setValue(game.workProgress());
        long seconds = game.workSecondsRemaining();
        remaining.setText("Nog " + seconds / 3600 + ":" + String.format("%02d:%02d", seconds / 60 % 60, seconds % 60));
    }
}

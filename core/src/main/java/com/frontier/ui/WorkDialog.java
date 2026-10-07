package com.frontier.ui;

import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.frontier.logic.GameSession;
import com.frontier.logic.LootManager;
import com.frontier.model.Job;
import java.util.function.Consumer;

final class WorkDialog extends Dialog {
    WorkDialog(Skin skin, GameSession game, Job job, Consumer<String> onResult) {
        super(job.name(), skin);
        getContentTable().pad(22);
        Table details = new Table(); details.defaults().pad(7);
        row(details, skin, "Locatie", job.location().displayName());
        row(details, skin, "Opbrengst", "$" + (job.basePay() - job.payVariation()) + " tot $" + (job.basePay() + job.payVariation()));
        row(details, skin, "Ervaring", "+" + job.xp());
        row(details, skin, "Energie voor werk", job.staminaCost() + " punten");
        row(details, skin, "Duur van het werk", Ui.duration(job.minutes()));
        int travel = game.state().location().travelMinutesTo(job.location());
        row(details, skin, "Reistijd erheen", travel == 0 ? "0 min (je bent hier)" : Ui.duration(travel));
        row(details, skin, "Energie voor de reis", game.state().location().travelStaminaTo(job.location()) + " punten");
        row(details, skin, "Kans op een vondst", LootManager.FIND_CHANCE_PERCENT + "% per afgeronde klus");
        details.setName("work-details");
        getContentTable().add(details).width(510).row();
        String reason = game.workBlockReason(job);
        Label note = new Label(reason.isEmpty() ? "Je houdt genoeg energie over voor de terugweg naar Red Creek." : reason, skin, "muted");
        note.setWrap(true); getContentTable().add(note).width(510).padTop(14).row();
        button("Terug", false);
        button(travel == 0 ? "Aan het werk" : "Reizen en werken", true);
        Ui.nameDialogButtons(this, "work-cancel", "work-confirm");
        ((TextButton) getButtonTable().getChildren().get(1)).setDisabled(!reason.isEmpty());
        getButtonTable().pad(16);
        for (Cell<?> cell : getButtonTable().getCells()) cell.minWidth(160).height(44).pad(6);
        this.onResult = onResult; this.game = game; this.job = job;
    }
    private final Consumer<String> onResult;
    private final GameSession game;
    private final Job job;
    @Override protected void result(Object value) {
        if (Boolean.TRUE.equals(value)) onResult.accept(game.work(job));
    }
    private static void row(Table table, Skin skin, String name, String value) {
        table.add(new Label(name, skin, "muted")).left().expandX();
        table.add(new Label(value, skin)).right().row();
    }
}

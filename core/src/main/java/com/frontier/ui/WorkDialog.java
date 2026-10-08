package com.frontier.ui;

import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.frontier.logic.GameSession;
import com.frontier.logic.LootManager;
import com.frontier.logic.WorkMaterials;
import com.frontier.model.*;
import java.util.EnumMap;
import java.util.Locale;
import java.util.function.Consumer;

final class WorkDialog extends Dialog {
    private final Consumer<String> onResult;
    private final GameSession game;
    private final Job job;
    private final Skin skin;
    private final Table details = new Table();
    private final Label note;
    private final EnumMap<WorkDuration, TextButton> choices = new EnumMap<>(WorkDuration.class);
    private WorkDuration selected = WorkDuration.LONG;
    WorkDialog(Skin skin, GameSession game, Job job, Consumer<String> onResult) {
        super(job.name(), skin); this.skin = skin; this.game = game; this.job = job; this.onResult = onResult;
        getContentTable().pad(22);
        getContentTable().add(new Label("Kies hoelang je wilt werken (echte wachttijd).", skin, "accent")).left().padBottom(12).row();
        Table durations = new Table();
        for (WorkDuration duration : WorkDuration.values()) {
            TextButton choice = Ui.button(skin, duration.displayName(), "work-duration-" + duration.name(), () -> {
                selected = duration; refresh();
            });
            choices.put(duration, choice); durations.add(choice).width(164).height(42).padRight(6);
        }
        getContentTable().add(durations).padBottom(12).row();
        details.setName("work-details"); getContentTable().add(details).width(510).row();
        note = new Label("", skin, "muted"); note.setWrap(true);
        getContentTable().add(note).width(510).padTop(14).row();
        button("Terug", false);
        button(game.state().location() == job.location() ? "Aan het werk" : "Reizen en werken", true);
        Ui.nameDialogButtons(this, "work-cancel", "work-confirm"); getButtonTable().pad(16);
        for (Cell<?> cell : getButtonTable().getCells()) cell.minWidth(160).height(44).pad(6);
        refresh();
    }
    private void refresh() {
        choices.forEach((duration, choice) -> choice.setStyle(skin.get(duration == selected ? "selected" : "default", TextButton.TextButtonStyle.class)));
        details.clearChildren(); details.defaults().pad(5);
        row("Locatie", job.location().displayName());
        row("Opbrengst", "$" + job.scaledPay(job.basePay() - job.payVariation(), selected) + " tot $" + job.scaledPay(job.basePay() + job.payVariation(), selected));
        if (WorkMaterials.item(job) != null) row("Vast bouwmateriaal", WorkMaterials.quantity(job, selected) + " x " + WorkMaterials.item(job).displayName());
        row("Ervaring", "+" + job.xp(selected));
        row("Energie voor werk", game.workCost(job, selected) + " punten");
        if (job.location()==Location.WILLOW_FARM && game.state().journal().done(VillageProject.WELL)) row("Dorpsvoordeel", "Waterput: 15% minder werkenergie");
        row("Duur van het werk", selected.displayName());
        int travel = game.state().location().travelMinutesTo(job.location());
        row("Reistijd erheen", travel == 0 ? "0 min (je bent hier)" : Ui.duration(travel));
        row("Energie voor de reis", game.travelCost(job.location()) + " punten");
        row("Kans op een vondst", String.format(Locale.forLanguageTag("nl-NL"), "%.3f%%", LootManager.chancePercent(selected)));
        String reason = game.workBlockReason(job, selected);
        note.setText(reason.isEmpty() ? "De voortgangsbalk blijft zichtbaar na het sluiten van dit venster. Reizen kost alleen in-game tijd." : reason);
        ((TextButton) getButtonTable().getChildren().get(1)).setDisabled(!reason.isEmpty());
    }
    @Override protected void result(Object value) {
        if (Boolean.TRUE.equals(value)) onResult.accept(game.startWork(job, selected));
    }
    private void row(String name, String value) {
        details.add(new Label(name, skin, "muted")).left().expandX();
        details.add(new Label(value, skin)).right().row();
    }
}

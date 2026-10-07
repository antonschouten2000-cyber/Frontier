package com.frontier.ui;

import com.badlogic.gdx.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.frontier.logic.GameSession;
import com.frontier.model.*;
import com.frontier.save.SaveManager;
import java.io.IOException;

public final class GameScreen extends ScreenAdapter {
    private final GameSession game;
    private final SaveManager saves;
    private final Skin skin = FrontierSkin.create();
    private final Stage stage = new Stage(new FitViewport(1200, 800));
    private final Label playerHud = label("", "default"), timeHud = label("", "accent");
    private final Label locationTitle = label("", "accent"), locationInfo = label("", "muted");
    private final Label status = label("Welkom in Red Creek. Twintig dollar en een nieuw begin. Kies een locatie of zoek een klus.", "default");
    private final Label energyText = label("", "default");
    private final ProgressBar energy = new ProgressBar(0, 100, 1, false, skin);
    private final Table jobs = new Table();
    private final TextButton travel, sleep, inventory;
    private final MapPanel map;
    private Location selected = Location.RED_CREEK;

    public GameScreen(GameSession game, SaveManager saves) {
        this.game = game; this.saves = saves;
        travel = button("Reizen", "travel", this::confirmTravel);
        sleep = button("Slapen in de herberg", "sleep", () -> message(game.sleep()));
        inventory = button("Inventaris", "inventory", () -> new InventoryDialog(skin, game.state().inventory()).show(stage));
        map = new MapPanel(skin, location -> { selected = location; refresh(); });
        status.setWrap(true); locationInfo.setWrap(true);
        playerHud.setName("player-hud"); timeHud.setName("time-hud"); status.setName("status");
        Table root = new Table(); root.setFillParent(true); root.pad(20); root.setBackground(skin.getDrawable("wood")); stage.addActor(root);
        Table heading = new Table(); heading.add(label("FRONTIER", "title")).left().expandX();
        heading.add(label("EEN LEVEN AAN DE GRENS  /  1880", "muted")).right();
        root.add(heading).growX().padBottom(16).row();
        Table hud = new Table(); hud.setBackground(skin.getDrawable("card")); hud.pad(16);
        hud.add(playerHud).left().expandX(); hud.add(timeHud).right();
        root.add(hud).growX().padBottom(16).row();
        Table content = new Table(), territory = new Table();
        territory.add(label("HET GEBIED ROND RED CREEK", "accent")).left().growX().padBottom(10).row();
        territory.add(map).width(800).height(400).row();
        territory.add(label("Kies een locatie. Klik op een werkzaamheid voor opbrengst en kosten.", "muted")).left().padTop(12);
        content.add(territory).width(800).top();
        Table activities = new Table(); activities.setBackground(skin.getDrawable("card")); activities.pad(16);
        activities.add(locationTitle).left().growX().padBottom(10).row();
        activities.add(locationInfo).width(254).height(48).left().padBottom(8).row();
        activities.add(travel).growX().height(40).padBottom(16).row();
        activities.add(label("WERKZAAMHEDEN", "accent")).left().growX().padBottom(10).row();
        activities.add(jobs).growX().padBottom(14).row();
        activities.add(energyText).left().growX().padBottom(8).row();
        activities.add(energy).growX().height(10).padBottom(14).row();
        activities.add(sleep).growX().height(40).padBottom(8).row();
        activities.add(label("Red Creek / gratis / acht uur rust", "muted")).left();
        content.add(activities).width(290).padLeft(18).top();
        root.add(content).growX().padBottom(16).row();
        Table journal = new Table(); journal.setBackground(skin.getDrawable("card")); journal.pad(12);
        journal.add(status).growX().height(48).left(); root.add(journal).growX().padBottom(16).row();
        Table menu = new Table(); menu.defaults().height(44).padRight(8);
        menu.add(button("Nieuw spel", "new-game", this::confirmNewGame)).width(140);
        menu.add(button("Spel opslaan", "save-game", this::save)).width(150);
        menu.add(button("Spel laden", "load-game", this::load)).width(140);
        menu.add(inventory).width(185);
        menu.add(label("De klok wacht op jou.", "muted")).expandX();
        menu.add(button("Afsluiten", "quit", () -> Gdx.app.exit())).width(125).padRight(0);
        root.add(menu).growX(); refresh();
    }
    private Label label(String text, String style) { return new Label(text, skin, style); }
    private TextButton button(String text, String name, Runnable action) { return Ui.button(skin, text, name, action); }
    private void confirmTravel() {
        Location target = selected;
        Dialog dialog = new Dialog("Reizen naar " + target.displayName(), skin) {
            @Override protected void result(Object value) { if (Boolean.TRUE.equals(value)) message(game.travel(target)); }
        };
        String reason = game.travelBlockReason(target);
        dialog.text("Reistijd: " + Ui.duration(game.state().location().travelMinutesTo(target))
            + "\nEnergie: " + game.state().location().travelStaminaTo(target) + " punten"
            + (reason.isEmpty() ? "\nJe houdt genoeg energie over voor de terugweg." : "\n" + reason));
        dialog.getContentTable().pad(24);
        dialog.button("Terug", false); dialog.button("Op pad", true);
        Ui.nameDialogButtons(dialog, "travel-cancel", "travel-confirm");
        ((TextButton) dialog.getButtonTable().getChildren().get(1)).setDisabled(!reason.isEmpty());
        dialog.getButtonTable().pad(16); dialog.show(stage);
    }
    private void confirmNewGame() {
        Dialog dialog = new Dialog("Nieuw spel", skin) {
            @Override protected void result(Object value) {
                if (Boolean.TRUE.equals(value)) {
                    game.newGame(); selected = Location.RED_CREEK;
                    message("Een nieuw begin in Red Creek. Je oude opslag blijft bewaard totdat je opnieuw opslaat.");
                }
            }
        };
        dialog.text("Opnieuw beginnen? Niet-opgeslagen voortgang gaat verloren."); dialog.getContentTable().pad(24);
        dialog.button("Annuleren", false); dialog.button("Nieuw spel beginnen", true);
        Ui.nameDialogButtons(dialog, "new-cancel", "new-confirm"); dialog.getButtonTable().pad(16); dialog.show(stage);
    }
    private void save() {
        try { saves.save(game.state()); message("Spel opgeslagen. Je kunt later verdergaan, inclusief je inventaris."); }
        catch (IOException | RuntimeException e) { message("Opslaan is mislukt. Controleer de opslagmap en probeer opnieuw."); }
    }
    private void load() {
        try { game.load(saves.load()); selected = game.state().location(); message("Spel geladen. Welkom terug bij " + selected.displayName() + "."); }
        catch (IOException | RuntimeException e) { message("Laden is mislukt. Er is geen geldig opgeslagen spel beschikbaar."); }
    }
    private void message(String text) { status.setText(text); refresh(); }
    private void refresh() {
        Player p = game.state().player();
        playerHud.setText(p.name() + "  |  $" + p.money() + "  |  Niveau " + p.level()
            + "  |  Ervaring " + p.xp() + "/" + p.nextLevelXp() + "  |  Energie " + p.stamina() + "/100");
        timeHud.setText(game.state().time().display() + "\n" + game.state().location().displayName()); timeHud.setAlignment(Align.right);
        locationTitle.setText(selected.displayName().toUpperCase(java.util.Locale.ROOT));
        locationInfo.setText(switch (selected) {
            case RED_CREEK -> "Een stoffig dorp met een herberg en een voorraadschuur.";
            case PINE_FOREST -> "Dennen, kronkelende paden en verlaten houthakkersplekken.";
            case OLD_MINE -> "Een oude schacht tussen rotsen en achtergelaten mijnkarren.";
            case LONELY_RANCH -> "Een afgelegen erf met schuren, akkers en houten hekken.";
        });
        travel.setText(selected == game.state().location() ? "Je bent hier" : "Reizen naar deze locatie");
        travel.setDisabled(selected == game.state().location());
        jobs.clearChildren();
        for (Job job : game.jobsAt(selected)) {
            jobs.add(button(job.name(), "job-" + job.id(), () -> new WorkDialog(skin, game, job, this::message).show(stage)))
                .growX().height(38).padBottom(7).row();
        }
        energyText.setText("ENERGIE  " + p.stamina() + " / 100"); energy.setValue(p.stamina());
        inventory.setText("Inventaris (" + game.state().inventory().totalCount() + ")");
        sleep.setDisabled(game.state().location() != Location.RED_CREEK);
        map.refresh(game, selected);
    }
    @Override public void show() { Gdx.input.setInputProcessor(stage); }
    @Override public void render(float delta) { ScreenUtils.clear(.10f, .07f, .04f, 1); stage.act(Math.min(delta, 1 / 30f)); stage.draw(); }
    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void hide() { if (Gdx.input.getInputProcessor() == stage) Gdx.input.setInputProcessor(null); }
    @Override public void dispose() { stage.dispose(); map.dispose(); skin.dispose(); }
}

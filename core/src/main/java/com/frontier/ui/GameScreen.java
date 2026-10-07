package com.frontier.ui;

import com.badlogic.gdx.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.frontier.logic.GameSession;
import com.frontier.model.*;
import com.frontier.save.SaveManager;
import java.io.IOException;

public final class GameScreen extends ScreenAdapter {
    private final GameSession game;
    private final SaveManager saves;
    private final Skin skin = FrontierSkin.create();
    private final Stage stage = new Stage(new ScreenViewport());
    private final WorkProgress workProgress = new WorkProgress(skin);
    private final ActionBar actionBar = new ActionBar(skin);
    private final Label locationTitle = label("", "accent"), locationInfo = label("", "muted");
    private final Label status = label("Welkom in Red Creek. Twintig dollar en een nieuw begin. Kies een locatie of zoek een klus.", "default");
    private final Table jobs = new Table();
    private final TextButton travel, town, inventory, messages;
    private final MapPanel map;
    private Cell<WorkProgress> workProgressCell;
    private Location selected = Location.RED_CREEK;

    public GameScreen(GameSession game, SaveManager saves) {
        this.game = game; this.saves = saves;
        travel = button("Reizen", "travel", this::confirmTravel);
        town = button("Stad openen", "town", () -> new TownDialog(skin, game, this::message).show(stage));
        inventory = button("Inventaris", "inventory", () -> new InventoryDialog(skin, game.state().inventory()).show(stage));
        messages = button("Berichten", "messages", () -> new MessagesDialog(skin, game.state().mailbox(), this::refresh).show(stage));
        map = new MapPanel(skin, location -> { selected = location; refresh(); },
            site -> new LandmarkDialog(skin, site).show(stage));
        status.setWrap(true); locationInfo.setWrap(true);
        status.setName("status");
        Table root = new Table(); root.setFillParent(true); root.pad(20); root.setBackground(skin.getDrawable("wood")); stage.addActor(root);
        Table heading = new Table(); heading.add(label("FRONTIER", "title")).left().expandX();
        heading.add(label("EEN LEVEN AAN DE GRENS  /  1880", "muted")).right();
        root.add(heading).growX().padBottom(16).row();
        root.add(actionBar).growX().padBottom(14).row();
        Table content = new Table(), territory = new Table();
        territory.add(label("DE FRONTIER", "accent")).left().growX().padBottom(10).row();
        territory.add(map).minSize(0, 0).prefSize(800, 380).grow().row();
        Table mapTools = new Table();
        mapTools.add().expandX();
        mapTools.add(button("Kaart centreren", "map-center", map::center)).width(180).height(34);
        territory.add(mapTools).growX().padTop(10);
        content.add(territory).minWidth(0).grow();
        Table activities = new Table(); activities.setBackground(skin.getDrawable("card")); activities.pad(16);
        activities.add(locationTitle).left().growX().padBottom(10).row();
        activities.add(locationInfo).width(254).height(64).left().padBottom(8).row();
        activities.add(travel).growX().height(40).padBottom(12).row();
        workProgressCell = activities.add(workProgress).growX().height(0); activities.row();
        ScrollPane jobList = new ScrollPane(jobs, skin); jobList.setFadeScrollBars(false);
        jobList.setScrollingDisabled(true, false); jobList.setName("job-list");
        activities.add(jobList).width(254).minHeight(100).prefHeight(200).growY().padBottom(12).row();
        activities.add(town).growX().height(42).row();
        content.add(activities).width(290).padLeft(18).growY();
        root.add(content).grow().padBottom(16).row();
        Table journal = new Table(); journal.setBackground(skin.getDrawable("card")); journal.pad(12);
        journal.add(status).growX().height(48).left(); root.add(journal).growX().padBottom(16).row();
        Table menu = new Table(); menu.defaults().height(44).padRight(8);
        menu.add(inventory).width(200);
        menu.add(messages).width(200);
        menu.add(label("De klok wacht op jou.", "muted")).expandX();
        menu.add(button("Instellingen", "settings", () ->
            new SettingsDialog(skin, this::confirmNewGame, this::save, this::load).show(stage))).width(175).padRight(0);
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
                    game.newGame(); selected = Location.RED_CREEK; map.center();
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
        actionBar.refresh(game.state());
        locationTitle.setText(selected.displayName().toUpperCase(java.util.Locale.ROOT));
        locationInfo.setText(switch (selected) {
            case RED_CREEK -> "Een stoffig dorp met een herberg en een voorraadschuur.";
            case PINE_FOREST -> "Dennen, kronkelende paden en verlaten houthakkersplekken.";
            case OLD_MINE -> "Een oude schacht tussen rotsen en achtergelaten mijnkarren.";
            case LONELY_RANCH -> "Een afgelegen erf met schuren, akkers en houten hekken.";
            case WILLOW_FARM -> "Een melkboerderij met stallen, kippenrennen en wilgen.";
            case SUNRISE_FARM -> "Maisvelden en een boomgaard rond een zonnig erf.";
            case RIVER_FARM -> "Groenteakkers en irrigatiekanalen langs de rivier.";
            case COTTON_FARM -> "Katoenvelden, een opslagschuur en stapels balen.";
            case NORTH_WOODS -> "Een uitgestrekt dennenwoud met een houthakkerskamp.";
            case QUARRY -> "Een steengroeve met grindhopen en leisteen.";
            case RIVERBANK -> "Rietkragen, visnetten en een kleine aanlegsteiger.";
            case TRADING_POST -> "Een handelspost met een magazijn en vrachtkisten.";
        });
        travel.setText(selected == game.state().location() ? "Je bent hier" : "Reizen naar deze locatie");
        travel.setDisabled(selected == game.state().location() || game.isWorking());
        workProgressCell.height(game.isWorking() ? 92 : 0); workProgress.refresh(game);
        jobs.clearChildren();
        if (selected == Location.RED_CREEK) {
            Label note = label(game.state().location() == Location.RED_CREEK
                ? "Red Creek is je uitvalsbasis. Open de stad om gebouwen te bezoeken. Werk vind je buiten de stad."
                : "Reis naar Red Creek om de stad te openen en uit te rusten.", "muted");
            note.setWrap(true); jobs.add(note).width(235).pad(8);
        } else {
            for (Job job : game.jobsAt(selected)) {
                jobs.add(button(job.name(), "job-" + job.id(), () -> new WorkDialog(skin, game, job, this::message).show(stage)))
                    .growX().height(38).padBottom(7).row();
            }
        }
        inventory.setText("Inventaris (" + game.state().inventory().totalCount() + ")");
        messages.setText("Berichten (" + game.state().mailbox().unreadCount() + ")");
        town.setVisible(selected == Location.RED_CREEK);
        town.setDisabled(game.state().location() != Location.RED_CREEK || game.isWorking());
        map.refresh(game, selected);
    }
    @Override public void show() { Gdx.input.setInputProcessor(stage); }
    @Override public void render(float delta) {
        ScreenUtils.clear(.10f, .07f, .04f, 1);
        String completed = game.updateWork();
        if (!completed.isEmpty()) message(completed);
        workProgress.refresh(game); stage.act(Math.min(delta, 1 / 30f)); stage.draw();
    }
    @Override public void resize(int width, int height) {
        if (width <= 0 || height <= 0) return;
        ScreenViewport viewport = (ScreenViewport) stage.getViewport();
        // Houd kleine vensters bruikbaar; grotere vensters krijgen meer ruimte.
        viewport.setUnitsPerPixel(Math.max(1f, Math.max(1200f / width, 800f / height)));
        viewport.update(width, height, true);
        for (var actor : stage.getRoot().getChildren()) {
            if (actor instanceof Dialog dialog) dialog.setPosition(
                (stage.getWidth() - dialog.getWidth()) / 2, (stage.getHeight() - dialog.getHeight()) / 2);
        }
    }
    @Override public void hide() { if (Gdx.input.getInputProcessor() == stage) Gdx.input.setInputProcessor(null); }
    @Override public void dispose() { stage.dispose(); map.dispose(); skin.dispose(); }
}

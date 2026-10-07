package com.frontier.lwjgl3;

import com.badlogic.gdx.*;
import com.badlogic.gdx.backends.lwjgl3.*;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.frontier.logic.GameSession;
import com.frontier.model.*;
import com.frontier.save.SaveManager;
import com.frontier.ui.GameScreen;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/** Echte desktopbediening, met een geïsoleerd tijdelijk opslagbestand. */
public final class DesktopSmoke extends Game {
    private final GameSession session = new GameSession(new Random(1880));
    private final SaveManager saves;
    private Throwable failure;
    private int frame;
    private Map<Item, Integer> savedInventory;
    private int savedMoney;
    private DesktopSmoke(Path directory) { saves = new SaveManager(directory.resolve("save.json")); }
    public static void main(String[] args) throws Exception {
        Path directory = Files.createTempDirectory("frontier-smoke-");
        DesktopSmoke app = new DesktopSmoke(directory);
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Frontier - desktopcontrole"); config.setWindowedMode(1200, 800);
        config.disableAudio(true); config.setForegroundFPS(60);
        try {
            new Lwjgl3Application(app, config);
            if (app.failure != null) throw new AssertionError("Desktopcontrole mislukt", app.failure);
            check(app.frame >= 14, "Alle stappen moeten zijn uitgevoerd.");
            System.out.println("DESKTOP SMOKE PASSED: Nederlandse UI, detailmenu, annuleren, alle klussen, terugreis, vondsten, inventarisfilters, opslaan/laden, nieuw spel, formaat en afsluiten.");
        } finally { Files.deleteIfExists(directory.resolve("save.json")); Files.deleteIfExists(directory); }
    }
    @Override public void create() { setScreen(new GameScreen(session, saves)); }
    @Override public void render() {
        super.render();
        try {
            frame++;
            if (frame == 3) {
                check(session.state().time().value().equals(GameTime.START), "De klok mag niet lopen tijdens wachten.");
                for (String name : new String[]{"new-game", "save-game", "load-game", "quit", "inventory", "sleep"}) {
                    Actor actor = stage().getRoot().findActor(name);
                    Vector2 point = actor.localToStageCoordinates(new Vector2());
                    check(point.y >= 0 && point.y + actor.getHeight() <= 800, "Knop past in beeld: " + name);
                }
                check(labelText(stage().getRoot()).contains("Spel opslaan"), "Het hoofdmenu is Nederlandstalig.");
                check(labelText(stage().getRoot()).contains("1 april 1880"), "De datum is Nederlandstalig.");
                // Lege inventaris en filters kosten geen tijd.
                click("inventory"); click("inventory-WEAPON"); click("inventory-close");
                click("travel-PINE_FOREST");
                check(session.state().location() == Location.RED_CREEK, "Kaartselectie mag niet direct reizen.");
                check(((TextButton) stage().getRoot().findActor("job-wood")).getText().toString().equals("Hout hakken"), "Werkknop toont alleen de naam.");
                click("job-wood");
                check(session.state().player().money() == 20, "Detailmenu mag geen werk uitvoeren.");
                check(labelText(stage().getRoot()).contains("Reistijd erheen"), "Reistijd staat in het menu.");
                click("work-cancel");
                check(session.state().time().value().equals(GameTime.START), "Annuleren kost geen tijd.");
                check(session.state().player().stamina() == 100, "Annuleren kost geen energie.");
                click("job-wood"); click("work-confirm");
                check(session.state().location() == Location.PINE_FOREST, "Werkmenu reist naar de klus.");
                check(session.state().player().xp() == 20, "Werkmenu geeft ervaring.");
                check(session.state().player().stamina() == 61, "Reis en werk verbruiken samen 39 energie.");
                check(session.state().time().value().equals(GameTime.START.plusMinutes(292)), "Reistijd en werkduur kloppen samen.");
            } else if (frame == 4) {
                goHomeAndRest();
                // Alle twaalf werkzaamheden via hun menu uitvoeren.
                for (Location location : Location.values()) {
                    for (Job job : session.jobsAt(location)) {
                        click("travel-" + location.name());
                        click("job-" + job.id()); click("work-confirm");
                        check(session.state().location() == location, "Kluslocatie klopt.");
                        goHomeAndRest();
                    }
                }
            } else if (frame == 5) {
                click("travel-OLD_MINE"); click("travel"); click("travel-confirm");
                while (session.workBlockReason().isEmpty()) { click("job-ore"); click("work-confirm"); }
                click("job-ore");
                check(((TextButton) stage().getRoot().findActor("work-confirm")).isDisabled(), "Te moe: bevestigen is uitgeschakeld.");
                click("work-cancel"); goHomeAndRest();
                check(session.state().inventory().totalCount() > 0, "Werkzaamheden moeten daadwerkelijk vondsten opleveren.");
                // Aanvullende fixtures zorgen dat alle filter- en beschrijvingsknoppen worden aangeklikt.
                session.state().inventory().add(Item.REVOLVER); session.state().inventory().add(Item.COAT); session.state().inventory().add(Item.WOOD);
                click("inventory"); click("inventory-WEAPON"); click("item-REVOLVER");
                check(labelText(stage().getRoot()).contains("zesschieter"), "Beschrijving van het wapen verschijnt.");
                click("inventory-CLOTHING"); click("item-COAT"); click("inventory-PRODUCT"); click("item-WOOD");
                click("inventory-all");
            } else if (frame == 6) {
                capture("build/frontier-inventory.png"); click("inventory-close");
                click("save-game"); savedMoney = session.state().player().money();
                savedInventory = new EnumMap<>(session.state().inventory().contents());
                click("job-wagons"); click("work-confirm");
                click("load-game");
                check(session.state().player().money() == savedMoney, "Laden herstelt geld.");
                check(session.state().inventory().contents().equals(savedInventory), "Laden herstelt alle voorwerpen.");
                click("new-game"); click("new-cancel");
                check(session.state().player().money() == savedMoney, "Nieuw spel annuleren behoudt voortgang.");
                click("new-game"); click("new-confirm");
                check(session.state().inventory().totalCount() == 0, "Nieuw spel leegt inventaris.");
                check(session.state().time().value().equals(GameTime.START), "Nieuw spel herstelt de klok.");
            } else if (frame == 8) {
                capture("build/frontier-desktop.png");
                click("travel-PINE_FOREST"); click("job-wood");
                check(stage().getRoot().findActor("work-confirm") != null, "Werkmenu moet zichtbaar zijn voor de opname.");
            } else if (frame == 9) { capture("build/frontier-work.png"); click("work-cancel"); }
            else if (frame == 10) { Gdx.graphics.setWindowedMode(960, 640); }
            else if (frame == 12) {
                click("job-branches"); click("work-confirm");
                check(session.state().location() == Location.PINE_FOREST, "Menu werkt ook na verkleinen.");
            } else if (frame == 14) { capture("build/frontier-desktop-small.png"); click("quit"); }
        } catch (Throwable e) { failure = e; capture("build/frontier-failure.png"); Gdx.app.exit(); }
    }
    private Stage stage() { return (Stage) Gdx.input.getInputProcessor(); }
    private void goHomeAndRest() {
        if (session.state().location() != Location.RED_CREEK) {
            click("travel-RED_CREEK"); click("travel"); click("travel-confirm");
        }
        var before = session.state().time().value(); click("sleep");
        check(session.state().player().stamina() == 100, "Slapen herstelt alle energie.");
        check(session.state().time().value().equals(before.plusHours(8)), "Slapen duurt acht uur.");
    }
    private void click(String name) {
        Stage stage = stage();
        ((Table) stage.getRoot().getChildren().first()).validate();
        for (Actor child : stage.getRoot().getChildren()) if (child instanceof Table table) table.validate();
        stage.draw(); // Layout van nieuw aangemaakte knoppen afronden vóór de muisklik.
        Actor actor = stage.getRoot().findActor(name);
        check(actor != null, "Knop bestaat: " + name);
        check(!(actor instanceof TextButton button) || !button.isDisabled(), "Knop is beschikbaar: " + name);
        Vector2 point = actor.localToStageCoordinates(new Vector2(actor.getWidth() / 2, actor.getHeight() / 2));
        stage.stageToScreenCoordinates(point);
        stage.touchDown(Math.round(point.x), Math.round(point.y), 0, Input.Buttons.LEFT);
        stage.touchUp(Math.round(point.x), Math.round(point.y), 0, Input.Buttons.LEFT);
        for (int i = 0; i < 4; i++) stage.act(.5f); // Rond opeenvolgende dialooganimaties af zonder de spelklok te veranderen.
    }
    private static String labelText(Actor actor) {
        if (actor instanceof Label label) return label.getText().toString();
        StringBuilder text = new StringBuilder();
        if (actor instanceof Group group) for (Actor child : group.getChildren()) text.append(labelText(child)).append('\n');
        return text.toString();
    }
    private void capture(String name) {
        for (Actor actor : stage().getRoot().getChildren()) {
            if (actor instanceof Dialog) check(actor.getColor().a > .99f, "Dialoog moet volledig zichtbaar zijn.");
        }
        stage().draw();
        Pixmap pixels = Pixmap.createFromFrameBuffer(0, 0, Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight());
        try { PixmapIO.writePNG(Gdx.files.local(name), pixels, -1, true); } finally { pixels.dispose(); }
    }
    private static void check(boolean condition, String message) { if (!condition) throw new AssertionError(message); }
    @Override public void dispose() { if (getScreen() != null) getScreen().dispose(); }
}

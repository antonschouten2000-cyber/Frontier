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
import java.time.*;

/** Echte desktopbediening, met een geïsoleerd tijdelijk opslagbestand. */
public final class DesktopSmoke extends Game {
    private final TestClock clock = new TestClock();
    private final GameSession session = new GameSession(new Random(1880), clock);
    private boolean autoFinishWork = true;
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
            check(app.frame >= 20, "Alle stappen moeten zijn uitgevoerd.");
            System.out.println("DESKTOP SMOKE PASSED: glad lettertype, uitgezoomde kaart en meegroeiende interface, vier toekomstige bezittingen, kaart slepen, statusbalken, stadsgebouwen, herberg, 42 klussen met echte werktimers, inventaris, instellingen, opslag, formaat en afsluiten.");
        } finally { Files.deleteIfExists(directory.resolve("save.json")); Files.deleteIfExists(directory); }
    }
    @Override public void create() { setScreen(new GameScreen(session, saves)); }
    @Override public void render() {
        super.render();
        try {
            frame++;
            if (frame == 3) {
                check(session.state().time().value().equals(GameTime.START), "De klok mag niet lopen tijdens wachten.");
                for (String name : new String[]{"settings", "inventory", "town", "map-center"}) {
                    Actor actor = stage().getRoot().findActor(name);
                    Vector2 point = actor.localToStageCoordinates(new Vector2());
                    check(point.y >= 0 && point.y + actor.getHeight() <= 800, "Knop past in beeld: " + name);
                }
                check(stage().getRoot().findActor("save-game") == null, "Opslaan staat alleen onder instellingen.");
                check(stage().getRoot().findActor("job-wagons") == null, "In de stad zijn geen werkzaamheden.");
                check(stage().getRoot().findActor("sleep") == null, "Slapen staat alleen in de herberg.");
                check(labelText(stage().getRoot()).contains("Instellingen"), "Het hoofdmenu is Nederlandstalig.");
                verifyDragging();
                verifyLandmarks();
                click("town");
                var townTime = session.state().time().value();
                for (String building : new String[]{"GUNSMITH", "TOWN_HALL", "TAILOR", "BANK", "INN"}) click("building-" + building);
                check(session.state().time().value().equals(townTime), "Gebouwen bekijken kost geen tijd.");
                check(stage().getRoot().findActor("job-wood") == null, "Het stadsvenster biedt geen arbeid.");
                capture("build/frontier-town.png"); click("town-close");
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
                check(Math.abs(((ProgressBar) stage().getRoot().findActor("xp-bar")).getValue() - .4f) < .01f, "Ervaringsbalk toont voortgang naar het volgende niveau.");
                check(((ProgressBar) stage().getRoot().findActor("energy-bar")).getValue() == 61, "Energiebalk verandert na een actie.");
                click("travel-RED_CREEK");
                check(((TextButton) stage().getRoot().findActor("town")).isDisabled(), "Je moet eerst naar de stad reizen.");
                click("travel-PINE_FOREST");
                check(session.state().player().stamina() == 61, "Reis en werk verbruiken samen 39 energie.");
                check(session.state().time().value().equals(GameTime.START.plusMinutes(172)), "Reistijd en gekozen uur werk kloppen samen.");
                session.state().player().reward(0, 30); // Exacte grens naar niveau 2 voor de weergavetest.
                click("travel-PINE_FOREST");
                check(session.state().player().level() == 2, "De fixture bereikt niveau 2.");
                check(((ProgressBar) stage().getRoot().findActor("xp-bar")).getValue() == 0, "Ervaringsbalk begint opnieuw na niveauverhoging.");
            } else if (frame == 4) {
                goHomeAndRest();
                verifyDurationChoices();
                // Alle werkzaamheden via hun menu uitvoeren met een vervangbare testklok.
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
                click("settings"); click("save-game"); savedMoney = session.state().player().money();
                savedInventory = new EnumMap<>(session.state().inventory().contents());
                click("travel-PINE_FOREST"); click("job-wood"); click("work-confirm");
                click("settings"); click("load-game");
                check(session.state().player().money() == savedMoney, "Laden herstelt geld.");
                check(session.state().inventory().contents().equals(savedInventory), "Laden herstelt alle voorwerpen.");
                click("settings"); click("new-game"); click("new-cancel");
                check(session.state().player().money() == savedMoney, "Nieuw spel annuleren behoudt voortgang.");
                click("settings"); click("new-game"); click("new-confirm");
                check(session.state().inventory().totalCount() == 0, "Nieuw spel leegt inventaris.");
                check(session.state().time().value().equals(GameTime.START), "Nieuw spel herstelt de klok.");
            } else if (frame == 8) {
                check(!labelText(stage().getRoot()).contains("Sleep de kaart"), "De sleepinstructie is verwijderd.");
                capture("build/frontier-desktop.png");
                click("travel-PINE_FOREST"); click("job-wood");
                check(stage().getRoot().findActor("work-confirm") != null, "Werkmenu moet zichtbaar zijn voor de opname.");
            } else if (frame == 9) { capture("build/frontier-work.png"); click("work-cancel"); }
            else if (frame == 10) { Gdx.graphics.setWindowedMode(960, 640); }
            else if (frame == 12) {
                click("job-branches"); click("work-confirm");
                check(session.state().location() == Location.PINE_FOREST, "Menu werkt ook na verkleinen.");
            } else if (frame == 14) { capture("build/frontier-desktop-small.png"); Gdx.graphics.setWindowedMode(1920, 1080); }
            else if (frame == 17) {
                ScrollPane map = stage().getRoot().findActor("world-map");
                check(stage().getWidth() >= 1920 && stage().getHeight() >= 1080, "De interface gebruikt de grotere vensterruimte.");
                check(map.getWidth() > 1400 && map.getHeight() > 600, "Het kaartpaneel groeit in beide richtingen.");
                Image background = (Image) ((Group) map.getActor()).getChildren().first();
                check(background.getWidth() == WorldMap.WIDTH * .75f, "Een groter venster zoomt het terrein niet opnieuw in.");
                click("map-center"); goHomeAndRest(); click("town");
                capture("build/frontier-desktop-large-town.png"); click("town-close");
            } else if (frame == 20) { capture("build/frontier-desktop-large.png"); click("settings"); click("quit"); }
        } catch (Throwable e) { failure = e; capture("build/frontier-failure.png"); Gdx.app.exit(); }
    }
    private Stage stage() { return (Stage) Gdx.input.getInputProcessor(); }
    private void goHomeAndRest() {
        if (session.state().location() != Location.RED_CREEK) {
            click("travel-RED_CREEK"); click("travel"); click("travel-confirm");
        }
        var before = session.state().time().value();
        click("town"); click("building-INN"); click("sleep"); click("town-close");
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
        ensureVisible(actor);
        stage.draw();
        check(!(actor instanceof TextButton button) || !button.isDisabled(), "Knop is beschikbaar: " + name);
        Vector2 point = actor.localToStageCoordinates(new Vector2(actor.getWidth() / 2, actor.getHeight() / 2));
        stage.stageToScreenCoordinates(point);
        stage.touchDown(Math.round(point.x), Math.round(point.y), 0, Input.Buttons.LEFT);
        stage.touchUp(Math.round(point.x), Math.round(point.y), 0, Input.Buttons.LEFT);
        for (int i = 0; i < 4; i++) stage.act(.5f); // Dialooganimaties veranderen de echte testklok niet.
        if (name.equals("work-confirm") && autoFinishWork && session.isWorking()) {
            clock.advanceSeconds(session.state().activeWork().duration().seconds());
            getScreen().render(0); // De game, niet de klikhelper, rondt de verstreken klus af.
        }
    }
    private void verifyDurationChoices() {
        goHomeAndRest(); click("travel-PINE_FOREST"); click("job-wood");
        for (WorkDuration duration : WorkDuration.values()) {
            click("work-duration-" + duration.name());
            check(labelText(stage().getRoot().findActor("work-details")).contains(duration.displayName()), "De details veranderen met de werktijd.");
        }
        click("work-duration-QUICK"); autoFinishWork = false;
        int cash = session.state().player().money(); click("work-confirm");
        check(session.isWorking(), "De klus start een echte timer.");
        check(session.state().player().money() == cash, "Beloningen komen niet meteen.");
        check(((TextButton) stage().getRoot().findActor("travel")).isDisabled(), "Reizen is geblokkeerd tijdens werk.");
        clock.advanceSeconds(7); getScreen().render(0);
        ProgressBar bar = stage().getRoot().findActor("active-work-bar");
        check(bar.isVisible() && Math.abs(bar.getValue() - 7f / 15) < .01f, "De voortgangsbalk volgt de echte werktijd.");
        capture("build/frontier-active-work.png");
        click("settings"); click("save-game"); click("settings"); click("load-game");
        check(session.isWorking() && session.workSecondsRemaining() == 8, "Opslaan en laden hervatten de resterende tijd.");
        clock.advanceSeconds(8); getScreen().render(0);
        check(!session.isWorking(), "Een klus van vijftien seconden is klaar na vijftien seconden.");
        check(session.state().player().money() > cash, "Een korte klus levert geld op.");
        autoFinishWork = true; goHomeAndRest();
    }
    private static final class TestClock extends Clock {
        private Instant now = Instant.parse("2026-10-07T12:00:00Z");
        void advanceSeconds(long seconds) { now = now.plusSeconds(seconds); }
        @Override public ZoneId getZone() { return ZoneOffset.UTC; }
        @Override public Clock withZone(ZoneId zone) { return Clock.fixed(now, zone); }
        @Override public Instant instant() { return now; }
    }
    private void ensureVisible(Actor actor) {
        for (Actor parent = actor.getParent(); parent != null; parent = parent.getParent()) {
            if (parent instanceof ScrollPane pane) {
                Vector2 local = actor.localToAscendantCoordinates(pane.getActor(), new Vector2());
                pane.scrollTo(local.x, local.y, actor.getWidth(), actor.getHeight(), true, true);
                pane.updateVisualScroll();
            }
        }
    }
    private void verifyLandmarks() {
        ScrollPane map = stage().getRoot().findActor("world-map");
        Image background = (Image) ((Group) map.getActor()).getChildren().first();
        check(background.getWidth() == WorldMap.WIDTH * .75f && background.getHeight() == WorldMap.HEIGHT * .75f,
            "De kaart toont dezelfde volledige wereld met 25 procent kleinere terreindetails.");
        var time = session.state().time().value();
        int cash = session.state().player().money(), energy = session.state().player().stamina();
        for (Landmark site : Landmark.values()) {
            click("landmark-" + site.name());
            check(labelText(stage().getRoot()).contains(site.displayName()), "Het informatievenster noemt de plek.");
            check(labelText(stage().getRoot()).contains("Prijs nog te bepalen"), "Er is nog geen aankoopprijs vastgesteld.");
            check(((TextButton) stage().getRoot().findActor("landmark-buy")).isDisabled(), "Aankopen zijn voor een volgende versie.");
            capture("build/frontier-landmark-" + site.name() + ".png");
            click("landmark-close");
        }
        check(session.state().time().value().equals(time), "Plekken bekijken kost geen tijd.");
        check(session.state().player().money() == cash && session.state().player().stamina() == energy,
            "Plekken bekijken kost geen geld of energie.");
        click("map-center");
    }
    private void verifyDragging() {
        ScrollPane map = stage().getRoot().findActor("world-map");
        float beforeX = map.getScrollX(), beforeY = map.getScrollY();
        Vector2 start = map.localToStageCoordinates(new Vector2(405, 190));
        stage().stageToScreenCoordinates(start);
        stage().touchDown(Math.round(start.x), Math.round(start.y), 0, Input.Buttons.LEFT);
        for (int i = 1; i <= 6; i++) stage().touchDragged(Math.round(start.x - i * 14), Math.round(start.y + i * 8), 0);
        stage().touchUp(Math.round(start.x - 84), Math.round(start.y + 48), 0, Input.Buttons.LEFT);
        stage().act(.05f); stage().draw();
        check(Math.abs(map.getScrollX() - beforeX) > 10 || Math.abs(map.getScrollY() - beforeY) > 10, "De kaart beweegt bij verslepen.");
        check(session.state().time().value().equals(GameTime.START), "Kaart slepen kost geen speltijd.");
        check(session.state().location() == Location.RED_CREEK, "Kaart slepen reist niet.");
        click("map-center");
        // Ook slepen vanaf een locatieknop mag geen onbedoelde selectie doen.
        Actor marker = stage().getRoot().findActor("travel-PINE_FOREST"); ensureVisible(marker); stage().draw();
        Vector2 point = marker.localToStageCoordinates(new Vector2(60, 25)); stage().stageToScreenCoordinates(point);
        stage().touchDown(Math.round(point.x), Math.round(point.y), 0, Input.Buttons.LEFT);
        for (int i = 1; i <= 6; i++) stage().touchDragged(Math.round(point.x + i * 12), Math.round(point.y), 0);
        stage().touchUp(Math.round(point.x + 72), Math.round(point.y), 0, Input.Buttons.LEFT);
        stage().act(.05f); stage().draw();
        check(stage().getRoot().findActor("job-wood") == null, "Een sleepbeweging vanaf een locatieknop selecteert die niet.");
        click("map-center");
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

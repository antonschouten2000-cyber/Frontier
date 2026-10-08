package com.frontier.logic;

import com.frontier.model.*;
import com.frontier.save.SaveManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.time.Clock;
import java.nio.file.*;
import java.io.IOException;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

class VillageStoryTest {
    @TempDir Path directory;
    private GameSession funded(){
        GameSession game=new GameSession(new Random(1),Clock.systemUTC(),new Random(){@Override public double nextDouble(){return 0;}});
        game.state().player().reward(1000,0);game.state().inventory().add(Item.WOOD,100);game.state().inventory().add(Item.STONE,100);return game;
    }
    private void build(GameSession game,VillageProject project){assertEquals("",game.story().selectReason(project));game.story().select(project);assertEquals("",game.story().finishReason());game.story().finish();}
    @Test void firstChoiceReservesPromiseUntilCompleteAndMaterialsAreConsumedOnce(){
        GameSession game=funded();game.story().select(VillageProject.WELL);
        assertEquals(VillageProject.WELL,game.state().journal().first());
        assertFalse(game.story().selectReason(VillageProject.VERANDA).isEmpty());game.story().select(VillageProject.VERANDA);
        assertEquals(VillageProject.WELL,game.state().journal().active());
        int cash=game.state().player().money();game.story().finish();
        assertEquals(cash-15,game.state().player().money());assertEquals(96,game.state().inventory().count(Item.WOOD));assertEquals(88,game.state().inventory().count(Item.STONE));
        assertEquals(GameTime.START.plusHours(2),game.state().time().value());
        game.story().finish();assertEquals(cash-15,game.state().player().money());
        build(game,VillageProject.VERANDA);assertEquals(VillageProject.WELL,game.state().journal().first());
    }
    @Test void unavailableFundsLocationOrRunningWorkLeaveProjectsUnchanged(){
        GameSession game=new GameSession();game.story().select(VillageProject.VERANDA);game.story().finish();
        assertEquals(20,game.state().player().money());assertTrue(game.state().journal().completed().isEmpty());assertEquals(GameTime.START,game.state().time().value());
        game.travel(Location.PINE_FOREST);assertFalse(game.story().finishReason().isEmpty());
        game.startWork(game.currentJob(),WorkDuration.QUICK);assertFalse(game.story().finishReason().isEmpty());
    }
    @Test void bridgeGatesBothTravelAndWorkThenCreatesNewDestination(){
        GameSession game=funded();Job job=game.jobsAt(Location.FORGOTTEN_STOP).getFirst();
        assertFalse(game.travelBlockReason(Location.FORGOTTEN_STOP).isEmpty());assertFalse(game.workBlockReason(job).isEmpty());
        assertFalse(game.story().selectReason(VillageProject.BRIDGE).isEmpty());
        build(game,VillageProject.VERANDA);build(game,VillageProject.BRIDGE);
        assertEquals("",game.travelBlockReason(Location.FORGOTTEN_STOP));game.work(job,WorkDuration.QUICK);
        assertEquals(Location.FORGOTTEN_STOP,game.state().location());assertEquals("",game.travelBlockReason(Location.RED_CREEK));
        assertTrue(game.state().journal().pages().stream().anyMatch(p->p.title().equals("Aan de andere oever")));
    }
    @Test void projectBenefitsApplyOnlyToRelevantActionAndStackWithClothing(){
        GameSession game=funded();Job farm=game.jobsAt(Location.WILLOW_FARM).getFirst();Job other=game.jobsAt(Location.SUNRISE_FARM).getFirst();
        int before=game.workCost(farm,WorkDuration.LONG),otherBefore=game.workCost(other,WorkDuration.LONG);
        build(game,VillageProject.WELL);assertTrue(game.workCost(farm,WorkDuration.LONG)<before);assertEquals(otherBefore,game.workCost(other,WorkDuration.LONG));
        game.state().inventory().add(Item.WORK_SHIRT);game.toggleEquipment(Item.WORK_SHIRT);assertTrue(game.workCost(farm,WorkDuration.LONG)<before);
        assertEquals(480,game.story().sleepMinutes());build(game,VillageProject.VERANDA);assertEquals(384,game.story().sleepMinutes());
        var time=game.state().time().value();game.sleep();assertEquals(time.plusMinutes(384),game.state().time().value());
    }
    @Test void discoveriesAreUniqueOnlyAfterWorkAndKeepDistinctDatedSketches(){
        GameSession game=funded();Job forest=game.jobsAt(Location.PINE_FOREST).getFirst();
        assertTrue(game.state().journal().discoveries().isEmpty());game.startWork(forest,WorkDuration.QUICK);
        assertTrue(game.state().journal().discoveries().isEmpty());
        game.newGame();game.work(forest,WorkDuration.QUICK);
        assertTrue(game.state().journal().found(Discovery.OLD_MAP));int size=game.state().journal().pages().size();
        game.work(forest,WorkDuration.QUICK);assertEquals(size,game.state().journal().pages().size());
        game.travel(Location.RED_CREEK);game.sleep();game.work(game.jobsAt(Location.OLD_MINE).getFirst(),WorkDuration.QUICK);
        assertTrue(game.state().journal().found(Discovery.LETTER));
        assertTrue(game.state().journal().pages().stream().anyMatch(p->p.sketch().equals("map")));
    }
    @Test void allStoryFieldsSurviveSaveAndLoadAndNewGameResetsThem() throws IOException {
        GameSession game=funded();build(game,VillageProject.WELL);build(game,VillageProject.BRIDGE);
        game.work(game.jobsAt(Location.FORGOTTEN_STOP).getFirst(),WorkDuration.QUICK);assertTrue(game.state().journal().found(Discovery.CELLAR));
        SaveManager saves=new SaveManager(directory.resolve("save.json"));saves.save(game.state());GameState loaded=saves.load();
        assertEquals(game.state().journal().first(),loaded.journal().first());assertEquals(game.state().journal().completed(),loaded.journal().completed());
        assertEquals(game.state().journal().pages(),loaded.journal().pages());assertEquals(game.state().journal().discoveries(),loaded.journal().discoveries());
        game.load(loaded);game.newGame();assertNull(game.state().journal().first());assertTrue(game.state().journal().discoveries().isEmpty());assertEquals(1,game.state().journal().pages().size());
    }
    @Test void oldVersionSixSaveMigratesWithoutPretendingProjectsWereBuilt() throws IOException {
        Path file=directory.resolve("save.json");SaveManager saves=new SaveManager(file);saves.save(new GameState());
        Files.writeString(file,Files.readString(file).replace("\"version\": 7","\"version\": 6"));
        GameState loaded=saves.load();assertTrue(loaded.journal().completed().isEmpty());assertEquals(1,loaded.journal().pages().size());
    }
    @Test void corruptProjectStateAndMissingJournalAreRejected() throws IOException {
        Path file=directory.resolve("save.json");SaveManager saves=new SaveManager(file);GameSession game=funded();game.story().select(VillageProject.WELL);saves.save(game.state());
        String valid=Files.readString(file);
        Files.writeString(file,valid.replace("\"first\": \"WELL\"","\"first\": \"BRIDGE\""));assertThrows(IOException.class,saves::load);
        Files.writeString(file,valid.replace("\"journal\": {","\"missingJournal\": {"));assertThrows(IOException.class,saves::load);
    }
}

package com.frontier.logic;

import com.frontier.model.*;
import com.frontier.save.SaveManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;

class SaloonGameplayTest {
    @TempDir Path directory;
    @Test void deliveryConsumesMaterialsAndRewardsOnlyOnce() {
        GameSession game=new GameSession();
        game.state().inventory().add(Item.WOOD,10);
        game.missions().claim(Mission.INN_WOOD); assertEquals(20,game.state().player().money());
        game.missions().accept(Mission.INN_WOOD); game.missions().claim(Mission.INN_WOOD);
        assertEquals(0,game.state().inventory().count(Item.WOOD)); assertEquals(45,game.state().player().money());
        assertEquals(30,game.state().player().xp()); assertEquals(1,game.state().inventory().count(Item.HAT));
        assertTrue(game.state().mailbox().messages().getFirst().mission());
        game.missions().claim(Mission.INN_WOOD); assertEquals(45,game.state().player().money());
        assertEquals(1,game.state().mailbox().size());
    }
    @Test void onlyCompletedJobsAfterAcceptanceAtCorrectLocationCount() {
        GameSession game=new GameSession(); Job farm=game.jobsAt(Location.WILLOW_FARM).getFirst();
        game.work(farm,WorkDuration.QUICK); game.travel(Location.RED_CREEK); game.sleep();
        game.missions().accept(Mission.FARM_HELP); assertEquals(0,game.missions().progress(Mission.FARM_HELP));
        game.work(game.jobsAt(Location.PINE_FOREST).getFirst(),WorkDuration.QUICK);
        assertEquals(0,game.missions().progress(Mission.FARM_HELP)); game.travel(Location.RED_CREEK); game.sleep();
        for(int i=0;i<3;i++) {game.work(farm,WorkDuration.QUICK); game.travel(Location.RED_CREEK); game.sleep();}
        assertEquals(3,game.missions().progress(Mission.FARM_HELP)); game.missions().claim(Mission.FARM_HELP);
        assertTrue(game.state().missions().entry(Mission.FARM_HELP).claimed());
    }
    @Test void runningTimerDoesNotAdvanceMissionAndBlocksClaim() {
        GameSession game=new GameSession(); game.missions().accept(Mission.FARM_HELP);
        game.startWork(game.jobsAt(Location.WILLOW_FARM).getFirst(),WorkDuration.QUICK);
        assertEquals(0,game.missions().progress(Mission.FARM_HELP)); assertFalse(game.missions().blockReason().isEmpty());
    }
    @Test void equipmentRequiresOwnershipAndUsesExclusiveBodySlot() {
        GameSession game=new GameSession(); game.toggleEquipment(Item.BOOTS); assertFalse(game.state().equipment().wearing(Item.BOOTS));
        for(Item item:new Item[]{Item.BOOTS,Item.WORK_SHIRT,Item.COAT}) game.state().inventory().add(item);
        int raw=game.travelCost(Location.OLD_MINE); game.toggleEquipment(Item.BOOTS);
        assertTrue(game.travelCost(Location.OLD_MINE)<raw);
        Job farm=game.jobsAt(Location.WILLOW_FARM).getFirst(); int cost=game.workCost(farm,WorkDuration.LONG);
        game.toggleEquipment(Item.WORK_SHIRT); assertTrue(game.workCost(farm,WorkDuration.LONG)<cost);
        game.toggleEquipment(Item.COAT); assertFalse(game.state().equipment().wearing(Item.WORK_SHIRT));
        assertEquals(cost,game.workCost(farm,WorkDuration.LONG)); assertEquals(1,game.state().inventory().count(Item.WORK_SHIRT));
        game.startWork(farm,WorkDuration.QUICK); game.toggleEquipment(Item.BOOTS); assertTrue(game.state().equipment().wearing(Item.BOOTS));
    }
    @Test void missionsGearSaloonAndRewardTelegramsSurviveSaveAndReset() throws IOException {
        GameSession game=new GameSession(); game.missions().accept(Mission.INN_WOOD); game.state().inventory().add(Item.WOOD,10);
        game.missions().claim(Mission.INN_WOOD); game.toggleEquipment(Item.HAT); game.missions().accept(Mission.FARM_HELP);
        game.work(game.jobsAt(Location.WILLOW_FARM).getFirst(),WorkDuration.QUICK);
        SaveManager saves=new SaveManager(directory.resolve("save.json")); saves.save(game.state()); GameState loaded=saves.load();
        assertEquals(game.state().missions().entries(),loaded.missions().entries());
        assertEquals(game.state().equipment().items(),loaded.equipment().items());
        assertEquals(game.state().mailbox().messages(),loaded.mailbox().messages()); assertEquals(1,loaded.town().level(Building.SALOON));
        game.load(loaded); game.newGame(); assertTrue(game.state().missions().entries().isEmpty()); assertTrue(game.state().equipment().items().isEmpty());
    }
    @Test void versionFiveSaveAddsSaloonAndEmptyJournal() throws IOException {
        Path file=directory.resolve("save.json"); java.nio.file.Files.writeString(file,"""
            {"version":5,"player":"Reiziger","money":20,"bankMoney":0,"level":1,"xp":0,"stamina":100,
             "location":"RED_CREEK","dateTime":"1880-04-01T08:00","inventory":{},"telegrams":[],
             "buildingLevels":{"GUNSMITH":1,"TOWN_HALL":1,"TAILOR":1,"INN":1,"BANK":1}}
            """);
        GameState state=new SaveManager(file).load(); assertEquals(1,state.town().level(Building.SALOON));
        assertTrue(state.missions().entries().isEmpty()); assertTrue(state.equipment().items().isEmpty());
    }
}

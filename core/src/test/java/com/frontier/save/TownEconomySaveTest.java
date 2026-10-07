package com.frontier.save;

import com.frontier.logic.*;
import com.frontier.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import java.nio.file.*;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;

class TownEconomySaveTest {
    @TempDir Path directory;
    @Test void balancesBuildingsPurchasesAndDetailedTelegramSurviveSaveLoad() throws IOException {
        GameSession game = new GameSession(); game.state().player().reward(5000, 49);
        for (Item material : new Item[]{Item.WOOD, Item.STONE, Item.ORE, Item.COTTON}) game.state().inventory().add(material, 100);
        game.town().upgrade(Building.TOWN_HALL);
        for (Building building : Building.values()) if (building != Building.TOWN_HALL) game.town().upgrade(building);
        game.town().deposit(1000); game.town().buy(ShopOffer.at(Building.GUNSMITH).getFirst());
        game.work(game.jobsAt(Location.PINE_FOREST).getFirst(), WorkDuration.QUICK);
        SaveManager saves = new SaveManager(directory.resolve("save.json")); saves.save(game.state()); GameState loaded = saves.load();
        assertEquals(game.state().player().money(), loaded.player().money()); assertEquals(1000, loaded.player().bankMoney());
        assertEquals(game.state().town().levels(), loaded.town().levels()); assertEquals(game.state().inventory().contents(), loaded.inventory().contents());
        assertEquals(game.state().mailbox().messages(), loaded.mailbox().messages()); assertEquals(5, loaded.mailbox().messages().getFirst().levelBonus());
        assertEquals(1, loaded.inventory().count(Item.SLINGSHOT));
    }
    @ParameterizedTest @ValueSource(ints = {1, 2, 3, 4}) void oldSaveVersionsStartWithLevelOneBuildingsAndEmptyAccount(int version) throws IOException {
        Path file = directory.resolve("save.json");
        Files.writeString(file, """
            {"version":%d,"player":"Reiziger","money":20,"level":1,"xp":0,"stamina":100,
            "location":"RED_CREEK","dateTime":"1880-04-01T08:00","inventory":{"WOOD":2},"activeWork":null,"telegrams":[]}
            """.formatted(version));
        GameState state = new SaveManager(file).load(); assertEquals(0, state.player().bankMoney());
        for (Building building : Building.values()) assertEquals(1, state.town().level(building));
        assertEquals(version == 1 ? 0 : 2, state.inventory().count(Item.WOOD));
    }
    @Test void oldTelegramKeepsItsContentsAndReceivesEmptyNewFields() throws IOException {
        Path file = directory.resolve("save.json");
        Files.writeString(file, """
            {"version":4,"player":"Reiziger","money":20,"level":1,"xp":0,"stamina":100,
            "location":"RED_CREEK","dateTime":"1880-04-01T08:00","inventory":{},"telegrams":[
            {"jobName":"Hout hakken","location":"PINE_FOREST","durationSeconds":3600,"money":15,"xp":20,
            "found":"WOOD","dateTime":"1880-04-01T08:00","read":true}]}
            """);
        Telegram message = new SaveManager(file).load().mailbox().messages().getFirst();
        assertEquals(15, message.money()); assertEquals(Item.WOOD, message.found()); assertTrue(message.read());
        assertNull(message.material()); assertEquals(0, message.levelBonus());
    }
    @ParameterizedTest @ValueSource(strings = {"null", "[]", "{}",
        "{\"GUNSMITH\":2,\"TOWN_HALL\":1,\"TAILOR\":1,\"INN\":1,\"BANK\":1}",
        "{\"GUNSMITH\":1,\"TOWN_HALL\":6,\"TAILOR\":1,\"INN\":1,\"BANK\":1}",
        "{\"GUNSMITH\":1,\"TOWN_HALL\":1,\"TAILOR\":1,\"INN\":1,\"BANK\":1,\"BANK\":1}"})
    void malformedBuildingLevelsNeverReplaceTheCurrentGame(String buildings) throws IOException {
        Path file = directory.resolve("save.json");
        Files.writeString(file, """
            {"version":5,"player":"Reiziger","money":20,"bankMoney":0,"level":1,"xp":0,"stamina":100,
            "location":"RED_CREEK","dateTime":"1880-04-01T08:00","inventory":{},"telegrams":[],"buildingLevels":%s}
            """.formatted(buildings));
        GameSession game = new GameSession(); GameState original = game.state();
        assertThrows(IOException.class, () -> game.load(new SaveManager(file).load())); assertSame(original, game.state());
    }
    @ParameterizedTest @ValueSource(strings = {"-1", "501", "1.5", "\"10\"", "2147483648"}) void badBankBalancesAreRejected(String balance) throws IOException {
        Path file = directory.resolve("save.json"); SaveManager saves = new SaveManager(file); saves.save(new GameState());
        Files.writeString(file, Files.readString(file).replace("\"bankMoney\": 0", "\"bankMoney\": " + balance));
        assertThrows(IOException.class, saves::load);
    }
}

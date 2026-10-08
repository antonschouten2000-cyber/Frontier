package com.frontier.save;

import com.frontier.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import java.nio.file.*;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;

class InventorySaveTest {
    @TempDir Path directory;
    @Test void allCategoriesAndQuantitiesSurviveSavingTwice() throws IOException {
        SaveManager saves = new SaveManager(directory.resolve("save.json")); GameState state = new GameState();
        for (Item item : Item.values()) state.inventory().add(item, item.ordinal() + 1);
        saves.save(state); saves.save(state);
        assertEquals(state.inventory().contents(), saves.load().inventory().contents());
    }
    @Test void versionOneSaveMigratesToEmptyInventoryAndDutchDefaultName() throws IOException {
        Path file = directory.resolve("save.json");
        Files.writeString(file, """
            {"version":1,"player":"Traveler","money":20,"level":1,"xp":0,"stamina":100,
             "location":"RED_CREEK","dateTime":"1880-04-01T08:00"}
            """);
        SaveManager saves = new SaveManager(file); GameState loaded = saves.load();
        assertEquals(0, loaded.inventory().totalCount()); assertEquals("Reiziger", loaded.player().name());
        saves.save(loaded); assertTrue(Files.readString(file).contains("\"version\": 7"));
    }
    @Test void versionTwoSaveRetainsInventoryWithoutCreatingATimer() throws IOException {
        Path file = directory.resolve("save.json");
        Files.writeString(file, """
            {"version":2,"player":"Reiziger","money":20,"level":1,"xp":0,"stamina":100,
             "location":"RED_CREEK","dateTime":"1880-04-01T08:00","inventory":{"WOOD":2}}
            """);
        SaveManager saves = new SaveManager(file); GameState loaded = saves.load();
        assertEquals(2, loaded.inventory().count(Item.WOOD)); assertNull(loaded.activeWork());
        saves.save(loaded); assertEquals(2, saves.load().inventory().count(Item.WOOD));
    }
    @ParameterizedTest @ValueSource(strings = {"null", "[]", "{\"COFFEE\":0}", "{\"COAT\":-1}",
        "{\"UNKNOWN\":1}", "{\"HAT\":1.5}", "{\"ORE\":\"2\"}", "{\"KNIFE\":2147483648}",
        "{\"COFFEE\":1,\"COFFEE\":2}"})
    void badInventoriesAreRejected(String inventory) throws IOException {
        Path file = directory.resolve("save.json");
        Files.writeString(file, """
            {"version":2,"player":"Reiziger","money":20,"level":1,"xp":0,"stamina":100,
             "location":"RED_CREEK","dateTime":"1880-04-01T08:00","inventory":%s}
            """.formatted(inventory));
        assertThrows(IOException.class, () -> new SaveManager(file).load());
    }
}

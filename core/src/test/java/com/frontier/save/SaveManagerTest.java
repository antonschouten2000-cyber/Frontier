package com.frontier.save;

import com.frontier.logic.GameSession;
import com.frontier.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import java.nio.file.*;
import java.io.IOException;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

class SaveManagerTest {
    @TempDir Path directory;
    private SaveManager manager() { return new SaveManager(directory.resolve("nested/save.json")); }
    @ParameterizedTest @EnumSource(Location.class) void roundTripRetainsEveryField(Location location) throws IOException {
        GameSession game = new GameSession(new Random(1));
        game.travel(Location.PINE_FOREST); game.work(); game.work();
        game.travel(Location.RED_CREEK); game.sleep(); game.travel(location);
        if (location != Location.RED_CREEK) game.work();
        SaveManager saves = manager(); saves.save(game.state());
        GameState loaded = saves.load(), original = game.state();
        assertEquals(original.player().name(), loaded.player().name());
        assertEquals(original.player().money(), loaded.player().money());
        assertEquals(original.player().stamina(), loaded.player().stamina());
        assertEquals(original.player().level(), loaded.player().level());
        assertEquals(original.player().xp(), loaded.player().xp());
        assertEquals(original.location(), loaded.location());
        assertEquals(original.time().value(), loaded.time().value());
        assertTrue(Files.readString(saves.file()).contains("\"version\": 3"));
        try (var files = Files.list(saves.file().getParent())) { assertEquals(1, files.count()); }
    }
    @Test void savingTwiceReplacesSnapshotAndNewGameDoesNotEraseIt() throws IOException {
        GameSession game = new GameSession(); SaveManager saves = manager();
        saves.save(game.state()); game.travel(Location.PINE_FOREST); game.work(); saves.save(game.state());
        int savedMoney = game.state().player().money(); game.newGame();
        assertEquals(savedMoney, saves.load().player().money());
    }
    @Test void missingSaveHasUsefulError() {
        IOException e = assertThrows(IOException.class, () -> manager().load());
        assertTrue(e.getMessage().contains("Nog geen opgeslagen spel"));
    }
    @ParameterizedTest @ValueSource(strings = {"broken JSON", "{}", "[]",
        "{\"version\":2}", "{\"stamina\":-20}"})
    void malformedOrIncompleteSaveIsRejected(String json) throws IOException {
        SaveManager saves = manager(); Files.createDirectories(saves.file().getParent());
        Files.writeString(saves.file(), json); assertThrows(IOException.class, saves::load);
    }
    @ParameterizedTest @ValueSource(strings = {"version", "stamina", "level", "location", "dateTime", "money"})
    void corruptFieldsDoNotReplaceCurrentSession(String field) throws IOException {
        GameSession game = new GameSession(); game.travel(Location.PINE_FOREST); game.work(); GameState live = game.state();
        SaveManager saves = manager(); saves.save(live);
        String json = Files.readString(saves.file());
        String replacement = switch (field) {
            case "version" -> "99";
            case "stamina" -> "-1";
            case "level" -> "99";
            case "location" -> "\"UNKNOWN\"";
            case "dateTime" -> "\"not-a-date\"";
            default -> "\"20\"";
        };
        json = json.replaceAll("\"" + field + "\": [^,\n]+", "\"" + field + "\": " + replacement);
        Files.writeString(saves.file(), json);
        assertThrows(IOException.class, () -> game.load(saves.load())); assertSame(live, game.state());
    }
}

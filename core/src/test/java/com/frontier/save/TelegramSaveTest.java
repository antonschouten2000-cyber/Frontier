package com.frontier.save;

import com.frontier.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import java.nio.file.*;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;

class TelegramSaveTest {
    @TempDir Path directory;
    @Test void readAndUnreadReportsWithAndWithoutFindsSurviveSaving() throws IOException {
        GameState state = new GameState();
        state.mailbox().add(new Telegram("Hout hakken", Location.PINE_FOREST, 15, 1, 1, Item.WOOD, GameTime.START, false));
        state.mailbox().add(new Telegram("Erts delven", Location.OLD_MINE, 3600, 20, 25, null, GameTime.START, false));
        state.mailbox().read(0); SaveManager saves = new SaveManager(directory.resolve("save.json")); saves.save(state);
        GameState loaded = saves.load(); assertEquals(state.mailbox().messages(), loaded.mailbox().messages());
        assertEquals(1, loaded.mailbox().unreadCount());
    }
    @Test void versionThreeSaveMigratesToEmptyMailbox() throws IOException {
        Path file = directory.resolve("save.json");
        Files.writeString(file, """
            {"version":3,"player":"Reiziger","money":20,"level":1,"xp":0,"stamina":100,
             "location":"RED_CREEK","dateTime":"1880-04-01T08:00","inventory":{},"activeWork":null}
            """);
        assertEquals(0, new SaveManager(file).load().mailbox().size());
    }
    @ParameterizedTest @ValueSource(strings = {"null", "{}", "[null]", "[{}]",
        "[{\"jobName\":\"Klus\",\"location\":\"RED_CREEK\",\"dateTime\":\"1880-04-01T08:00\",\"durationSeconds\":15,\"money\":1,\"xp\":1,\"found\":null,\"read\":\"false\"}]"})
    void invalidMailboxesAreRejected(String messages) throws IOException {
        Path file = directory.resolve("save.json");
        Files.writeString(file, """
            {"version":4,"player":"Reiziger","money":20,"level":1,"xp":0,"stamina":100,
             "location":"RED_CREEK","dateTime":"1880-04-01T08:00","inventory":{},"telegrams":%s}
            """.formatted(messages));
        assertThrows(IOException.class, () -> new SaveManager(file).load());
    }
    @Test void fullMailboxRemainsLoadableWithinFileSizeLimit() throws IOException {
        GameState state = new GameState();
        for (int i = 0; i < Mailbox.MAX_MESSAGES; i++)
            state.mailbox().add(new Telegram("Mijnwerktuigen poetsen", Location.OLD_MINE, 3600, 10, 11, Item.RIFLE, GameTime.START, false));
        SaveManager saves = new SaveManager(directory.resolve("save.json")); saves.save(state);
        assertEquals(Mailbox.MAX_MESSAGES, saves.load().mailbox().size());
    }
}

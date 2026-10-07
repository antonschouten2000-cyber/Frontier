package com.frontier.logic;

import com.frontier.model.*;
import com.frontier.save.SaveManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.*;
import java.time.*;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

class WorkTimerTest {
    @TempDir Path directory;
    private static final Instant NOW = Instant.parse("2026-10-07T12:00:00Z");
    private GameSession session(Instant now) { return new GameSession(new Random(1), Clock.fixed(now, ZoneOffset.UTC)); }
    @ParameterizedTest @EnumSource(WorkDuration.class) void rewardsOnlyArriveAtEndAndOnlyOnce(WorkDuration duration) {
        GameSession game = session(NOW); Job job = game.jobsAt(Location.PINE_FOREST).getFirst();
        game.startWork(job, duration);
        assertTrue(game.isWorking()); assertEquals(20, game.state().player().money()); assertEquals(0, game.state().player().xp());
        assertEquals(GameTime.START.plusMinutes(112), game.state().time().value());
        assertEquals(100 - 14 - job.staminaCost(duration), game.state().player().stamina());
        GameSession beforeEnd = session(NOW.plusSeconds(duration.seconds()).minusMillis(1)); beforeEnd.load(game.state());
        assertEquals("", beforeEnd.updateWork()); assertTrue(beforeEnd.isWorking()); assertEquals(1, beforeEnd.workSecondsRemaining());
        GameSession end = session(NOW.plusSeconds(duration.seconds())); end.load(game.state());
        assertFalse(end.updateWork().isEmpty()); assertFalse(end.isWorking());
        assertEquals(GameTime.START.plusMinutes(112).plusSeconds(duration.seconds()), end.state().time().value());
        assertEquals(job.xp(duration), end.state().player().xp());
        int money = end.state().player().money(), xp = end.state().player().xp();
        assertEquals("", end.updateWork()); assertEquals(money, end.state().player().money()); assertEquals(xp, end.state().player().xp());
    }
    @Test void progressIsRealTimeAndWaitingDoesNotAdvanceGameClock() {
        GameSession game = session(NOW); game.startWork(game.jobsAt(Location.PINE_FOREST).getFirst(), WorkDuration.QUICK);
        var time = game.state().time().value(); GameSession halfway = session(NOW.plusMillis(7500)); halfway.load(game.state());
        assertEquals(.5f, halfway.workProgress(), .001); assertEquals(8, halfway.workSecondsRemaining());
        halfway.updateWork(); assertEquals(time, halfway.state().time().value());
    }
    @Test void busySessionRejectsTravelSleepAndSecondJob() {
        GameSession game = session(NOW); Job job = game.jobsAt(Location.PINE_FOREST).getFirst();
        game.startWork(job, WorkDuration.SHORT); ActiveWork active = game.state().activeWork();
        int stamina = game.state().player().stamina(); var time = game.state().time().value();
        assertFalse(game.travelBlockReason(Location.RED_CREEK).isEmpty()); game.travel(Location.RED_CREEK); game.sleep();
        assertFalse(game.workBlockReason(job, WorkDuration.QUICK).isEmpty()); game.startWork(job, WorkDuration.QUICK); game.work(job);
        assertSame(active, game.state().activeWork()); assertEquals(stamina, game.state().player().stamina());
        assertEquals(time, game.state().time().value()); assertEquals(20, game.state().player().money());
    }
    @Test void shortWorkRemainsAvailableWhenLongWorkIsTooTiring() {
        GameSession game = session(NOW); game.travel(Location.PINE_FOREST);
        game.state().player().spendStamina(71); // 15 energie: 14 terugreis + 1 korte klus.
        Job job = game.currentJob(); assertFalse(game.workBlockReason(job, WorkDuration.LONG).isEmpty());
        assertEquals("", game.workBlockReason(job, WorkDuration.QUICK));
        game.startWork(job, WorkDuration.QUICK); assertEquals(14, game.state().player().stamina());
    }
    @Test void insufficientEnergyDoesNotCreateTimerOrMutateState() {
        GameSession game = session(NOW); Job job = game.jobsAt(Location.QUARRY).getLast();
        game.load(new GameState(new Player("Reiziger", 20, 85, 0, 1), new GameTime(), Location.RED_CREEK));
        assertFalse(game.workBlockReason(job, WorkDuration.QUICK).isEmpty()); game.startWork(job, WorkDuration.QUICK);
        assertFalse(game.isWorking()); assertEquals(85, game.state().player().stamina()); assertEquals(GameTime.START, game.state().time().value());
    }
    @Test void longerWorkHasLargerCostsRewardsAndLootChance() {
        Job job = new JobManager(new Random(1)).at(Location.PINE_FOREST).getFirst();
        assertEquals(15, WorkDuration.QUICK.seconds()); assertEquals(600, WorkDuration.SHORT.seconds()); assertEquals(3600, WorkDuration.LONG.seconds());
        assertTrue(job.staminaCost(WorkDuration.QUICK) < job.staminaCost(WorkDuration.SHORT));
        assertTrue(job.staminaCost(WorkDuration.SHORT) < job.staminaCost(WorkDuration.LONG));
        assertTrue(job.scaledPay(15, WorkDuration.SHORT) < job.scaledPay(15, WorkDuration.LONG));
        assertEquals(.125, LootManager.chancePercent(WorkDuration.QUICK), .001);
        assertEquals(5, LootManager.chancePercent(WorkDuration.SHORT), .001);
        assertEquals(30, LootManager.chancePercent(WorkDuration.LONG), .001);
    }
    @Test void pendingTimerSurvivesSaveLoadAndFinishesAfterOfflineTime() throws Exception {
        GameSession game = session(NOW); game.startWork(game.jobsAt(Location.WILLOW_FARM).getFirst(), WorkDuration.QUICK);
        SaveManager saves = new SaveManager(directory.resolve("save.json")); saves.save(game.state());
        GameSession resumed = session(NOW.plusSeconds(8)); resumed.load(saves.load());
        assertEquals(7, resumed.workSecondsRemaining()); assertEquals("", resumed.updateWork());
        GameSession later = session(NOW.plusSeconds(20)); later.load(saves.load());
        assertFalse(later.updateWork().isEmpty()); assertFalse(later.isWorking());
        saves.save(later.state()); later.load(saves.load()); assertEquals("", later.updateWork());
        assertEquals(15, later.state().time().value().getSecond());
    }
    @Test void badSavedTimerIsRejected() throws Exception {
        GameSession game = session(NOW); game.startWork(game.jobsAt(Location.WILLOW_FARM).getFirst(), WorkDuration.QUICK);
        SaveManager saves = new SaveManager(directory.resolve("save.json")); saves.save(game.state());
        String json = Files.readString(saves.file()); Files.writeString(saves.file(), json.replace("\"QUICK\"", "\"INVALID\""));
        assertThrows(java.io.IOException.class, saves::load);
        Files.writeString(saves.file(), json.replace("\"milk\"", "\"wood\"")); assertThrows(java.io.IOException.class, saves::load);
    }
}

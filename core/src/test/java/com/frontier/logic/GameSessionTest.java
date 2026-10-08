package com.frontier.logic;

import com.frontier.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import java.time.LocalDateTime;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

class GameSessionTest {
    private GameSession session() { return new GameSession(new Random(1880)); }
    @Test void startsWithRequestedValues() {
        GameState s = session().state();
        assertAll(() -> assertEquals("Reiziger", s.player().name()),
            () -> assertEquals(20, s.player().money()), () -> assertEquals(100, s.player().stamina()),
            () -> assertEquals(1, s.player().level()), () -> assertEquals(0, s.player().xp()),
            () -> assertEquals(Location.RED_CREEK, s.location()),
            () -> assertEquals(LocalDateTime.of(1880, 4, 1, 8, 0), s.time().value()));
    }
    @ParameterizedTest @EnumSource(value = Location.class, names = "RED_CREEK", mode = EnumSource.Mode.EXCLUDE) void everyJobPaysAndAdvancesOnlyActionTime(Location location) {
        GameSession game = session();
        if(location==Location.FORGOTTEN_STOP){
            game.state().journal().select(VillageProject.WELL);game.state().journal().finish();
            game.state().journal().select(VillageProject.BRIDGE);game.state().journal().finish();
        }
        game.travel(location);
        Player p = game.state().player();
        Job job = game.currentJob();
        int money = p.money(), stamina = p.stamina(), xp = p.xp();
        LocalDateTime time = game.state().time().value();
        game.work();
        assertTrue(p.money() >= money + job.basePay() - job.payVariation());
        assertTrue(p.money() <= money + job.basePay() + job.payVariation());
        assertEquals(stamina - job.staminaCost(), p.stamina());
        assertEquals(xp + job.xp(), p.xp());
        assertEquals(time.plusMinutes(job.minutes()), game.state().time().value());
    }
    @Test void travelCostsAreSymmetricAndAdvanceTime() {
        GameSession game = session();
        int cost = Location.RED_CREEK.travelStaminaTo(Location.OLD_MINE);
        assertEquals(cost, Location.OLD_MINE.travelStaminaTo(Location.RED_CREEK));
        game.travel(Location.OLD_MINE);
        assertEquals(100 - cost, game.state().player().stamina());
        assertEquals(GameTime.START.plusMinutes(cost * 8L), game.state().time().value());
        assertEquals(Location.OLD_MINE, game.state().location());
    }
    @Test void clickingCurrentLocationCostsNothing() {
        GameSession game = session(); game.travel(Location.RED_CREEK);
        assertEquals(100, game.state().player().stamina());
        assertEquals(GameTime.START, game.state().time().value());
    }
    @Test void rejectedTravelChangesNothing() {
        GameSession game = session();
        game.load(new GameState(new Player("Reiziger", 20, 5, 0, 1), new GameTime(), Location.RED_CREEK));
        assertFalse(game.travelBlockReason(Location.OLD_MINE).isEmpty());
        game.travel(Location.OLD_MINE);
        assertEquals(5, game.state().player().stamina());
        assertEquals(Location.RED_CREEK, game.state().location());
        assertEquals(GameTime.START, game.state().time().value());
    }
    @ParameterizedTest @EnumSource(value = Location.class, names = "RED_CREEK", mode = EnumSource.Mode.EXCLUDE) void exhaustingWorkNeverStrandsPlayer(Location location) {
        GameSession game = session();
        if(location==Location.FORGOTTEN_STOP){
            game.state().journal().select(VillageProject.WELL);game.state().journal().finish();
            game.state().journal().select(VillageProject.BRIDGE);game.state().journal().finish();
        }
        game.travel(location);
        int actions = 0;
        while (game.workBlockReason().isEmpty()) {
            game.work(); assertTrue(++actions <= 5);
        }
        assertTrue(actions > 0);
        int money = game.state().player().money(), xp = game.state().player().xp();
        LocalDateTime time = game.state().time().value();
        game.work(); // Rejected work must not grant free rewards or consume time.
        assertEquals(money, game.state().player().money());
        assertEquals(xp, game.state().player().xp());
        assertEquals(time, game.state().time().value());
        if (location != Location.RED_CREEK) {
            assertEquals("", game.travelBlockReason(Location.RED_CREEK));
            game.travel(Location.RED_CREEK);
        }
        LocalDateTime beforeSleep = game.state().time().value();
        game.sleep();
        assertEquals(100, game.state().player().stamina());
        assertEquals(beforeSleep.plusHours(8), game.state().time().value());
        assertEquals("", game.travelBlockReason(Location.OLD_MINE));
    }
    @Test void sleepingOutsideRedCreekDoesNothing() {
        GameSession game = session(); game.travel(Location.PINE_FOREST);
        int stamina = game.state().player().stamina(); LocalDateTime time = game.state().time().value();
        game.sleep();
        assertEquals(stamina, game.state().player().stamina());
        assertEquals(time, game.state().time().value());
    }
    @Test void sleepCrossesMonthBoundary() {
        GameSession game = session();
        game.load(new GameState(new Player(), new GameTime(LocalDateTime.of(1880, 4, 30, 22, 0)), Location.RED_CREEK));
        game.sleep(); assertEquals(LocalDateTime.of(1880, 5, 1, 6, 0), game.state().time().value());
    }
    @Test void xpLevelsUpAtThresholdAndCanSkipMultipleLevels() {
        Player p = new Player(); p.reward(0, 49); assertEquals(1, p.level());
        p.reward(0, 1); assertEquals(2, p.level()); assertEquals(150, p.nextLevelXp());
        p.reward(0, 450); assertEquals(5, p.level()); assertEquals(750, p.nextLevelXp());
    }
    @Test void paymentVariationIsSmallAndActuallyVaries() {
        JobManager jobs = new JobManager(new Random(1)); Job job = jobs.at(Location.OLD_MINE).getFirst();
        java.util.Set<Integer> payments = new java.util.HashSet<>();
        for (int i = 0; i < 100; i++) {
            int pay = jobs.payment(job); assertTrue(pay >= 17 && pay <= 23); payments.add(pay);
        }
        assertTrue(payments.size() > 1);
    }
    @Test void newGameResetsAllState() {
        GameSession game = session(); game.travel(Location.PINE_FOREST); game.work(); game.newGame();
        assertEquals(20, game.state().player().money()); assertEquals(100, game.state().player().stamina());
        assertEquals(0, game.state().player().xp()); assertEquals(1, game.state().player().level());
        assertEquals(Location.RED_CREEK, game.state().location()); assertEquals(GameTime.START, game.state().time().value());
    }
    @Test void invalidModelsAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Player("X", 20, -1, 0, 1));
        assertThrows(IllegalArgumentException.class, () -> new Player("X", 20, 100, 50, 1));
        assertThrows(IllegalArgumentException.class, () -> new GameTime().advanceMinutes(-1));
        assertThrows(IllegalArgumentException.class, () -> new GameState(new Player("X", 20, 0, 0, 1), new GameTime(), Location.OLD_MINE));
    }
}

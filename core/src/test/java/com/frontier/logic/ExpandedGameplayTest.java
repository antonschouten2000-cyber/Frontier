package com.frontier.logic;

import com.frontier.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import java.util.*;
import java.util.random.RandomGenerator;
import static org.junit.jupiter.api.Assertions.*;

class ExpandedGameplayTest {
    @Test void fortyTwoDistinctJobsAreOutsideTown() {
        GameSession game = new GameSession(); Set<String> ids = new HashSet<>();
        for (Location location : Location.values()) {
            assertEquals(location == Location.RED_CREEK ? 0 : location.ordinal() <= Location.LONELY_RANCH.ordinal() ? 6 : 3, game.jobsAt(location).size());
            for (Job job : game.jobsAt(location)) {
                assertEquals(location, job.location()); assertTrue(ids.add(job.id()));
            }
        }
        assertEquals(42, ids.size()); assertNull(game.currentJob());
        assertFalse(game.workBlockReason().isEmpty()); game.work();
        assertEquals(20, game.state().player().money()); assertEquals(100, game.state().player().stamina());
        assertEquals(GameTime.START, game.state().time().value());
    }
    @ParameterizedTest @EnumSource(Location.class) void everyJobCanTravelWorkAndReturn(Location target) {
        GameSession game = new GameSession(new Random(1));
        for (Job job : game.jobsAt(target)) {
            game.newGame();
            int travelCost = Location.RED_CREEK.travelStaminaTo(target);
            int travelMinutes = Location.RED_CREEK.travelMinutesTo(target);
            game.work(job);
            assertEquals(target, game.state().location());
            assertEquals(100 - travelCost - job.staminaCost(), game.state().player().stamina());
            assertEquals(GameTime.START.plusMinutes(travelMinutes + job.minutes()), game.state().time().value());
            assertEquals(job.xp(), game.state().player().xp());
            if (target != Location.RED_CREEK) assertEquals("", game.travelBlockReason(Location.RED_CREEK));
        }
    }
    @Test void remoteWorkRequiresEnergyForTravelJobAndReturn() {
        GameSession game = new GameSession(new Random(1));
        Job job = game.jobsAt(Location.OLD_MINE).getFirst();
        int required = 2 * Location.RED_CREEK.travelStaminaTo(Location.OLD_MINE) + job.staminaCost();
        GameState state = new GameState(new Player("Reiziger", 20, required - 1, 0, 1), new GameTime(), Location.RED_CREEK);
        game.load(state); assertFalse(game.workBlockReason(job).isEmpty()); game.work(job);
        assertSame(state, game.state()); assertEquals(Location.RED_CREEK, state.location());
        assertEquals(20, state.player().money()); assertEquals(0, state.player().xp());
        assertEquals(GameTime.START, state.time().value()); assertEquals(0, state.inventory().totalCount());
        game.load(new GameState(new Player("Reiziger", 20, required, 0, 1), new GameTime(), Location.RED_CREEK));
        game.work(job); assertEquals(Location.OLD_MINE, game.state().location());
        assertEquals("", game.travelBlockReason(Location.RED_CREEK));
    }
    @Test void unregisteredJobCannotGrantRewards() {
        GameSession game = new GameSession();
        Job fake = new Job("fake", Location.RED_CREEK, "Gratis geld", 1000, 0, 1, 1, 999);
        game.work(fake); assertEquals(20, game.state().player().money()); assertEquals(100, game.state().player().stamina());
    }
    private static final class Rolls implements RandomGenerator {
        private final int[] rolls;
        private int index;
        Rolls(int... rolls) { this.rolls = rolls; }
        @Override public long nextLong() { throw new AssertionError("Geen onvoorspelbare worp verwacht"); }
        @Override public int nextInt(int bound) {
            int value = rolls[index++]; assertTrue(value >= 0 && value < bound); return value;
        }
        @Override public int nextInt(int origin, int bound) { assertTrue(origin <= 0 && bound > 0); return 0; }
    }
    @Test void findChanceBoundaryAndProductDependsOnLocation() {
        assertTrue(new LootManager(new Rolls(30)).find(Location.PINE_FOREST).isEmpty());
        assertEquals(Item.WOOD, new LootManager(new Rolls(29, 59)).find(Location.PINE_FOREST).orElseThrow());
        assertEquals(Item.ORE, new LootManager(new Rolls(0, 0)).find(Location.OLD_MINE).orElseThrow());
        assertEquals(Item.COFFEE, new LootManager(new Rolls(0, 0)).find(Location.RED_CREEK).orElseThrow());
    }
    @Test void clothingAndWeaponsCanBothBeFound() {
        assertEquals(Item.COAT, new LootManager(new Rolls(0, 60, 2)).find(Location.RED_CREEK).orElseThrow());
        assertEquals(Item.REVOLVER, new LootManager(new Rolls(0, 90, 0)).find(Location.OLD_MINE).orElseThrow());
        assertEquals(Item.RIFLE, new LootManager(new Rolls(0, 99, 1)).find(Location.LONELY_RANCH).orElseThrow());
    }
    @Test void successfulJobsAddAndStackLootAndNewGameClearsIt() {
        GameSession game = new GameSession(new Rolls(0, 0, 0, 0));
        game.travel(Location.PINE_FOREST); game.work(); game.work(); assertEquals(26, game.state().inventory().count(Item.WOOD)); // Twee vondsten plus tweemaal twaalf bouwmateriaal.
        game.newGame(); assertEquals(0, game.state().inventory().totalCount());
    }
    @Test void rejectedJobDoesNotRollLoot() {
        GameSession game = new GameSession(new Rolls());
        game.load(new GameState(new Player("Reiziger", 20, 0, 0, 1), new GameTime(), Location.RED_CREEK));
        game.work(); assertEquals(0, game.state().inventory().totalCount());
    }
    @Test void inventoryValidatesCountsAndProtectsItsContents() {
        Inventory inventory = new Inventory(); inventory.add(Item.BOOTS, 2); inventory.add(Item.BOOTS);
        assertEquals(3, inventory.count(Item.BOOTS));
        assertThrows(IllegalArgumentException.class, () -> inventory.add(Item.HAT, 0));
        assertThrows(UnsupportedOperationException.class, () -> inventory.contents().put(Item.HAT, 99));
    }
    @Test void dateAndDefaultNameAreDutch() {
        assertEquals("1 april 1880  08:00", new GameTime().display());
        assertEquals("Reiziger", new Player().name());
    }
}

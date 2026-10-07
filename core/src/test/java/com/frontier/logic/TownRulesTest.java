package com.frontier.logic;

import com.frontier.model.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TownRulesTest {
    @Test void cityJobCannotBeExecutedEvenWhenCalledDirectly() {
        GameSession game = new GameSession();
        Job oldCityJob = new Job("wagons", Location.RED_CREEK, "Wagens laden", 10, 2, 20, 120, 15);
        assertTrue(game.jobsAt(Location.RED_CREEK).isEmpty());
        assertFalse(game.workBlockReason(oldCityJob).isEmpty()); game.work(oldCityJob);
        assertEquals(20, game.state().player().money()); assertEquals(0, game.state().player().xp());
        assertEquals(100, game.state().player().stamina()); assertEquals(0, game.state().inventory().totalCount());
        assertEquals(GameTime.START, game.state().time().value());
    }
    @Test void workOutsideTownStillAllowsReturnAndInnRecovery() {
        GameSession game = new GameSession();
        Job hardJob = game.jobsAt(Location.OLD_MINE).stream().filter(j -> j.id().equals("supports")).findFirst().orElseThrow();
        game.work(hardJob);
        assertEquals(Location.OLD_MINE, game.state().location());
        game.travel(Location.RED_CREEK);
        int cash = game.state().player().money(), xp = game.state().player().xp();
        var before = game.state().time().value(); game.sleep();
        assertEquals(100, game.state().player().stamina());
        assertEquals(before.plusHours(8), game.state().time().value());
        assertEquals(cash, game.state().player().money()); assertEquals(xp, game.state().player().xp());
    }
}

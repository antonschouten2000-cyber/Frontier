package com.frontier.logic;

import com.frontier.model.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class WorldMapTest {
    @Test void originalRegionKeepsNativeDistancesInsideLargerWorld() {
        assertTrue(WorldMap.WIDTH > WorldMap.REGION_WIDTH);
        assertTrue(WorldMap.HEIGHT > WorldMap.REGION_HEIGHT);
        assertEquals(384, WorldMap.x(Location.OLD_MINE) - WorldMap.x(Location.RED_CREEK), .001);
        assertEquals(.40 * 440, WorldMap.y(Location.PINE_FOREST) - WorldMap.y(Location.RED_CREEK), .001);
        for (Location location : Location.values()) {
            assertTrue(WorldMap.x(location) >= WorldMap.REGION_X);
            assertTrue(WorldMap.x(location) <= WorldMap.REGION_X + WorldMap.REGION_WIDTH);
            assertTrue(WorldMap.y(location) >= WorldMap.HEIGHT - WorldMap.REGION_Y - WorldMap.REGION_HEIGHT);
            assertTrue(WorldMap.y(location) <= WorldMap.HEIGHT - WorldMap.REGION_Y);
        }
    }
    @Test void ghostTownsAndFortAreOutsideOldRegionAndWithinWorld() {
        int towns = 0, forts = 0;
        for (Landmark site : Landmark.values()) {
            if (site.isFort()) forts++; else towns++;
            assertTrue(site.x() >= 105 && site.x() <= WorldMap.WIDTH - 105);
            assertTrue(site.y() >= 28 && site.y() <= WorldMap.HEIGHT - 28);
            boolean inOldRegion = site.x() >= WorldMap.REGION_X && site.x() <= WorldMap.REGION_X + WorldMap.REGION_WIDTH
                && site.y() >= WorldMap.HEIGHT - WorldMap.REGION_Y - WorldMap.REGION_HEIGHT
                && site.y() <= WorldMap.HEIGHT - WorldMap.REGION_Y;
            assertFalse(inOldRegion);
        }
        assertEquals(3, towns); assertEquals(1, forts);
    }
    @Test void expandingArtworkDoesNotIncreaseExistingTripCostOrInvalidateSaves() {
        GameSession game = new GameSession();
        game.travel(Location.OLD_MINE);
        assertEquals(81, game.state().player().stamina());
        assertEquals(GameTime.START.plusMinutes(152), game.state().time().value());
    }
}

package com.frontier.logic;

import com.frontier.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

class ProgressionMaterialsTest {
    @Test void everyLevelCrossedGrantsItsBonusAndRepeatedRewardsCannotGrantItAgain() {
        Player player = new Player(); int bonus = player.reward(10, 500);
        assertEquals(5, player.level()); assertEquals(ProgressionRules.bonusBetween(1, 5), bonus); assertEquals(30 + bonus, player.money());
        assertEquals(0, player.reward(0, 0)); assertEquals(30 + bonus, player.money());
    }
    @Test void bonusGrowsByTenPercentWithASmallCapAndDoesNotDependOnWealth() {
        assertEquals(5, ProgressionRules.bonusForLevel(2)); assertEquals(6, ProgressionRules.bonusForLevel(3));
        assertTrue(ProgressionRules.bonusForLevel(10) > 5); assertEquals(50, ProgressionRules.bonusForLevel(100));
        Player poor = new Player(), rich = new Player("Reiziger", 10000, 100, 0, 1); poor.deposit(20);
        assertEquals(5, poor.reward(0, 50)); assertEquals(5, rich.reward(0, 50)); assertEquals(20, poor.bankMoney());
    }
    @ParameterizedTest @EnumSource(WorkDuration.class) void appropriateWorkGivesGuaranteedMaterialsAndReportsThem(WorkDuration duration) {
        for (Location location : new Location[]{Location.PINE_FOREST, Location.OLD_MINE, Location.QUARRY, Location.COTTON_FARM}) {
            GameSession game = new GameSession(new Random(1)); Job job = game.jobsAt(location).getFirst(); game.work(job, duration);
            Telegram message = game.state().mailbox().messages().getFirst(); Item material = WorkMaterials.item(job);
            assertEquals(material, message.material()); assertEquals(WorkMaterials.quantity(job, duration), message.materialQuantity());
            assertEquals(message.materialQuantity() + (message.found() == material ? 1 : 0), game.state().inventory().count(material));
        }
    }
    @Test void workTelegramIncludesLevelBonusInTotalPayment() {
        GameSession game = new GameSession(new Random(1)); game.state().player().reward(0, 49); int cash = game.state().player().money();
        game.work(game.jobsAt(Location.PINE_FOREST).getFirst(), WorkDuration.QUICK);
        Telegram telegram = game.state().mailbox().messages().getFirst(); assertEquals(5, telegram.levelBonus());
        assertEquals(game.state().player().money() - cash, telegram.money()); assertEquals(6, telegram.money());
    }
    @Test void inventoryRemovalCannotGoNegativeAndRemovesEmptyStacks() {
        Inventory inventory = new Inventory(); inventory.add(Item.STONE, 3); inventory.remove(Item.STONE, 2); assertEquals(1, inventory.count(Item.STONE));
        assertThrows(IllegalArgumentException.class, () -> inventory.remove(Item.STONE, 2)); assertEquals(1, inventory.count(Item.STONE));
        inventory.remove(Item.STONE, 1); assertFalse(inventory.contents().containsKey(Item.STONE));
    }
    @Test void currencyOverflowLeavesPlayerUnchanged() {
        Player player = new Player("Reiziger", Integer.MAX_VALUE, 100, 0, 1);
        assertThrows(ArithmeticException.class, () -> player.reward(0, 50)); assertEquals(0, player.xp()); assertEquals(1, player.level());
    }
}

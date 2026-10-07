package com.frontier.logic;

import com.frontier.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class TownEconomyTest {
    private GameSession funded() {
        GameSession game = new GameSession();
        Inventory inventory = new Inventory();
        for (Item item : new Item[]{Item.WOOD, Item.STONE, Item.ORE, Item.COTTON}) inventory.add(item, 1000);
        game.load(new GameState(new Player("Reiziger", 10000, 100, 0, 1), new GameTime(), Location.RED_CREEK, inventory));
        return game;
    }
    @Test void allBuildingsBeginAtOneAndTownHallUnlocksOtherUpgrades() {
        GameSession game = funded();
        for (Building building : Building.values()) assertEquals(1, game.state().town().level(building));
        assertFalse(game.town().upgradeReason(Building.GUNSMITH).isEmpty());
        game.town().upgrade(Building.GUNSMITH); assertEquals(1, game.state().town().level(Building.GUNSMITH));
        game.town().upgrade(Building.TOWN_HALL); assertEquals("", game.town().upgradeReason(Building.GUNSMITH));
    }
    @ParameterizedTest @EnumSource(Building.class) void upgradesConsumeExactlyTheShownResources(Building building) {
        GameSession game = funded();
        if (building != Building.TOWN_HALL) game.town().upgrade(Building.TOWN_HALL);
        UpgradeCost cost = UpgradeCost.forBuilding(building, 1);
        int cash = game.state().player().money(); var before = new EnumMap<>(game.state().inventory().contents());
        var time = game.state().time().value(); int stamina = game.state().player().stamina();
        assertEquals("", game.town().upgradeReason(building)); game.town().upgrade(building);
        assertEquals(2, game.state().town().level(building)); assertEquals(cash - cost.money(), game.state().player().money());
        for (Item item : Item.values()) assertEquals(before.getOrDefault(item, 0) - cost.materials().getOrDefault(item, 0), game.state().inventory().count(item));
        assertEquals(time, game.state().time().value()); assertEquals(stamina, game.state().player().stamina());
    }
    @Test void costsGrowAndAllBuildingsStopAtFive() {
        GameSession game = funded();
        for (int target = 2; target <= 5; target++) {
            game.town().upgrade(Building.TOWN_HALL);
            for (Building building : Building.values()) if (building != Building.TOWN_HALL) game.town().upgrade(building);
        }
        for (Building building : Building.values()) {
            assertEquals(5, game.state().town().level(building)); int money = game.state().player().money();
            assertFalse(game.town().upgradeReason(building).isEmpty()); game.town().upgrade(building); assertEquals(money, game.state().player().money());
            assertTrue(UpgradeCost.forBuilding(building, 4).money() > UpgradeCost.forBuilding(building, 1).money());
            assertTrue(UpgradeCost.forBuilding(building, 4).materials().get(Item.WOOD) > UpgradeCost.forBuilding(building, 1).materials().get(Item.WOOD));
        }
    }
    @Test void missingCashOrMaterialLeavesUpgradeCompletelyUnchanged() {
        GameSession game = new GameSession(); var before = game.state().inventory().contents();
        game.town().upgrade(Building.TOWN_HALL); assertEquals(20, game.state().player().money()); assertEquals(1, game.state().town().level(Building.TOWN_HALL));
        game.state().player().reward(100, 0); game.state().inventory().add(Item.WOOD, 2);
        game.town().upgrade(Building.TOWN_HALL); assertEquals(120, game.state().player().money()); assertEquals(2, game.state().inventory().count(Item.WOOD));
        assertEquals(1, game.state().town().level(Building.TOWN_HALL));
    }
    @Test void storesStartWithOneItemAndUnlockFiveLevels() {
        GameSession game = funded();
        for (Building building : new Building[]{Building.GUNSMITH, Building.TAILOR}) {
            var offers = ShopOffer.at(building); assertEquals(5, offers.size());
            assertEquals(1, offers.stream().filter(offer -> game.town().buyReason(offer).isEmpty()).count());
            assertTrue(offers.getLast().quality() > offers.getFirst().quality());
        }
        assertEquals(Item.SLINGSHOT, ShopOffer.at(Building.GUNSMITH).getFirst().item());
        ShopOffer locked = ShopOffer.at(Building.GUNSMITH).get(1); int money = game.state().player().money();
        game.town().buy(locked); assertEquals(money, game.state().player().money()); assertEquals(0, game.state().inventory().count(locked.item()));
        game.town().upgrade(Building.TOWN_HALL); game.town().upgrade(Building.GUNSMITH); game.town().buy(locked);
        assertEquals(1, game.state().inventory().count(locked.item()));
    }
    @Test void shopPurchasesCostOnlyCashAndDoNotGrantXpOrAdvanceTime() {
        GameSession game = funded(); ShopOffer offer = ShopOffer.at(Building.GUNSMITH).getFirst(); int money = game.state().player().money();
        game.town().buy(offer); assertEquals(money - offer.price(), game.state().player().money());
        assertEquals(1, game.state().inventory().count(offer.item())); assertEquals(0, game.state().player().xp()); assertEquals(GameTime.START, game.state().time().value());
        ShopOffer fake = new ShopOffer(Building.GUNSMITH, 1, Item.REPEATER, 0, 100); game.town().buy(fake); assertEquals(0, game.state().inventory().count(Item.REPEATER));
    }
    @Test void bankTransfersPreserveTotalMoneyAndRejectBadAmounts() {
        GameSession game = funded(); Player player = game.state().player(); game.town().deposit(100);
        assertEquals(9900, player.money()); assertEquals(100, player.bankMoney()); game.town().withdraw(40);
        assertEquals(9940, player.money()); assertEquals(60, player.bankMoney());
        for (int value : new int[]{0, -1, Integer.MAX_VALUE}) { game.town().deposit(value); game.town().withdraw(value); }
        assertEquals(9940, player.money()); assertEquals(60, player.bankMoney()); assertEquals(GameTime.START, game.state().time().value());
    }
    @Test void bankLevelIncreasesCapacityAndUpgradeUsesCashOnly() {
        GameSession game = funded(); game.town().deposit(501); assertEquals(0, game.state().player().bankMoney());
        game.town().deposit(500); assertEquals(500, game.state().player().bankMoney());
        game.town().upgrade(Building.TOWN_HALL); game.town().upgrade(Building.BANK); assertEquals(2000, game.state().town().bankCapacity());
        game.town().deposit(1500); assertEquals(2000, game.state().player().bankMoney());
        GameSession poor = new GameSession(); poor.town().deposit(20); poor.state().inventory().add(Item.WOOD, 2); poor.state().inventory().add(Item.STONE);
        poor.town().upgrade(Building.TOWN_HALL); assertEquals(20, poor.state().player().bankMoney()); assertEquals(1, poor.state().town().level(Building.TOWN_HALL));
    }
    @Test void innAlwaysRestoresStaminaInLessTimeAtHigherLevels() {
        GameSession game = funded(); int[] expected = {480, 360, 240, 180, 120};
        for (int level = 1; level <= 5; level++) {
            assertEquals(expected[level - 1], game.state().town().sleepMinutes()); game.state().player().spendStamina(60);
            var time = game.state().time().value(); game.sleep(); assertEquals(time.plusMinutes(expected[level - 1]), game.state().time().value());
            assertEquals(100, game.state().player().stamina());
            if (level < 5) { game.town().upgrade(Building.TOWN_HALL); game.town().upgrade(Building.INN); }
        }
    }
    @Test void cityActionsAreRejectedOutsideTownWithoutMutation() {
        GameSession game = funded(); game.travel(Location.PINE_FOREST); int money = game.state().player().money();
        game.town().deposit(10); game.town().withdraw(10); game.town().upgrade(Building.TOWN_HALL); game.town().buy(ShopOffer.at(Building.GUNSMITH).getFirst());
        assertEquals(money, game.state().player().money()); assertEquals(0, game.state().player().bankMoney()); assertEquals(1, game.state().town().level(Building.TOWN_HALL));
    }
}

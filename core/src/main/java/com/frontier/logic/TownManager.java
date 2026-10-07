package com.frontier.logic;

import com.frontier.model.*;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/** Alle stadsacties controleren eerst de volledige transactie, zonder UI-afhankelijkheid. */
public final class TownManager {
    private final Supplier<GameState> state;
    private final BooleanSupplier working;
    public TownManager(Supplier<GameState> state, BooleanSupplier working) { this.state = state; this.working = working; }
    private String accessReason() {
        if (state.get().location() != Location.RED_CREEK) return "Reis eerst naar Red Creek.";
        return working.getAsBoolean() ? "Wacht tot je werkzaamheid klaar is." : "";
    }
    public String upgradeReason(Building building) {
        GameState game = state.get(); String reason = accessReason(); if (!reason.isEmpty()) return reason;
        int level = game.town().level(building);
        if (level == Building.MAX_LEVEL) return "Dit gebouw heeft het hoogste level.";
        if (building != Building.TOWN_HALL && level >= game.town().level(Building.TOWN_HALL)) return "Upgrade eerst het stadhuis.";
        UpgradeCost cost = UpgradeCost.forBuilding(building, level);
        if (game.player().money() < cost.money()) return "Te weinig cash. Neem eventueel geld op bij de bank.";
        for (var material : cost.materials().entrySet())
            if (game.inventory().count(material.getKey()) < material.getValue()) return "Je hebt nog niet alle bouwmaterialen.";
        return "";
    }
    public String upgrade(Building building) {
        String reason = upgradeReason(building); if (!reason.isEmpty()) return reason;
        GameState game = state.get(); UpgradeCost cost = UpgradeCost.forBuilding(building, game.town().level(building));
        game.player().spendMoney(cost.money()); cost.materials().forEach(game.inventory()::remove);
        game.town().upgrade(building);
        return building.displayName() + " uitgebreid naar level " + game.town().level(building) + ".";
    }
    public String buyReason(ShopOffer offer) {
        String reason = accessReason(); if (!reason.isEmpty()) return reason;
        if (!ShopOffer.available(offer)) return "Dit voorwerp wordt hier niet verkocht.";
        GameState game = state.get();
        if (game.town().level(offer.building()) < offer.requiredLevel()) return "Upgrade dit gebouw naar level " + offer.requiredLevel() + ".";
        if (game.player().money() < offer.price()) return "Te weinig cash.";
        if (game.inventory().count(offer.item()) == Integer.MAX_VALUE) return "Je inventaris heeft het maximale aantal.";
        return "";
    }
    public String buy(ShopOffer offer) {
        String reason = buyReason(offer); if (!reason.isEmpty()) return reason;
        state.get().player().spendMoney(offer.price()); state.get().inventory().add(offer.item());
        return offer.item().displayName() + " gekocht voor $" + offer.price() + ".";
    }
    public String deposit(int amount) {
        String reason = accessReason(); if (!reason.isEmpty()) return reason;
        Player player = state.get().player();
        if (amount <= 0) return "Vul een positief bedrag in.";
        if (amount > player.money()) return "Te weinig cash.";
        if ((long) player.bankMoney() + amount > state.get().town().bankCapacity()) return "Je rekeninglimiet is bereikt. Upgrade de bank.";
        player.deposit(amount); return "$" + amount + " op je rekening gestort.";
    }
    public String withdraw(int amount) {
        String reason = accessReason(); if (!reason.isEmpty()) return reason;
        Player player = state.get().player();
        if (amount <= 0) return "Vul een positief bedrag in.";
        if (amount > player.bankMoney()) return "Te weinig geld op je rekening.";
        if ((long) player.money() + amount > Integer.MAX_VALUE) return "Je kunt niet meer cash meenemen.";
        player.withdraw(amount); return "$" + amount + " van je rekening opgenomen.";
    }
}

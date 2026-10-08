package com.frontier.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.frontier.logic.*;
import com.frontier.model.*;
import java.util.EnumMap;
import java.util.function.Consumer;

/** Gebouwbeheer, winkels, bank en herberg in een afzonderlijk stadsvenster. */
final class TownDialog extends Dialog {
    private final GameSession game;
    private final Consumer<String> onAction;
    private final Label description, summary, requirements;
    private final TextButton sleep, shop, bank, upgrade, saloon;
    private final EnumMap<Building, TextButton> signs = new EnumMap<>(Building.class);
    private Image streetImage;
    private Texture streetTexture;
    private java.util.Set<VillageProject> picturedProjects = java.util.Set.of();
    private Building selected = Building.TOWN_HALL;
    TownDialog(Skin skin, GameSession game, Consumer<String> onAction) {
        super("Red Creek - Stad", skin); this.game = game; this.onAction = onAction; setName("town-window");
        streetTexture = TownArtwork.create(game.state().journal()); picturedProjects=game.state().journal().completed();
        Table contents = getContentTable(); contents.pad(18);
        contents.add(new Label("RED CREEK", skin, "title")).left().padBottom(8).row();
        contents.add(new Label("Een dorp dat weer tot leven komt. In de Saloon besluiten we wat we samen herstellen.", skin, "muted")).left().padBottom(10).row();
        Stack street = new Stack(); streetImage=new Image(streetTexture); street.add(streetImage);
        Table buttons = new Table(); buttons.bottom().padBottom(20); buttons.defaults().width(146).height(50).pad(6);
        for (Building building : Building.values()) {
            TextButton sign = Ui.button(skin, building.displayName(), "building-" + building.name(), () -> { selected = building; refresh(); });
            signs.put(building, sign); buttons.add(sign);
        }
        street.add(buttons); contents.add(street).width(960).height(270).row();
        description = new Label("", skin); description.setWrap(true); description.setName("town-description");
        contents.add(description).width(930).height(55).left().padTop(10).row();
        requirements = new Label("", skin, "muted"); requirements.setWrap(true); requirements.setName("town-upgrade-cost");
        contents.add(requirements).width(930).height(50).left().padTop(4).row();
        sleep = Ui.button(skin, "Slapen", "sleep", () -> action(game.sleep()));
        shop = Ui.button(skin, "Winkel openen", "shop-open", () -> new ShopDialog(skin, game, selected, this::action).show(getStage()));
        bank = Ui.button(skin, "Rekening beheren", "bank-open", () -> new BankDialog(skin, game, this::action).show(getStage()));
        upgrade = Ui.button(skin, "Gebouw upgraden", "upgrade-building", () -> new BuildingUpgradeDialog(skin, game, selected, this::action).show(getStage()));
        saloon = Ui.button(skin, "Opdrachtgevers", "saloon-open", () -> new SaloonDialog(skin, game, this::action).show(getStage()));
        Table controls = new Table(); controls.defaults().width(174).height(42).padRight(12);
        controls.add(upgrade); controls.add(shop); controls.add(bank); controls.add(sleep); controls.add(saloon);
        contents.add(controls).left().padTop(8).row();
        summary = new Label("", skin, "muted"); summary.setName("town-summary");
        contents.add(summary).left().padTop(12).row();
        button("Terug naar de wereldkaart"); Ui.nameDialogButtons(this, "town-close"); getButtonTable().pad(14); refresh();
    }
    private void action(String message) { onAction.accept(message); refresh(); }
    @Override public void act(float delta) { super.act(delta); if(!picturedProjects.equals(game.state().journal().completed())) refresh(); }
    @Override public boolean remove() { boolean removed=super.remove(); if(removed && streetTexture!=null){streetTexture.dispose();streetTexture=null;} return removed; }
    private void refresh() {
        if(!picturedProjects.equals(game.state().journal().completed())) {
            Texture replacement=TownArtwork.create(game.state().journal()); streetImage.setDrawable(new com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable(replacement)); streetTexture.dispose(); streetTexture=replacement; picturedProjects=game.state().journal().completed();
        }
        int level = game.state().town().level(selected);
        signs.forEach((building, sign) -> sign.setText(building.displayName() + "\nLevel " + game.state().town().level(building)));
        description.setText(selected.displayName() + " - Level " + level + "\n" + selected.description()
            + (selected == Building.BANK ? " Limiet: $" + game.state().town().bankCapacity() + "." : ""));
        if (level == Building.MAX_LEVEL) requirements.setText("Dit gebouw is volledig uitgebreid.");
        else {
            UpgradeCost cost = UpgradeCost.forBuilding(selected, level);
            StringBuilder text = new StringBuilder("Naar level " + (level + 1) + ": $" + cost.money() + " cash");
            for (Item item : Item.values()) if (cost.materials().containsKey(item))
                text.append("  |  ").append(item.displayName()).append(" ").append(game.state().inventory().count(item)).append("/").append(cost.materials().get(item));
            String reason = game.town().upgradeReason(selected); if (!reason.isEmpty()) text.append("\n").append(reason);
            requirements.setText(text.toString());
        }
        upgrade.setDisabled(level == Building.MAX_LEVEL);
        shop.setVisible(selected == Building.GUNSMITH || selected == Building.TAILOR);
        saloon.setVisible(selected == Building.SALOON);
        bank.setVisible(selected == Building.BANK); sleep.setVisible(selected == Building.INN);
        sleep.setText(Ui.duration(game.story().sleepMinutes()) + " slapen");
        sleep.setDisabled(game.state().location() != Location.RED_CREEK || game.isWorking());
        summary.setText(game.state().time().display() + "   |   Energie " + game.state().player().stamina()
            + "/100   |   Dorpsprojecten " + game.state().journal().completed().size() + "/3   |   Cash $" + game.state().player().money() + "   |   Rekening $" + game.state().player().bankMoney());
    }
}

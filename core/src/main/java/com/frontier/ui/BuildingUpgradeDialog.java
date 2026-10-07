package com.frontier.ui;

import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.frontier.logic.*;
import com.frontier.model.*;
import java.util.function.Consumer;

final class BuildingUpgradeDialog extends Dialog {
    private final GameSession game;
    private final Building building;
    private final Consumer<String> onAction;
    BuildingUpgradeDialog(Skin skin, GameSession game, Building building, Consumer<String> onAction) {
        super(building.displayName() + " upgraden", skin); this.game = game; this.building = building; this.onAction = onAction;
        int level = game.state().town().level(building); UpgradeCost cost = UpgradeCost.forBuilding(building, level);
        getContentTable().pad(22);
        getContentTable().add(new Label("Level " + level + " naar " + (level + 1), skin, "accent")).left().row();
        getContentTable().add(new Label("Kosten: $" + cost.money() + " cash", skin)).left().padTop(12).row();
        Table materials = new Table();
        for (Item item : Item.values()) {
            Integer needed = cost.materials().get(item); if (needed == null) continue;
            Table line = new Table(); line.add(new Image(ItemIcons.get(skin, item))).size(42).padRight(12);
            line.add(new Label(item.displayName(), skin)).left().expandX();
            line.add(new Label(game.state().inventory().count(item) + " / " + needed, skin, "accent")).right();
            materials.add(line).width(440).padTop(8).row();
        }
        getContentTable().add(materials).padTop(8).row();
        String reason = game.town().upgradeReason(building);
        Label note = new Label(reason.isEmpty() ? "De materialen en het geld worden direct verbruikt." : reason, skin, "muted");
        note.setWrap(true); getContentTable().add(note).width(440).padTop(16).row();
        button("Annuleren", false); button("Upgraden", true);
        Ui.nameDialogButtons(this, "upgrade-cancel", "upgrade-confirm");
        ((TextButton) getButtonTable().getChildren().get(1)).setDisabled(!reason.isEmpty()); getButtonTable().pad(16);
    }
    @Override protected void result(Object value) { if (Boolean.TRUE.equals(value)) onAction.accept(game.town().upgrade(building)); }
}

package com.frontier.ui;

import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.frontier.model.*;

final class InventoryDialog extends Dialog {
    private final Skin skin;
    private final Inventory inventory;
    private final com.frontier.logic.GameSession game;
    private final java.util.function.Consumer<String> onAction;
    private final TextButton equip;
    private Item selected;
    private final Table items = new Table();
    private final Label description;
    private final java.util.Map<Item.Category, TextButton> filters = new java.util.EnumMap<>(Item.Category.class);
    private final TextButton all;
    InventoryDialog(Skin skin, com.frontier.logic.GameSession game, java.util.function.Consumer<String> onAction) {
        super("Inventaris", skin);
        this.skin = skin; this.game=game; this.onAction=onAction; this.inventory = game.state().inventory();
        description = new Label("Klik op een voorwerp voor de beschrijving.", skin, "muted"); description.setWrap(true);
        getContentTable().pad(20);
        getContentTable().add(new Label("Je draagt " + inventory.totalCount() + " voorwerpen bij je.", skin, "accent")).left().row();
        Table tabs = new Table(); tabs.defaults().width(138).height(40).padRight(6);
        all = Ui.button(skin, "Alles", "inventory-all", () -> refresh(null)); tabs.add(all);
        for (Item.Category category : Item.Category.values()) {
            TextButton tab = Ui.button(skin, category.displayName(), "inventory-" + category.name(), () -> refresh(category));
            filters.put(category, tab); tabs.add(tab);
        }
        getContentTable().add(tabs).padTop(18).padBottom(14).row();
        ScrollPane scroll = new ScrollPane(items, skin);
        scroll.setFadeScrollBars(false); scroll.setScrollingDisabled(true, false);
        getContentTable().add(scroll).width(580).height(260).row();
        getContentTable().add(description).width(580).height(55).padTop(12).row();
        equip=Ui.button(skin,"Uitrusten","equip-item",()->{ onAction.accept(game.toggleEquipment(selected)); select(selected); });
        getContentTable().add(equip).width(220).height(40).padTop(8).row(); equip.setVisible(false);
        button("Sluiten"); Ui.nameDialogButtons(this, "inventory-close");
        getButtonTable().pad(14);
        refresh(null);
    }
    private void select(Item item) {
        selected=item; boolean wearing=game.state().equipment().wearing(item);
        description.setText(item.description()+"\n"+Equipment.bonus(item)+(wearing?" (Uitgerust)":""));
        equip.setVisible(Equipment.slot(item)!=null); equip.setText(wearing?"Uittrekken":"Uitrusten"); equip.setDisabled(game.isWorking());
    }
    private void refresh(Item.Category category) {
        items.clearChildren(); items.top(); equip.setVisible(false);
        all.setDisabled(category == null);
        filters.forEach((key, tab) -> tab.setDisabled(key == category));
        boolean any = false; int column = 0;
        for (Item item : Item.values()) {
            int quantity = inventory.count(item);
            if (quantity == 0 || category != null && item.category() != category) continue;
            any = true;
            ItemTile tile = new ItemTile(skin, item, "x " + quantity + (game.state().equipment().wearing(item) ? " - Uitgerust" : ""), "item-" + item.name(), () -> select(item));
            items.add(tile).width(135).height(130).pad(3);
            if (++column % 4 == 0) items.row();
        }
        if (!any) {
            Label empty = new Label(inventory.totalCount() == 0
                ? "Je inventaris is leeg. Tijdens werk kun je voorwerpen vinden."
                : "In deze categorie heb je nog geen voorwerpen.", skin, "muted");
            empty.setWrap(true); items.add(empty).width(535).pad(18);
        }
        description.setText("Klik op een voorwerp voor de beschrijving.");
    }
}

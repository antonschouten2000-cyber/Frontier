package com.frontier.ui;

import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.frontier.logic.GameSession;
import java.util.function.Consumer;

final class BankDialog extends Dialog {
    private final GameSession game;
    private final Label balances, note;
    private final TextField amount;
    BankDialog(Skin skin, GameSession game, Consumer<String> onAction) {
        super("Bank van Red Creek", skin); this.game = game; setName("bank-window"); getContentTable().pad(24);
        balances = new Label("", skin, "accent"); balances.setName("bank-balances");
        getContentTable().add(balances).left().width(470).padBottom(18).row();
        getContentTable().add(new Label("Bedrag in dollars", skin)).left().row();
        amount = new TextField("10", skin); amount.setName("bank-amount"); amount.setMaxLength(10);
        amount.setTextFieldFilter((field, character) -> Character.isDigit(character));
        getContentTable().add(amount).width(470).height(44).padTop(8).row();
        Table actions = new Table();
        actions.add(Ui.button(skin, "Storten", "bank-deposit", () -> transact(true, onAction))).width(220).height(44).padRight(12);
        actions.add(Ui.button(skin, "Opnemen", "bank-withdraw", () -> transact(false, onAction))).width(220).height(44);
        getContentTable().add(actions).padTop(14).row();
        note = new Label("Je betaalt aankopen en upgrades met cash. Storten en opnemen kost geen geld of tijd.", skin, "muted"); note.setWrap(true);
        getContentTable().add(note).width(470).height(72).padTop(14).row();
        button("Sluiten"); Ui.nameDialogButtons(this, "bank-close"); getButtonTable().pad(16); refresh();
    }
    private void transact(boolean deposit, Consumer<String> onAction) {
        int value;
        try { value = Integer.parseInt(amount.getText()); }
        catch (NumberFormatException e) { note.setText("Vul een geldig positief bedrag in."); return; }
        String result = deposit ? game.town().deposit(value) : game.town().withdraw(value);
        note.setText(result); onAction.accept(result); refresh();
    }
    private void refresh() {
        balances.setText("Cash: $" + game.state().player().money() + "\nRekening: $" + game.state().player().bankMoney()
            + " / $" + game.state().town().bankCapacity());
    }
}

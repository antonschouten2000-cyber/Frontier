package com.frontier.ui;

import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.frontier.model.*;

/** Postvak met telegrammen; nieuwe post verschijnt ook terwijl dit venster open is. */
final class MessagesDialog extends Dialog {
    private final Skin skin;
    private final Mailbox mailbox;
    private final Runnable onRead;
    private final Table list = new Table();
    private final Label details;
    private int revision = -1;
    MessagesDialog(Skin skin, Mailbox mailbox, Runnable onRead) {
        super("Berichten - Telegrammen", skin); this.skin = skin; this.mailbox = mailbox; this.onRead = onRead;
        setName("messages-window"); getContentTable().pad(20);
        getContentTable().add(new Label("TELEGRAAFKANTOOR", skin, "title")).left().padBottom(14).row();
        ScrollPane scroll = new ScrollPane(list, skin); scroll.setFadeScrollBars(false); scroll.setScrollingDisabled(true, false);
        getContentTable().add(scroll).width(610).height(180).row();
        Table paper = new Table(); paper.setBackground(skin.getDrawable("card")); paper.pad(16);
        details = new Label("Kies een telegram om het verslag te lezen.", skin); details.setWrap(true); details.setName("telegram-details");
        paper.add(details).width(570).height(215).left().top();
        getContentTable().add(paper).padTop(14).row();
        button("Sluiten"); Ui.nameDialogButtons(this, "messages-close"); getButtonTable().pad(14);
        refresh();
    }
    @Override public void act(float delta) {
        super.act(delta);
        if (revision != mailbox.revision()) refresh();
    }
    private void refresh() {
        revision = mailbox.revision(); list.clearChildren(); list.top();
        var messages = mailbox.messages();
        if (messages.isEmpty()) {
            Label empty = new Label("Nog geen telegrammen. Rond een werkzaamheid af om je eerste bericht te ontvangen.", skin, "muted");
            empty.setWrap(true); list.add(empty).width(570).pad(16);
        }
        for (int i = messages.size() - 1; i >= 0; i--) {
            int index = i; Telegram message = messages.get(i);
            String text = (message.read() ? "" : "NIEUW  -  ") + message.jobName() + "\n" + new GameTime(message.dateTime()).display();
            list.add(Ui.button(skin, text, "telegram-" + i, () -> select(index))).width(580).height(60).padBottom(7).row();
        }
    }
    private void select(int index) {
        Telegram message = mailbox.read(index);
        int seconds = message.durationSeconds();
        String duration = seconds < 60 ? seconds + " seconden" : Ui.duration(seconds / 60);
        details.setText("TELEGRAM - WERK AFGEROND\n"
            + message.jobName() + " bij " + message.location().displayName() + "\n"
            + new GameTime(message.dateTime()).display() + "\n\n"
            + "Werktijd: " + duration + "\n"
            + "Opbrengst: $" + message.money() + "  |  Ervaring: +" + message.xp() + "\n"
            + "Gevonden: " + (message.found() == null ? "Geen voorwerpen." : "1 x " + message.found().displayName()));
        refresh(); onRead.run();
    }
}

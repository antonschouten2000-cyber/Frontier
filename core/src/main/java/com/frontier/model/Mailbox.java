package com.frontier.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Bewaart de laatste honderd telegrammen, van oud naar nieuw. */
public final class Mailbox {
    public static final int MAX_MESSAGES = 100;
    private final List<Telegram> messages = new ArrayList<>();
    private int revision;
    public List<Telegram> messages() { return List.copyOf(messages); }
    public int size() { return messages.size(); }
    public int revision() { return revision; }
    public long unreadCount() { return messages.stream().filter(message -> !message.read()).count(); }
    public void add(Telegram telegram) {
        Objects.requireNonNull(telegram);
        if (messages.size() == MAX_MESSAGES) messages.removeFirst();
        messages.add(telegram); revision++;
    }
    public Telegram read(int index) {
        Telegram message = messages.get(index);
        if (!message.read()) { message = message.markRead(); messages.set(index, message); revision++; }
        return message;
    }
}

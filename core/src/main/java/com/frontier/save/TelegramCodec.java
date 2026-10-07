package com.frontier.save;

import com.badlogic.gdx.utils.JsonValue;
import com.frontier.model.*;
import java.time.LocalDateTime;

/** Telegrammen bewaren hun eigen beloningen, ook als de werkbalans later verandert. */
final class TelegramCodec {
    private TelegramCodec() {}
    static Data[] write(Mailbox mailbox) {
        return mailbox.messages().stream().map(message -> {
            Data data = new Data(); data.jobName = message.jobName(); data.location = message.location().name();
            data.durationSeconds = message.durationSeconds(); data.money = message.money(); data.xp = message.xp();
            data.found = message.found() == null ? null : message.found().name(); data.dateTime = message.dateTime().toString();
            data.read = message.read(); data.material = message.material() == null ? null : message.material().name();
            data.materialQuantity = message.materialQuantity(); data.levelBonus = message.levelBonus(); return data;
        }).toArray(Data[]::new);
    }
    static void read(JsonValue root, int version, Mailbox mailbox) {
        JsonValue entries = root.get("telegrams");
        if (entries == null || !entries.isArray() || entries.size > Mailbox.MAX_MESSAGES)
            throw new IllegalArgumentException("Ongeldige berichtenlijst.");
        for (JsonValue entry : entries) mailbox.add(readMessage(entry, version));
    }
    private static Telegram readMessage(JsonValue entry, int version) {
        if (!entry.isObject()) throw new IllegalArgumentException("Ongeldig telegram.");
        for (String field : new String[]{"jobName", "location", "dateTime"})
            if (!entry.has(field) || !entry.get(field).isString()) throw new IllegalArgumentException("Ongeldige telegramtekst.");
        int duration = number(entry, "durationSeconds"), money = number(entry, "money"), xp = number(entry, "xp");
        if (!entry.has("read") || !entry.get("read").isBoolean()) throw new IllegalArgumentException("Ongeldige leesstatus.");
        Item found = item(entry, "found"), material = version >= 5 ? item(entry, "material") : null;
        int quantity = version >= 5 ? number(entry, "materialQuantity") : 0;
        int bonus = version >= 5 ? number(entry, "levelBonus") : 0;
        return new Telegram(entry.getString("jobName"), Location.valueOf(entry.getString("location")), duration, money, xp, found,
            LocalDateTime.parse(entry.getString("dateTime")), entry.getBoolean("read"), material, quantity, bonus);
    }
    private static int number(JsonValue entry, String field) {
        JsonValue value = entry.get(field);
        if (value == null || !value.isLong() || value.asLong() < 0 || value.asLong() > Integer.MAX_VALUE)
            throw new IllegalArgumentException("Ongeldig telegramgetal.");
        return value.asInt();
    }
    private static Item item(JsonValue entry, String field) {
        JsonValue value = entry.get(field);
        if (value == null || !value.isNull() && !value.isString()) throw new IllegalArgumentException("Ongeldig voorwerp in telegram.");
        return value.isNull() ? null : Item.valueOf(value.asString());
    }
    public static final class Data {
        public String jobName, location, found, dateTime, material;
        public int durationSeconds, money, xp, materialQuantity, levelBonus;
        public boolean read;
    }
}

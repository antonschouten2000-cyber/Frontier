package com.frontier.save;

import com.badlogic.gdx.utils.JsonValue;
import com.frontier.model.*;
import java.util.EnumMap;

/** Valideert nieuwe stadsgegevens voordat de opgeslagen toestand wordt gebruikt. */
final class TownSaveCodec {
    private TownSaveCodec() {}
    static int bankMoney(JsonValue root) {
        JsonValue value = root.get("bankMoney");
        if (value == null || !value.isLong() || value.asLong() < 0 || value.asLong() > Integer.MAX_VALUE)
            throw new IllegalArgumentException("Ongeldig banksaldo.");
        return value.asInt();
    }
    static TownState read(JsonValue root) {
        JsonValue entries = root.get("buildingLevels");
        if (entries == null || !entries.isObject()) throw new IllegalArgumentException("Gebouwlevels ontbreken.");
        EnumMap<Building, Integer> levels = new EnumMap<>(Building.class);
        for (JsonValue entry : entries) {
            Building building = Building.valueOf(entry.name());
            if (!entry.isLong() || entry.asLong() < 1 || entry.asLong() > Building.MAX_LEVEL || levels.containsKey(building))
                throw new IllegalArgumentException("Ongeldig gebouwlevel.");
            levels.put(building, entry.asInt());
        }
        if (root.getInt("version") == 5) levels.putIfAbsent(Building.SALOON, 1);
        return new TownState(levels);
    }
}

package com.frontier.save;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;
import com.frontier.model.*;
import com.frontier.logic.JobManager;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;

/** Versioned JSON stored outside the checkout; invalid loads never replace the live game. */
public final class SaveManager {
    private final Path file;
    public SaveManager(Path file) { this.file = file.toAbsolutePath(); }
    public static SaveManager local() {
        Path directory = Path.of(System.getProperty("frontier.saveDir",
            Path.of(System.getProperty("user.home"), ".frontier").toString()));
        return new SaveManager(directory.resolve("save.json"));
    }
    public Path file() { return file; }
    public void save(GameState state) throws IOException {
        SaveData data = new SaveData();
        Player player = state.player();
        data.version = 7;
        data.journal = JournalCodec.write(state.journal());
        data.equipment = new java.util.LinkedHashMap<>();
        state.equipment().items().forEach((slot,item)->data.equipment.put(slot.name(),item.name()));
        data.missions = new java.util.LinkedHashMap<>();
        state.missions().entries().forEach((mission, entry) -> {
            MissionData value = new MissionData(); value.progress=entry.progress(); value.claimed=entry.claimed();
            data.missions.put(mission.name(), value);
        });
        data.bankMoney = player.bankMoney();
        data.buildingLevels = new java.util.LinkedHashMap<>();
        state.town().levels().forEach((building, level) -> data.buildingLevels.put(building.name(), level));
        data.telegrams = TelegramCodec.write(state.mailbox());
        if (state.activeWork() != null) {
            ActiveWork work = state.activeWork(); data.activeWork = new WorkData();
            data.activeWork.jobId = work.job().id(); data.activeWork.duration = work.duration().name();
            data.activeWork.startedAt = work.startedAt(); data.activeWork.endsAt = work.endsAt();
        }
        data.inventory = new java.util.LinkedHashMap<>();
        state.inventory().contents().forEach((item, count) -> data.inventory.put(item.name(), count));
        data.player = player.name();
        data.money = player.money();
        data.level = player.level();
        data.xp = player.xp();
        data.stamina = player.stamina();
        data.location = state.location().name();
        data.dateTime = state.time().value().toString();
        Json json = new Json();
        json.setOutputType(JsonWriter.OutputType.json);
        json.setUsePrototypes(false);
        json.setTypeName(null);
        Files.createDirectories(file.getParent());
        Path temporary = Files.createTempFile(file.getParent(), "frontier-", ".tmp");
        try {
            Files.writeString(temporary, json.prettyPrint(data), StandardCharsets.UTF_8);
            try { Files.move(temporary, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE); }
            catch (AtomicMoveNotSupportedException e) {
                Files.move(temporary, file, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally { Files.deleteIfExists(temporary); }
    }
    public GameState load() throws IOException {
        if (!Files.isRegularFile(file)) throw new IOException("Nog geen opgeslagen spel. Kies eerst Spel opslaan.");
        if (Files.size(file) > 256 * 1024) throw new IOException("Het opslagbestand is te groot.");
        try {
            JsonValue root = new JsonReader().parse(Files.readString(file, StandardCharsets.UTF_8));
            if (!root.isObject()) throw new IllegalArgumentException("Ongeldig opslagformaat.");
            String[] fields = {"version", "player", "money", "level", "xp", "stamina", "location", "dateTime"};
            for (String field : fields) {
                if (!root.has(field) || root.get(field).isNull())
                    throw new IllegalArgumentException("Ontbrekend veld: " + field);
            }
            for (String field : new String[]{"version", "money", "level", "xp", "stamina"}) {
                JsonValue value = root.get(field);
                if (!value.isLong() || value.asLong() < 0 || value.asLong() > Integer.MAX_VALUE)
                    throw new IllegalArgumentException("Ongeldig getal: " + field);
            }
            for (String field : new String[]{"player", "location", "dateTime"}) {
                if (!root.get(field).isString()) throw new IllegalArgumentException("Ongeldige tekst: " + field);
            }
            int version = root.getInt("version");
            if (version < 1 || version > 7) throw new IllegalArgumentException("Deze opslagversie wordt niet ondersteund.");
            Inventory inventory = new Inventory();
            if (version >= 2) {
                JsonValue entries = root.get("inventory");
                if (entries == null || !entries.isObject()) throw new IllegalArgumentException("Inventaris ontbreekt of is ongeldig.");
                java.util.Set<Item> seen = new java.util.HashSet<>();
                for (JsonValue entry : entries) {
                    Item item = Item.valueOf(entry.name());
                    if (!seen.add(item) || !entry.isLong() || entry.asLong() <= 0 || entry.asLong() > Integer.MAX_VALUE)
                        throw new IllegalArgumentException("Ongeldig voorwerpaantal.");
                    inventory.add(item, entry.asInt());
                }
            }
            Player player = new Player(root.getString("player").equals("Traveler") ? "Reiziger" : root.getString("player"), root.getInt("money"),
                root.getInt("stamina"), root.getInt("xp"), root.getInt("level"), version >= 5 ? TownSaveCodec.bankMoney(root) : 0);
            GameState loaded = new GameState(player, new GameTime(LocalDateTime.parse(root.getString("dateTime"))),
                Location.valueOf(root.getString("location")), inventory);
            if (version >= 5) loaded.setTown(TownSaveCodec.read(root));
            if (version >= 3 && root.has("activeWork") && !root.get("activeWork").isNull()) {
                JsonValue work = root.get("activeWork");
                if (!work.isObject() || !work.has("jobId") || !work.has("duration")
                    || !work.has("startedAt") || !work.has("endsAt")
                    || !work.get("jobId").isString() || !work.get("duration").isString()
                    || !work.get("startedAt").isLong() || !work.get("endsAt").isLong())
                    throw new IllegalArgumentException("Ongeldige lopende klus.");
                loaded.setActiveWork(new ActiveWork(JobManager.byId(work.getString("jobId")),
                    WorkDuration.valueOf(work.getString("duration")), work.getLong("startedAt"), work.getLong("endsAt")));
            }
            if (version >= 4) TelegramCodec.read(root, version, loaded.mailbox());
            if (version >= 6) {
                JsonValue gear = root.get("equipment");
                if (gear == null || !gear.isObject()) throw new IllegalArgumentException("Uitrusting ontbreekt.");
                java.util.Set<Equipment.Slot> slots = new java.util.HashSet<>();
                for (JsonValue entry : gear) {
                    Equipment.Slot slot=Equipment.Slot.valueOf(entry.name());
                    if (!entry.isString() || !slots.add(slot)) throw new IllegalArgumentException("Ongeldige uitrusting.");
                    Item item=Item.valueOf(entry.asString());
                    if (Equipment.slot(item)!=slot) throw new IllegalArgumentException("Verkeerd lichaamsdeel.");
                    loaded.equipment().equip(item,inventory);
                }
                JsonValue entries = root.get("missions");
                if (entries == null || !entries.isObject()) throw new IllegalArgumentException("Opdrachten ontbreken.");
                for (JsonValue entry : entries) {
                    if (!entry.isObject() || !entry.has("progress") || !entry.get("progress").isLong()
                        || entry.getLong("progress") < 0 || entry.getLong("progress") > Integer.MAX_VALUE
                        || !entry.has("claimed") || !entry.get("claimed").isBoolean())
                        throw new IllegalArgumentException("Ongeldige opdracht.");
                    loaded.missions().restore(Mission.valueOf(entry.name()), entry.getInt("progress"), entry.getBoolean("claimed"));
                }
            }
            if (version >= 7) JournalCodec.read(root, loaded.journal());
            if (loaded.location()==Location.FORGOTTEN_STOP && !loaded.journal().done(VillageProject.BRIDGE))
                throw new IllegalArgumentException("De brug is nog niet hersteld.");
            return loaded;
        } catch (RuntimeException e) {
            throw new IOException("Dit spel kan niet worden geladen: " + e.getMessage(), e);
        }
    }
    public static final class MissionData { public int progress; public boolean claimed; }
    public static final class SaveData {
        public JournalCodec.Data journal;
        public java.util.Map<String, MissionData> missions;
        public java.util.Map<String, String> equipment;
        public int version, money, bankMoney, level, xp, stamina;
        public String player, location, dateTime;
        public java.util.Map<String, Integer> inventory;
        public WorkData activeWork;
        public TelegramCodec.Data[] telegrams;
        public java.util.Map<String, Integer> buildingLevels;
    }
    public static final class WorkData {
        public String jobId, duration;
        public long startedAt, endsAt;
    }
}

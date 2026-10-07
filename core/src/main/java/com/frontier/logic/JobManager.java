package com.frontier.logic;

import com.frontier.model.Job;
import com.frontier.model.Location;
import com.frontier.model.WorkDuration;
import java.util.List;
import java.util.Map;
import java.util.random.RandomGenerator;

public final class JobManager {
    private final RandomGenerator random;
    private static final Map<Location, List<Job>> JOBS = Map.ofEntries(
        Map.entry(Location.RED_CREEK, List.of()),
        Map.entry(Location.PINE_FOREST, List.of(
            new Job("wood", Location.PINE_FOREST, "Hout hakken", 15, 2, 25, 60, 20),
            new Job("branches", Location.PINE_FOREST, "Takken verzamelen", 9, 1, 12, 60, 10),
            new Job("trail", Location.PINE_FOREST, "Bospad vrijmaken", 13, 2, 20, 60, 17),
            new Job("split", Location.PINE_FOREST, "Boomstammen kloven", 17, 2, 28, 60, 23),
            new Job("resin", Location.PINE_FOREST, "Hars verzamelen", 10, 2, 14, 60, 12),
            new Job("plant", Location.PINE_FOREST, "Jonge bomen planten", 14, 2, 22, 60, 19))),
        Map.entry(Location.OLD_MINE, List.of(
            new Job("ore", Location.OLD_MINE, "Erts delven", 20, 3, 30, 60, 25),
            new Job("stones", Location.OLD_MINE, "Erts sorteren", 12, 2, 16, 60, 14),
            new Job("carts", Location.OLD_MINE, "Mijnkarren vullen", 17, 2, 24, 60, 21),
            new Job("supports", Location.OLD_MINE, "Stutbalken plaatsen", 22, 3, 32, 60, 28),
            new Job("rubble", Location.OLD_MINE, "Puin ruimen", 16, 2, 22, 60, 19),
            new Job("tools", Location.OLD_MINE, "Mijnwerktuigen poetsen", 10, 1, 12, 60, 11))),
        Map.entry(Location.LONELY_RANCH, List.of(
            new Job("fences", Location.LONELY_RANCH, "Hekken repareren", 12, 2, 22, 60, 18),
            new Job("hay", Location.LONELY_RANCH, "Hooi stapelen", 11, 1, 18, 60, 14),
            new Job("well", Location.LONELY_RANCH, "Water putten", 8, 1, 12, 60, 9),
            new Job("feed", Location.LONELY_RANCH, "Vee voeren", 10, 1, 14, 60, 12),
            new Job("harvest", Location.LONELY_RANCH, "Graan oogsten", 16, 2, 25, 60, 21),
            new Job("baskets", Location.LONELY_RANCH, "Mandwerk vlechten", 9, 1, 10, 60, 10))),
        Map.entry(Location.WILLOW_FARM, List.of(
            new Job("milk", Location.WILLOW_FARM, "Koeien melken", 14, 2, 18, 60, 16),
            new Job("eggs", Location.WILLOW_FARM, "Eieren rapen", 9, 1, 10, 60, 10),
            new Job("stable", Location.WILLOW_FARM, "Stallen uitmesten", 15, 2, 22, 60, 18))),
        Map.entry(Location.SUNRISE_FARM, List.of(
            new Job("corn", Location.SUNRISE_FARM, "Mais plukken", 16, 2, 24, 60, 20),
            new Job("orchard", Location.SUNRISE_FARM, "Appels oogsten", 13, 2, 18, 60, 15),
            new Job("sacks", Location.SUNRISE_FARM, "Zakken vullen", 12, 1, 20, 60, 14))),
        Map.entry(Location.RIVER_FARM, List.of(
            new Job("irrigate", Location.RIVER_FARM, "Akkers bevloeien", 12, 1, 16, 60, 14),
            new Job("vegetables", Location.RIVER_FARM, "Groenten sorteren", 10, 1, 12, 60, 11),
            new Job("seed", Location.RIVER_FARM, "Zaaigoed verdelen", 11, 1, 14, 60, 12))),
        Map.entry(Location.COTTON_FARM, List.of(
            new Job("cotton", Location.COTTON_FARM, "Katoen plukken", 17, 2, 24, 60, 21),
            new Job("bales", Location.COTTON_FARM, "Balen binden", 14, 2, 22, 60, 18),
            new Job("weeds", Location.COTTON_FARM, "Onkruid wieden", 11, 1, 18, 60, 13))),
        Map.entry(Location.NORTH_WOODS, List.of(
            new Job("pinecones", Location.NORTH_WOODS, "Dennenappels rapen", 10, 1, 12, 60, 11),
            new Job("saplings", Location.NORTH_WOODS, "Zaailingen verzorgen", 13, 2, 18, 60, 16),
            new Job("logs", Location.NORTH_WOODS, "Hout stapelen", 15, 2, 20, 60, 18))),
        Map.entry(Location.QUARRY, List.of(
            new Job("gravel", Location.QUARRY, "Grind zeven", 13, 2, 12, 60, 15),
            new Job("slate", Location.QUARRY, "Leisteen sorteren", 15, 2, 14, 60, 18),
            new Job("markstone", Location.QUARRY, "Bouwstenen markeren", 12, 1, 10, 60, 13))),
        Map.entry(Location.RIVERBANK, List.of(
            new Job("reeds", Location.RIVERBANK, "Riet snijden", 11, 1, 16, 60, 13),
            new Job("bank", Location.RIVERBANK, "Oever verstevigen", 16, 2, 25, 60, 21),
            new Job("nets", Location.RIVERBANK, "Visnetten herstellen", 12, 1, 14, 60, 15))),
        Map.entry(Location.TRADING_POST, List.of(
            new Job("parcels", Location.TRADING_POST, "Pakketten sorteren", 10, 1, 10, 60, 12),
            new Job("ledger", Location.TRADING_POST, "Voorraad tellen", 11, 1, 8, 60, 13),
            new Job("labels", Location.TRADING_POST, "Vracht etiketteren", 12, 1, 9, 60, 14))));
    public JobManager(RandomGenerator random) { this.random = random; }
    public List<Job> at(Location location) { return JOBS.get(location); }
    public static Job byId(String id) {
        return JOBS.values().stream().flatMap(List::stream).filter(job -> job.id().equals(id)).findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Onbekende werkzaamheid."));
    }
    public boolean contains(Job job) { return job != null && at(job.location()).contains(job); }
    public int payment(Job job, WorkDuration duration) { return job.scaledPay(payment(job), duration); }
    public int payment(Job job) {
        return job.basePay() + random.nextInt(-job.payVariation(), job.payVariation() + 1);
    }
}

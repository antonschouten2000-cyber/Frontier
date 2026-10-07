package com.frontier.logic;

import com.frontier.model.Job;
import com.frontier.model.Location;
import java.util.List;
import java.util.Map;
import java.util.random.RandomGenerator;

public final class JobManager {
    private final RandomGenerator random;
    private static final Map<Location, List<Job>> JOBS = Map.of(
        Location.RED_CREEK, List.of(),
        Location.PINE_FOREST, List.of(
            new Job("wood", Location.PINE_FOREST, "Hout hakken", 15, 2, 25, 180, 20),
            new Job("branches", Location.PINE_FOREST, "Takken verzamelen", 9, 1, 12, 90, 10),
            new Job("trail", Location.PINE_FOREST, "Bospad vrijmaken", 13, 2, 20, 120, 17),
            new Job("split", Location.PINE_FOREST, "Boomstammen kloven", 17, 2, 28, 180, 23),
            new Job("resin", Location.PINE_FOREST, "Hars verzamelen", 10, 2, 14, 90, 12),
            new Job("plant", Location.PINE_FOREST, "Jonge bomen planten", 14, 2, 22, 150, 19)),
        Location.OLD_MINE, List.of(
            new Job("ore", Location.OLD_MINE, "Erts delven", 20, 3, 30, 240, 25),
            new Job("stones", Location.OLD_MINE, "Erts sorteren", 12, 2, 16, 120, 14),
            new Job("carts", Location.OLD_MINE, "Mijnkarren vullen", 17, 2, 24, 180, 21),
            new Job("supports", Location.OLD_MINE, "Stutbalken plaatsen", 22, 3, 32, 210, 28),
            new Job("rubble", Location.OLD_MINE, "Puin ruimen", 16, 2, 22, 150, 19),
            new Job("tools", Location.OLD_MINE, "Mijnwerktuigen poetsen", 10, 1, 12, 75, 11)),
        Location.LONELY_RANCH, List.of(
            new Job("fences", Location.LONELY_RANCH, "Hekken repareren", 12, 2, 22, 150, 18),
            new Job("hay", Location.LONELY_RANCH, "Hooi stapelen", 11, 1, 18, 120, 14),
            new Job("well", Location.LONELY_RANCH, "Water putten", 8, 1, 12, 60, 9),
            new Job("feed", Location.LONELY_RANCH, "Vee voeren", 10, 1, 14, 90, 12),
            new Job("harvest", Location.LONELY_RANCH, "Graan oogsten", 16, 2, 25, 180, 21),
            new Job("baskets", Location.LONELY_RANCH, "Mandwerk vlechten", 9, 1, 10, 90, 10)));
    public JobManager(RandomGenerator random) { this.random = random; }
    public List<Job> at(Location location) { return JOBS.get(location); }
    public boolean contains(Job job) { return job != null && at(job.location()).contains(job); }
    public int payment(Job job) {
        return job.basePay() + random.nextInt(-job.payVariation(), job.payVariation() + 1);
    }
}

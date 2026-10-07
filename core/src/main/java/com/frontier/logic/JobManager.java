package com.frontier.logic;

import com.frontier.model.Job;
import com.frontier.model.Location;
import java.util.List;
import java.util.Map;
import java.util.random.RandomGenerator;

public final class JobManager {
    private final RandomGenerator random;
    private static final Map<Location, List<Job>> JOBS = Map.of(
        Location.RED_CREEK, List.of(
            new Job("wagons", Location.RED_CREEK, "Wagens laden", 10, 2, 20, 120, 15),
            new Job("stable", Location.RED_CREEK, "Stallen schoonmaken", 8, 1, 14, 90, 10),
            new Job("supplies", Location.RED_CREEK, "Voorraden sorteren", 7, 1, 10, 60, 8)),
        Location.PINE_FOREST, List.of(
            new Job("wood", Location.PINE_FOREST, "Hout hakken", 15, 2, 25, 180, 20),
            new Job("branches", Location.PINE_FOREST, "Takken verzamelen", 9, 1, 12, 90, 10),
            new Job("trail", Location.PINE_FOREST, "Bospad vrijmaken", 13, 2, 20, 120, 17)),
        Location.OLD_MINE, List.of(
            new Job("ore", Location.OLD_MINE, "Erts delven", 20, 3, 30, 240, 25),
            new Job("stones", Location.OLD_MINE, "Erts sorteren", 12, 2, 16, 120, 14),
            new Job("carts", Location.OLD_MINE, "Mijnkarren vullen", 17, 2, 24, 180, 21)),
        Location.LONELY_RANCH, List.of(
            new Job("fences", Location.LONELY_RANCH, "Hekken repareren", 12, 2, 22, 150, 18),
            new Job("hay", Location.LONELY_RANCH, "Hooi stapelen", 11, 1, 18, 120, 14),
            new Job("well", Location.LONELY_RANCH, "Water putten", 8, 1, 12, 60, 9)));
    public JobManager(RandomGenerator random) { this.random = random; }
    public List<Job> at(Location location) { return JOBS.get(location); }
    public boolean contains(Job job) { return job != null && at(job.location()).contains(job); }
    public int payment(Job job) {
        return job.basePay() + random.nextInt(-job.payVariation(), job.payVariation() + 1);
    }
}

package com.frontier.logic;

import com.frontier.model.*;
import java.util.List;
import java.util.Objects;
import java.util.random.RandomGenerator;

/** Spelacties staan los van de interface en de echte klok. */
public final class GameSession {
    private GameState state = new GameState();
    private final JobManager jobs;
    private final LootManager loot;
    public GameSession() { this(RandomGenerator.getDefault()); }
    public GameSession(RandomGenerator random) {
        jobs = new JobManager(random); loot = new LootManager(random);
    }
    public GameState state() { return state; }
    public List<Job> jobsAt(Location location) { return jobs.at(location); }
    public Job currentJob() { return jobsAt(state.location()).getFirst(); }
    public void newGame() { state = new GameState(); }
    public void load(GameState saved) { state = Objects.requireNonNull(saved); }
    public String travelBlockReason(Location target) {
        if (target == state.location()) return "Je bent al op deze locatie.";
        int trip = state.location().travelStaminaTo(target);
        int reserve = target.travelStaminaTo(Location.RED_CREEK);
        if (state.player().stamina() < trip + reserve)
            return "Te weinig energie voor de reis en de terugweg naar Red Creek.";
        return "";
    }
    public String travel(Location target) {
        String reason = travelBlockReason(target);
        if (!reason.isEmpty()) return reason;
        int minutes = state.location().travelMinutesTo(target);
        state.player().spendStamina(state.location().travelStaminaTo(target));
        state.time().advanceMinutes(minutes); state.moveTo(target);
        return "Aangekomen bij " + target.displayName() + ". De reis duurde " + minutes + " minuten.";
    }
    public String workBlockReason() { return workBlockReason(currentJob()); }
    public String workBlockReason(Job job) {
        if (!jobs.contains(job)) return "Deze werkzaamheid is niet beschikbaar.";
        int trip = state.location().travelStaminaTo(job.location());
        int reserve = job.location().travelStaminaTo(Location.RED_CREEK);
        if (state.player().stamina() < trip + job.staminaCost() + reserve)
            return "Te weinig energie. Reis terug naar Red Creek en slaap in de herberg.";
        return "";
    }
    public String work() { return work(currentJob()); }
    public String work(Job job) {
        String reason = workBlockReason(job);
        if (!reason.isEmpty()) return reason;
        // Controleer de volledige reis, klus en terugweg voordat iets verandert.
        int tripCost = state.location().travelStaminaTo(job.location());
        int tripMinutes = state.location().travelMinutesTo(job.location());
        int pay = jobs.payment(job);
        int previousLevel = state.player().level();
        state.player().reward(pay, job.xp());
        state.player().spendStamina(tripCost + job.staminaCost());
        state.time().advanceMinutes(tripMinutes + job.minutes());
        state.moveTo(job.location());
        String found = loot.find(job.location()).map(item -> {
            state.inventory().add(item);
            return " Gevonden: " + item.displayName() + "!";
        }).orElse("");
        return job.name() + ": $" + pay + " en " + job.xp() + " ervaring verdiend."
            + (state.player().level() > previousLevel ? " Nieuw niveau: " + state.player().level() + "!" : "") + found;
    }
    public String sleep() {
        if (state.location() != Location.RED_CREEK) return "Je kunt alleen in de herberg van Red Creek slapen.";
        state.player().rest(); state.time().advanceMinutes(8 * 60);
        return "Acht uur geslapen. Je energie is weer 100.";
    }
}

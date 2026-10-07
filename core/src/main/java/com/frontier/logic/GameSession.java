package com.frontier.logic;

import com.frontier.model.*;
import java.time.Clock;
import java.util.List;
import java.util.Objects;
import java.util.random.RandomGenerator;

/** Spelacties staan los van de interface; de echte werktimer gebruikt een vervangbare klok. */
public final class GameSession {
    private GameState state = new GameState();
    private final JobManager jobs;
    private final LootManager loot;
    private final Clock clock;
    public GameSession() { this(RandomGenerator.getDefault()); }
    public GameSession(RandomGenerator random) { this(random, Clock.systemUTC()); }
    public GameSession(RandomGenerator random, Clock clock) {
        this.clock = Objects.requireNonNull(clock);
        jobs = new JobManager(random); loot = new LootManager(random);
    }
    public GameState state() { return state; }
    public List<Job> jobsAt(Location location) { return jobs.at(location); }
    public Job currentJob() { return jobsAt(state.location()).isEmpty() ? null : jobsAt(state.location()).getFirst(); }
    public void newGame() { state = new GameState(); }
    public void load(GameState saved) {
        Objects.requireNonNull(saved);
        if (saved.activeWork() != null && !jobs.contains(saved.activeWork().job()))
            throw new IllegalArgumentException("Onbekende lopende klus.");
        state = saved;
    }
    public boolean isWorking() { return state.activeWork() != null; }
    public float workProgress() { return isWorking() ? state.activeWork().progress(clock.millis()) : 0; }
    public long workSecondsRemaining() { return isWorking() ? (state.activeWork().remainingMillis(clock.millis()) + 999) / 1000 : 0; }
    public String startWork(Job job, WorkDuration duration) {
        String reason = workBlockReason(job, duration);
        if (!reason.isEmpty()) return reason;
        long now = clock.millis();
        ActiveWork active = new ActiveWork(job, duration, now, Math.addExact(now, duration.seconds() * 1000L));
        int tripMinutes = state.location().travelMinutesTo(job.location());
        state.player().spendStamina(state.location().travelStaminaTo(job.location()) + job.staminaCost(duration));
        state.time().advanceMinutes(tripMinutes); state.moveTo(job.location()); state.setActiveWork(active);
        return job.name() + " gestart. Werktijd: " + duration.displayName() + ".";
    }
    /** Geeft eenmaal een resultaat terug, ook als een opgeslagen timer al verstreken is. */
    public String updateWork() {
        ActiveWork active = state.activeWork();
        if (active == null || active.remainingMillis(clock.millis()) > 0) return "";
        state.setActiveWork(null);
        return finishWork(active.job(), active.duration());
    }
    public String travelBlockReason(Location target) {
        if (isWorking()) return "Je bent aan het werk. Wacht tot je klus klaar is.";
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
    public String workBlockReason(Job job) { return workBlockReason(job, WorkDuration.LONG); }
    public String workBlockReason(Job job, WorkDuration duration) {
        Objects.requireNonNull(duration);
        if (isWorking()) return "Er loopt al een klus. Wacht tot deze klaar is.";
        if (!jobs.contains(job)) return "Deze werkzaamheid is niet beschikbaar.";
        int trip = state.location().travelStaminaTo(job.location());
        int reserve = job.location().travelStaminaTo(Location.RED_CREEK);
        if (state.player().stamina() < trip + job.staminaCost(duration) + reserve)
            return "Te weinig energie. Reis terug naar Red Creek en slaap in de herberg.";
        return "";
    }
    public String work() { return work(currentJob()); }
    public String work(Job job) { return work(job, WorkDuration.LONG); }
    public String work(Job job, WorkDuration duration) {
        String reason = workBlockReason(job, duration);
        if (!reason.isEmpty()) return reason;
        // Controleer de volledige reis, klus en terugweg voordat iets verandert.
        int tripCost = state.location().travelStaminaTo(job.location());
        int tripMinutes = state.location().travelMinutesTo(job.location());
        state.player().spendStamina(tripCost + job.staminaCost(duration));
        state.time().advanceMinutes(tripMinutes); state.moveTo(job.location());
        return finishWork(job, duration);
    }
    private String finishWork(Job job, WorkDuration duration) {
        int pay = jobs.payment(job, duration);
        int previousLevel = state.player().level();
        state.player().reward(pay, job.xp(duration));
        state.time().advanceSeconds(duration.seconds());
        Item item = loot.find(job.location(), duration).orElse(null);
        if (item != null) state.inventory().add(item);
        state.mailbox().add(new Telegram(job.name(), job.location(), duration.seconds(), pay, job.xp(duration),
            item, state.time().value(), false));
        String found = item == null ? "" : " Gevonden: " + item.displayName() + "!";
        return job.name() + ": $" + pay + " en " + job.xp(duration) + " ervaring verdiend."
            + (state.player().level() > previousLevel ? " Nieuw niveau: " + state.player().level() + "!" : "") + found;
    }
    public String sleep() {
        if (isWorking()) return "Je bent aan het werk. Slapen kan na je klus.";
        if (state.location() != Location.RED_CREEK) return "Je kunt alleen in de herberg van Red Creek slapen.";
        state.player().rest(); state.time().advanceMinutes(TimeRules.SLEEP_HOURS * 60);
        return TimeRules.SLEEP_HOURS + " uur geslapen. Je energie is weer 100.";
    }
}

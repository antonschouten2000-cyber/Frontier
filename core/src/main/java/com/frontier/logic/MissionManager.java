package com.frontier.logic;

import com.frontier.model.*;
import java.util.function.Supplier;
import java.util.function.BooleanSupplier;

public final class MissionManager {
    private final Supplier<GameState> state;
    private final BooleanSupplier busy;
    private final java.util.function.Consumer<Mission> accepted, claimed;
    public MissionManager(Supplier<GameState> state, BooleanSupplier busy, java.util.function.Consumer<Mission> accepted, java.util.function.Consumer<Mission> claimed) { this.state=state; this.busy=busy; this.accepted=accepted; this.claimed=claimed; }
    public int progress(Mission mission) {
        GameState s=state.get(); MissionJournal.Entry entry=s.missions().entry(mission);
        if (entry==null) return 0;
        return mission.material()==null || entry.claimed() ? entry.progress() : Math.min(mission.target(),s.inventory().count(mission.material()));
    }
    public String blockReason() {
        if (busy.getAsBoolean()) return "Kom na je klus terug naar de Saloon.";
        return state.get().location()!=Location.RED_CREEK ? "Reis eerst naar de Saloon in Red Creek." : "";
    }
    public String accept(Mission mission) {
        String reason=blockReason(); if(!reason.isEmpty()) return reason;
        if(state.get().missions().entry(mission)!=null) return "Deze opdracht is al aangenomen.";
        state.get().missions().accept(mission); accepted.accept(mission); return "Opdracht aangenomen: "+mission.title()+".";
    }
    public String claim(Mission mission) {
        String reason=blockReason(); if(!reason.isEmpty()) return reason;
        GameState s=state.get(); MissionJournal.Entry entry=s.missions().entry(mission);
        if(entry==null) return "Neem de opdracht eerst aan.";
        if(entry.claimed()) return "Je hebt deze beloning al ontvangen.";
        if(progress(mission)<mission.target()) return "De opdracht is nog niet klaar.";
        // Controleer aantallen voordat geld, materialen of voortgang veranderen.
        Math.addExact(s.inventory().count(mission.rewardItem()),1);
        int bonus=s.player().reward(mission.money(),mission.xp());
        if(mission.material()!=null) s.inventory().remove(mission.material(),mission.target());
        s.inventory().add(mission.rewardItem()); s.missions().claim(mission); claimed.accept(mission);
        s.mailbox().add(new Telegram(mission.giver()+": "+mission.title(),Location.RED_CREEK,0,mission.money()+bonus,mission.xp(),mission.rewardItem(),s.time().value(),false,null,0,bonus,true));
        return mission.giver()+": opdracht voltooid! $"+mission.money()+", "+mission.xp()+" ervaring en "+mission.rewardItem().displayName()+" ontvangen.";
    }
}

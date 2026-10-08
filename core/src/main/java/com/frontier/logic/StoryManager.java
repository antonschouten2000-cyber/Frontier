package com.frontier.logic;

import com.frontier.model.*;
import java.util.function.*;
import java.util.random.RandomGenerator;

public final class StoryManager {
    private final Supplier<GameState> state;
    private final BooleanSupplier busy;
    private final RandomGenerator random;
    public StoryManager(Supplier<GameState> state,BooleanSupplier busy,RandomGenerator random){this.state=state;this.busy=busy;this.random=random;}
    public String selectReason(VillageProject project){
        GameState s=state.get();
        if(busy.getAsBoolean())return "Rond eerst je klus af.";
        if(s.location()!=Location.RED_CREEK)return "Bespreek dit project in de Saloon van Red Creek.";
        if(s.journal().done(project))return "Dit project is voltooid.";
        if(s.journal().active()!=null)return "Maak eerst je gekozen project af.";
        if(project==VillageProject.BRIDGE && s.journal().completed().isEmpty())return "Help eerst Martha of Elias; daarna pakken we samen de brug aan.";
        return "";
    }
    public String select(VillageProject project){
        String reason=selectReason(project);if(!reason.isEmpty())return reason;
        GameState s=state.get();s.journal().select(project);
        s.journal().add(s.time().value(),"Een belofte aan "+project.giver(),project.story()+" Ik heb beloofd dit eerst aan te pakken.",sketch(project));
        return "Project gekozen: "+project.title()+". Verzamel de materialen en kom terug.";
    }
    public String finishReason(){
        GameState s=state.get();VillageProject p=s.journal().active();
        if(busy.getAsBoolean())return "Rond eerst je klus af.";
        if(s.location()!=Location.RED_CREEK)return "Kom terug naar Red Creek om de bouw af te ronden.";
        if(p==null)return "Kies eerst een dorpsproject.";
        if(s.player().money()<p.money()||s.inventory().count(Item.WOOD)<p.wood()||s.inventory().count(Item.STONE)<p.stone())return "Je hebt nog niet genoeg cash, hout en steen.";
        return "";
    }
    public String finish(){
        String reason=finishReason();if(!reason.isEmpty())return reason;
        GameState s=state.get();VillageProject p=s.journal().active();
        s.player().spendMoney(p.money());s.inventory().remove(Item.WOOD,p.wood());s.inventory().remove(Item.STONE,p.stone());s.journal().finish();
        s.time().advanceMinutes(120);
        String text=switch(p){
            case VERANDA -> "Martha zet twee stoelen op de nieuwe veranda. Voor het eerst hoor ik gelach voor de herberg. 'Er is hier altijd een plek voor je,' zegt ze.";
            case WELL -> "Elias trekt de eerste volle emmer omhoog. Zijn kinderen rennen naar de akkers. 'Volgende zomer blijven we,' zegt hij. Het klinkt alsof hij het zelf pas nu gelooft.";
            case BRIDGE -> "We leggen de laatste plank over de kreek. Clara kijkt naar de andere oever: 'Misschien begon Red Creek daar. Ga eens kijken.' De Vergeten halte is weer bereikbaar.";
        };
        s.journal().add(s.time().value(),p.title(),text,sketch(p));return text+" "+p.benefit();
    }
    public int sleepMinutes(){GameState s=state.get();int raw=s.town().sleepMinutes();return s.journal().done(VillageProject.VERANDA)?(raw*80+99)/100:raw;}
    public int workCost(Job job,WorkDuration duration,int raw){return state.get().journal().done(VillageProject.WELL)&&job.location()==Location.WILLOW_FARM?(raw*85+99)/100:raw;}
    public String accessReason(Location location){return location==Location.FORGOTTEN_STOP && !state.get().journal().done(VillageProject.BRIDGE)?"De voetbrug is ingestort. Help eerst een inwoner en herstel daarna de brug via Dorpsprojecten in de Saloon.":"";}
    public void arrived(Location location){
        GameState s=state.get();
        if(location==Location.FORGOTTEN_STOP && s.journal().pages().stream().noneMatch(p->p.title().equals("Aan de andere oever")))
            s.journal().add(s.time().value(),"Aan de andere oever","De halte is verlaten, maar tussen het gras ligt nog een spoor van het vroegere Red Creek. Ik wil weten wie hier woonde.","bridge");
    }
    /** Een ontdekking per soort; korte klussen hebben een kleine kans, een uur meer. */
    public Discovery completedWork(Job job,WorkDuration duration){
        Discovery discovery=switch(job.location()){
            case PINE_FOREST,NORTH_WOODS -> Discovery.OLD_MAP;
            case OLD_MINE,QUARRY,LONELY_RANCH -> Discovery.LETTER;
            case FORGOTTEN_STOP -> Discovery.CELLAR;
            default -> null;
        };
        GameState s=state.get();
        if(discovery==null||s.journal().found(discovery))return null;
        double chance=switch(duration){case QUICK -> .01;case SHORT -> .12;case LONG -> .45;};
        if(random.nextDouble()>=chance)return null;
        s.journal().discover(discovery);s.journal().add(s.time().value(),discovery.title(),discovery.text(),discovery.sketch());return discovery;
    }
    public void missionAccepted(Mission mission){GameState s=state.get();s.journal().add(s.time().value(),"Aan tafel bij "+mission.giver(),mission.description()+" Een kleine klus, maar hier kent iedereen elkaars zorgen.","letter");}
    public void missionClaimed(Mission mission){GameState s=state.get();s.journal().add(s.time().value(),"Een bedankje van "+mission.giver(),"Ik heb '"+mission.title()+"' afgerond. "+mission.giver()+" bedankte me met $"+mission.money()+" en "+mission.rewardItem().displayName()+". Langzaam word ik hier meer dan een voorbijganger.","home");}
    private static String sketch(VillageProject p){return switch(p){case VERANDA -> "home";case WELL -> "well";case BRIDGE -> "bridge";};}
}

package com.frontier.model;

import java.time.LocalDateTime;
import java.util.*;

/** Verhaalfeiten en gedateerde dagboekbladzijden blijven naast zakelijke telegrammen bestaan. */
public final class TravelJournal {
    public static final int MAX_ENTRIES=100;
    public record Page(LocalDateTime date,String title,String text,String sketch) {
        public Page {
            new GameTime(date);
            if(title==null||title.isBlank()||title.length()>140||text==null||text.isBlank()||text.length()>2000
                || !Set.of("map","letter","cellar","home","well","bridge","boots").contains(sketch))
                throw new IllegalArgumentException("Ongeldige dagboekbladzijde.");
        }
    }
    private VillageProject first, active;
    private final EnumSet<VillageProject> completed=EnumSet.noneOf(VillageProject.class);
    private final EnumSet<Discovery> discoveries=EnumSet.noneOf(Discovery.class);
    private final List<Page> pages=new ArrayList<>();
    public TravelJournal(){add(GameTime.START,"Twintig dollar en een nieuw begin","Ik kwam op 1 april in Red Creek aan met twintig dollar op zak. De herbergdeur klemt en de put bij de Wilgenhoeve staat laag. Misschien kan ik hier meer achterlaten dan voetsporen.","boots");}
    public VillageProject first(){return first;} public VillageProject active(){return active;}
    public Set<VillageProject> completed(){return Set.copyOf(completed);} public boolean done(VillageProject p){return completed.contains(p);}
    public Set<Discovery> discoveries(){return Set.copyOf(discoveries);} public boolean found(Discovery d){return discoveries.contains(d);}
    public List<Page> pages(){return List.copyOf(pages);}
    public void select(VillageProject project){
        if(done(project)||active!=null || project==VillageProject.BRIDGE && completed.isEmpty()) throw new IllegalArgumentException("Project niet beschikbaar.");
        if(first==null){if(project==VillageProject.BRIDGE)throw new IllegalArgumentException("Begin bij Martha of Elias."); first=project;}
        active=project;
    }
    public void finish(){if(active==null)throw new IllegalStateException("Geen dorpsproject."); completed.add(active);active=null;}
    public void discover(Discovery discovery){discoveries.add(discovery);}
    public void add(LocalDateTime date,String title,String text,String sketch){Page page=new Page(date,title,text,sketch);if(pages.size()==MAX_ENTRIES)pages.removeFirst();pages.add(page);}
    public void restore(VillageProject first,VillageProject active,Set<VillageProject> done,Set<Discovery> found,List<Page> savedPages){
        if(savedPages.isEmpty()||savedPages.size()>MAX_ENTRIES || first==VillageProject.BRIDGE
            || first==null && (active!=null||!done.isEmpty()) || active!=null && done.contains(active)
            || active==VillageProject.BRIDGE && done.isEmpty() || done.contains(VillageProject.BRIDGE) && !done.contains(VillageProject.VERANDA) && !done.contains(VillageProject.WELL)
            || first!=null && !done.contains(first) && active!=first || found.contains(Discovery.CELLAR) && !done.contains(VillageProject.BRIDGE))
            throw new IllegalArgumentException("Ongeldige verhaalvoortgang.");
        this.first=first;this.active=active;completed.clear();completed.addAll(done);discoveries.clear();discoveries.addAll(found);pages.clear();pages.addAll(savedPages);
    }
}

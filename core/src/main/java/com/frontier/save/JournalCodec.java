package com.frontier.save;

import com.badlogic.gdx.utils.JsonValue;
import com.frontier.model.*;
import java.time.LocalDateTime;
import java.util.*;

final class JournalCodec {
    private JournalCodec(){}
    public static final class Data {
        public String first, active;
        public String[] completed, discoveries;
        public PageData[] pages;
    }
    public static final class PageData {public String date,title,text,sketch;}
    static Data write(TravelJournal journal){
        Data data=new Data();data.first=journal.first()==null?null:journal.first().name();data.active=journal.active()==null?null:journal.active().name();
        data.completed=journal.completed().stream().map(Enum::name).sorted().toArray(String[]::new);
        data.discoveries=journal.discoveries().stream().map(Enum::name).sorted().toArray(String[]::new);
        data.pages=journal.pages().stream().map(page->{PageData p=new PageData();p.date=page.date().toString();p.title=page.title();p.text=page.text();p.sketch=page.sketch();return p;}).toArray(PageData[]::new);return data;
    }
    static void read(JsonValue root,TravelJournal journal){
        JsonValue data=root.get("journal");if(data==null||!data.isObject())throw new IllegalArgumentException("Reisdagboek ontbreekt.");
        VillageProject first=project(data,"first"),active=project(data,"active");
        Set<VillageProject> done=values(data,"completed",VillageProject.class);
        Set<Discovery> found=values(data,"discoveries",Discovery.class);
        JsonValue pages=data.get("pages");if(pages==null||!pages.isArray()||pages.size<1||pages.size>TravelJournal.MAX_ENTRIES)throw new IllegalArgumentException("Ongeldig reisdagboek.");
        List<TravelJournal.Page> saved=new ArrayList<>();
        for(JsonValue p:pages){
            if(!p.isObject())throw new IllegalArgumentException("Ongeldige bladzijde.");
            saved.add(new TravelJournal.Page(LocalDateTime.parse(text(p,"date")),text(p,"title"),text(p,"text"),text(p,"sketch")));
        }
        journal.restore(first,active,done,found,saved);
    }
    private static String text(JsonValue obj,String key){JsonValue v=obj.get(key);if(v==null||!v.isString())throw new IllegalArgumentException("Ongeldige dagboektekst.");return v.asString();}
    private static VillageProject project(JsonValue data,String key){JsonValue v=data.get(key);if(v==null)throw new IllegalArgumentException("Ontbrekend project.");return v.isNull()?null:VillageProject.valueOf(text(data,key));}
    private static <E extends Enum<E>> Set<E> values(JsonValue data,String key,Class<E> type){
        JsonValue list=data.get(key);if(list==null||!list.isArray())throw new IllegalArgumentException("Ongeldige verhaalfeiten.");
        Set<E> values=EnumSet.noneOf(type);for(JsonValue v:list){if(!v.isString()||!values.add(Enum.valueOf(type,v.asString())))throw new IllegalArgumentException("Ongeldig of dubbel verhaalfeit.");}return values;
    }
}

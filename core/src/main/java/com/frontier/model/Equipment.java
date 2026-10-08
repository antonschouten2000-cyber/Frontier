package com.frontier.model;

import java.util.EnumMap;
import java.util.Map;

/** Uitgeruste kleding blijft in de inventaris; één voorwerp per lichaamsdeel. */
public final class Equipment {
    public enum Slot { HEAD, BODY, FEET }
    private final EnumMap<Slot, Item> items=new EnumMap<>(Slot.class);
    public static Slot slot(Item item) {
        return switch(item) {case HAT -> Slot.HEAD; case BOOTS -> Slot.FEET; case WORK_SHIRT, COAT, DUSTER -> Slot.BODY; default -> null;};
    }
    public Map<Slot, Item> items(){return Map.copyOf(items);}
    public boolean wearing(Item item){return items.containsValue(item);}
    public void equip(Item item, Inventory inventory){
        Slot slot=slot(item); if(slot==null || inventory.count(item)==0) throw new IllegalArgumentException("Je bezit deze kleding niet.");
        items.put(slot,item);
    }
    public void remove(Item item){if(wearing(item)) items.remove(slot(item));}
    public int travelCost(int raw){return wearing(Item.BOOTS)?reduced(raw,10):raw;}
    public int workCost(Job job, WorkDuration duration){
        int bonus=wearing(Item.DUSTER)?12:0;
        if(wearing(Item.WORK_SHIRT) && switch(job.location()){case WILLOW_FARM,SUNRISE_FARM,RIVER_FARM,COTTON_FARM,LONELY_RANCH -> true;default -> false;}) bonus+=10;
        if(wearing(Item.COAT) && (job.location()==Location.OLD_MINE || job.location()==Location.QUARRY)) bonus+=10;
        if(wearing(Item.HAT) && (job.location()==Location.PINE_FOREST || job.location()==Location.NORTH_WOODS)) bonus+=5;
        return reduced(job.staminaCost(duration),bonus);
    }
    private static int reduced(int cost,int percent){return (cost*(100-percent)+99)/100;}
    public static String bonus(Item item){return switch(item){
        case BOOTS -> "10% minder reisenergie."; case WORK_SHIRT -> "10% minder energie bij boerderij- en ranchwerk.";
        case COAT -> "10% minder energie in de mijn en steengroeve."; case DUSTER -> "12% minder energie bij alle werkzaamheden.";
        case HAT -> "5% minder energie bij boswerk."; default -> "";};}
}

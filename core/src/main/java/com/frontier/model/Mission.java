package com.frontier.model;

/** Kleine vaste opdrachten, onafhankelijk van de interface. */
public enum Mission {
    INN_WOOD("Hout voor de herberg", "Herbergier Martha", "Lever 10 brandhout voor de herberg.", Item.WOOD, 10, null, 25, 30, Item.HAT),
    FARM_HELP("Help de boerderij", "Boer Elias", "Rond na het aannemen 3 klussen af bij Willow Farm. Elke werktijd telt.", null, 3, Location.WILLOW_FARM, 30, 40, Item.WORK_SHIRT),
    TOWN_STONE("Steen voor Red Creek", "Burgemeester Clara", "Lever 10 bouwsteen voor de dorpsstraat.", Item.STONE, 10, null, 35, 50, Item.BOOTS);
    private final String title, giver, description;
    private final Item material, rewardItem;
    private final int target, money, xp;
    private final Location location;
    Mission(String title, String giver, String description, Item material, int target, Location location, int money, int xp, Item rewardItem) {
        this.title=title; this.giver=giver; this.description=description; this.material=material;
        this.target=target; this.location=location; this.money=money; this.xp=xp; this.rewardItem=rewardItem;
    }
    public String title(){return title;} public String giver(){return giver;} public String description(){return description;}
    public Item material(){return material;} public int target(){return target;} public Location location(){return location;}
    public int money(){return money;} public int xp(){return xp;} public Item rewardItem(){return rewardItem;}
}

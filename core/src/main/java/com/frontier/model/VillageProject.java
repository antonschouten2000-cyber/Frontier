package com.frontier.model;

/** Een eerste klein hoofdstuk: gedeelde materialen, verschillende voordelen. */
public enum VillageProject {
    VERANDA("Een deur die weer opengaat", "Martha", "Mijn herberg was ooit het hart van Red Creek. Met een nieuwe veranda durven reizigers hier weer te stoppen.", 12, 4, 15,
        "Een veranda met zitbanken verschijnt bij de herberg. Slapen kost 20% minder speltijd."),
    WELL("Water voor de wilgen", "Elias", "De put valt bijna droog. Mijn gezin kan de hoeve niet nog een droge zomer volhouden. Help ons het water terug te brengen.", 4, 12, 15,
        "De Wilgenhoeve krijgt een werkende waterput en groene akkers. Werk daar kost 15% minder energie."),
    BRIDGE("De andere oever", "Clara", "Nu er weer hoop is, kunnen we de oude voetbrug herstellen. Aan de overkant ligt de vergeten halte van Red Creek.", 16, 10, 25,
        "De voetbrug verschijnt op de kaart. De Vergeten halte wordt bereikbaar voor reizen en werkzaamheden.");
    private final String title, giver, story, benefit;
    private final int wood, stone, money;
    VillageProject(String title,String giver,String story,int wood,int stone,int money,String benefit){
        this.title=title;this.giver=giver;this.story=story;this.wood=wood;this.stone=stone;this.money=money;this.benefit=benefit;
    }
    public String title(){return title;} public String giver(){return giver;} public String story(){return story;}
    public String benefit(){return benefit;} public int wood(){return wood;} public int stone(){return stone;} public int money(){return money;}
}

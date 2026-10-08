package com.frontier.model;

/** Gebouwen en hun eerste uitbreidingsprijs; verdere kosten groeien kwadratisch. */
public enum Building {
    GUNSMITH("Geweermaker", "Een hoger level ontsluit betere wapens in de winkel.", 30),
    TOWN_HALL("Stadhuis", "Het stadhuis bepaalt het maximale level van de andere gebouwen.", 25),
    TAILOR("Kledingmaker", "Een hoger level ontsluit betere kleding in de winkel.", 20),
    INN("Herberg", "Een hoger level herstelt je energie in minder speluren.", 25),
    BANK("Bank", "Stort je cash op je rekening. Een hoger level verhoogt de rekeninglimiet.", 20),
    SALOON("Saloon", "Ontmoet opdrachtgevers en verdien beloningen met eenmalige opdrachten.", 20);
    public static final int MAX_LEVEL = 5;
    private final String name, description;
    private final int upgradePrice;
    Building(String name, String description, int upgradePrice) { this.name = name; this.description = description; this.upgradePrice = upgradePrice; }
    public String displayName() { return name; }
    public String description() { return description; }
    public int upgradePrice() { return upgradePrice; }
}

package com.frontier.model;

public enum Item {
    SLINGSHOT("Katapult", Category.WEAPON, "Een eenvoudige houten katapult. Wapenkwaliteit 1."),
    REPEATER("Repeatergeweer", Category.WEAPON, "Een zorgvuldig gemaakt repeatergeweer. Wapenkwaliteit 12."),
    WORK_SHIRT("Werkhemd", Category.CLOTHING, "Een eenvoudig katoenen werkhemd. Kledingkwaliteit 1."),
    DUSTER("Lange stofjas", Category.CLOTHING, "Een stevige lange jas van de beste stof. Kledingkwaliteit 8."),
    STONE("Bouwsteen", Category.PRODUCT, "Steen uit de steengroeve, nodig voor gebouwupgrades."),
    COTTON("Katoen", Category.PRODUCT, "Vezels van de Katoenhoeve, nodig voor de kledingmaker."),
    REVOLVER("Oude revolver", Category.WEAPON, "Een verweerde zesschieter met een houten greep."),
    RIFLE("Jachtgeweer", Category.WEAPON, "Een oud geweer, gevonden tussen achtergelaten spullen."),
    KNIFE("Zakmes", Category.WEAPON, "Een stevig mes met een afgesleten heft."),
    WOOD("Brandhout", Category.PRODUCT, "Hout uit het bos, nodig voor gebouwupgrades."),
    ORE("IJzererts", Category.PRODUCT, "IJzererts uit de mijn, nodig voor upgrades van de geweermaker."),
    COFFEE("Koffiebonen", Category.PRODUCT, "Een klein zakje geurige koffiebonen."),
    HAT("Vilten hoed", Category.CLOTHING, "Een stoffige hoed met een brede rand."),
    BOOTS("Leren laarzen", Category.CLOTHING, "Een gedragen, maar degelijk paar laarzen."),
    COAT("Wollen jas", Category.CLOTHING, "Een warme jas voor koude nachten.");

    public enum Category {
        WEAPON("Wapens"), PRODUCT("Producten"), CLOTHING("Kleding");
        private final String name;
        Category(String name) { this.name = name; }
        public String displayName() { return name; }
    }
    private final String name, description;
    private final Category category;
    Item(String name, Category category, String description) {
        this.name = name; this.category = category; this.description = description;
    }
    public String displayName() { return name; }
    public Category category() { return category; }
    public String description() { return description; }
}

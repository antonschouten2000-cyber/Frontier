package com.frontier.model;

/** Toekomstige bezittingen zijn kaartpunten, geen reis- of werklocaties. */
public enum Landmark {
    DRY_WELL("Droge Bron", "Spookdorp", 300, 1650,
        "Een verlaten dorp rond een opgedroogde waterput. Lege huizen en verweerde hekken wachten op een nieuwe eigenaar."),
    SAND_VALLEY("Zandvallei", "Spookdorp", 2850, 1560,
        "Een verlaten nederzetting in de zandheuvels. Van de oude handelsstraat zijn alleen houten gevels over."),
    LOST_CREEK("Verloren Kreek", "Spookdorp", 350, 300,
        "Een stil dorp aan een oude kreek. De bewoners zijn vertrokken; het erf en de gebouwen staan leeg."),
    SANDSTONE_FORT("Fort Zandsteen", "Fort", 2750, 380,
        "Een verlaten fort met palissades, wachttorens en een binnenplaats. Een mogelijke uitvalsbasis voor een toekomstige eigenaar.");
    private final String name, kind, description;
    private final int x, y;
    Landmark(String name, String kind, int x, int y, String description) {
        this.name = name; this.kind = kind; this.x = x; this.y = y; this.description = description;
    }
    public String displayName() { return name; }
    public String kind() { return kind; }
    public int x() { return x; }
    public int y() { return y; }
    public String description() { return description; }
    public boolean isFort() { return this == SANDSTONE_FORT; }
}

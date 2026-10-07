package com.frontier.ui;

enum TownBuilding {
    GUNSMITH("Geweermaker", "In deze werkplaats worden geweren en revolvers vervaardigd en onderhouden."),
    TOWN_HALL("Stadhuis", "Het bestuurlijke hart van Red Creek, waar de geschiedenis van de stad wordt bijgehouden."),
    TAILOR("Kledingmaker", "Hier worden stevige jassen, hoeden en werkkleding gemaakt."),
    INN("Herberg", "Een bed voor de nacht. Acht uur slapen is gratis en herstelt je energie volledig."),
    BANK("Bank", "Achter deze stevige muren bewaart de bank de kostbaarheden van het dorp.");
    final String name, description;
    TownBuilding(String name, String description) { this.name = name; this.description = description; }
}

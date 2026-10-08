package com.frontier.model;

public enum Discovery {
    OLD_MAP("Een kaart zonder naam", "Tussen twee boomwortels vond ik een opgevouwen kaart. Een potloodlijn volgt de kreek naar de oude voetbrug. Aan de overkant staat een halte getekend.", "map"),
    LETTER("Een brief aan huis", "Onder een losliggende steen lag een brief: 'Als de trein niet meer komt, volg dan het water. Er is altijd iemand die een deur voor je opent.' Ik bewaar hem tussen mijn bladzijden.", "letter"),
    CELLAR("De kelder onder de halte", "Achter de overwoekerde halte vond ik een kelder. Op de muur staan namen van reizigers, jaartallen en één zin: 'We waren hier. Laat deze plek niet verdwijnen.'", "cellar");
    private final String title, text, sketch;
    Discovery(String title,String text,String sketch){this.title=title;this.text=text;this.sketch=sketch;}
    public String title(){return title;} public String text(){return text;} public String sketch(){return sketch;}
}

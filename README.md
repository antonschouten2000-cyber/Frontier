# Frontier

Een klein westernspel voor één speler, rond 1880. Java 21, LibGDX 1.13.1, LWJGL3 en Scene2D. De Nederlandstalige interface heeft houten panelen, knoppen en een helder meegeleverd DejaVu Sans-lettertype. Bovenaan staan aparte kaarten voor geld, niveau, ervaring en energie. Ervaring en energie worden als voortgangsbalk weergegeven; de ervaringsbalk loopt van het begin van je huidige niveau tot het volgende niveau. De streekkaart bevat bos, bergen, bruggen, dorpshuizen, een mijnschacht, een ranch, vier werkende boerderijen, een steengroeve, handelspost, rivieroever en noorderwoud. De grotere buitengebieden bevatten extra bossen, zijrivieren, kronkelende paden en kleine boerenerven. Kaart en stadsillustratie worden lokaal gegenereerd. De lettertypen en hun licentie worden meegeleverd in `core/src/main/resources/fonts`; er zijn geen accounts nodig.

## Starten

Installeer een **JDK 21** en zorg dat `JAVA_HOME` daarnaar verwijst. De Gradle Wrapper zit in het project: Gradle apart installeren hoeft niet. De eerste build downloadt de afhankelijkheden.

```sh
./gradlew :lwjgl3:run
```

Op Windows: `gradlew.bat :lwjgl3:run`. Voor macOS voegt de run-task automatisch `-XstartOnFirstThread` toe. Het venster is maximaliseerbaar. Op grotere schermen groeien de kaart, zijbalk en statuspanelen mee met de beschikbare ruimte. Een grafische desktop met OpenGL is nodig; dit is geen browsergame.

## Spelen

Je begint als **Reiziger** in **Red Creek** met $20, 100 energie, niveau 1 en 0 ervaring, op 1 april 1880 om 08:00.

- Klik op een locatie om die te bekijken. Dit kost geen tijd of energie. Kies **Reizen naar deze locatie** om een reis te bevestigen, of klik op een van de werkzaamheden.
- Houd de linkermuisknop ingedrukt en sleep om de grotere kaart te verplaatsen. Een sleepbeweging selecteert geen locatie en kost geen tijd of energie. **Kaart centreren** herstelt het overzicht.
- De wereldkaart bevat 3200 × 1920 pixels terrein, weergegeven op 75% schaal zodat je meer van de wereld tegelijk ziet. Locatieknoppen en tekst blijven op hun leesbare formaat. Rond de bestaande streek liggen de spookdorpen **Droge Bron**, **Zandvallei** en **Verloren Kreek**, plus **Fort Zandsteen**. Klik erop voor informatie. Aankopen is gereserveerd voor een volgende versie; de prijs is nog te bepalen. Deze plekken zijn nog geen reis- of werklocaties.
- De werkknoppen tonen alleen de naam. In het detailmenu kies je **15 seconden**, **10 minuten** of **1 uur**. Dit is echte wachttijd, met een voortgangsbalk en resterende tijd in de zijbalk. Opbrengst, ervaring, energie en vondstkans passen zich aan je keuze aan.
- **Aan het werk** start een lokale klus; **Reizen en werken** reist eerst en start dan de timer. De reis kost alleen in-game tijd. Energie wordt bij de start afgeschreven; geld, ervaring en eventuele voorwerpen komen pas bij afronding. Tijdens werk kun je de kaart en inventaris bekijken en opslaan, maar niet reizen, slapen of een tweede klus starten. **Terug** annuleert het keuzemenu zonder kosten.
- Elke afgeronde klus geeft geld en ervaring, met een kleine variatie in betaling. De waarden per klus zijn gebaseerd op één uur werk. Betaling en ervaring worden naar beneden afgerond met minimaal $1 en 1 ervaring; energie wordt naar boven afgerond met minimaal 1 punt.
- Als je te moe bent, reis terug naar Red Creek. Open daar **Stad openen**, kies **Herberg** en klik op **8 uur slapen**: gratis, +8 uur en energie naar 100.
- Je behoudt altijd genoeg energie om terug te keren naar Red Creek. Werk en reizen die deze reserve zouden verbruiken zijn geblokkeerd.
- De spelklok gaat alleen vooruit door reizen, afgerond werk of slapen. Tijdens het wachten op de echte werktimer blijft de spelklok staan. Een klus van 15 seconden voegt bij afronding precies 15 spel-seconden toe; de klok toont dan ook seconden.
- XP blijft cumulatief. Level 2 begint bij 50 XP, level 3 bij 150, level 4 bij 300; elk volgend level vraagt 50 XP meer dan het vorige.

| Locatie | Werkzaamheden |
| --- | --- |
| Red Creek | Geen werkzaamheden: stad met gebouwen en herberg |
| Dennenbos | Hout hakken · Takken verzamelen · Bospad vrijmaken · Boomstammen kloven · Hars verzamelen · Jonge bomen planten |
| Oude mijn | Erts delven · Erts sorteren · Mijnkarren vullen · Stutbalken plaatsen · Puin ruimen · Mijnwerktuigen poetsen |
| Verlaten ranch | Hekken repareren · Hooi stapelen · Water putten · Vee voeren · Graan oogsten · Mandwerk vlechten |
| Wilgenhoeve | Koeien melken · Eieren rapen · Stallen uitmesten |
| Zonnehoeve | Mais plukken · Appels oogsten · Zakken vullen |
| Rivierhoeve | Akkers bevloeien · Groenten sorteren · Zaaigoed verdelen |
| Katoenhoeve | Katoen plukken · Balen binden · Onkruid wieden |
| Noorderwoud | Dennenappels rapen · Zaailingen verzorgen · Hout stapelen |
| Steengroeve | Grind zeven · Leisteen sorteren · Bouwstenen markeren |
| Rivieroever | Riet snijden · Oever verstevigen · Visnetten herstellen |
| Handelspost | Pakketten sorteren · Voorraad tellen · Vracht etiketteren |

## De stad

In Red Creek verschijnt **Stad openen**. Je moet daadwerkelijk in Red Creek zijn om naar binnen te gaan. Een apart venster toont de dorpsstraat met **Geweermaker**, **Stadhuis**, **Kledingmaker**, **Herberg** en **Bank**. Klik op de gebouwen voor hun informatiekaart. Bij de herberg kun je acht uur slapen. De andere gebouwen zijn in deze versie bezoekbare informatiekaarten; handel, banktransacties en stadsbouw zijn nog niet toegevoegd. In de stad zijn geen werkzaamheden.

## Inventaris en vondsten

Een klus van één uur heeft **30% kans** op één voorwerp. Bij 10 minuten is dit **5%**, bij 15 seconden **0,125%**. Bij een vondst is 60% een product, 30% kleding en 10% een wapen. Producten passen bij de locatie: brandhout uit het bos, ijzererts uit de mijn en koffiebonen bij de ranch. Er zijn ook hoeden, laarzen, jassen, oude revolvers, jachtgeweren en zakmessen.

De knop **Inventaris** toont gestapelde aantallen en filters voor **Alles**, **Wapens**, **Producten** en **Kleding**. Klik op een voorwerp voor de beschrijving. De inventaris begint leeg en gaat mee in je save. Het bekijken van menu's kost geen tijd of energie. Voorwerpen worden voorlopig verzameld; uitrusten, verkopen en gebruiken horen niet bij deze uitbreiding.

Reizen gaat rechtstreeks tussen locaties. Afstand bepaalt de energiekosten; iedere energie-eenheid staat voor 8 minuten reistijd.

## Opslaan

**Nieuw spel**, **Spel opslaan**, **Spel laden** en **Afsluiten** staan onder **Instellingen** rechts onderaan. De inventaris blijft direct bereikbaar. Nieuw spel vraagt bevestiging. Er is één opslagplek voor handmatige opslag; Afsluiten slaat niet automatisch op. Een nieuw spel verwijdert je eerdere save niet, totdat je opnieuw opslaat.

De JSON-save staat standaard in `.frontier/save.json` onder je gebruikersmap (`user.home`). Hij bevat versie, spelernaam, geld, niveau, ervaring, energie, locatie en datum/tijd plus de volledige inventaris en eventuele lopende klus. Versie-3 saves bewaren het begin- en eindtijdstip van de echte werktimer. Na laden wordt de resterende tijd hervat; als de tijd al verstreken is, wordt de klus één keer afgerond. Sla tijdens een lange klus op als je het spel wilt afsluiten. Opslaan gebruikt een tijdelijk bestand en waar ondersteund een atomische vervanging. Oude versie-1 en versie-2 saves blijven bruikbaar; versie 1 krijgt een lege inventaris. De oorspronkelijke standaardnaam Traveler wordt Reiziger. Ontbrekende of ongeldige saves geven een melding en laten de huidige game intact.

Voor een andere opslagmap, bijvoorbeeld in de cloud:

```sh
./gradlew :lwjgl3:run -PsaveDir=/workspace/.frontier-save
```

Voor een uitgepakte distributie kan dezelfde map worden gekozen via de JVM-optie `-Dfrontier.saveDir=/pad/naar/map` in `JAVA_OPTS`.

## Tijden aanpassen in de code

- **Lopen/reizen:** `core/src/main/java/com/frontier/model/TimeRules.java`, `TRAVEL_MINUTES_PER_ENERGY` (standaard 8). `Location.travelMinutesTo()` vermenigvuldigt dit met de afstandskosten. Reizen blijft een directe actie die de spelklok vooruitzet.
- **Slapen:** dezelfde `TimeRules.java`, `SLEEP_HOURS` (standaard 8). Dit past zowel de slaapactie als de tekst op de herbergknop aan. Slapen blijft direct, zonder echte wachttimer.
- **Werkkeuzes:** `core/src/main/java/com/frontier/model/WorkDuration.java`. De tweede waarde bij `QUICK`, `SHORT` en `LONG` is het aantal seconden echte wachttijd (15, 600, 3600). Pas de bijbehorende Nederlandse tekst ook aan als je deze tijden verandert. Dezelfde duur wordt bij afronding aan de spelklok toegevoegd.
- **Werkbalans:** `core/src/main/java/com/frontier/logic/JobManager.java`. Elke `Job` bevat achtereenvolgens id, locatie, naam, basisbetaling, betalingsvariatie, energie, referentieduur in minuten (60), ervaring. Dit zijn referentiewaarden voor één uur; `Job` schaalt ze naar de gekozen duur.

## Bouwen en testen

```sh
./gradlew build
./gradlew :lwjgl3:installDist
```

De uitvoerbare distributie staat in `lwjgl3/build/install/lwjgl3/`. Start `bin/lwjgl3` of `bin/lwjgl3.bat`; een JDK/JRE 21+ blijft nodig. Op macOS voeg je bij deze launcher `-XstartOnFirstThread` toe aan `JAVA_OPTS`.

`./gradlew test` draait 106 tests voor de gameplay-loop, alle 42 werkzaamheden, energie, level-ups, klok, echte werktimers, beloningen op het eindtijdstip, hervatten na offline tijd, vondstkansen, stapeling, opslagmigratie, JSON-roundtrips en ongeldige saves. Een aparte desktoptest opent een echt venster en klikt de Scene2D-knoppen:

```sh
./gradlew :lwjgl3:desktopSmoke
```

Deze test gebruikt een tijdelijke save en verandert jouw eigen save niet. Hij controleert de uitgezoomde versleepbare kaart, de vier toekomstige aankoopplekken, statusbalken, stadsgebouwen, herberg, instellingen, alle 42 werkzaamheden, detailmenu’s, annuleren, uitputting, terugreis, slapen, vondsten, inventarisfilters, opslaan/laden, Nieuw spel, kleine en grote venstergroottes, meegroeiende kaartpanelen en Afsluiten. Screenshots staan na de test in `build/frontier-desktop.png` en `build/frontier-desktop-small.png`, `build/frontier-work.png`, `build/frontier-inventory.png`, `build/frontier-town.png` en `build/frontier-active-work.png`. Zonder desktop kan hij met Xvfb worden uitgevoerd, bijvoorbeeld `xvfb-run -a ./gradlew :lwjgl3:desktopSmoke`.

## Architectuur

- `core/model`: `Player`, `GameTime`, `Location`, `Job`, `GameState`, `Item`, `Inventory`, `WorldMap`, `Landmark`, `WorkDuration`, `TimeRules`, `ActiveWork`.
- `core/logic`: `GameSession` voert acties uit; `JobManager` bevat de 42 werkzaamheden en willekeurige betaling. `LootManager` bepaalt vondsten. Geen afhankelijkheid van UI. `GameSession` gebruikt een injecteerbare `Clock` voor de werktimers, zodat tests niet echt hoeven te wachten.
- `core/save`: `SaveManager` valideert en bewaart versie-3 JSON met inventaris en werktimer; versie-1 en versie-2 saves worden ook geladen.
- `core/ui`: `GameScreen`, `ActionBar`, `SettingsDialog`, `TownDialog`, `TownArtwork`, `TownBuilding`, `WorkDialog`, `InventoryDialog`, `MapPanel`, `FrontierSkin`, `WoodTexture`, `MapArtwork`, `WorldArtwork`, `LandmarkDialog`, `WorkProgress` verzorgen uitsluitend presentatie en bediening.
- `lwjgl3`: desktoplauncher en desktop-smoketest.

Deze versie bevat een versleepbare wereldkaart, 42 werkzaamheden met echte werktimers buiten de stad, een stadsvenster met vijf gebouwen, niveaus, slapen, vondsten, inventaris en opslag via instellingen.

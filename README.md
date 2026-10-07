# Frontier

Een klein westernspel voor één speler, rond 1880. Java 21, LibGDX 1.13.1, LWJGL3 en Scene2D. De Nederlandstalige interface heeft houten panelen, knoppen en een helder meegeleverd DejaVu Sans-lettertype. Bovenaan staan aparte kaarten voor geld, niveau, ervaring en energie. Ervaring en energie worden als voortgangsbalk weergegeven; de ervaringsbalk loopt van het begin van je huidige niveau tot het volgende niveau. De streekkaart bevat bos, bergen, bruggen, dorpshuizen, een mijnschacht en een ranch. Kaart en stadsillustratie worden lokaal gegenereerd. De lettertypen en hun licentie worden meegeleverd in `core/src/main/resources/fonts`; er zijn geen accounts nodig.

## Starten

Installeer een **JDK 21** en zorg dat `JAVA_HOME` daarnaar verwijst. De Gradle Wrapper zit in het project: Gradle apart installeren hoeft niet. De eerste build downloadt de afhankelijkheden.

```sh
./gradlew :lwjgl3:run
```

Op Windows: `gradlew.bat :lwjgl3:run`. Voor macOS voegt de run-task automatisch `-XstartOnFirstThread` toe. Een grafische desktop met OpenGL is nodig; dit is geen browsergame.

## Spelen

Je begint als **Reiziger** in **Red Creek** met $20, 100 energie, niveau 1 en 0 ervaring, op 1 april 1880 om 08:00.

- Klik op een locatie om die te bekijken. Dit kost geen tijd of energie. Kies **Reizen naar deze locatie** om een reis te bevestigen, of klik op een van de werkzaamheden.
- Houd de linkermuisknop ingedrukt en sleep om de grotere kaart te verplaatsen. Een sleepbeweging selecteert geen locatie en kost geen tijd of energie. **Kaart centreren** herstelt het overzicht.
- De werkknoppen tonen alleen de naam. Het detailmenu toont de opbrengst, ervaring, energie, werkduur en reistijd erheen. **Aan het werk** start een lokale klus; **Reizen en werken** voert de reis en klus samen uit. **Terug** annuleert zonder kosten.
- Elke afgeronde klus geeft geld en ervaring, met een kleine variatie in betaling.
- Als je te moe bent, reis terug naar Red Creek. Open daar **Stad openen**, kies **Herberg** en klik op **Acht uur slapen**: gratis, +8 uur en energie naar 100.
- Je behoudt altijd genoeg energie om terug te keren naar Red Creek. Werk en reizen die deze reserve zouden verbruiken zijn geblokkeerd.
- Tijd gaat uitsluitend vooruit door reizen, werken of slapen. Er zijn geen real-time timers.
- XP blijft cumulatief. Level 2 begint bij 50 XP, level 3 bij 150, level 4 bij 300; elk volgend level vraagt 50 XP meer dan het vorige.

| Locatie | Werkzaamheden |
| --- | --- |
| Red Creek | Geen werkzaamheden: stad met gebouwen en herberg |
| Dennenbos | Hout hakken · Takken verzamelen · Bospad vrijmaken · Boomstammen kloven · Hars verzamelen · Jonge bomen planten |
| Oude mijn | Erts delven · Erts sorteren · Mijnkarren vullen · Stutbalken plaatsen · Puin ruimen · Mijnwerktuigen poetsen |
| Verlaten ranch | Hekken repareren · Hooi stapelen · Water putten · Vee voeren · Graan oogsten · Mandwerk vlechten |

## De stad

In Red Creek verschijnt **Stad openen**. Je moet daadwerkelijk in Red Creek zijn om naar binnen te gaan. Een apart venster toont de dorpsstraat met **Geweermaker**, **Stadhuis**, **Kledingmaker**, **Herberg** en **Bank**. Klik op de gebouwen voor hun informatiekaart. Bij de herberg kun je acht uur slapen. De andere gebouwen zijn in deze versie bezoekbare informatiekaarten; handel, banktransacties en stadsbouw zijn nog niet toegevoegd. In de stad zijn geen werkzaamheden.

## Inventaris en vondsten

Elke afgeronde klus heeft **30% kans** op één voorwerp. Bij een vondst is 60% een product, 30% kleding en 10% een wapen. Producten passen bij de locatie: brandhout uit het bos, ijzererts uit de mijn en koffiebonen bij de ranch. Er zijn ook hoeden, laarzen, jassen, oude revolvers, jachtgeweren en zakmessen.

De knop **Inventaris** toont gestapelde aantallen en filters voor **Alles**, **Wapens**, **Producten** en **Kleding**. Klik op een voorwerp voor de beschrijving. De inventaris begint leeg en gaat mee in je save. Het bekijken van menu's kost geen tijd of energie. Voorwerpen worden voorlopig verzameld; uitrusten, verkopen en gebruiken horen niet bij deze uitbreiding.

Reizen gaat rechtstreeks tussen locaties. Afstand bepaalt de energiekosten; iedere energie-eenheid staat voor 8 minuten reistijd.

## Opslaan

**Nieuw spel**, **Spel opslaan**, **Spel laden** en **Afsluiten** staan onder **Instellingen** rechts onderaan. De inventaris blijft direct bereikbaar. Nieuw spel vraagt bevestiging. Er is één opslagplek voor handmatige opslag; Afsluiten slaat niet automatisch op. Een nieuw spel verwijdert je eerdere save niet, totdat je opnieuw opslaat.

De JSON-save staat standaard in `.frontier/save.json` onder je gebruikersmap (`user.home`). Hij bevat versie, spelernaam, geld, niveau, ervaring, energie, locatie en datum/tijd plus de volledige inventaris. Opslaan gebruikt een tijdelijk bestand en waar ondersteund een atomische vervanging. Oude versie-1 saves blijven bruikbaar en krijgen een lege inventaris. De oorspronkelijke standaardnaam Traveler wordt Reiziger. Ontbrekende of ongeldige saves geven een melding en laten de huidige game intact.

Voor een andere opslagmap, bijvoorbeeld in de cloud:

```sh
./gradlew :lwjgl3:run -PsaveDir=/workspace/.frontier-save
```

Voor een uitgepakte distributie kan dezelfde map worden gekozen via de JVM-optie `-Dfrontier.saveDir=/pad/naar/map` in `JAVA_OPTS`.

## Bouwen en testen

```sh
./gradlew build
./gradlew :lwjgl3:installDist
```

De uitvoerbare distributie staat in `lwjgl3/build/install/lwjgl3/`. Start `bin/lwjgl3` of `bin/lwjgl3.bat`; een JDK/JRE 21+ blijft nodig. Op macOS voeg je bij deze launcher `-XstartOnFirstThread` toe aan `JAVA_OPTS`.

`./gradlew test` draait 59 tests voor de gameplay-loop, alle achttien werkzaamheden, energie, level-ups, klok, vondstkansen, stapeling, opslagmigratie, JSON-roundtrips en ongeldige saves. Een aparte desktoptest opent een echt venster en klikt de Scene2D-knoppen:

```sh
./gradlew :lwjgl3:desktopSmoke
```

Deze test gebruikt een tijdelijke save en verandert jouw eigen save niet. Hij controleert de versleepbare kaart, statusbalken, stadsgebouwen, herberg, instellingen, alle achttien werkzaamheden, detailmenu’s, annuleren, uitputting, terugreis, slapen, vondsten, inventarisfilters, opslaan/laden, Nieuw spel, venstergrootte en Afsluiten. Screenshots staan na de test in `build/frontier-desktop.png` en `build/frontier-desktop-small.png`, `build/frontier-work.png`, `build/frontier-inventory.png` en `build/frontier-town.png`. Zonder desktop kan hij met Xvfb worden uitgevoerd, bijvoorbeeld `xvfb-run -a ./gradlew :lwjgl3:desktopSmoke`.

## Architectuur

- `core/model`: `Player`, `GameTime`, `Location`, `Job`, `GameState`, `Item`, `Inventory`.
- `core/logic`: `GameSession` voert acties uit; `JobManager` bevat de achttien werkzaamheden en willekeurige betaling. `LootManager` bepaalt vondsten. Geen afhankelijkheid van UI of systeemklok.
- `core/save`: `SaveManager` valideert en bewaart versie-2 JSON met inventaris; versie-1 saves krijgen bij laden een lege inventaris.
- `core/ui`: `GameScreen`, `ActionBar`, `SettingsDialog`, `TownDialog`, `TownArtwork`, `TownBuilding`, `WorkDialog`, `InventoryDialog`, `MapPanel`, `FrontierSkin`, `WoodTexture`, `MapArtwork` verzorgen uitsluitend presentatie en bediening.
- `lwjgl3`: desktoplauncher en desktop-smoketest.

Deze versie bevat een versleepbare wereldkaart, achttien werkzaamheden buiten de stad, een stadsvenster met vijf gebouwen, niveaus, slapen, vondsten, inventaris en opslag via instellingen.

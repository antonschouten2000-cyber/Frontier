# Frontier

Een klein westernspel voor één speler, rond 1880. Java 21, LibGDX 1.13.1, LWJGL3 en Scene2D. De Nederlandstalige interface gebruikt verweerd papier, leren labels, pentekeningen en een helder meegeleverd DejaVu Sans-lettertype. Hout hoort bij de gebouwen; menu’s lijken op een atlas en een reisdagboek. Bovenaan staan aparte kaarten voor geld, niveau, ervaring en energie. Ervaring en energie worden als voortgangsbalk weergegeven; de ervaringsbalk loopt van het begin van je huidige niveau tot het volgende niveau. De streekkaart bevat bos, bergen, bruggen, dorpshuizen, een mijnschacht, een ranch, vier werkende boerderijen, een steengroeve, handelspost, rivieroever en noorderwoud. De grotere buitengebieden bevatten extra bossen, zijrivieren, kronkelende paden en kleine boerenerven. Kaart en stadsillustratie worden lokaal gegenereerd. De lettertypen en hun licentie worden meegeleverd in `core/src/main/resources/fonts`; er zijn geen accounts nodig.

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
- Als je te moe bent, reis terug naar Red Creek. Open daar **Stad openen**, kies **Herberg** en klik op **8 uur slapen**: gratis, bij level 1 +8 uur en energie naar 100. Hogere herberglevels verkorten de slaaptijd.
- Je behoudt altijd genoeg energie om terug te keren naar Red Creek. Werk en reizen die deze reserve zouden verbruiken zijn geblokkeerd.
- De spelklok gaat vooruit door reizen, afgerond werk, slapen en het afronden van een dorpsproject. Tijdens het wachten op de echte werktimer blijft de spelklok staan. Een klus van 15 seconden voegt bij afronding precies 15 spel-seconden toe; de klok toont dan ook seconden.
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
| Steengroeve | Grind zeven · Leisteen sorteren · Steen houwen |
| Rivieroever | Riet snijden · Oever verstevigen · Visnetten herstellen |
| Handelspost | Pakketten sorteren · Voorraad tellen · Vracht etiketteren |

## De stad

In Red Creek verschijnt **Stad openen**. Je moet daadwerkelijk in Red Creek zijn om naar binnen te gaan. Een apart venster toont **Geweermaker**, **Stadhuis**, **Kledingmaker**, **Herberg** en **Bank**, elk met een eigen level. Alle gebouwen beginnen op **level 1** en kunnen naar **level 5**. Klik op een gebouw en **Gebouw upgraden** voor de kosten en vereiste materialen. Bevestigen verbruikt direct cash en materialen; annuleren kost niets. In de stad zijn geen werkzaamheden.

Het **stadhuis** bepaalt het maximale level van de andere gebouwen. Upgrade het stadhuis dus eerst. Voor een upgrade vanaf het huidige level `L` betaal je de basisprijs maal `L²`, plus `2 × L²` hout en `L²` steen. De geweermaker vraagt daarnaast `L` ijzererts; de kledingmaker `L` katoen. Basisprijzen: geweermaker $30, stadhuis $25, kledingmaker $20, herberg $25, bank $20 en Saloon $20. Upgrades, winkelen en banktransacties kosten geen tijd of energie.

| Gebouw | Functie per level |
| --- | --- |
| Stadhuis | Andere gebouwen mogen tot hetzelfde level worden uitgebreid |
| Geweermaker | Katapult → zakmes → oude revolver → jachtgeweer → repeatergeweer |
| Kledingmaker | Werkhemd → vilten hoed → leren laarzen → wollen jas → lange stofjas |
| Herberg | Volledig herstellen in 8 → 6 → 4 → 3 → 2 speluren |
| Bank | Rekeninglimiet $500 → $2.000 → $4.500 → $8.000 → $12.500 |

**Winkel openen** toont voorwerpen met iconen, prijs, kwaliteit en benodigd gebouwlevel. Oudere artikelen blijven beschikbaar na een upgrade. Aankopen gaan naar de inventaris en worden betaald met cash. De kwaliteitswaarde geeft de rang van het voorwerp aan; combat is nog niet toegevoegd.

Bij de bank opent **Rekening beheren** een venster met cash en banksaldo. Vul een bedrag in en kies **Storten** of **Opnemen**. Je kunt geen negatief bedrag storten, meer cash uitgeven dan je hebt of meer opnemen dan je rekening bevat. Storten boven de rekeninglimiet is geblokkeerd. Upgrades en aankopen gebruiken cash; neem daarvoor zo nodig eerst geld op. Cash en rekening zijn ook zichtbaar in de bovenste statusbalk.

Bij iedere levelstijging krijgt de speler een kleine cashbonus: **$5 bij level 2**, daarna groeit de bonus **10% per level**, afgerond op hele dollars, tot maximaal **$50 per level**. Als één klus meerdere levels oplevert, krijgt de speler de bonus voor elk bereikt level. Dit hangt niet af van cash of banksaldo. Het werktelegram vermeldt de bonus apart binnen de totale opbrengst.

## Inventaris en vondsten

Passende klussen leveren gegarandeerd bouwmateriaal op: **hout** bij boswerk, **ijzererts** bij mijnwerk, **bouwsteen** in de steengroeve en **katoen** op de Katoenhoeve. Een klus van 15 seconden geeft **1**, 10 minuten **3** en 1 uur **12** stuks. De hoeveelheid staat vooraf in het werkvenster en achteraf in het telegram. Dit staat los van willekeurige vondsten.

Een klus van één uur heeft **30% kans** op één voorwerp. Bij 10 minuten is dit **5%**, bij 15 seconden **0,125%**. Bij een vondst is 60% een product, 30% kleding en 10% een wapen. Producten passen bij de locatie: brandhout uit het bos, ijzererts uit de mijn en koffiebonen bij de ranch. Er zijn ook hoeden, laarzen, jassen, oude revolvers, jachtgeweren en zakmessen.

De knop **Inventaris** toont klikbare voorwerpslots met herkenbare iconen, namen en gestapelde aantallen en filters voor **Alles**, **Wapens**, **Producten** en **Kleding**. Klik op een voorwerp voor de beschrijving. De inventaris begint leeg en gaat mee in je save. Het bekijken van menu's kost geen tijd of energie. Hout, bouwsteen, ijzererts en katoen worden bij gebouwupgrades verbruikt. Wapens kunnen worden gekocht en verzameld; combat en verkopen volgen later. Kleding kan worden uitgerust.

Reizen gaat rechtstreeks tussen locaties. Afstand bepaalt de energiekosten; iedere energie-eenheid staat voor 8 minuten reistijd.

## Telegrammen

Naast **Inventaris** staat **Berichten**, met het aantal ongelezen telegrammen. Iedere afgeronde werkzaamheid levert precies één telegram op. Je ziet de arbeid, locatie, gekozen werktijd, speldatum, werkelijk verdiend geld en ervaring, en het gevonden voorwerp (of **Geen voorwerpen**). De nieuwste post staat bovenaan; klik op een telegram om het te lezen. Nieuwe berichten verschijnen ook als het venster al open is. Het postvak bewaart de laatste honderd telegrammen. Bekijken kost geen tijd of energie.

Telegrammen en hun leesstatus gaan mee in de save. Een nieuw spel begint met een leeg postvak; oude saves krijgen bij het laden een leeg postvak.

## Opslaan

**Nieuw spel**, **Spel opslaan**, **Spel laden** en **Afsluiten** staan onder **Instellingen** rechts onderaan. De inventaris blijft direct bereikbaar. Nieuw spel vraagt bevestiging. Er is één opslagplek voor handmatige opslag; Afsluiten slaat niet automatisch op. Een nieuw spel verwijdert je eerdere save niet, totdat je opnieuw opslaat.

De JSON-save staat standaard in `.frontier/save.json` onder je gebruikersmap (`user.home`). Hij bevat versie, spelernaam, geld, niveau, ervaring, energie, locatie en datum/tijd plus de volledige inventaris, eventuele lopende klus, telegrammen met leesstatus, banksaldo en alle gebouwlevels. Versie-5 saves bewaren het begin- en eindtijdstip van de echte werktimer. Na laden wordt de resterende tijd hervat; als de tijd al verstreken is, wordt de klus één keer afgerond. Sla tijdens een lange klus op als je het spel wilt afsluiten. Opslaan gebruikt een tijdelijk bestand en waar ondersteund een atomische vervanging. Oude versie-1 tot en met versie-4 saves blijven bruikbaar; versie 1 krijgt een lege inventaris. Oude saves krijgen een lege bankrekening en gebouwen op level 1; historische levelbonussen worden niet alsnog uitgekeerd. De oorspronkelijke standaardnaam Traveler wordt Reiziger. Ontbrekende of ongeldige saves geven een melding en laten de huidige game intact.

Voor een andere opslagmap, bijvoorbeeld in de cloud:

```sh
./gradlew :lwjgl3:run -PsaveDir=/workspace/.frontier-save
```

Voor een uitgepakte distributie kan dezelfde map worden gekozen via de JVM-optie `-Dfrontier.saveDir=/pad/naar/map` in `JAVA_OPTS`.

## Tijden aanpassen in de code

- **Lopen/reizen:** `core/src/main/java/com/frontier/model/TimeRules.java`, `TRAVEL_MINUTES_PER_ENERGY` (standaard 8). `Location.travelMinutesTo()` vermenigvuldigt dit met de afstandskosten. Reizen blijft een directe actie die de spelklok vooruitzet.
- **Slapen:** dezelfde `TimeRules.java`, `SLEEP_HOURS` (standaard 8). Dit bepaalt de basisduur op herberglevel 1. `TownState.sleepMinutes()` verkort die duur bij hogere gebouwlevels en de knop toont de berekende duur. Slapen blijft direct, zonder echte wachttimer.
- **Werkkeuzes:** `core/src/main/java/com/frontier/model/WorkDuration.java`. De tweede waarde bij `QUICK`, `SHORT` en `LONG` is het aantal seconden echte wachttijd (15, 600, 3600). Pas de bijbehorende Nederlandse tekst ook aan als je deze tijden verandert. Dezelfde duur wordt bij afronding aan de spelklok toegevoegd.
- **Werkbalans:** `core/src/main/java/com/frontier/logic/JobManager.java`. Elke `Job` bevat achtereenvolgens id, locatie, naam, basisbetaling, betalingsvariatie, energie, referentieduur in minuten (60), ervaring. Dit zijn referentiewaarden voor één uur; `Job` schaalt ze naar de gekozen duur.

## Balans aanpassen

- `core/model/ProgressionRules.java`: eerste levelbonus, groeipercentage en maximum.
- `core/model/Building.java` en `core/logic/UpgradeCost.java`: basisprijzen en materiaalvereisten voor upgrades.
- `core/model/TownState.java`: maximale gebouwlevels, banksaldo-limiet en slaaptijden per level.
- `core/logic/ShopOffer.java`: artikelen, benodigde gebouwlevels, prijzen en kwaliteit.
- `core/logic/WorkMaterials.java`: materiaalsoorten en hoeveelheden per werkduur.

Deze paden vallen onder `core/src/main/java/com/frontier/`.

## Bouwen en testen

```sh
./gradlew build
./gradlew :lwjgl3:installDist
```

De uitvoerbare distributie staat in `lwjgl3/build/install/lwjgl3/`. Start `bin/lwjgl3` of `bin/lwjgl3.bat`; een JDK/JRE 21+ blijft nodig. Op macOS voeg je bij deze launcher `-XstartOnFirstThread` toe aan `JAVA_OPTS`.

`./gradlew test` draait 159 tests voor de gameplay-loop, alle 44 werkzaamheden, energie, level-ups, klok, echte werktimers, beloningen op het eindtijdstip, hervatten na offline tijd, telegrammen, leesstatus, postvakopslag, gebouwupgrades, banktransacties, winkelontgrendelingen, materiaalverbruik, levelbonussen, vondstkansen, stapeling, opslagmigratie, JSON-roundtrips en ongeldige saves. Een aparte desktoptest opent een echt venster en klikt de Scene2D-knoppen:

```sh
./gradlew :lwjgl3:desktopSmoke
```

Deze test gebruikt een tijdelijke save en verandert jouw eigen save niet. Hij controleert de uitgezoomde versleepbare kaart, de vier toekomstige aankoopplekken, statusbalken, stadsgebouwen, herberg, instellingen, alle 44 werkzaamheden, detailmenu’s, annuleren, uitputting, terugreis, slapen, vondsten, inventarisfilters, opslaan/laden, Nieuw spel, kleine en grote venstergroottes, meegroeiende kaartpanelen, gebouwlevels 1–5, winkeliconen, aankopen, stortingen/opnames en Afsluiten. Screenshots staan na de test in `build/frontier-desktop.png` en `build/frontier-desktop-small.png`, `build/frontier-work.png`, `build/frontier-inventory.png`, `build/frontier-town.png` en `build/frontier-active-work.png`. Telegrammen staan in `build/frontier-messages.png`. De nieuwe functies staan in `build/frontier-town-upgraded.png`, `build/frontier-shop-level-one.png`, `build/frontier-shop-level-five.png`, `build/frontier-tailor.png` en `build/frontier-bank.png`. Zonder desktop kan hij met Xvfb worden uitgevoerd, bijvoorbeeld `xvfb-run -a ./gradlew :lwjgl3:desktopSmoke`.

## Architectuur

- `core/model`: `Player`, `GameTime`, `Location`, `Job`, `GameState`, `Item`, `Inventory`, `WorldMap`, `Landmark`, `WorkDuration`, `TimeRules`, `ActiveWork`, `Telegram`, `Mailbox`, `Building`, `TownState`, `ProgressionRules`.
- `core/logic`: `GameSession` voert acties uit; `JobManager` bevat de 44 werkzaamheden en willekeurige betaling. `LootManager` bepaalt vondsten. `TownManager` controleert upgrades, aankopen en banktransacties; `WorkMaterials` bepaalt vaste materialen. Geen afhankelijkheid van UI. `GameSession` gebruikt een injecteerbare `Clock` voor de werktimers, zodat tests niet echt hoeven te wachten.
- `core/save`: `SaveManager` valideert en bewaart versie-7 JSON met inventaris, werktimer, telegrammen, bank, gebouwen, uitrusting, opdrachten, dorpsprojecten, ontdekkingen en reisdagboek. Versie-1 tot en met versie-6 saves worden ook geladen.
- `core/ui`: `GameScreen`, `ActionBar`, `SettingsDialog`, `TownDialog`, `TownArtwork`, `ShopDialog`, `BankDialog`, `BuildingUpgradeDialog`, `ItemIcons`, `ItemTile`, `WorkDialog`, `InventoryDialog`, `MapPanel`, `FrontierSkin`, `WoodTexture`, `MapArtwork`, `WorldArtwork`, `LandmarkDialog`, `WorkProgress`, `MessagesDialog` verzorgen uitsluitend presentatie en bediening.
- `lwjgl3`: desktoplauncher en desktop-smoketest.

Deze versie bevat een versleepbare wereldkaart, 44 werkzaamheden met echte werktimers buiten de stad, een stadsvenster met zes gebouwen, niveaus, slapen, vondsten, inventaris en opslag via instellingen.

### Saloon en opdrachten

Open de stad in Red Creek, selecteer **Saloon** en kies **Opdrachtgevers**. Er zijn drie eenmalige opdrachten: Martha vraagt 10 brandhout ($25, 30 XP, hoed), Elias vraagt drie afgeronde klussen bij Willow Farm ($30, 40 XP, werkhemd) en Clara vraagt 10 bouwsteen ($35, 50 XP, laarzen). Elke opdracht is vanaf het begin beschikbaar. Neem een opdracht eerst aan. Alleen daarna afgeronde boerderijklussen tellen; gevonden materialen die je al hebt mag je wel inleveren. Je kunt alle opdrachten tegelijk aannemen. Kom terug naar de Saloon om materialen in te leveren en je beloning op te halen. Voltooide opdrachten geven een telegram; beloningen kunnen niet opnieuw worden opgehaald.

### Kleding uitrusten

Klik in **Inventaris** op kleding en kies **Uitrusten** of **Uittrekken**. Hoofd, lichaam en voeten hebben elk één kledingplek. Kleding blijft in je inventaris. Laarzen besparen 10% reisenergie, een werkhemd 10% energie bij boerderijen en de ranch, een wollen jas 10% bij de mijn en steengroeve, een stofjas 12% bij alle werkzaamheden en een hoed 5% bij boswerk. Kosten worden naar boven afgerond; zeer korte klussen kunnen daardoor evenveel kosten. Reistijd en beloningen veranderen niet. De volledige terugweg blijft gereserveerd, zodat je ook na het uittrekken van laarzen thuis kunt komen. Kleding wisselen kan na een lopende klus.

Saves gebruiken versie 7 en bewaren opdrachtvoortgang, opgehaalde beloningen, uitrusting en het persoonlijke verhaal. Oudere saves blijven ondersteund en krijgen een Saloon op level 1.

### Een thuis in Red Creek

**Stad → Saloon → Opdrachtgevers → Dorpsprojecten** begint een klein verhaal. Martha probeert haar herberg weer gastvrij te maken; Elias wil voorkomen dat zijn gezin door droogte moet vertrekken. Jij kiest wie je eerst helpt. Eén project tegelijk: na die eerste belofte kun je het andere project ook uitvoeren. Je eerste keuze blijft bewaard. Materialen voor opdrachten, gebouwupgrades en dorpsprojecten komen uit dezelfde inventaris; kies waarvoor je ze gebruikt.

| Project | Kosten | Zichtbaar resultaat en voordeel |
| --- | --- | --- |
| Martha’s veranda | $15, 12 hout, 4 steen | Veranda met banken bij de herberg; slapen kost 20% minder speltijd. |
| Elias’ waterput | $15, 4 hout, 12 steen | Waterput en groene akkers op de Wilgenhoeve; werkzaamheden daar kosten 15% minder energie. |
| Clara’s voetbrug | $25, 16 hout, 10 steen | Een voetbrug op de kaart; de Vergeten halte wordt bereikbaar. Eerst moet minstens één van de andere projecten klaar zijn. |

Kies **Eerst helpen**, verzamel cash en materialen en kies daarna **Samen bouwen** in Red Creek. Bouwen kost twee speluren, zonder echte wachttijd. De kosten worden één keer afgeschreven. Verhaalvoordelen staan los van gebouwlevels; de waterputbonus stapelt met kleding. Alle energiekosten worden naar boven afgerond. De Vergeten halte heeft twee eigen werkzaamheden: het perron vrijmaken en oude registers doorzoeken. Reizen en werken bij de halte zijn vóór het brugherstel geblokkeerd.

De knop **Reisdagboek** bewaart ontmoetingen, beloftes, bouwmomenten en ontdekkingen als gedateerde bladzijden met kleine pentekeningen. Dit is een apart venster naast de zakelijke telegrammen. Tijdens afgerond boswerk kun je een oude kaart ontdekken; in de mijn, steengroeve of ranch een brief; bij de Vergeten halte een verborgen kelder. Elke ontdekking is eenmalig. Kans per passende klus: 1% bij 15 seconden, 12% bij 10 minuten en 45% bij één uur. Ontdekkingen zijn verhaalfeiten en komen in het dagboek, niet als verkoopbare inventarisitems.

De drie dorpsprojecten, je eerste keuze, het actieve project, de ontdekkingen en dagboekbladzijden blijven bewaard bij opslaan/laden en worden gewist bij Nieuw spel. Oudere saves beginnen dit hoofdstuk zonder voltooide projecten. In `core/model/VillageProject.java` staan verhalen, kosten en voordelen; `core/logic/StoryManager.java` voert de acties uit en bepaalt ontdekkingskansen. De desktoptest controleert dit hoofdstuk via echte Scene2D-knoppen; screenshots staan in `build/frontier-village-projects.png`, `build/frontier-story-town.png` en `build/frontier-travel-journal.png`.

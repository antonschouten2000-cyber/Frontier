package com.frontier.logic;

import com.frontier.model.*;
import java.util.List;

/** Een klein, vast assortiment dat per gebouwlevel wordt ontsloten. */
public record ShopOffer(Building building, int requiredLevel, Item item, int price, int quality) {
    private static final List<ShopOffer> OFFERS = List.of(
        new ShopOffer(Building.GUNSMITH, 1, Item.SLINGSHOT, 8, 1),
        new ShopOffer(Building.GUNSMITH, 2, Item.KNIFE, 18, 2),
        new ShopOffer(Building.GUNSMITH, 3, Item.REVOLVER, 45, 5),
        new ShopOffer(Building.GUNSMITH, 4, Item.RIFLE, 85, 8),
        new ShopOffer(Building.GUNSMITH, 5, Item.REPEATER, 140, 12),
        new ShopOffer(Building.TAILOR, 1, Item.WORK_SHIRT, 6, 1),
        new ShopOffer(Building.TAILOR, 2, Item.HAT, 12, 2),
        new ShopOffer(Building.TAILOR, 3, Item.BOOTS, 25, 3),
        new ShopOffer(Building.TAILOR, 4, Item.COAT, 50, 5),
        new ShopOffer(Building.TAILOR, 5, Item.DUSTER, 90, 8));
    public static List<ShopOffer> at(Building building) { return OFFERS.stream().filter(offer -> offer.building() == building).toList(); }
    public static boolean available(ShopOffer offer) { return OFFERS.contains(offer); }
}

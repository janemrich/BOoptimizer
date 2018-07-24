package de.jan;

import java.util.Random;

public enum Unit {
    SCV("SCV"), BARRACKS("Barracks"), SUPPLY_DEPOT("SupplyDepot"), MARINE("Marine");

    private final String unitDescription;

    private Unit(String value) {
        unitDescription = value;
    }

    public String getUnitDescription() {
        return unitDescription;
    }

    /**
     * @return random Unit
     */
    public static Unit randomUnit() {
        Random rn = new Random();

        int i = rn.nextInt(Unit.values().length);
        return Unit.values()[i];
    }

    /**
     * @return random Unit
     */
    public static Unit firstUnit() {
        Random rn = new Random();

        Unit[] units = new Unit[] {SUPPLY_DEPOT, SCV};
        int i = rn.nextInt(units.length);
        return units[i];
    }

    /**
     * @return random Unit
     */
    public static Unit secondUnit() {
        Random rn = new Random();

        Unit[] units = new Unit[] {SUPPLY_DEPOT, SCV, BARRACKS};
        int i = rn.nextInt(units.length);
        return units[i];
    }
}

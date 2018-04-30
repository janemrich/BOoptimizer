package de.jan;

public enum Unit {
    SCV("SCV"), BARRACKS("Barracks"), SUPPLY_DEPOT("Supply Depot"), MARINE("Marine");

    private final String unitDescription;

    private Unit(String value) {
        unitDescription = value;
    }

    public String getUnitDescription() {
        return unitDescription;
    }
}
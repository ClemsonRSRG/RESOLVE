package edu.clemson.rsrg.statushandling;

public enum WarningType {
    GENERIC_WARNING("Generic Warning"),
    INCORRECT_PARAMETER_MODE_USAGE("Incorrect Parameter Mode"),;

    private final String plainName;

    private WarningType(String plainName) {
        this.plainName = plainName;
    }

    public String toString() {
        return plainName;
    }
}

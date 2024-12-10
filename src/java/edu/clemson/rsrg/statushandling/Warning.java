package edu.clemson.rsrg.statushandling;

public class Warning {
    WarningType type;

    public Warning(WarningType type) {
        this.type = type;
    }

    public boolean isType(WarningType type) {
        return this.type == type;
    }
}

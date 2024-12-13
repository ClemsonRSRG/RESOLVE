package edu.clemson.rsrg.statushandling;

public class Warning {
    WarningType type;
    String message;

    public Warning(WarningType type) {
        this.type = type;
        message = "abcd";
    }

    public boolean isType(WarningType type) {
        return this.type == type;
    }

    public String getMessage() {
        return message;
    }
}

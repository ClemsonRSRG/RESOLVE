package edu.clemson.rsrg.statushandling;

public class Warning {
    WarningType type;
    String message;

    public Warning(WarningType type, String msg) {
        this.type = type;
        message = msg;
    }

    public boolean isType(WarningType type) {
        return this.type == type;
    }

    public String getMessage() {
        return message;
    }
}

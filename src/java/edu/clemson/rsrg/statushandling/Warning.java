package edu.clemson.rsrg.statushandling;

import edu.clemson.rsrg.parsing.data.Location;

public class Warning {
    WarningType type;
    String message;
    Location location;

    public Warning(WarningType typ, Location loc, String msg) {
        type = typ;
        message = msg;
        location = loc;
    }

    public boolean isType(WarningType type) {
        return this.type == type;
    }

    public String getMessage() {
        return message;
    }

    public Location getLocation() {
        return location;
    }
}

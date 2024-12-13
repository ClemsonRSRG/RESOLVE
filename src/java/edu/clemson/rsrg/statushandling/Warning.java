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

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nWarning: ");
        sb.append(type.toString());
        sb.append("\nat ");
        sb.append(location.toString());
        sb.append("\n");
        sb.append(message);
        sb.append("\n");
        return sb.toString();
    }
}

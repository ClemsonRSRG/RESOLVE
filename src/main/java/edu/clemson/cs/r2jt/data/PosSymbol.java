/**
 * PosSymbol.java
 * ---------------------------------
 * Copyright (c) 2014
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.data;

import java.io.File;

public class PosSymbol implements AsStringCapability {

    // ===========================================================
    // Variables
    // ===========================================================

    private Location location;

    private Symbol symbol;

    // ===========================================================
    // Constructors
    // ===========================================================

    public PosSymbol() {};

    public PosSymbol(Location location, Symbol symbol) {
        this.location = location;
        this.symbol = symbol;
    }

    // ===========================================================
    // Accessor Methods
    // ===========================================================

    /** Returns the value of the location variable. */
    public Location getLocation() {
        return location;
    }

    /** Returns the value of the symbol variable. */
    public Symbol getSymbol() {
        return symbol;
    }

    public File getFile() {
        assert location != null : "PosSymbol's location is null";
        return location.getFile();
    }

    public Pos getPos() {
        assert location != null : "PosSymbol's location is null";
        return location.getPos();
    }

    public String getName() {
        return symbol.getName();
    }

    // -----------------------------------------------------------
    // Set Methods
    // -----------------------------------------------------------

    /** Sets the location variable to the specified value. */
    public void setLocation(Location location) {
        this.location = location;
    }

    /** Sets the symbol variable to the specified value. */
    public void setSymbol(Symbol symbol) {
        this.symbol = symbol;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * Returns true if both the symbol field inside the PosSymbol
     * matches our symbol.
     *
     * @param pos PosSymbol to be compared.
     *
     * @return True if equal, false otherwise.
     */
    public boolean equals(PosSymbol pos) {
        return equals(pos.getSymbol());
    }

    /**
     * Returns true if both the symbol field matches
     * our symbol.
     *
     * @param sym Symbol to be compared.
     *
     * @return True if equal, false otherwise.
     */
    public boolean equals(Symbol sym) {
        return symbol.getName().equals(sym.getName());
    }

    /**
     * Returns true if the symbol field matches the symbol created
     * by the specified string.
     *
     * @param str String to be compared.
     *
     * @return True if equal, false otherwise.
     */
    public boolean equals(String str) {
        return symbol == Symbol.symbol(str);
    }

    /** Returns the string representation of the associated symbol. */
    public String toString() {
        String retval;

        if (symbol == null) {
            retval = "[No Symbol]";
        }
        else {
            retval = symbol.toString();
        }

        return retval;
    }

    /** Returns a formatted text string of this class. */
    public String asString(int indent, int increment) {
        StringBuffer sb = new StringBuffer();
        sb.append(printSpace(indent));
        sb.append(symbol.toString());
        sb.append("\n");
        return sb.toString();
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    private String printSpace(int n) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < n; i++) {
            sb.append(" ");
        }
        return sb.toString();
    }

    public PosSymbol copy() {
        String name = symbol.getName();
        return new PosSymbol(location, Symbol.symbol(name));
    }
}

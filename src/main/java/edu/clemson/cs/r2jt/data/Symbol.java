/*
 * Symbol.java
 * ---------------------------------
 * Copyright (c) 2018
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.data;

public class Symbol implements Comparable<Symbol> {

    // ===========================================================
    // Variables
    // ===========================================================

    private String name;

    private static java.util.Dictionary dict = new java.util.Hashtable();

    // ===========================================================
    // Constructors
    // ===========================================================

    private Symbol(String name) {
        this.name = name;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    public String getName() {
        return name;
    }

    public boolean equals(String str) {
        return (this == Symbol.symbol(str));
    }

    public boolean equals(Symbol sym) {
        return (this == sym);
    }

    /** Returns the unique symbol associated with a string. */
    public static Symbol symbol(String str) {
        String inStr = str.intern();
        Symbol sym = (Symbol) dict.get(inStr);
        if (sym == null) {
            sym = new Symbol(inStr);
            dict.put(inStr, sym);
        }
        return sym;
    }

    public static Symbol creatOldSymbol(Symbol sym) {
        String str = "#" + sym.toString();
        return Symbol.symbol(str);
    }

    public String asString(int indent, int increment) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < indent; i++) {
            sb.append(" ");
        }
        sb.append(this.toString() + "\n");
        return sb.toString();
    }

    public String toString() {
        return name;
    }

    public int compareTo(Symbol o) {
        return name.compareTo(o.name);
    }
}

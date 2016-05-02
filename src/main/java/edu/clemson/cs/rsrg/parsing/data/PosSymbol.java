/**
 * PosSymbol.java
 * ---------------------------------
 * Copyright (c) 2016
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.parsing.data;

import edu.clemson.cs.rsrg.errorhandling.exception.MiscErrorException;
import java.util.Dictionary;
import java.util.Hashtable;

/**
 * <p>This class creates the representation of any symbol we
 * have encountered during the compile process.</p>
 *
 * @author Yu-Shan Sun
 * @version 1.0
 */
public class PosSymbol implements BasicCapabilities {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>Symbol location</p> */
    private final Location myLocation;

    /** <p>Static dictionary containing all the symbols we have created so far.</p> */
    private final static Dictionary<String, Symbol> mySymbolDict = new Hashtable<>();

    /** <p>Inner symbol representation</p> */
    private final Symbol mySymbol;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructor create the symbol representation
     * used by the compiler.</p>
     *
     * @param location The location where this symbol originated from.
     * @param sym Symbol name.
     */
    public PosSymbol(Location location, String sym) {
        myLocation = location;
        mySymbol = getSymbol(sym);
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This method creates a special indented
     * text version of the class as a string.</p>
     *
     * @param indentSize The base indentation to the first line
     *                   of the text.
     * @param innerIndentSize The additional indentation increment
     *                        for the subsequent lines.
     *
     * @return A formatted text string of the class.
     */
    public String asString(int indentSize, int innerIndentSize) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < indentSize; i++) {
            sb.append(" ");
        }
        sb.append(mySymbol.toString());
        sb.append("\n");

        return sb.toString();
    }

    /**
     * <p>This method overrides the default clone method implementation
     * for the {link PosSymbol} class.</p>
     *
     * @return A deep copy of the object.
     */
    @Override
    public PosSymbol clone() {
        return new PosSymbol(myLocation, mySymbol.getName());
    }

    /**
     * <p>This method overrides the default equals method implementation
     * for the {link PosSymbol} class.</p>
     *
     * @param o Object to be compared.
     *
     * @return True if all the fields are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if (o instanceof String) {
            String str = (String) o;
            result = mySymbol.equals(str);
        }
        else if (o instanceof PosSymbol) {
            PosSymbol posSymbol = (PosSymbol) o;
            result = mySymbol.equals(posSymbol.mySymbol);
        }

        return result;
    }

    /**
     * <p>Returns of the location where this object
     * originated from.</p>
     *
     * @return A {link Location} representation object.
     */
    public Location getLocation() {
        return myLocation;
    }

    /**
     * <p>Returns the inner string representation
     * of this class.</p>
     *
     * @return Name as a string.
     */
    public String getName() {
        return mySymbol.getName();
    }

    /**
     * <p>Returns the symbol in string format.</p>
     *
     * @return Symbol as a string.
     */
    @Override
    public String toString() {
        return mySymbol.toString();
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>In order to not have duplicated symbols, we have
     * are using a static dictionary to keep track of all symbols
     * we have created so far. If a symbol already exists,
     * we return the symbol in our dictionary. Otherwise,
     * we create one and store it in our dictionary.</p>
     *
     * @param str The symbol we are searching.
     *
     * @return The unique symbol associated with a string.
     */
    private Symbol getSymbol(String str) {
        String inStr = str.intern();
        Symbol sym = mySymbolDict.get(inStr);
        if (sym == null) {
            sym = new Symbol(inStr);
            mySymbolDict.put(inStr, sym);
        }

        return sym;
    }

    // ===========================================================
    // Inner Class for Symbol
    // ===========================================================

    /**
     * <p>Private inner class to store the symbol.</p>
     */
    private class Symbol implements Comparable<Symbol>, BasicCapabilities {

        // ===========================================================
        // Member Fields
        // ===========================================================

        /** <p>Symbol name</p> */
        private final String myName;

        // ===========================================================
        // Constructors
        // ===========================================================

        /**
         * <p>This constructor adds/modifies functionality to the
         * {link String} class to create the inner symbol representation
         * used by the compiler.</p>
         *
         * @param name Symbol name
         */
        private Symbol(String name) {
            myName = name;
        }

        // ===========================================================
        // Public Methods
        // ===========================================================

        /**
         * <p>This method creates a special indented
         * text version of the class as a string.</p>
         *
         * @param indentSize The base indentation to the first line
         *                   of the text.
         * @param innerIndentSize The additional indentation increment
         *                        for the subsequent lines.
         *
         * @return A formatted text string of the class.
         */
        public String asString(int indentSize, int innerIndentSize) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < indentSize; i++) {
                sb.append(" ");
            }
            sb.append(this.toString() + "\n");

            return sb.toString();
        }

        /**
         * <p>This method shouldn't be called, because we don't want to be able to
         * make copies of the current symbol.</p>
         *
         * @return Nothing. This throws an exception.
         */
        @Override
        public Object clone() {
            throw new MiscErrorException("Can't make copies of a Symbol.", new IllegalAccessException());
        }

        /**
         * <p>This method implements the method in {link Comparable}.</p>
         *
         * @param o Object to be compared.
         *
         * @return A negative integer, zero, or a positive integer as this object
         *         is less than, equal to, or greater than the specified object.
         */
        public int compareTo(Symbol o) {
            return myName.compareTo(o.myName);
        }

        /**
         * <p>This method overrides the default equals method implementation
         * for the {link Symbol} class.</p>
         *
         * @param o Object to be compared.
         *
         * @return True if all the fields are equal, false otherwise.
         */
        @Override
        public boolean equals(Object o) {
            boolean result = false;
            if (o instanceof String) {
                String str = (String) o;
                result = myName.equals(str);
            }
            else if (o instanceof Symbol) {
                Symbol sym = (Symbol) o;
                result = myName.equals(sym.myName);
            }

            return result;
        }

        /**
         * <p>Returns the inner string representation
         * of this class.</p>
         *
         * @return Name as a string.
         */
        public String getName() {
            return myName;
        }

        /**
         * <p>Returns the symbol in string format.</p>
         *
         * @return Symbol as a string.
         */
        @Override
        public String toString() {
            return myName;
        }

    }

}
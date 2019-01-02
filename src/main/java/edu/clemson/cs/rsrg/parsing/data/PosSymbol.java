/*
 * PosSymbol.java
 * ---------------------------------
 * Copyright (c) 2019
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.parsing.data;

import edu.clemson.cs.rsrg.statushandling.exception.MiscErrorException;
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
     * text version of the instantiated object.</p>
     *
     * @param indentSize The base indentation to the first line
     *                   of the text.
     * @param innerIndentInc The additional indentation increment
     *                       for the subsequent lines.
     *
     * @return A formatted text string of the class.
     */
    public final String asString(int indentSize, int innerIndentInc) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < indentSize; i++) {
            sb.append(" ");
        }
        sb.append(mySymbol.toString());

        return sb.toString();
    }

    /**
     * <p>This method overrides the default clone method implementation
     * for the {@code PosSymbol} class.</p>
     *
     * @return A deep copy of the object.
     */
    @Override
    public final PosSymbol clone() {
        Location newLoc = null;
        if (myLocation != null) {
            newLoc = myLocation.clone();
        }

        return new PosSymbol(newLoc, mySymbol.getName());
    }

    /**
     * <p>This method overrides the default equals method implementation
     * for the {@code PosSymbol} class.</p>
     *
     * @param o Object to be compared.
     *
     * @return {@code true} if all the fields are equal, {@code false} otherwise.
     */
    @Override
    public final boolean equals(Object o) {
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
     * @return A {@link Location} representation object.
     */
    public final Location getLocation() {
        return myLocation;
    }

    /**
     * <p>Returns the inner string representation
     * of this class.</p>
     *
     * @return Name as a string.
     */
    public final String getName() {
        return mySymbol.getName();
    }

    /**
     * <p>This method overrides the default {@code hashCode} method implementation
     * for the {@code PosSymbol} class.</p>
     *
     * @return The hash code associated with the object.
     */
    @Override
    public final int hashCode() {
        int result = myLocation != null ? myLocation.hashCode() : 0;
        result = 31 * result + mySymbol.hashCode();
        return result;
    }

    /**
     * <p>Returns the symbol in string format.</p>
     *
     * @return Symbol as a string.
     */
    @Override
    public final String toString() {
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
         * text version of the instantiated object.</p>
         *
         * @param indentSize The base indentation to the first line
         *                   of the text.
         * @param innerIndentInc The additional indentation increment
         *                       for the subsequent lines.
         *
         * @return A formatted text string of the class.
         */
        public final String asString(int indentSize, int innerIndentInc) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < indentSize; i++) {
                sb.append(" ");
            }
            sb.append(this.toString());

            return sb.toString();
        }

        /**
         * <p>This method shouldn't be called, because we don't want to be able to
         * make copies of the current symbol.</p>
         *
         * @return Nothing. This throws an exception.
         */
        @Override
        public final Object clone() {
            throw new MiscErrorException("Can't make copies of a Symbol.", new IllegalAccessException());
        }

        /**
         * <p>This method implements the method in {@link Comparable#compareTo(Object)}.</p>
         *
         * @param o Object to be compared.
         *
         * @return A negative integer, zero, or a positive integer as this object
         *         is less than, equal to, or greater than the specified object.
         */
        public final int compareTo(Symbol o) {
            return myName.compareTo(o.myName);
        }

        /**
         * <p>This method overrides the default equals method implementation
         * for the {@code Symbol} class.</p>
         *
         * @param o Object to be compared.
         *
         * @return {@code true} if all the fields are equal, {@code false} otherwise.
         */
        @Override
        public final boolean equals(Object o) {
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
        public final String getName() {
            return myName;
        }

        /**
         * <p>This method overrides the default {@code hashCode} method implementation
         * for the {@code Symbol} class.</p>
         *
         * @return The hash code associated with the object.
         */
        @Override
        public final int hashCode() {
            return myName.hashCode();
        }

        /**
         * <p>Returns the symbol in string format.</p>
         *
         * @return Symbol as a string.
         */
        @Override
        public final String toString() {
            return myName;
        }

    }

}
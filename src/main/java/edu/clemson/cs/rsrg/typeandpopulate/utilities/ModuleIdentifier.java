/*
 * ModuleIdentifier.java
 * ---------------------------------
 * Copyright (c) 2021
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.typeandpopulate.utilities;

import edu.clemson.cs.rsrg.absyn.declarations.moduledecl.ModuleDec;
import edu.clemson.cs.rsrg.absyn.items.programitems.UsesItem;

/**
 * <p>
 * Identifies a particular module unambiguously.
 * </p>
 * 
 * <p>
 * <strong>Note:</strong> Currently, we only permit one level of namespace. But
 * ultimately that will
 * probably change (because, for example, at this moment if there were two
 * "Stack_Templates", we
 * couldn't deal with that. A java class-path-like solution seems inevitable.
 * For the moment
 * however, this is just a wrapper around the string name of the module to
 * facilitate changing how
 * we deal with modules later.
 * </p>
 *
 * @author Hampton Smith
 * @author Yu-Shan Sun
 * @author Daniel Welch
 * @version 2.0
 */
public class ModuleIdentifier implements Comparable<ModuleIdentifier> {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * Name of the module.
     * </p>
     */
    private final String myName;

    /**
     * <p>
     * Flag to indicate whether or not this is a global module.
     * </p>
     */
    private final boolean myGlobalFlag;

    // ===========================================================
    // Objects
    // ===========================================================

    /**
     * <p>
     * This is used to indicate that a created object belongs to a global
     * module.
     * </p>
     */
    public static final ModuleIdentifier GLOBAL = new ModuleIdentifier();

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * Private constructor to create a global module object.
     * </p>
     */
    private ModuleIdentifier() {
        myName = "GLOBAL";
        myGlobalFlag = true;
    }

    /**
     * <p>
     * This creates an module identifier for a module declaration.
     * </p>
     *
     * @param m A {@link ModuleDec} object.
     */
    public ModuleIdentifier(ModuleDec m) {
        this(m.getName().getName());
    }

    /**
     * <p>
     * This creates an module identifier for an import module declaration.
     * </p>
     *
     * @param i An imported {@link ModuleDec} object.
     */
    public ModuleIdentifier(UsesItem i) {
        this(i.getName().getName());
    }

    /**
     * <p>
     * This creates an module identifier with a name.
     * </p>
     *
     * @param s The string representation of an module.
     */
    public ModuleIdentifier(String s) {
        myName = s;
        myGlobalFlag = false;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * This method overrides the default clone method implementation for the
     * {@link ModuleIdentifier}
     * class.
     * </p>
     *
     * @return A deep copy of the object.
     */
    @Override
    public final ModuleIdentifier clone() {
        return new ModuleIdentifier(myName);
    }

    /**
     * <p>
     * This method implements the method in {@link Comparable}.
     * </p>
     *
     * @param o Object to be compared.
     *
     * @return A negative integer, zero, or a positive integer as this object is
     *         less than, equal to,
     *         or greater than the specified object.
     */
    @Override
    public final int compareTo(ModuleIdentifier o) {
        return myName.compareTo(o.myName);
    }

    /**
     * <p>
     * This method overrides the default equals method implementation for the
     * {@link ModuleIdentifier}
     * class.
     * </p>
     *
     * @param o Object to be compared.
     *
     * @return True if all the fields are equal, false otherwise.
     */
    @Override
    public final boolean equals(Object o) {
        boolean result = (o instanceof ModuleIdentifier);
        if (result) {
            result = ((ModuleIdentifier) o).myName.equals(myName);
        }

        return result;
    }

    /**
     * <p>
     * Returns full qualified symbol in string format.
     * </p>
     *
     * @param symbol Symbol to be qualified.
     *
     * @return Qualified symbol as a string.
     */
    public final String fullyQualifiedRepresentation(String symbol) {
        return myName + "::" + symbol;
    }

    /**
     * <p>
     * This method overrides the default hashCode method implementation for the
     * {@link ModuleIdentifier} class.
     * </p>
     *
     * @return The hash code value of the object.
     */
    @Override
    public final int hashCode() {
        return myName.hashCode();
    }

    /**
     * <p>
     * Returns the module identifier in string format.
     * </p>
     *
     * @return Symbol as a string.
     */
    @Override
    public final String toString() {
        return myName;
    }

}

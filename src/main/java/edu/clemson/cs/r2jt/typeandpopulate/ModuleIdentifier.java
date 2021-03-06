/*
 * ModuleIdentifier.java
 * ---------------------------------
 * Copyright (c) 2020
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.typeandpopulate;

import edu.clemson.cs.r2jt.absyn.ModuleDec;
import edu.clemson.cs.r2jt.absyn.UsesItem;
import edu.clemson.cs.r2jt.data.ModuleID;
import org.antlr.v4.runtime.Token;

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
 */
public class ModuleIdentifier implements Comparable<ModuleIdentifier> {

    public static final ModuleIdentifier GLOBAL = new ModuleIdentifier();

    private final String myName;
    private final boolean myGlobalFlag;

    private ModuleIdentifier() {
        myName = "GLOBAL";
        myGlobalFlag = true;
    }

    public ModuleIdentifier(ModuleID mid) {
        this(mid.getName().getName());
    }

    public ModuleIdentifier(ModuleDec m) {
        this(m.getName().getName());
    }

    public ModuleIdentifier(Token t) {
        this(t.getText());
    }

    public ModuleIdentifier(UsesItem i) {
        this(i.getName().getName());
    }

    public ModuleIdentifier(String s) {
        myName = s;
        myGlobalFlag = false;
    }

    public boolean equals(Object o) {
        boolean result = (o instanceof ModuleIdentifier);

        if (result) {
            result = ((ModuleIdentifier) o).myName.equals(myName);
        }
        return result;
    }

    public int hashCode() {
        return myName.hashCode();
    }

    @Override
    public int compareTo(ModuleIdentifier o) {
        return myName.compareTo(o.myName);
    }

    public String toString() {
        return myName;
    }

    public String fullyQualifiedRepresentation(String symbol) {
        return myName + "." + symbol;
    }
}

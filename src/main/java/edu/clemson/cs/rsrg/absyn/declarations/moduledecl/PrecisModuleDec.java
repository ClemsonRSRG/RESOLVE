/**
 * PrecisModuleDec.java
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
package edu.clemson.cs.rsrg.absyn.declarations.moduledecl;

import edu.clemson.cs.rsrg.absyn.declarations.Dec;
import edu.clemson.cs.rsrg.absyn.declarations.paramdecl.ModuleParameterDec;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>This is the class for the precis module declarations
 * that the compiler builds from the ANTLR4 AST tree.</p>
 *
 * @version 2.0
 */
public class PrecisModuleDec extends ModuleDec {

    // ===========================================================
    // Constructor
    // ===========================================================

    /**
     * <p>This constructor creates a "Precis" module representation.</p>
     *
     * @param l A {@link Location} representation object.
     * @param name The name in {@link PosSymbol} format.
     * @param parameterDecs The list of {@link ModuleParameterDec} objects.
     * @param usesItems The list of {@link UsesItem} objects.
     * @param decs The list of {@link Dec} objects.
     */
    public PrecisModuleDec(Location l, PosSymbol name,
            List<ModuleParameterDec> parameterDecs, List<UsesItem> usesItems,
            List<Dec> decs) {
        super(l, name, parameterDecs, usesItems, decs);
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
    @Override
    public String asString(int indentSize, int innerIndentSize) {
        StringBuffer sb = new StringBuffer();
        printSpace(indentSize, sb);
        sb.append("PrecisModuleDec\n");

        if (myName != null) {
            sb.append(myName.asString(indentSize + innerIndentSize,
                    innerIndentSize));
            sb.append("\n");
        }

        if (myParameterDecs != null) {
            for (ModuleParameterDec m : myParameterDecs) {
                sb.append(m.asString(indentSize + innerIndentSize,
                        innerIndentSize));
                sb.append("\n");
            }
        }

        if (myUsesItems != null) {
            for (UsesItem u : myUsesItems) {
                sb.append(u.asString(indentSize + innerIndentSize,
                        innerIndentSize));
                sb.append("\n");
            }
        }

        if (myDecs != null) {
            for (Dec d : myDecs) {
                sb.append(d.asString(indentSize + innerIndentSize,
                        innerIndentSize));
                sb.append("\n");
            }
        }

        return sb.toString();
    }

    /**
     * <p>This method overrides the default clone method implementation
     * for the {@link PrecisModuleDec} class.</p>
     *
     * @return A deep copy of the object.
     */
    @Override
    public PrecisModuleDec clone() {
        // Copy all the items in the lists
        List<ModuleParameterDec> newParameterDecs = new ArrayList<>(myParameterDecs.size());
        Collections.copy(newParameterDecs, myParameterDecs);
        List<UsesItem> newUsesItems = new ArrayList<>(myUsesItems.size());
        Collections.copy(newUsesItems, myUsesItems);
        List<Dec> newDecs = new ArrayList<>(myDecs.size());
        Collections.copy(newDecs, myDecs);

        return new PrecisModuleDec(new Location(myLoc), myName.clone(), newParameterDecs, newUsesItems, newDecs);
    }

    /**
     * <p>This method overrides the default equals method implementation
     * for the {@link PrecisModuleDec} class.</p>
     *
     * @param o Object to be compared.
     *
     * @return True if all the fields are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if (o instanceof PrecisModuleDec) {
            PrecisModuleDec moduleDec = (PrecisModuleDec) o;
            result = myLoc.equals(moduleDec.myLoc);

            if (result) {
                result = myName.equals(moduleDec.myName);

                if (result) {
                    result = myParameterDecs.equals(moduleDec.myParameterDecs);

                    if (result) {
                        result = myUsesItems.equals(moduleDec.myUsesItems);

                        if (result) {
                            result = myDecs.equals(moduleDec.myDecs);
                        }
                    }
                }
            }
        }

        return result;
    }

    /**
     * <p>Returns the module in string format.</p>
     *
     * @return Module as a string.
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();

        if (myName != null) {
            sb.append(myName.toString());
            sb.append("\n");
        }

        if (myParameterDecs != null) {
            for (ModuleParameterDec m : myParameterDecs) {
                sb.append(m.toString());
                sb.append("\n");
            }
        }

        if (myUsesItems != null) {
            for (UsesItem u : myUsesItems) {
                sb.append(u.toString());
                sb.append("\n");
            }
        }

        if (myDecs != null) {
            for (Dec d : myDecs) {
                sb.append(d.toString());
                sb.append("\n");
            }
        }

        return sb.toString();
    }

}
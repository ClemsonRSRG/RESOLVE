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

import edu.clemson.cs.rsrg.absyn.items.programitems.UsesItem;
import edu.clemson.cs.rsrg.absyn.declarations.Dec;
import edu.clemson.cs.rsrg.absyn.declarations.paramdecl.ModuleParameterDec;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * <p>This is the class for the precis module declarations
 * that the compiler builds using the ANTLR4 AST nodes.</p>
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
     * {@inheritDoc}
     */
    @Override
    public final String asString(int indentSize, int innerIndentInc) {
        StringBuffer sb = new StringBuffer();
        printSpace(indentSize, sb);
        sb.append("Precis ");
        sb.append(myName.asString(0, innerIndentInc));

        if (myParameterDecs.size() > 0) {
            sb.append("( ");
            Iterator<ModuleParameterDec> it = myParameterDecs.iterator();
            while (it.hasNext()) {
                ModuleParameterDec m = it.next();
                sb.append(m.asString(0, innerIndentInc));

                if (it.hasNext()) {
                    sb.append(", ");
                }
            }
            sb.append(" )\n");
        }

        if (myUsesItems.size() > 0) {
            printSpace(indentSize + innerIndentInc, sb);
            sb.append("uses ");
            Iterator<UsesItem> it = myUsesItems.iterator();
            while (it.hasNext()) {
                UsesItem u = it.next();
                sb.append(u.asString(0, innerIndentInc));

                if (it.hasNext()) {
                    sb.append(", ");
                }
            }
            sb.append("\n");
        }

        for (Dec d : myDecs) {
            sb.append(d.asString(indentSize + innerIndentInc, innerIndentInc));
            sb.append("\n");
        }
        sb.append("\nend ");
        sb.append(myName.asString(0, innerIndentInc));

        return sb.toString();
    }

    /**
     * <p>Returns the module in string format.</p>
     *
     * @return Module as a string.
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Precis ");
        sb.append(myName.toString());

        if (myParameterDecs.size() > 0) {
            sb.append("( ");
            Iterator<ModuleParameterDec> it = myParameterDecs.iterator();
            while (it.hasNext()) {
                ModuleParameterDec m = it.next();
                sb.append(m.toString());

                if (it.hasNext()) {
                    sb.append(", ");
                }
            }
            sb.append(" )\n");
        }

        if (myUsesItems.size() > 0) {
            sb.append("\tuses ");
            Iterator<UsesItem> it = myUsesItems.iterator();
            while (it.hasNext()) {
                UsesItem u = it.next();
                sb.append(u.toString());

                if (it.hasNext()) {
                    sb.append(", ");
                }
            }
            sb.append("\n");
        }

        for (Dec d : myDecs) {
            sb.append("\t");
            sb.append(d.toString());
            sb.append("\n");
        }
        sb.append("\nend ");
        sb.append(myName.toString());

        return sb.toString();
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * {@inheritDoc}
     */
    @Override
    protected final PrecisModuleDec copy() {
        // Copy all the items in the lists
        List<ModuleParameterDec> newParameterDecs = new ArrayList<>(myParameterDecs.size());
        Collections.copy(newParameterDecs, myParameterDecs);
        List<UsesItem> newUsesItems = new ArrayList<>(myUsesItems.size());
        Collections.copy(newUsesItems, myUsesItems);
        List<Dec> newDecs = new ArrayList<>(myDecs.size());
        Collections.copy(newDecs, myDecs);

        return new PrecisModuleDec(new Location(myLoc), myName.clone(), newParameterDecs, newUsesItems, newDecs);
    }
}
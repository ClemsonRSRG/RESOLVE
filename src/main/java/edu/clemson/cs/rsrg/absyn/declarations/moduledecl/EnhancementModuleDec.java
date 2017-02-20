/**
 * EnhancementModuleDec.java
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

import edu.clemson.cs.rsrg.absyn.clauses.AssertionClause;
import edu.clemson.cs.rsrg.absyn.declarations.Dec;
import edu.clemson.cs.rsrg.absyn.declarations.paramdecl.ModuleParameterDec;
import edu.clemson.cs.rsrg.absyn.items.programitems.UsesItem;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import java.util.*;

/**
 * <p>This is the class for the enhancement module declarations
 * that the compiler builds using the ANTLR4 AST nodes.</p>
 *
 * @version 2.0
 */
public class EnhancementModuleDec extends ModuleDec {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The concept module associated with this module</p> */
    private final PosSymbol myConceptName;

    /** <p>The requires expression</p> */
    private final AssertionClause myRequires;

    // ===========================================================
    // Constructor
    // ===========================================================

    /**
     * <p>This constructor creates a "Enhancement" module representation.</p>
     *
     * @param l A {@link Location} representation object.
     * @param name The name in {@link PosSymbol} format.
     * @param parameterDecs The list of {@link ModuleParameterDec} objects.
     * @param conceptName The concept name in {@link PosSymbol} format.
     * @param usesItems The list of {@link UsesItem} objects.
     * @param requires A {@link AssertionClause} representing the concept's
     *                 requires clause.
     * @param decs The list of {@link Dec} objects.
     * @param moduleDependencies A set of {@link PosSymbol} that indicates
     *                           all the modules that this module declaration
     *                           depends on.
     */
    public EnhancementModuleDec(Location l, PosSymbol name,
            List<ModuleParameterDec> parameterDecs, PosSymbol conceptName,
            List<UsesItem> usesItems, AssertionClause requires, List<Dec> decs,
            Set<PosSymbol> moduleDependencies) {
        super(l, name, parameterDecs, usesItems, decs, moduleDependencies);
        myConceptName = conceptName;
        myRequires = requires;
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

        sb.append("Enhancement ");
        sb.append(formNameArgs(0, innerIndentInc));
        sb.append(" for ");
        sb.append(myConceptName.asString(0, innerIndentInc));
        sb.append(";\n");
        sb.append(formUses(indentSize, innerIndentInc));
        sb.append(myRequires.asString(indentSize + innerIndentInc,
                innerIndentInc));
        sb.append("\n\n");
        sb.append(formDecEnd(indentSize, innerIndentInc));

        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;

        EnhancementModuleDec that = (EnhancementModuleDec) o;

        if (!myConceptName.equals(that.myConceptName))
            return false;
        return myRequires.equals(that.myRequires);
    }

    /**
     * <p>This method returns the symbol representation
     * of for the concept name.</p>
     *
     * @return The name in {@link PosSymbol} format.
     */
    public final PosSymbol getConceptName() {
        return myConceptName;
    }

    /**
     * <p>This method returns the requires clause
     * for this enhancement declaration.</p>
     *
     * @return The {@link AssertionClause} representation object.
     */
    public final AssertionClause getRequires() {
        return myRequires;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        int result = super.hashCode();
        result = 31 * result + myConceptName.hashCode();
        result = 31 * result + myRequires.hashCode();
        return result;
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * {@inheritDoc}
     */
    @Override
    protected final EnhancementModuleDec copy() {
        // Copy all the items in the lists
        List<ModuleParameterDec> newParameterDecs = new ArrayList<>(myParameterDecs.size());
        Collections.copy(newParameterDecs, myParameterDecs);
        List<UsesItem> newUsesItems = new ArrayList<>(myUsesItems.size());
        Collections.copy(newUsesItems, myUsesItems);
        List<Dec> newDecs = new ArrayList<>(myDecs.size());
        Collections.copy(newDecs, myDecs);
        Set<PosSymbol> newModuleDependencies = new HashSet<>(myModuleDependencies.size());
        newModuleDependencies.addAll(myModuleDependencies);

        return new EnhancementModuleDec(cloneLocation(), myName.clone(), newParameterDecs,
                myConceptName.clone(), newUsesItems, myRequires.clone(),
                newDecs, newModuleDependencies);
    }
}
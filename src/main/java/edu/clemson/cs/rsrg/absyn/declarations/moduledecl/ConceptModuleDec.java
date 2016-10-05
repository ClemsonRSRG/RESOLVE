/**
 * ConceptModuleDec.java
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>This is the class for the concept module declarations
 * that the compiler builds using the ANTLR4 AST nodes.</p>
 *
 * @version 2.0
 */
public class ConceptModuleDec extends ModuleDec {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The requires expression</p> */
    private final AssertionClause myRequires;

    /**
     * <p>The list of concept level constraints.</p>
     *
     * <p>Note that placing this down here means it is processed last by the
     * treewalker, and so we will have access to any definitions in the
     * body of the concept.</p>
     */
    private final List<AssertionClause> myConstraints;

    // ===========================================================
    // Constructor
    // ===========================================================

    /**
     * <p>This constructor creates a "Concept" module representation.</p>
     *
     * @param l A {@link Location} representation object.
     * @param name The name in {@link PosSymbol} format.
     * @param parameterDecs The list of {@link ModuleParameterDec} objects.
     * @param usesItems The list of {@link UsesItem} objects.
     * @param requires A {@link AssertionClause} representing the concept's
     *                 requires clause.
     * @param constraints The list of {@link AssertionClause} representing the concept
     *                    level constraints.
     * @param decs The list of {@link Dec} objects.
     */
    public ConceptModuleDec(Location l, PosSymbol name,
            List<ModuleParameterDec> parameterDecs, List<UsesItem> usesItems,
            AssertionClause requires, List<AssertionClause> constraints,
            List<Dec> decs) {
        super(l, name, parameterDecs, usesItems, decs);
        myConstraints = constraints;
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

        sb.append("Concept ");
        sb.append(formNameArgs(0, innerIndentInc));
        sb.append(";\n");
        sb.append(formUses(indentSize, innerIndentInc));
        sb.append(myRequires.asString(indentSize + innerIndentInc,
                innerIndentInc));
        sb.append("\n");

        for (AssertionClause constraint : myConstraints) {
            sb.append("\n");
            sb.append(constraint.asString(indentSize + innerIndentInc,
                    innerIndentInc));
            sb.append("\n\n");
        }

        sb.append("\n");
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

        ConceptModuleDec that = (ConceptModuleDec) o;

        if (!myRequires.equals(that.myRequires))
            return false;
        return myConstraints.equals(that.myConstraints);
    }

    /**
     * <p>This method returns the concept level constraints.</p>
     *
     * @return A list of {@link AssertionClause} representation objects.
     */
    public final List<AssertionClause> getConstraints() {
        return myConstraints;
    }

    /**
     * <p>This method returns the requires clause
     * for this concept declaration.</p>
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
        result = 31 * result + myRequires.hashCode();
        result = 31 * result + myConstraints.hashCode();
        return result;
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * {@inheritDoc}
     */
    @Override
    protected final ConceptModuleDec copy() {
        // Copy all the items in the lists
        List<ModuleParameterDec> newParameterDecs = new ArrayList<>(myParameterDecs.size());
        Collections.copy(newParameterDecs, myParameterDecs);
        List<UsesItem> newUsesItems = new ArrayList<>(myUsesItems.size());
        Collections.copy(newUsesItems, myUsesItems);
        List<Dec> newDecs = new ArrayList<>(myDecs.size());
        Collections.copy(newDecs, myDecs);
        List<AssertionClause> newConstraints = new ArrayList<>(myConstraints.size());
        Collections.copy(newConstraints, myConstraints);

        return new ConceptModuleDec(cloneLocation(), myName.clone(), newParameterDecs,
                newUsesItems, myRequires.clone(), newConstraints, newDecs);
    }
}
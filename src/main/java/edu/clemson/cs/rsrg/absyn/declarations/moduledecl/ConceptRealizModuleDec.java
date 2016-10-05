/**
 * ConceptRealizModuleDec.java
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
 * <p>This is the class for the concept realization module declarations
 * that the compiler builds using the ANTLR4 AST nodes.</p>
 *
 * @version 2.0
 */
public class ConceptRealizModuleDec extends ModuleDec {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The performance profile associated with this module</p> */
    private final PosSymbol myProfileName;

    /** <p>The concept module associated with this module</p> */
    private final PosSymbol myConceptName;

    /** <p>The requires expression</p> */
    private final AssertionClause myRequires;

    // ===========================================================
    // Constructor
    // ===========================================================

    /**
     * <p>This constructor creates a "Concept Realization"
     * module representation.</p>
     *
     * @param l A {@link Location} representation object.
     * @param name The name in {@link PosSymbol} format.
     * @param parameterDecs The list of {@link ModuleParameterDec} objects.
     * @param profileName The performance profile name (if any) in {@link PosSymbol} format.
     * @param conceptName The concept name in {@link PosSymbol} format.
     * @param usesItems The list of {@link UsesItem} objects.
     * @param requires A {@link AssertionClause} representing the concept's
     *                 requires clause.
     * @param decs The list of {@link Dec} objects.
     */
    public ConceptRealizModuleDec(Location l, PosSymbol name,
            List<ModuleParameterDec> parameterDecs, PosSymbol profileName,
            PosSymbol conceptName, List<UsesItem> usesItems,
            AssertionClause requires, List<Dec> decs) {
        super(l, name, parameterDecs, usesItems, decs);
        myConceptName = conceptName;
        myProfileName = profileName;
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

        sb.append("Realization ");
        sb.append(formNameArgs(0, innerIndentInc));

        if (myProfileName != null) {
            sb.append(" with_profile ");
            sb.append(myProfileName.asString(0, innerIndentInc));
        }

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

        ConceptRealizModuleDec that = (ConceptRealizModuleDec) o;

        if (myProfileName != null ? !myProfileName.equals(that.myProfileName)
                : that.myProfileName != null)
            return false;
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
     * <p>This method returns the symbol representation
     * of for the profile name.</p>
     *
     * @return The name in {@link PosSymbol} format.
     */
    public final PosSymbol getProfileName() {
        return myProfileName;
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
        result =
                31
                        * result
                        + (myProfileName != null ? myProfileName.hashCode() : 0);
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
    protected final ConceptRealizModuleDec copy() {
        // Copy all the items in the lists
        List<ModuleParameterDec> newParameterDecs = new ArrayList<>(myParameterDecs.size());
        Collections.copy(newParameterDecs, myParameterDecs);
        List<UsesItem> newUsesItems = new ArrayList<>(myUsesItems.size());
        Collections.copy(newUsesItems, myUsesItems);
        List<Dec> newDecs = new ArrayList<>(myDecs.size());
        Collections.copy(newDecs, myDecs);

        // Copy the profile name
        PosSymbol newProfileName = null;
        if (myProfileName != null) {
            newProfileName = myProfileName.clone();
        }

        return new ConceptRealizModuleDec(cloneLocation(), myName.clone(), newParameterDecs,
                newProfileName, myConceptName.clone(), newUsesItems, myRequires.clone(), newDecs);
    }
}
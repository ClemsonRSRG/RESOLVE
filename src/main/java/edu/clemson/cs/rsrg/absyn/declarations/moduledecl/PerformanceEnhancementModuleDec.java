/**
 * PerformanceEnhancementModuleDec.java
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
 * <p>This is the class for the performance profiles for enhancement module
 * declarations that the compiler builds using the ANTLR4 AST nodes.</p>
 *
 * @version 2.0
 */
public class PerformanceEnhancementModuleDec extends ModuleDec {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>The complete name for the performance profile
     * associated with this module</p>
     */
    private final PosSymbol myProfileLongName;

    /** <p>The enhancement module associated with this module</p> */
    private final PosSymbol myEnhancementName;

    /** <p>The concept module associated with this module</p> */
    private final PosSymbol myConceptName;

    /**
     * <p>The performance profile for the concept module
     * associated with this module</p>
     */
    private final PosSymbol myConceptProfileName;

    /** <p>The requires expression</p> */
    private final AssertionClause myRequires;

    // ===========================================================
    // Constructor
    // ===========================================================

    /**
     * <p>This constructor creates a "Performance Profile" for an {@code Enhancement}
     * module representation.</p>
     *
     * @param l A {@link Location} representation object.
     * @param name The short profile name in {@link PosSymbol} format.
     * @param parameterDecs The list of {@link ModuleParameterDec} objects.
     * @param profileLongName The long profile name in {@link PosSymbol} format.
     * @param enhancementName The enhancement name in {@link PosSymbol} format.
     * @param conceptName The concept name in {@link PosSymbol} format.
     * @param conceptProfileName The concept profile name in {@link PosSymbol} format.
     * @param usesItems The list of {@link UsesItem} objects.
     * @param requires A {@link AssertionClause} representing the concept's
     *                 requires clause.
     * @param decs The list of {@link Dec} objects.
     */
    public PerformanceEnhancementModuleDec(Location l, PosSymbol name,
            List<ModuleParameterDec> parameterDecs, PosSymbol profileLongName,
            PosSymbol enhancementName, PosSymbol conceptName,
            PosSymbol conceptProfileName, List<UsesItem> usesItems,
            AssertionClause requires, List<Dec> decs) {
        super(l, name, parameterDecs, usesItems, decs);
        myConceptName = conceptName;
        myConceptProfileName = conceptProfileName;
        myEnhancementName = enhancementName;
        myProfileLongName = profileLongName;
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

        sb.append("Profile ");
        sb.append(formNameArgs(0, innerIndentInc));
        sb.append(" short_for ");
        sb.append(myProfileLongName.asString(0, innerIndentInc));
        sb.append(" for ");
        sb.append(myEnhancementName.asString(0, innerIndentInc));
        sb.append(" of ");
        sb.append(myConceptName.asString(0, innerIndentInc));
        sb.append(" with_profile ");
        sb.append(myConceptProfileName.asString(0, innerIndentInc));
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

        PerformanceEnhancementModuleDec that =
                (PerformanceEnhancementModuleDec) o;

        if (!myProfileLongName.equals(that.myProfileLongName))
            return false;
        if (!myEnhancementName.equals(that.myEnhancementName))
            return false;
        if (!myConceptName.equals(that.myConceptName))
            return false;
        if (!myConceptProfileName.equals(that.myConceptProfileName))
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
     * of for the concept's profile name.</p>
     *
     * @return The name in {@link PosSymbol} format.
     */
    public final PosSymbol getConceptProfileName() {
        return myConceptProfileName;
    }

    /**
     * <p>This method returns the symbol representation
     * of for the enhancement name.</p>
     *
     * @return The name in {@link PosSymbol} format.
     */
    public final PosSymbol getEnhancementName() {
        return myEnhancementName;
    }

    /**
     * <p>This method returns the symbol representation
     * of for the long version of the profile name.</p>
     *
     * @return The name in {@link PosSymbol} format.
     */
    public final PosSymbol getProfileLongName() {
        return myProfileLongName;
    }

    /**
     * <p>This method returns the requires clause
     * for this performance profile declaration.</p>
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
        result = 31 * result + myProfileLongName.hashCode();
        result = 31 * result + myEnhancementName.hashCode();
        result = 31 * result + myConceptName.hashCode();
        result = 31 * result + myConceptProfileName.hashCode();
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
    protected final PerformanceEnhancementModuleDec copy() {
        // Copy all the items in the lists
        List<ModuleParameterDec> newParameterDecs = new ArrayList<>(myParameterDecs.size());
        Collections.copy(newParameterDecs, myParameterDecs);
        List<UsesItem> newUsesItems = new ArrayList<>(myUsesItems.size());
        Collections.copy(newUsesItems, myUsesItems);
        List<Dec> newDecs = new ArrayList<>(myDecs.size());
        Collections.copy(newDecs, myDecs);

        return new PerformanceEnhancementModuleDec(new Location(myLoc), myName.clone(), newParameterDecs,
                myProfileLongName.clone(), myEnhancementName.clone(), myConceptName.clone(),
                myConceptProfileName.clone(), newUsesItems, myRequires.clone(), newDecs);
    }
}
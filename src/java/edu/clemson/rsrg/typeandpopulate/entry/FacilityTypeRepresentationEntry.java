/*
 * FacilityTypeRepresentationEntry.java
 * ---------------------------------
 * Copyright (c) 2023
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.rsrg.typeandpopulate.entry;

import edu.clemson.rsrg.absyn.ResolveConceptualElement;
import edu.clemson.rsrg.absyn.clauses.AssertionClause;
import edu.clemson.rsrg.absyn.expressions.mathexpr.VarExp;
import edu.clemson.rsrg.parsing.data.Location;
import edu.clemson.rsrg.statushandling.exception.SourceErrorException;
import edu.clemson.rsrg.typeandpopulate.programtypes.PTType;
import edu.clemson.rsrg.typeandpopulate.utilities.ModuleIdentifier;

/**
 * <p>
 * This creates a symbol table entry for a program type representation defined in a {@code Facility}.
 * </p>
 *
 * @version 2.0
 */
public class FacilityTypeRepresentationEntry extends TypeRepresentationEntry {

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This creates a symbol table entry for a facility type representation definition.
     * </p>
     *
     * @param name
     *            Name associated with this entry.
     * @param definingElement
     *            The element that created this entry.
     * @param sourceModule
     *            The module where this entry was created from.
     * @param representation
     *            The program type used to implement this entry.
     * @param convention
     *            The mathematical convention expression for this entry.
     */
    public FacilityTypeRepresentationEntry(String name, ResolveConceptualElement definingElement,
            ModuleIdentifier sourceModule, PTType representation, AssertionClause convention) {
        super(name, definingElement, sourceModule, null, representation, convention,
                new AssertionClause(definingElement.getLocation().clone(), AssertionClause.ClauseType.CONVENTION,
                        VarExp.getTrueVarExp(definingElement.getLocation().clone(), representation.getTypeGraph())));
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * This method returns a description associated with this entry.
     * </p>
     *
     * @return A string.
     */
    @Override
    public final String getEntryTypeDescription() {
        return "a facility type representation definition";
    }

    /**
     * <p>
     * This method will attempt to convert this {@link SymbolTableEntry} into a {@link FacilityTypeRepresentationEntry}.
     * </p>
     *
     * @param l
     *            Location where we encountered this entry.
     *
     * @return A {@link FacilityTypeRepresentationEntry} if possible. Otherwise, it throws a
     *         {@link SourceErrorException}.
     */
    @Override
    public final FacilityTypeRepresentationEntry toFacilityTypeRepresentationEntry(Location l) {
        return this;
    }

}

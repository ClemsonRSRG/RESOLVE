/*
 * OperationProfileEntry.java
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
package edu.clemson.cs.rsrg.typeandpopulate.entry;

import edu.clemson.cs.rsrg.absyn.ResolveConceptualElement;
import edu.clemson.cs.rsrg.absyn.clauses.AssertionClause;
import edu.clemson.cs.rsrg.absyn.declarations.operationdecl.PerformanceOperationDec;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.statushandling.exception.SourceErrorException;
import edu.clemson.cs.rsrg.typeandpopulate.programtypes.PTType;
import edu.clemson.cs.rsrg.typeandpopulate.utilities.ModuleIdentifier;
import java.util.Map;

/**
 * <p>
 * This creates a symbol table entry for a performance profile for an operation.
 * </p>
 *
 * @version 2.0
 */
public class OperationProfileEntry extends SymbolTableEntry {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * The operation entry associated with this entry.
     * </p>
     */
    private final OperationEntry myCorrespondingOperation;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This creates a symbol table entry for a performance profile for an
     * operation.
     * </p>
     *
     * @param name Name associated with this entry.
     * @param definingElement The element that created this entry.
     * @param sourceModule The module where this entry was created from.
     * @param correspondingOperation The operation entry associated with this
     *        entry.
     */
    public OperationProfileEntry(String name,
            ResolveConceptualElement definingElement,
            ModuleIdentifier sourceModule,
            OperationEntry correspondingOperation) {
        super(name, definingElement, sourceModule);
        myCorrespondingOperation = correspondingOperation;
    }

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This method returns the operation entry associated with this entry.
     * </p>
     *
     * @return An {@link OperationEntry} representation object.
     */
    public final OperationEntry getCorrespondingOperation() {
        return myCorrespondingOperation;
    }

    /**
     * <p>
     * This method returns the duration clause associated with this entry.
     * </p>
     *
     * @return An {@link AssertionClause} representation object.
     */
    public final AssertionClause getDurationClause() {
        return ((PerformanceOperationDec) getDefiningElement()).getDuration()
                .clone();
    }

    /**
     * <p>
     * This method returns the ensures clause associated with this entry.
     * </p>
     *
     * @return An {@link AssertionClause} representation object.
     */
    public final AssertionClause getEnsuresClause() {
        return ((PerformanceOperationDec) getDefiningElement())
                .getWrappedOpDec().getEnsures().clone();
    }

    /**
     * <p>
     * This method returns a description associated with this entry.
     * </p>
     *
     * @return A string.
     */
    @Override
    public final String getEntryTypeDescription() {
        return "the profile of an operation";
    }

    /**
     * <p>
     * This method returns the manipulation displacement clause associated with
     * this entry.
     * </p>
     *
     * @return An {@link AssertionClause} representation object.
     */
    public final AssertionClause getManipDispClause() {
        return ((PerformanceOperationDec) getDefiningElement()).getManipDisp()
                .clone();
    }

    /**
     * <p>
     * This method converts a generic {@link SymbolTableEntry} to an entry that
     * has all the generic
     * types and variables replaced with actual values.
     * </p>
     *
     * @param genericInstantiations Map containing all the instantiations.
     * @param instantiatingFacility Facility that instantiated this type.
     *
     * @return A {@link SymbolTableEntry} that has been instantiated.
     */
    @Override
    public final SymbolTableEntry instantiateGenerics(
            Map<String, PTType> genericInstantiations,
            FacilityEntry instantiatingFacility) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * <p>
     * This method will attempt to convert this {@link SymbolTableEntry} into a
     * {@link OperationProfileEntry}.
     * </p>
     *
     * @param l Location where we encountered this entry.
     *
     * @return A {@link OperationProfileEntry} if possible. Otherwise, it throws
     *         a
     *         {@link SourceErrorException}.
     */
    @Override
    public final OperationProfileEntry toOperationProfileEntry(Location l) {
        return this;
    }

}

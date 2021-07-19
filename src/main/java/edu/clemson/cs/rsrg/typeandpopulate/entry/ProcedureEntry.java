/*
 * ProcedureEntry.java
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
package edu.clemson.cs.rsrg.typeandpopulate.entry;

import edu.clemson.cs.rsrg.absyn.ResolveConceptualElement;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.statushandling.exception.SourceErrorException;
import edu.clemson.cs.rsrg.typeandpopulate.programtypes.PTType;
import edu.clemson.cs.rsrg.typeandpopulate.utilities.ModuleIdentifier;
import java.util.Map;

/**
 * <p>
 * This creates a symbol table entry for a procedure declaration for an
 * operation.
 * </p>
 *
 * @version 2.0
 */
public class ProcedureEntry extends SymbolTableEntry {

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
     * This creates a symbol table entry for a procedure declaration for an
     * operation.
     * </p>
     *
     * @param name Name associated with this entry.
     * @param definingElement The element that created this entry.
     * @param sourceModule The module where this entry was created from.
     * @param correspondingOperation The operation entry associated with this
     *        entry.
     */
    public ProcedureEntry(String name, ResolveConceptualElement definingElement,
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
     * This method returns a description associated with this entry.
     * </p>
     *
     * @return A string.
     */
    @Override
    public final String getEntryTypeDescription() {
        return "a procedure";
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
     * @return A {@link ProcedureEntry} that has been instantiated.
     */
    @Override
    public final ProcedureEntry instantiateGenerics(
            Map<String, PTType> genericInstantiations,
            FacilityEntry instantiatingFacility) {
        return new ProcedureEntry(getName(), getDefiningElement(),
                getSourceModuleIdentifier(),
                myCorrespondingOperation.instantiateGenerics(
                        genericInstantiations, instantiatingFacility));
    }

    /**
     * <p>
     * This method will attempt to convert this {@link SymbolTableEntry} into a
     * {@link ProcedureEntry}.
     * </p>
     *
     * @param l Location where we encountered this entry.
     *
     * @return A {@link ProcedureEntry} if possible. Otherwise, it throws a
     *         {@link SourceErrorException}.
     */
    @Override
    public final ProcedureEntry toProcedureEntry(Location l) {
        return this;
    }

}

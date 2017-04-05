/*
 * OperationEntry.java
 * ---------------------------------
 * Copyright (c) 2017
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.typeandpopulate.entry;

import edu.clemson.cs.r2jt.misc.Utils;
import edu.clemson.cs.r2jt.rewriteprover.immutableadts.ArrayBackedImmutableList;
import edu.clemson.cs.r2jt.rewriteprover.immutableadts.ImmutableList;
import edu.clemson.cs.r2jt.rewriteprover.immutableadts.LazilyMappedImmutableList;
import edu.clemson.cs.rsrg.absyn.ResolveConceptualElement;
import edu.clemson.cs.rsrg.absyn.clauses.AffectsClause;
import edu.clemson.cs.rsrg.absyn.clauses.AssertionClause;
import edu.clemson.cs.rsrg.absyn.declarations.operationdecl.OperationDec;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.statushandling.exception.SourceErrorException;
import edu.clemson.cs.rsrg.typeandpopulate.programtypes.PTType;
import edu.clemson.cs.rsrg.typeandpopulate.utilities.ModuleIdentifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>This creates a symbol table entry for an operation.</p>
 *
 * @version 2.0
 */
public class OperationEntry extends SymbolTableEntry {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The program type associated with this entry's return value.</p> */
    private final PTType myReturnType;

    /** <p>The program parameters associated with this entry.</p> */
    private final ImmutableList<ProgramParameterEntry> myParameters;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This creates a symbol table entry for an operation.</p>
     *
     * @param name Name associated with this entry.
     * @param definingElement The element that created this entry.
     * @param sourceModule The module where this entry was created from.
     * @param returnType The program type associated with this entry's return value.
     * @param parameters The program parameters associated with this entry.
     */
    public OperationEntry(String name, ResolveConceptualElement definingElement,
                          ModuleIdentifier sourceModule, PTType returnType,
                          List<ProgramParameterEntry> parameters) {
        this(name, definingElement, sourceModule, returnType,
                new ArrayBackedImmutableList<>(parameters));
    }

    /**
     * <p>This creates a symbol table entry for an operation.</p>
     *
     * @param name Name associated with this entry.
     * @param definingElement The element that created this entry.
     * @param sourceModule The module where this entry was created from.
     * @param returnType The program type associated with this entry's return value.
     * @param parameters The program parameters associated with this entry.
     */
    public OperationEntry(String name,
            ResolveConceptualElement definingElement,
            ModuleIdentifier sourceModule, PTType returnType,
            ImmutableList<ProgramParameterEntry> parameters) {
        super(name, definingElement, sourceModule);

        myParameters = parameters;
        myReturnType = returnType;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This method returns the affects clause associated with this entry.</p>
     *
     * @return An {@link AffectsClause} representation object.
     */
    public final AffectsClause getAffectsClause() {
        return ((OperationDec) getDefiningElement()).getAffectedVars().clone();
    }

    /**
     * <p>This method returns the ensures clause associated with this entry.</p>
     *
     * @return An {@link AssertionClause} representation object.
     */
    public final AssertionClause getEnsuresClause() {
        return ((OperationDec) getDefiningElement()).getEnsures().clone();
    }

    /**
     * <p>This method returns a description associated with this entry.</p>
     *
     * @return A string.
     */
    @Override
    public final String getEntryTypeDescription() {
        return "an operation";
    }

    /**
     * <p>This method returns the program parameters associated with this entry.</p>
     *
     * @return An immutable list containing {@link ProgramParameterEntry} objects.
     */
    public final ImmutableList<ProgramParameterEntry> getParameters() {
        return myParameters;
    }

    /**
     * <p>This method returns the requires clause associated with this entry.</p>
     *
     * @return An {@link AssertionClause} representation object.
     */
    public final AssertionClause getRequiresClause() {
        return ((OperationDec) getDefiningElement()).getRequires().clone();
    }

    /**
     * <p>This method returns the program type associated with this entry's return value.</p>
     *
     * @return An immutable list containing {@link ProgramParameterEntry} objects.
     */
    public final PTType getReturnType() {
        return myReturnType;
    }

    /**
     * <p>This method converts a generic {@link SymbolTableEntry} to an entry
     * that has all the generic types and variables replaced with actual
     * values.</p>
     *
     * @param genericInstantiations Map containing all the instantiations.
     * @param instantiatingFacility Facility that instantiated this type.
     *
     * @return A {@link OperationEntry} that has been instantiated.
     */
    @Override
    public final OperationEntry instantiateGenerics(
            Map<String, PTType> genericInstantiations, FacilityEntry instantiatingFacility) {
        return new OperationEntry(getName(), getDefiningElement(),
                getSourceModuleIdentifier(),
                myReturnType.instantiateGenerics(genericInstantiations, instantiatingFacility),
                new LazilyMappedImmutableList<>(myParameters,
                        new InstantiationMapping(genericInstantiations, instantiatingFacility)));
    }

    /**
     * <p>This method will attempt to convert this {@link SymbolTableEntry}
     * into a {@link OperationEntry}.</p>
     *
     * @param l Location where we encountered this entry.
     *
     * @return A {@link OperationEntry} if possible. Otherwise,
     * it throws a {@link SourceErrorException}.
     */
    @Override
    public final OperationEntry toOperationEntry(Location l) {
        return this;
    }

    // ===========================================================
    // Helper Constructs
    // ===========================================================

    /**
     * <p>This is a helper class that provides an instantiation mapping
     * for {@link ProgramParameterEntry}s.</p>
     */
    private static class InstantiationMapping
            implements
                Utils.Mapping<ProgramParameterEntry, ProgramParameterEntry> {

        // ===========================================================
        // Member Fields
        // ===========================================================

        /** <p>A map of generic instantiations.</p> */
        private final Map<String, PTType> myGenericInstantiations;

        /** <p>The facility entry that is instantiating this class</p> */
        private final FacilityEntry myInstantiatingFacility;

        // ===========================================================
        // Constructors
        // ===========================================================

        /**
         * <p>This constructs a mapping between the generic instantiations
         * and a instantiating facility.</p>
         *
         * @param instantiations Map containing all the instantiations.
         * @param instantiatingFacility Facility that instantiated this type.
         */
        InstantiationMapping(Map<String, PTType> instantiations, FacilityEntry instantiatingFacility) {
            myGenericInstantiations = new HashMap<>(instantiations);
            myInstantiatingFacility = instantiatingFacility;
        }

        // ===========================================================
        // Public Methods
        // ===========================================================

        /**
         * <p>This method instantiates a generic {@link ProgramParameterEntry}.</p>
         *
         * @param input A generic {@link ProgramParameterEntry}.
         *
         * @return The instantiated {@link ProgramParameterEntry}.
         */
        @Override
        public final ProgramParameterEntry map(ProgramParameterEntry input) {
            return input.instantiateGenerics(myGenericInstantiations,
                    myInstantiatingFacility);
        }

    }

}
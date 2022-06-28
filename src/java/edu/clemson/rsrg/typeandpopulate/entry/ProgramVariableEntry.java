/*
 * ProgramVariableEntry.java
 * ---------------------------------
 * Copyright (c) 2022
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
import edu.clemson.rsrg.parsing.data.Location;
import edu.clemson.rsrg.statushandling.exception.SourceErrorException;
import edu.clemson.rsrg.typeandpopulate.programtypes.PTType;
import edu.clemson.rsrg.typeandpopulate.utilities.ModuleIdentifier;
import java.util.Map;

/**
 * <p>
 * This creates a symbol table entry for a program variable.
 * </p>
 *
 * @version 2.0
 */
public class ProgramVariableEntry extends SymbolTableEntry {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * The program type assigned to this entry.
     * </p>
     */
    private final PTType myType;

    /**
     * <p>
     * The mathematical symbol entry associated with this entry.
     * </p>
     */
    private final MathSymbolEntry myMathSymbolAlterEgo;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This creates a symbol table entry for a program variable.
     * </p>
     *
     * @param name
     *            Name associated with this entry.
     * @param definingElement
     *            The element that created this entry.
     * @param sourceModule
     *            The module where this entry was created from.
     * @param type
     *            The program type assigned to this entry.
     */
    public ProgramVariableEntry(String name, ResolveConceptualElement definingElement, ModuleIdentifier sourceModule,
            PTType type) {
        super(name, definingElement, sourceModule);

        myType = type;

        // TODO: Probably need to recajigger this to correctly account for any
        // generics in the defining context
        myMathSymbolAlterEgo = new MathSymbolEntry(type.getTypeGraph(), name, Quantification.NONE, definingElement,
                type.toMath(), null, null, null, sourceModule);
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
    public String getEntryTypeDescription() {
        return "a program variable";
    }

    /**
     * <p>
     * This method returns the program type associated with this entry.
     * </p>
     *
     * @return A {@link PTType} representation object.
     */
    public final PTType getProgramType() {
        return myType;
    }

    /**
     * <p>
     * This method converts a generic {@link SymbolTableEntry} to an entry that has all the generic types and variables
     * replaced with actual values.
     * </p>
     *
     * @param genericInstantiations
     *            Map containing all the instantiations.
     * @param instantiatingFacility
     *            Facility that instantiated this type.
     *
     * @return A {@link SymbolTableEntry} that has been instantiated.
     */
    @Override
    public final SymbolTableEntry instantiateGenerics(Map<String, PTType> genericInstantiations,
            FacilityEntry instantiatingFacility) {
        SymbolTableEntry result;

        PTType instantiatedType = myType.instantiateGenerics(genericInstantiations, instantiatingFacility);

        if (instantiatedType != myType) {
            result = new ProgramVariableEntry(getName(), getDefiningElement(), getSourceModuleIdentifier(),
                    instantiatedType);
        } else {
            result = this;
        }

        return result;
    }

    /**
     * <p>
     * This method will attempt to convert this {@link SymbolTableEntry} into a {@link MathSymbolEntry}.
     * </p>
     *
     * @param l
     *            Location where we encountered this entry.
     *
     * @return A {@link MathSymbolEntry} if possible. Otherwise, it throws a {@link SourceErrorException}.
     */
    @Override
    public final MathSymbolEntry toMathSymbolEntry(Location l) {
        return myMathSymbolAlterEgo;
    }

    /**
     * <p>
     * This method will attempt to convert this {@link SymbolTableEntry} into a {@link ProgramVariableEntry}.
     * </p>
     *
     * @param l
     *            Location where we encountered this entry.
     *
     * @return A {@link ProgramVariableEntry} if possible. Otherwise, it throws a {@link SourceErrorException}.
     */
    @Override
    public final ProgramVariableEntry toProgramVariableEntry(Location l) {
        return this;
    }

}

/*
 * TypeRepresentationEntry.java
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
import edu.clemson.rsrg.absyn.clauses.AssertionClause;
import edu.clemson.rsrg.parsing.data.Location;
import edu.clemson.rsrg.statushandling.exception.SourceErrorException;
import edu.clemson.rsrg.typeandpopulate.programtypes.PTType;
import edu.clemson.rsrg.typeandpopulate.utilities.ModuleIdentifier;
import java.util.Map;

/**
 * <p>
 * This creates a symbol table entry for a program type representation defined in a {@code Concept Realization}.
 * </p>
 *
 * @version 2.0
 */
public class TypeRepresentationEntry extends SymbolTableEntry {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * The type family associated with this entry.
     * </p>
     */
    private final TypeFamilyEntry myDefinition;

    /**
     * <p>
     * The program type used to implement this entry.
     * </p>
     */
    private final PTType myRepresentation;

    /**
     * <p>
     * The mathematical convention expression for this entry.
     * </p>
     */
    private final AssertionClause myConvention;

    /**
     * <p>
     * The mathematical correspondence expression for this entry.
     * </p>
     */
    private final AssertionClause myCorrespondence;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This creates a symbol table entry for a type representation definition.
     * </p>
     *
     * @param name
     *            Name associated with this entry.
     * @param definingElement
     *            The element that created this entry.
     * @param sourceModule
     *            The module where this entry was created from.
     * @param definition
     *            The type family associated with this entry.
     * @param representation
     *            The program type used to implement this entry.
     * @param convention
     *            The mathematical convention expression for this entry.
     * @param correspondence
     *            The mathematical correspondence expression for this entry.
     */
    public TypeRepresentationEntry(String name, ResolveConceptualElement definingElement, ModuleIdentifier sourceModule,
            TypeFamilyEntry definition, PTType representation, AssertionClause convention,
            AssertionClause correspondence) {
        super(name, definingElement, sourceModule);
        myDefinition = definition;
        myRepresentation = representation;
        myConvention = convention;
        myCorrespondence = correspondence;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * Since this is used by multiple objects, we really don't want to be returning a reference, therefore this method
     * returns a deep copy of the convention expression.
     * </p>
     *
     * @return A {@link AssertionClause} representation object.
     */
    public final AssertionClause getConvention() {
        return myConvention.clone();
    }

    /**
     * <p>
     * Since this is used by multiple objects, we really don't want to be returning a reference, therefore this method
     * returns a deep copy of the correspondence expression.
     * </p>
     *
     * @return A {@link AssertionClause} representation object.
     */
    public final AssertionClause getCorrespondence() {
        return myCorrespondence.clone();
    }

    /**
     * <p>
     * This method returns the type family associated with this entry.
     * </p>
     *
     * @return A {@link TypeFamilyEntry} representation object.
     */
    public final TypeFamilyEntry getDefiningTypeEntry() {
        return myDefinition;
    }

    /**
     * <p>
     * This method returns a description associated with this entry.
     * </p>
     *
     * @return A string.
     */
    @Override
    public String getEntryTypeDescription() {
        return "a program type representation definition";
    }

    /**
     * <p>
     * This method returns the program type used to implement this entry.
     * </p>
     *
     * @return A {@link PTType} representation object.
     */
    public final PTType getRepresentationType() {
        return myRepresentation;
    }

    /**
     * <p>
     * This method will attempt to convert this {@link SymbolTableEntry} into a {@link ProgramTypeEntry}.
     * </p>
     *
     * @param l
     *            Location where we encountered this entry.
     *
     * @return A {@link ProgramTypeEntry} if possible. Otherwise, it throws a {@link SourceErrorException}.
     */
    @Override
    public final ProgramTypeEntry toProgramTypeEntry(Location l) {
        return new ProgramTypeEntry(myRepresentation.getTypeGraph(), getName(), getDefiningElement(),
                getSourceModuleIdentifier(), myDefinition.getModelType(), myRepresentation);
    }

    /**
     * <p>
     * This method will attempt to convert this {@link SymbolTableEntry} into a {@link TypeRepresentationEntry}.
     * </p>
     *
     * @param l
     *            Location where we encountered this entry.
     *
     * @return A {@link TypeRepresentationEntry} if possible. Otherwise, it throws a {@link SourceErrorException}.
     */
    @Override
    public TypeRepresentationEntry toTypeRepresentationEntry(Location l) {
        return this;
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

        // Representation is an internal implementation detail of a realization
        // and cannot be accessed through a facility instantiation
        throw new UnsupportedOperationException("Cannot instantiate " + this.getClass());
    }

}

/**
 * SymbolTableEntry.java
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
package edu.clemson.cs.rsrg.typeandpopulate.entry;

import edu.clemson.cs.rsrg.absyn.ResolveConceptualElement;
import edu.clemson.cs.rsrg.absyn.declarations.moduledecl.ModuleDec;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.statushandling.exception.SourceErrorException;
import edu.clemson.cs.rsrg.typeandpopulate.mathtypes.MTType;
import edu.clemson.cs.rsrg.typeandpopulate.programtypes.PTType;
import edu.clemson.cs.rsrg.typeandpopulate.utilities.ModuleIdentifier;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>This abstract class serves as the parent class of all
 * symbol table entries.</p>
 *
 * <p>Checklist for subclassing <code>SymbolTableEntry</code>:</p>
 *
 * <ul>
 * 		<li>Create subclass.</li>
 * 		<li>Add "toXXX()" method in this parent class.</li>
 * 		<li>Override it in subclass.</li>
 *      <li>Consider if entry can be coerced to other kinds of entries,
 *      and override those toXXXs as well. (See ProgramVariableEntry
 *      as an example.</li>
 * </ul>
 *
 * @version 2.0
 */
public abstract class SymbolTableEntry {

    // ===========================================================
    // Quantification
    // ===========================================================

    /**
     * <p>This defines the various different quantification options
     * for the expressions in the {@link ResolveConceptualElement}
     * hierarchy.</p>
     *
     * @version 2.0
     */
    public enum Quantification {
        NONE {

            @Override
            public String toString() {
                return "None";
            }

        },
        UNIVERSAL {

            @Override
            public String toString() {
                return "Universal";
            }

        },
        EXISTENTIAL {

            @Override
            public String toString() {
                return "Existential";
            }

        },
        UNIQUE {

            @Override
            public String toString() {
                return "Unique";
            }

        },
    }

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>Name associated with this entry.</p> */
    private final String myName;

    /** <p>Element that created this entry.</p> */
    private final ResolveConceptualElement myDefiningElement;

    /** <p>Module where this entry was created from.</p> */
    private final ModuleIdentifier mySourceModuleIdentifier;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>An helper constructor that allow us to store the name,
     * defining element and source module identifier for
     * any objects created from a class that inherits from
     * {@code SymbolTableEntry}.</p>
     *
     * @param name Name associated with this entry.
     * @param definingElement The element that created this entry.
     * @param sourceModule The module where this entry was created from.
     */
    protected SymbolTableEntry(String name,
            ResolveConceptualElement definingElement,
            ModuleIdentifier sourceModule) {
        myName = name;
        myDefiningElement = definingElement;
        mySourceModuleIdentifier = sourceModule;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This static method returns a map of {@link MTType}s obtained
     * from their associated {@link PTType}s.</p>
     *
     * @param genericInstantiations Map containing all the instantiations.
     *
     * @return A map containing the {@link MTType}s obtained from the
     * instantiated program types.
     */
    public static Map<String, MTType> buildMathTypeGenerics(
            Map<String, PTType> genericInstantiations) {

        Map<String, MTType> genericMathematicalInstantiations =
                new HashMap<>();

        for (Map.Entry<String, PTType> instantiation : genericInstantiations
                .entrySet()) {

            genericMathematicalInstantiations.put(instantiation.getKey(),
                    instantiation.getValue().toMath());
        }

        return genericMathematicalInstantiations;
    }

    /**
     * <p>This method returns the RESOLVE AST node that instantiated
     * this entry.</p>
     *
     * @return The {@link ResolveConceptualElement} that instantiated this
     * entry.
     */
    public final ResolveConceptualElement getDefiningElement() {
        return myDefiningElement;
    }

    /**
     * <p>This method returns a description associated with this entry.</p>
     *
     * @return A string.
     */
    public abstract String getEntryTypeDescription();

    /**
     * <p>This method returns the name associated with this entry.</p>
     *
     * @return A string.
     */
    public final String getName() {
        return myName;
    }

    /**
     * <p>This method returns the module identifier for the {@link ModuleDec}
     * that instantiated this entry.</p>
     *
     * @return A {@link ModuleIdentifier} representation object.
     */
    public final ModuleIdentifier getSourceModuleIdentifier() {
        return mySourceModuleIdentifier;
    }

    /**
     * <p>This method converts a generic {@link SymbolTableEntry} to an entry
     * that has all the generic types and variables replaced with actual
     * values.</p>
     *
     * @param genericInstantiations Map containing all the instantiations.
     * @param instantiatingFacility Facility that instantiated this type.
     *
     * @return A {@link SymbolTableEntry} that has been instantiated.
     */
    public abstract SymbolTableEntry instantiateGenerics(
            Map<String, PTType> genericInstantiations,
            FacilityEntry instantiatingFacility);

    /**
     * <p>This method will attempt to convert this {@link SymbolTableEntry}
     * into a {@link FacilityEntry}.</p>
     *
     * @param l Location where we encountered this entry.
     *
     * @return A {@link FacilityEntry} if possible. Otherwise,
     * it throws a {@link SourceErrorException}.
     */
    public FacilityEntry toFacilityEntry(Location l) {
        throw new SourceErrorException("Expecting a facility. Found "
                + getEntryTypeDescription(), l);
    }

    /**
     * <p>This method will attempt to convert this {@link SymbolTableEntry}
     * into a {@link FacilityTypeRepresentationEntry}.</p>
     *
     * @param l Location where we encountered this entry.
     *
     * @return A {@link FacilityTypeRepresentationEntry} if possible. Otherwise,
     * it throws a {@link SourceErrorException}.
     */
    public FacilityTypeRepresentationEntry toFacilityTypeRepresentationEntry(
            Location l) {
        throw new SourceErrorException("Expecting a facility type "
                + "representation.  Found " + getEntryTypeDescription() + ".",
                l);
    }

    /**
     * <p>This method will attempt to convert this {@link SymbolTableEntry}
     * into a {@link MathSymbolEntry}.</p>
     *
     * @param l Location where we encountered this entry.
     *
     * @return A {@link MathSymbolEntry} if possible. Otherwise,
     * it throws a {@link SourceErrorException}.
     */
    public MathSymbolEntry toMathSymbolEntry(Location l) {
        throw new SourceErrorException("Expecting a math symbol.  Found "
                + getEntryTypeDescription() + ".", l);
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
    public OperationEntry toOperationEntry(Location l) {
        throw new SourceErrorException("Expecting an operation.  Found "
                + getEntryTypeDescription(), l);
    }

    /**
     * <p>This method will attempt to convert this {@link SymbolTableEntry}
     * into a {@link ProgramParameterEntry}.</p>
     *
     * @param l Location where we encountered this entry.
     *
     * @return A {@link ProgramParameterEntry} if possible. Otherwise,
     * it throws a {@link SourceErrorException}.
     */
    public ProgramParameterEntry toProgramParameterEntry(Location l) {
        throw new SourceErrorException("Expecting a program parameter.  "
                + "Found " + getEntryTypeDescription(), l);
    }

    /**
     * <p>This method will attempt to convert this {@link SymbolTableEntry}
     * into a {@link ProgramTypeEntry}.</p>
     *
     * @param l Location where we encountered this entry.
     *
     * @return A {@link ProgramTypeEntry} if possible. Otherwise,
     * it throws a {@link SourceErrorException}.
     */
    public ProgramTypeEntry toProgramTypeEntry(Location l) {
        throw new SourceErrorException("Expecting a program type.  Found "
                + getEntryTypeDescription() + ".", l);
    }

    /**
     * <p>This method will attempt to convert this {@link SymbolTableEntry}
     * into a {@link ProgramVariableEntry}.</p>
     *
     * @param l Location where we encountered this entry.
     *
     * @return A {@link ProgramVariableEntry} if possible. Otherwise,
     * it throws a {@link SourceErrorException}.
     */
    public ProgramVariableEntry toProgramVariableEntry(Location l) {
        throw new SourceErrorException("Expecting a program variable.  "
                + "Found " + getEntryTypeDescription(), l);
    }

    /**
     * <p>This method will attempt to convert this {@link SymbolTableEntry}
     * into a {@link ShortFacilityEntry}.</p>
     *
     * @param l Location where we encountered this entry.
     *
     * @return A {@link ShortFacilityEntry} if possible. Otherwise,
     * it throws a {@link SourceErrorException}.
     */
    public ShortFacilityEntry toShortFacilityEntry(Location l) {
        throw new SourceErrorException("Expecting a short facility module.  "
                + "Found " + getEntryTypeDescription(), l);
    }

    /**
     * <p>This method will attempt to convert this {@link SymbolTableEntry}
     * into a {@link TheoremEntry}.</p>
     *
     * @param l Location where we encountered this entry.
     *
     * @return A {@link TheoremEntry} if possible. Otherwise,
     * it throws a {@link SourceErrorException}.
     */
    public TheoremEntry toTheoremEntry(Location l) {
        throw new SourceErrorException("Expecting a theorem.  " + "Found "
                + getEntryTypeDescription(), l);
    }

    /**
     * <p>This method will attempt to convert this {@link SymbolTableEntry}
     * into a {@link TypeFamilyEntry}.</p>
     *
     * @param l Location where we encountered this entry.
     *
     * @return A {@link TypeFamilyEntry} if possible. Otherwise,
     * it throws a {@link SourceErrorException}.
     */
    public TypeFamilyEntry toTypeFamilyEntry(Location l) {
        throw new SourceErrorException("Expecting a program type family.  "
                + "Found " + getEntryTypeDescription(), l);
    }

    /**
     * <p>This method will attempt to convert this {@link SymbolTableEntry}
     * into a {@link TypeRepresentationEntry}.</p>
     *
     * @param l Location where we encountered this entry.
     *
     * @return A {@link TypeRepresentationEntry} if possible. Otherwise,
     * it throws a {@link SourceErrorException}.
     */
    public TypeRepresentationEntry toTypeRepresentationEntry(Location l) {
        throw new SourceErrorException("Expecting a program type "
                + "representation.  Found " + getEntryTypeDescription() + ".",
                l);
    }

}
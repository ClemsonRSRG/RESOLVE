/*
 * ProgramTypeEntry.java
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
import edu.clemson.cs.rsrg.typeandpopulate.mathtypes.MTType;
import edu.clemson.cs.rsrg.typeandpopulate.programtypes.PTType;
import edu.clemson.cs.rsrg.typeandpopulate.typereasoning.TypeGraph;
import edu.clemson.cs.rsrg.typeandpopulate.typevisitor.VariableReplacingVisitor;
import edu.clemson.cs.rsrg.typeandpopulate.utilities.ModuleIdentifier;
import java.util.Map;

/**
 * <p>
 * This creates a symbol table entry for a program type.
 * </p>
 *
 * @version 2.0
 */
public class ProgramTypeEntry extends SymbolTableEntry {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * The mathematical type assigned to this entry.
     * </p>
     */
    private final MTType myModelType;

    /**
     * <p>
     * The program type assigned to this entry.
     * </p>
     */
    private final PTType myProgramType;

    /**
     * <p>
     * A program type can masquerade as a math type. This will represent the
     * (non-existent) symbol
     * table entry for the "program type" when viewed as a math type.
     * </p>
     */
    private final MathSymbolEntry myMathTypeAlterEgo;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This creates a symbol table entry for a program type.
     * </p>
     *
     * @param g The current type graph.
     * @param name Name associated with this entry.
     * @param definingElement The element that created this entry.
     * @param sourceModule The module where this entry was created from.
     * @param modelType The mathematical type assigned to this entry.
     * @param programType The program type assigned to this entry.
     */
    public ProgramTypeEntry(TypeGraph g, String name,
            ResolveConceptualElement definingElement,
            ModuleIdentifier sourceModule, MTType modelType,
            PTType programType) {
        super(name, definingElement, sourceModule);

        myModelType = modelType;

        // TODO: Probably need to recajigger this to correctly account for any
        // generics in the defining context
        myMathTypeAlterEgo = new MathSymbolEntry(g, name,
                SymbolTableEntry.Quantification.NONE, definingElement, g.SSET,
                modelType, null, null, sourceModule);
        myProgramType = programType;
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
        return "a program type.";
    }

    /**
     * <p>
     * This method returns the mathematical type associated with this entry.
     * </p>
     *
     * @return A {@link MTType} representation object.
     */
    public final MTType getModelType() {
        return myModelType;
    }

    /**
     * <p>
     * This method returns the program type associated with this entry.
     * </p>
     *
     * @return A {@link PTType} representation object.
     */
    public PTType getProgramType() {
        return myProgramType;
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
     * @return A {@link ProgramTypeEntry} that has been instantiated.
     */
    @Override
    public final ProgramTypeEntry instantiateGenerics(
            Map<String, PTType> genericInstantiations,
            FacilityEntry instantiatingFacility) {
        Map<String, MTType> genericMathematicalInstantiations =
                SymbolTableEntry.buildMathTypeGenerics(genericInstantiations);

        VariableReplacingVisitor typeSubstitutor =
                new VariableReplacingVisitor(genericMathematicalInstantiations);
        myModelType.accept(typeSubstitutor);

        return new ProgramTypeEntry(myModelType.getTypeGraph(), getName(),
                getDefiningElement(), getSourceModuleIdentifier(),
                typeSubstitutor.getFinalExpression(),
                myProgramType.instantiateGenerics(genericInstantiations,
                        instantiatingFacility));
    }

    /**
     * <p>
     * This method will attempt to convert this {@link SymbolTableEntry} into a
     * {@link MathSymbolEntry}.
     * </p>
     *
     * @param l Location where we encountered this entry.
     *
     * @return A {@link MathSymbolEntry} if possible. Otherwise, it throws a
     *         {@link SourceErrorException}.
     */
    @Override
    public final MathSymbolEntry toMathSymbolEntry(Location l) {
        return myMathTypeAlterEgo;
    }

    /**
     * <p>
     * This method will attempt to convert this {@link SymbolTableEntry} into a
     * {@link ProgramTypeEntry}.
     * </p>
     *
     * @param l Location where we encountered this entry.
     *
     * @return A {@link ProgramTypeEntry} if possible. Otherwise, it throws a
     *         {@link SourceErrorException}.
     */
    @Override
    public final ProgramTypeEntry toProgramTypeEntry(Location l) {
        return this;
    }

}

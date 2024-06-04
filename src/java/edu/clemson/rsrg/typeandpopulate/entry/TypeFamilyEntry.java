/*
 * TypeFamilyEntry.java
 * ---------------------------------
 * Copyright (c) 2024
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
import edu.clemson.rsrg.absyn.declarations.mathdecl.MathDefVariableDec;
import edu.clemson.rsrg.parsing.data.Location;
import edu.clemson.rsrg.statushandling.exception.SourceErrorException;
import edu.clemson.rsrg.typeandpopulate.mathtypes.MTType;
import edu.clemson.rsrg.typeandpopulate.programtypes.PTFamily;
import edu.clemson.rsrg.typeandpopulate.typereasoning.TypeGraph;
import edu.clemson.rsrg.typeandpopulate.utilities.ModuleIdentifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * This creates a symbol table entry for a {@code Type Family}.
 * </p>
 *
 * @version 2.0
 */
public class TypeFamilyEntry extends ProgramTypeEntry {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * Exemplar associated with this entry.
     * </p>
     */
    private final MathSymbolEntry myExemplar;

    /**
     * <p>
     * The mathematical type constraint for this entry.
     * </p>
     */
    private final AssertionClause myConstraint;

    /**
     * <p>
     * The list of mathematical definition variables for the new type family.
     * </p>
     */
    private final List<MathDefVariableDec> myDefVarList;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This creates a symbol table entry for a type family declaration.
     * </p>
     *
     * @param g
     *            The current type graph.
     * @param name
     *            Name associated with this entry.
     * @param definingElement
     *            The element that created this entry.
     * @param sourceModule
     *            The module where this entry was created from.
     * @param modelType
     *            The mathematical type assigned to this entry.
     * @param programType
     *            The program type assigned to this entry.
     * @param exemplarEntry
     *            The exemplar for this entry.
     * @param constraint
     *            The constraint for this entry.
     * @param defVariableDecs
     *            The list of math definition declarations for this entry.
     */
    public TypeFamilyEntry(TypeGraph g, String name, ResolveConceptualElement definingElement,
            ModuleIdentifier sourceModule, MTType modelType, PTFamily programType, MathSymbolEntry exemplarEntry,
            AssertionClause constraint, List<MathDefVariableDec> defVariableDecs) {
        super(g, name, definingElement, sourceModule, modelType, programType);

        myExemplar = exemplarEntry;
        myConstraint = constraint;
        myDefVarList = defVariableDecs;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;

        TypeFamilyEntry that = (TypeFamilyEntry) o;

        if (!Objects.equals(myExemplar, that.myExemplar))
            return false;
        if (!Objects.equals(myConstraint, that.myConstraint))
            return false;
        return Objects.equals(myDefVarList, that.myDefVarList);
    }

    /**
     * <p>
     * Since this is used by multiple objects, we really don't want to be returning a reference, therefore this method
     * returns a deep copy of the constraint expression.
     * </p>
     *
     * @return A {@link AssertionClause} representation object.
     */
    public final AssertionClause getConstraint() {
        return myConstraint.clone();
    }

    /**
     * <p>
     * Since this is used by multiple objects, we really don't want to be returning a reference, therefore this method
     * returns a deep copy of all the definition variables.
     * </p>
     *
     * @return A list of {@link MathDefVariableDec} representation objects.
     */
    public final List<MathDefVariableDec> getDefinitionVarList() {
        List<MathDefVariableDec> newMathDefVarDecs = new ArrayList<>(myDefVarList.size());
        for (MathDefVariableDec variableDec : myDefVarList) {
            newMathDefVarDecs.add((MathDefVariableDec) variableDec.clone());
        }

        return myDefVarList;
    }

    /**
     * <p>
     * This method returns the exemplar associated with this entry.
     * </p>
     *
     * @return A {@link MathSymbolEntry} representation object.
     */
    public final MathSymbolEntry getExemplar() {
        return myExemplar;
    }

    /**
     * <p>
     * This method returns the program type associated with this entry.
     * </p>
     *
     * @return A {@link PTFamily} representation object.
     */
    @Override
    public final PTFamily getProgramType() {
        return (PTFamily) super.getProgramType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (myExemplar != null ? myExemplar.hashCode() : 0);
        result = 31 * result + (myConstraint != null ? myConstraint.hashCode() : 0);
        result = 31 * result + (myDefVarList != null ? myDefVarList.hashCode() : 0);
        return result;
    }

    /**
     * <p>
     * This method will attempt to convert this {@link SymbolTableEntry} into a {@link TypeFamilyEntry}.
     * </p>
     *
     * @param l
     *            Location where we encountered this entry.
     *
     * @return A {@link TypeFamilyEntry} if possible. Otherwise, it throws a {@link SourceErrorException}.
     */
    @Override
    public final TypeFamilyEntry toTypeFamilyEntry(Location l) {
        return this;
    }

}

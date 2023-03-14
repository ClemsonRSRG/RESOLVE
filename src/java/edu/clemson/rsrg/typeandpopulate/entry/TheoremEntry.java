/*
 * TheoremEntry.java
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

import edu.clemson.rsrg.absyn.declarations.mathdecl.MathAssertionDec;
import edu.clemson.rsrg.absyn.expressions.Exp;
import edu.clemson.rsrg.parsing.data.Location;
import edu.clemson.rsrg.statushandling.exception.SourceErrorException;
import edu.clemson.rsrg.typeandpopulate.programtypes.PTType;
import edu.clemson.rsrg.typeandpopulate.typereasoning.TypeGraph;
import edu.clemson.rsrg.typeandpopulate.utilities.ModuleIdentifier;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * <p>
 * This creates a symbol table entry for a mathematical theorem.
 * </p>
 *
 * @version 2.0
 */
public class TheoremEntry extends SymbolTableEntry {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * The mathematical expression for this entry.
     * </p>
     */
    private final Exp myAssertionExp;

    /**
     * <p>
     * The mathematical symbol entry associated with this entry.
     * </p>
     */
    private final MathSymbolEntry myMathSymbolAlterEgo;

    /**
     * <p>
     * The set of mathematical operators in the assertion associated with this entry.
     * </p>
     */
    private final Set<Exp> myOperators;

    /**
     * <p>
     * The subtype associated with this mathematical assertion declaration.
     * </p>
     */
    private final MathAssertionDec.TheoremSubtype myTheoremSubtype;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This creates a symbol table entry for a mathematical theorem.
     * </p>
     *
     * @param g
     *            The current type graph.
     * @param name
     *            Name associated with this entry.
     * @param definingElement
     *            The element that created this entry.
     * @param operators
     *            The operators associated with this entry.
     * @param sourceModule
     *            The module where this entry was created from.
     */
    public TheoremEntry(TypeGraph g, String name, MathAssertionDec definingElement, Set<Exp> operators,
            ModuleIdentifier sourceModule) {
        super(name, definingElement, sourceModule);
        myAssertionExp = definingElement.getAssertion();
        myTheoremSubtype = definingElement.getTheoremSubtype();
        myOperators = operators;
        myMathSymbolAlterEgo = new MathSymbolEntry(g, name, Quantification.NONE, definingElement, g.BOOLEAN, null, null,
                null, sourceModule);
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

        TheoremEntry that = (TheoremEntry) o;

        if (!Objects.equals(myAssertionExp, that.myAssertionExp))
            return false;
        if (!Objects.equals(myMathSymbolAlterEgo, that.myMathSymbolAlterEgo))
            return false;
        if (!Objects.equals(myOperators, that.myOperators))
            return false;
        return myTheoremSubtype == that.myTheoremSubtype;
    }

    /**
     * <p>
     * Since this is used by multiple objects, we really don't want to be returning a reference, therefore this method
     * returns a deep copy of the assertion expression.
     * </p>
     *
     * @return A {@link Exp} representation object.
     */
    public final Exp getAssertion() {
        return myAssertionExp.clone();
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
        return "a theorem";
    }

    /**
     * <p>
     * This method returns a set of operators in this entry.
     * </p>
     *
     * @return A {@link Set} of operator expressions.
     */
    public final Set<Exp> getOperators() {
        return myOperators;
    }

    /**
     * <p>
     * This method returns a theorem subtype associated with this entry.
     * </p>
     *
     * @return A {@link MathAssertionDec.TheoremSubtype} representation object.
     */
    public final MathAssertionDec.TheoremSubtype getTheoremSubtype() {
        return myTheoremSubtype;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (myAssertionExp != null ? myAssertionExp.hashCode() : 0);
        result = 31 * result + (myMathSymbolAlterEgo != null ? myMathSymbolAlterEgo.hashCode() : 0);
        result = 31 * result + (myOperators != null ? myOperators.hashCode() : 0);
        result = 31 * result + (myTheoremSubtype != null ? myTheoremSubtype.hashCode() : 0);
        return result;
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
     * @return A {@link TheoremEntry} that has been instantiated.
     */
    @Override
    public final TheoremEntry instantiateGenerics(Map<String, PTType> genericInstantiations,
            FacilityEntry instantiatingFacility) {
        throw new UnsupportedOperationException("Not supported yet.");
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
     * This method will attempt to convert this {@link SymbolTableEntry} into a {@link TheoremEntry}.
     * </p>
     *
     * @param l
     *            Location where we encountered this entry.
     *
     * @return A {@link TheoremEntry} if possible. Otherwise, it throws a {@link SourceErrorException}.
     */
    @Override
    public final TheoremEntry toTheoremEntry(Location l) {
        return this;
    }

}

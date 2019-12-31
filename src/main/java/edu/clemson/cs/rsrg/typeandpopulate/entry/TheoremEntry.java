/*
 * TheoremEntry.java
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

import edu.clemson.cs.rsrg.absyn.declarations.mathdecl.MathAssertionDec;
import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.statushandling.exception.SourceErrorException;
import edu.clemson.cs.rsrg.typeandpopulate.programtypes.PTType;
import edu.clemson.cs.rsrg.typeandpopulate.typereasoning.TypeGraph;
import edu.clemson.cs.rsrg.typeandpopulate.utilities.ModuleIdentifier;
import java.util.Map;

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

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This creates a symbol table entry for a mathematical theorem.
     * </p>
     *
     * @param g The current type graph.
     * @param name Name associated with this entry.
     * @param definingElement The element that created this entry.
     * @param sourceModule The module where this entry was created from.
     */
    public TheoremEntry(TypeGraph g, String name,
            MathAssertionDec definingElement, ModuleIdentifier sourceModule) {
        super(name, definingElement, sourceModule);
        myAssertionExp = definingElement.getAssertion();

        myMathSymbolAlterEgo = new MathSymbolEntry(g, name, Quantification.NONE,
                definingElement, g.BOOLEAN, null, null, null, sourceModule);
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * Since this is used by multiple objects, we really don't want to be
     * returning a reference,
     * therefore this method returns a deep copy of the assertion expression.
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
     * This method converts a generic {@link SymbolTableEntry} to an entry that
     * has all the generic
     * types and variables replaced with actual values.
     * </p>
     *
     * @param genericInstantiations Map containing all the instantiations.
     * @param instantiatingFacility Facility that instantiated this type.
     *
     * @return A {@link TheoremEntry} that has been instantiated.
     */
    @Override
    public final TheoremEntry instantiateGenerics(
            Map<String, PTType> genericInstantiations,
            FacilityEntry instantiatingFacility) {
        throw new UnsupportedOperationException("Not supported yet.");
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
        return myMathSymbolAlterEgo;
    }

    /**
     * <p>
     * This method will attempt to convert this {@link SymbolTableEntry} into a
     * {@link TheoremEntry}.
     * </p>
     *
     * @param l Location where we encountered this entry.
     *
     * @return A {@link TheoremEntry} if possible. Otherwise, it throws a
     *         {@link SourceErrorException}.
     */
    @Override
    public final TheoremEntry toTheoremEntry(Location l) {
        return this;
    }

}

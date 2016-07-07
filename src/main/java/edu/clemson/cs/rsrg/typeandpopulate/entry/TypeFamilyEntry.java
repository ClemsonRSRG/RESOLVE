/**
 * TypeFamilyEntry.java
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
import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.statushandling.exception.SourceErrorException;
import edu.clemson.cs.rsrg.typeandpopulate.mathtypes.MTType;
import edu.clemson.cs.rsrg.typeandpopulate.programtypes.PTFamily;
import edu.clemson.cs.rsrg.typeandpopulate.typereasoning.TypeGraph;
import edu.clemson.cs.rsrg.typeandpopulate.utilities.ModuleIdentifier;

/**
 * <p>This creates a symbol table entry for a {@code Type Family}.</p>
 *
 * @version 2.0
 */
public class TypeFamilyEntry extends ProgramTypeEntry {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>Exemplar associated with this entry.</p> */
    private final MathSymbolEntry myExemplar;

    /** <p>The mathematical type constraint for this entry.</p> */
    private final Exp myConstraintExp;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This creates a symbol table entry for a type family declaration.</p>
     *
     * @param g The current type graph.
     * @param name Name associated with this entry.
     * @param definingElement The element that created this entry.
     * @param sourceModule The module where this entry was created from.
     * @param modelType The mathematical type assigned to this entry.
     * @param programType The program type assigned to this entry.
     * @param exemplarEntry The exemplar for this entry.
     * @param constraintExp The constraint expression for this entry.
     */
    public TypeFamilyEntry(TypeGraph g, String name,
            ResolveConceptualElement definingElement,
            ModuleIdentifier sourceModule, MTType modelType,
            PTFamily programType, MathSymbolEntry exemplarEntry,
            Exp constraintExp) {
        super(g, name, definingElement, sourceModule, modelType, programType);

        myExemplar = exemplarEntry;
        myConstraintExp = constraintExp;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     *  <p>Since this is used by multiple objects, we really don't want to be returning a reference,
     * therefore this method returns a deep copy of the constraint expression.</p>
     *
     * @return A {@link Exp} representation object.
     */
    public final Exp getAssertion() {
        return myConstraintExp.clone();
    }

    /**
     * <p>This method returns the exemplar associated with this entry.</p>
     *
     * @return A {@link MathSymbolEntry} representation object.
     */
    public final MathSymbolEntry getExemplar() {
        return myExemplar;
    }

    /**
     * <p>This method returns the program type associated with this entry.</p>
     *
     * @return A {@link PTFamily} representation object.
     */
    @Override
    public final PTFamily getProgramType() {
        return (PTFamily) super.getProgramType();
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
    @Override
    public final TypeFamilyEntry toTypeFamilyEntry(Location l) {
        return this;
    }

}
/*
 * ConceptTypeExtractor.java
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
package edu.clemson.rsrg.vcgeneration.utilities.treewalkers;

import edu.clemson.rsrg.absyn.declarations.typedecl.TypeFamilyDec;
import edu.clemson.rsrg.treewalk.TreeWalkerVisitor;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>
 * This class extracts all the {@link TypeFamilyDec} introduced by a {@code Concept}. This visitor logic is implemented
 * as a {@link TreeWalkerVisitor}.
 * </p>
 *
 * @author Yu-Shan Sun
 *
 * @version 1.0
 */
public class ConceptTypeExtractor extends TreeWalkerVisitor {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * The list of {@link TypeFamilyDec} encountered.
     * </p>
     */
    private final List<TypeFamilyDec> myTypeFamilyDecs;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This creates an object that extracts all the {@link TypeFamilyDec} while walking a {@code Concept} module.
     * </p>
     */
    public ConceptTypeExtractor() {
        myTypeFamilyDecs = new LinkedList<>();
    }

    // ===========================================================
    // Visitor Methods
    // ===========================================================

    /**
     * <p>
     * Code that gets executed after visiting a {@link TypeFamilyDec}.
     * </p>
     *
     * @param dec
     *            A type family declared in a {@code Concept}.
     */
    @Override
    public final void postTypeFamilyDec(TypeFamilyDec dec) {
        myTypeFamilyDecs.add(dec);
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * This method returns the list of {@link TypeFamilyDec} that we encountered after walking a {@code Concept}.
     * </p>
     *
     * @return A list of {@link TypeFamilyDec}.
     */
    public final List<TypeFamilyDec> getTypeFamilyDecs() {
        return myTypeFamilyDecs;
    }

}

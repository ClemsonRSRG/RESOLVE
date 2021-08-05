/*
 * ConceptSharedStateExtractor.java
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
package edu.clemson.rsrg.vcgeneration.utilities.treewalkers;

import edu.clemson.rsrg.absyn.declarations.sharedstatedecl.SharedStateDec;
import edu.clemson.rsrg.treewalk.TreeWalkerVisitor;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>
 * This class extracts all the {@link SharedStateDec} introduced by a {@code Concept}. This visitor logic is implemented
 * as a {@link TreeWalkerVisitor}.
 * </p>
 *
 * @author Yu-Shan Sun
 * 
 * @version 1.0
 */
public class ConceptSharedStateExtractor extends TreeWalkerVisitor {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * The list of {@link SharedStateDec} encountered.
     * </p>
     */
    private final List<SharedStateDec> mySharedStateDecs;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This creates an object that extracts all the {@link SharedStateDec} while walking a {@code Concept} module.
     * </p>
     */
    public ConceptSharedStateExtractor() {
        mySharedStateDecs = new LinkedList<>();
    }

    // ===========================================================
    // Visitor Methods
    // ===========================================================

    /**
     * <p>
     * Code that gets executed after visiting a {@link SharedStateDec}.
     * </p>
     *
     * @param dec
     *            A shared state declared in a {@code Concept}.
     */
    @Override
    public final void postSharedStateDec(SharedStateDec dec) {
        mySharedStateDecs.add(dec);
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * This method returns the list of {@link SharedStateDec} that we encountered after walking a {@code Concept}.
     * </p>
     *
     * @return A list of {@link SharedStateDec}.
     */
    public final List<SharedStateDec> getSharedStateDecs() {
        return mySharedStateDecs;
    }

}

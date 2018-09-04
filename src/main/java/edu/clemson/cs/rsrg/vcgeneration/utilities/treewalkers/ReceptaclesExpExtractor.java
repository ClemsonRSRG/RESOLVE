/*
 * ReceptaclesExpExtractor.java
 * ---------------------------------
 * Copyright (c) 2018
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.vcgeneration.utilities.treewalkers;

import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.absyn.expressions.mathexpr.RecpExp;
import edu.clemson.cs.rsrg.absyn.expressions.mathexpr.TypeReceptaclesExp;
import edu.clemson.cs.rsrg.absyn.expressions.programexpr.ProgramExp;
import edu.clemson.cs.rsrg.statushandling.exception.SourceErrorException;
import edu.clemson.cs.rsrg.treewalk.TreeWalkerVisitor;

/**
 * <p>This class determines if an {@link Exp} is contains any {@link RecpExp} or
 * {@link TypeReceptaclesExp}. This visitor logic is implemented as a
 * {@link TreeWalkerVisitor}.</p>
 *
 * @author Yu-Shan Sun
 * @version 1.0
 */
public class ReceptaclesExpExtractor extends TreeWalkerVisitor {

    // ===========================================================
    // Member Fields
    // ===========================================================

    // ===========================================================
    // Constructors
    // ===========================================================

    // ===========================================================
    // Visitor Methods
    // ===========================================================

    // -----------------------------------------------------------
    // Math Expression-Related
    // -----------------------------------------------------------

    // -----------------------------------------------------------
    // Program Expression-Related
    // -----------------------------------------------------------

    /**
     * <p>Code that gets executed before visiting a {@link ProgramExp}.</p>
     *
     * @param exp A programming expression.
     */
    @Override
    public final void preProgramExp(ProgramExp exp) {
        // This is an error! We should have converted all ProgramExp to their
        // MathExp counterparts.
        throw new SourceErrorException("[VCGenerator] Found: " + exp
                + " in a formal specification!", exp.getLocation());
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

}
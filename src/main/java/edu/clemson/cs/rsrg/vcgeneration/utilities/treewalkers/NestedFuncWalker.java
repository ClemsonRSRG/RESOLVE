/*
 * NestedFuncWalker.java
 * ---------------------------------
 * Copyright (c) 2017
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.vcgeneration.utilities.treewalkers;

import edu.clemson.cs.rsrg.absyn.clauses.AssertionClause;
import edu.clemson.cs.rsrg.absyn.expressions.programexpr.ProgramFunctionExp;
import edu.clemson.cs.rsrg.treewalk.TreeWalkerVisitor;

/**
 * <p>This class extracts ensures clauses (with the appropriate substitutions)
 * from walking nested {@link ProgramFunctionExp}. This visitor logic is implemented
 * as a {@link TreeWalkerVisitor}.</p>
 *
 * @author Yu-Shan Sun
 * @version 2.0
 */
public class NestedFuncWalker extends TreeWalkerVisitor {

    // ===========================================================
    // Member Fields
    // ===========================================================

    // ===========================================================
    // Constructors
    // ===========================================================

    public NestedFuncWalker() {}

    // ===========================================================
    // Visitor Methods
    // ===========================================================

    // -----------------------------------------------------------
    // Program Expression-Related
    // -----------------------------------------------------------

    /**
     * <p>Code that gets executed before visiting a {@link ProgramFunctionExp}.</p>
     *
     * @param exp A program function expression.
     */
    @Override
    public final void preProgramFunctionExp(ProgramFunctionExp exp) {}

    /**
     * <p>Code that gets executed after visiting a {@link ProgramFunctionExp}.</p>
     *
     * @param exp A program function expression.
     */
    @Override
    public final void postProgramFunctionExp(ProgramFunctionExp exp) {}

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This method returns the final modified ensures clause
     * after all the necessary replacement/substitutions have been made.</p>
     *
     * @return The complete ensures clause.
     */
    public final AssertionClause getEnsuresClause() {
        return null;
    }

    /**
     * <p>This method returns the final modified requires clause
     * after all the necessary replacement/substitutions have been made.</p>
     *
     * @return The complete requires clause.
     */
    public final AssertionClause getRequiresClause() {
        return null;
    }

}
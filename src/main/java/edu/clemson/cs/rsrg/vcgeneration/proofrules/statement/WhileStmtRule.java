/*
 * WhileStmtRule.java
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
package edu.clemson.cs.rsrg.vcgeneration.proofrules.statement;

import edu.clemson.cs.rsrg.absyn.statements.WhileStmt;
import edu.clemson.cs.rsrg.vcgeneration.proofrules.AbstractProofRuleApplication;
import edu.clemson.cs.rsrg.vcgeneration.proofrules.ProofRuleApplication;
import edu.clemson.cs.rsrg.vcgeneration.utilities.AssertiveCodeBlock;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

/**
 * <p>This class contains the logic for applying the {@code while}
 * rule.</p>
 *
 * @author Yu-Shan Sun
 * @version 1.0
 */
public class WhileStmtRule extends AbstractProofRuleApplication
        implements
            ProofRuleApplication {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The {@link WhileStmt} we are applying the rule to.</p> */
    private final WhileStmt myWhileStmt;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This creates a new application of the {@code while}
     * rule.</p>
     *
     * @param whileStmt The {@link WhileStmt} we are applying
     *                  the rule to.
     * @param block The assertive code block that the subclasses are
     *              applying the rule to.
     * @param stGroup The string template group we will be using.
     * @param blockModel The model associated with {@code block}.
     */
    public WhileStmtRule(WhileStmt whileStmt, AssertiveCodeBlock block,
            STGroup stGroup, ST blockModel) {
        super(block, stGroup, blockModel);
        myWhileStmt = whileStmt;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This method applies the {@code Proof Rule}.</p>
     */
    @Override
    public final void applyRule() {}

    /**
     * <p>This method returns a description associated with
     * the {@code Proof Rule}.</p>
     *
     * @return A string.
     */
    @Override
    public final String getRuleDescription() {
        return "While Rule";
    }

}
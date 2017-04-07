/*
 * ProcedureDeclRule.java
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
package edu.clemson.cs.rsrg.vcgeneration.proofrules.declaration;

import edu.clemson.cs.rsrg.vcgeneration.proofrules.AbstractProofRuleApplication;
import edu.clemson.cs.rsrg.vcgeneration.proofrules.ProofRuleApplication;
import edu.clemson.cs.rsrg.vcgeneration.vcs.AssertiveCodeBlock;

/**
 * <p>This class contains the logic for the {@code procedure}
 * declaration rule.</p>
 *
 * @author Yu-Shan Sun
 * @version 1.0
 */
public class ProcedureDeclRule extends AbstractProofRuleApplication
        implements
            ProofRuleApplication {

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This creates a new application for the {@code procedure}
     * declaration rule.</p>
     *
     * @param block The assertive code block that the subclasses are
     *              applying the rule to.
     */
    public ProcedureDeclRule(AssertiveCodeBlock block) {
        super(block);
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This method applies the {@code Proof Rule}.</p>
     */
    @Override
    public final void applyRule() {

    }

    /**
     * <p>This method returns a description associated with
     * the {@code Proof Rule}.</p>
     *
     * @return A string.
     */
    @Override
    public final String getRuleDescription() {
        return "Procedure Declaration Rule";
    }

}
/*
 * RememberStmtRule.java
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

import edu.clemson.cs.rsrg.vcgeneration.proofrules.AbstractProofRuleApplication;
import edu.clemson.cs.rsrg.vcgeneration.proofrules.ProofRuleApplication;
import edu.clemson.cs.rsrg.vcgeneration.vcs.AssertiveCodeBlock;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

public class RememberStmtRule extends AbstractProofRuleApplication
        implements
            ProofRuleApplication {

    public RememberStmtRule(AssertiveCodeBlock block, STGroup stGroup,
            ST blockModel) {
        super(block, stGroup, blockModel);
    }

    /**
     * <p>This method applies the {@code Proof Rule}.</p>
     */
    @Override
    public void applyRule() {

    }

    /**
     * <p>This method returns a description associated with
     * the {@code Proof Rule}.</p>
     *
     * @return A string.
     */
    @Override
    public final String getRuleDescription() {
        return "Remember Rule";
    }
}
/*
 * ProofRuleApplication.java
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
package edu.clemson.cs.rsrg.vcgeneration.proofrules;

import edu.clemson.cs.rsrg.vcgeneration.vcs.AssertiveCodeBlock;
import java.util.List;

/**
 * <p>A common interface that allows the different {@code Proof Rules}
 * to be applied.</p>
 *
 * @author Yu-Shan Sun
 * @version 1.0
 */
public interface ProofRuleApplication {

    /**
     * <p>This method applies the {@code Proof Rule}.</p>
     */
    void applyRule();

    /**
     * <p>This method returns the list of {@link AssertiveCodeBlock AssertiveCodeBlock(s)}
     * that resulted from applying the {@code Proof Rule}.</p>
     */
    List<AssertiveCodeBlock> getAssertiveCodeBlocks();

    /**
     * <p>This method returns a description associated with
     * the {@code Proof Rule}.</p>
     *
     * @return A string.
     */
    String getRuleDescription();

}
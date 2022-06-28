/*
 * ProofRuleApplication.java
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
package edu.clemson.rsrg.vcgeneration.proofrules;

import edu.clemson.rsrg.vcgeneration.utilities.AssertiveCodeBlock;
import java.util.Deque;
import java.util.Map;
import org.stringtemplate.v4.ST;

/**
 * <p>
 * A common interface that allows the different {@code Proof Rules} to be applied.
 * </p>
 *
 * @author Yu-Shan Sun
 *
 * @version 1.0
 */
public interface ProofRuleApplication {

    /**
     * <p>
     * This method applies the {@code Proof Rule}.
     * </p>
     */
    void applyRule();

    /**
     * <p>
     * This method returns the a {@link Deque} of {@link AssertiveCodeBlock AssertiveCodeBlock(s)} that resulted from
     * applying the {@code Proof Rule}.
     * </p>
     *
     * @return A {@link Deque} containing all the {@link AssertiveCodeBlock AssertiveCodeBlock(s)}.
     */
    Deque<AssertiveCodeBlock> getAssertiveCodeBlocks();

    /**
     * <p>
     * This method returns the string template associated with the incoming {@link AssertiveCodeBlock}.
     * </p>
     *
     * @return A {@link ST} object.
     */
    ST getBlockModel();

    /**
     * <p>
     * This method returns the a map containing the new string template block models associated with any
     * {@link AssertiveCodeBlock AssertiveCodeBlock(s)} that resulted from applying the {@code Proof Rule}.
     * </p>
     *
     * @return A map from {@link AssertiveCodeBlock AssertiveCodeBlock(s)} to {@link ST} block models.
     */
    Map<AssertiveCodeBlock, ST> getNewAssertiveCodeBlockModels();

    /**
     * <p>
     * This method returns a description associated with the {@code Proof Rule}.
     * </p>
     *
     * @return A string.
     */
    String getRuleDescription();

}

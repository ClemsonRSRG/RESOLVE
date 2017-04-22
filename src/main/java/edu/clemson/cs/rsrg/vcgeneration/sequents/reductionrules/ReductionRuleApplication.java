/*
 * ReductionRuleApplication.java
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
package edu.clemson.cs.rsrg.vcgeneration.sequents.reductionrules;

import edu.clemson.cs.rsrg.vcgeneration.sequents.Sequent;
import java.util.List;

/**
 * <p>A common interface that allows the different {@code Sequent Reduction Rules}
 * to be applied.</p>
 *
 * @author Yu-Shan Sun
 * @version 1.0
 */
public interface ReductionRuleApplication {

    /**
     * <p>This method applies the {@code Sequent Reduction Rule}.</p>
     *
     * @return A list of {@link Sequent Sequents} that resulted
     * from applying the rule.
     */
    List<Sequent> applyRule();

    /**
     * <p>This method returns a description associated with
     * the {@code Sequent Reduction Rule}.</p>
     *
     * @return A string.
     */
    String getRuleDescription();

}
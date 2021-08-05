/*
 * PerThreadReasoningResources.java
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
package edu.clemson.rsrg.typeandpopulate.typereasoning;

import edu.clemson.rsrg.typeandpopulate.typevisitor.AlphaEquivalencyChecker;

/**
 * <p>
 * Type reasoning is used extensively by the prover, where things are done in a tight loop that needs to run as quickly
 * as possible. As a result, performance is at a premium and we want to avoid dynamic object creation. At the same time,
 * we can't have a bunch of static variables running around because many of these structures are not thread safe. This
 * class is guaranteed not to be shared between threads
 * </p>
 *
 * @version 2.0
 */
public class PerThreadReasoningResources {

    /**
     * <p>
     * Simply create one alpha equivalency checker.
     * </p>
     */
    public final AlphaEquivalencyChecker alphaChecker = new AlphaEquivalencyChecker();

}

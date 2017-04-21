/*
 * AbstractReductionRuleApplication.java
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

import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.statushandling.exception.MiscErrorException;
import edu.clemson.cs.rsrg.vcgeneration.sequents.Sequent;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>This is the abstract base class for all the {@code Sequent Reduction Rules}.</p>
 *
 * @author Yu-Shan Sun
 * @version 1.0
 */
public abstract class AbstractReductionRuleApplication
        implements
            ReductionRuleApplication {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The original expression to be reduced</p> */
    protected final Exp myOriginalExp;

    /** <p>The original sequent that contains {@code myOriginalExp}.</p> */
    protected final Sequent myOriginalSequent;

    /** <p>The resulting {@code sequents}.</p> */
    protected final List<Sequent> myResultingSequents;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>An helper constructor that stores the {@code originalSequent}
     * and the {@code originalExp} we are trying to reduce as well as
     * creating the list to store the resulting {@link Sequent Sequents}.</p>
     *
     * @param originalSequent The original {@link Sequent} that contains
     *                        the expression to be reduced.
     * @param originalExp The {@link Exp} to be reduced.
     */
    protected AbstractReductionRuleApplication(Sequent originalSequent, Exp originalExp) {
        myOriginalExp = originalExp;
        myOriginalSequent = originalSequent;
        myResultingSequents = new ArrayList<>();
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * <p>This is an helper method that throws an unexpected expression
     * error message.</p>
     */
    protected final void unexpectedExp() {
        throw new MiscErrorException("Found: " + myOriginalExp + " of type: "
                + myOriginalExp.getClass().getSimpleName() + " while applying "
                + getRuleDescription(), new IllegalStateException());
    }

}
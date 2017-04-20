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

    /** <p>The resulting {@code antecedent} expressions.</p> */
    protected final List<Exp> myResultingAntecedentExps;

    /** <p>The resulting {@code consequent} expressions.</p> */
    protected final List<Exp> myResultingConsequentExps;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>An helper constructor that stores {@code originalExp} we
     * are trying to reduce as well as creating the lists to store
     * the new {@code antecedent} and {@code consequent} expressions.</p>
     *
     * @param originalExp The {@link Exp} to be reduced.
     */
    protected AbstractReductionRuleApplication(Exp originalExp) {
        myOriginalExp = originalExp;
        myResultingAntecedentExps = new ArrayList<>();
        myResultingConsequentExps = new ArrayList<>();
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This method returns the resulting {@code antecedent}
     * expressions.</p>
     *
     * @return A list of {@link Exp Exps}.
     */
    @Override
    public final List<Exp> getResultingAntecedentExps() {
        return myResultingAntecedentExps;
    }

    /**
     * <p>This method returns the resulting {@code consequent}
     * expressions.</p>
     *
     * @return A list of {@link Exp Exps}.
     */
    @Override
    public final List<Exp> getResultingConsequentExps() {
        return myResultingConsequentExps;
    }

}
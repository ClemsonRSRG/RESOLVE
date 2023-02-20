/*
 * GenerateRememberRuleSubstitutionMap.java
 * ---------------------------------
 * Copyright (c) 2023
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.rsrg.vcgeneration.utilities.treewalkers;

import edu.clemson.rsrg.absyn.expressions.Exp;
import edu.clemson.rsrg.absyn.expressions.mathexpr.*;
import edu.clemson.rsrg.absyn.expressions.programexpr.ProgramExp;
import edu.clemson.rsrg.statushandling.exception.SourceErrorException;
import edu.clemson.rsrg.treewalk.TreeWalker;
import edu.clemson.rsrg.treewalk.TreeWalkerVisitor;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This class is an helper class that helps generate a substitution map to help apply the {@code Remember} rule. This
 * visitor logic is implemented as a {@link TreeWalkerVisitor}.
 * </p>
 *
 * @author Yu-Shan Sun
 *
 * @version 1.0
 */
public class GenerateRememberRuleSubstitutionMap extends TreeWalkerVisitor {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * This contains a mapping of the original {@link Exp} to the resulting {@link Exp} after applying the
     * {@code Remember} rule.
     * </p>
     */
    private final Map<Exp, Exp> myGeneratedExpMap;

    /**
     * <p>
     * This is the original {@link Exp} that we are applying the rule to.
     * </p>
     */
    private final Exp myOriginalExp;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This creates an object that that applies the {@code Remember} rule to anything that descends from
     * {@link MathExp}.
     * </p>
     *
     * @param originalExp
     *            The expression we want to apply the {@code Remember} rule to.
     */
    public GenerateRememberRuleSubstitutionMap(Exp originalExp) {
        myGeneratedExpMap = new HashMap<>();
        myOriginalExp = originalExp;
    }

    // ===========================================================
    // Visitor Methods
    // ===========================================================

    // -----------------------------------------------------------
    // Math Expression-Related
    // -----------------------------------------------------------

    /**
     * <p>
     * This method redefines how an {@link OldExp} should be walked.
     * </p>
     *
     * @param exp
     *            An {@code old} expression.
     *
     * @return {@code true}
     */
    @Override
    public final boolean walkOldExp(OldExp exp) {
        preAny(exp);
        preExp(exp);
        preMathExp(exp);
        preOldExp(exp);

        // YS: We only want to get rid of the outermost
        // "#". If the expression happens to be "##y",
        // then we simply get rid of the outermost "#" and
        // return. Otherwise, we walk the children of OldExp
        if (exp.getExp() instanceof OldExp) {
            myGeneratedExpMap.put(exp, exp.getExp().clone());
        } else {
            // Visit our inner expression to see if there any more
            // substitutions to be made.
            TreeWalker.visit(this, exp.getExp());

            // Generate a new substitution expression and add it to
            // our map.
            myGeneratedExpMap.put(exp, exp.getExp().substitute(myGeneratedExpMap));
        }

        postOldExp(exp);
        postMathExp(exp);
        postExp(exp);
        postAny(exp);

        return true;
    }

    /**
     * <p>
     * This method redefines how a {@link SetCollectionExp} should be walked.
     * </p>
     *
     * @param exp
     *            A set collection expression.
     *
     * @return {@code true}
     */
    @Override
    public final boolean walkSetCollectionExp(SetCollectionExp exp) {
        preAny(exp);
        preExp(exp);
        preMathExp(exp);
        preSetCollectionExp(exp);

        // YS: Our tree walker visitor doesn't visit items inside
        // sets right now, so we will need to redefine the walk method
        // to walk it ourselves.
        for (Exp innerExp : exp.getVars()) {
            TreeWalker.visit(this, innerExp);

            // Generate a new substitution expression and add it to
            // our map.
            myGeneratedExpMap.put(exp, innerExp.substitute(myGeneratedExpMap));
        }

        postSetCollectionExp(exp);
        postMathExp(exp);
        postExp(exp);
        postAny(exp);

        return true;
    }

    // -----------------------------------------------------------
    // Program Expression-Related
    // -----------------------------------------------------------

    /**
     * <p>
     * Code that gets executed before visiting a {@link ProgramExp}.
     * </p>
     *
     * @param exp
     *            A programming expression.
     */
    @Override
    public final void preProgramExp(ProgramExp exp) {
        // This is an error! We should have converted all ProgramExp
        // to their math counterparts.
        throw new SourceErrorException("[VCGenerator] Encountered ProgramExp: " + exp + " in " + myOriginalExp
                + " while applying the Remember Rule.", exp.getLocation());
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * This method returns the substitution map generated by this tree walker visitor.
     * </p>
     *
     * @return A map containing the {@link Exp} to be substituted.
     */
    public final Map<Exp, Exp> getSubstitutionMap() {
        return myGeneratedExpMap;
    }

}

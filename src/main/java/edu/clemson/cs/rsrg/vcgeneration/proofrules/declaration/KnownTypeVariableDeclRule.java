/*
 * KnownTypeVariableDeclRule.java
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

import edu.clemson.cs.rsrg.absyn.clauses.AssertionClause;
import edu.clemson.cs.rsrg.absyn.declarations.variabledecl.VarDec;
import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.absyn.statements.AssumeStmt;
import edu.clemson.cs.rsrg.vcgeneration.proofrules.AbstractProofRuleApplication;
import edu.clemson.cs.rsrg.vcgeneration.proofrules.ProofRuleApplication;
import edu.clemson.cs.rsrg.vcgeneration.utilities.AssertiveCodeBlock;
import edu.clemson.cs.rsrg.vcgeneration.utilities.Utilities;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

/**
 * <p>This class contains the logic for a variable declaration
 * rule with a known program type.</p>
 *
 * @author Yu-Shan Sun
 * @version 1.0
 */
public class KnownTypeVariableDeclRule extends AbstractProofRuleApplication
        implements
            ProofRuleApplication {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The variable's {@code initialization ensures} clause.</p> */
    private final AssertionClause myInitEnsuresClause;

    /** <p>The variable declaration we are applying the rule to.</p> */
    private final VarDec myVarDec;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This creates a new application for a variable declaration
     * rule with a known program type.</p>
     *
     * @param varDec The variable declaration we are applying the
     *               rule to.
     * @param initEnsuresClause The initialization ensures clause
     *                          for the variable we are trying to apply
     *                          the rule to.
     * @param block The assertive code block that the subclasses are
     *              applying the rule to.
     * @param stGroup The string template group we will be using.
     * @param blockModel The model associated with {@code block}.
     */
    public KnownTypeVariableDeclRule(VarDec varDec,
            AssertionClause initEnsuresClause, AssertiveCodeBlock block,
            STGroup stGroup, ST blockModel) {
        super(block, stGroup, blockModel);
        myInitEnsuresClause = initEnsuresClause;
        myVarDec = varDec;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This method applies the {@code Proof Rule}.</p>
     */
    @Override
    public final void applyRule() {
        // Create an assume statement with the initialization ensures clause.
        Exp assumeExp =
                Utilities.formConjunct(myInitEnsuresClause.getLocation(), null,
                        myInitEnsuresClause);
        AssumeStmt initAssumeStmt =
                new AssumeStmt(myInitEnsuresClause.getLocation(), assumeExp,
                        false);
        myCurrentAssertiveCodeBlock.addStatement(initAssumeStmt);

        // Add this as a free variable
        myCurrentAssertiveCodeBlock.addFreeVar(Utilities.createVarExp(myVarDec
                .getLocation(), null, myVarDec.getName(), myVarDec
                .getMathType(), null));

        // Add the different details to the various different output models
        ST stepModel = mySTGroup.getInstanceOf("outputVCGenStep");
        stepModel.add("proofRuleName", getRuleDescription()).add(
                "currentStateOfBlock", myCurrentAssertiveCodeBlock);
        myBlockModel.add("vcGenSteps", stepModel.render());
    }

    /**
     * <p>This method returns a description associated with
     * the {@code Proof Rule}.</p>
     *
     * @return A string.
     */
    @Override
    public final String getRuleDescription() {
        return "Variable Declaration Rule (Known Program Type)";
    }

}
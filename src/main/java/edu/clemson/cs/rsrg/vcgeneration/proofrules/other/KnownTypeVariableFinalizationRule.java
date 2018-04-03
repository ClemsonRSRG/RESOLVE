/*
 * KnownTypeVariableFinalizationRule.java
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
package edu.clemson.cs.rsrg.vcgeneration.proofrules.other;

import edu.clemson.cs.rsrg.absyn.clauses.AffectsClause;
import edu.clemson.cs.rsrg.absyn.clauses.AssertionClause;
import edu.clemson.cs.rsrg.absyn.declarations.variabledecl.VarDec;
import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.absyn.expressions.mathexpr.OldExp;
import edu.clemson.cs.rsrg.absyn.expressions.mathexpr.VCVarExp;
import edu.clemson.cs.rsrg.absyn.items.mathitems.SpecInitFinalItem;
import edu.clemson.cs.rsrg.absyn.statements.AssumeStmt;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.parsing.data.LocationDetailModel;
import edu.clemson.cs.rsrg.vcgeneration.proofrules.AbstractProofRuleApplication;
import edu.clemson.cs.rsrg.vcgeneration.proofrules.ProofRuleApplication;
import edu.clemson.cs.rsrg.vcgeneration.utilities.AssertiveCodeBlock;
import edu.clemson.cs.rsrg.vcgeneration.utilities.Utilities;
import edu.clemson.cs.rsrg.vcgeneration.utilities.VerificationContext;
import java.util.LinkedHashMap;
import java.util.Map;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

/**
 * <p>This class contains the logic for a variable finalization
 * rule for a variable declaration with a known program type.</p>
 *
 * @author Yu-Shan Sun
 * @version 1.0
 */
public class KnownTypeVariableFinalizationRule
        extends
            AbstractProofRuleApplication implements ProofRuleApplication {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The variable's {@code finalization} specification item.</p> */
    private final SpecInitFinalItem myFinalItem;

    /** <p>The variable declaration we are applying the rule to.</p> */
    private final VarDec myVarDec;

    /** <p>List of new free variables.</p> */
    private final Map<Exp, Exp> myNewFreeVarSubstitutions;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This creates a new application for a variable finalization
     * rule with a known program type.</p>
     *
     * @param varDec The variable declaration we are applying the
     *               rule to.
     * @param finalItem The {@code finalization} specification item
     * @param block The assertive code block that the subclasses are
     *              applying the rule to.
     * @param context The verification context that contains all
     *                the information we have collected so far.
     * @param stGroup The string template group we will be using.
     * @param blockModel The model associated with {@code block}.
     */
    public KnownTypeVariableFinalizationRule(VarDec varDec, SpecInitFinalItem finalItem,
            AssertiveCodeBlock block, VerificationContext context, STGroup stGroup, ST blockModel) {
        super(block, context, stGroup, blockModel);
        myFinalItem = finalItem;
        myNewFreeVarSubstitutions = new LinkedHashMap<>();
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
        // Create an assume statement with the finalization ensures clause and add
        // the location detail associated with it.
        AssertionClause finalEnsuresClause = myFinalItem.getEnsures();
        Location finalEnsuresLoc =
                finalEnsuresClause.getAssertionExp().getLocation();
        Exp assumeExp =
                Utilities.formConjunct(finalEnsuresLoc, null,
                        finalEnsuresClause, new LocationDetailModel(
                                finalEnsuresLoc.clone(), finalEnsuresLoc.clone(),
                                "Finalization Ensures Clause of "
                                        + myVarDec.getName()));

        // Create a replacement map
        Map<Exp, Exp> substitutions = new LinkedHashMap<>();

        // Create replacements for any affected variables
        AffectsClause affectsClause = myFinalItem.getAffectedVars();
        if (affectsClause != null) {
            for (Exp affectedExp : affectsClause.getAffectedExps()) {
                // Create a VCVarExp using the affectedExp
                VCVarExp vcVarExp =
                        Utilities.createVCVarExp(myCurrentAssertiveCodeBlock, affectedExp);
                myCurrentAssertiveCodeBlock.addFreeVar(vcVarExp);
                substitutions.put(affectedExp, vcVarExp);
                myNewFreeVarSubstitutions.put(affectedExp.clone(), vcVarExp.clone());

                // Replace all instances of incoming affectedExp with affectedExp
                substitutions.put(new OldExp(affectedExp.getLocation(), affectedExp),
                        affectedExp.clone());
            }
        }
        assumeExp = assumeExp.substitute(substitutions);

        AssumeStmt finalAssumeStmt =
                new AssumeStmt(finalEnsuresClause.getLocation(), assumeExp, false);
        myCurrentAssertiveCodeBlock.addStatement(finalAssumeStmt);
    }

    /**
     * <p>This method returns the list of free variables created
     * by this {@code Proof Rule}.</p>
     *
     * @return A list of {@link Exp Exps}.
     */
    public final Map<Exp, Exp> getNewFreeVarSubstitutions() {
        return myNewFreeVarSubstitutions;
    }

    /**
     * <p>This method returns a description associated with
     * the {@code Proof Rule}.</p>
     *
     * @return A string.
     */
    @Override
    public final String getRuleDescription() {
        return "Variable Finalization Rule (Known Program Type)";
    }

}
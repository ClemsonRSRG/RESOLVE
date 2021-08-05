/*
 * TypeRepresentationCorrRule.java
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
package edu.clemson.rsrg.vcgeneration.proofrules.declarations.typedecl;

import edu.clemson.rsrg.absyn.clauses.AssertionClause;
import edu.clemson.rsrg.absyn.declarations.typedecl.TypeFamilyDec;
import edu.clemson.rsrg.absyn.declarations.typedecl.TypeRepresentationDec;
import edu.clemson.rsrg.absyn.declarations.variabledecl.VarDec;
import edu.clemson.rsrg.absyn.expressions.Exp;
import edu.clemson.rsrg.absyn.expressions.mathexpr.DotExp;
import edu.clemson.rsrg.absyn.expressions.mathexpr.VarExp;
import edu.clemson.rsrg.absyn.statements.AssumeStmt;
import edu.clemson.rsrg.absyn.statements.ConfirmStmt;
import edu.clemson.rsrg.parsing.data.LocationDetailModel;
import edu.clemson.rsrg.typeandpopulate.symboltables.MathSymbolTableBuilder;
import edu.clemson.rsrg.typeandpopulate.typereasoning.TypeGraph;
import edu.clemson.rsrg.vcgeneration.proofrules.AbstractProofRuleApplication;
import edu.clemson.rsrg.vcgeneration.proofrules.ProofRuleApplication;
import edu.clemson.rsrg.vcgeneration.utilities.AssertiveCodeBlock;
import edu.clemson.rsrg.vcgeneration.utilities.Utilities;
import edu.clemson.rsrg.vcgeneration.utilities.VerificationContext;
import java.util.LinkedHashMap;
import java.util.Map;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

/**
 * <p>
 * This class contains the logic for establishing the {@code Type Representation}'s {@code correspondence} is well
 * defined.
 * </p>
 *
 * @author Yu-Shan Sun
 * 
 * @version 1.0
 */
public class TypeRepresentationCorrRule extends AbstractProofRuleApplication implements ProofRuleApplication {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * The {@code type} representation we are applying the rule to.
     * </p>
     */
    private final TypeRepresentationDec myTypeRepresentationDec;

    /**
     * <p>
     * This is the math type graph that indicates relationship between different math types.
     * </p>
     */
    private final TypeGraph myTypeGraph;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This creates a new application for a well defined {@code correspondence} rule for a
     * {@link TypeRepresentationDec}.
     * </p>
     *
     * @param dec
     *            A concept type realization.
     * @param symbolTableBuilder
     *            The current symbol table.
     * @param block
     *            The assertive code block that the subclasses are applying the rule to.
     * @param context
     *            The verification context that contains all the information we have collected so far.
     * @param stGroup
     *            The string template group we will be using.
     * @param blockModel
     *            The model associated with {@code block}.
     */
    public TypeRepresentationCorrRule(TypeRepresentationDec dec, MathSymbolTableBuilder symbolTableBuilder,
            AssertiveCodeBlock block, VerificationContext context, STGroup stGroup, ST blockModel) {
        super(block, context, stGroup, blockModel);
        myTypeGraph = symbolTableBuilder.getTypeGraph();
        myTypeRepresentationDec = dec;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * This method applies the {@code Proof Rule}.
     * </p>
     */
    @Override
    public final void applyRule() {
        // Create the top most level assume statement and
        // add it to the assertive code block as the first statement
        AssertionClause typeConventionClause = myTypeRepresentationDec.getConvention();
        Exp topLevelAssumeExp = myCurrentVerificationContext
                .createTopLevelAssumeExpFromContext(myTypeRepresentationDec.getLocation(), true, false);
        topLevelAssumeExp = Utilities.formConjunct(myTypeRepresentationDec.getLocation(), topLevelAssumeExp,
                typeConventionClause,
                new LocationDetailModel(typeConventionClause.getAssertionExp().getLocation().clone(),
                        typeConventionClause.getAssertionExp().getLocation().clone(),
                        "Type: " + myTypeRepresentationDec.getName().getName() + "'s Convention"));

        // ( Assume CPC and RPC and DC and RDC and SS_RC and RC; )
        AssumeStmt topLevelAssumeStmt = new AssumeStmt(myTypeRepresentationDec.getLocation().clone(), topLevelAssumeExp,
                false);
        myCurrentAssertiveCodeBlock.addStatement(topLevelAssumeStmt);

        // ( Assume Cor_Exp; )
        AssertionClause typeCorrespondenceClause = myTypeRepresentationDec.getCorrespondence();
        Exp corrExp = Utilities.formConjunct(myTypeRepresentationDec.getLocation(), null, typeCorrespondenceClause,
                new LocationDetailModel(typeCorrespondenceClause.getLocation().clone(),
                        typeCorrespondenceClause.getLocation().clone(),
                        "Type: " + myTypeRepresentationDec.getName().getName() + "'s Correspondence"));

        AssumeStmt correspondenceAssumeStmt = new AssumeStmt(myTypeRepresentationDec.getLocation().clone(), corrExp,
                false);
        myCurrentAssertiveCodeBlock.addStatement(correspondenceAssumeStmt);

        // Create a replacement map for substituting exemplar
        // variable with one that indicates it is conceptual.
        TypeFamilyDec typeFamilyDec = Utilities.getAssociatedTypeFamilyDec(myTypeRepresentationDec,
                myCurrentVerificationContext);
        VarExp exemplarExp = Utilities.createVarExp(myTypeRepresentationDec.getLocation().clone(), null,
                typeFamilyDec.getExemplar().clone(), typeFamilyDec.getModel().getMathTypeValue(), null);
        DotExp concExemplarExp = Utilities.createConcVarExp(
                new VarDec(typeFamilyDec.getExemplar(), myTypeRepresentationDec.getRepresentation()),
                typeFamilyDec.getMathType(), myTypeGraph.BOOLEAN);
        Map<Exp, Exp> substitutionExemplarToConc = new LinkedHashMap<>();
        substitutionExemplarToConc.put(exemplarExp, concExemplarExp);

        // Confirm the type's constraint
        // ( Confirm TC; )
        Exp typeConstraintExp = typeFamilyDec.getConstraint().getAssertionExp().clone();
        typeConstraintExp = typeConstraintExp.substitute(substitutionExemplarToConc);
        typeConstraintExp.setLocationDetailModel(new LocationDetailModel(typeFamilyDec.getLocation().clone(),
                myTypeRepresentationDec.getLocation().clone(),
                "Well Defined Correspondence for " + myTypeRepresentationDec.getName()));
        ConfirmStmt finalConfirmStmt = new ConfirmStmt(myTypeRepresentationDec.getLocation().clone(), typeConstraintExp,
                VarExp.isLiteralTrue(typeConstraintExp));
        myCurrentAssertiveCodeBlock.addStatement(finalConfirmStmt);

        // Add the different details to the various different output models
        ST stepModel = mySTGroup.getInstanceOf("outputVCGenStep");
        stepModel.add("proofRuleName", getRuleDescription()).add("currentStateOfBlock", myCurrentAssertiveCodeBlock);

        // Add the different details to the various different output models
        myBlockModel.add("vcGenSteps", stepModel.render());
    }

    /**
     * <p>
     * This method returns a description associated with the {@code Proof Rule}.
     * </p>
     *
     * @return A string.
     */
    @Override
    public final String getRuleDescription() {
        return "Well Defined Correspondence Rule (Concept Type Realization)";
    }

}

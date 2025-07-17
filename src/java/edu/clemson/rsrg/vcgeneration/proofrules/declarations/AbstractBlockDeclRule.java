/*
 * AbstractBlockDeclRule.java
 * ---------------------------------
 * Copyright (c) 2024
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.rsrg.vcgeneration.proofrules.declarations;

import edu.clemson.rsrg.absyn.clauses.AffectsClause;
import edu.clemson.rsrg.absyn.declarations.mathdecl.MathDefVariableDec;
import edu.clemson.rsrg.absyn.declarations.sharedstatedecl.SharedStateDec;
import edu.clemson.rsrg.absyn.declarations.typedecl.TypeFamilyDec;
import edu.clemson.rsrg.absyn.declarations.variabledecl.MathVarDec;
import edu.clemson.rsrg.absyn.declarations.variabledecl.VarDec;
import edu.clemson.rsrg.absyn.expressions.Exp;
import edu.clemson.rsrg.absyn.expressions.mathexpr.*;
import edu.clemson.rsrg.parsing.data.Location;
import edu.clemson.rsrg.parsing.data.LocationDetailModel;
import edu.clemson.rsrg.parsing.data.PosSymbol;
import edu.clemson.rsrg.typeandpopulate.mathtypes.MTType;
import edu.clemson.rsrg.typeandpopulate.symboltables.MathSymbolTableBuilder;
import edu.clemson.rsrg.typeandpopulate.symboltables.ModuleScope;
import edu.clemson.rsrg.typeandpopulate.typereasoning.TypeGraph;
import edu.clemson.rsrg.vcgeneration.proofrules.AbstractProofRuleApplication;
import edu.clemson.rsrg.vcgeneration.proofrules.ProofRuleApplication;
import edu.clemson.rsrg.vcgeneration.proofrules.declarations.typedecl.TypeRepresentationFinalRule;
import edu.clemson.rsrg.vcgeneration.proofrules.declarations.typedecl.TypeRepresentationInitRule;
import edu.clemson.rsrg.vcgeneration.utilities.AssertiveCodeBlock;
import edu.clemson.rsrg.vcgeneration.utilities.Utilities;
import edu.clemson.rsrg.vcgeneration.utilities.VerificationContext;
import edu.clemson.rsrg.vcgeneration.utilities.formaltoactual.InstantiatedFacilityDecl;
import java.util.*;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

/**
 * <p>
 * This is the abstract base class for all the {@code declaration}'s that contains a block of inner {@code declarations}
 * and/or {@code statements}.
 * </p>
 *
 * @author Yu-Shan Sun
 *
 * @version 1.0
 */
public abstract class AbstractBlockDeclRule extends AbstractProofRuleApplication implements ProofRuleApplication {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * All the shared variables affected by the current {@code block}.
     * </p>
     */
    protected final Set<Exp> myAffectedExps;

    /**
     * <p>
     * The module scope for the file we are generating {@code VCs} for.
     * </p>
     */
    protected final ModuleScope myCurrentModuleScope;

    /**
     * <p>
     * This is the name for the current block we are applying the rule to.
     * </p>
     */
    private final String myDeclName;

    /**
     * <p>
     * This is the math type graph that indicates relationship between different math types.
     * </p>
     */
    protected final TypeGraph myTypeGraph;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * An helper constructor stores all declaration related items for a class that inherits from
     * {@code AbstractBlockDeclRule}.
     * </p>
     *
     * @param block
     *            The assertive code block that the subclasses are applying the rule to.
     * @param declName
     *            Name associated with the declaration.
     * @param symbolTableBuilder
     *            The current symbol table.
     * @param moduleScope
     *            The current module scope we are visiting.
     * @param context
     *            The verification context that contains all the information we have collected so far.
     * @param stGroup
     *            The string template group we will be using.
     * @param blockModel
     *            The model associated with {@code block}.
     */
    protected AbstractBlockDeclRule(AssertiveCodeBlock block, String declName,
            MathSymbolTableBuilder symbolTableBuilder, ModuleScope moduleScope, VerificationContext context,
            STGroup stGroup, ST blockModel) {
        super(block, context, stGroup, blockModel);
        myAffectedExps = new LinkedHashSet<>();
        myCurrentModuleScope = moduleScope;
        myDeclName = declName;
        myTypeGraph = symbolTableBuilder.getTypeGraph();
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * <p>
     * This method adds appropriate substitutions for affected {@code Shared Variables}.
     * </p>
     *
     * @param affectsClause
     *            The affects clause we are processing.
     * @param substitutionMap
     *            The substitution map where these expressions are needed.
     * @param booleanType
     *            Mathematical boolean type.
     *
     * @return An updated map.
     */
    protected final Map<Exp, Exp> addAffectedConceptualSharedVars(AffectsClause affectsClause,
            Map<Exp, Exp> substitutionMap, MTType booleanType) {
        if (affectsClause != null) {
            for (Exp exp : affectsClause.getAffectedExps()) {
                VarExp sharedVarExp = (VarExp) exp;
                OldExp odlSharedVarExp = new OldExp(sharedVarExp.getLocation().clone(), sharedVarExp.clone());
                odlSharedVarExp.setMathType(sharedVarExp.getMathType());

                // Create Conc.<sharedVarExp>
                VarExp concVarExp = Utilities.createVarExp(sharedVarExp.getLocation(), null,
                        new PosSymbol(sharedVarExp.getLocation().clone(), "Conc"), booleanType, null);
                List<Exp> segments = new ArrayList<>();
                segments.add(concVarExp);
                segments.add(sharedVarExp.clone());

                DotExp concSharedVarExp = new DotExp(sharedVarExp.getLocation().clone(), segments);
                concSharedVarExp.setMathType(sharedVarExp.getMathType());

                // Add this as a substitution
                substitutionMap = addConceptualVariables(sharedVarExp, odlSharedVarExp, concSharedVarExp,
                        substitutionMap);
            }
        }

        return substitutionMap;
    }

    /**
     * <p>
     * This method adds the appropriate substitutions for the different versions of {@code varExp}.
     * </p>
     *
     * @param varExp
     *            A variable expression.
     * @param oldVarExp
     *            The incoming version of {@code varExp}.
     * @param concVarExp
     *            The conceptual version of {@code varExp}.
     * @param substitutionMap
     *            The substitution map where these expressions are needed.
     *
     * @return An updated map.
     */
    protected final Map<Exp, Exp> addConceptualVariables(VarExp varExp, OldExp oldVarExp, DotExp concVarExp,
            Map<Exp, Exp> substitutionMap) {
        // Create an incoming version of the conceptual variable
        OldExp oldConcVarExp = new OldExp(concVarExp.getLocation().clone(), concVarExp.clone());
        oldConcVarExp.setMathType(concVarExp.getMathType());

        // Add these to our substitution map
        substitutionMap.put(varExp, concVarExp);
        substitutionMap.put(oldVarExp, oldConcVarExp);

        return substitutionMap;
    }

    /**
     * <p>
     * This method creates a {@code restores ensures} clause for any {@code shared variables} not being affected by the
     * current declaration.
     * </p>
     *
     * @param declLoc
     *            The current declaration's location.
     * @param exp
     *            The expression where we are going add the new {@code ensures} expressions to.
     *
     * @return A modified {@code ensures} clause with the new {@code restores} expression.
     */
    protected final Exp createFacilitySharedVarRestoresEnsuresExp(Location declLoc, Exp exp) {
        // Loop through all instantiated facility's and generate a "restores" ensures clause
        // for non-affected shared variables/math definition variables.
        Exp retExp = exp;
        for (InstantiatedFacilityDecl facilityDecl : myCurrentVerificationContext.getProcessedInstFacilityDecls()) {
            for (SharedStateDec stateDec : facilityDecl.getConceptSharedStates()) {
                for (MathVarDec mathVarDec : stateDec.getAbstractStateVars()) {
                    // Convert the math variables to variable expressions
                    VarExp stateVarExp = Utilities.createVarExp(declLoc.clone(),
                            facilityDecl.getInstantiatedFacilityName(), mathVarDec.getName(), mathVarDec.getMathType(),
                            null);
                    OldExp oldStateVarExp = new OldExp(declLoc.clone(), stateVarExp);
                    oldStateVarExp.setMathType(stateVarExp.getMathType());

                    // Add a "restores" mode to any shared variables not being affected
                    if (!Utilities.containsEquivalentExp(myAffectedExps, stateVarExp)) {
                        retExp = createRestoresExpForSharedVars(declLoc, stateVarExp, oldStateVarExp, retExp);
                    }
                }
            }

            for (TypeFamilyDec typeFamilyDec : facilityDecl.getConceptDeclaredTypes()) {
                for (MathDefVariableDec mathDefVariableDec : typeFamilyDec.getDefinitionVarList()) {
                    // Convert the math definition variables to variable expressions
                    MathVarDec mathVarDec = mathDefVariableDec.getVariable();
                    VarExp defVarExp = Utilities.createVarExp(declLoc.clone(),
                            facilityDecl.getInstantiatedFacilityName(), mathVarDec.getName(), mathVarDec.getMathType(),
                            null);
                    OldExp oldDefVarExp = new OldExp(declLoc.clone(), defVarExp);
                    oldDefVarExp.setMathType(defVarExp.getMathType());

                    // Add a "restores" mode to any definition variables not being affected
                    if (!Utilities.containsEquivalentExp(myAffectedExps, defVarExp)) {
                        retExp = createRestoresExpForDefVars(declLoc, defVarExp, oldDefVarExp, retExp);
                    }
                }
            }
        }

        return retExp;
    }

    /**
     * <p>
     * This method creates a new {@code ensures} expression that includes a {@code restores} mode clause for the given
     * math definition variable.
     * </p>
     *
     * @param declLoc
     *            The current declaration's location.
     * @param defVarExp
     *            A math definition variable as a {@link VarExp}.
     * @param oldDefVarExp
     *            The incoming value of {@code defVarExp}.
     * @param ensuresExp
     *            The current ensures clause we are building.
     *
     * @return A modified {@code ensures} clause with the new {@code restores} expression.
     */
    protected final Exp createRestoresExpForDefVars(Location declLoc, VarExp defVarExp, OldExp oldDefVarExp,
            Exp ensuresExp) {
        // Construct an expression using the expression and it's
        // old expression equivalent.
        Exp restoresConditionExp = new EqualsExp(declLoc.clone(), defVarExp.clone(), null, EqualsExp.Operator.EQUAL,
                oldDefVarExp.clone());
        restoresConditionExp.setMathType(myTypeGraph.BOOLEAN);
        restoresConditionExp.setLocationDetailModel(new LocationDetailModel(declLoc.clone(), declLoc.clone(),
                "Ensures Clause of " + myDeclName + " (Condition from Non-Affected Definition Variable)"));

        // Form a conjunct if needed.
        Exp retExp;
        if (VarExp.isLiteralTrue(ensuresExp)) {
            retExp = restoresConditionExp;
        } else {
            retExp = InfixExp.formConjunct(ensuresExp.getLocation(), ensuresExp, restoresConditionExp);
        }

        return retExp;
    }

    /**
     * <p>
     * This method creates a new {@code ensures} expression that includes a {@code restores} mode clause for the given
     * global state variable.
     * </p>
     *
     * @param declLoc
     *            The current declaration's location.
     * @param stateVarExp
     *            A global state variable as a {@link VarExp}.
     * @param oldStateVarExp
     *            The incoming value of {@code stateVarExp}.
     * @param ensuresExp
     *            The current ensures clause we are building.
     *
     * @return A modified {@code ensures} clause with the new {@code restores} expression.
     */
    protected final Exp createRestoresExpForSharedVars(Location declLoc, VarExp stateVarExp, OldExp oldStateVarExp,
            Exp ensuresExp) {
        // Update the location detail
        stateVarExp.setLocationDetailModel(new LocationDetailModel(stateVarExp.getLocation().clone(),
                stateVarExp.getLocation().clone(), "Outgoing value of " + stateVarExp));
        oldStateVarExp.setLocationDetailModel(new LocationDetailModel(oldStateVarExp.getLocation().clone(),
                oldStateVarExp.getLocation().clone(), "Incoming value of " + stateVarExp));

        // Construct an expression using the expression and it's
        // old expression equivalent.
        Exp restoresConditionExp = new EqualsExp(declLoc.clone(), stateVarExp.clone(), null, EqualsExp.Operator.EQUAL,
                oldStateVarExp.clone());
        restoresConditionExp.setMathType(myTypeGraph.BOOLEAN);
        restoresConditionExp.setLocationDetailModel(new LocationDetailModel(declLoc.clone(), declLoc.clone(),
                "Ensures Clause of " + myDeclName + " (Condition from Non-Affected Shared Variable)"));

        // Form a conjunct if needed.
        Exp retExp;
        if (VarExp.isLiteralTrue(ensuresExp)) {
            retExp = restoresConditionExp;
        } else {
            retExp = InfixExp.formConjunct(ensuresExp.getLocation(), ensuresExp, restoresConditionExp);
        }

        return retExp;
    }

    /**
     * <p>
     * This method processes all non-affected {@code Shared Variables} and/or {@code Def Vars} and generate the proper
     * {@code ensures} clause.
     * </p>
     * <p>
     * Note that this method should only be called by {@link TypeRepresentationInitRule} and
     * {@link TypeRepresentationFinalRule} due to the fact that it does substitutions for conceptual variables.
     * </p>
     *
     * @param ensuresLoc
     *            Location to be used for the new {@code ensures} clauses.
     * @param ensuresExp
     *            The current ensures clause we are building.
     * @param typeFamilyDec
     *            The current type family declaration we are processing.
     *
     * @return A modified {@code ensures} clause with {@code restores} mode {@code ensures} clause for each non-affected
     *         {@code Shared Variable} and/or {@code Def Vars}.
     */
    protected final Exp processNonAffectedVarsEnsures(Location ensuresLoc, Exp ensuresExp,
            TypeFamilyDec typeFamilyDec) {
        Exp retExp = ensuresExp.clone();

        // Create a replacement map for substituting parameter
        // variables with representation types.
        Map<Exp, Exp> substitutionParamToConc = new LinkedHashMap<>();

        // Loop through all shared variable declared from the
        // associated concept.
        List<SharedStateDec> sharedStateDecs = myCurrentVerificationContext.getConceptSharedVars();
        for (SharedStateDec stateDec : sharedStateDecs) {
            for (MathVarDec mathVarDec : stateDec.getAbstractStateVars()) {
                // Convert the math variables to variable expressions
                VarExp stateVarExp = Utilities.createVarExp(ensuresLoc.clone(), null, mathVarDec.getName(),
                        mathVarDec.getMathType(), null);
                OldExp oldStateVarExp = new OldExp(ensuresLoc.clone(), stateVarExp);
                oldStateVarExp.setMathType(stateVarExp.getMathType());

                // Add a "restores" mode to any shared variables not being affected
                if (!Utilities.containsEquivalentExp(myAffectedExps, stateVarExp)) {
                    retExp = createRestoresExpForSharedVars(ensuresLoc.clone(), stateVarExp, oldStateVarExp, retExp);
                    // Our ensures clause should say something about the conceptual
                    // shared variables so we create the appropriate conceptual versions
                    // of the shared variables and add them to our substitution maps.
                    DotExp concVarExp = Utilities.createConcVarExp(new VarDec(mathVarDec.getName(), mathVarDec.getTy()),
                            mathVarDec.getMathType(), myTypeGraph.BOOLEAN);
                    substitutionParamToConc = addConceptualVariables(stateVarExp, oldStateVarExp, concVarExp,
                            substitutionParamToConc);
                }
            }
        }

        // Generate a "restores" ensures clause for non-affected definition variables in our type family
        for (MathDefVariableDec mathDefVariableDec : typeFamilyDec.getDefinitionVarList()) {
            // Convert the math definition variables to variable expressions
            MathVarDec mathVarDec = mathDefVariableDec.getVariable();
            VarExp defVarExp = Utilities.createVarExp(ensuresLoc.clone(), null, mathVarDec.getName(),
                    mathVarDec.getMathType(), null);
            OldExp oldDefVarExp = new OldExp(ensuresLoc.clone(), defVarExp);
            oldDefVarExp.setMathType(defVarExp.getMathType());

            // Add a "restores" mode to any definition variables not being affected
            if (!Utilities.containsEquivalentExp(myAffectedExps, defVarExp)) {
                retExp = createRestoresExpForDefVars(ensuresLoc.clone(), defVarExp, oldDefVarExp, retExp);

                // Our ensures clause should say something about the conceptual
                // shared variables so we create the appropriate conceptual versions
                // of the shared variables and add them to our substitution maps.
                DotExp concVarExp = Utilities.createConcVarExp(new VarDec(mathVarDec.getName(), mathVarDec.getTy()),
                        mathVarDec.getMathType(), myTypeGraph.BOOLEAN);
                substitutionParamToConc = addConceptualVariables(defVarExp, oldDefVarExp, concVarExp,
                        substitutionParamToConc);
            }
        }

        // Loop through all instantiated facility's and generate a "restores" ensures clause
        // for non-affected shared variables/math definition variables.
        retExp = createFacilitySharedVarRestoresEnsuresExp(ensuresLoc.clone(), retExp);

        // Apply any substitution and return the modified expression
        return retExp.substitute(substitutionParamToConc);
    }
}

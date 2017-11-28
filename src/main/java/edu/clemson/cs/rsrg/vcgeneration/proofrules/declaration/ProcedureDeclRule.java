/*
 * ProcedureDeclRule.java
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
import edu.clemson.cs.rsrg.absyn.clauses.AssertionClause.ClauseType;
import edu.clemson.cs.rsrg.absyn.declarations.operationdecl.ProcedureDec;
import edu.clemson.cs.rsrg.absyn.declarations.typedecl.AbstractTypeRepresentationDec;
import edu.clemson.cs.rsrg.absyn.declarations.typedecl.TypeFamilyDec;
import edu.clemson.cs.rsrg.absyn.declarations.variabledecl.ParameterVarDec;
import edu.clemson.cs.rsrg.absyn.declarations.variabledecl.VarDec;
import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.absyn.expressions.mathexpr.*;
import edu.clemson.cs.rsrg.absyn.expressions.mathexpr.EqualsExp.Operator;
import edu.clemson.cs.rsrg.absyn.items.mathitems.SpecInitFinalItem;
import edu.clemson.cs.rsrg.absyn.rawtypes.NameTy;
import edu.clemson.cs.rsrg.absyn.statements.ConfirmStmt;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import edu.clemson.cs.rsrg.typeandpopulate.entry.*;
import edu.clemson.cs.rsrg.typeandpopulate.entry.ProgramParameterEntry.ParameterMode;
import edu.clemson.cs.rsrg.typeandpopulate.mathtypes.MTCartesian;
import edu.clemson.cs.rsrg.typeandpopulate.mathtypes.MTType;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.MathSymbolTableBuilder;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.ModuleScope;
import edu.clemson.cs.rsrg.typeandpopulate.typereasoning.TypeGraph;
import edu.clemson.cs.rsrg.vcgeneration.proofrules.AbstractProofRuleApplication;
import edu.clemson.cs.rsrg.vcgeneration.proofrules.ProofRuleApplication;
import edu.clemson.cs.rsrg.vcgeneration.proofrules.other.KnownTypeVariableFinalizationRule;
import edu.clemson.cs.rsrg.vcgeneration.utilities.AssertiveCodeBlock;
import edu.clemson.cs.rsrg.vcgeneration.utilities.LocationDetailModel;
import edu.clemson.cs.rsrg.vcgeneration.utilities.Utilities;
import edu.clemson.cs.rsrg.vcgeneration.utilities.formaltoactual.InstantiatedFacilityDecl;
import java.util.*;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

/**
 * <p>This class contains the logic for the {@code procedure}
 * declaration rule.</p>
 *
 * @author Yu-Shan Sun
 * @version 1.0
 */
public class ProcedureDeclRule extends AbstractProofRuleApplication
        implements
            ProofRuleApplication {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>This contains all the types declared by the {@code Concept}
     * associated with the current module. Note that if we are in a
     * {@code Facility}, this list will be empty.</p>
     */
    private final List<TypeFamilyDec> myCurrentConceptDeclaredTypes;

    /**
     * <p>The module scope for the file we are generating
     * {@code VCs} for.</p>
     */
    private final ModuleScope myCurrentModuleScope;

    /**
     * <p>If we are in a {@code Procedure} and it is an recursive
     * operation implementation, then this stores the decreasing clause
     * expression.</p>
     */
    private final Exp myCurrentProcedureDecreasingExp;

    /**
     * <p>The {@link OperationEntry} associated with this {@code If}
     * statement if we are inside a {@code ProcedureDec}.</p>
     */
    private final OperationEntry myCurrentProcedureOperationEntry;

    /**
     * <p>If our current module scope allows us to introduce new type implementations,
     * this will contain all the {@link AbstractTypeRepresentationDec}. Otherwise,
     * this list will be empty.</p>
     */
    private final List<AbstractTypeRepresentationDec> myLocalRepresentationTypeDecs;

    /** <p>The {@link ProcedureDec} we are applying the rule to.</p> */
    private final ProcedureDec myProcedureDec;

    /** <p>The list of processed {@link InstantiatedFacilityDecl}. </p> */
    private final List<InstantiatedFacilityDecl> myProcessedInstFacilityDecls;

    /**
     * <p>This stores all the local {@link VarDec VarDec's}
     * {@code finalization} specification item if we were able to generate one.</p>
     */
    private final Map<VarDec, SpecInitFinalItem> myVariableSpecFinalItems;

    /** <p>The symbol table we are currently building.</p> */
    private final MathSymbolTableBuilder mySymbolTable;

    /**
     * <p>This is the math type graph that indicates relationship
     * between different math types.</p>
     */
    private final TypeGraph myTypeGraph;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This creates a new application for the {@code procedure}
     * declaration rule.</p>
     *
     * @param procedureDec The {@link ProcedureDec} we are applying
     *                     the rule to.
     * @param currentProcedureOpEntry An {@link OperationEntry} with a {@code Procedure},
     *                                if {@code ifStmt} is inside one. Otherwise it should
     *                                be left as {@code null}.
     * @param currentProcedureDecreasingExp If we are in a {@code Procedure} and it is recursive,
     *                                      this is its {@code decreasing} clause expression.
     *                                      Otherwise it should be left as {@code null}.
     * @param procVarFinalItems The local variable declaration's
     *                          {@code finalization} specification items.
     * @param typeFamilyDecs List of abstract types we are implementing or extending.
     * @param localRepresentationTypeDecs List of local representation types.
     * @param processedInstFacDecs The list of processed {@link InstantiatedFacilityDecl}.
     * @param symbolTableBuilder The current symbol table.
     * @param moduleScope The current module scope we are visiting.
     * @param block The assertive code block that the subclasses are
     *              applying the rule to.
     * @param stGroup The string template group we will be using.
     * @param blockModel The model associated with {@code block}.
     */
    public ProcedureDeclRule(ProcedureDec procedureDec,
            OperationEntry currentProcedureOpEntry,
            Exp currentProcedureDecreasingExp,
            Map<VarDec, SpecInitFinalItem> procVarFinalItems,
            List<TypeFamilyDec> typeFamilyDecs,
            List<AbstractTypeRepresentationDec> localRepresentationTypeDecs,
            List<InstantiatedFacilityDecl> processedInstFacDecs,
            MathSymbolTableBuilder symbolTableBuilder, ModuleScope moduleScope,
            AssertiveCodeBlock block, STGroup stGroup, ST blockModel) {
        super(block, stGroup, blockModel);
        myCurrentConceptDeclaredTypes = typeFamilyDecs;
        myCurrentModuleScope = moduleScope;
        myCurrentProcedureDecreasingExp = currentProcedureDecreasingExp;
        myCurrentProcedureOperationEntry = currentProcedureOpEntry;
        myLocalRepresentationTypeDecs = localRepresentationTypeDecs;
        myProcessedInstFacilityDecls = processedInstFacDecs;
        mySymbolTable = symbolTableBuilder;
        myTypeGraph = symbolTableBuilder.getTypeGraph();
        myProcedureDec = procedureDec;
        myVariableSpecFinalItems = procVarFinalItems;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This method applies the {@code Proof Rule}.</p>
     */
    @Override
    public final void applyRule() {
        // TODO: Assume decreasing expression if it is recursive

        // Add all the statements
        myCurrentAssertiveCodeBlock.addStatements(myProcedureDec.getStatements());

        // TODO: Add the finalization duration ensures (if any)

        // TODO: Correct_Op_Hyp rule (Shared Variables and Type)
        // Correct_Op_Hyp rule: Only applies to non-local operations
        // in concept realizations.

        // TODO: Well_Def_Corr_Hyp rule (Shared Variables and Type)
        // Well_Def_Corr_Hyp rule: Rather than doing direct replacement,
        // we leave that logic to the parsimonious vc step. A replacement
        // will occur if this is a correspondence function or an implies
        // will be formed if this is a correspondence relation.

        // Add the variable finalization expressions
        List<VarDec> varDecs = myProcedureDec.getVariables();
        Map<Exp, Exp> newFreeVarSubstitutions = new LinkedHashMap<>();
        for (VarDec dec : varDecs) {
            if (myVariableSpecFinalItems.containsKey(dec)) {
                KnownTypeVariableFinalizationRule finalizationRule =
                        new KnownTypeVariableFinalizationRule(dec,
                                myVariableSpecFinalItems.get(dec),
                                myCurrentAssertiveCodeBlock, mySTGroup,
                                myBlockModel);
                finalizationRule.applyRule();

                // Add all the free variables.
                newFreeVarSubstitutions.putAll(finalizationRule.getNewFreeVarSubstitutions());
            }
        }

        // Create the final confirm expression
        Exp finalConfirmExp = createFinalConfirmExp();

        // Replace any facility declaration instantiation arguments
        // in the requires clause.
        finalConfirmExp =
                Utilities.replaceFacilityFormalWithActual(finalConfirmExp,
                        myProcedureDec.getParameters(), myCurrentModuleScope
                                .getDefiningElement().getName(),
                        myCurrentConceptDeclaredTypes,
                        myLocalRepresentationTypeDecs,
                        myProcessedInstFacilityDecls);

        // Replace any new free variables expressions generated by the
        // variable finalization rule.
        finalConfirmExp = finalConfirmExp.substitute(newFreeVarSubstitutions);

        // Use the ensures clause to create a final confirm statement
        ConfirmStmt finalConfirmStmt =
                new ConfirmStmt(myProcedureDec.getLocation().clone(), finalConfirmExp, false);
        myCurrentAssertiveCodeBlock.addStatement(finalConfirmStmt);

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
        return "Procedure Declaration Rule (Part 2)";
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>An helper method that helps create an conjunct expression and adds the associated
     * {@link LocationDetailModel} to our map.</p>
     *
     * <p>Note that {@code exp1} may be {@code null}. In that case, we simply create a
     * {@link LocationDetailModel} for {@code exp2} and return {@code exp2}.</p>
     *
     * @param srcLoc The {@link Location} where {@code exp2} was found.
     *               Used as the source location for the detail model.
     * @param conjunctLoc The {@link Location} to be used when forming a
     *                    conjunct expression.
     * @param exp1 The expression to appear on the left hand side of
     *             a conjunct expression. This may be {@code null}.
     * @param exp2 The expression to appear on the right hand side of
     *             a conjunct expression. This must be non-{@code null}
     *             and ideally not the {@code true} expression.
     * @param message The message associated with {@code exp2}.
     *
     * @return An {@link Exp}.
     */
    private Exp createConjunctExp(Location srcLoc, Location conjunctLoc,
            Exp exp1, Exp exp2, String message) {
        Exp retExp;

        if (exp1 == null) {
            retExp = exp2;

            // Add the location details for this expression.
            myLocationDetails.put(exp2.getLocation(), new LocationDetailModel(
                    srcLoc.clone(), exp2.getLocation(), message));
        }
        else {
            // Form a conjunct with our return expression
            InfixExp conjunctExp =
                    (InfixExp) MathExp.formConjunct(conjunctLoc.clone(), exp1,
                            exp2);

            // Add the location details for this expression.
            Location rightLoc = conjunctExp.getRight().getLocation();
            myLocationDetails.put(rightLoc, new LocationDetailModel(srcLoc
                    .clone(), rightLoc, message));

            // Store this as our return expression.
            retExp = conjunctExp;
        }

        return retExp;
    }

    /**
     * <p>An helper method that uses the {@code ensures} clause from the operation entry
     * and adds in additional {@code ensures} clauses for different parameter modes
     * and builds the appropriate {@code ensures} clause that will be an
     * {@link AssertiveCodeBlock AssertiveCodeBlock's} final {@code confirm} statement.</p>
     *
     * @return The final confirm expression.
     */
    private Exp createFinalConfirmExp() {
        Exp retExp = null;
        Location procedureLoc = myProcedureDec.getLocation();

        // Add the operation's ensures clause (and any which_entails clause)
        AssertionClause ensuresClause =
                myCurrentProcedureOperationEntry.getEnsuresClause();
        Exp ensuresExp = ensuresClause.getAssertionExp().clone();
        if (!VarExp.isLiteralTrue(ensuresExp)) {
            // Make a copy of our ensures clause and add the
            // associated location detail.
            retExp = ensuresClause.getAssertionExp().clone();
            myLocationDetails.put(retExp.getLocation(), new LocationDetailModel(
                    ensuresExp.getLocation().clone(), retExp.getLocation(),
                    "Ensures Clause of " + myCurrentProcedureOperationEntry.getName()));
        }

        // Loop through each of the parameters in the operation entry.
        Iterator<ProgramParameterEntry> specParamVarDecIt = myCurrentProcedureOperationEntry.getParameters().iterator();
        Iterator<ParameterVarDec> realizParamVarDecIt = myProcedureDec.getParameters().iterator();
        while (specParamVarDecIt.hasNext())  {
            // Information from the operation specification
            ProgramParameterEntry entry = specParamVarDecIt.next();
            ParameterVarDec parameterVarDec =
                    (ParameterVarDec) entry.getDefiningElement();
            ParameterMode parameterMode = entry.getParameterMode();
            NameTy nameTy = (NameTy) parameterVarDec.getTy();

            // Obtain the corresponding parameter declaration from
            // operation realization
            ParameterVarDec realizParamVarDec = realizParamVarDecIt.next();

            // Parameter variable and incoming parameter variable
            VarExp parameterExp = Utilities.createVarExp(parameterVarDec.getLocation().clone(), null,
                    parameterVarDec.getName().clone(), nameTy.getMathTypeValue(), null);
            OldExp oldParameterExp = new OldExp(parameterVarDec.getLocation().clone(), parameterExp.clone());
            oldParameterExp.setMathType(nameTy.getMathTypeValue());

            // Query for the type entry in the symbol table
            SymbolTableEntry ste =
                    Utilities.searchProgramType(procedureLoc, nameTy.getQualifier(),
                            nameTy.getName(), myCurrentModuleScope);

            ProgramTypeEntry typeEntry;
            if (ste instanceof ProgramTypeEntry) {
                typeEntry = ste.toProgramTypeEntry(nameTy.getLocation());
            } else {
                typeEntry =
                        ste.toTypeRepresentationEntry(nameTy.getLocation())
                                .getDefiningTypeEntry();
            }

            // The restores mode adds an additional ensures
            // that the outgoing value is equal to the incoming value.
            // Ex: w = #w
            if (parameterMode == ParameterMode.RESTORES) {
                // Set the details for the new location
                Location restoresLoc = realizParamVarDec.getLocation().clone();

                // Need to ensure here that the everything inside the type family
                // is restored at the end of the operation.
                Exp restoresConditionExp = null;
                if (typeEntry.getModelType() instanceof MTCartesian) {
                    MTCartesian cartesian =
                            (MTCartesian) typeEntry.getModelType();
                    List<MTType> elementTypes =
                            cartesian.getComponentTypes();

                    for (int i = 0; i < cartesian.size(); i++) {
                        // Create an Exp for the Cartesian product element
                        VarExp elementExp = Utilities.createVarExp(restoresLoc.clone(), null,
                                new PosSymbol(restoresLoc.clone(), cartesian.getTag(i)),
                                elementTypes.get(i), null);

                        // Create a list of segments. The first element should be the original
                        // parameterExp and oldParameterExp and the second element the cartesian product element.
                        List<Exp> segments = new ArrayList<>();
                        List<Exp> oldSegments = new ArrayList<>();
                        segments.add(parameterExp);
                        oldSegments.add(oldParameterExp);
                        segments.add(elementExp);
                        oldSegments.add(elementExp.clone());

                        // Create the dotted expressions
                        DotExp elementDotExp = new DotExp(restoresLoc.clone(), segments);
                        elementDotExp.setMathType(elementExp.getMathType());
                        DotExp oldElementDotExp = new DotExp(restoresLoc.clone(), oldSegments);
                        oldElementDotExp.setMathType(elementExp.getMathType());

                        // Create an equality expression
                        EqualsExp equalsExp = new EqualsExp(restoresLoc.clone(), elementDotExp, null,
                                Operator.EQUAL, oldElementDotExp);
                        equalsExp.setMathType(myTypeGraph.BOOLEAN);

                        // Add this to our final equals expression
                        if (restoresConditionExp == null) {
                            restoresConditionExp = equalsExp;
                        }
                        else {
                            restoresConditionExp = InfixExp.formConjunct(restoresLoc.clone(),
                                    restoresConditionExp, equalsExp);
                        }
                    }
                }
                else {
                    // Construct an expression using the expression and it's
                    // old expression equivalent.
                    restoresConditionExp =
                            new EqualsExp(restoresLoc.clone(), parameterExp.clone(), null,
                                    Operator.EQUAL, oldParameterExp.clone());
                    restoresConditionExp.setMathType(myTypeGraph.BOOLEAN);
                }

                // YS: This is complicated because locations change when we form a conjunct,
                //     but we really want the location details to match up.
                String message = "Ensures Clause of " + myCurrentProcedureOperationEntry.getName()
                        + " (Condition from \"" + parameterMode + "\" parameter mode)";
                retExp = createConjunctExp(parameterVarDec.getLocation(), procedureLoc,
                        retExp, restoresConditionExp, message);
            }
            // The clears mode adds an additional ensures
            // that the outgoing value is the initial value.
            else if (parameterMode == ParameterMode.CLEARS) {
                AssertionClause modifiedInitEnsures;
                if (typeEntry.getDefiningElement() instanceof TypeFamilyDec) {
                    // Parameter variable with known program type
                    TypeFamilyDec type =
                            (TypeFamilyDec) typeEntry.getDefiningElement();
                    AssertionClause initEnsures =
                            type.getInitialization().getEnsures();
                    modifiedInitEnsures =
                            Utilities.getTypeEnsuresClause(initEnsures,
                                    procedureLoc.clone(), null,
                                    parameterVarDec.getName(), type.getExemplar(),
                                    typeEntry.getModelType(), null);

                    // TODO: Logic for types in concept realizations
                }
                else {
                    VarDec parameterAsVarDec =
                            new VarDec(parameterVarDec.getName(), parameterVarDec.getTy());
                    modifiedInitEnsures =
                            new AssertionClause(procedureLoc.clone(), ClauseType.ENSURES,
                                    Utilities.createInitExp(parameterAsVarDec, myTypeGraph.BOOLEAN));
                }

                // YS: This is complicated because locations change when we form a conjunct,
                //     but we really want the location details to match up.
                String message = "Ensures Clause of " + myCurrentProcedureOperationEntry.getName()
                        + " (Condition from \"" + parameterMode + "\" parameter mode)";
                retExp = createConjunctExp(parameterVarDec.getLocation(), procedureLoc,
                        retExp, modifiedInitEnsures.getAssertionExp(), message);
            }

            // TODO: See below!
            // If the type is a type representation, then our requires clause
            // should really say something about the conceptual type and not
            // the variable
        }

        // Check to see if it is null. If that is the case, then we simply return "true"
        if (retExp == null) {
            retExp = VarExp.getTrueVarExp(procedureLoc.clone(), myTypeGraph);

            // Add the location details for this expression.
            Location retExpLoc = retExp.getLocation();
            myLocationDetails.put(retExpLoc, new LocationDetailModel(
                    procedureLoc.clone(), retExpLoc,
                    "Ensures Clause of "
                            + myCurrentProcedureOperationEntry.getName()));
        }

        return retExp;
    }
}
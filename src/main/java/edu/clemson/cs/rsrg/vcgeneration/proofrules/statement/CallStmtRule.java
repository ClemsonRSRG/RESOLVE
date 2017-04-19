/*
 * CallStmtRule.java
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
package edu.clemson.cs.rsrg.vcgeneration.proofrules.statement;

import edu.clemson.cs.r2jt.rewriteprover.immutableadts.ImmutableList;
import edu.clemson.cs.rsrg.absyn.clauses.AssertionClause;
import edu.clemson.cs.rsrg.absyn.declarations.operationdecl.OperationDec;
import edu.clemson.cs.rsrg.absyn.declarations.typedecl.TypeFamilyDec;
import edu.clemson.cs.rsrg.absyn.declarations.variabledecl.ParameterVarDec;
import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.absyn.expressions.mathexpr.InfixExp;
import edu.clemson.cs.rsrg.absyn.expressions.mathexpr.OldExp;
import edu.clemson.cs.rsrg.absyn.expressions.mathexpr.VarExp;
import edu.clemson.cs.rsrg.absyn.rawtypes.NameTy;
import edu.clemson.cs.rsrg.absyn.statements.CallStmt;
import edu.clemson.cs.rsrg.absyn.statements.ConfirmStmt;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import edu.clemson.cs.rsrg.typeandpopulate.entry.OperationEntry;
import edu.clemson.cs.rsrg.typeandpopulate.entry.ProgramParameterEntry;
import edu.clemson.cs.rsrg.typeandpopulate.entry.ProgramParameterEntry.ParameterMode;
import edu.clemson.cs.rsrg.typeandpopulate.entry.ProgramTypeEntry;
import edu.clemson.cs.rsrg.typeandpopulate.entry.SymbolTableEntry;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.ModuleScope;
import edu.clemson.cs.rsrg.vcgeneration.absyn.mathexpr.VCVarExp;
import edu.clemson.cs.rsrg.vcgeneration.absyn.statements.AssumeStmt;
import edu.clemson.cs.rsrg.vcgeneration.proofrules.AbstractProofRuleApplication;
import edu.clemson.cs.rsrg.vcgeneration.proofrules.ProofRuleApplication;
import edu.clemson.cs.rsrg.vcgeneration.utilities.Utilities;
import edu.clemson.cs.rsrg.vcgeneration.vcs.AssertiveCodeBlock;
import edu.clemson.cs.rsrg.vcgeneration.vcs.Sequent;
import java.util.*;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

/**
 * <p>This class contains the logic for applying the {@code call}
 * rule to a {@link CallStmt}.</p>
 *
 * @author Yu-Shan Sun
 * @version 1.0
 */
public class CallStmtRule extends AbstractProofRuleApplication
        implements
            ProofRuleApplication {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The {@link OperationEntry} this call statement is trying to call.</p> */
    private final OperationEntry myAssociatedOperationEntry;

    /** <p>The {@link CallStmt} we are applying the rule to.</p> */
    private final CallStmt myCallStmt;

    /**
     * <p>The module scope for the file we are generating
     * {@code VCs} for.</p>
     */
    private final ModuleScope myCurrentModuleScope;

    /**
     * <p>Since we could have nested function calls, this list contains
     * the new set of arguments with all those nested function calls
     * replaced with what they generate.</p>
     */
    private final List<Exp> myModifiedArguments;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This creates a new application of the {@code assume}
     * rule.</p>
     *
     * @param callStmt The {@link CallStmt} we are applying
     *                 the rule to.
     * @param associatedOpEntry The associated {@link OperationEntry}
     *                          that the {@code callStmt} is calling.
     * @param modifiedArguments The modified arguments that with all the nested
     *                          function calls taken care of.
     * @param moduleScope The current module scope we are visiting.
     * @param block The assertive code block that the subclasses are
     *              applying the rule to.
     * @param stGroup The string template group we will be using.
     * @param blockModel The model associated with {@code block}.
     */
    public CallStmtRule(CallStmt callStmt, OperationEntry associatedOpEntry,
            List<Exp> modifiedArguments, ModuleScope moduleScope,
            AssertiveCodeBlock block, STGroup stGroup, ST blockModel) {
        super(block, stGroup, blockModel);
        myAssociatedOperationEntry = associatedOpEntry;
        myCallStmt = callStmt;
        myCurrentModuleScope = moduleScope;
        myModifiedArguments = modifiedArguments;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This method applies the {@code Proof Rule}.</p>
     */
    @Override
    public final void applyRule() {
        OperationDec opDec = (OperationDec) myAssociatedOperationEntry.getDefiningElement();

        // Get the ensures clause for this operation
        AssertionClause ensuresClause = myAssociatedOperationEntry.getEnsuresClause();
        Exp ensuresExp =
                Utilities.formConjunct(ensuresClause.getLocation(), null, ensuresClause);

        /* TODO: Recursive call
        // Check for recursive call of itself
        if (myCurrentOperationEntry != null
                && myOperationDecreasingExp != null
                && myCurrentOperationEntry.getName().equals(opEntry.getName())
                && myCurrentOperationEntry.getReturnType().equals(
                opEntry.getReturnType())
                && myCurrentOperationEntry.getSourceModuleIdentifier().equals(
                opEntry.getSourceModuleIdentifier())) {
            // Create a new confirm statement using P_val and the decreasing clause
            VarExp pVal =
                    Utilities.createPValExp(myOperationDecreasingExp
                            .getLocation(), myCurrentModuleScope);

            // Create a new infix expression
            IntegerExp oneExp = new IntegerExp();
            oneExp.setValue(1);
            oneExp.setMathType(myOperationDecreasingExp.getMathType());
            InfixExp leftExp =
                    new InfixExp(stmt.getLocation(), oneExp, Utilities
                            .createPosSymbol("+"), Exp
                            .copy(myOperationDecreasingExp));
            leftExp.setMathType(myOperationDecreasingExp.getMathType());
            InfixExp exp =
                    Utilities.createLessThanEqExp(stmt.getLocation(), leftExp,
                            pVal, BOOLEAN);

            // Create the new confirm statement
            Location loc;
            if (myOperationDecreasingExp.getLocation() != null) {
                loc = (Location) myOperationDecreasingExp.getLocation().clone();
            }
            else {
                loc = (Location) stmt.getLocation().clone();
            }
            loc.setDetails("Show Termination of Recursive Call");
            Utilities.setLocation(exp, loc);
            ConfirmStmt conf = new ConfirmStmt(loc, exp, false);

            // Add it to our list of assertions
            myCurrentAssertiveCode.addCode(conf);
        }
        */

        // Get the requires clause for this operation
        AssertionClause requiresClause = myAssociatedOperationEntry.getRequiresClause();
        Exp requiresExp =
                Utilities.formConjunct(requiresClause.getLocation(), null, requiresClause);
        boolean simplify = VarExp.isLiteralTrue(requiresExp);

        // Replace PreCondition variables in the requires clause
        requiresExp =
                replaceFormalWithActualReq(requiresExp, opDec.getParameters(), myModifiedArguments);

        /* TODO:
        // Replace facility actuals variables in the requires clause
        requires =
                Utilities.replaceFacilityFormalWithActual(stmt.getLocation(),
                        requires, opDec.getParameters(),
                        myInstantiatedFacilityArgMap, myCurrentModuleScope); */

        // Loop through each of the parameters in the operation entry.
        Map<Exp, Exp> substitutionsForSeq = new HashMap<>();
        Map<Exp, Exp> substitutions = new HashMap<>();
        Exp parameterEnsures = null;
        Iterator<Exp> it = myModifiedArguments.iterator();
        ImmutableList<ProgramParameterEntry> entries =
                myAssociatedOperationEntry.getParameters();
        for (ProgramParameterEntry entry : entries) {
            ParameterVarDec parameterVarDec =
                    (ParameterVarDec) entry.getDefiningElement();
            ParameterMode parameterMode = entry.getParameterMode();
            NameTy nameTy = (NameTy) parameterVarDec.getTy();
            Location loc = parameterVarDec.getLocation();
            Exp argument = it.next();

            // TODO: Add the other parameter mode logic
            if (parameterMode == ParameterMode.UPDATES) {
                // Parameter variable and incoming parameter variable and NQV(parameterExp)
                VarExp parameterExp = Utilities.createVarExp(loc.clone(), null,
                        parameterVarDec.getName().clone(), nameTy.getMathTypeValue(), null);
                OldExp oldParameterExp = new OldExp(loc.clone(), parameterExp.clone());
                oldParameterExp.setMathType(nameTy.getMathTypeValue());
                VCVarExp nqvParameterExp = Utilities.createVCVarExp(myCurrentAssertiveCodeBlock, parameterExp);
                myCurrentAssertiveCodeBlock.addFreeVar(nqvParameterExp);

                // Add these to our substitutions map
                substitutions.put(parameterExp, nqvParameterExp);
                substitutions.put(oldParameterExp, Utilities.convertExp(argument, myCurrentModuleScope));

                // Add this as something to substitute in our sequents
                substitutionsForSeq.put(parameterExp.clone(), nqvParameterExp.clone());

                // Query for the type entry in the symbol table
                SymbolTableEntry ste =
                        Utilities.searchProgramType(parameterVarDec.getLocation(), nameTy.getQualifier(),
                                nameTy.getName(), myCurrentModuleScope);

                ProgramTypeEntry typeEntry;
                if (ste instanceof ProgramTypeEntry) {
                    typeEntry = ste.toProgramTypeEntry(nameTy.getLocation());
                } else {
                    typeEntry =
                            ste.toTypeRepresentationEntry(nameTy.getLocation())
                                    .getDefiningTypeEntry();
                }

                AssertionClause modifiedConstraint = null;
                if (typeEntry.getDefiningElement() instanceof TypeFamilyDec) {
                    // Parameter variable with known program type
                    TypeFamilyDec type =
                            (TypeFamilyDec) typeEntry.getDefiningElement();
                    AssertionClause constraint = type.getConstraint();
                    modifiedConstraint =
                            Utilities.getTypeConstraintClause(constraint,
                                    loc.clone(), null,
                                    parameterVarDec.getName(), type.getExemplar(),
                                    typeEntry.getModelType(), null);
                }
                else {
                    Utilities.notAType(typeEntry, parameterVarDec.getLocation());
                }

                if (!VarExp.isLiteralTrue(modifiedConstraint.getAssertionExp())) {
                    parameterEnsures =
                            Utilities.formConjunct(myCallStmt.getLocation(),
                                    parameterEnsures, modifiedConstraint);
                }
            }
        }

        /* TODO:
        // Replace facility actuals variables in the ensures clause
        ensures =
                Utilities.replaceFacilityFormalWithActual(stmt.getLocation(),
                        ensures, opDec.getParameters(),
                        myInstantiatedFacilityArgMap, myCurrentModuleScope); */

        /* TODO: Add duration
        // NY YS
        // Duration for CallStmt
        if (myInstanceEnvironment.flags.isFlagSet(FLAG_ALTPVCS_VC)) {
            Location loc = (Location) stmt.getLocation().clone();
            ConfirmStmt finalConfirm = myCurrentAssertiveCode.getFinalConfirm();
            Exp finalConfirmExp = finalConfirm.getAssertion();

            // Obtain the corresponding OperationProfileEntry
            OperationProfileEntry ope =
                    Utilities.searchOperationProfile(loc, stmt.getQualifier(),
                            stmt.getName(), argTypes, myCurrentModuleScope);

            // Add the profile ensures as additional assume
            Exp profileEnsures = ope.getEnsuresClause();
            if (profileEnsures != null) {
                profileEnsures =
                        replaceFormalWithActualEns(profileEnsures, opDec
                                        .getParameters(), opDec.getStateVars(),
                                replaceArgs, false);

                // Obtain the current location
                if (stmt.getName().getLocation() != null) {
                    // Set the details of the current location
                    Location ensuresLoc = (Location) loc.clone();
                    ensuresLoc.setDetails("Ensures Clause of "
                            + opDec.getName() + " from Profile "
                            + ope.getName());
                    Utilities.setLocation(profileEnsures, ensuresLoc);
                }

                ensures = myTypeGraph.formConjunct(ensures, profileEnsures);
            }

            // Construct the Duration Clause
            Exp opDur = Exp.copy(ope.getDurationClause());

            // Replace PostCondition variables in the duration clause
            opDur =
                    replaceFormalWithActualEns(opDur, opDec.getParameters(),
                            opDec.getStateVars(), replaceArgs, false);

            VarExp cumDur =
                    Utilities.createVarExp((Location) loc.clone(), null,
                            Utilities.createPosSymbol(Utilities
                                    .getCumDur(finalConfirmExp)),
                            myTypeGraph.R, null);
            Exp durCallExp =
                    Utilities.createDurCallExp((Location) loc.clone(), Integer
                                    .toString(opDec.getParameters().size()), Z,
                            myTypeGraph.R);
            InfixExp sumEvalDur =
                    new InfixExp((Location) loc.clone(), opDur, Utilities
                            .createPosSymbol("+"), durCallExp);
            sumEvalDur.setMathType(myTypeGraph.R);
            sumEvalDur =
                    new InfixExp((Location) loc.clone(), Exp.copy(cumDur),
                            Utilities.createPosSymbol("+"), sumEvalDur);
            sumEvalDur.setMathType(myTypeGraph.R);

            // For any evaluates mode expression, we need to finalize the variable
            edu.clemson.cs.r2jt.collections.List<ProgramExp> assignExpList =
                    stmt.getArguments();
            for (int i = 0; i < assignExpList.size(); i++) {
                ParameterVarDec p = opDec.getParameters().get(i);
                VariableExp pExp = (VariableExp) assignExpList.get(i);
                if (p.getMode() == Mode.EVALUATES) {
                    VarDec v =
                            new VarDec(Utilities.getVarName(pExp), p.getTy());
                    FunctionExp finalDur =
                            Utilities.createFinalizAnyDur(v, myTypeGraph.R);
                    sumEvalDur =
                            new InfixExp((Location) loc.clone(), sumEvalDur,
                                    Utilities.createPosSymbol("+"), finalDur);
                    sumEvalDur.setMathType(myTypeGraph.R);
                }
            }

            // Replace Cum_Dur in our final ensures clause
            finalConfirmExp =
                    Utilities.replace(finalConfirmExp, cumDur, sumEvalDur);
            myCurrentAssertiveCode.setFinalConfirm(finalConfirmExp,
                    finalConfirm.getSimplify());
        } */

        // We will need to confirm the requires clause
        ConfirmStmt confirmStmt =
                new ConfirmStmt(myCallStmt.getLocation().clone(), requiresExp, simplify);
        myCurrentAssertiveCodeBlock.addStatement(confirmStmt);

        // Store the location detail for the confirm statement
        myLocationDetails.put(confirmStmt.getLocation(), "Requires Clause of " + opDec.getName());

        if (parameterEnsures != null) {
            if (VarExp.isLiteralTrue(ensuresExp)) {
                ensuresExp = parameterEnsures;
            }
            else {
                ensuresExp =
                        InfixExp.formConjunct(myCallStmt.getLocation().clone(),
                                parameterEnsures, ensuresExp);
            }
        }
        ensuresExp = ensuresExp.substitute(substitutions);

        // We can assume the ensures clause.
        AssumeStmt assumeStmt =
                new AssumeStmt(myCallStmt.getLocation().clone(), ensuresExp, false);
        myCurrentAssertiveCodeBlock.addStatement(assumeStmt);

        // Store the location detail for the assume statement
        myLocationDetails.put(assumeStmt.getLocation(), "Ensures Clause of " + opDec.getName());

        // Use the sequent substitution map to do replacements
        List<Sequent> sequents = myCurrentAssertiveCodeBlock.getSequents();
        List<Sequent> newSequent = new ArrayList<>(sequents.size());
        for (Sequent s : sequents) {
            newSequent.add(createReplacementSequent(s, substitutionsForSeq));
        }

        // Store the new list of sequents
        myCurrentAssertiveCodeBlock.setSequents(newSequent);

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
        return "Call Rule";
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>An helper method that performs the substitution on all the
     * {@link Exp} in the {@link Sequent}.</p>
     *
     * @param s The original {@link Sequent}.
     * @param substitutions A map of substitutions.
     *
     * @return A modified {@link Sequent}.
     */
    private Sequent createReplacementSequent(Sequent s, Map<Exp, Exp> substitutions) {
        List<Exp> newAntecedents = new ArrayList<>();
        List<Exp> newConsequents = new ArrayList<>();

        for (Exp antencedent : s.getAntecedents()) {
            newAntecedents.add(antencedent.substitute(substitutions));
        }

        for (Exp consequent : s.getConcequents()) {
            newConsequents.add(consequent.substitute(substitutions));
        }

        return new Sequent(s.getLocation(), newAntecedents, newConsequents);
    }

    /**
     * <p>Replace the formal with the actual variables
     * inside the requires clause.</p>
     *
     * @param requires The requires clause.
     * @param paramList The list of parameter variables.
     * @param argList The list of arguments from the operation call.
     *
     * @return The requires clause in <code>Exp</code> form.
     */
    private Exp replaceFormalWithActualReq(Exp requires, List<ParameterVarDec> paramList, List<Exp> argList) {
        // YS: We need two replacement maps in case we happen to have the
        // same names in formal parameter arguments and in the argument list.
        Map<Exp, Exp> paramToTemp = new HashMap<>();
        Map<Exp, Exp> tempToActual = new HashMap<>();

        // Replace precondition variables in the requires clause
        for (int i = 0; i < argList.size(); i++) {
            ParameterVarDec varDec = paramList.get(i);
            Exp exp = argList.get(i);

            // Convert the pExp into a something we can use
            Exp repl = Utilities.convertExp(exp, myCurrentModuleScope);

            // VarExp form of the parameter variable
            VarExp oldExp =
                    Utilities.createVarExp(varDec.getLocation(), null,
                            varDec.getName(), exp.getMathType(), exp.getMathTypeValue());

            // New VarExp
            VarExp newExp =
                    Utilities.createVarExp(varDec.getLocation(), null,
                            new PosSymbol(varDec.getLocation(), "_" + varDec.getName().getName()),
                            repl.getMathType(), repl.getMathTypeValue());

            // Add a substitution entry from formal parameter to temp
            paramToTemp.put(oldExp, newExp);

            // Add a substitution entry from temp to actual parameter
            tempToActual.put(newExp, repl);
        }

        // Replace from formal to temp and then from temp to actual
        requires = requires.substitute(paramToTemp);
        requires = requires.substitute(tempToActual);

        return requires;
    }
}
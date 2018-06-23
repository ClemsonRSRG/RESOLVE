/*
 * ProgramFunctionExpWalker.java
 * ---------------------------------
 * Copyright (c) 2018
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.vcgeneration.utilities.treewalkers;

import edu.clemson.cs.rsrg.absyn.declarations.operationdecl.OperationDec;
import edu.clemson.cs.rsrg.absyn.declarations.sharedstatedecl.SharedStateDec;
import edu.clemson.cs.rsrg.absyn.declarations.variabledecl.MathVarDec;
import edu.clemson.cs.rsrg.absyn.declarations.variabledecl.ParameterVarDec;
import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.absyn.expressions.mathexpr.*;
import edu.clemson.cs.rsrg.absyn.expressions.programexpr.ProgramExp;
import edu.clemson.cs.rsrg.absyn.expressions.programexpr.ProgramFunctionExp;
import edu.clemson.cs.rsrg.absyn.statements.ConfirmStmt;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.parsing.data.LocationDetailModel;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import edu.clemson.cs.rsrg.statushandling.exception.SourceErrorException;
import edu.clemson.cs.rsrg.treewalk.TreeWalkerVisitor;
import edu.clemson.cs.rsrg.typeandpopulate.entry.OperationEntry;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.ModuleScope;
import edu.clemson.cs.rsrg.typeandpopulate.typereasoning.TypeGraph;
import edu.clemson.cs.rsrg.vcgeneration.utilities.Utilities;
import edu.clemson.cs.rsrg.vcgeneration.utilities.VerificationContext;
import edu.clemson.cs.rsrg.vcgeneration.utilities.formaltoactual.InstantiatedFacilityDecl;
import java.util.*;

/**
 * <p>This class extracts ensures clauses (with the appropriate substitutions)
 * from walking potentially nested {@link ProgramFunctionExp}. This visitor logic
 * is implemented as a {@link TreeWalkerVisitor}.</p>
 *
 * <p><strong>Note:</strong> We don't have to worry about any shared variables
 * being affected. By definition a function operation can't have side effects,
 * therefore modifying a shared variables would count as a side-effect.</p>
 *
 * @author Yu-Shan Sun
 * @version 2.0
 */
public class ProgramFunctionExpWalker extends TreeWalkerVisitor {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>The module scope for the file we are generating
     * {@code VCs} for.</p>
     */
    private final ModuleScope myCurrentModuleScope;

    /** <p>The current procedure declaration we are processing.</p> */
    private final OperationEntry myCurrentOperationEntry;

    /**
     * <p>The {@link VerificationContext} where all the information for the
     * current {@code Assertive Code Block} is located.</p>
     */
    private final VerificationContext myCurrentVerificationContext;

    /**
     * <p>If this is a {@code Recursive Procedure}, then this will contain
     * the {@code decreasing} clause.</p>
     */
    private final Exp myDecreasingExp;

    /**
     * <p>A map that contains the modified ensures clause with the formal
     * replaced with the actuals for each of the nested function calls.</p>
     */
    private final Map<ProgramFunctionExp, Exp> myEnsuresClauseMap;

    /**
     * <p>A list that contains the modified requires clauses with the formal
     * replaced with the actuals for each of the nested function calls.</p>
     */
    private final List<Exp> myRequiresClauseList;

    /** <p>A list that contains any generated termination {@code Confirm} statements.</p> */
    private final List<ConfirmStmt> myTerminationConfirmStmts;

    /**
     * <p>This is the math type graph that indicates relationship
     * between different math types.</p>
     */
    private final TypeGraph myTypeGraph;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This creates a {@link TreeWalkerVisitor} that visits
     * and generates {@code requires} and {@code ensures} clauses
     * for potentially nested function calls.</p>
     *
     * <p>Note that this constructor is used by non-recursive declarations,
     * where there isn't a {@code decreasing} clause.</p>
     *
     * @param context The verification context that contains all
     *                the information we have collected so far.
     * @param moduleScope The current module scope we are visiting.
     * @param g The current type graph.
     */
    public ProgramFunctionExpWalker(VerificationContext context,
            ModuleScope moduleScope, TypeGraph g) {
        this(null, null, context, moduleScope, g);
    }

    /**
     * <p>This creates a {@link TreeWalkerVisitor} that visits
     * and generates {@code requires} and {@code ensures} clauses
     * for potentially nested function calls.</p>
     *
     * @param entry The current visiting {@code Procedure} declaration's
     *              {@link OperationEntry}.
     * @param decreasingExp The {@code decreasing} clause for the visiting
     * @param context The verification context that contains all
     *                the information we have collected so far.
     * @param moduleScope The current module scope we are visiting.
     * @param g The current type graph.
     */
    public ProgramFunctionExpWalker(OperationEntry entry, Exp decreasingExp,
            VerificationContext context, ModuleScope moduleScope, TypeGraph g) {
        myCurrentModuleScope = moduleScope;
        myCurrentOperationEntry = entry;
        myCurrentVerificationContext = context;
        myDecreasingExp = decreasingExp;
        myEnsuresClauseMap = new HashMap<>();
        myRequiresClauseList = new LinkedList<>();
        myTerminationConfirmStmts = new LinkedList<>();
        myTypeGraph = g;
    }

    // ===========================================================
    // Visitor Methods
    // ===========================================================

    // -----------------------------------------------------------
    // Program Expression-Related
    // -----------------------------------------------------------

    /**
     * <p>Code that gets executed after visiting a {@link ProgramFunctionExp}.</p>
     *
     * @param exp A program function expression.
     */
    @Override
    public final void postProgramFunctionExp(ProgramFunctionExp exp) {
        // Create a string with the qualifier (if any) and the operation name
        String fullOperationName = exp.getName().getName();
        if (exp.getQualifier() != null) {
            fullOperationName = exp.getQualifier() + "::" + fullOperationName;
        }

        // Call a method to locate the operation entry for this call
        OperationEntry operationEntry =
                Utilities.getOperationEntry(exp, myCurrentModuleScope);
        OperationDec operationDec = operationEntry.getOperationDec();

        // YS: It is possible we don't have any parameters and still have
        //     shared variables to replace. If that is the case, we add
        //     those to our map and perform the replacement.
        //     If it is not empty, then "replaceFacilityFormalWithActual"
        //     we take care of the replacement.
        Map<Exp, Exp> substitutionFacSharedVars = new LinkedHashMap<>();
        if (exp.getQualifier() != null && operationDec.getParameters().isEmpty()) {
            PosSymbol qualifier = exp.getQualifier().clone();
            for (InstantiatedFacilityDecl decl : myCurrentVerificationContext.getProcessedInstFacilityDecls()) {
                if (decl.getInstantiatedFacilityName().getName().equals(qualifier.getName())) {
                    // Replace any shared variables
                    for (SharedStateDec stateDec : decl.getConceptSharedStates()) {
                        for (MathVarDec varDec : stateDec.getAbstractStateVars()) {
                            // Construct the qualified and not qualified version of varDec
                            VarExp varDecAsVarExp =
                                    Utilities.createVarExp(exp.getLocation().clone(),
                                            null, varDec.getName(),
                                            varDec.getMathType(), null);
                            VarExp qualifiedVarExp = (VarExp) varDecAsVarExp.clone();
                            qualifiedVarExp.setQualifier(qualifier);

                            // Add it to our substitution map
                            substitutionFacSharedVars.put(varDecAsVarExp, qualifiedVarExp);
                        }
                    }
                }
            }
        }

        // Only need to do something if it is not "requires true"
        if (!VarExp.isLiteralTrue(operationDec.getRequires().getAssertionExp())) {
            // Replace formals in the original requires clause with the
            // actuals from the function call.
            Exp requiresExp =
                    replaceFormalWithActualReq(operationDec.getRequires()
                            .getAssertionExp(), operationDec.getParameters(),
                            exp.getArguments());

            // Store the location detail for the function call's requires clause
            requiresExp.setLocationDetailModel(new LocationDetailModel(
                    operationDec.getRequires().getLocation().clone(), exp
                            .getLocation().clone(), "Requires Clause of "
                            + fullOperationName));

            // Replace any facility declaration instantiation arguments
            // in the requires clause.
            requiresExp =
                    Utilities.replaceFacilityFormalWithActual(requiresExp,
                            operationDec.getParameters(), myCurrentModuleScope
                                    .getDefiningElement().getName(),
                            myCurrentVerificationContext);

            // Apply any substitutions that are in our map
            requiresExp = requiresExp.substitute(substitutionFacSharedVars);

            // Store the modified requires clause in our list
            myRequiresClauseList.add(requiresExp);
        }

        // YS: The ensures clause was sanity checked already, so no need to do it here.
        // Replace formals in the original ensures clause with the actuals
        // from the function call.
        Exp ensuresExp =
                replaceFormalWithActualEns(operationDec.getEnsures()
                        .getAssertionExp(), operationDec.getParameters(), exp
                        .getArguments());

        // Store the location detail for the function call's ensures clause
        ensuresExp.setLocationDetailModel(new LocationDetailModel(operationDec
                .getEnsures().getLocation().clone(), exp.getLocation().clone(),
                "Ensures Clause of " + fullOperationName));

        // Replace any facility declaration instantiation arguments
        // in the ensures clause.
        ensuresExp =
                Utilities.replaceFacilityFormalWithActual(ensuresExp,
                        operationDec.getParameters(), myCurrentModuleScope
                                .getDefiningElement().getName(),
                        myCurrentVerificationContext);

        // Apply any substitutions that are in our map
        ensuresExp = ensuresExp.substitute(substitutionFacSharedVars);

        // Store the modified ensures clause in our map
        myEnsuresClauseMap.put(exp, ensuresExp);

        // Check to see if this function is calling itself recursively
        // and generate the appropriate termination VC.
        if (myCurrentOperationEntry != null
                && myCurrentOperationEntry.equals(operationEntry)) {
            generateTerminationConfirmStmt(exp);
        }
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This method returns the final modified ensures clause
     * after all the necessary replacement/substitutions have been made.</p>
     *
     * <p>Note that the return value is removed from the class and
     * subsequent calls to this method with the same {@code exp}
     * will result in an exception.</p>
     *
     * @param exp The outermost {@link ProgramFunctionExp} that we wish to
     *            extract an {@code ensures} clause for.
     *
     * @return The complete ensures clause as an {@link Exp}.
     *
     * @throws SourceErrorException This is thrown when we can't locate the
     * ensures clause for {@code exp}.
     */
    public final Exp getEnsuresClause(ProgramFunctionExp exp) {
        Exp ensures;

        // Attempt to locate the ensures clause for exp
        if (myEnsuresClauseMap.containsKey(exp)) {
            ensures = formConditionExp(myEnsuresClauseMap.remove(exp));
        }
        else {
            throw new SourceErrorException(
                    "[VCGenerator] Cannot locate the ensures clause for: "
                            + exp.toString()
                            + ". Our ensures clause map contains: "
                            + myEnsuresClauseMap.toString(), exp.getLocation());
        }

        return ensures;
    }

    /**
     * <p>This method returns the final modified requires clause
     * after all the necessary replacement/substitutions have been made.</p>
     *
     * @param loc The location to be stored inside the requires clause
     *            generated by this walker.
     *
     * @return The complete requires clause as an {@link Exp}.
     */
    public final Exp getRequiresClause(Location loc) {
        Exp allRequiresExp = VarExp.getTrueVarExp(loc.clone(), myTypeGraph);
        for (Exp exp : myRequiresClauseList) {
            // Replace allRequiresExp if it is still "true"
            if (VarExp.isLiteralTrue(allRequiresExp)) {
                allRequiresExp = exp.clone();
            }
            // Else form a conjunct.
            else {
                allRequiresExp =
                        InfixExp.formConjunct(loc.clone(), allRequiresExp, exp
                                .clone());
            }
        }

        return allRequiresExp;
    }

    /**
     * <p>This method returns the list of termination
     * {@code Confirm} clauses (if any).</p>
     *
     * @return {@code Confirm} clauses generated from
     * recursive calls to our current {@code Procedure}
     * declaration.
     */
    public final List<ConfirmStmt> getTerminationConfirmStmts() {
        return myTerminationConfirmStmts;
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>An helper method for sanity checking the generated expression from
     * the {@link ProgramFunctionExpWalker}.</p>
     *
     * @param generatedExp The generated {@code ensures} clause
     *                     expression.
     *
     * @return The appropriate mathematical form of
     * the condition {@link Exp}.
     */
    private Exp formConditionExp(Exp generatedExp) {
        Exp retExp;

        // Make sure we have an EqualsExp, else it is an error.
        if (generatedExp instanceof EqualsExp) {
            // Has to be a VarExp on the left hand side (containing the name
            // of the function operation)
            EqualsExp generatedExpAsEqualsExp = (EqualsExp) generatedExp;
            if (generatedExpAsEqualsExp.getLeft() instanceof VarExp) {
                retExp = generatedExpAsEqualsExp.getRight().clone();
            }
            else {
                // Something went wrong with the program function walker.
                // We should have generated an equals expression containing the
                // results of the program function call.
                throw new SourceErrorException(
                        "[VCGenerator] Condition expression: "
                                + generatedExp.toString()
                                + " is not of the form: <OperationName> = <expression> "
                                + generatedExp.getLocation(), generatedExp
                                .getLocation());
            }
        }
        else {
            // Something went wrong with the program function walker.
            // We should have generated an equals expression containing the
            // results of the program function call.
            throw new SourceErrorException(
                    "[VCGenerator] Condition expression: "
                            + generatedExp.toString()
                            + " is not an equivalence expression "
                            + generatedExp.getLocation(), generatedExp
                            .getLocation());
        }

        return retExp;
    }

    /**
     * <p>An helper method that generates termination {@code Confirm} statements.</p>
     *
     * @param functionExp A program function expression.
     */
    private void generateTerminationConfirmStmt(ProgramFunctionExp functionExp) {
        // Make sure we have a decreasing clause
        if (myDecreasingExp == null) {
            throw new SourceErrorException(
                    "[VCGenerator] Cannot locate the decreasing clause for: "
                            + functionExp.toString(), functionExp.getLocation());
        }
        else {
            // Generate the termination of recursive call: 1 + P_Exp <= P_Val
            VarExp pValExp =
                    Utilities.createPValExp(myDecreasingExp.getLocation()
                            .clone(), myCurrentModuleScope);
            IntegerExp oneExp =
                    new IntegerExp(myDecreasingExp.getLocation().clone(), null,
                            1);
            oneExp.setMathType(myDecreasingExp.getMathType());

            InfixExp sumExp =
                    new InfixExp(myDecreasingExp.getLocation().clone(), oneExp,
                            null, new PosSymbol(myDecreasingExp.getLocation()
                                    .clone(), "+"), myDecreasingExp.clone());
            sumExp.setMathType(myDecreasingExp.getMathType());

            InfixExp terminationExp =
                    new InfixExp(myDecreasingExp.getLocation().clone(), sumExp,
                            null, new PosSymbol(myDecreasingExp.getLocation()
                                    .clone(), "<="), pValExp.clone());
            terminationExp.setMathType(myTypeGraph.BOOLEAN);

            // Store the location detail for the recursive function call's
            // termination expression.
            terminationExp.setLocationDetailModel(new LocationDetailModel(
                    myDecreasingExp.getLocation().clone(), functionExp
                            .getLocation().clone(),
                    "Termination of Recursive Call"));

            // Generate a new ConfirmStmt using terminationExp
            ConfirmStmt confirmStmt =
                    new ConfirmStmt(terminationExp.getLocation().clone(),
                            terminationExp, false);
            myTerminationConfirmStmts.add(confirmStmt);
        }
    }

    /**
     * <p>An helper method that replaces the formal with the actual variables
     * inside the {@code ensures} clause.</p>
     *
     * @param ensures The {@code ensures} clause as an {@link Exp}.
     * @param paramList The list of parameter variables.
     * @param argList The list of arguments from the operation call.
     *
     * @return A potentially modified {@code ensures} clause
     * represented using an {@link Exp}.
     */
    private Exp replaceFormalWithActualEns(Exp ensures,
            List<ParameterVarDec> paramList, List<ProgramExp> argList) {
        // YS: We need two replacement maps in case we happen to have the
        // same names in formal parameter arguments and in the argument list.
        Map<Exp, Exp> paramToTemp = new HashMap<>();
        Map<Exp, Exp> tempToActual = new HashMap<>();

        // Replace postcondition variables in the ensures clause
        for (int i = 0; i < argList.size(); i++) {
            ParameterVarDec varDec = paramList.get(i);
            Exp exp = argList.get(i);

            // If we happen to have a nested function call as argument, then
            // simply look inside our ensures clause map for the new modified
            // ensures clause.
            Exp replExp;
            if (exp instanceof ProgramFunctionExp) {
                // Check to see if we have an ensures clause
                // for this nested call
                if (myEnsuresClauseMap.containsKey(exp)) {
                    // The replacement will be the inner operation's
                    // ensures clause. We are done processing the
                    // inner function call, so we can remove it from our map.
                    replExp = formConditionExp(myEnsuresClauseMap.remove(exp));
                }
                else {
                    // Something went wrong with the walking mechanism.
                    // We should have seen this inner operation call before
                    // processing the outer operation call.
                    throw new SourceErrorException("[VCGenerator] Could not find the modified ensures clause of: " +
                            exp.toString(), exp.getLocation());
                }
            }
            // All other types of expressions
            else {
                // Convert the exp into a something we can use
                replExp = Utilities.convertExp(exp, myCurrentModuleScope);
            }

            // VarExp form of the parameter variable
            VarExp paramExpAsVarExp =
                    Utilities.createVarExp(varDec.getLocation(), null,
                            varDec.getName(), exp.getMathType(), exp.getMathTypeValue());

            // A temporary VarExp that avoids any formal with the same name as the actual.
            VarExp tempExp =
                    Utilities.createVarExp(varDec.getLocation(), null,
                            new PosSymbol(varDec.getLocation(), "_" + varDec.getName().getName()),
                            replExp.getMathType(), replExp.getMathTypeValue());

            // Add a substitution entry from formal parameter to tempExp.
            paramToTemp.put(paramExpAsVarExp, tempExp);

            // Add a substitution entry from tempExp to actual parameter.
            tempToActual.put(tempExp, replExp);
        }

        // Replace from formal to temp and then from temp to actual
        ensures = ensures.substitute(paramToTemp);
        ensures = ensures.substitute(tempToActual);

        return ensures;
    }

    /**
     * <p>An helper method that replaces the formal with the actual variables
     * inside the {@code requires} clause.</p>
     *
     * @param requires The {@code requires} clause as an {@link Exp}.
     * @param paramList The list of parameter variables.
     * @param argList The list of arguments from the operation call.
     *
     * @return A potentially modified {@code requires} clause
     * represented using an {@link Exp}.
     */
    private Exp replaceFormalWithActualReq(Exp requires,
            List<ParameterVarDec> paramList, List<ProgramExp> argList) {
        // YS: We need two replacement maps in case we happen to have the
        // same names in formal parameter arguments and in the argument list.
        Map<Exp, Exp> paramToTemp = new HashMap<>();
        Map<Exp, Exp> tempToActual = new HashMap<>();

        // Replace precondition variables in the requires clause
        for (int i = 0; i < argList.size(); i++) {
            ParameterVarDec varDec = paramList.get(i);
            Exp exp = argList.get(i);

            // If we happen to have a nested function call as argument, then
            // simply look inside our ensures clause map for the new modified
            // ensures clause for any replacements in the requires clause.
            Exp replExp;
            if (exp instanceof ProgramFunctionExp) {
                // Check to see if we have an ensures clause
                // for this nested call
                if (myEnsuresClauseMap.containsKey(exp)) {
                    // The replacement will be the inner operation's
                    // ensures clause. Notice that we only do a "get"
                    // and not a "remove". We still need this for when
                    // we process the processing function call's ensures clause.
                    replExp = myEnsuresClauseMap.get(exp);
                }
                else {
                    // Something went wrong with the walking mechanism.
                    // We should have seen this inner operation call before
                    // processing the outer operation call.
                    throw new SourceErrorException("[VCGenerator] Could not find the modified ensures clause of: " +
                            exp.toString(), exp.getLocation());
                }
            }
            // All other types of expressions
            else {
                // Convert the exp into a something we can use
                replExp = Utilities.convertExp(exp, myCurrentModuleScope);
            }

            // VarExp form of the parameter variable
            VarExp paramExpAsVarExp =
                    Utilities.createVarExp(varDec.getLocation(), null,
                            varDec.getName(), exp.getMathType(), exp.getMathTypeValue());

            // A temporary VarExp that avoids any formal with the same name as the actual.
            VarExp tempExp =
                    Utilities.createVarExp(varDec.getLocation(), null,
                            new PosSymbol(varDec.getLocation(), "_" + varDec.getName().getName()),
                            replExp.getMathType(), replExp.getMathTypeValue());

            // Add a substitution entry from formal parameter to tempExp.
            paramToTemp.put(paramExpAsVarExp, tempExp);

            // Add a substitution entry from tempExp to actual parameter.
            tempToActual.put(tempExp, replExp);
        }

        // Replace from formal to temp and then from temp to actual
        requires = requires.substitute(paramToTemp);
        requires = requires.substitute(tempToActual);

        return requires;
    }
}
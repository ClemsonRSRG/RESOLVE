/*
 * ProgramFunctionExpWalker.java
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
package edu.clemson.cs.rsrg.vcgeneration.utilities.treewalkers;

import edu.clemson.cs.rsrg.absyn.declarations.operationdecl.OperationDec;
import edu.clemson.cs.rsrg.absyn.declarations.variabledecl.ParameterVarDec;
import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.absyn.expressions.mathexpr.EqualsExp;
import edu.clemson.cs.rsrg.absyn.expressions.mathexpr.EqualsExp.Operator;
import edu.clemson.cs.rsrg.absyn.expressions.mathexpr.InfixExp;
import edu.clemson.cs.rsrg.absyn.expressions.mathexpr.OldExp;
import edu.clemson.cs.rsrg.absyn.expressions.mathexpr.VarExp;
import edu.clemson.cs.rsrg.absyn.expressions.programexpr.ProgramExp;
import edu.clemson.cs.rsrg.absyn.expressions.programexpr.ProgramFunctionExp;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import edu.clemson.cs.rsrg.statushandling.exception.MiscErrorException;
import edu.clemson.cs.rsrg.treewalk.TreeWalkerVisitor;
import edu.clemson.cs.rsrg.typeandpopulate.entry.OperationEntry;
import edu.clemson.cs.rsrg.typeandpopulate.entry.ProgramParameterEntry.ParameterMode;
import edu.clemson.cs.rsrg.typeandpopulate.programtypes.PTType;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.ModuleScope;
import edu.clemson.cs.rsrg.typeandpopulate.typereasoning.TypeGraph;
import edu.clemson.cs.rsrg.vcgeneration.utilities.Utilities;
import java.util.*;

/**
 * <p>This class extracts ensures clauses (with the appropriate substitutions)
 * from walking potentially nested {@link ProgramFunctionExp}. This visitor logic
 * is implemented as a {@link TreeWalkerVisitor}.</p>
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

    /**
     * <p>A map that contains the modified ensures clause with the formal
     * replaced with the actuals for each of the nested function calls.</p>
     */
    private final Map<ProgramFunctionExp, Exp> myEnsuresClauseMap;

    /**
     * <p>A map that stores all the details associated with
     * a particular {@link Location}.</p>
     */
    private final Map<Location, String> myLocationDetails;

    /**
     * <p>A list that contains the modified requires clauses with the formal
     * replaced with the actuals for each of the nested function calls.</p>
     */
    private final List<Exp> myRequiresClauseList;

    /** <p>A list that contains all the restores parameter ensures clauses</p> */
    private final List<Exp> myRestoresParamEnsuresClauses;

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
     * @param moduleScope The current module scope we are visiting.
     * @param g The current type graph.
     */
    public ProgramFunctionExpWalker(ModuleScope moduleScope, TypeGraph g) {
        myCurrentModuleScope = moduleScope;
        myEnsuresClauseMap = new HashMap<>();
        myRequiresClauseList = new LinkedList<>();
        myRestoresParamEnsuresClauses = new LinkedList<>();
        myLocationDetails = new HashMap<>();
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

        // Call a method to locate the operation declaration for this call
        OperationDec operationDec = getOperationDec(exp);

        // Only need to do something if it is not "requires true"
        if (!VarExp.isLiteralTrue(operationDec.getRequires().getAssertionExp())) {
            // Replace formals in the original requires clause with the
            // actuals from the function call.
            Exp requiresExp =
                    replaceFormalWithActualReq(operationDec.getRequires()
                            .getAssertionExp(), operationDec.getParameters(),
                            exp.getArguments());

            // TODO: Replace any facility declaration actuals in the requires clause.

            // TODO: Append the name of the declaration that is making this function call.
            String details = "";

            // Store the location detail for the function call's requires clause
            myLocationDetails.put(requiresExp.getLocation(),
                    "Requires Clause of " + fullOperationName + details);

            // Store the modified requires clause in our list
            myRequiresClauseList.add(requiresExp);
        }

        // YS: The ensures clause was sanity checked already, so no need to do it here.
        // Replace formals in the original ensures clause with the actuals
        // from the function call.

        // Add any ensures clauses for restores parameter to our restores parameter
        // ensures clause list.
        generateRestoresParamEnsuresClause(fullOperationName, operationDec
                .getParameters(), exp.getArguments());
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
     * @throws MiscErrorException This is thrown when we can't locate the
     * ensures clause for {@code exp}.
     */
    public final Exp getEnsuresClause(ProgramFunctionExp exp) {
        Exp ensures;

        // Attempt to locate the ensures clause for exp
        if (myEnsuresClauseMap.containsKey(exp)) {
            ensures = myEnsuresClauseMap.remove(exp);
        }
        else {
            throw new MiscErrorException(
                    "[VCGenerator] Cannot locate the ensures clause for: "
                            + exp.toString()
                            + ". Our ensures clause map contains: "
                            + myEnsuresClauseMap.toString(),
                    new RuntimeException());
        }

        return ensures;
    }

    /**
     * <p>This method returns a map containing details about
     * a {@link Location} object that was generated while visiting
     * function calls.</p>
     *
     * @return A map from {@link Location} to location detail strings.
     */
    public final Map<Location, String> getNewLocationString() {
        return myLocationDetails;
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
     * <p>This method returns the list of @code restores} parameter's
     * ensures clauses (if any).</p>
     *
     * @return {@code Ensures} clauses generated from {@code restores}
     * parameters.
     */
    public final List<Exp> getRestoresParamEnsuresClauses() {
        return myRestoresParamEnsuresClauses;
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>An helper method that generates {@code ensures} clauses for any parameters
     * with {@code restores} parameter mode.</p>
     *
     * @param opName Name of the operation we are calling.
     * @param paramList The list of parameter variables.
     * @param argList The list of arguments from the operation call.
     */
    private void generateRestoresParamEnsuresClause(String opName,
            List<ParameterVarDec> paramList, List<ProgramExp> argList) {
        for (int i = 0; i < argList.size(); i++) {
            ParameterVarDec varDec = paramList.get(i);
            Exp exp = argList.get(i);

            // Only do this if it is a restores parameter mode
            if (varDec.getMode().equals(ParameterMode.RESTORES)) {
                // YS: Can safely cast this as VarExp because it is the only thing that
                // we can pass to a restores parameter.
                VarExp expAsVarExp =
                        (VarExp) Utilities
                                .convertExp(exp, myCurrentModuleScope);
                OldExp oldExp =
                        new OldExp(expAsVarExp.getLocation().clone(),
                                expAsVarExp.clone());

                // Generate the restores parameter ensures clause and
                // store the new location detail.
                EqualsExp equalsExp =
                        new EqualsExp(expAsVarExp.getLocation().clone(),
                                expAsVarExp, null, Operator.EQUAL, oldExp);
                myLocationDetails.put(equalsExp.getLocation(),
                        "Ensures Clause of " + opName + " (Condition from \""
                                + ParameterMode.RESTORES.name()
                                + "\" parameter mode)");
                myRestoresParamEnsuresClauses.add(equalsExp);
            }
        }
    }

    /**
     * <p>An helper method that returns {@link ProgramFunctionExp ProgramFunctionExp's}
     * corresponding {@link OperationDec}.</p>
     *
     * @param functionExp A program function expression.
     *
     * @return The corresponding {@link OperationDec}.
     */
    private OperationDec getOperationDec(ProgramFunctionExp functionExp) {
        // Obtain the corresponding OperationEntry and OperationDec
        List<PTType> argTypes = new LinkedList<>();
        for (ProgramExp arg : functionExp.getArguments()) {
            argTypes.add(arg.getProgramType());
        }
        OperationEntry opEntry =
                Utilities.searchOperation(functionExp.getLocation(),
                        functionExp.getQualifier(), functionExp.getName(),
                        argTypes, myCurrentModuleScope);

        return (OperationDec) opEntry.getDefiningElement();
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
                    replExp = myEnsuresClauseMap.remove(exp);
                }
                else {
                    // Something went wrong with the walking mechanism.
                    // We should have seen this inner operation call before
                    // processing the outer operation call.
                    throw new MiscErrorException("[VCGenerator] Could not find the modified ensures clause of: " +
                            exp.toString() + " " + exp.getLocation(), new RuntimeException());
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
                    throw new MiscErrorException("[VCGenerator] Could not find the modified ensures clause of: " +
                            exp.toString() + " " + exp.getLocation(), new RuntimeException());
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
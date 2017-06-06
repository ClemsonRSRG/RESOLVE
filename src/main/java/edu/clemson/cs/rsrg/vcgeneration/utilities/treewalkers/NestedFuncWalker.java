/*
 * NestedFuncWalker.java
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

import edu.clemson.cs.rsrg.absyn.clauses.AssertionClause;
import edu.clemson.cs.rsrg.absyn.declarations.operationdecl.OperationDec;
import edu.clemson.cs.rsrg.absyn.declarations.variabledecl.ParameterVarDec;
import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.absyn.expressions.mathexpr.VarExp;
import edu.clemson.cs.rsrg.absyn.expressions.programexpr.ProgramExp;
import edu.clemson.cs.rsrg.absyn.expressions.programexpr.ProgramFunctionExp;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import edu.clemson.cs.rsrg.statushandling.exception.MiscErrorException;
import edu.clemson.cs.rsrg.treewalk.TreeWalkerVisitor;
import edu.clemson.cs.rsrg.typeandpopulate.entry.OperationEntry;
import edu.clemson.cs.rsrg.typeandpopulate.programtypes.PTType;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.ModuleScope;
import edu.clemson.cs.rsrg.vcgeneration.utilities.Utilities;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * <p>This class extracts ensures clauses (with the appropriate substitutions)
 * from walking nested {@link ProgramFunctionExp}. This visitor logic is implemented
 * as a {@link TreeWalkerVisitor}.</p>
 *
 * @author Yu-Shan Sun
 * @version 2.0
 */
public class NestedFuncWalker extends TreeWalkerVisitor {

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

    // ===========================================================
    // Constructors
    // ===========================================================

    public NestedFuncWalker(ModuleScope moduleScope) {
       myCurrentModuleScope = moduleScope;
        myEnsuresClauseMap = new HashMap<>();
    }

    // ===========================================================
    // Visitor Methods
    // ===========================================================

    // -----------------------------------------------------------
    // Program Expression-Related
    // -----------------------------------------------------------

    /**
     * <p>Code that gets executed before visiting a {@link ProgramFunctionExp}.</p>
     *
     * @param exp A program function expression.
     */
    @Override
    public final void preProgramFunctionExp(ProgramFunctionExp exp) {}

    /**
     * <p>Code that gets executed after visiting a {@link ProgramFunctionExp}.</p>
     *
     * @param exp A program function expression.
     */
    @Override
    public final void postProgramFunctionExp(ProgramFunctionExp exp) {}

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This method returns the final modified ensures clause
     * after all the necessary replacement/substitutions have been made.</p>
     *
     * @return The complete ensures clause.
     */
    public final AssertionClause getEnsuresClause() {
        return null;
    }

    /**
     * <p>This method returns the final modified requires clause
     * after all the necessary replacement/substitutions have been made.</p>
     *
     * @return The complete requires clause.
     */
    public final AssertionClause getRequiresClause() {
        return null;
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

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
     * inside the requires clause.</p>
     *
     * @param requires The requires clause.
     * @param paramList The list of parameter variables.
     * @param argList The list of arguments from the operation call.
     *
     * @return The requires clause in <code>Exp</code> form.
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
            // ensures clause.
            Exp replExp;
            if (exp instanceof ProgramFunctionExp) {
                // Check to see if we have an ensures clause
                // for this nested call
                if (myEnsuresClauseMap.containsKey(exp)) {
                    // The replacement will be the inner operation's
                    // ensures clause.
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

            // Add a substitution entry from formal parameter to temp
            paramToTemp.put(paramExpAsVarExp, tempExp);

            // Add a substitution entry from temp to actual parameter
            tempToActual.put(tempExp, replExp);
        }

        // Replace from formal to temp and then from temp to actual
        requires = requires.substitute(paramToTemp);
        requires = requires.substitute(tempToActual);

        return requires;
    }
}
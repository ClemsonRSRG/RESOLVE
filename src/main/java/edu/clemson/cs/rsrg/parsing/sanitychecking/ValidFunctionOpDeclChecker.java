/*
 * ValidFunctionOpDeclChecker.java
 * ---------------------------------
 * Copyright (c) 2020
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.parsing.sanitychecking;

import edu.clemson.cs.rsrg.absyn.declarations.operationdecl.OperationDec;
import edu.clemson.cs.rsrg.absyn.declarations.variabledecl.ParameterVarDec;
import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.absyn.expressions.mathexpr.EqualsExp;
import edu.clemson.cs.rsrg.absyn.expressions.mathexpr.EqualsExp.Operator;
import edu.clemson.cs.rsrg.absyn.expressions.mathexpr.VarExp;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.statushandling.exception.SourceErrorException;
import edu.clemson.cs.rsrg.typeandpopulate.entry.ProgramParameterEntry.ParameterMode;

/**
 * <p>
 * This is a sanity checker for making sure the {@link OperationDec} is valid
 * function operation
 * declaration.
 * </p>
 *
 * @author Yu-Shan Sun
 * @version 1.0
 */
public class ValidFunctionOpDeclChecker {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * The function operation declaration to be checked.
     * </p>
     */
    private final OperationDec myFunctionOperationDec;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * Creates a sanity checker for checking the various different criteria for
     * being a valid function
     * operation declaration.
     * </p>
     *
     * @param functionOperation The associated function operation declaration.
     */
    public ValidFunctionOpDeclChecker(OperationDec functionOperation) {
        myFunctionOperationDec = functionOperation;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * Checks to see if the provided {@link OperationDec} is a valid function
     * operation declaration. A
     * valid function operation declaration must have an {@code ensures} clause
     * of the form:
     * {@code <FuncOpDeclName> = <Expression/Value>}. It also shouldn't affect
     * any global state
     * variables.
     * </p>
     *
     * @throws SourceErrorException This is thrown if it is not a valid function
     *         operation
     *         declaration. The message will indicate why it is not valid.
     */
    public final void checkFunctionOpDecl() {
        Location funcOpLoc = myFunctionOperationDec.getLocation();
        String funcOpName = myFunctionOperationDec.getName().getName();
        Exp ensuresExp = myFunctionOperationDec.getEnsures().getAssertionExp();

        // 1. Check to see if it affects any global state variables
        if (myFunctionOperationDec.getAffectedVars() != null) {
            throw new SourceErrorException("Function operation: " + funcOpName
                    + " cannot contain an affects clause.", funcOpLoc);
        }

        // 2. Check to see if the parameter modes are either PRESERVES, RESTORES or EVALUATES
        for (ParameterVarDec varDec : myFunctionOperationDec.getParameters()) {
            ParameterMode varDecMode = varDec.getMode();
            if (varDecMode.equals(ParameterMode.ALTERS)
                    || varDecMode.equals(ParameterMode.CLEARS)
                    || varDecMode.equals(ParameterMode.REPLACES)
                    || varDecMode.equals(ParameterMode.UPDATES)) {
                throw new SourceErrorException("Function parameter: "
                        + varDec.getName().getName()
                        + ", must have either PRESERVES, RESTORES or EVALUATES mode.",
                        varDec.getLocation());
            }
        }

        // 3. Make sure we don't have "ensures true"
        if (!VarExp.isLiteralTrue(ensuresExp)) {
            // 4. Make sure it is an EqualsExp with the "=" operator
            if (ensuresExp instanceof EqualsExp && ((EqualsExp) ensuresExp)
                    .getOperator().equals(Operator.EQUAL)) {
                EqualsExp ensuresExpAsEqualsExp = (EqualsExp) ensuresExp;
                // 5. Make sure the function name is used in the ensures clause
                if (ensuresExpAsEqualsExp.containsVar(funcOpName, false)) {
                    Exp leftExp = ensuresExpAsEqualsExp.getLeft();
                    Exp rightExp = ensuresExpAsEqualsExp.getRight();

                    // 6. Make sure that the left hand side is a VarExp with the
                    // function operation as the name.
                    if (leftExp instanceof VarExp && ((VarExp) leftExp)
                            .getName().getName().equals(funcOpName)) {
                        // 7. Make sure that the function name isn't on the right hand side.
                        if (rightExp.containsVar(funcOpName, false)) {
                            throw new SourceErrorException(
                                    "Function operation name: " + funcOpName
                                            + " can only appear on the left hand side of the ensures clause. "
                                            + "The ensures clause must be of the form: '"
                                            + funcOpName
                                            + " = <Expression/Value>'",
                                    funcOpLoc);
                        }
                    }
                    else {
                        throw new SourceErrorException(
                                "Function operation name: " + funcOpName
                                        + " must be the only thing on the left hand side of the equals expression. "
                                        + "The ensures clause must be of the form: '"
                                        + funcOpName + " = <Expression/Value>'",
                                funcOpLoc);
                    }
                }
                else {
                    throw new SourceErrorException("Function operation name: "
                            + funcOpName + " not found in the ensures clause. "
                            + "The ensures clause must be of the form: '"
                            + funcOpName + " = <Expression/Value>'", funcOpLoc);
                }
            }
            else {
                throw new SourceErrorException(
                        "Function operation: " + funcOpName
                                + " must have an ensures clause of the form: '"
                                + funcOpName + " = <Expression/Value>'",
                        funcOpLoc);
            }
        }
        else {
            throw new SourceErrorException("Function operation: " + funcOpName
                    + " cannot have 'ensures true;' as its specification.",
                    funcOpLoc);
        }
    }

}

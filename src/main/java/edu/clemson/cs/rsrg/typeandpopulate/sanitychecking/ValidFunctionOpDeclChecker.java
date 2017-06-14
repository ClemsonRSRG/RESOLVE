/*
 * ValidFunctionOpDeclChecker.java
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
package edu.clemson.cs.rsrg.typeandpopulate.sanitychecking;

import edu.clemson.cs.rsrg.absyn.declarations.operationdecl.OperationDec;
import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.absyn.expressions.mathexpr.EqualsExp;
import edu.clemson.cs.rsrg.absyn.expressions.mathexpr.EqualsExp.Operator;
import edu.clemson.cs.rsrg.absyn.expressions.mathexpr.VarExp;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.statushandling.exception.SourceErrorException;

/**
 * <p>This is a sanity checker for making sure the {@link OperationDec}
 * is valid function operation declaration.</p>
 *
 * @author Yu-Shan Sun
 * @version 1.0
 */
public class ValidFunctionOpDeclChecker {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The function operation declaration to be checked.</p> */
    private final OperationDec myFunctionOperationDec;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>Creates a sanity checker for checking the various different criteria
     * for being a valid function operation declaration.</p>
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
     * <p>Checks to see if the provided {@link OperationDec} is a
     * valid function operation declaration. A valid function
     * operation declaration must have an {@code ensures} clause
     * of the form: {@code <FuncOpDeclName> = <Expression/Value>}.</p>
     *
     * @throws SourceErrorException This is thrown if it is not a valid
     * function operation declaration. The message will indicate why it is
     * not valid.
     */
    public final void checkFunctionOpDecl() {
        Location funcOpLoc = myFunctionOperationDec.getLocation();
        String funcOpName = myFunctionOperationDec.getName().getName();
        Exp ensuresExp = myFunctionOperationDec.getEnsures().getAssertionExp();

        // 1. Make sure we don't have "ensures true"
        if (!VarExp.isLiteralTrue(ensuresExp)) {
            // 2. Make sure it is an EqualsExp with the "=" operator
            if (ensuresExp instanceof EqualsExp
                    && ((EqualsExp) ensuresExp).getOperator().equals(
                            Operator.EQUAL)) {
                EqualsExp ensuresExpAsEqualsExp = (EqualsExp) ensuresExp;
                // 3. Make sure the function name is used in the ensures clause
                if (ensuresExpAsEqualsExp.containsVar(funcOpName, false)) {

                }
                else {
                    throw new SourceErrorException(
                            "Function operation name: "
                                    + funcOpName
                                    + " not found in the ensures clause. The ensures clause must be of the form: '"
                                    + funcOpName + " = <Expression/Value>'",
                            funcOpLoc);
                }
            }
            else {
                throw new SourceErrorException("Function operation: "
                        + funcOpName
                        + " must have an ensures clause of the form: '"
                        + funcOpName + " = <Expression/Value>'", funcOpLoc);
            }
        }
        else {
            throw new SourceErrorException("Function operation: " + funcOpName
                    + " cannot have 'ensures true;' as its specification.",
                    funcOpLoc);
        }
    }

}
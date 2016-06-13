/**
 * SwapStmtGenerator.java
 * ---------------------------------
 * Copyright (c) 2016
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.parsing.utilities;

import edu.clemson.cs.rsrg.absyn.expressions.programexpr.ProgramVariableExp;
import edu.clemson.cs.rsrg.absyn.statements.CallStmt;
import edu.clemson.cs.rsrg.absyn.statements.Statement;
import edu.clemson.cs.rsrg.absyn.statements.SwapStmt;
import edu.clemson.cs.rsrg.parsing.TreeBuildingListener;
import edu.clemson.cs.rsrg.parsing.data.Location;

/**
 * <p>The main purpose of this class is to assist the {@link TreeBuildingListener}
 * in building the various different swap statements.</p>
 *
 * <p>Most of the time, we are simply building a regular {@link SwapStmt}. However,
 * we might have syntactic sugar that needs to be replaced with swap calls in
 * {@code Static_Array_Template}.</p>
 *
 * <p>This class will take care of the various different scenarios where we might
 * find an array expression and handle it appropriately.</p>
 *
 * @author Yu-Shan Sun
 * @version 1.0
 */
public class SwapStmtGenerator {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>Location for the new statement.</p> */
    private final Location myStmtLocation;

    /** <p>The program variable expression that is on the left.</p> */
    private final ProgramVariableExp myProgLeftExp;

    /** <p>The program variable expression that is on the right.</p> */
    private final ProgramVariableExp myProgRightExp;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs an object that will take care of generating
     * the appropriate swap statement.</p>
     *
     * @param l The location for the new statement.
     * @param leftExp The left variable expression.
     * @param rightExp The right variable expression.
     */
    public SwapStmtGenerator(Location l, ProgramVariableExp leftExp,
            ProgramVariableExp rightExp) {
        myStmtLocation = l;
        myProgLeftExp = leftExp;
        myProgRightExp = rightExp;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This method either generates a regular {@link SwapStmt} or
     * a {@link CallStmt} to an operation in {@code Static_Array_Template} depending
     * on what we have as our left and right expressions.</p>
     *
     * @return A {@link Statement} representation object.
     */
    public Statement buildStatement() {
        // TODO: Build an statement for each of the cases.
        Statement newStatement = null;

        // Case #1: ProgramVariableExp :=: ProgramArrayExp or
        // Same if the left and right are exchanged.
        // (ie: x :=: A[i], where "A" is an array, "i" is
        // index and "x" is a variable.)

        // Case #2: ProgramVariableExp :=: ProgramArrayExp
        // but the array is inside a ProgramVariableDotExp.
        // Same if the left and right are exchanged.
        // (ie: x :=: S.A[i], where "S" is a record,
        // "A" is an array, "i" is index and
        // "x" is a variable.)

        // Case #3: ProgramArrayExp :=: ProgramArrayExp
        // (ie: A[i] :=: A[j], where "A" is an array and
        // "i" and "j" are indexes)

        // Case #4: ProgramArrayExp :=: ProgramVariableDotExp
        // Same if the left and right are exchanged.
        // (ie: A[i] :=: S.x, where "S" is a record,
        // "A" is an array, "i" is index and "x" is a variable.)

        // Case #5: Both left and right are ProgramVariableDotExp

        // Case #5a: ProgramArrayExp :=: ProgramArrayExp,
        // but the arrays are inside a ProgramVariableDotExp.
        // (ie: S.A[i] :=: S.A[j], where "S" is a record,
        // "A" is an array and "i" and "j" are indexes)

        // Case #5b: ProgramVariableExp :=: ProgramArrayExp,
        // but the variable and arrays are inside a ProgramVariableDotExp.
        // Same if the left and right are exchanged.
        // (ie: S.x :=: S.A[i], where "S" is a record,
        // "A" is an array and "i" is index and "x" is a variable.)

        // Case #6: If it is not cases 1-5, then we build a regular
        // SwapStmt.
        if (newStatement == null) {
            newStatement =
                    new SwapStmt(myStmtLocation, myProgLeftExp, myProgRightExp);
        }

        return newStatement;
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

}
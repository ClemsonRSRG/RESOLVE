/**
 * ArrayConversionUtilities.java
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

import edu.clemson.cs.rsrg.absyn.expressions.programexpr.*;
import edu.clemson.cs.rsrg.absyn.statements.CallStmt;
import edu.clemson.cs.rsrg.absyn.statements.FuncAssignStmt;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>The main purpose of this class is to assist the {@link SyntacticSugarConverter}
 * in building the various different calls to operations in
 * {@code Static_Array_Template}.</p>
 *
 * <p>It also provides various helper methods to identify and transform
 * {@link ProgramVariableArrayExp}s.</p>
 *
 * @author Yu-Shan Sun
 * @version 1.0
 */
public class ArrayConversionUtilities {

    // ===========================================================
    // Public Methods
    // ===========================================================

    public static CallStmt buildAssignEntryCall() {
        return null;
    }

    public static CallStmt buildEntryReplicaCall() {
        return null;
    }

    /**
     * <a>An helper method to create a {@link FuncAssignStmt} to store
     * the array indexes.</a>
     *
     * @param l Location for the new statement.
     * @param variableExp The variable to be assigned to.
     * @param indexExp The array index expression.
     *
     * @return A {@link FuncAssignStmt} object.
     */
    public static FuncAssignStmt buildStoreArrayIndexStmt(
            Location l, ProgramVariableExp variableExp, ProgramExp indexExp) {
        // Call "Replica" if it is not already some kind
        // of ProgramFunctionExp that returns an integer.
        ProgramExp newIndexExp;
        if (!(indexExp instanceof ProgramFunctionExp)) {
            Location indexLocation = indexExp.getLocation();
            List<ProgramExp> args = new ArrayList<>();
            args.add(indexExp.clone());

            newIndexExp = new ProgramFunctionExp(new Location(indexLocation), null,
                    new PosSymbol(new Location(indexLocation), "Replica"), args);
        }
        else {
            newIndexExp = indexExp.clone();
        }

        return new FuncAssignStmt(new Location(l), variableExp, newIndexExp);
    }

    public static CallStmt buildSwapEntryCall() {
        return null;
    }

    public static CallStmt buildSwapTwoEntriesCall() {
        return null;
    }

    /**
     * <p>An helper method to check whether or not the {@link ProgramExp} passed
     * in is a {@link ProgramVariableArrayExp}. This includes {@link ProgramVariableDotExp}
     * that contain a {@link ProgramVariableArrayExp} as the last element.</p>
     *
     * @param exp The {@link ProgramExp} to be checked.
     *
     * @return {@code true} if it is a programming array expression, {@code false} otherwise.
     */
    public static boolean isProgArrayExp(ProgramExp exp) {
        boolean retVal = false;
        if (exp instanceof ProgramVariableArrayExp) {
            retVal = true;
        }
        else if (exp instanceof ProgramVariableDotExp) {
            List<ProgramVariableExp> segments =
                    ((ProgramVariableDotExp) exp).getSegments();
            if (segments.get(segments.size() - 1) instanceof ProgramVariableArrayExp) {
                retVal = true;
            }
        }

        return retVal;
    }

}
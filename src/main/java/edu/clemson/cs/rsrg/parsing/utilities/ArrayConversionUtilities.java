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

import edu.clemson.cs.rsrg.absyn.expressions.programexpr.ProgramExp;
import edu.clemson.cs.rsrg.absyn.expressions.programexpr.ProgramVariableArrayExp;
import edu.clemson.cs.rsrg.absyn.expressions.programexpr.ProgramVariableDotExp;
import edu.clemson.cs.rsrg.absyn.expressions.programexpr.ProgramVariableExp;
import edu.clemson.cs.rsrg.absyn.statements.Statement;
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

    public static Statement buildAssignEntryCall() {
        return null;
    }

    public static Statement buildEntryReplicaCall() {
        return null;
    }

    public static Statement buildSwapEntryCall() {
        return null;
    }

    public static Statement buildSwapTwoEntriesCall() {
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
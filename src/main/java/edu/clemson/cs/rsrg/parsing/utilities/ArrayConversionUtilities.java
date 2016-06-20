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

import edu.clemson.cs.rsrg.absyn.declarations.variabledecl.VarDec;
import edu.clemson.cs.rsrg.absyn.expressions.programexpr.*;
import edu.clemson.cs.rsrg.absyn.rawtypes.Ty;
import edu.clemson.cs.rsrg.absyn.statements.CallStmt;
import edu.clemson.cs.rsrg.errorhandling.exception.MiscErrorException;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import java.util.ArrayList;
import java.util.Iterator;
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

    /**
     * <p>An helper method to create a call to the {@code Assign_Entry}
     * operation in {@code Static_Array_Template}.</p>
     *
     * @param l Location for the new elements.
     * @param assignExp Expression to be assigned to.
     * @param facQualifier The facility name of the array.
     * @param arrayNameExp The array name expression.
     * @param arrayIndexExp The array index expression.
     *
     * @return A {@link ProgramFunctionExp} object.
     */
    public static CallStmt buildAssignEntryCall(
            Location l, ProgramExp assignExp, PosSymbol facQualifier,
            ProgramVariableExp arrayNameExp, ProgramExp arrayIndexExp) {
        List<ProgramExp> args = new ArrayList<>();
        args.add(arrayNameExp.clone());
        args.add(assignExp.clone());
        args.add(arrayIndexExp.clone());

        return new CallStmt(new Location(l), new ProgramFunctionExp(new Location(l),
                facQualifier, new PosSymbol(new Location(l), "Assign_Entry"), args));
    }

    /**
     * <p>An helper method to create a call to the {@code Entry_Replica}
     * operation in {@code Static_Array_Template}.</p>
     *
     * @param l Location for the new elements.
     * @param facQualifier The facility name of the array.
     * @param arrayNameExp The array name expression.
     * @param arrayIndexExp The array index expression.
     *
     * @return A {@link ProgramFunctionExp} object.
     */
    public static ProgramFunctionExp buildEntryReplicaCall(
            Location l, PosSymbol facQualifier,
            ProgramVariableExp arrayNameExp, ProgramExp arrayIndexExp) {
        List<ProgramExp> args = new ArrayList<>();
        args.add(arrayNameExp.clone());
        args.add(arrayIndexExp.clone());

        return new ProgramFunctionExp(new Location(l), facQualifier,
                new PosSymbol(new Location(l), "Entry_Replica"), args);
    }

    /**
     * <p>An helper method to create a call to the {@code Swap_Entry}
     * operation in {@code Static_Array_Template}.</p>
     *
     * @param l Location for the new elements.
     * @param varExp Name of the variable to swap the contents of.
     * @param facQualifier The facility name of the array.
     * @param arrayNameExp The array name expression.
     * @param arrayIndexExp The array index expression.
     *
     * @return A {@link CallStmt} object.
     */
    public static CallStmt buildSwapEntryCall(
            Location l, ProgramVariableExp varExp, PosSymbol facQualifier,
            ProgramVariableExp arrayNameExp, ProgramExp arrayIndexExp) {
        List<ProgramExp> args = new ArrayList<>();
        args.add(arrayNameExp.clone());
        args.add(varExp.clone());
        args.add(arrayIndexExp.clone());

        return new CallStmt(new Location(l), new ProgramFunctionExp(new Location(l),
                facQualifier, new PosSymbol(new Location(l), "Swap_Entry"), args));
    }

    /**
     * <p>An helper method to create a call to the {@code Swap_Two_Entries}
     * operation in {@code Static_Array_Template}.</p>
     *
     * @param l Location for the new elements.
     * @param facQualifier The facility name of the array.
     * @param arrayNameExp The array name expression.
     * @param arrayIndexExp1 The first array index expression.
     * @param arrayIndexExp2 The second array index expression.
     *
     * @return A {@link CallStmt} object.
     */
    public static CallStmt buildSwapTwoEntriesCall(
            Location l, PosSymbol facQualifier, ProgramVariableExp arrayNameExp,
            ProgramExp arrayIndexExp1, ProgramExp arrayIndexExp2) {
        List<ProgramExp> args = new ArrayList<>();
        args.add(arrayNameExp.clone());
        args.add(arrayIndexExp1.clone());
        args.add(arrayIndexExp2.clone());

        return new CallStmt(new Location(l), new ProgramFunctionExp(new Location(l),
                facQualifier, new PosSymbol(new Location(l), "Swap_Two_Entries"), args));
    }

    /**
     * <p>An helper method to build a new {@link VarDec} to store the value
     * in the array expression resulting from calling the operations in
     * {@code Static_Array_Template}.</p>
     *
     * @param arrayNameExp The {@link ProgramVariableExp} containing the array name.
     * @param arrayContentType The {@link Ty} of the array's contents.
     * @param counter An integer value that helps us create distinct
     *                new variables.
     *
     * @return A {@link VarDec} object.
     *
     * @exception MiscErrorException
     */
    public static VarDec buildTempArrayNameVarDec(
            ProgramVariableExp arrayNameExp, Ty arrayContentType, int counter) {
        StringBuffer sb = new StringBuffer();
        sb.append("_");

        if (arrayNameExp instanceof ProgramVariableNameExp) {
            sb.append(((ProgramVariableNameExp) arrayNameExp).getName()
                    .getName());
        }
        else if (arrayNameExp instanceof ProgramVariableDotExp) {
            Iterator<ProgramVariableExp> segmentIt =
                    ((ProgramVariableDotExp) arrayNameExp).getSegments()
                            .iterator();
            while (segmentIt.hasNext()) {
                ProgramVariableNameExp next =
                        (ProgramVariableNameExp) segmentIt.next();
                sb.append(next.getName().getName());

                if (segmentIt.hasNext()) {
                    sb.append("_");
                }
            }
        }
        else {
            throw new MiscErrorException(
                    "Cannot generate a new variable declaration using: "
                            + arrayNameExp, new IllegalStateException());
        }
        sb.append("_");
        sb.append(counter);

        return new VarDec(new PosSymbol(
                new Location(arrayNameExp.getLocation()), sb.toString()),
                arrayContentType.clone());
    }

    /**
     * <p>An helper method that returns a new {@link ProgramExp} containing
     * the array index expression.</p>
     *
     * @param exp A programming expression.
     *
     * @return The array index expression.
     *
     * @exception MiscErrorException
     */
    public static ProgramExp getArrayIndexExp(ProgramExp exp) {
        ProgramExp arrayIndexExp;
        if (exp instanceof ProgramVariableArrayExp) {
            arrayIndexExp =
                    ((ProgramVariableArrayExp) exp).getArrayIndexExp().clone();
        }
        else if (exp instanceof ProgramVariableDotExp) {
            List<ProgramVariableExp> segments =
                    ((ProgramVariableDotExp) exp).getSegments();
            ProgramExp lastElementExp = segments.get(segments.size() - 1);

            if (lastElementExp instanceof ProgramVariableArrayExp) {
                arrayIndexExp =
                        ((ProgramVariableArrayExp) lastElementExp)
                                .getArrayIndexExp().clone();
            }
            else {
                throw new MiscErrorException(
                        "Not a programming array expression: " + exp.toString(),
                        new IllegalStateException());
            }
        }
        else {
            throw new MiscErrorException("Not a programming array expression: "
                    + exp.toString(), new IllegalStateException());
        }

        return arrayIndexExp;
    }

    /**
     * <p>An helper method that returns a new {@link ProgramVariableExp} containing
     * the array name expression.</p>
     *
     * @param exp A programming expression.
     *
     * @return The array name expression.
     *
     * @exception MiscErrorException
     */
    public static ProgramVariableExp getArrayNameExp(ProgramExp exp) {
        ProgramVariableExp arrayNameExp;
        if (exp instanceof ProgramVariableArrayExp) {
            arrayNameExp = (ProgramVariableExp) ((ProgramVariableArrayExp) exp).getArrayNameExp().clone();
        }
        else if (exp instanceof ProgramVariableDotExp) {
            List<ProgramVariableExp> newSegments = new ArrayList<>();
            Iterator<ProgramVariableExp> segmentIt = ((ProgramVariableDotExp) exp).getSegments().iterator();
            while (segmentIt.hasNext()) {
                ProgramVariableExp currentExp = segmentIt.next();

                // Not the last element
                if (segmentIt.hasNext()) {
                    newSegments.add((ProgramVariableExp) currentExp.clone());
                }
                else {
                    if (currentExp instanceof ProgramVariableArrayExp) {
                        ProgramVariableArrayExp arrayExp = (ProgramVariableArrayExp) currentExp;
                        newSegments.add((ProgramVariableExp) arrayExp.getArrayNameExp().clone());
                    }
                    else {
                        throw new MiscErrorException(
                                "Not a programming array expression: " + exp.toString(),
                                new IllegalStateException());
                    }
                }
            }

            arrayNameExp = new ProgramVariableDotExp(new Location(exp.getLocation()), newSegments);
        }
        else {
            throw new MiscErrorException("Not a programming array expression: "
                    + exp.toString(), new IllegalStateException());
        }

        return arrayNameExp;
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
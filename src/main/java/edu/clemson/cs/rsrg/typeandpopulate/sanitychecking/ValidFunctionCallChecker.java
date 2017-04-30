/*
 * ValidFunctionCallChecker.java
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

import edu.clemson.cs.rsrg.absyn.declarations.operationdecl.OperationProcedureDec;
import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.absyn.expressions.programexpr.*;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.statushandling.exception.SourceErrorException;
import edu.clemson.cs.rsrg.typeandpopulate.entry.OperationEntry;
import edu.clemson.cs.rsrg.typeandpopulate.entry.ProgramParameterEntry;
import edu.clemson.cs.rsrg.typeandpopulate.entry.ProgramParameterEntry.ParameterMode;
import java.util.Iterator;
import java.util.List;

/**
 * <p>This is a sanity checker for making sure the parameters being passed
 * to a {@link ProgramFunctionExp} are valid.</p>
 *
 * @author Yu-Shan Sun
 * @version 1.0
 */
public class ValidFunctionCallChecker {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The {@link ProgramFunctionExp} to be checked.</p> */
    private final ProgramFunctionExp myCallingFunctionExp;

    /** <p>The operation corresponding to {@code myCallingFunctionExp}.</p> */
    private final OperationEntry myCorrespondingOperation;

    /** <p>The location that generated this checker.</p> */
    private final Location myLocation;

    /**
     * <p>While we walk the children of an operation, {@link OperationProcedureDec}, or
     * procedure, this list will contain all formal parameters encountered so
     * far, otherwise it will be null.  Since none of these structures can be
     * be nested, there's no need for a stack.</p>
     *
     * <p>If you need to distinguish if you're in the middle of an
     * operation/{@link OperationProcedureDec} or a procedure, check
     * {@code myCorrespondingOperation}.</p>
     */
    private final List<ProgramParameterEntry> myProcedureParameters;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>Creates a sanity checker for checking to see if the parameters being
     * passed to a {@link ProgramFunctionExp} are valid.</p>
     *
     * @param functionExp The program function expression to be checked.
     * @param correspondingOp The operation entry corresponding to
     *                        {@code functionExp}.
     * @param parameterEntries The current list of parameter entries from
     *                         the procedure declaration.
     */
    public ValidFunctionCallChecker(ProgramFunctionExp functionExp,
            OperationEntry correspondingOp,
            List<ProgramParameterEntry> parameterEntries) {
        myCallingFunctionExp = functionExp;
        myCorrespondingOperation = correspondingOp;
        myLocation = myCallingFunctionExp.getLocation();
        myProcedureParameters = parameterEntries;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>Checks to see if the {@link Exp Exps} being passed to this
     * {@link ProgramFunctionExp} are valid.</p>
     *
     * @throws SourceErrorException This is thrown when we encounter an expression
     * that violates the specified parameter mode.
     */
    public final void areValidExpArgs() {
        Iterator<ProgramParameterEntry> paramIt =
                myCorrespondingOperation.getParameters().iterator();
        Iterator<ProgramExp> argIt =
                myCallingFunctionExp.getArguments().iterator();

        // YS: No need to check the size or argument type of both lists.
        // We wouldn't have found this corresponding operation if the size
        // or the argument types didn't match.
        while (paramIt.hasNext()) {
            ProgramParameterEntry param = paramIt.next();
            ProgramExp argExp = argIt.next();

            if (param.getParameterMode().equals(ParameterMode.EVALUATES)) {
                if (!(argExp instanceof ProgramCharExp
                        || argExp instanceof ProgramFunctionExp
                        || argExp instanceof ProgramIntegerExp || argExp instanceof ProgramStringExp)) {
                    throw new SourceErrorException(
                            "An EVALUATES mode can only accept program character/function/integer/string expressions. "
                                    + "Found: " + argExp, myLocation);
                }
            }
        }
    }

}
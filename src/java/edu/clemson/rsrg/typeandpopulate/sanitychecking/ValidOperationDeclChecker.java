/*
 * ValidOperationDeclChecker.java
 * ---------------------------------
 * Copyright (c) 2022
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.rsrg.typeandpopulate.sanitychecking;

import edu.clemson.rsrg.absyn.declarations.operationdecl.*;
import edu.clemson.rsrg.parsing.data.Location;
import edu.clemson.rsrg.statushandling.exception.SourceErrorException;
import edu.clemson.rsrg.typeandpopulate.entry.OperationEntry;
import edu.clemson.rsrg.typeandpopulate.entry.ProgramParameterEntry;
import edu.clemson.rsrg.typeandpopulate.programtypes.PTType;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * <p>
 * This is a sanity checker for making sure the declared {@link OperationProcedureDec} or {@link ProcedureDec} is valid.
 * </p>
 *
 * @author Yu-Shan Sun
 *
 * @version 1.0
 */
public class ValidOperationDeclChecker {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * While walking a procedure, this is set to the entry for the operation or {@link OperationProcedureDec} that the
     * procedure is attempting to implement.
     * </p>
     *
     * <p>
     * <strong>INVARIANT:</strong> <code>myCorrespondingOperation != null</code> <em>implies</em>
     * <code>myCurrentParameters != null</code>.
     * </p>
     */
    private final OperationEntry myCorrespondingOperation;

    /**
     * <p>
     * While we walk the children of an operation, {@link OperationProcedureDec}, or procedure, this list will contain
     * all formal parameters encountered so far, otherwise it will be null. Since none of these structures can be be
     * nested, there's no need for a stack.
     * </p>
     *
     * <p>
     * If you need to distinguish if you're in the middle of an operation/{@link OperationProcedureDec} or a procedure,
     * check {@code myCorrespondingOperation}.
     * </p>
     */
    private final List<ProgramParameterEntry> myCurrentParameters;

    /**
     * <p>
     * The location that generated this checker.
     * </p>
     */
    private final Location myLocation;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * Creates a sanity checker for checking the various different criteria for being a valid procedure declaration.
     * </p>
     *
     * @param location
     *            Location that generated this checker.
     * @param operationEntry
     *            The associated operation entry.
     * @param parameterEntries
     *            The current list of parameter entries.
     */
    public ValidOperationDeclChecker(Location location, OperationEntry operationEntry,
            List<ProgramParameterEntry> parameterEntries) {
        myLocation = location;
        myCorrespondingOperation = operationEntry;
        myCurrentParameters = parameterEntries;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * Checks to see if the parameter modes specified in the {@link ProcedureDec} can be used to implement those that
     * have been specified in the corresponding operation entry.
     * </p>
     *
     * @throws SourceErrorException
     *             This is thrown when we encounter a mode that is not known to implement the mode specified in the
     *             corresponding operation entry.
     */
    public final void hasValidParameterModesImpl() {
        Iterator<ProgramParameterEntry> opParams = myCorrespondingOperation.getParameters().iterator();
        Iterator<ProgramParameterEntry> procParams = myCurrentParameters.iterator();
        ProgramParameterEntry curOpParam, curProcParam;
        while (opParams.hasNext()) {
            curOpParam = opParams.next();
            curProcParam = procParams.next();

            if (!curOpParam.getParameterMode().canBeImplementedWith(curProcParam.getParameterMode())) {
                throw new SourceErrorException(
                        curOpParam.getParameterMode() + " mode parameter " + "cannot be implemented with "
                                + curProcParam.getParameterMode() + " mode.\n"
                                + "Select one of these valid modes instead: "
                                + Arrays.toString(curOpParam.getParameterMode().getValidImplementationModes()),
                        curProcParam.getDefiningElement().getLocation());
            }

            if (!curProcParam.getDeclaredType().acceptableFor(curOpParam.getDeclaredType())) {
                throw new SourceErrorException(
                        "Parameter " + curProcParam.getName() + "'s type does not "
                                + "match corresponding operation parameter's type." + "\n\nExpected: "
                                + curOpParam.getDeclaredType() + " [" + curOpParam.getDefiningElement().getLocation()
                                + "]\n" + "Found: " + curProcParam.getDeclaredType(),
                        curProcParam.getDefiningElement().getLocation());
            }

            if (!curOpParam.getName().equals(curProcParam.getName())) {
                throw new SourceErrorException("Parameter name does not "
                        + "match corresponding operation parameter name." + "\n\nExpected name: " + curOpParam.getName()
                        + " [" + curOpParam.getDefiningElement().getLocation() + "]\n" + "Found name: "
                        + curProcParam.getName(), curProcParam.getDefiningElement().getLocation());
            }
        }
    }

    /**
     * <p>
     * Checks to see if the number of parameters matches the one specified in the corresponding operation entry.
     * </p>
     *
     * @throws SourceErrorException
     *             This is thrown when they are not the same.
     */
    public final void isSameNumberOfParameters() {
        if (myCorrespondingOperation.getParameters().size() != myCurrentParameters.size()) {
            throw new SourceErrorException(myCorrespondingOperation.getName() + "'s " + "parameter count "
                    + "does not correspond to the parameter count of the "
                    + "operation it implements. \n\nExpected count: " + myCorrespondingOperation.getParameters().size()
                    + " [" + myCorrespondingOperation.getOperationDec().getLocation() + "]\n" + "Found count: "
                    + myCurrentParameters.size(), myLocation);
        }
    }

    /**
     * <p>
     * Checks to see if the return type provided by {@link ProcedureDec} matches the one specified in the corresponding
     * operation entry.
     * </p>
     *
     * @param procedureReturnType
     *            The return type provided by {@link ProcedureDec}.
     *
     * @throws SourceErrorException
     *             This is thrown when they are not the same.
     */
    public final void isSameReturnType(PTType procedureReturnType) {
        if (!procedureReturnType.equals(myCorrespondingOperation.getReturnType())) {
            throw new SourceErrorException(myCorrespondingOperation.getName() + "'s " + "return type does "
                    + "not correspond to the return type of the operation " + "it implements.  \n\nExpected type: "
                    + myCorrespondingOperation.getReturnType() + " ["
                    + myCorrespondingOperation.getOperationDec().getLocation() + "]\n" + "Found type: "
                    + procedureReturnType, myLocation);
        }
    }

    /**
     * <p>
     * Checks to see if this procedure is a proper recursive procedure.
     * </p>
     *
     * @param isRecursive
     *            The recursive flag.
     * @param recursiveCallLocation
     *            The location of the recursive call.
     *
     * @throws SourceErrorException
     *             This is thrown when either the procedure wasn't declared as recursive, but contains a recursive call
     *             or when the procedure was declared as recursive, but doesn't have a recursive call.
     */
    public final void isValidRecursiveProcedure(boolean isRecursive, Location recursiveCallLocation) {
        // If the procedure has not been declared as recursive, we need to
        // make sure that none of the statements is a recursive call to itself.
        if (!isRecursive && recursiveCallLocation != null) {
            throw new SourceErrorException(
                    "Procedure not declared as recursive, " + "but makes a recursive call to itself.",
                    recursiveCallLocation);
        }
    }

}

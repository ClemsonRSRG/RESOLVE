/**
 * ValidOperationDeclChecker.java
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
package edu.clemson.cs.rsrg.typeandpopulate.sanitychecking;

import edu.clemson.cs.rsrg.absyn.declarations.operationdecl.*;
import edu.clemson.cs.rsrg.typeandpopulate.entry.OperationEntry;
import edu.clemson.cs.rsrg.typeandpopulate.entry.ProgramParameterEntry;
import java.util.List;

/**
 * <p>This is a sanity checker for making sure the declared {@link OperationProcedureDec} or
 * {@link ProcedureDec} is valid.</p>
 *
 * @author Yu-Shan Sun
 * @version 1.0
 */
public class ValidOperationDeclChecker {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>While walking a procedure, this is set to the entry for the operation
     * or FacilityOperation that the procedure is attempting to implement.</p>
     *
     * <p><strong>INVARIANT:</strong>
     * <code>myCorrespondingOperation != null</code> <em>implies</em>
     * <code>myCurrentParameters != null</code>.</p>
     */
    private final OperationEntry myCorrespondingOperation;

    /**
     * <p>While we walk the children of an operation, FacilityOperation, or
     * procedure, this list will contain all formal parameters encountered so
     * far, otherwise it will be null.  Since none of these structures can be
     * be nested, there's no need for a stack.</p>
     *
     * <p>If you need to distinguish if you're in the middle of an
     * operation/FacilityOperation or a procedure, check
     * myCorrespondingOperation.</p>
     */
    private final List<ProgramParameterEntry> myCurrentParameters;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     *
     * @param operationEntry
     * @param parameterEntries
     */
    public ValidOperationDeclChecker(OperationEntry operationEntry,
            List<ProgramParameterEntry> parameterEntries) {
        myCorrespondingOperation = operationEntry;
        myCurrentParameters = parameterEntries;
    }

}
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
    private final OperationDec MyFunctionOperationDec;

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
        MyFunctionOperationDec = functionOperation;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>Checks to see if the provided {@link OperationDec} is a
     * valid function operation declaration. A valid function
     * operation declaration must be of the form:
     * {@code <FuncOpDeclName> = <Expression/Value>}</p>
     *
     * @throws SourceErrorException This is thrown if it is not a valid
     * function operation declaration. The message will indicate why it is
     * not valid.
     */
    public final void checkFunctionOpDecl() {}

}
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

    /** <p>The location that generated this checker.</p> */
    private final Location myLocation;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>Creates a sanity checker for checking the various different criteria
     * for being a valid function operation declaration.</p>
     *
     * @param location Location that generated this checker.
     * @param functionOperation The associated function operation declaration.
     */
    public ValidFunctionOpDeclChecker(Location location,
            OperationDec functionOperation) {
        myLocation = location;
        MyFunctionOperationDec = functionOperation;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

}
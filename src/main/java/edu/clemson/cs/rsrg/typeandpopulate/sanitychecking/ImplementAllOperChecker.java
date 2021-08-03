/*
 * ImplementAllOperChecker.java
 * ---------------------------------
 * Copyright (c) 2021
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.typeandpopulate.sanitychecking;

import edu.clemson.cs.rsrg.absyn.declarations.Dec;
import edu.clemson.cs.rsrg.absyn.declarations.operationdecl.OperationDec;
import edu.clemson.cs.rsrg.absyn.declarations.operationdecl.ProcedureDec;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.statushandling.exception.SourceErrorException;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>
 * This is a sanity checker for making sure all operations from the
 * specification are implemented by
 * the realization.
 * </p>
 *
 * @author Yu-Shan Sun
 * @version 1.0
 */
public class ImplementAllOperChecker {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * Location object from the realization module.
     * </p>
     */
    private final Location myLocation;

    /**
     * <p>
     * List of all specification declarations.
     * </p>
     */
    private final List<Dec> mySpecDecs;

    /**
     * <p>
     * List of all realization declarations.
     * </p>
     */
    private final List<Dec> myRealizationDecs;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * Creates a sanity checker for checking whether or not the realization
     * implements all the
     * operations in a given {@code Concept}/{@code Enhancement}.
     * </p>
     *
     * @param location The module that called this method.
     * @param specDecs List of decs of the Concept/Enhancement
     * @param realizationDecs List of decs of the realization.
     */
    public ImplementAllOperChecker(Location location, List<Dec> specDecs,
            List<Dec> realizationDecs) {
        myLocation = location;
        mySpecDecs = specDecs;
        myRealizationDecs = realizationDecs;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * Checks to see if all operation specified by
     * {@code Concept}/{@code Enhancement} are implemented
     * by the corresponding realization.
     * </p>
     *
     * @throws SourceErrorException This is thrown when an operation isn't
     *         implemented.
     */
    public final void implementAllOper() throws SourceErrorException {
        List<Dec> opDecList1 = getOperationDecs(mySpecDecs);
        List<Dec> opDecList2 = getOperationDecs(myRealizationDecs);

        for (Dec d1 : opDecList1) {
            boolean inRealization = false;
            for (Dec d2 : opDecList2) {
                if (d1.getName().equals(d2.getName())) {
                    inRealization = true;
                }
            }
            if (!inRealization) {
                throw new SourceErrorException(
                        "Operation " + d1.getName()
                                + " not implemented by the realization.",
                        myLocation);
            }
        }
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>
     * Obtains the list of all <code>OperationDec</code> and
     * <code>ProcedureDec</code>.
     * </p>
     *
     * @param decs List of all declarations.
     *
     * @return List containing only operations.
     */
    private List<Dec> getOperationDecs(List<Dec> decs) {
        List<Dec> decList = new LinkedList<>();
        for (Dec d : decs) {
            if (d instanceof OperationDec || d instanceof ProcedureDec) {
                decList.add(d);
            }
        }

        return decList;
    }
}

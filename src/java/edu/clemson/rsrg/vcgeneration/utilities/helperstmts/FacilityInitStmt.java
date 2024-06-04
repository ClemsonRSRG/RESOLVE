/*
 * FacilityInitStmt.java
 * ---------------------------------
 * Copyright (c) 2024
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.rsrg.vcgeneration.utilities.helperstmts;

import edu.clemson.rsrg.absyn.declarations.facilitydecl.FacilityDec;
import edu.clemson.rsrg.absyn.statements.Statement;
import edu.clemson.rsrg.vcgeneration.VCGenerator;

/**
 * <p>
 * This is the class that builds a special kind of statement that acts as a placeholder for initialize a facility
 * declaration. Since the user cannot supply their own {@code _Facility_Init} statements, any instances of this class
 * will solely be created by the {@link VCGenerator} and/or by our various different {@code proof rules}.
 * </p>
 *
 * @author Yu-Shan Sun
 *
 * @version 1.0
 */
public class FacilityInitStmt extends Statement {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * The facility declaration we are applying the rule to.
     * </p>
     */
    private final FacilityDec myInstantiatedFacilityDec;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This constructs an helper statement that indicates initialization logic for a facility happens here.
     * </p>
     *
     * @param facilityDec
     *            A processed instantiated facility declaration.
     */
    public FacilityInitStmt(FacilityDec facilityDec) {
        super(facilityDec.getLocation());
        myInstantiatedFacilityDec = facilityDec;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * {@inheritDoc}
     */
    @Override
    public final String asString(int indentSize, int innerIndentInc) {
        StringBuffer sb = new StringBuffer();
        printSpace(indentSize, sb);
        sb.append("_Facility_Init(");
        sb.append(myInstantiatedFacilityDec.getName().asString(0, innerIndentInc));
        sb.append(");");

        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        FacilityInitStmt that = (FacilityInitStmt) o;

        return myInstantiatedFacilityDec.equals(that.myInstantiatedFacilityDec);
    }

    /**
     * <p>
     * This method returns the instantiated {@code Facility} declaration.
     * </p>
     *
     * @return A {@link FacilityDec}.
     */
    public final FacilityDec getInstantiatedFacilityDec() {
        return myInstantiatedFacilityDec;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        return myInstantiatedFacilityDec.hashCode();
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * {@inheritDoc}
     */
    @Override
    protected final Statement copy() {
        return new FacilityInitStmt((FacilityDec) myInstantiatedFacilityDec.clone());
    }
}

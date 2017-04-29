/*
 * VCConfirmStmt.java
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
package edu.clemson.cs.rsrg.vcgeneration.utilities;

import edu.clemson.cs.rsrg.absyn.statements.Statement;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.vcgeneration.VCGenerator;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>This is the class that builds a special kind of confirm statement
 * that temporary holds {@link VerificationCondition VCs}. The only usage
 * of this class should be the {@link WhileStmtRule}. Since the user cannot
 * supply their own {@code VC_Confirm} statements, any instances of this
 * class will solely be created by the {@link VCGenerator}.</p>
 *
 * @author Yu-Shan Sun
 * @version 1.0
 */
public class VCConfirmStmt extends Statement {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>List of {@link VerificationCondition VCs} we are trying
     * to prove.</p>
     */
    private final List<VerificationCondition> myVCs;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs an assume statement.</p>
     *
     * @param l A {@link Location} representation object.
     * @param vcs A list of {@link VerificationCondition} to
     *            be temporary stored in this {@link Statement}.
     */
    public VCConfirmStmt(Location l, List<VerificationCondition> vcs) {
        super(l);
        myVCs = vcs;
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
        sb.append("VC_Confirm ");
        Iterator<VerificationCondition> conditionIterator = myVCs.iterator();
        while (conditionIterator.hasNext()) {
            VerificationCondition vc = conditionIterator.next();
            sb.append(vc.asString(0, innerIndentInc));

            if (conditionIterator.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append(";");

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

        VCConfirmStmt that = (VCConfirmStmt) o;

        return myVCs.equals(that.myVCs);
    }

    /**
     * <p>This method returns the list of {@code VCs} stored inside
     * this {@link Statement}.</p>
     *
     * @return A list of {@link VerificationCondition VCs}.
     */
    public final List<VerificationCondition> getVCs() {
        return myVCs;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        return myVCs.hashCode();
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * {@inheritDoc}
     */
    @Override
    protected final Statement copy() {
        List<VerificationCondition> newVCs = new LinkedList<>();
        Collections.copy(newVCs, myVCs);

        return new VCConfirmStmt(cloneLocation(), newVCs);
    }
}
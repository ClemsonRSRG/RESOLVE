/*
 * ChangeStmt.java
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
package edu.clemson.rsrg.absyn.statements;

import edu.clemson.rsrg.absyn.expressions.Exp;
import edu.clemson.rsrg.parsing.data.Location;
import edu.clemson.rsrg.vcgeneration.VCGenerator;
import edu.clemson.rsrg.vcgeneration.utilities.Utilities;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * This is the class that builds the change statements created by the {@link VCGenerator}. Since the user cannot supply
 * their own change statements, any instances of this class will solely be created by the {@link VCGenerator}.
 * </p>
 *
 * @author Yu-Shan Sun
 *
 * @version 1.0
 */
public class ChangeStmt extends Statement {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * The list of expressions being changed.
     * </p>
     */
    private final List<Exp> myChangingVars;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This constructs a change statement.
     * </p>
     *
     * @param l
     *            A {@link Location} representation object.
     * @param changingVars
     *            List of all the variable expressions being changed.
     */
    public ChangeStmt(Location l, List<Exp> changingVars) {
        super(l);
        myChangingVars = new ArrayList<>(changingVars);
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
        sb.append("Change ");
        sb.append(Utilities.expListAsString(myChangingVars));
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

        ChangeStmt that = (ChangeStmt) o;

        return myChangingVars.equals(that.myChangingVars);
    }

    /**
     * <p>
     * This method returns the variable expressions that are changing.
     * </p>
     *
     * @return A list of {@link Exp} representation object.
     */
    public final List<Exp> getChangingVars() {
        return myChangingVars;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        return myChangingVars.hashCode();
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * {@inheritDoc}
     */
    @Override
    protected final Statement copy() {
        // YS: Collections.copy complains about source does not fit in dest,
        // so we manually copy everything.
        List<Exp> newChangingVars = new ArrayList<>(myChangingVars.size());
        for (Exp changesExp : myChangingVars) {
            newChangingVars.add(changesExp.clone());
        }

        return new ChangeStmt(cloneLocation(), newChangingVars);
    }
}

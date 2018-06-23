/*
 * ProgramVariableNameExp.java
 * ---------------------------------
 * Copyright (c) 2018
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.absyn.expressions.programexpr;

import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>This is the class for all the programming named variable expressions objects
 * that the compiler builds using the ANTLR4 AST nodes.</p>
 *
 * @version 2.0
 */
public class ProgramVariableNameExp extends ProgramVariableExp {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The variable name</p> */
    private final PosSymbol myVarName;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs a programming named variable expression.</p>
     *
     * @param l A {@link Location} representation object.
     * @param qual A {@link PosSymbol} representing the expression's qualifier.
     * @param name A {@link PosSymbol} representing the expression's name.
     */
    public ProgramVariableNameExp(Location l, PosSymbol qual, PosSymbol name) {
        super(l, qual);
        myVarName = name;
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

        if (getQualifier() != null) {
            sb.append(getQualifier().asString(0, innerIndentInc));
            sb.append("::");
        }

        sb.append(myVarName.asString(0, innerIndentInc));

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
        if (!super.equals(o))
            return false;

        ProgramVariableNameExp that = (ProgramVariableNameExp) o;

        return myVarName.equals(that.myVarName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean equivalent(Exp e) {
        boolean retval = e instanceof ProgramVariableNameExp;

        if (retval) {
            ProgramVariableNameExp eAsProgramVariableNameExp =
                    (ProgramVariableNameExp) e;

            retval =
                    posSymbolEquivalent(getQualifier(),
                            eAsProgramVariableNameExp.getQualifier())
                            && posSymbolEquivalent(myVarName,
                                    eAsProgramVariableNameExp.myVarName);
        }

        return retval;
    }

    /**
     * <p>This method returns the variable name.</p>
     *
     * @return The {@link PosSymbol} representation object.
     */
    public final PosSymbol getName() {
        return myVarName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final List<Exp> getSubExpressions() {
        return new ArrayList<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        int result = super.hashCode();
        result = 31 * result + myVarName.hashCode();
        return result;
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * {@inheritDoc}
     */
    @Override
    protected final Exp copy() {
        PosSymbol newQualifier = null;
        if (getQualifier() != null) {
            newQualifier = getQualifier().clone();
        }

        return new ProgramVariableNameExp(cloneLocation(), newQualifier,
                myVarName.clone());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final Exp substituteChildren(Map<Exp, Exp> substitutions) {
        PosSymbol newQualifier = null;
        if (getQualifier() != null) {
            newQualifier = getQualifier().clone();
        }

        return new ProgramVariableNameExp(cloneLocation(), newQualifier,
                myVarName.clone());
    }

}
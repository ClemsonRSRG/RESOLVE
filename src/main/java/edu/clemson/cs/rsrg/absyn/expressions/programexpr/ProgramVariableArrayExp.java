/*
 * ProgramVariableArrayExp.java
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
package edu.clemson.cs.rsrg.absyn.expressions.programexpr;

import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.parsing.TreeBuildingListener;
import edu.clemson.cs.rsrg.parsing.data.Location;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>Although the grammar allows user to type in programming array expressions,
 * in reality these are just syntactic sugar for different operation/function calls in
 * {@code Static_Array_Template}.</p>
 *
 * <p>When the {@link TreeBuildingListener} walks either to the exit function of
 * a {@code ResolveParser.ProgVarDotArrayExpContext} node or
 * a {@code ResolveParser.ProgVarArrayExpContext} node, we create an instance
 * of this class to satisfy the requirements of building something of type
 * {@link ProgramExp}.</p>
 *
 * <p>Before a {@link ProgramExp} is added to a statement, we check to see if
 * it contains any instances of this class. For all instances of this class,
 * we convert it to the corresponding operation/function calls.</p>
 *
 * <p>When we are done building the RESOLVE AST, there should not be any instances
 * of this class left.</p>
 *
 * @version 2.0
 */
public class ProgramVariableArrayExp extends ProgramVariableExp {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The expressions's index expression.</p> */
    private final ProgramExp myProgramIndexExp;

    /** <p>The expressions's array name expression.</p> */
    private final ProgramVariableExp myProgramNameExp;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs a programming variable array expression.</p>
     *
     * @param l A {@link Location} representation object.
     * @param nameExp A {@link ProgramVariableExp} representing the expression's name expression.
     * @param indexExp A {@link ProgramExp} representing the expression's index expression.
     */
    public ProgramVariableArrayExp(Location l, ProgramVariableExp nameExp,
            ProgramExp indexExp) {
        super(l, null);
        myProgramNameExp = nameExp;
        myProgramIndexExp = indexExp;
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
        sb.append(myProgramNameExp.asString(indentSize, innerIndentInc));
        sb.append("[");
        sb.append(myProgramIndexExp.asString(0, innerIndentInc));
        sb.append("]");

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

        ProgramVariableArrayExp that = (ProgramVariableArrayExp) o;

        if (!myProgramIndexExp.equals(that.myProgramIndexExp))
            return false;
        return myProgramNameExp.equals(that.myProgramNameExp);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean equivalent(Exp e) {
        boolean retval = e instanceof ProgramVariableArrayExp;

        if (retval) {
            ProgramVariableArrayExp eAsProgramVariableNameExp =
                    (ProgramVariableArrayExp) e;

            retval =
                    myProgramNameExp
                            .equivalent(eAsProgramVariableNameExp.myProgramNameExp)
                            && myProgramIndexExp
                                    .equivalent(eAsProgramVariableNameExp.myProgramIndexExp);
        }

        return retval;
    }

    /**
     * <p>This method returns the expression's name expression.</p>
     *
     * @return The {@link ProgramVariableExp} representation object.
     */
    public final ProgramVariableExp getArrayNameExp() {
        return myProgramNameExp;
    }

    /**
     * <p>This method returns the expression's index expression.</p>
     *
     * @return The {@link ProgramExp} representation object.
     */
    public final ProgramExp getArrayIndexExp() {
        return myProgramIndexExp;
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
        result = 31 * result + myProgramIndexExp.hashCode();
        result = 31 * result + myProgramNameExp.hashCode();
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
        return new ProgramVariableArrayExp(cloneLocation(),
                (ProgramVariableExp) myProgramNameExp.clone(),
                myProgramIndexExp.clone());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final Exp substituteChildren(Map<Exp, Exp> substitutions) {
        throw new UnsupportedOperationException(
                "Cannot substitute in a program array expression.");
    }

}
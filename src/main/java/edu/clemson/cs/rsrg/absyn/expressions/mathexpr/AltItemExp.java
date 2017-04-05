/*
 * AltItemExp.java
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
package edu.clemson.cs.rsrg.absyn.expressions.mathexpr;

import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.statushandling.exception.MiscErrorException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>This is the class for all the individual mathematical alternative
 * items inside the {@link AlternativeExp}s.</p>
 *
 * @version 2.0
 */
public class AltItemExp extends MathExp {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The testing expression.</p> */
    private final Exp myTestingExp;

    /** <p>The assignment expression.</p> */
    private final Exp myAssignmentExp;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs an inner alternative expression for
     * the {@link AlternativeExp} class.</p>
     *
     * @param l A {@link Location} representation object.
     * @param test An {@link Exp} testing expression.
     * @param assignment An {@link Exp} assignment expression.
     */
    public AltItemExp(Location l, Exp test, Exp assignment) {
        super(l);
        if (assignment == null) {
            throw new MiscErrorException("Cannot have null assignment.",
                    new IllegalArgumentException());
        }

        myTestingExp = test;
        myAssignmentExp = assignment;
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
        sb.append(myAssignmentExp.asString(indentSize, innerIndentInc));

        // Check for if/otherwise
        if (myTestingExp != null) {
            printSpace(innerIndentInc, sb);
            sb.append("if ");
            sb.append(myTestingExp.asString(0, innerIndentInc));
        }
        else {
            printSpace(innerIndentInc, sb);
            sb.append("otherwise");
        }

        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean containsExp(Exp exp) {
        boolean found = myAssignmentExp.containsExp(exp);
        if (!found && myTestingExp != null) {
            found = myTestingExp.containsExp(exp);
        }

        return found;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean containsVar(String varName, boolean IsOldExp) {
        boolean found = myAssignmentExp.containsVar(varName, IsOldExp);
        if (!found && myTestingExp != null) {
            found = myTestingExp.containsVar(varName, IsOldExp);
        }

        return found;
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

        AltItemExp that = (AltItemExp) o;

        if (myTestingExp != null ? !myTestingExp.equals(that.myTestingExp)
                : that.myTestingExp != null)
            return false;
        return myAssignmentExp != null ? myAssignmentExp
                .equals(that.myAssignmentExp) : that.myAssignmentExp == null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean equivalent(Exp e) {
        boolean result = e instanceof AltItemExp;

        if (result) {
            AltItemExp eAsAltItemExp = (AltItemExp) e;

            result = eAsAltItemExp.myAssignmentExp.equivalent(myAssignmentExp);

            if (myTestingExp != null && eAsAltItemExp.myTestingExp != null) {
                result &= eAsAltItemExp.myTestingExp.equivalent(myTestingExp);
            }
            else {
                result = false;
            }
        }

        return result;
    }

    /**
     * <p>Returns this expression's assignment expression.</p>
     *
     * @return The assignment {@link Exp} object.
     */
    public final Exp getAssignment() {
        return myAssignmentExp;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final List<Exp> getSubExpressions() {
        List<Exp> subExpList = new ArrayList<>();

        if (myTestingExp != null) {
            subExpList.add(myTestingExp.clone());
        }
        subExpList.add(myAssignmentExp.clone());

        return subExpList;
    }

    /**
     * <p>Returns this expression's testing expression.</p>
     *
     * @return The testing {@link Exp} object.
     */
    public final Exp getTest() {
        return myTestingExp;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        int result = super.hashCode();
        result =
                31 * result
                        + (myTestingExp != null ? myTestingExp.hashCode() : 0);
        result =
                31
                        * result
                        + (myAssignmentExp != null ? myAssignmentExp.hashCode()
                                : 0);
        return result;
    }

    /**
     * <p>This method applies VC Generator's remember rule.
     * For all inherited programming expression classes, this method
     * should throw an exception.</p>
     *
     * @return The resulting {@link AltItemExp} from applying the remember rule.
     */
    @Override
    public final AltItemExp remember() {
        Exp testingExp = myTestingExp;
        if (testingExp != null) {
            testingExp = ((MathExp) testingExp).remember();
        }

        Exp assignmentExp = ((MathExp) myAssignmentExp).remember();

        return new AltItemExp(cloneLocation(), testingExp, assignmentExp);
    }

    /**
     * <p>This method applies the VC Generator's simplification step.</p>
     *
     * @return The resulting {@link MathExp} from applying the simplification step.
     */
    @Override
    public final MathExp simplify() {
        return this.clone();
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * {@inheritDoc}
     */
    @Override
    protected final Exp copy() {
        Exp newTest = null;
        if (myTestingExp != null) {
            newTest = myTestingExp.clone();
        }

        return new AltItemExp(cloneLocation(), newTest, myAssignmentExp.clone());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final Exp substituteChildren(Map<Exp, Exp> substitutions) {
        Exp newTest = null;
        if (myTestingExp != null) {
            newTest = substitute(myTestingExp, substitutions);
        }

        return new AltItemExp(cloneLocation(), newTest, substitute(
                myAssignmentExp, substitutions));
    }

}
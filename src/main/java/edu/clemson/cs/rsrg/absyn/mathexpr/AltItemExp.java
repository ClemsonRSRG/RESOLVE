/**
 * AltItemExp.java
 * ---------------------------------
 * Copyright (c) 2015
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.absyn.mathexpr;

import edu.clemson.cs.rsrg.absyn.Exp;
import edu.clemson.cs.rsrg.errorhandling.exception.MiscErrorException;
import edu.clemson.cs.rsrg.parsing.data.Location;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>This is the class for all the individual mathematical alternative
 * items inside the {link AlternativeExp}s.</p>
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
     * <p>This method creates a special indented
     * text version of the class as a string.</p>
     *
     * @param indentSize The base indentation to the first line
     *                   of the text.
     * @param innerIndentSize The additional indentation increment
     *                        for the subsequent lines.
     *
     * @return A formatted text string of the class.
     */
    @Override
    public String asString(int indentSize, int innerIndentSize) {
        StringBuffer sb = new StringBuffer();
        printSpace(indentSize, sb);
        sb.append("AltItemExp\n");

        if (myTestingExp != null) {
            sb.append(myTestingExp.asString(indentSize + innerIndentSize,
                    innerIndentSize));
        }

        if (myAssignmentExp != null) {
            sb.append(myAssignmentExp.asString(indentSize + innerIndentSize,
                    innerIndentSize));
        }

        return sb.toString();
    }

    /**
     * <p>This method attempts to find the provided expression in our
     * subexpressions.</p>
     *
     * @param exp The expression we wish to locate.
     *
     * @return True if there is an instance of <code>exp</code>
     * within this object's subexpressions. False otherwise.
     */
    @Override
    public boolean containsExp(Exp exp) {
        boolean found = false;
        if (myTestingExp != null) {
            found = myTestingExp.containsExp(exp);
        }
        if (!found && myAssignmentExp != null) {
            found = myAssignmentExp.containsExp(exp);
        }

        return found;
    }

    /**
     *  <p>This method attempts to find an expression with the given name in our
     * subexpressions.</p>
     *
     * @param varName Expression name.
     * @param IsOldExp Flag to indicate if the given name is of the form
     *                 "#[varName]"
     *
     * @return True if there is a {@link Exp} within this object's
     * subexpressions that matches <code>varName</code>. False otherwise.
     */
    @Override
    public boolean containsVar(String varName, boolean IsOldExp) {
        boolean found = false;
        if (myTestingExp != null) {
            found = myTestingExp.containsVar(varName, IsOldExp);
        }
        if (!found && myAssignmentExp != null) {
            found = myAssignmentExp.containsVar(varName, IsOldExp);
        }

        return found;
    }

    /**
     * <p>This method overrides the default equals method implementation
     * for the {@link AltItemExp} class.</p>
     *
     * @param o Object to be compared.
     *
     * @return True if all the fields are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if (o instanceof AltItemExp) {
            AltItemExp eAsAltItemExp = (AltItemExp) o;
            result = myLoc.equals(eAsAltItemExp.myLoc);

            if (result) {
                if (myTestingExp != null) {
                    result = myTestingExp.equals(eAsAltItemExp.myTestingExp);
                }

                if (result) {
                    result =
                            myAssignmentExp
                                    .equals(eAsAltItemExp.myAssignmentExp);
                }
            }
        }

        return result;
    }

    /**
     * <p>Shallow compare is too weak for many things, and equals() is too
     * strict. This method returns <code>true</code> <strong>iff</code> this
     * expression and the provided expression, <code>e</code>, are equivalent
     * with respect to structure and all function and variable names.</p>
     *
     * @param e The expression to compare this one to.
     *
     * @return True <strong>iff</strong> this expression and the provided
     *         expression are equivalent with respect to structure and all
     *         function and variable names.
     */
    @Override
    public boolean equivalent(Exp e) {
        boolean result = e instanceof AltItemExp;

        if (result) {
            AltItemExp eAsAltItemExp = (AltItemExp) e;

            result = eAsAltItemExp.myTestingExp.equivalent(myTestingExp);
            result &= eAsAltItemExp.myAssignmentExp.equivalent(myAssignmentExp);
        }

        return result;
    }

    /**
     * <p>Returns a deep copy of this expression's assignment expression.</p>
     *
     * @return The assignment {@link Exp} object.
     */
    public Exp getAssignment() {
        return myAssignmentExp.clone();
    }

    /**
     * <p>This method returns the list of subexpressions.</p>
     *
     * @return A list containing subexpressions ({@link Exp}s).
     */
    @Override
    public List<Exp> getSubExpressions() {
        List<Exp> subExpList = new ArrayList<>();
        subExpList.add(myTestingExp.clone());
        subExpList.add(myAssignmentExp.clone());

        return subExpList;
    }

    /**
     * <p>Returns a deep copy this expression's testing expression.</p>
     *
     * @return The testing {@link Exp} object.
     */
    public Exp getTest() {
        return myTestingExp.clone();
    }

    /**
     * <p>This method applies VC Generator's remember rule.
     * For all inherited programming expression classes, this method
     * should throw an exception.</p>
     *
     * @return The resulting {@link AltItemExp} from applying the remember rule.
     */
    @Override
    public AltItemExp remember() {
        Exp testingExp = myTestingExp;
        if (testingExp instanceof OldExp) {
            testingExp = ((OldExp) testingExp).getExp();
        }
        if (testingExp != null) {
            testingExp = ((MathExp) testingExp).remember();
        }

        Exp assignmentExp = myAssignmentExp;
        if (assignmentExp instanceof OldExp) {
            assignmentExp = ((OldExp) assignmentExp).getExp();
        }
        if (assignmentExp != null) {
            assignmentExp = ((MathExp) assignmentExp).remember();
        }

        return new AltItemExp(new Location(myLoc), testingExp, assignmentExp);
    }

    /**
     *  <p>This method adds a new expression to our list of subexpressions.</p>
     *
     * @param index The index in our subexpression list.
     * @param e The new {@link Exp} to be added.
     */
    // TODO: See the message in Exp.
    /*
    @Override
    public void setSubExpression(int index, Exp e) {
        switch (index) {
        case 0:
            //edu.clemson.cs.r2jt.data.List was written by crazed monkies and
            //silently ignores adding null elements (in violation of 
            //java.util.List's contract), so if testingExp is null, index 0 is the
            //assignment subexpression, otherwise it's the testingExp subexpression.
            if (myTestingExp == null) {
                setAssignment(e);
            }
            else {
                setTest(e);
            }
            break;
        case 1:
            setAssignment(e);
            break;
        }
    }*/

    /**
     * <p>This method applies the VC Generator's simplification step.</p>
     *
     * @return The resulting {@link MathExp} from applying the simplification step.
     */
    @Override
    public MathExp simplify() {
        return this.clone();
    }

    /**
     * <p>Returns the expression in string format.</p>
     *
     * @return Expression as a string.
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(myAssignmentExp.toString());

        if (myTestingExp != null) {
            sb.append(" if ");
            sb.append(myTestingExp.toString());
        }
        else {
            sb.append(" otherwise");
        }

        return sb.toString();
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * <p>Implemented by this concrete subclass of {@link Exp} to manufacture
     * a copy of themselves.</p>
     *
     * @return A new {@link Exp} that is a deep copy of the original.
     */
    @Override
    protected Exp copy() {
        Location newLoc = new Location(myLoc);
        Exp newTest = null;
        if (myTestingExp != null) {
            newTest = myTestingExp.clone();
        }

        Exp newAssignment = null;
        if (myAssignmentExp != null) {
            newAssignment = myAssignmentExp.clone();
        }

        return new AltItemExp(newLoc, newTest, newAssignment);
    }

    /**
     * <p>Implemented by this concrete subclass of {@link Exp} to manufacture
     * a copy of themselves where all subexpressions have been appropriately
     * substituted. This class is assuming that <code>this</code>
     * does not match any key in <code>substitutions</code> and thus need only
     * concern itself with performing substitutions in its children.</p>
     *
     * @param substitutions A mapping from {@link Exp}s that should be
     *                      substituted out to the {@link Exp} that should
     *                      replace them.
     *
     * @return A new {@link Exp} that is a deep copy of the original with
     *         the provided substitutions made.
     */
    @Override
    protected Exp substituteChildren(Map<Exp, Exp> substitutions) {
        return new AltItemExp(myLoc, substitute(myTestingExp, substitutions),
                substitute(myAssignmentExp, substitutions));
    }

}
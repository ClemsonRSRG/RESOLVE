/**
 * SetCollectionExp.java
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
import edu.clemson.cs.rsrg.parsing.data.Location;
import java.util.*;

/**
 * <p>This is the class for all the mathematical set (as a collection)
 * expressions that the compiler builds from the ANTLR4 AST tree.</p>
 *
 * @version 2.0
 */
public class SetCollectionExp extends MathExp {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The list of member expressions in this set collection.</p> */
    private final Set<MathExp> myMembers;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs a mathematical set collection expression.</p>
     *
     * @param l A {@link Location} representation object.
     * @param vars A set of {@link MathExp}s where each one is a member
     *             in this set.
     */
    public SetCollectionExp(Location l, Set<MathExp> vars) {
        super(l);
        myMembers = vars;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This method creates a special indented
     * text version of the class as a string.</p>
     *
     * @param indentSize      The base indentation to the first line
     *                        of the text.
     * @param innerIndentSize The additional indentation increment
     *                        for the subsequent lines.
     * @return A formatted text string of the class.
     */
    @Override
    public String asString(int indentSize, int innerIndentSize) {
        StringBuffer sb = new StringBuffer();
        printSpace(indentSize, sb);
        sb.append("SetCollectionExp\n");

        sb.append("{");
        if (myMembers != null) {
            if (myMembers.isEmpty()) {
                sb.append("");
            }
            else {
                Iterator<MathExp> i = myMembers.iterator();
                while (i.hasNext()) {
                    MathExp m = i.next();
                    sb.append(m.asString(indentSize, innerIndentSize));

                    if (i.hasNext()) {
                        sb.append(", ");
                    }
                }
            }
        }
        sb.append("}");

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

        Iterator<MathExp> i = myMembers.iterator();
        while (i.hasNext() && !found) {
            MathExp m = i.next();

            if (m != null) {
                found = m.containsExp(exp);
            }
        }

        return found;
    }

    /**
     * <p>This method attempts to find an expression with the given name in our
     * subexpressions.</p>
     *
     * @param varName  Expression name.
     * @param IsOldExp Flag to indicate if the given name is of the form
     *                 "#[varName]"
     * @return False.
     */
    @Override
    public boolean containsVar(String varName, boolean IsOldExp) {
        boolean found = false;

        Iterator<MathExp> i = myMembers.iterator();
        while (i.hasNext() && !found) {
            MathExp m = i.next();

            if (m != null) {
                found = m.containsVar(varName, IsOldExp);
            }
        }

        return found;
    }

    /**
     * <p>This method overrides the default equals method implementation
     * for the {@link SetCollectionExp} class.</p>
     *
     * @param o Object to be compared.
     *
     * @return True if all the fields are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if (o instanceof SetCollectionExp) {
            SetCollectionExp eAsSetCollectionExp = (SetCollectionExp) o;
            result = myLoc.equals(eAsSetCollectionExp.myLoc);

            if (result) {
                if (myMembers != null && eAsSetCollectionExp.myMembers != null) {
                    Iterator<MathExp> thisMemberExps = myMembers.iterator();
                    Iterator<MathExp> eMemberExps =
                            eAsSetCollectionExp.myMembers.iterator();
                    while (result && thisMemberExps.hasNext()
                            && eMemberExps.hasNext()) {
                        result &=
                                thisMemberExps.next()
                                        .equals(eMemberExps.next());
                    }

                    //Both had better have run out at the same time
                    result &=
                            (!thisMemberExps.hasNext())
                                    && (!eMemberExps.hasNext());
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
        boolean result = (e instanceof SetCollectionExp);

        if (result) {
            SetCollectionExp eAsSetCollectionExp = (SetCollectionExp) e;

            if (myMembers != null && eAsSetCollectionExp.myMembers != null) {
                Iterator<MathExp> thisMemberExps = myMembers.iterator();
                Iterator<MathExp> eMemberExps =
                        eAsSetCollectionExp.myMembers.iterator();
                while (result && thisMemberExps.hasNext()
                        && eMemberExps.hasNext()) {
                    result &=
                            thisMemberExps.next()
                                    .equivalent(eMemberExps.next());
                }

                //Both had better have run out at the same time
                result &=
                        (!thisMemberExps.hasNext()) && (!eMemberExps.hasNext());
            }
        }

        return result;
    }

    /**
     * <p>This method returns a deep copy of the list of
     * subexpressions.</p>
     *
     * @return A list containing subexpressions ({@link Exp}s).
     */
    @Override
    public List<Exp> getSubExpressions() {
        List<Exp> subExpList = new ArrayList<>();
        for (MathExp m : myMembers) {
            subExpList.add(m.clone());
        }

        return subExpList;
    }

    /**
     * <p>This method returns a deep copy of all the
     * variable expressions in this set.</p>
     *
     * @return A set containing all the {@link MathExp}s.
     */
    public Set<MathExp> getVars() {
        return copyExps();
    }

    /**
     * <p>This method applies VC Generator's remember rule.
     * For all inherited programming expression classes, this method
     * should throw an exception.</p>
     *
     * @return The resulting {@link SetCollectionExp} from applying the remember rule.
     */
    @Override
    public SetCollectionExp remember() {
        Set<MathExp> newVarExps = new HashSet<>();
        for (MathExp m : myMembers) {
            newVarExps.add((MathExp) m.remember());
        }

        return new SetCollectionExp(new Location(myLoc), newVarExps);
    }

    /**
     * <p>This method adds a new expression to our list of subexpressions.</p>
     *
     * @param index The index in our subexpression list.
     * @param e The new {@link Exp} to be added.
     */
    // TODO: See the message in Exp.
    //public void setSubExpression(int index, Exp e) {}

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

        sb.append("{");
        if (myMembers != null) {
            Iterator<MathExp> i = myMembers.iterator();

            while (i.hasNext()) {
                sb.append(i.next().toString());

                if (i.hasNext()) {
                    sb.append(", ");
                }
            }
        }
        sb.append("}");

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
        return new SetCollectionExp(new Location(myLoc), copyExps());
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
    public Exp substituteChildren(Map<Exp, Exp> substitutions) {
        Set<MathExp> newMembers = new HashSet<>();
        for (MathExp m : myMembers) {
            newMembers.add((MathExp) substitute(m, substitutions));
        }

        return new SetCollectionExp(new Location(myLoc), newMembers);
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>This is a helper method that makes a copy of the
     * list containing all the variable expressions.</p>
     *
     * @return A list containing {@link MathExp}s.
     */
    private Set<MathExp> copyExps() {
        Set<MathExp> copyMathExps = new HashSet<>();
        for (MathExp v : myMembers) {
            copyMathExps.add(v.clone());
        }

        return copyMathExps;
    }
}
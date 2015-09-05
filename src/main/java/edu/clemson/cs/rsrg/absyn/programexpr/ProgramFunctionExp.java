/**
 * ProgramFunctionExp.java
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
package edu.clemson.cs.rsrg.absyn.programexpr;

import edu.clemson.cs.rsrg.absyn.Exp;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * <p>This is the class for all the programming function call expressions
 * that the compiler builds from the ANTLR4 AST tree.</p>
 *
 * @version 2.0
 */
public class ProgramFunctionExp extends ProgramExp {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The expression's qualifier</p> */
    private PosSymbol myQualifier;

    /** <p>The function/operation name</p> */
    private final PosSymbol myOperationName;

    /** The arguments member. */
    private final List<ProgramExp> myExpressionArgs;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs a programming function call expression.</p>
     *
     * @param l A {@link Location} representation object.
     * @param qual A {@link PosSymbol} representing the expression's qualifier.
     * @param name A {@link PosSymbol} representing the expression's name.
     */
    public ProgramFunctionExp(Location l, PosSymbol qual, PosSymbol name,
            List<ProgramExp> arguments) {
        super(l);
        myQualifier = qual;
        myOperationName = name;
        myExpressionArgs = arguments;
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
        sb.append("ProgramFunctionExp\n");

        if (myQualifier != null) {
            sb.append(myQualifier.asString(indentSize + innerIndentSize,
                    innerIndentSize));
            sb.append("::");
        }

        if (myOperationName != null) {
            sb.append(myOperationName.asString(indentSize + innerIndentSize,
                    innerIndentSize));
        }

        if (myExpressionArgs != null) {
            sb.append("(");
            for (ProgramExp exp : myExpressionArgs) {
                sb.append(exp.asString(indentSize + innerIndentSize,
                        innerIndentSize));
            }
            sb.append(")");
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
        if (myExpressionArgs != null) {
            Iterator<ProgramExp> i = myExpressionArgs.iterator();
            while (i.hasNext() && !found) {
                ProgramExp temp = i.next();
                if (temp != null) {
                    if (temp.containsExp(exp)) {
                        found = true;
                    }
                }
            }
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
        if (myExpressionArgs != null) {
            Iterator<ProgramExp> i = myExpressionArgs.iterator();
            while (i.hasNext() && !found) {
                ProgramExp temp = i.next();
                if (temp != null) {
                    if (temp.containsVar(varName, IsOldExp)) {
                        found = true;
                    }
                }
            }
        }

        return found;
    }

    /**
     * <p>This method overrides the default equals method implementation
     * for the {@link ProgramFunctionExp} class.</p>
     *
     * @param o Object to be compared.
     *
     * @return True if all the fields are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if (o instanceof ProgramFunctionExp) {
            ProgramFunctionExp eAsProgramFunctionExp = (ProgramFunctionExp) o;
            result = myLoc.equals(eAsProgramFunctionExp.myLoc);

            if (result) {
                if (myExpressionArgs != null
                        && eAsProgramFunctionExp.myExpressionArgs != null) {
                    Iterator<ProgramExp> thisExpArgs =
                            myExpressionArgs.iterator();
                    Iterator<ProgramExp> eExpArgs =
                            eAsProgramFunctionExp.myExpressionArgs.iterator();

                    while (result && thisExpArgs.hasNext()
                            && eExpArgs.hasNext()) {
                        result &= thisExpArgs.next().equals(eExpArgs.next());
                    }

                    //Both had better have run out at the same time
                    result &= (!thisExpArgs.hasNext()) && (!eExpArgs.hasNext());
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
        boolean result = (e instanceof ProgramFunctionExp);

        if (result) {
            ProgramFunctionExp eAsProgramFunctionExp = (ProgramFunctionExp) e;

            if (myExpressionArgs != null
                    && eAsProgramFunctionExp.myExpressionArgs != null) {
                Iterator<ProgramExp> thisExpArgs = myExpressionArgs.iterator();
                Iterator<ProgramExp> eExpArgs =
                        eAsProgramFunctionExp.myExpressionArgs.iterator();
                while (result && thisExpArgs.hasNext() && eExpArgs.hasNext()) {

                    result &= thisExpArgs.next().equivalent(eExpArgs.next());
                }

                //Both had better have run out at the same time
                result &= (!thisExpArgs.hasNext()) && (!eExpArgs.hasNext());
            }
        }

        return result;
    }

    /**
     * <p>This method returns a deep copy of all the argument expressions.</p>
     *
     * @return A list containing all the argument {@link Exp}s.
     */
    /** Returns the value of the arguments variable. */
    public List<ProgramExp> getArguments() {
        return copyExps();
    }

    /**
     * <p>This method returns a deep copy of the operation name.</p>
     *
     * @return The {@link PosSymbol} representation object.
     */
    public final PosSymbol getName() {
        return myOperationName.clone();
    }

    /**
     * <p>This method returns a deep copy of the qualifier name.</p>
     *
     * @return The {@link PosSymbol} representation object.
     */
    public final PosSymbol getQualifier() {
        PosSymbol qual = null;
        if (myQualifier != null) {
            qual = myQualifier.clone();
        }

        return qual;
    }

    /**
     * <p>This method returns a deep copy of the list of
     * subexpressions.</p>
     *
     * @return A list containing subexpressions ({@link Exp}s).
     */
    @Override
    public List<Exp> getSubExpressions() {
        List<Exp> copyArgExps = new ArrayList<>();
        for (ProgramExp exp : myExpressionArgs) {
            copyArgExps.add(exp.clone());
        }

        return copyArgExps;
    }

    /**
     * <p>Sets the qualifier for this expression.</p>
     *
     * @param qualifier The qualifier for this expression.
     */
    public final void setQualifier(PosSymbol qualifier) {
        myQualifier = qualifier;
    }

    /**
     * <p>This method adds a new expression to our list of subexpressions.</p>
     *
     * @param index The index in our subexpression list.
     * @param e The new {@link Exp} to be added.
     */
    // TODO: See the message in Exp.
    /*public void setSubExpression(int index, Exp e) {
        arguments.set(index, (ProgramExp) e);
    }*/

    /**
     * <p>Returns the expression in string format.</p>
     *
     * @return Expression as a string.
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();

        if (myQualifier != null) {
            sb.append(myQualifier.toString());
            sb.append("::");
        }

        if (myOperationName != null) {
            sb.append(myOperationName.toString());
        }

        if (myExpressionArgs != null) {
            sb.append("(");
            for (ProgramExp exp : myExpressionArgs) {
                sb.append(exp.toString());
            }
            sb.append(")");
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
        PosSymbol newQualifier = null;
        if (myQualifier != null) {
            newQualifier = myQualifier.clone();
        }

        return new ProgramFunctionExp(new Location(myLoc), newQualifier,
                myOperationName.clone(), copyExps());
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
        PosSymbol newQualifier = null;
        if (myQualifier != null) {
            newQualifier = myQualifier.clone();
        }

        List<ProgramExp> newExpressionArgs = new ArrayList<>();
        for (ProgramExp e : myExpressionArgs) {
            newExpressionArgs.add((ProgramExp) substitute(e, substitutions));
        }

        return new ProgramFunctionExp(new Location(myLoc), newQualifier, myOperationName.clone(), newExpressionArgs);
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>This is a helper method that makes a copy of the
     * list containing all the argument expressions.</p>
     *
     * @return A list containing {@link Exp}s.
     */
    private List<ProgramExp> copyExps() {
        List<ProgramExp> copyArgExps = new ArrayList<>();
        for (ProgramExp exp : myExpressionArgs) {
            copyArgExps.add(exp.clone());
        }

        return copyArgExps;
    }
}
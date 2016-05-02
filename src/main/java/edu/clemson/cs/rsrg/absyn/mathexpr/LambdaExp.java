/**
 * LambdaExp.java
 * ---------------------------------
 * Copyright (c) 2016
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
import edu.clemson.cs.rsrg.absyn.variables.MathVarDec;
import edu.clemson.cs.rsrg.parsing.data.Location;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * <p>This is the class for all the mathematical lambda expressions
 * that the compiler builds from the ANTLR4 AST tree.</p>
 *
 * @version 2.0
 */
public class LambdaExp extends MathExp {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The list of mathematical variables in this lambda expression.</p> */
    private final List<MathVarDec> myParameters;

    /** <p>The lambda expression's body.</p> */
    private final Exp myBodyExp;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs a lambda expression.</p>
     *
     * @param l A {@link Location} representation object.
     * @param params A list of {@link MathVarDec} representing the expression's variables.
     * @param body A {@link Exp} representing the body of the expression.
     */
    public LambdaExp(Location l, List<MathVarDec> params, Exp body) {
        super(l);

        if (params == null) {
            throw new IllegalArgumentException("null LambdaExp params");
        }

        myParameters = params;
        myBodyExp = body;
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
        sb.append("LambdaExp\n");

        for (MathVarDec v : myParameters) {
            sb.append(v);
        }

        if (myBodyExp != null) {
            sb.append(myBodyExp.asString(indentSize + innerIndentSize,
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
    public final boolean containsExp(Exp exp) {
        return myBodyExp.containsExp(exp);
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
        boolean result = false;

        Iterator<MathVarDec> parameterIter = myParameters.iterator();
        while (!result && parameterIter.hasNext()) {
            result = parameterIter.next().getName().getName().equals(varName);
        }

        if (!result && myBodyExp != null) {
            result = myBodyExp.containsVar(varName, IsOldExp);
        }

        return result;
    }

    /**
     * <p>This method overrides the default equals method implementation
     * for the {@link SetExp} class.</p>
     *
     * @param o Object to be compared.
     *
     * @return True if all the fields are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if (o instanceof LambdaExp) {
            LambdaExp eAsLambdaExp = (LambdaExp) o;
            result = myLoc.equals(eAsLambdaExp.myLoc);

            if (result) {
                Iterator<MathVarDec> thisParameters = myParameters.iterator();
                Iterator<MathVarDec> eParameters =
                        eAsLambdaExp.myParameters.iterator();
                while (result && thisParameters.hasNext()
                        && eParameters.hasNext()) {
                    result &= thisParameters.next().equals(eParameters.next());
                }

                //Both had better have run out at the same time
                result &=
                        (!thisParameters.hasNext()) && (!eParameters.hasNext());

                if (result) {
                    result = myBodyExp.equals(eAsLambdaExp.myBodyExp);
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
        boolean result = e instanceof LambdaExp;
        if (result) {
            LambdaExp eAsLambdaExp = (LambdaExp) e;

            result = (myParameters.size() == eAsLambdaExp.myParameters.size());

            Iterator<MathVarDec> parameterIterator = myParameters.iterator();
            Iterator<MathVarDec> eParameterIterator =
                    eAsLambdaExp.myParameters.iterator();
            while (parameterIterator.hasNext() && result) {
                result =
                        parameterIterator.next().equals(
                                eParameterIterator.next());
            }

            if (result) {
                result = myBodyExp.equivalent(eAsLambdaExp.myBodyExp);
            }
        }

        return result;
    }

    /**
     * <p>This method returns a deep copy of the body expression.</p>
     *
     * @return The {@link Exp} representation object.
     */
    public Exp getBody() {
        return myBodyExp.clone();
    }

    /**
     * <p>This method returns a deep copy of all the lambda parameter variables.</p>
     *
     * @return A list containing all the parameter {@link MathVarDec}s.
     */
    public List<MathVarDec> getParameters() {
        return copyParameters();
    }

    /**
     * <p>This method returns a deep copy of the list of
     * subexpressions.</p>
     *
     * @return A list containing subexpressions ({@link Exp}s).
     */
    @Override
    public List<Exp> getSubExpressions() {
        List<Exp> list = new ArrayList<>();
        list.add(myBodyExp.clone());

        return list;
    }

    /**
     * <p>This method applies VC Generator's remember rule.
     * For all inherited programming expression classes, this method
     * should throw an exception.</p>
     *
     * @return The resulting {@link LambdaExp} from applying the remember rule.
     */
    @Override
    public Exp remember() {
        Exp newBody = ((MathExp) myBodyExp).remember();

        return new LambdaExp(new Location(myLoc), copyParameters(), newBody);
    }

    /**
     * <p>This method adds a new expression to our list of subexpressions.</p>
     *
     * @param index The index in our subexpression list.
     * @param e The new {@link Exp} to be added.
     */
    // TODO: See the message in Exp.
    /*public void setSubExpression(int index, Exp e) {
        body = e;
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
        sb.append("lambda (");

        Iterator<MathVarDec> it = myParameters.iterator();
        while (it.hasNext()) {
            sb.append(it.next().toString());

            if (it.hasNext()) {
                sb.append(", ");
            }
        }

        sb.append(").(");
        sb.append(myBodyExp.toString());
        sb.append(")");

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
        return new LambdaExp(new Location(myLoc), copyParameters(), myBodyExp
                .clone());
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
        return new LambdaExp(new Location(myLoc), copyParameters(), substitute(
                myBodyExp, substitutions));
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>This is a helper method that makes a copy of the
     * list containing all the parameter variables.</p>
     *
     * @return A list containing {@link MathVarDec}s.
     */
    private List<MathVarDec> copyParameters() {
        List<MathVarDec> copyParameters = new ArrayList<>();
        for (MathVarDec v : myParameters) {
            copyParameters.add(v.clone());
        }

        return copyParameters;
    }
}
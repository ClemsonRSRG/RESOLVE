/*
 * LambdaExp.java
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
import edu.clemson.cs.rsrg.absyn.declarations.variabledecl.MathVarDec;
import edu.clemson.cs.rsrg.parsing.data.Location;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * <p>This is the class for all the mathematical lambda expression objects
 * that the compiler builds using the ANTLR4 AST nodes.</p>
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
     * {@inheritDoc}
     */
    @Override
    public final String asString(int indentSize, int innerIndentInc) {
        StringBuffer sb = new StringBuffer();
        printSpace(indentSize, sb);
        sb.append("lambda (");

        Iterator<MathVarDec> it = myParameters.iterator();
        while (it.hasNext()) {
            sb.append(it.next().asString(0, innerIndentInc));

            if (it.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append(").(");
        sb.append(myBodyExp.asString(0, innerIndentInc));
        sb.append(")");

        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean containsExp(Exp exp) {
        return myBodyExp.containsExp(exp);
    }

    /**
     * {@inheritDoc}
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

        LambdaExp lambdaExp = (LambdaExp) o;

        if (!myParameters.equals(lambdaExp.myParameters))
            return false;
        return myBodyExp.equals(lambdaExp.myBodyExp);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean equivalent(Exp e) {
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
     * <p>This method returns the body expression.</p>
     *
     * @return The {@link Exp} representation object.
     */
    public final Exp getBody() {
        return myBodyExp;
    }

    /**
     * <p>This method returns all the lambda parameter variables.</p>
     *
     * @return A list containing all the parameter {@link MathVarDec}s.
     */
    public final List<MathVarDec> getParameters() {
        return myParameters;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final List<Exp> getSubExpressions() {
        List<Exp> list = new ArrayList<>();
        list.add(myBodyExp);

        return list;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        int result = super.hashCode();
        result = 31 * result + myParameters.hashCode();
        result = 31 * result + myBodyExp.hashCode();
        return result;
    }

    /**
     * <p>This method applies VC Generator's remember rule.
     * For all inherited programming expression classes, this method
     * should throw an exception.</p>
     *
     * @return The resulting {@link LambdaExp} from applying the remember rule.
     */
    @Override
    public final Exp remember() {
        Exp newBody = ((MathExp) myBodyExp).remember();

        return new LambdaExp(cloneLocation(), copyParameters(), newBody);
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
        return new LambdaExp(cloneLocation(), copyParameters(), myBodyExp
                .clone());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final Exp substituteChildren(Map<Exp, Exp> substitutions) {
        return new LambdaExp(cloneLocation(), copyParameters(), substitute(
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
            copyParameters.add((MathVarDec) v.clone());
        }

        return copyParameters;
    }
}
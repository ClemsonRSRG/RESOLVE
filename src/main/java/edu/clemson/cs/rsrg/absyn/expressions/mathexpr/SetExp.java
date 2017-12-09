/*
 * SetExp.java
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
import java.util.List;
import java.util.Map;

/**
 * <p>This is the class for all the mathematical set expression objects
 * of the kind {@code {x : Z | x < y}} that the compiler builds using
 * the ANTLR4 AST nodes.</p>
 *
 * @version 2.0
 */
public class SetExp extends MathExp {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The mathematical variable in this set expression.</p> */
    private final MathVarDec myVar;

    /** <p>The set expression's where part.</p> */
    private final Exp myWhereExp;

    /** <p>The set expression's body.</p> */
    private final Exp myBodyExp;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs a set expression.</p>
     *
     * @param l A {@link Location} representation object.
     * @param var A {@link MathVarDec} representing the expression's variable.
     * @param where A {@link Exp} representing the where clause.
     * @param body A {@link Exp} representing the body of the expression.
     */
    public SetExp(Location l, MathVarDec var, Exp where, Exp body) {
        super(l);
        myVar = var;
        myWhereExp = where;
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

        sb.append("{");
        sb.append(myVar.asString(0, innerIndentInc));

        if (myWhereExp != null) {
            sb.append(" where ");
            sb.append(myWhereExp.asString(0, innerIndentInc));
        }

        sb.append(" | ");
        sb.append(myBodyExp.asString(0, innerIndentInc));

        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean containsExp(Exp exp) {
        boolean found = myWhereExp.containsExp(exp);
        if (!found) {
            found = myBodyExp.containsExp(exp);
        }

        return found;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean containsVar(String varName, boolean IsOldExp) {
        boolean found = myWhereExp.containsVar(varName, IsOldExp);
        if (!found) {
            found = myBodyExp.containsVar(varName, IsOldExp);
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

        SetExp setExp = (SetExp) o;

        if (!myVar.equals(setExp.myVar))
            return false;
        if (myWhereExp != null ? !myWhereExp.equals(setExp.myWhereExp)
                : setExp.myWhereExp != null)
            return false;
        return myBodyExp.equals(setExp.myBodyExp);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean equivalent(Exp e) {
        boolean retval = e instanceof SetExp;
        if (retval) {
            SetExp eAsSetExp = (SetExp) e;
            retval =
                    myVar.getName().equals(eAsSetExp.myVar.getName())
                            && myVar.getTy().equals(eAsSetExp.myVar.getTy());
            retval &= myWhereExp.equivalent(eAsSetExp.myWhereExp);
            retval &= myBodyExp.equivalent(eAsSetExp.myBodyExp);
        }

        return retval;
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
     * {@inheritDoc}
     */
    @Override
    public final List<Exp> getSubExpressions() {
        List<Exp> list = new ArrayList<>();
        list.add(myWhereExp);
        list.add(myBodyExp);

        return list;
    }

    /**
     * <p>This method returns the variable.</p>
     *
     * @return The {@link MathVarDec} representation object.
     */
    public final MathVarDec getVar() {
        return myVar;
    }

    /**
     * <p>This method returns the where expression.</p>
     *
     * @return The {@link Exp} representation object.
     */
    public final Exp getWhere() {
        return myWhereExp;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        int result = super.hashCode();
        result = 31 * result + myVar.hashCode();
        result = 31 * result + (myWhereExp != null ? myWhereExp.hashCode() : 0);
        result = 31 * result + myBodyExp.hashCode();
        return result;
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
        Exp newWhere = null;
        if (myWhereExp != null) {
            newWhere = myWhereExp.clone();
        }

        return new SetExp(cloneLocation(), (MathVarDec) myVar.clone(),
                newWhere, myBodyExp.clone());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final Exp substituteChildren(Map<Exp, Exp> substitutions) {
        Exp newWhere = null;
        if (myWhereExp != null) {
            newWhere = substitute(myWhereExp, substitutions);
        }

        return new SetExp(cloneLocation(), (MathVarDec) myVar.clone(),
                newWhere, substitute(myBodyExp, substitutions));
    }

}
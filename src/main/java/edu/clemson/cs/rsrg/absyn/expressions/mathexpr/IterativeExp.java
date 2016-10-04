/**
 * IterativeExp.java
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
package edu.clemson.cs.rsrg.absyn.expressions.mathexpr;

import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.absyn.declarations.variabledecl.MathVarDec;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>This is the class for all the mathematical iterative expression objects
 * that the compiler builds using the ANTLR4 AST nodes.</p>
 *
 * @version 2.0
 */
public class IterativeExp extends MathExp {

    // ===========================================================
    // Operators
    // ===========================================================

    /**
     * <p>This defines the various iterable operators.</p>
     *
     * @version 2.0
     */
    public enum Operator {
        SUM {

            @Override
            public String toString() {
                return "SUM";
            }

        },
        PRODUCT {

            @Override
            public String toString() {
                return "PRODUCT";
            }

        },
        CONCATENATION {

            @Override
            public String toString() {
                return "CONCATENATION";
            }

        },
        UNION {

            @Override
            public String toString() {
                return "UNION";
            }

        },
        INTERSECTION {

            @Override
            public String toString() {
                return "INTERSECTION";
            }

        };

        /**
         * <p>This method returns a deep copy of the operator name.</p>
         *
         * @param l A {@link Location} representation object.
         *
         * @return A {@link PosSymbol} object containing the operator.
         */
        public PosSymbol getOperatorAsPosSymbol(Location l) {
            return new PosSymbol(l.clone(), toString());
        }
    }

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The expression's operation.</p> */
    private final Operator myOperator;

    /** <p>The mathematical variable in this iterative expression.</p> */
    private final MathVarDec myVar;

    /** <p>The iterative expression's where part.</p> */
    private final Exp myWhereExp;

    /** <p>The iterative expression's body.</p> */
    private final Exp myBodyExp;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs a iterative expression.</p>
     *
     * @param l A {@link Location} representation object.
     * @param operator A {@link Operator} representing the operator.
     * @param var A {@link MathVarDec} representing the expression's variable.
     * @param where A {@link Exp} representing the where clause.
     * @param body A {@link Exp} representing the body of the expression.
     */
    public IterativeExp(Location l, Operator operator, MathVarDec var,
            Exp where, Exp body) {
        super(l);
        myOperator = operator;
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
        sb.append(myOperator);
        sb.append(" ");

        sb.append(myVar.getName().asString(0, innerIndentInc));
        sb.append(" : ");
        sb.append(myVar.getTy().asString(0, innerIndentInc));

        if (myWhereExp != null) {
            sb.append(" where ");
            sb.append(myWhereExp.asString(0, innerIndentInc));
        }

        sb.append(",\n");
        sb.append(myBodyExp.asString(indentSize + innerIndentInc,
                innerIndentInc));

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

        IterativeExp that = (IterativeExp) o;

        if (myOperator != that.myOperator)
            return false;
        if (!myVar.equals(that.myVar))
            return false;
        if (myWhereExp != null ? !myWhereExp.equals(that.myWhereExp)
                : that.myWhereExp != null)
            return false;
        return myBodyExp.equals(that.myBodyExp);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean equivalent(Exp e) {
        boolean retval = e instanceof IterativeExp;
        if (retval) {
            IterativeExp eAsIterativeExp = (IterativeExp) e;
            retval =
                    myOperator.getOperatorAsPosSymbol(myLoc).equals(
                            eAsIterativeExp.myOperator
                                    .getOperatorAsPosSymbol(myLoc));
            retval &=
                    myVar.getName().equals(eAsIterativeExp.myVar.getName())
                            && myVar.getTy().equals(
                                    eAsIterativeExp.myVar.getTy());
            retval &= myWhereExp.equivalent(eAsIterativeExp.myWhereExp);
            retval &= myBodyExp.equivalent(eAsIterativeExp.myBodyExp);
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
     * <p>This method returns the operator.</p>
     *
     * @return A {@link Operator} object containing the operator.
     */
    public final Operator getOperator() {
        return myOperator;
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
        result = 31 * result + myOperator.hashCode();
        result = 31 * result + myVar.hashCode();
        result = 31 * result + (myWhereExp != null ? myWhereExp.hashCode() : 0);
        result = 31 * result + myBodyExp.hashCode();
        return result;
    }

    /**
     * <p>This method applies VC Generator's remember rule.
     * For all inherited programming expression classes, this method
     * should throw an exception.</p>
     *
     * @return The resulting {@link IterativeExp} from applying the remember rule.
     */
    @Override
    public final IterativeExp remember() {
        Exp newWhere = ((MathExp) myWhereExp).remember();
        Exp newBody = ((MathExp) myBodyExp).remember();

        return new IterativeExp(cloneLocation(), myOperator, (MathVarDec) myVar
                .clone(), newWhere, newBody);
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

        return new IterativeExp(cloneLocation(), myOperator, (MathVarDec) myVar
                .clone(), newWhere, myBodyExp.clone());
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

        return new IterativeExp(cloneLocation(), myOperator, (MathVarDec) myVar
                .clone(), newWhere, substitute(myBodyExp, substitutions));
    }

}
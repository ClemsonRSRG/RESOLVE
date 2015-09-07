/**
 * EqualsExp.java
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
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>This is the class for all the mathematical equality/inequality expressions
 * that the compiler builds from the ANTLR4 AST tree.</p>
 *
 * @version 2.0
 */
public class EqualsExp extends InfixExp {

    // ===========================================================
    // Operators
    // ===========================================================

    public enum Operator {
        EQUAL {

            @Override
            public String toString() {
                return "=";
            }

        },
        NOT_EQUAL {

            @Override
            public String toString() {
                return "/=";
            }

        };

        /**
         * <p>This method returns a deep copy of the operator name.</p>
         *
         * @param l A {@link Location} representation object.
         *
         * @return A {link PosSymbol} object containing the operator.
         */
        public PosSymbol getOperatorAsPosSymbol(Location l) {
            return new PosSymbol(new Location(l), toString());
        }
    }

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The expression's operation.</p> */
    private final Operator myOperator;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs an equality/inequality expression.</p>
     *
     * @param l A {@link Location} representation object.
     * @param qual A {@link PosSymbol} representing the expression's qualifier.
     * @param left A {@link Exp} representing the left hand side.
     * @param operator A {@link Operator} representing the operator.
     * @param right A {@link Exp} representing the right hand side.
     */
    public EqualsExp(Location l, PosSymbol qual, Exp left, Operator operator,
            Exp right) {
        super(l, qual, left, operator.getOperatorAsPosSymbol(l), right);
        myOperator = operator;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This method overrides the default equals method implementation
     * for the {@link EqualsExp} class.</p>
     *
     * @param o Object to be compared.
     *
     * @return True if all the fields are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if (o instanceof EqualsExp) {
            EqualsExp eAsEqualsExp = (EqualsExp) o;
            result = myLoc.equals(eAsEqualsExp.myLoc);

            if (result) {
                result =
                        (myOperator == eAsEqualsExp.myOperator)
                                && myLeftHandSide
                                        .equals(eAsEqualsExp.myLeftHandSide)
                                && myRightHandSide
                                        .equals(eAsEqualsExp.myRightHandSide);
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
        boolean retval = e instanceof EqualsExp;
        if (retval) {
            EqualsExp eAsEquals = (EqualsExp) e;
            retval =
                    (myOperator == eAsEquals.myOperator)
                            && (myLeftHandSide
                                    .equivalent(eAsEquals.myLeftHandSide))
                            && (myRightHandSide
                                    .equivalent(eAsEquals.myRightHandSide));
        }

        return retval;
    }

    /**
     * <p>This method returns the operator.</p>
     *
     * @return A {link Operator} object containing the operator.
     */
    public Operator getOperator() {
        return myOperator;
    }

    /**
     * <p>This method applies VC Generator's remember rule.
     * For all inherited programming expression classes, this method
     * should throw an exception.</p>
     *
     * @return The resulting {@link EqualsExp} from applying the remember rule.
     */
    @Override
    public EqualsExp remember() {
        Exp newLeft = ((MathExp) myLeftHandSide).remember();
        Exp newRight = ((MathExp) myRightHandSide).remember();

        PosSymbol qualifier = null;
        if (myQualifier != null) {
            qualifier = myQualifier.clone();
        }

        return new EqualsExp(new Location(myLoc), qualifier, newLeft,
                myOperator, newRight);
    }

    /**
     * <p>This method applies the VC Generator's simplification step.</p>
     *
     * @return The resulting {@link MathExp} from applying the simplification step.
     */
    @Override
    public MathExp simplify() {
        Exp simplified;
        if (myLeftHandSide.equivalent(myRightHandSide)) {
            simplified =
                    MathExp.getTrueVarExp(myLoc, myMathType.getTypeGraph());
        }
        else {
            simplified = this.clone();
        }

        return (MathExp) simplified;
    }

    /**
     * <p>This method is used to convert a {@link Exp} into the prover's
     * version of {@link PExp}. The key to this method is figuring out
     * where the different implications occur within the expression.</p>
     *
     * <p>However, for {@link EqualsExp}s, this will throw an
     * exception if we have equality, because we should have dealt with
     * simplifications before we attempt to split the VCs.</p>
     *
     * @param assumpts The assumption expressions for this expression.
     * @param single Boolean flag to indicate whether or not this is a
     *               standalone expression.
     *
     * @return A list of {link Exp} objects.
     */
    @Override
    public List<InfixExp> split(MathExp assumpts, boolean single) {
        List<InfixExp> lst = new ArrayList<>();

        if (myOperator == Operator.EQUAL) {
            throw new MiscErrorException("Cannot split an EqualsExp!", new IllegalStateException());
        }
        else {
            if (myLeftHandSide != null) {
                lst.addAll(((MathExp) myLeftHandSide).split(assumpts, single));
            }
            if (myRightHandSide != null) {
                lst.addAll(((MathExp) myRightHandSide).split(assumpts, single));
            }
        }

        return lst;
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
        PosSymbol qualifier = null;
        if (myQualifier != null) {
            qualifier = myQualifier.clone();
        }

        return new EqualsExp(new Location(myLoc), qualifier, myLeftHandSide
                .clone(), myOperator, myRightHandSide.clone());
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
        PosSymbol qualifier = null;
        if (myQualifier != null) {
            qualifier = myQualifier.clone();
        }

        return new EqualsExp(new Location(myLoc), qualifier, substitute(
                myLeftHandSide, substitutions), myOperator, substitute(
                myRightHandSide, substitutions));
    }

}
/**
 * InfixExp.java
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
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * <p>This is the class for all the mathematical infix expression objects
 * that the compiler builds using the ANTLR4 AST nodes.</p>
 *
 * @version 2.0
 */
public class InfixExp extends AbstractFunctionExp {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The expression on the left hand side.</p> */
    protected final Exp myLeftHandSide;

    /** <p>The expression's operation.</p> */
    protected final PosSymbol myOperationName;

    /** <p>The expression on the right hand side.</p> */
    protected final Exp myRightHandSide;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs an infix expression.</p>
     *
     * @param l A {@link Location} representation object.
     * @param qual A {@link PosSymbol} representing the expression's qualifier.
     * @param left A {@link Exp} representing the left hand side.
     * @param opName A {@link PosSymbol} representing the operator.
     * @param right A {@link Exp} representing the right hand side.
     */
    public InfixExp(Location l, PosSymbol qual, Exp left, PosSymbol opName,
            Exp right) {
        super(l, qual);
        myLeftHandSide = left;
        myOperationName = opName;
        myRightHandSide = right;
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

        if (myLeftHandSide != null) {
            sb.append(myLeftHandSide.asString(indentSize + innerIndentInc,
                    innerIndentInc));
        }

        if (myOperationName != null) {
            sb.append(myOperationName.asString(indentSize + innerIndentInc,
                    innerIndentInc));
        }

        if (myRightHandSide != null) {
            sb.append(myRightHandSide.asString(indentSize + innerIndentInc,
                    innerIndentInc));
        }

        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Exp compareWithAssumptions(Exp exp) {
        Exp retExp;
        if (this.equals(exp)) {
            retExp = VarExp.getTrueVarExp(myLoc, myMathType.getTypeGraph());
        }
        else if (myOperationName.equals("and")) {
            Exp newLeftSide = myLeftHandSide.compareWithAssumptions(exp);
            Exp newRightSide = myRightHandSide.compareWithAssumptions(exp);

            retExp =
                    new InfixExp(new Location(myLoc), myQualifier.clone(),
                            newLeftSide, myOperationName.clone(), newRightSide);
        }
        else {
            retExp = this.clone();
        }

        return retExp;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean containsExp(Exp exp) {
        boolean found = myLeftHandSide.containsExp(exp);
        if (!found) {
            found = myRightHandSide.containsExp(exp);
        }

        return found;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean containsVar(String varName, boolean IsOldExp) {
        boolean found = myLeftHandSide.containsVar(varName, IsOldExp);
        if (!found) {
            found = myRightHandSide.containsVar(varName, IsOldExp);
        }

        return found;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;

        InfixExp infixExp = (InfixExp) o;

        if (!myLeftHandSide.equals(infixExp.myLeftHandSide))
            return false;
        if (!myOperationName.equals(infixExp.myOperationName))
            return false;
        return myRightHandSide.equals(infixExp.myRightHandSide);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equivalent(Exp e) {
        boolean retval = e instanceof InfixExp;
        if (retval) {
            InfixExp eAsInfix = (InfixExp) e;
            retval =
                    posSymbolEquivalent(myOperationName,
                            eAsInfix.myOperationName)
                            && myLeftHandSide
                                    .equivalent(eAsInfix.myLeftHandSide)
                            && myRightHandSide
                                    .equivalent(eAsInfix.myRightHandSide);
        }

        return retval;
    }

    /**
     * <p>This method returns the left hand side expression.</p>
     *
     * @return The {@link Exp} representation object.
     */
    public final Exp getLeft() {
        return myLeftHandSide;
    }

    /**
     * <p>This method returns the operator name.</p>
     *
     * @return A {@link PosSymbol} object containing the operator.
     */
    @Override
    public final PosSymbol getOperatorAsPosSymbol() {
        return myOperationName;
    }

    /**
     * <p>This method returns the operator name in string format.</p>
     *
     * @return The operator as a string.
     */
    @Override
    public final String getOperatorAsString() {
        return myOperationName.toString();
    }

    /**
     * <p>This method returns the right hand side expression.</p>
     *
     * @return The {@link Exp} representation object.
     */
    public final Exp getRight() {
        return myRightHandSide;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final List<Exp> getSubExpressions() {
        List<Exp> subExps = new ArrayList<>();
        subExps.add(myLeftHandSide.clone());
        subExps.add(myRightHandSide.clone());

        return subExps;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + myLeftHandSide.hashCode();
        result = 31 * result + myOperationName.hashCode();
        result = 31 * result + myRightHandSide.hashCode();
        return result;
    }

    /**
     * <p>This method applies VC Generator's remember rule.
     * For all inherited programming expression classes, this method
     * should throw an exception.</p>
     *
     * @return The resulting {@link InfixExp} from applying the remember rule.
     */
    @Override
    public InfixExp remember() {
        Exp newLeft = ((MathExp) myLeftHandSide).remember();
        Exp newRight = ((MathExp) myRightHandSide).remember();

        PosSymbol qualifier = null;
        if (myQualifier != null) {
            qualifier = myQualifier.clone();
        }

        return new InfixExp(new Location(myLoc), qualifier, newLeft,
                myOperationName.clone(), newRight);
    }

    /**
     * <p>This method applies the VC Generator's simplification step.</p>
     *
     * @return The resulting {@link MathExp} from applying the simplification step.
     */
    @Override
    public MathExp simplify() {
        Exp retVal;
        InfixExp simplified = applySimplification();
        PosSymbol operatorName = simplified.getOperatorAsPosSymbol();

        // Further simplification of the left hand side
        Exp leftHandSide = simplified.getLeft();
        if (leftHandSide != null) {
            leftHandSide = ((MathExp) leftHandSide).simplify();
        }
        else {
            leftHandSide =
                    MathExp.getTrueVarExp(myLoc, myMathType.getTypeGraph());
        }

        // Further simplification of the right hand side
        Exp rightHandSide = simplified.getRight();
        if (rightHandSide != null) {
            rightHandSide = ((MathExp) rightHandSide).simplify();
        }
        else {
            rightHandSide =
                    MathExp.getTrueVarExp(myLoc, myMathType.getTypeGraph());
        }

        // Our right hand side is an InfixExp
        if (operatorName.equals("implies") && rightHandSide instanceof InfixExp) {
            PosSymbol innerOperaratorName =
                    ((InfixExp) rightHandSide).getOperatorAsPosSymbol();
            Exp innerLeftSide = ((InfixExp) rightHandSide).getLeft();
            Exp innerRightSide = ((InfixExp) rightHandSide).getRight();

            // Implies
            if (innerOperaratorName.equals("implies")) {
                // Simplify A -> B -> C to (A ^ B) -> C
                leftHandSide =
                        MathExp.formConjunct(leftHandSide.getLocation(),
                                leftHandSide, innerLeftSide);
                rightHandSide = innerRightSide;
            }
            // And
            else if (innerOperaratorName.equals("and")) {
                // Simplify A -> A ^ B to A -> B
                // Note that we don't really need the strict "equals()" here.
                if (leftHandSide.equivalent(innerLeftSide)) {
                    rightHandSide = ((MathExp) innerRightSide).simplify();
                }
                // Simplify A -> B ^ A to A -> B
                // Note that we don't really need the strict "equals()" here.
                else if (rightHandSide.equivalent(innerRightSide)) {
                    rightHandSide = ((MathExp) innerLeftSide).simplify();
                }
            }
        }

        // Our left hand side is an InfixExp
        if (operatorName.equals("implies") && leftHandSide instanceof InfixExp) {
            // Contains only "and"-ed expressions
            if (((InfixExp) leftHandSide).onlyAndExps()) {
                Iterator<Exp> iter =
                        ((InfixExp) leftHandSide).getExpressions().iterator();
                while (iter.hasNext()) {
                    rightHandSide =
                            rightHandSide.compareWithAssumptions(iter.next());
                }
            }
        }
        else if (operatorName.equals("implies")
                && leftHandSide instanceof InfixExp
                && rightHandSide instanceof InfixExp) {
            // Contains only "and"-ed expressions
            if (((InfixExp) leftHandSide).onlyAndExps()
                    && ((InfixExp) rightHandSide).onlyAndExps()) {
                Iterator<Exp> iter =
                        ((InfixExp) leftHandSide).getExpressions().iterator();
                while (iter.hasNext()) {
                    rightHandSide =
                            rightHandSide.compareWithAssumptions(iter.next());
                }
            }
        }

        //Simplify (A ^ true) to A or (true ^ A) to A
        PosSymbol qual = myQualifier.clone();
        if (operatorName.equals("and")) {
            if (MathExp.isLiteralTrue(leftHandSide)) {
                retVal = rightHandSide.clone();
            }
            else if (MathExp.isLiteralTrue(rightHandSide)) {
                retVal = leftHandSide.clone();
            }
            else {
                retVal =
                        new InfixExp(new Location(myLoc), qual, leftHandSide,
                                operatorName, rightHandSide);
            }
        }
        // Simplify A -> true to true
        else if (operatorName.equals("implies")
                && rightHandSide instanceof VarExp
                && MathExp.isLiteralTrue(rightHandSide)) {
            retVal = MathExp.getTrueVarExp(myLoc, myMathType.getTypeGraph());
        }
        else {
            retVal =
                    new InfixExp(new Location(myLoc), qual, leftHandSide,
                            operatorName, rightHandSide);

            if (retVal.equivalent(this)) {
                retVal = ((MathExp) retVal).simplify();
            }
        }

        return (MathExp) retVal;
    }

    /**
     * {@inheritDoc}
     */
    // TODO: Understand this and put more inline comments!
    @Override
    public List<InfixExp> split(MathExp assumpts, boolean single) {
        List<InfixExp> lst = new ArrayList<>();
        MathExp tmpLeft, tmpRight;
        if (myOperationName.toString().equals("and")) {
            if (myLeftHandSide != null) {
                lst.addAll(((MathExp) myLeftHandSide).split(assumpts, single));
            }
            if (myRightHandSide != null) {
                lst.addAll(((MathExp) myRightHandSide).split(assumpts, single));
            }
        } else if (myOperationName.toString().equals("implies")) {
            if (myLeftHandSide instanceof InfixExp) {
                tmpLeft = (MathExp) ((InfixExp) myLeftHandSide).getAssumptions();
                lst = ((MathExp) myLeftHandSide).split(assumpts, false);
            } else {
                tmpLeft = (MathExp) myLeftHandSide;
            }

            if (assumpts != null) {
                tmpLeft = MathExp.formConjunct(assumpts.getLocation(), assumpts, tmpLeft);
            }

            if (myRightHandSide instanceof InfixExp) {
                tmpRight = (MathExp) ((InfixExp) myRightHandSide).getAssertions();

                lst = ((MathExp) myRightHandSide).split(tmpLeft, single);

                if (tmpRight == null)
                    return lst;
            } else {
                tmpRight = (MathExp) myRightHandSide;

                if (!(tmpLeft == null || tmpRight == null)) {
                    lst.add((InfixExp) MathExp.formImplies(myLoc, tmpLeft, tmpRight));
                }
            }

        } else if (single) {
            lst.add((InfixExp) MathExp.formImplies(myLoc, assumpts, this));
        }

        return lst;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String toString() {
        StringBuffer sb = new StringBuffer();

        if (myQualifier != null) {
            sb.append(myQualifier.toString());
            sb.append("::");
        }

        if (myLeftHandSide != null) {
            sb.append("(");
            sb.append(myLeftHandSide.toString());
            sb.append(" ");
        }

        if (myOperationName != null) {
            sb.append(getOperatorAsString());
            sb.append(" ");
        }

        if (myRightHandSide != null) {
            sb.append(myRightHandSide.toString());
            sb.append(")");
        }

        return sb.toString();
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * {@inheritDoc}
     */
    @Override
    protected Exp copy() {
        PosSymbol qualifier = null;
        if (myQualifier != null) {
            qualifier = myQualifier.clone();
        }

        return new InfixExp(new Location(myLoc), qualifier, myLeftHandSide
                .clone(), myOperationName.clone(), myRightHandSide.clone());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Exp substituteChildren(Map<Exp, Exp> substitutions) {
        PosSymbol qualifier = null;
        if (myQualifier != null) {
            qualifier = myQualifier.clone();
        }

        return new InfixExp(new Location(myLoc), qualifier, substitute(
                myLeftHandSide, substitutions), myOperationName.clone(),
                substitute(myRightHandSide, substitutions));
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>This helper method first attempts to simplify the left and right hand
     * side of this expression. The result is then stored in a
     * new {@link InfixExp} and returned.</p>
     *
     * @return A {@link InfixExp} representation object.
     */
    private InfixExp applySimplification() {
        Exp leftSimplify = ((MathExp) myLeftHandSide).simplify();
        Exp rightSimplify = ((MathExp) myRightHandSide).simplify();

        return new InfixExp(new Location(myLoc), myQualifier.clone(),
                leftSimplify, myOperationName.clone(), rightSimplify);
    }

    /**
     * <p>This helper method returns all the assertions in the current
     * {@link InfixExp}.</p>
     *
     * @return The resulting {@link Exp} object.
     */
    private Exp getAssertions() {
        Exp retval = null;

        // If we have a conjuncted expression
        if (getOperatorAsString().equals("and")) {
            // Get the assertion from the left hand side
            // and from the right hand side.
            Exp tmpLeft, tmpRight;
            if (myLeftHandSide instanceof InfixExp) {
                tmpLeft = ((InfixExp) myLeftHandSide).getAssertions();
            }
            else {
                tmpLeft = myLeftHandSide.clone();
            }

            if (myRightHandSide instanceof InfixExp) {
                tmpRight = ((InfixExp) myRightHandSide).getAssertions();
            }
            else {
                tmpRight = myRightHandSide.clone();
            }

            retval = MathExp.formConjunct(myLoc, tmpLeft, tmpRight);
        }
        // For all expressions that are not implications,
        // we make a copy of ourselves.
        else if (!(getOperatorAsString().equals("implies"))) {
            retval = this.clone();
        }

        // If it is an implication, then there are no assertions,
        // so this method should return "null".
        return retval;
    }

    /**
     * <p>This helper method returns all the assumptions in the current
     * {@link InfixExp}.</p>
     *
     * @return The resulting {@link Exp} object.
     */
    private Exp getAssumptions() {
        Exp retval;

        // If we have an implication or a conjuncted expression
        if (getOperatorAsString().equals("implies")
                || getOperatorAsString().equals("and")) {
            // Get the assumptions from the left hand side
            // and from the right hand side.
            Exp tmpLeft, tmpRight;
            if (myLeftHandSide instanceof InfixExp) {
                tmpLeft = ((InfixExp) myLeftHandSide).getAssumptions();
            }
            else {
                tmpLeft = myLeftHandSide.clone();
            }

            if (myRightHandSide instanceof InfixExp) {
                tmpRight = ((InfixExp) myRightHandSide).getAssumptions();
            }
            else {
                tmpRight = myRightHandSide.clone();
            }

            retval = MathExp.formConjunct(myLoc, tmpLeft, tmpRight);
        }
        // Make a deep copy of ourselves since we couldn't find
        // any assumptions.
        else {
            retval = this.clone();
        }

        return retval;
    }

    /**
     * <p>This helper method returns all the expressions in the current
     * {@link InfixExp} in a {@link List}.</p>
     *
     * @return A list containing all the {@link Exp} object.
     */
    private List<Exp> getExpressions() {
        List<Exp> lst = new ArrayList<>();
        if (!myOperationName.equals("and") && !myOperationName.equals("implies")) {
            lst.add(this.clone());
        }
        if ((myLeftHandSide instanceof InfixExp)) {
            lst.addAll(((InfixExp) myLeftHandSide).getExpressions());
            if (myRightHandSide instanceof InfixExp) {
                lst.addAll(((InfixExp) myRightHandSide).getExpressions());
            }
            else {
                lst.add(myRightHandSide);
            }
        }
        else {
            lst.add(myLeftHandSide);
            if (myRightHandSide instanceof InfixExp) {
                lst.addAll(((InfixExp) myRightHandSide).getExpressions());
            }
            else {
                lst.add(myRightHandSide);
            }
        }

        return lst;
    }

    /**
     * <p>This helper method checks to see if the current
     * {@link InfixExp} only contains conjuncted expressions.</p>
     *
     * @return True if it is a conjuncted expression, false otherwise.
     */
    // TODO: Understand this and put more inline comments!
    private boolean onlyAndExps() {
        boolean result = false;
        if ((myLeftHandSide instanceof InfixExp)) {
            if (((InfixExp) myLeftHandSide).onlyAndExps())
                if (myRightHandSide instanceof InfixExp) {
                    if (((InfixExp) myRightHandSide).onlyAndExps()) {
                        if (!myOperationName.equals("implies")) {
                            result = true;
                        }
                    }
                }
                else {
                    if (!myOperationName.equals("implies")) {
                        result = true;
                    }
                }
        }
        else {
            if (myRightHandSide instanceof InfixExp) {
                if (((InfixExp) myRightHandSide).onlyAndExps()) {
                    if (!myOperationName.equals("implies")) {
                        result = true;
                    }
                }
            }
            else {
                if (!myOperationName.equals("implies")) {
                    result = true;
                }
            }

        }
        return result;
    }

}
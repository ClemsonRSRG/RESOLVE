/**
 * InfixExp.java
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
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * <p>This is the abstract base class for all the mathematical infix expressions
 * that the compiler builds from the ANTLR4 AST tree.</p>
 *
 * @version 2.0
 */
public class InfixExp extends AbstractFunctionExp {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The expression on the left hand side.</p> */
    private final Exp myLeftHandSide;

    /** <p>The expression's operation.</p> */
    private final PosSymbol myOperationName;

    /** <p>The expression on the right hand side.</p> */
    private final Exp myRightHandSide;

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
        sb.append("InfixExp\n");

        if (myLeftHandSide != null) {
            sb.append(myLeftHandSide.asString(indentSize + innerIndentSize,
                    innerIndentSize));
        }

        if (myOperationName != null) {
            sb.append(myOperationName.asString(indentSize + innerIndentSize,
                    innerIndentSize));
        }

        if (myRightHandSide != null) {
            sb.append(myRightHandSide.asString(indentSize + innerIndentSize,
                    innerIndentSize));
        }

        return sb.toString();
    }

    /**
     * <p>Compares to see if the expression matches this object.</p>
     *
     * @param exp A {@link Exp} to compare.
     *
     * @return A {@link VarExp} containing "true" if it is exactly the same,
     * otherwise just return a deep copy our ourselves.
     */
    @Override
    public Exp compareWithAssumptions(Exp exp) {
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
     * <p>This method attempts to find the provided expression in our
     * subexpressions.</p>
     *
     * @param exp The expression we wish to locate.
     *
     * @return False.
     */
    @Override
    public boolean containsExp(Exp exp) {
        boolean found = myLeftHandSide.containsExp(exp);
        if (!found) {
            found = myRightHandSide.containsExp(exp);
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
     * @return False.
     */
    @Override
    public boolean containsVar(String varName, boolean IsOldExp) {
        boolean found = myLeftHandSide.containsVar(varName, IsOldExp);
        if (!found) {
            found = myRightHandSide.containsVar(varName, IsOldExp);
        }

        return found;
    }

    /**
     * <p>This method overrides the default equals method implementation
     * for the {@link InfixExp} class.</p>
     *
     * @param o Object to be compared.
     *
     * @return True if all the fields are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if (o instanceof InfixExp) {
            InfixExp eAsInfixExp = (InfixExp) o;
            result = myLoc.equals(eAsInfixExp.myLoc);

            if (result) {
                result =
                        myOperationName.equals(eAsInfixExp.myOperationName)
                                && myLeftHandSide
                                        .equals(eAsInfixExp.myLeftHandSide)
                                && myRightHandSide
                                        .equals(eAsInfixExp.myRightHandSide);
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
     * <p>This method returns a deep copy of the left hand side expression.</p>
     *
     * @return The {@link Exp} representation object.
     */
    public Exp getLeft() {
        return myLeftHandSide.clone();
    }

    /**
     * <p>This method returns a deep copy of the operator name.</p>
     *
     * @return A {link PosSymbol} object containing the operator.
     */
    @Override
    public PosSymbol getOperatorAsPosSymbol() {
        return myOperationName.clone();
    }

    /**
     * <p>This method returns a deep copy of the operator name.</p>
     *
     * @return The operator as a string.
     */
    @Override
    public String getOperatorAsString() {
        return myOperationName.toString();
    }

    /**
     * <p>This method returns a deep copy of the right hand side expression.</p>
     *
     * @return The {@link Exp} representation object.
     */
    public Exp getRight() {
        return myRightHandSide.clone();
    }

    /**
     * <p>This method method returns a deep copy of the list of
     * subexpressions.</p>
     *
     * @return A list containing subexpressions ({@link Exp}s).
     */
    @Override
    public List<Exp> getSubExpressions() {
        List<Exp> subExps = new ArrayList<>();
        subExps.add(myLeftHandSide);
        subExps.add(myRightHandSide);

        return subExps;
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
        Exp newLeft = myLeftHandSide;
        if (myLeftHandSide instanceof OldExp) {
            newLeft = ((MathExp) ((OldExp) myLeftHandSide).getExp()).remember();
        }

        Exp newRight = myRightHandSide;
        if (myRightHandSide instanceof OldExp) {
            newRight =
                    ((MathExp) ((OldExp) myRightHandSide).getExp()).remember();
        }

        if (newLeft != null) {
            newLeft = ((MathExp) newLeft).remember();
        }

        if (newRight != null) {
            newRight = ((MathExp) newRight).remember();
        }

        PosSymbol qualifier = null;
        if (myQualifier != null) {
            qualifier = myQualifier.clone();
        }

        return new InfixExp(new Location(myLoc), qualifier, newLeft,
                myOperationName.clone(), newRight);
    }

    /**
     * <p>This method adds a new expression to our list of subexpressions.</p>
     *
     * @param index The index in our subexpression list.
     * @param e The new {@link Exp} to be added.
     */
    // TODO: See the message in Exp.
    /*public void setSubExpression(int index, Exp e) {
        switch (index) {
            case 0:
                myLeftHandSide = e;
                break;
            case 1:
                myRightHandSide = e;
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
     * <p>This method is used to convert a {@link Exp} into the prover's
     * version of {@link PExp}. The key to this method is figuring out
     * where the different implications occur within the expression.</p>
     *
     * @param assumpts The assumption expressions for this expression.
     * @param single Boolean flag to indicate whether or not this is a
     *               standalone expression.
     *
     * @return A list of {link Exp} objects.
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

        if (myLeftHandSide != null) {
            sb.append("(");
            sb.append(myLeftHandSide.toString());
            sb.append(" ");
        }

        if (myOperationName != null) {
            sb.append(myOperationName.toString());
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
     * <p>Implemented by this concrete subclass of {@link Exp} to manufacture
     * a copy of themselves.</p>
     *
     * @return A new {@link Exp} that is a deep copy of the original.
     */
    @Override
    protected Exp copy() {
        return new InfixExp(new Location(myLoc), myQualifier.clone(),
                myLeftHandSide.clone(), myOperationName.clone(),
                myRightHandSide.clone());
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
        return new InfixExp(new Location(myLoc), myQualifier.clone(),
                substitute(myLeftHandSide, substitutions), myOperationName
                        .clone(), substitute(myRightHandSide, substitutions));
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
/**
 * FunctionExp.java
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

import edu.clemson.cs.r2jt.typeandpopulate2.entry.SymbolTableEntry;
import edu.clemson.cs.rsrg.absyn.Exp;
import edu.clemson.cs.rsrg.errorhandling.exception.MiscErrorException;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import java.io.InvalidClassException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * <p>This is the class for all the mathematical function expressions
 * that the compiler builds from the ANTLR4 AST tree.</p>
 *
 * @version 2.0
 */
public class FunctionExp extends AbstractFunctionExp {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The mathematical name expression for this function.</p> */
    private Exp myFuncNameExp;

    /** <p>The expression's argument fields</p> */
    private List<Exp> myArguments;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs a function expression with its associated
     * arguments.</p>
     *
     * @param l A {@link Location} representation object.
     * @param qualifier A {@link PosSymbol} qualifier name object.
     * @param name A {@link Exp} name expression object.
     * @param argList A list of {@link Exp} argument objects.
     * @param quantification The object's quantification (if any)
     */
    public FunctionExp(Location l, PosSymbol qualifier, Exp name,
            List<Exp> argList, SymbolTableEntry.Quantification quantification) {
        super(l, qualifier);
        myFuncNameExp = name;
        myArguments = argList;
        setQuantification(quantification);
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
        sb.append("FunctionExp\n");

        if (myQuantification != SymbolTableEntry.Quantification.NONE) {
            printSpace(indentSize + innerIndentSize, sb);
            sb.append(myQuantification.toString());
            sb.append(" ");
        }

        if (myQualifier != null) {
            sb.append(myQualifier.asString(indentSize + innerIndentSize,
                    innerIndentSize));
            sb.append("::");
        }

        sb.append(myFuncNameExp.asString(indentSize + innerIndentSize,
                innerIndentSize));

        if (myArguments != null) {
            sb.append("(");
            Iterator<Exp> it = myArguments.iterator();
            while (it.hasNext()) {
                sb.append(it.next().asString(indentSize + innerIndentSize,
                        innerIndentSize));

                if (it.hasNext()) {
                    sb.append(", ");
                }
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
        boolean found = myFuncNameExp.containsExp(exp);
        if (!found && myArguments != null) {
            Iterator<Exp> i = myArguments.iterator();
            while (i.hasNext() && !found) {
                Exp temp = i.next();
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
        boolean found = myFuncNameExp.containsVar(varName, IsOldExp);
        if (!found && myArguments != null) {
            Iterator<Exp> i = myArguments.iterator();
            while (i.hasNext() && !found) {
                Exp temp = i.next();
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
     * for the {@link FunctionExp} class.</p>
     *
     * @param o Object to be compared.
     *
     * @return True if all the fields are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if (o instanceof FunctionExp) {
            FunctionExp eAsFunctionExp = (FunctionExp) o;
            result = myLoc.equals(eAsFunctionExp.myLoc);

            if (result) {
                result =
                        posSymbolEquivalent(myQualifier,
                                eAsFunctionExp.myQualifier)
                                && myFuncNameExp
                                        .equals(eAsFunctionExp.myFuncNameExp)
                                && myQuantification
                                        .equals(eAsFunctionExp.myQuantification);

                if (myArguments != null && eAsFunctionExp.myArguments != null) {
                    Iterator<Exp> thisArgumentsExps = myArguments.iterator();
                    Iterator<Exp> eArgumentsExps =
                            eAsFunctionExp.myArguments.iterator();

                    while (result && thisArgumentsExps.hasNext()
                            && eArgumentsExps.hasNext()) {
                        result &=
                                thisArgumentsExps.next().equals(
                                        eArgumentsExps.next());
                    }

                    //Both had better have run out at the same time
                    result &=
                            (!thisArgumentsExps.hasNext())
                                    && (!eArgumentsExps.hasNext());
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
        boolean retval = e instanceof FunctionExp;

        if (retval) {
            FunctionExp eAsFunction = (FunctionExp) e;
            retval =
                    posSymbolEquivalent(myQualifier, eAsFunction.myQualifier)
                            && equivalent(myFuncNameExp,
                                    eAsFunction.myFuncNameExp)
                            && argsEquivalent(myArguments,
                                    eAsFunction.myArguments)
                            && myQuantification == eAsFunction.myQuantification;
        }

        return retval;
    }

    /**
     * <p>This method returns a deep copy of the function name expression.</p>
     *
     * @return The {@link Exp} representation object.
     */
    public Exp getName() {
        return myFuncNameExp.clone();
    }

    /**
     * <p>This method returns a deep copy of the operator name.</p>
     *
     * @return A {link PosSymbol} object containing the operator.
     */
    @Override
    public PosSymbol getOperatorAsPosSymbol() {
        if (!(myFuncNameExp instanceof VarExp)) {
            throw new MiscErrorException(
                    "We encountered an expression of the type "
                            + myFuncNameExp.getClass().getName(),
                    new InvalidClassException(""));
        }

        return ((VarExp) myFuncNameExp).getName();
    }

    /**
     * <p>This method returns a deep copy of the operator name.</p>
     *
     * @return The operator as a string.
     */
    @Override
    public String getOperatorAsString() {
        if (!(myFuncNameExp instanceof VarExp)) {
            throw new MiscErrorException(
                    "We encountered an expression of the type "
                            + myFuncNameExp.getClass().getName(),
                    new InvalidClassException(""));
        }

        return ((VarExp) myFuncNameExp).getName().getName();
    }

    /**
     * <p>This method returns a deep copy of all the argument expressions.</p>
     *
     * @return A list containing all the argument {@link Exp}s.
     */
    public List<Exp> getArguments() {
        return copyExps();
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
        list.add(myFuncNameExp);
        list.addAll(getArguments());

        return list;
    }

    /**
     * <p>This method applies VC Generator's remember rule.
     * For all inherited programming expression classes, this method
     * should throw an exception.</p>
     *
     * @return The resulting {@link FunctionExp} from applying the remember rule.
     */
    @Override
    public FunctionExp remember() {
        PosSymbol qualifier = null;
        if (myQualifier != null) {
            qualifier = myQualifier.clone();
        }

        Exp newNameExp;
        if (myFuncNameExp instanceof MathExp){
            newNameExp = ((MathExp) myFuncNameExp).remember();
        }
        else {
            throw new MiscErrorException("We encountered an expression of the type " +
                    myFuncNameExp.getClass().getName(),
                    new InvalidClassException(""));
        }

        List<Exp> newArgs = new ArrayList<>();
        for (Exp e : myArguments) {
            Exp copyExp;
            if (e instanceof MathExp){
                copyExp = ((MathExp) e).remember();
            }
            else {
                throw new MiscErrorException("We encountered an expression of the type " +
                        e.getClass().getName(),
                        new InvalidClassException(""));
            }

            newArgs.add(copyExp);
        }

        return new FunctionExp(new Location(myLoc), qualifier, newNameExp, newArgs, myQuantification);
    }

    /**
     * <p>This method adds a new expression to our list of subexpressions.</p>
     *
     * @param index The index in our subexpression list.
     * @param e The new {@link Exp} to be added.
     */
    // TODO: See the message in Exp.
    /*public void setSubExpression(int index, Exp e) {
        if (natural == null) {
            //List--for whatever reason--will not add null elements, so in
            //getSubExpression, the returned list will have "natural" (whatever
            //THAT means...) as its 0th element ONLY IF NATURAL IS NOT NULL,
            //otherwise the parameters will start immediately
            paramList.get(0).getArguments().set(index, e);
        }
        else {
            switch (index) {
                case 0:
                    natural = e;
                    break;
                default:
                    paramList.get(0).getArguments().set(index - 1, e);
                    break;
            }
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

        if (myQuantification != SymbolTableEntry.Quantification.NONE) {
            sb.append(myQuantification);
            sb.append(" ");
        }

        if (myQualifier != null) {
            sb.append(myQualifier.toString());
            sb.append("::");
        }

        sb.append(myFuncNameExp.toString());
        sb.append("(");
        Iterator<Exp> it = myArguments.iterator();
        while (it.hasNext()) {
            sb.append(it.next().toString());

            if (it.hasNext()) {
                sb.append(", ");
            }
        }
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
        PosSymbol qualifier = null;
        if (myQualifier != null) {
            qualifier = myQualifier.clone();
        }

        return new FunctionExp(new Location(myLoc), qualifier, myFuncNameExp
                .clone(), copyExps(), myQuantification);
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

        List<Exp> newArgs = new ArrayList<>();
        for (Exp f : myArguments) {
            newArgs.add(substitute(f, substitutions));
        }

        return new FunctionExp(new Location(myLoc), qualifier, substitute(
                myFuncNameExp, substitutions), newArgs, myQuantification);
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>This is a helper method that makes checks for argument
     * equivalency of the.</p>
     *
     * @return True if all arguments are equivalent, false otherwise.
     */
    private boolean argsEquivalent(List<Exp> a1, List<Exp> a2) {
        boolean retval = true;

        Iterator<Exp> args1 = a1.iterator();
        Iterator<Exp> args2 = a2.iterator();
        while (retval && args1.hasNext() && args2.hasNext()) {
            retval = args1.next().equivalent(args2.next());
        }

        return retval && !(args1.hasNext() || args2.hasNext());
    }

    /**
     * <p>This is a helper method that makes a copy of the
     * list containing all the argument expressions.</p>
     *
     * @return A list containing {@link Exp}s.
     */
    private List<Exp> copyExps() {
        List<Exp> copyArgs = new ArrayList<>();
        for (Exp exp : myArguments) {
            copyArgs.add(exp.clone());
        }

        return copyArgs;
    }
}
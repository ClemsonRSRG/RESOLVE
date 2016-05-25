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
 * <p>This is the class for all the mathematical function expression objects
 * that the compiler builds using the ANTLR4 AST nodes.</p>
 *
 * @version 2.0
 */
public class FunctionExp extends AbstractFunctionExp {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The mathematical name expression for this function.</p> */
    private final Exp myFuncNameExp;

    /** <p>The expression's argument fields</p> */
    private final List<Exp> myArguments;

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
     * {@inheritDoc}
     */
    @Override
    public final String asString(int indentSize, int innerIndentInc) {
        StringBuffer sb = new StringBuffer();
        printSpace(indentSize, sb);

        if (myQuantification != SymbolTableEntry.Quantification.NONE) {
            printSpace(indentSize + innerIndentInc, sb);
            sb.append(myQuantification.toString());
            sb.append(" ");
        }

        if (myQualifier != null) {
            sb.append(myQualifier.asString(indentSize + innerIndentInc,
                    innerIndentInc));
            sb.append("::");
        }

        sb.append(myFuncNameExp.asString(indentSize + innerIndentInc,
                innerIndentInc));

        if (myArguments != null) {
            sb.append("(");
            Iterator<Exp> it = myArguments.iterator();
            while (it.hasNext()) {
                sb.append(it.next().asString(indentSize + innerIndentInc,
                        innerIndentInc));

                if (it.hasNext()) {
                    sb.append(", ");
                }
            }
            sb.append(")");
        }

        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean containsExp(Exp exp) {
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
     * {@inheritDoc}
     */
    @Override
    public final boolean containsVar(String varName, boolean IsOldExp) {
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
     * {@inheritDoc}
     */
    @Override
    public final boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        FunctionExp that = (FunctionExp) o;

        if (!myFuncNameExp.equals(that.myFuncNameExp))
            return false;
        return myArguments.equals(that.myArguments);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean equivalent(Exp e) {
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
     * <p>This method returns the function name expression.</p>
     *
     * @return The {@link Exp} representation object.
     */
    public final Exp getName() {
        return myFuncNameExp;
    }

    /**
     * <p>This method returns the operator name.</p>
     *
     * @return A {@link PosSymbol} object containing the operator.
     */
    @Override
    public final PosSymbol getOperatorAsPosSymbol() {
        if (!(myFuncNameExp instanceof VarExp)) {
            throw new MiscErrorException(
                    "We encountered an expression of the type "
                            + myFuncNameExp.getClass().getName(),
                    new InvalidClassException(""));
        }

        return ((VarExp) myFuncNameExp).getName();
    }

    /**
     * <p>This method returns the operator name.</p>
     *
     * @return The operator as a string.
     */
    @Override
    public final String getOperatorAsString() {
        if (!(myFuncNameExp instanceof VarExp)) {
            throw new MiscErrorException(
                    "We encountered an expression of the type "
                            + myFuncNameExp.getClass().getName(),
                    new InvalidClassException(""));
        }

        return ((VarExp) myFuncNameExp).getName().getName();
    }

    /**
     * <p>This method returns all the argument expressions.</p>
     *
     * @return A list containing all the argument {@link Exp}s.
     */
    public final List<Exp> getArguments() {
        return myArguments;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final List<Exp> getSubExpressions() {
        List<Exp> list = new ArrayList<>();
        list.add(myFuncNameExp);
        list.addAll(copyExps());

        return list;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        int result = myFuncNameExp.hashCode();
        result = 31 * result + myArguments.hashCode();
        return result;
    }

    /**
     * <p>This method applies VC Generator's remember rule.
     * For all inherited programming expression classes, this method
     * should throw an exception.</p>
     *
     * @return The resulting {@link FunctionExp} from applying the remember rule.
     */
    @Override
    public final FunctionExp remember() {
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
     * <p>This method applies the VC Generator's simplification step.</p>
     *
     * @return The resulting {@link MathExp} from applying the simplification step.
     */
    @Override
    public final MathExp simplify() {
        return this.clone();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String toString() {
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
     * {@inheritDoc}
     */
    @Override
    protected final Exp copy() {
        PosSymbol qualifier = null;
        if (myQualifier != null) {
            qualifier = myQualifier.clone();
        }

        return new FunctionExp(new Location(myLoc), qualifier, myFuncNameExp
                .clone(), copyExps(), myQuantification);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final Exp substituteChildren(Map<Exp, Exp> substitutions) {
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
/**
 * ProgramFunctionExp.java
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
package edu.clemson.cs.rsrg.absyn.expressions.programexpr;

import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * <p>This is the class for all the programming function call expression objects
 * that the compiler builds using the ANTLR4 AST nodes.</p>
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
     * {@inheritDoc}
     */
    @Override
    public final String asString(int indentSize, int innerIndentInc) {
        StringBuffer sb = new StringBuffer();
        printSpace(indentSize, sb);

        if (myQualifier != null) {
            sb.append(myQualifier.asString(0, innerIndentInc));
            sb.append("::");
        }

        sb.append(myOperationName.asString(0, innerIndentInc));

        // Args
        sb.append("(");
        for (ProgramExp exp : myExpressionArgs) {
            sb.append(exp.asString(0, innerIndentInc));
        }
        sb.append(")");

        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean containsExp(Exp exp) {
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
     * {@inheritDoc}
     */
    @Override
    public final boolean containsVar(String varName, boolean IsOldExp) {
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

        ProgramFunctionExp that = (ProgramFunctionExp) o;

        if (myQualifier != null ? !myQualifier.equals(that.myQualifier)
                : that.myQualifier != null)
            return false;
        if (!myOperationName.equals(that.myOperationName))
            return false;
        return myExpressionArgs.equals(that.myExpressionArgs);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean equivalent(Exp e) {
        boolean result = (e instanceof ProgramFunctionExp);

        if (result) {
            ProgramFunctionExp eAsProgramFunctionExp = (ProgramFunctionExp) e;

            result =
                    posSymbolEquivalent(myQualifier,
                            eAsProgramFunctionExp.myQualifier)
                            && posSymbolEquivalent(myOperationName,
                                    eAsProgramFunctionExp.myOperationName);

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
     * <p>This method returns all the argument expressions.</p>
     *
     * @return A list containing all the argument {@link Exp}s.
     */
    public final List<ProgramExp> getArguments() {
        return myExpressionArgs;
    }

    /**
     * <p>This method returns the operation name.</p>
     *
     * @return The {@link PosSymbol} representation object.
     */
    public final PosSymbol getName() {
        return myOperationName;
    }

    /**
     * <p>This method returns the qualifier name.</p>
     *
     * @return The {@link PosSymbol} representation object.
     */
    public final PosSymbol getQualifier() {
        return myQualifier;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final List<Exp> getSubExpressions() {
        List<Exp> copyArgExps = new ArrayList<>();
        for (ProgramExp exp : myExpressionArgs) {
            copyArgExps.add(exp);
        }

        return copyArgExps;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        int result = super.hashCode();
        result =
                31 * result
                        + (myQualifier != null ? myQualifier.hashCode() : 0);
        result = 31 * result + myOperationName.hashCode();
        result = 31 * result + myExpressionArgs.hashCode();
        return result;
    }

    /**
     * <p>Sets the qualifier for this expression.</p>
     *
     * @param qualifier The qualifier for this expression.
     */
    public final void setQualifier(PosSymbol qualifier) {
        myQualifier = qualifier;
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * {@inheritDoc}
     */
    @Override
    protected final Exp copy() {
        PosSymbol newQualifier = null;
        if (myQualifier != null) {
            newQualifier = myQualifier.clone();
        }

        return new ProgramFunctionExp(new Location(myLoc), newQualifier,
                myOperationName.clone(), copyExps());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final Exp substituteChildren(Map<Exp, Exp> substitutions) {
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
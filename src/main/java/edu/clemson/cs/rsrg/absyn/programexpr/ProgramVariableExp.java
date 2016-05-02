/**
 * ProgramVariableExp.java
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
package edu.clemson.cs.rsrg.absyn.programexpr;

import edu.clemson.cs.rsrg.absyn.Exp;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>This is the abstract base class for all the programming variable expressions
 * that the compiler builds from the ANTLR4 AST tree.</p>
 *
 * @version 2.0
 */
public abstract class ProgramVariableExp extends ProgramExp {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The expression's qualifier</p> */
    private PosSymbol myQualifier;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>A helper constructor that allow us to store the location
     * of the created object directly in the this class.</p>
     *
     * @param l A {@link Location} representation object.
     * @param qual A {@link PosSymbol} representing the expression's qualifier.
     */
    protected ProgramVariableExp(Location l, PosSymbol qual) {
        super(l);
        myQualifier = qual;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This method attempts to find the provided expression in our
     * subexpressions. The result of this calling this method should
     * always be false, because we can not contain an expression.</p>
     *
     * @param exp The expression we wish to locate.
     *
     * @return False.
     */
    @Override
    public final boolean containsExp(Exp exp) {
        return false;
    }

    /**
     * <p>This method attempts to find an expression with the given name in our
     * subexpressions. The result of this calling this method should
     * always be false, because we can not contain a variable.</p>
     *
     * @param varName Expression name.
     * @param IsOldExp Flag to indicate if the given name is of the form
     *                 "#[varName]"
     *
     * @return False.
     */
    @Override
    public final boolean containsVar(String varName, boolean IsOldExp) {
        return false;
    }

    /**
     * <p>This method returns a deep copy of the qualifier name.</p>
     *
     * @return The {@link PosSymbol} representation object.
     */
    public final PosSymbol getQualifier() {
        PosSymbol qual = null;
        if (myQualifier != null) {
            qual = myQualifier.clone();
        }

        return qual;
    }

    /**
     * <p>This method returns a deep copy of the list of
     * subexpressions. The result of this calling this method should
     * always be an empty list, because we can not contain an expression.</p>
     *
     * @return A list containing subexpressions ({@link Exp}s).
     */
    @Override
    public final List<Exp> getSubExpressions() {
        return new ArrayList<>();
    }

    /**
     * <p>Sets the qualifier for this expression.</p>
     *
     * @param qualifier The qualifier for this expression.
     */
    public final void setQualifier(PosSymbol qualifier) {
        myQualifier = qualifier;
    }

    /**
     * <p>This method adds a new expression to our list of subexpressions.</p>
     *
     * @param index The index in our subexpression list.
     * @param e The new {@link Exp} to be added.
     */
    // TODO: See the message in Exp.
    //public void setSubExpression(int index, Exp e) {}

}
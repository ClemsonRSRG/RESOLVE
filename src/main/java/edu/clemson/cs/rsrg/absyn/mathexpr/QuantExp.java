/**
 * QuantExp.java
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

import edu.clemson.cs.r2jt.typeandpopulate2.entry.SymbolTableEntry;
import edu.clemson.cs.rsrg.absyn.Exp;
import edu.clemson.cs.rsrg.absyn.variables.MathVarDec;
import edu.clemson.cs.rsrg.parsing.data.Location;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * <p>This is the class for all the mathematical quantified expressions
 * that the compiler builds from the ANTLR4 AST tree.</p>
 *
 * @version 2.0
 */
public class QuantExp extends MathExp {

    /* TODO: Might need to revisit this
    public static final int NONE = 0;
    public static final int FORALL = 1;
    public static final int EXISTS = 2;
    public static final int UNIQUE = 3;*/

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The object's quantification (if any).</p> */
    private final SymbolTableEntry.Quantification myQuantification;

    /** <p>The mathematical variables in this quantified expression.</p> */
    private List<MathVarDec> myVars;

    /** <p>The quantified expression's where part.</p> */
    private final Exp myWhereExp;

    /** <p>The quantified expression's body.</p> */
    private final Exp myBodyExp;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs a quantified expression.</p>
     *
     * @param l A {@link Location} representation object.
     * @param quantifier A {@link SymbolTableEntry.Quantification} quantifier object.
     * @param vars A list of {@link MathVarDec}s representing the expression's variables.
     * @param where A {@link Exp} representing the where clause.
     * @param body A {@link Exp} representing the body of the expression.
     */
    public QuantExp(Location l, SymbolTableEntry.Quantification quantifier,
            List<MathVarDec> vars, Exp where, Exp body) {
        super(l);
        myQuantification = quantifier;
        myVars = vars;
        myWhereExp = where;
        myBodyExp = body;
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
        sb.append("QuantExp\n");

        if (myQuantification != SymbolTableEntry.Quantification.NONE) {
            sb.append(myQuantification);
        }

        if (myVars != null) {
            Iterator<MathVarDec> i = myVars.iterator();
            while (i.hasNext()) {
                MathVarDec m = i.next();
                sb.append(m.asString(indentSize, innerIndentSize));

                if (i.hasNext()) {
                    sb.append(", ");
                }
            }
        }

        if (myWhereExp != null) {
            sb.append(myWhereExp.asString(indentSize + innerIndentSize,
                    innerIndentSize));
        }

        sb.append(myBodyExp.asString(indentSize + innerIndentSize,
                innerIndentSize));

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
    public final boolean containsExp(Exp exp) {
        boolean found = myWhereExp.containsExp(exp);
        if (!found) {
            found = myBodyExp.containsExp(exp);
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
        boolean found = myWhereExp.containsVar(varName, IsOldExp);
        if (!found) {
            found = myBodyExp.containsVar(varName, IsOldExp);
        }

        return found;
    }

    /**
     * <p>This method overrides the default equals method implementation
     * for the {@link QuantExp} class.</p>
     *
     * @param o Object to be compared.
     *
     * @return True if all the fields are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if (o instanceof QuantExp) {
            QuantExp eAsQuantExp = (QuantExp) o;
            result = myLoc.equals(eAsQuantExp.myLoc);

            if (result) {
                if (myVars != null && eAsQuantExp.myVars != null) {
                    Iterator<MathVarDec> thisVars = myVars.iterator();
                    Iterator<MathVarDec> eVars = eAsQuantExp.myVars.iterator();
                    while (result && thisVars.hasNext() && eVars.hasNext()) {
                        result &= thisVars.next().equals(eVars.next());
                    }

                    //Both had better have run out at the same time
                    result &= (!thisVars.hasNext()) && (!eVars.hasNext());
                }

                if (result) {
                    result = myWhereExp.equals(eAsQuantExp.myWhereExp);

                    if (result) {
                        result = myBodyExp.equals(eAsQuantExp.myBodyExp);
                    }
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
        boolean retval = e instanceof QuantExp;
        if (retval) {
            QuantExp eAsQuantExp = (QuantExp) e;

            if (myVars != null && eAsQuantExp.myVars != null) {
                Iterator<MathVarDec> thisVars = myVars.iterator();
                Iterator<MathVarDec> eVars = eAsQuantExp.myVars.iterator();
                while (retval && thisVars.hasNext() && eVars.hasNext()) {
                    MathVarDec cThisVar = thisVars.next();
                    MathVarDec cEVar = eVars.next();
                    retval &=
                            cThisVar.getName().equals(cEVar.getName())
                                    && cThisVar.getTy().equals(cEVar.getTy());
                }

                //Both had better have run out at the same time
                retval &= (!thisVars.hasNext()) && (!eVars.hasNext());
            }

            retval &= myWhereExp.equivalent(eAsQuantExp.myWhereExp);
            retval &= myBodyExp.equivalent(eAsQuantExp.myBodyExp);
        }

        return retval;
    }

    /**
     * <p>This method returns a deep copy of the body expression.</p>
     *
     * @return The {@link Exp} representation object.
     */
    public Exp getBody() {
        return myBodyExp.clone();
    }

    /**
     * <p>This method returns this variable expression's quantification.</p>
     *
     * @return The {@link SymbolTableEntry.Quantification} object.
     */
    public SymbolTableEntry.Quantification getQuantification() {
        return myQuantification;
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
        list.add(myWhereExp.clone());
        list.add(myBodyExp.clone());

        return list;
    }

    /**
     * <p>This method returns a deep copy of all the
     * variable expressions in this quantified expression.</p>
     *
     * @return A set containing all the {@link MathVarDec}s.
     */
    public List<MathVarDec> getVars() {
        return copyVars();
    }

    /**
     * <p>This method returns a deep copy of the where expression.</p>
     *
     * @return The {@link Exp} representation object.
     */
    public Exp getWhere() {
        return myWhereExp.clone();
    }

    /**
     * <p>This method applies VC Generator's remember rule.
     * For all inherited programming expression classes, this method
     * should throw an exception.</p>
     *
     * @return The resulting {@link QuantExp} from applying the remember rule.
     */
    @Override
    public Exp remember() {
        Exp newWhere = ((MathExp) myWhereExp).remember();
        Exp newBody = ((MathExp) myBodyExp).remember();

        return new QuantExp(new Location(myLoc), myQuantification, copyVars(),
                newWhere, newBody);
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
                where = e;
                break;
            case 1:
                body = e;
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
        }

        if (myVars != null) {
            Iterator<MathVarDec> i = myVars.iterator();
            while (i.hasNext()) {
                MathVarDec m = i.next();
                sb.append(m.toString());

                if (i.hasNext()) {
                    sb.append(", ");
                }
            }
        }

        if (myWhereExp != null) {
            sb.append(myWhereExp.toString());
            sb.append(", ");
        }

        sb.append(" such that ");
        sb.append(myBodyExp.toString());

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
        Exp newWhere = null;
        if (myWhereExp != null) {
            newWhere = myWhereExp.clone();
        }

        return new QuantExp(new Location(myLoc), myQuantification, myVars,
                newWhere, myBodyExp.clone());
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
        return new QuantExp(new Location(myLoc), myQuantification, copyVars(),
                substitute(myWhereExp, substitutions), substitute(myBodyExp,
                        substitutions));
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>This is a helper method that makes a copy of the
     * list containing all the variables in the expression.</p>
     *
     * @return A list containing {@link MathVarDec}s.
     */
    private List<MathVarDec> copyVars() {
        List<MathVarDec> copyVars = new ArrayList<>();
        for (MathVarDec v : myVars) {
            copyVars.add(v.clone());
        }

        return copyVars;
    }
}
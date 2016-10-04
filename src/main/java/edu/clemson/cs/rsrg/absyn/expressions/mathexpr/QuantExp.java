/**
 * QuantExp.java
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
import edu.clemson.cs.rsrg.typeandpopulate.entry.SymbolTableEntry;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * <p>This is the class for all the mathematical quantified expression objects
 * that the compiler builds using the ANTLR4 AST nodes.</p>
 *
 * @version 2.0
 */
public class QuantExp extends MathExp {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The object's quantification (if any).</p> */
    private final SymbolTableEntry.Quantification myQuantification;

    /** <p>The mathematical variables in this quantified expression.</p> */
    private final List<MathVarDec> myVars;

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
     * {@inheritDoc}
     */
    @Override
    public final String asString(int indentSize, int innerIndentInc) {
        StringBuffer sb = new StringBuffer();

        printSpace(indentSize, sb);
        if (myQuantification != SymbolTableEntry.Quantification.NONE) {
            String quantificationAsString;
            if (myQuantification == SymbolTableEntry.Quantification.UNIVERSAL) {
                quantificationAsString = "For all ";
            }
            else if (myQuantification == SymbolTableEntry.Quantification.EXISTENTIAL) {
                quantificationAsString = "There exist ";
            }
            else {
                quantificationAsString = "There exist unique ";
            }
            sb.append(quantificationAsString);
        }

        Iterator<MathVarDec> i = myVars.iterator();
        while (i.hasNext()) {
            MathVarDec m = i.next();
            sb.append(m.getName().asString(0, innerIndentInc));

            if (i.hasNext()) {
                sb.append(", ");
            }
        }

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

        QuantExp quantExp = (QuantExp) o;

        if (myQuantification != quantExp.myQuantification)
            return false;
        if (!myVars.equals(quantExp.myVars))
            return false;
        if (myWhereExp != null ? !myWhereExp.equals(quantExp.myWhereExp)
                : quantExp.myWhereExp != null)
            return false;
        return myBodyExp.equals(quantExp.myBodyExp);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean equivalent(Exp e) {
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
     * <p>This method returns the body expression.</p>
     *
     * @return The {@link Exp} representation object.
     */
    public final Exp getBody() {
        return myBodyExp;
    }

    /**
     * <p>This method returns this variable expression's quantification.</p>
     *
     * @return The {@link SymbolTableEntry.Quantification} object.
     */
    public final SymbolTableEntry.Quantification getQuantification() {
        return myQuantification;
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
     * <p>This method returns all the variable expressions
     * in this quantified expression.</p>
     *
     * @return A list containing all the {@link MathVarDec}s.
     */
    public final List<MathVarDec> getVars() {
        return myVars;
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
        result = 31 * result + myQuantification.hashCode();
        result = 31 * result + myVars.hashCode();
        result = 31 * result + (myWhereExp != null ? myWhereExp.hashCode() : 0);
        result = 31 * result + myBodyExp.hashCode();
        return result;
    }

    /**
     * <p>This method applies VC Generator's remember rule.
     * For all inherited programming expression classes, this method
     * should throw an exception.</p>
     *
     * @return The resulting {@link QuantExp} from applying the remember rule.
     */
    @Override
    public final Exp remember() {
        Exp newWhere = ((MathExp) myWhereExp).remember();
        Exp newBody = ((MathExp) myBodyExp).remember();

        return new QuantExp(cloneLocation(), myQuantification, copyVars(),
                newWhere, newBody);
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

        return new QuantExp(cloneLocation(), myQuantification, myVars,
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

        return new QuantExp(cloneLocation(), myQuantification, copyVars(),
                newWhere, substitute(myBodyExp, substitutions));
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
            copyVars.add((MathVarDec) v.clone());
        }

        return copyVars;
    }
}
/*
 * ProgramDotExp.java
 * ---------------------------------
 * Copyright (c) 2020
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.absyn;

import edu.clemson.cs.r2jt.collections.List;
import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.collections.Iterator;

public class ProgramDotExp extends ProgramExp {

    // ===========================================================
    // Variables
    // ===========================================================

    /**
     * <p>
     * The location member.
     * </p>
     */
    private Location myLocation;

    /**
     * <p>
     * The facility qualifier member.
     * </p>
     */
    private PosSymbol myQualifier;

    /**
     * <p>
     * The program expression member.
     * </p>
     */
    private ProgramExp myExp;

    /**
     * <p>
     * The semanticExp member.
     * </p>
     */
    private ProgramExp mySemanticExp;

    // ===========================================================
    // Constructors
    // ===========================================================

    public ProgramDotExp(Location location, PosSymbol qualifier, ProgramExp exp,
            ProgramExp semanticExp) {
        myLocation = location;
        myQualifier = qualifier;
        myExp = exp;
        mySemanticExp = semanticExp;
    }

    /**
     * <p>
     * This is a method that is called to create a new ProgramDotExp that is
     * substituted.
     * </p>
     *
     * @param substitutions A mapping from <code>Exp</code>s that should be
     *        substituted out to the
     *        <code>Exp</code> that should replace them.
     *
     * @return A new ProgramDotExp.
     */
    public Exp substituteChildren(java.util.Map<Exp, Exp> substitutions) {
        return new ProgramDotExp(myLocation, myQualifier, myExp, mySemanticExp);
    }

    // ===========================================================
    // Accessor Methods
    // ===========================================================

    // -----------------------------------------------------------
    // Get Methods
    // -----------------------------------------------------------

    /**
     * <p>
     * Returns the value of the location variable.
     * </p>
     *
     * @return The location of the <code>ProgramDotExp</code>.
     */
    public Location getLocation() {
        return myLocation;
    }

    /**
     * <p>
     * Returns the name of the qualifying facility.
     * </p>
     *
     * @return The <code>PosSymbol</code> form of the qualifier.
     */
    public PosSymbol getQualifier() {
        return myQualifier;
    }

    /**
     * <p>
     * Returns the expression that is being dotted.
     * </p>
     *
     * @return The <code>Exp</code> form of the expression.
     */
    public ProgramExp getExp() {
        return myExp;
    }

    /**
     * <p>
     * Returns the value of the semanticExp variable.
     * </p>
     *
     * @return The semantic expression.
     */
    public ProgramExp getSemanticExp() {
        return mySemanticExp;
    }

    // -----------------------------------------------------------
    // Set Methods
    // -----------------------------------------------------------

    /**
     * <p>
     * Sets the location variable to the specified value.
     * </p>
     *
     * @param location New location.
     */
    public void setLocation(Location location) {
        myLocation = location;
    }

    /**
     * <p>
     * Sets the qualifier variable to the specified value.
     * </p>
     *
     * @param qualifier New qualifier symbol.
     */
    public void setQualifier(PosSymbol qualifier) {
        myQualifier = qualifier;
    }

    /**
     * <p>
     * Sets the expression variable to the specified value.
     * </p>
     *
     * @param exp New expression.
     */
    public void setExp(ProgramExp exp) {
        myExp = exp;
    }

    /**
     * <p>
     * Sets the semanticExp variable to the specified value.
     * </p>
     *
     * @param semanticExp New semantic expression.
     */
    public void setSemanticExp(ProgramExp semanticExp) {
        mySemanticExp = semanticExp;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * Accepts a ResolveConceptualVisitor.
     * </p>
     *
     * @param v A visitor object.
     */
    @Override
    public void accept(ResolveConceptualVisitor v) {
        v.visitProgramDotExp(this);
    }

    /**
     * <p>
     * Returns a formatted text string of this class.
     * </p>
     *
     * @param indent The value to be indented.
     * @param increment The increment value.
     *
     * @return String form of the <code>Exp</code>.
     */
    @Override
    public String asString(int indent, int increment) {
        StringBuffer sb = new StringBuffer();
        printSpace(indent, sb);
        sb.append("ProgramDotExp\n");

        // Facility qualifier
        if (myQualifier != null) {
            sb.append(myQualifier.asString(indent + increment, increment));
        }

        // Expression
        if (myExp != null) {
            sb.append(myExp + "\n");
        }

        // Semantic value
        if (mySemanticExp != null) {
            sb.append(mySemanticExp.asString(indent + increment, increment));
        }

        return sb.toString();
    }

    /**
     * <p>
     * Returns true if the variable is found in any sub expression of this one.
     * </p>
     *
     * @param varName Name of the variable to be checked.
     * @param IsOldExp Check to see if this is a "#" expression.
     *
     * @return True if it contains the variable as a sub expression, false
     *         otherwise.
     */
    @Override
    public boolean containsVar(String varName, boolean IsOldExp) {
        if (myExp != null) {
            if (myExp.containsVar(varName, IsOldExp)) {
                return true;
            }
        }

        if (mySemanticExp != null) {
            if (mySemanticExp.containsVar(varName, IsOldExp)) {
                return true;
            }
        }

        return false;
    }

    /**
     * <p>
     * Creates a deep copy of the <code>ProgramDotExp</code>.
     * </p>
     *
     * @return A new program expression.
     */
    @Override
    protected ProgramDotExp copy() {
        ProgramDotExp c = new ProgramDotExp(myLocation, myQualifier, myExp,
                mySemanticExp);
        c.setMathType(this.myMathType);
        c.setMathTypeValue(this.myMathTypeValue);
        return c;
    }

    /**
     * <p>
     * This doesn't do anything since we don't have any sub expressions. This is
     * here because it is an
     * abstract method.
     * </p>
     *
     * @return An empty list.
     */
    @Override
    public List<Exp> getSubExpressions() {
        return new List<Exp>();
    }

    /**
     * <p>
     * This replaces the old expression with the new one.
     * </p>
     *
     * @param old The old expression.
     * @param replacement The replacing expression.
     *
     * @return The modified expression.
     */
    public Exp replace(Exp old, Exp replacement) {
        // Do nothing if no change is required.
        if (old instanceof ProgramDotExp) {
            if (old.equals(this)) {
                return replacement;
            }
        }

        // Check for VarExp or OldExp.
        if ((old instanceof VarExp || old instanceof OldExp)) {
            if (myExp.equivalent(old)) {
                Exp.replace(this, old, replacement);
            }
        }

        return this;
    }

    /**
     * <p>
     * This doesn't do anything since we don't have any sub expressions. This is
     * here because it is an
     * abstract method.
     * </p>
     *
     * @param index Index location where we need to set the expression.
     * @param e Expression to be set.
     */
    @Override
    public void setSubExpression(int index, Exp e) {}

    /**
     * <p>
     * Returns a formatted text string of this class.
     * </p>
     *
     * @param indent The value to be indented.
     *
     * @return A string form of the <code>Exp</code>.
     */
    @Override
    public String toString(int indent) {
        StringBuffer sb = new StringBuffer();
        printSpace(indent, sb);

        sb.append(myQualifier.toString() + "." + myExp.toString());

        return sb.toString();
    }
}

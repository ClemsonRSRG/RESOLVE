/*
 * IsInExp.java
 * ---------------------------------
 * Copyright (c) 2017
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
import edu.clemson.cs.r2jt.data.Symbol;
import edu.clemson.cs.r2jt.errors.ErrorHandler;

public class IsInExp extends AbstractFunctionExp {

    // ===========================================================
    // Constants
    // ===========================================================

    public static final int IS_IN = 1;
    public static final int IS_NOT_IN = 2;

    // ===========================================================
    // Variables
    // ===========================================================

    /** The location member. */
    private Location location;

    /** The left member. */
    private Exp left;

    /** The operator member. */
    private int operator;

    /** The right member. */
    private Exp right;

    // ===========================================================
    // Constructors
    // ===========================================================

    public IsInExp() {};

    public IsInExp(Location location, Exp left, int operator, Exp right) {
        this.location = location;
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    // ===========================================================
    // Accessor Methods
    // ===========================================================

    // -----------------------------------------------------------
    // Get Methods
    // -----------------------------------------------------------

    @Override
    public int getQuantification() {
        return VarExp.NONE;
    }

    /** Returns the value of the location variable. */
    @Override
    public Location getLocation() {
        return location;
    }

    /** Returns the value of the left variable. */
    public Exp getLeft() {
        return left;
    }

    /** Returns the value of the operator variable. */
    public int getOperator() {
        return operator;
    }

    /** Returns the value of the right variable. */
    public Exp getRight() {
        return right;
    }

    // -----------------------------------------------------------
    // Set Methods
    // -----------------------------------------------------------

    /** Sets the location variable to the specified value. */
    public void setLocation(Location location) {
        this.location = location;
    }

    /** Sets the left variable to the specified value. */
    public void setLeft(Exp left) {
        this.left = left;
    }

    /** Sets the operator variable to the specified value. */
    public void setOperator(int operator) {
        this.operator = operator;
    }

    /** Sets the right variable to the specified value. */
    public void setRight(Exp right) {
        this.right = right;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    @Override
    public String getOperatorAsString() {
        String retval;

        switch (operator) {
        case IS_IN:
            retval = "is_in";
            break;
        case IS_NOT_IN:
            retval = "is_not_in";
            break;
        default:
            throw new RuntimeException("Invalid operator code.");
        }

        return retval;
    }

    @Override
    public PosSymbol getOperatorAsPosSymbol() {
        return new PosSymbol(location, Symbol.symbol(getOperatorAsString()));
    }

    public Exp substituteChildren(java.util.Map<Exp, Exp> substitutions) {
        Exp retval =
                new EqualsExp(location, substitute(left, substitutions),
                        operator, substitute(right, substitutions));
        return retval;
    }

    /** Accepts a ResolveConceptualVisitor. */
    public void accept(ResolveConceptualVisitor v) {
        v.visitIsInExp(this);
    }

    /** Returns a formatted text string of this class. */
    public String asString(int indent, int increment) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);
        sb.append("EqualsExp\n");

        if (left != null) {
            sb.append(left.asString(indent + increment, increment));
        }

        printSpace(indent + increment, sb);
        sb.append(printConstant(operator) + "\n");

        if (right != null) {
            sb.append(right.asString(indent + increment, increment));
        }

        return sb.toString();
    }

    /** Returns a formatted text string of this class. */
    public String toString(int indent) {
        //Environment   env	= Environment.getInstance();
        //if(env.isabelle()){return toIsabelleString(indent);};    	

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);

        if (left != null) {
            sb.append(left.toString(0));
        }

        if (operator == 1)
            sb.append(" is_in ");
        else {
            sb.append(" is_not_in ");
        }

        if (right != null) {
            sb.append(right.toString(0));
        }

        return sb.toString();
    }

    /** Returns a formatted text string of this class. */
    public String toIsabelleString(int indent) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);

        if (left != null) {
            sb.append(left.toString(0));
        }

        if (operator == 1)
            sb.append(" = ");
        else
            sb.append(" ~= ");

        if (right != null) {
            sb.append(right.toString(0));
        }

        return sb.toString();
    }

    public Exp replace(Exp old, Exp replacement) {
        if (!(old instanceof EqualsExp)) {
            IsInExp newExp = new IsInExp();
            newExp.setLeft((Exp) Exp.clone(left));
            newExp.setRight((Exp) Exp.clone(right));
            newExp.setOperator(this.operator);
            newExp.setLocation(this.location);
            Exp lft = Exp.replace(left, old, replacement);
            Exp rgt = Exp.replace(right, old, replacement);
            if (lft != null)
                newExp.setLeft(lft);
            if (rgt != null)
                newExp.setRight(rgt);
            return newExp;
        }
        else {}
        //
        return this;
    }

    /** Returns true if the variable is found in any sub expression   
        of this one. **/
    public boolean containsVar(String varName, boolean IsOldExp) {
        Boolean found = false;
        if (left != null) {
            found = left.containsVar(varName, IsOldExp);
        }
        if (!found && right != null) {
            found = right.containsVar(varName, IsOldExp);
        }
        return found;
    }

    private String printConstant(int k) {
        StringBuffer sb = new StringBuffer();
        switch (k) {
        case 1:
            sb.append("IS_IN");
            break;
        case 2:
            sb.append("IS_NOT_IN");
            break;
        default:
            sb.append(k);
        }
        return sb.toString();
    }

    public Object clone() {
        IsInExp clone = new IsInExp();
        clone.setLeft((Exp) Exp.clone(this.getLeft()));
        clone.setRight((Exp) Exp.clone(this.getRight()));
        if (this.location != null)
            clone.setLocation((Location) this.getLocation().clone());
        clone.setOperator(this.getOperator());
        return clone;
    }

    public Exp remember() {
        if (left != null)
            left = left.remember();
        if (left != null)
            right = right.remember();

        return this;
    }

    public List<Exp> getSubExpressions() {
        List<Exp> list = new List<Exp>();
        list.add(left);
        list.add(right);
        return list;
    }

    public void setSubExpression(int index, Exp e) {
        switch (index) {
        case 0:
            left = e;
            break;
        case 1:
            right = e;
            break;
        }
    }

    @Override
    public PosSymbol getQualifier() {
        return null;
    }
}
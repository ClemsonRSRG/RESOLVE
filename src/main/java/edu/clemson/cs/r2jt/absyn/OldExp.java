/**
 * OldExp.java
 * ---------------------------------
 * Copyright (c) 2014
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
import edu.clemson.cs.r2jt.typeandpopulate.MTType;

public class OldExp extends Exp {

    // ===========================================================
    // Variables
    // ===========================================================

    /** The location member. */
    private Location location;

    /** The exp member. */
    private Exp exp;

    // ===========================================================
    // Constructors
    // ===========================================================

    public OldExp() {};

    public OldExp(Location location, Exp exp) {
        this.location = location;
        this.exp = exp;

        if (exp.getMathType() != null) {
            setMathType(exp.getMathType());
        }

        if (exp.getMathTypeValue() != null) {
            setMathTypeValue(exp.getMathTypeValue());
        }
    }

    @Override
    public void setMathType(MTType t) {
        super.setMathType(t);
        exp.setMathType(t);
    }

    @Override
    public void setMathTypeValue(MTType t) {
        super.setMathTypeValue(t);
        exp.setMathTypeValue(t);
    }

    public Exp substituteChildren(java.util.Map<Exp, Exp> substitutions) {
        return new OldExp(location, substitute(exp, substitutions));
    }

    // ===========================================================
    // Accessor Methods
    // ===========================================================

    // -----------------------------------------------------------
    // Get Methods
    // -----------------------------------------------------------

    /** Returns the value of the location variable. */
    public Location getLocation() {
        return location;
    }

    /** Returns the value of the exp variable. */
    public Exp getExp() {
        return exp;
    }

    // -----------------------------------------------------------
    // Set Methods
    // -----------------------------------------------------------

    /** Sets the location variable to the specified value. */
    public void setLocation(Location location) {
        this.location = location;
    }

    /** Sets the exp variable to the specified value. */
    public void setExp(Exp exp) {
        this.exp = exp;
        setType(exp.getType());
        setMathType(exp.getMathType());
        setMathTypeValue(exp.getMathTypeValue());
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /** Accepts a ResolveConceptualVisitor. */
    public void accept(ResolveConceptualVisitor v) {
        v.visitOldExp(this);
    }

    /** Returns a formatted text string of this class. */
    public String asString(int indent, int increment) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);
        sb.append("OldExp\n");

        if (exp != null) {
            sb.append(exp.asString(indent + increment, increment));
        }

        return sb.toString();
    }

    /** Returns a formatted text string of this class. */
    public String toString(int indent) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);

        if (exp != null) {
            sb.append("#" + exp.toString(0));
        }

        return sb.toString();
    }

    /** Returns true if the variable is found in any sub expression   
        of this one. **/
    public boolean containsVar(String varName, boolean IsOldExp) {
        if (exp != null) {
            if (IsOldExp) {
                return exp.containsVar(varName, false);
            }
            else {
                return false;
            }
        }
        return false;
    }

    public Object clone() {
        OldExp clone = new OldExp();
        clone.setExp((Exp) Exp.clone(this.getExp()));
        clone.setLocation(this.getLocation());
        clone.setType(getType());
        return clone;
    }

    public List<Exp> getSubExpressions() {
        List<Exp> list = new List<Exp>();
        list.add(exp);
        return list;
    }

    public void setSubExpression(int index, Exp e) {
        exp = e;
    }

    public boolean shallowCompare(Exp e2) {
        if (!(e2 instanceof OldExp)) {
            return false;
        }
        return true;
    }

    public Exp replace(Exp old, Exp replacement) {
        if (old instanceof OldExp) {
            if (replacement instanceof OldExp) {
                Exp tmp =
                        Exp.replace(exp, ((OldExp) old).getExp(),
                                ((OldExp) replacement).getExp());
                if (tmp != null) {
                    exp = tmp;
                    return this;
                }
            }
            else {
                Exp tmp =
                        Exp.replace(exp, ((OldExp) old).getExp(), replacement);
                if (tmp != null)
                    return tmp;
            }
        }
        else {
            if (exp instanceof FunctionExp) {
                if (old instanceof VarExp
                        && !((FunctionExp) exp).getName().equals(
                                ((VarExp) old).getName().toString())) {
                    if (!(replacement instanceof VarExp && (((VarExp) replacement)
                            .getName().getName().startsWith("?") || ((VarExp) replacement)
                            .getName().getName().startsWith("_")))) {
                        exp = Exp.replace(exp, old, replacement);
                    }
                    else {
                        List<FunctionArgList> paramList =
                                ((FunctionExp) exp)
                                        .replaceVariableInParamListWithExp(
                                                ((FunctionExp) exp)
                                                        .getParamList(), old,
                                                replacement);
                        ((FunctionExp) exp).setParamList(paramList);
                    }
                }
                return this;
            }
        }
        return this;
    }

    public Exp remember() {
        return (exp).remember();
    }

    public void prettyPrint() {
        System.out.print("#");
        exp.prettyPrint();
    }

    public Exp copy() {
        Exp newExp = Exp.copy(exp);
        newExp = new OldExp(getLocation(), newExp);
        newExp.setType(getType());
        return newExp;
    }
}

/*
 * OutfixExp.java
 * ---------------------------------
 * Copyright (c) 2021
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

public class OutfixExp extends AbstractFunctionExp {

    // ===========================================================
    // Constants
    // ===========================================================

    public static final int ANGLE = 1;
    public static final int DBL_ANGLE = 2;
    public static final int SQUARE = 3;
    public static final int DBL_SQUARE = 4;
    public static final int BAR = 5;
    public static final int DBL_BAR = 6;

    private static final String[] myLeftDelimiters =
            { "", "<", "<<", "[", "[[", "|", "||" };
    private static final String[] myRightDelimiters =
            { "", ">", ">>", "]", "]]", "|", "||" };

    // ===========================================================
    // Variables
    // ===========================================================

    /** The location member. */
    private Location location;

    /** The operator member. */
    private int operator;

    /** The argument member. */
    private Exp argument;

    // ===========================================================
    // Constructors
    // ===========================================================

    public OutfixExp() {};

    public OutfixExp(Location location, int operator, Exp argument) {
        this.location = location;
        this.operator = operator;
        this.argument = argument;
    }

    public boolean equivalent(Exp e) {
        boolean retval = e instanceof OutfixExp;

        if (retval) {
            OutfixExp eAsOutfix = (OutfixExp) e;
            retval = (operator == eAsOutfix.operator)
                    && equivalent(argument, eAsOutfix.argument);
        }

        return retval;
    }

    public Exp substituteChildren(java.util.Map<Exp, Exp> substitutions) {
        Exp retval = new OutfixExp(location, operator,
                substitute(argument, substitutions));
        return retval;
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

    /** Returns the value of the operator variable. */
    public int getOperator() {
        return operator;
    }

    /** Returns the value of the argument variable. */
    public Exp getArgument() {
        return argument;
    }

    // -----------------------------------------------------------
    // Set Methods
    // -----------------------------------------------------------

    /** Sets the location variable to the specified value. */
    public void setLocation(Location location) {
        this.location = location;
    }

    /** Sets the operator variable to the specified value. */
    public void setOperator(int operator) {
        this.operator = operator;
    }

    /** Sets the argument variable to the specified value. */
    public void setArgument(Exp argument) {
        this.argument = argument;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /** Accepts a ResolveConceptualVisitor. */
    public void accept(ResolveConceptualVisitor v) {
        v.visitOutfixExp(this);
    }

    /** Returns a formatted text string of this class. */
    public String asString(int indent, int increment) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);
        sb.append("OutfixExp\n");

        printSpace(indent + increment, sb);
        sb.append(printConstant(operator) + "\n");

        if (argument != null) {
            sb.append(argument.asString(indent + increment, increment));
        }

        return sb.toString();
    }

    /** Returns a formatted text string of this class. */
    public String toString(int indent) {

        StringBuffer sb = new StringBuffer();

        switch (operator) {
        case 1:
            sb.append("<");
            sb.append(argument.toString(indent));
            sb.append(">");
            break;
        case 2:
            sb.append("DBL_ANGLE");
            break;
        case 3:
            sb.append("SQUARE");
            break;
        case 4:
            sb.append("DBL_SQUARE");
            break;
        case 5:
            sb.append("|");
            sb.append(argument.toString(0));
            sb.append("|");
            break;
        case 6:
            sb.append("DBL_BAR");
            break;
        default:
            sb.append(operator);
        }

        return sb.toString();
    }

    /** Returns a formatted text string of this class. */
    public String toIsabelleString(int indent) {

        StringBuffer sb = new StringBuffer();

        switch (operator) {
        case 1:
            sb.append("<");
            sb.append(argument.toString(indent));
            sb.append(">");
            break;
        case 2:
            sb.append("DBL_ANGLE");
            break;
        case 3:
            sb.append("SQUARE");
            break;
        case 4:
            sb.append("DBL_SQUARE");
            break;
        case 5:
            sb.append("length(");
            sb.append(argument.toString(0));
            sb.append(") ");
            break;
        case 6:
            sb.append("DBL_BAR");
            break;
        default:
            sb.append(operator);
        }

        return sb.toString();
    }

    /**
     * Returns true if the variable is found in any sub expression of this one.
     **/
    public boolean containsVar(String varName, boolean IsOldExp) {
        if (argument != null) {
            return argument.containsVar(varName, IsOldExp);
        }
        return false;
    }

    private String printConstant(int k) {
        StringBuffer sb = new StringBuffer();
        switch (k) {
        case 1:
            sb.append("ANGLE");
            break;
        case 2:
            sb.append("DBL_ANGLE");
            break;
        case 3:
            sb.append("SQUARE");
            break;
        case 4:
            sb.append("DBL_SQUARE");
            break;
        case 5:
            sb.append("BAR");
            break;
        case 6:
            sb.append("DBL_BAR");
            break;
        default:
            sb.append(k);
        }
        return sb.toString();
    }

    public Object clone() {
        OutfixExp clone = new OutfixExp();
        clone.setOperator(this.operator);
        clone.setLocation(this.getLocation());
        clone.setArgument((Exp) Exp.clone(this.getArgument()));
        return clone;
    }

    public List<Exp> getSubExpressions() {
        List<Exp> list = new List<Exp>();
        list.add(argument);
        return list;
    }

    public void setSubExpression(int index, Exp e) {
        argument = e;
    }

    public boolean shallowCompare(Exp e2) {
        if (!(e2 instanceof OutfixExp)) {
            return false;
        }
        if (operator != ((OutfixExp) e2).getOperator()) {
            return false;
        }
        return true;
    }

    protected Exp replace(Exp old, Exp replacement) {
        if (!(old instanceof OutfixExp)) {
            Exp tmp = (Exp.replace(argument, old, replacement));
            if (tmp != null)
                argument = tmp;
            return this;
        }
        else
            return this;
    }

    public Exp remember() {
        if (argument instanceof OldExp)
            this.setArgument(((OldExp) (argument)).getExp());
        else {
            argument = argument.remember();
            ;
        }
        return this;
    }

    public String getLeftDelimiter() {
        return myLeftDelimiters[operator];
    }

    public String getRightDelimiter() {
        return myRightDelimiters[operator];
    }

    @Override
    public String getOperatorAsString() {
        String retval;

        switch (operator) {
        case ANGLE:
            retval = "<_>";
            break;
        case DBL_ANGLE:
            retval = "<<_>>";
            break;
        case SQUARE:
            retval = "[_]";
            break;
        case DBL_SQUARE:
            retval = "[[_]]";
            break;
        case BAR:
            retval = "|_|";
            break;
        case DBL_BAR:
            retval = "||_||";
            break;
        default:
            throw new RuntimeException("Invalid operator code");
        }

        return retval;
    }

    @Override
    public PosSymbol getOperatorAsPosSymbol() {
        return new PosSymbol(location, Symbol.symbol(getOperatorAsString()));
    }

    public void prettyPrint() {
        if (operator == ANGLE) {
            System.out.print("<");
            argument.prettyPrint();
            System.out.print(">");
        }
        else if (operator == DBL_ANGLE) {
            System.out.print("<<");
            argument.prettyPrint();
            System.out.print(">>");
        }
        else if (operator == SQUARE) {
            System.out.print("[");
            argument.prettyPrint();
            System.out.print("]");
        }
        else if (operator == DBL_SQUARE) {
            System.out.print("[[");
            argument.prettyPrint();
            System.out.print("]]");
        }
        else if (operator == BAR) {
            System.out.print("|");
            argument.prettyPrint();
            System.out.print("|");
        }
        else {
            System.out.print("||");
            argument.prettyPrint();
            System.out.print("||");
        }
    }

    public Exp copy() {
        Exp retval;
        int newOperator = operator;
        Exp newArgument = Exp.copy(argument);
        retval = new OutfixExp(null, newOperator, newArgument);
        return retval;
    }

    @Override
    public PosSymbol getQualifier() {
        return null;
    }
}

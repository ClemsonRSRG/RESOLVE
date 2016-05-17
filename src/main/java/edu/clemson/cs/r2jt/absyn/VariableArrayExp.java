/**
 * VariableArrayExp.java
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
package edu.clemson.cs.r2jt.absyn;

import edu.clemson.cs.r2jt.collections.List;
import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.data.PosSymbol;

public class VariableArrayExp extends VariableExp {

    // ===========================================================
    // Variables
    // ===========================================================

    /** The location member. */
    private Location location;

    /** The qualifier member. */
    private PosSymbol qualifier;

    /** The name member. */
    private PosSymbol name;

    /** The argument member. */
    private ProgramExp argument;

    // ===========================================================
    // Constructors
    // ===========================================================

    public VariableArrayExp() {};

    public VariableArrayExp(Location location, PosSymbol qualifier,
            PosSymbol name, ProgramExp argument) {
        this.location = location;
        this.qualifier = qualifier;
        this.name = name;
        this.argument = argument;
    }

    public Exp substituteChildren(java.util.Map<Exp, Exp> substitutions) {
        return new VariableArrayExp(location, qualifier, name,
                (ProgramExp) substitute(argument, substitutions));
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

    /** Returns the value of the qualifier variable. */
    public PosSymbol getQualifier() {
        return qualifier;
    }

    /** Returns the value of the name variable. */
    public PosSymbol getName() {
        return name;
    }

    /** Returns the value of the argument variable. */
    public ProgramExp getArgument() {
        return argument;
    }

    // -----------------------------------------------------------
    // Set Methods
    // -----------------------------------------------------------

    /** Sets the location variable to the specified value. */
    public void setLocation(Location location) {
        this.location = location;
    }

    /** Sets the qualifier variable to the specified value. */
    public void setQualifier(PosSymbol qualifier) {
        this.qualifier = qualifier;
    }

    /** Sets the name variable to the specified value. */
    public void setName(PosSymbol name) {
        this.name = name;
    }

    /** Sets the argument variable to the specified value. */
    public void setArgument(ProgramExp argument) {
        this.argument = argument;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /** Accepts a ResolveConceptualVisitor. */
    public void accept(ResolveConceptualVisitor v) {
        v.visitVariableArrayExp(this);
    }

    /** Returns a formatted text string of this class. */
    public String asString(int indent, int increment) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);
        sb.append("VariableArrayExp\n");

        if (qualifier != null) {
            sb.append(qualifier.asString(indent + increment, increment));
        }

        if (name != null) {
            sb.append(name.asString(indent + increment, increment));
        }

        if (argument != null) {
            sb.append(argument.asString(indent + increment, increment));
        }

        return sb.toString();
    }

    /** Returns a formatted text string of this class. */
    public String toString(int indent) {
        //Environment   env	= Environment.getInstance();
        //if(env.isabelle()){return toIsabelleString(indent);};

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);

        if (qualifier != null) {
            sb.append(qualifier.toString());
        }

        if (name != null) {
            sb.append(name.toString());
        }

        if (argument != null) {
            sb.append("[" + argument.toString() + "]");
        }

        return sb.toString();
    }

    public String toIsabelleString(int indent) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);

        if (qualifier != null) {
            sb.append(qualifier.toString());
        }

        if (name != null) {
            sb.append(name.toString());
        }

        if (argument != null) {
            sb.append("(" + argument.toString() + ")");
        }

        return sb.toString();
    }

    /** Returns true if the variable is found in any sub expression
        of this one. **/
    public boolean containsVar(String varName, boolean IsOldExp) {
        if (argument != null) {
            return argument.containsVar(varName, IsOldExp);
        }
        return false;
    }

    public List<Exp> getSubExpressions() {
        List<Exp> list = new List<Exp>();
        list.add((Exp) argument);
        return list;
    }

    public void setSubExpression(int index, Exp e) {
        argument = (ProgramExp) e;
    }

    public Exp copy() {
        VariableArrayExp result =
                new VariableArrayExp(location, qualifier, name, argument);
        result.setMathType(myMathType);
        result.setMathTypeValue(myMathTypeValue);

        return result;
    }
}

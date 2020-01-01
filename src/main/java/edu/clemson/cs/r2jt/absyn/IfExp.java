/*
 * IfExp.java
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

public class IfExp extends Exp {

    // ===========================================================
    // Variables
    // ===========================================================

    /** The location member. */
    private Location location;

    /** The test member. */
    private Exp test;

    /** The thenclause member. */
    private Exp thenclause;

    /** The elseclause member. */
    private Exp elseclause;

    // ===========================================================
    // Constructors
    // ===========================================================

    public IfExp() {};

    public IfExp(Location location, Exp test, Exp thenclause, Exp elseclause) {
        this.location = location;
        this.test = test;
        this.thenclause = thenclause;
        this.elseclause = elseclause;
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

    /** Returns the value of the test variable. */
    public Exp getTest() {
        return test;
    }

    /** Returns the value of the thenclause variable. */
    public Exp getThenclause() {
        return thenclause;
    }

    /** Returns the value of the elseclause variable. */
    public Exp getElseclause() {
        return elseclause;
    }

    // -----------------------------------------------------------
    // Set Methods
    // -----------------------------------------------------------

    /** Sets the location variable to the specified value. */
    public void setLocation(Location location) {
        this.location = location;
    }

    /** Sets the test variable to the specified value. */
    public void setTest(Exp test) {
        this.test = test;
    }

    /** Sets the thenclause variable to the specified value. */
    public void setThenclause(Exp thenclause) {
        this.thenclause = thenclause;
    }

    /** Sets the elseclause variable to the specified value. */
    public void setElseclause(Exp elseclause) {
        this.elseclause = elseclause;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    public Exp substituteChildren(java.util.Map<Exp, Exp> substitutions) {
        return new IfExp(location, substitute(test, substitutions),
                substitute(thenclause, substitutions),
                substitute(elseclause, substitutions));
    }

    /** Accepts a ResolveConceptualVisitor. */
    public void accept(ResolveConceptualVisitor v) {
        v.visitIfExp(this);
    }

    /** Returns a formatted text string of this class. */
    public String asString(int indent, int increment) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);
        sb.append("IfExp\n");

        if (test != null) {
            sb.append(test.asString(indent + increment, increment));
        }

        if (thenclause != null) {
            sb.append(thenclause.asString(indent + increment, increment));
        }

        if (elseclause != null) {
            sb.append(elseclause.asString(indent + increment, increment));
        }

        return sb.toString();
    }

    public String toString(int indent) {
        // Environment env = Environment.getInstance();
        // if(env.isabelle()){return toIsabelleString(indent);};
        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);
        sb.append("If ");
        sb.append(test.toString(0));

        sb.append(" then ");

        sb.append("(" + thenclause.toString(0) + ")");

        if (elseclause != null) {
            sb.append("else ");
            sb.append("(" + elseclause.toString(0) + ")");
        }

        return sb.toString();
    }

    public String toIsabelleString(int indent) {
        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);

        sb.append(test.toString(0));

        sb.append(" --> ");

        sb.append("(" + thenclause.toString(0) + ")");

        if (elseclause != null) {
            sb.append("& not(" + test.toString(0) + ")");
            sb.append(" --> ");
            sb.append("(" + elseclause.toString(0) + ")");
        }

        return sb.toString();
    }

    /**
     * Returns true if the variable is found in any sub expression of this one.
     **/
    public boolean containsVar(String varName, boolean IsOldExp) {
        Boolean found = false;
        if (test != null) {
            found = test.containsVar(varName, IsOldExp);
        }
        if (!found && thenclause != null) {
            found = thenclause.containsVar(varName, IsOldExp);
        }
        if (!found && elseclause != null) {
            found = elseclause.containsVar(varName, IsOldExp);
        }
        return found;
    }

    public Object clone() {
        IfExp clone = new IfExp();
        if (test != null)
            clone.setTest((Exp) Exp.clone(this.getTest()));
        if (elseclause != null)
            clone.setElseclause((Exp) Exp.clone(this.getElseclause()));
        if (thenclause != null)
            clone.setThenclause((Exp) Exp.clone(this.getThenclause()));
        clone.setLocation(this.getLocation());
        return clone;
    }

    public Exp replace(Exp old, Exp replacement) {
        if (!(old instanceof IfExp)) {
            if (test != null) {
                Exp testcl = Exp.replace(test, old, replacement);
                if (testcl != null)
                    this.setTest(testcl);
            }
            if (thenclause != null) {
                Exp then = Exp.replace(thenclause, old, replacement);
                if (then != null)
                    this.setThenclause(then);
            }
            if (elseclause != null) {
                Exp elsecl = Exp.replace(elseclause, old, replacement);

                if (elsecl != null)
                    this.setElseclause(elsecl);
            }

            return this;
        }
        else
            return this;
    }

    public List<Exp> getSubExpressions() {
        List<Exp> list = new List<Exp>();
        list.add(test);
        list.add(thenclause);
        list.add(elseclause);
        return list;
    }

    public void setSubExpression(int index, Exp e) {
        switch (index) {
        case 0:
            test = e;
            break;
        case 1:
            thenclause = e;
            break;
        case 2:
            elseclause = e;
            break;
        }
    }

    public boolean shallowCompare(Exp e2) {
        if (!(e2 instanceof IfExp)) {
            return false;
        }
        return true;
    }

    public Exp remember() {
        if (test instanceof OldExp)
            this.setTest(((OldExp) (test)).getExp());
        if (elseclause instanceof OldExp)
            this.setElseclause(((OldExp) (elseclause)).getExp());
        if (thenclause instanceof OldExp)
            this.setThenclause(((OldExp) (thenclause)).getExp());

        if (test != null)
            test = test.remember();
        if (elseclause != null)
            elseclause = elseclause.remember();
        if (thenclause != null)
            thenclause = thenclause.remember();

        return this;
    }

    public void prettyPrint() {
        System.out.print("if ");
        test.prettyPrint();
        System.out.println();
        System.out.print("then ");
        thenclause.prettyPrint();
        if (elseclause != null) {
            System.out.println();
            System.out.print("else ");
            elseclause.prettyPrint();
        }
    }

    public Exp copy() {
        Exp newTest = Exp.copy(test);
        Exp newThenclause = Exp.copy(thenclause);

        Exp newElseclause = null;
        if (elseclause != null) {
            newElseclause = Exp.copy(elseclause);
        }

        Exp result = new IfExp(null, newTest, newThenclause, newElseclause);
        return result;
    }

}

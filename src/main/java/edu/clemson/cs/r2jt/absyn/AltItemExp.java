/**
 * AltItemExp.java
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

public class AltItemExp extends Exp {

    // Variables

    /** The location member. */
    private Location location;

    /** The test member. */
    private Exp test;

    /** The assignment member. */
    private Exp assignment;

    // Constructors

    public AltItemExp() {
    // Empty
    }

    public AltItemExp(Location location, Exp test, Exp assignment) {
        if (assignment == null) {
            throw new IllegalArgumentException("Cannot have null assignment.");
        }

        this.location = location;
        this.test = test;
        this.assignment = assignment;
    }

    // Accessor Methods

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

    /** Returns the value of the assignment variable. */
    public Exp getAssignment() {
        return assignment;
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

    /** Sets the assignment variable to the specified value. */
    public void setAssignment(Exp assignment) {

        if (assignment == null) {
            throw new IllegalArgumentException("Cannot have null assignment.");
        }

        this.assignment = assignment;
    }

    // Public Methods

    public boolean equivalent(Exp e) {
        boolean result = e instanceof AltItemExp;

        if (result) {
            AltItemExp eAsAltItemExp = (AltItemExp) e;

            result = eAsAltItemExp.test.equivalent(test);
            result &= eAsAltItemExp.assignment.equivalent(assignment);
        }

        return result;
    }

    public Exp substituteChildren(java.util.Map<Exp, Exp> substitutions) {
        return new AltItemExp(location, substitute(test, substitutions),
                substitute(assignment, substitutions));
    }

    /** Accepts a ResolveConceptualVisitor. */
    public void accept(ResolveConceptualVisitor v) {
        v.visitAltItemExp(this);
    }

    /** Returns a formatted text string of this class. */
    public String asString(int indent, int increment) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);
        sb.append("AltItemExp\n");

        if (test != null) {
            sb.append(test.asString(indent + increment, increment));
        }

        if (assignment != null) {
            sb.append(assignment.asString(indent + increment, increment));
        }

        return sb.toString();
    }

    /** Returns true if the variable is found in any sub expression   
        of this one. **/
    public boolean containsVar(String varName, boolean IsOldExp) {
        Boolean found = false;
        if (test != null) {
            found = test.containsVar(varName, IsOldExp);
        }
        if (!found && assignment != null) {
            found = assignment.containsVar(varName, IsOldExp);
        }
        return found;
    }

    public List<Exp> getSubExpressions() {
        List<Exp> list = new List<Exp>();
        list.add(test);
        list.add(assignment);
        return list;
    }

    @Override
    public void setSubExpression(int index, Exp e) {
        switch (index) {
        case 0:
            //edu.clemson.cs.r2jt.data.List was written by crazed monkies and
            //silently ignores adding null elements (in violation of 
            //java.util.List's contract), so if test is null, index 0 is the
            //assignment subexpression, otherwise it's the test subexpression.
            if (test == null) {
                setAssignment(e);
            }
            else {
                setTest(e);
            }
            break;
        case 1:
            setAssignment(e);
            break;
        }
    }

    public boolean shallowCompare(Exp e2) {
        if (!(e2 instanceof AltItemExp)) {
            return false;
        }
        return true;
    }

    public String toString(int index) {
        StringBuffer sb = new StringBuffer();
        sb.append(assignment.toString(0));

        if (test != null) {
            sb.append(" if ");
            sb.append(test.toString(0));
        }
        else {
            sb.append(" otherwise");
        }
        return sb.toString();
    }

    public void prettyPrint() {
        assignment.prettyPrint();
        if (test != null) {
            System.out.print(" if ");
            test.prettyPrint();
        }
        else {
            System.out.print(" otherwise");
        }
    }

    public Exp replace(Exp old, Exp replacement) {
        AltItemExp result = (AltItemExp) Exp.copy(this);

        if (test != null) {
            result.setTest(Exp.replace(test, old, replacement));
        }

        if (assignment != null) {
            if (result.assignment == null) {
                throw new IllegalArgumentException(
                        "Cannot have null assignment.");
            }

            Exp oldAssignment = assignment;
            result.assignment = Exp.replace(assignment, old, replacement);

            if (result.assignment == null) {
                result.assignment = oldAssignment;
            }
        }

        return result;
    }

    public Exp copy() {
        Exp newTest = null;
        if (test != null) {
            newTest = Exp.copy(test);
        }

        Exp newAssignment = assignment;
        if (newAssignment != null) {
            newAssignment = Exp.copy(newAssignment);
        }

        Exp result = new AltItemExp(null, newTest, newAssignment);

        return result;
    }

    public Object clone() {
        Exp newTest = test;
        if (newTest != null) {
            newTest = (Exp) Exp.clone(newTest);
        }

        Exp newAssignment = assignment;
        if (newAssignment != null) {
            newAssignment = (Exp) Exp.clone(newAssignment);
        }

        Exp result = new AltItemExp(null, newTest, newAssignment);

        return result;
    }

    public Exp remember() {

        if (test instanceof OldExp)
            this.setTest(((OldExp) (test)).getExp());

        if (test != null)
            setTest(test.remember());

        if (assignment instanceof OldExp)
            this.setAssignment(((OldExp) (assignment)).getExp());

        if (assignment != null)
            assignment = assignment.remember();

        return this;
    }

}

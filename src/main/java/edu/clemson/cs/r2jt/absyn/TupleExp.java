/**
 * TupleExp.java
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
package edu.clemson.cs.r2jt.absyn;

import edu.clemson.cs.r2jt.collections.List;
import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.data.Mode;
import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.typeandpopulate.MTCartesian;
import edu.clemson.cs.r2jt.typeandpopulate.MTCartesian.Element;
import edu.clemson.cs.r2jt.collections.Iterator;

/**
 * <p>Making TupleExp extend from AbstractFunctionExp was considered and
 * explicitly decided against during the great math-type-overhaul of 2012.
 * If we chose to admit the presence of some function that builds tuples for us,
 * how would we pass it its parameters if not via a tuple?  Thus, TupleExp is
 * a built-in notion, and not imagined as the result of the application of a
 * function.</p>
 */
public class TupleExp extends Exp {

    // ===========================================================
    // Variables
    // ===========================================================

    /** The location member. */
    private Location location;

    /** The fields member. */
    private List<Exp> fields;

    private int mySize;

    // ===========================================================
    // Constructors
    // ===========================================================

    public TupleExp() {
        fields = new List<Exp>();
    }

    public TupleExp(Location location, List<Exp> fields) {
        this(location, fields.toArray(new Exp[0]), fields.size());
    }

    public TupleExp(Location l, Exp[] fields) {
        this(l, fields, fields.length);
    }

    public TupleExp(Location l, java.util.List<Exp> fields) {
        this(l, fields.toArray(new Exp[0]), fields.size());
    }

    private TupleExp(Location l, Exp[] fields, int elementCount) {
        if (elementCount < 2) {
            //We assert this isn't possible, but who knows?
            throw new IllegalArgumentException(
                    "Unexpected cartesian product size.");
        }

        location = l;
        this.fields = new List<Exp>();

        int workingSize = 0;

        Exp first;
        if (elementCount == 2) {
            first = fields[0];
        }
        else {
            first = new TupleExp(l, fields, elementCount - 1);
        }

        if (first instanceof TupleExp) {
            workingSize += ((TupleExp) first).getSize();
        }
        else {
            workingSize += 1;
        }

        Exp second = fields[elementCount - 1];
        workingSize += 1;

        this.fields.add(first);
        this.fields.add(second);

        mySize = workingSize;
    }

    public Exp substituteChildren(java.util.Map<Exp, Exp> substitutions) {
        List<Exp> newFields = new List<Exp>();
        for (Exp f : fields) {
            newFields.add(substitute(f, substitutions));
        }

        Exp result = new TupleExp(location, newFields);
        result.setMathType(getMathType());
        result.setMathTypeValue(getMathTypeValue());

        return result;
    }

    // ===========================================================
    // Accessor Methods
    // ===========================================================

    // -----------------------------------------------------------
    // Get Methods
    // -----------------------------------------------------------

    public int getSize() {
        return mySize;
    }

    /** Returns the value of the location variable. */
    public Location getLocation() {
        return location;
    }

    public Exp getField(int index) {
        Exp result;

        if (index < 0 || index >= mySize) {
            throw new IndexOutOfBoundsException("" + index);
        }

        if (index == (mySize - 1)) {
            result = fields.get(1);
        }
        else {
            if (mySize == 2) {
                //ASSERT: !(myElements.get(0) instanceof MTCartesian)
                if (index != 0) {
                    throw new IndexOutOfBoundsException("" + index);
                }

                result = fields.get(0);
            }
            else {
                //ASSERT: myElements.get(0) instanceof MTCartesian
                result = ((TupleExp) fields.get(0)).getField(index);
            }
        }

        return result;
    }

    /** Returns the value of the fields variable. */
    public List<Exp> getFields() {
        return fields;
    }

    // -----------------------------------------------------------
    // Set Methods
    // -----------------------------------------------------------

    /** Sets the location variable to the specified value. */
    public void setLocation(Location location) {
        this.location = location;
    }

    /** Sets the fields variable to the specified value. */
    public void setFields(List<Exp> fields) {
        this.fields = fields;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    public boolean isUniversallyQuantified() {
        boolean soFar = true;

        for (Exp field : fields) {
            soFar =
                    soFar
                            && ((field instanceof VarExp && ((VarExp) field)
                                    .getQuantification() == VarExp.FORALL) || (field instanceof TupleExp && ((TupleExp) field)
                                    .isUniversallyQuantified()));
        }

        return soFar;
    }

    /*public void addField(Exp field) {
    	fields.add(field);
    }*/

    /** Accepts a ResolveConceptualVisitor. */
    public void accept(ResolveConceptualVisitor v) {
        v.visitTupleExp(this);
    }

    /** Returns a formatted text string of this class. */
    public String asString(int indent, int increment) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);
        sb.append("TupleExp\n");

        if (fields != null) {
            sb.append(fields.asString(indent + increment, increment));
        }

        return sb.toString();
    }

    /** Returns true if the variable is found in any sub expression
        of this one. **/
    public boolean containsVar(String varName, boolean IsOldExp) {
        Iterator<Exp> i = fields.iterator();
        while (i.hasNext()) {
            Exp temp = i.next();
            if (temp != null) {
                if (temp.containsVar(varName, IsOldExp)) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<Exp> getSubExpressions() {
        return fields;
    }

    public void setSubExpression(int index, Exp e) {
        fields.set(index, e);
    }

    public boolean shallowCompare(Exp e2) {
        if (!(e2 instanceof TupleExp)) {
            return false;
        }
        return true;
    }

    public void prettyPrint() {
        Iterator<Exp> it = fields.iterator();
        System.out.print("(");
        if (it.hasNext()) {
            it.next().prettyPrint();
        }
        while (it.hasNext()) {
            System.out.print(", ");
            it.next().prettyPrint();
        }
        System.out.print(")");
    }

    public Exp copy() {
        Iterator<Exp> it = fields.iterator();
        List<Exp> newFields = new List<Exp>();
        while (it.hasNext()) {
            newFields.add(Exp.copy(it.next()));
        }

        Exp result = new TupleExp(location, newFields);
        result.setMathType(getMathType());
        result.setMathTypeValue(getMathTypeValue());

        return result;
    }

    @Override
    public String toString() {
        String result = "(";

        boolean first = true;
        for (Exp member : fields) {
            if (!first) {
                result += ", ";
            }
            else {
                first = false;
            }

            result += member;
        }

        return result + ")";
    }
}

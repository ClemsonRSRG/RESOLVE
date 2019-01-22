/*
 * VariableRecordExp.java
 * ---------------------------------
 * Copyright (c) 2019
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

public class VariableRecordExp extends VariableExp {

    // ===========================================================
    // Variables
    // ===========================================================

    /** The location member. */
    private Location location;

    /** The qualifier member. */
    private PosSymbol qualifier;

    /** The name member. */
    private PosSymbol name;

    /** The fields member. */
    private List<VariableExp> fields;

    // ===========================================================
    // Constructors
    // ===========================================================

    public VariableRecordExp() {};

    public VariableRecordExp(Location location, PosSymbol qualifier,
            PosSymbol name, List<VariableExp> fields) {
        this.location = location;
        this.qualifier = qualifier;
        this.name = name;
        this.fields = fields;
    }

    public Exp substituteChildren(java.util.Map<Exp, Exp> substitutions) {
        List<VariableExp> newFields = new List<VariableExp>();
        for (VariableExp v : fields) {
            newFields.add((VariableExp) substitute(v, substitutions));
        }

        return new VariableRecordExp(location, qualifier, name, newFields);
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

    /** Returns the value of the fields variable. */
    public List<VariableExp> getFields() {
        return fields;
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

    /** Sets the fields variable to the specified value. */
    public void setFields(List<VariableExp> fields) {
        this.fields = fields;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /** Accepts a ResolveConceptualVisitor. */
    public void accept(ResolveConceptualVisitor v) {
        v.visitVariableRecordExp(this);
    }

    /** Returns a formatted text string of this class. */
    public String asString(int indent, int increment) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);
        sb.append("VariableRecordExp\n");

        if (qualifier != null) {
            sb.append(qualifier.asString(indent + increment, increment));
        }

        if (name != null) {
            sb.append(name.asString(indent + increment, increment));
        }

        if (fields != null) {
            sb.append(fields.asString(indent + increment, increment));
        }

        return sb.toString();
    }

    /** Returns a formatted text string of this class. */
    public String toString(int indent) {

        StringBuffer sb = new StringBuffer();
        /*
         printSpace(indent, sb);
         sb.append("VariableRecordExp\n");

         if (qualifier != null) {
         sb.append(qualifier.asString(indent+increment,increment));
         }

         if (name != null) {
         sb.append(name.asString(indent+increment,increment));
         }

         if (fields != null) {
         sb.append(fields.asString(indent+increment,increment));
         }
         */
        return sb.toString();
    }

    /** Returns true if the variable is found in any sub expression
        of this one. **/
    public boolean containsVar(String varName, boolean IsOldExp) {
        Iterator<VariableExp> i = fields.iterator();
        while (i.hasNext()) {
            VariableExp temp = i.next();
            if (temp != null) {
                if (temp.containsVar(varName, IsOldExp)) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<Exp> getSubExpressions() {
        List<Exp> list = new List<Exp>();
        Iterator<VariableExp> fieldsIt = fields.iterator();
        while (fieldsIt.hasNext()) {
            list.add((Exp) (fieldsIt.next()));
        }
        return list;
    }

    public void setSubExpression(int index, Exp e) {
        fields.set(index, (VariableExp) e);
    }

    public Exp copy() {
        VariableRecordExp result =
                new VariableRecordExp(location, qualifier, name, fields);

        result.setMathType(myMathType);
        result.setMathTypeValue(myMathTypeValue);

        return result;
    }
}

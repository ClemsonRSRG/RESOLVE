/*
 * DotExp.java
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

import java.util.ListIterator;

import edu.clemson.cs.r2jt.collections.List;
import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.collections.Iterator;

public class DotExp extends Exp {

    // Variables

    /** The location member. */
    private Location location;

    /** The segments member. */
    private List<Exp> segments;

    /** The semanticExp member. */
    private Exp semanticExp;

    // Constructors

    public DotExp() {};

    public DotExp(Location location, List<Exp> segments, Exp semanticExp) {
        this.location = location;
        this.segments = segments;
        this.semanticExp = semanticExp;
    }

    // Accessor Methods

    // -----------------------------------------------------------
    // Get Methods
    // -----------------------------------------------------------

    /** Returns the value of the location variable. */
    public Location getLocation() {
        return location;
    }

    /** Returns the value of the segments variable. */
    public List<Exp> getSegments() {
        return segments;
    }

    /** Returns the value of the semanticExp variable. */
    public Exp getSemanticExp() {
        return semanticExp;
    }

    // -----------------------------------------------------------
    // Set Methods
    // -----------------------------------------------------------

    /** Sets the location variable to the specified value. */
    public void setLocation(Location location) {
        this.location = location;
    }

    /** Sets the segments variable to the specified value. */
    public void setSegments(List<Exp> segments) {
        this.segments = segments;
    }

    /** Sets the semanticExp variable to the specified value. */
    public void setSemanticExp(Exp semanticExp) {
        this.semanticExp = semanticExp;
    }

    // Public Methods

    public Exp substituteChildren(java.util.Map<Exp, Exp> substitutions) {
        Exp retval;

        List<Exp> newSegments = new List<Exp>();
        for (Exp e : segments) {
            newSegments.add(substitute(e, substitutions));
        }

        retval =
                new DotExp(location, newSegments, substitute(semanticExp,
                        substitutions));
        return retval;
    }

    /** Accepts a ResolveConceptualVisitor. */
    public void accept(ResolveConceptualVisitor v) {
        v.visitDotExp(this);
    }

    /** Returns a formatted text string of this class. */
    public String asString(int indent, int increment) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);
        sb.append("DotExp\n");

        if (segments != null) {
            sb.append(segments.asString(indent + increment, increment));
        }

        return sb.toString();
    }

    /** Returns true if the variable is found in any sub expression   
        of this one. **/
    public boolean containsVar(String varName, boolean IsOldExp) {
        if (segments != null) {
            Iterator<Exp> i = segments.iterator();
            while (i.hasNext()) {
                Exp temp = i.next();
                if (temp != null) {
                    if (temp.containsVar(varName, IsOldExp)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public Object clone() {
        DotExp clone = new DotExp();
        clone.setLocation(this.getLocation());
        Iterator<Exp> i = segments.iterator();
        List<Exp> newSegments = new List<Exp>();

        Exp curSegment;
        Exp clonedSegment;
        while (i.hasNext()) {
            curSegment = i.next();
            clonedSegment = (Exp) Exp.clone(curSegment);
            newSegments.add(clonedSegment);
        }
        clone.setSegments(newSegments);
        return clone;
    }

    public List<Exp> getSubExpressions() {
        return segments;
    }

    public void setSubExpression(int index, Exp e) {
        segments.set(index, e);
    }

    public boolean shallowCompare(Exp e2) {
        if (!(e2 instanceof DotExp)) {
            return false;
        }
        return true;
    }

    public boolean equivalent(Exp e) {
        boolean retval = (e instanceof DotExp);

        if (retval) {
            DotExp eAsDotExp = (DotExp) e;
            retval = Exp.equivalent(semanticExp, eAsDotExp.semanticExp);

            if (retval) {
                Iterator<Exp> thisSegments = segments.iterator();
                Iterator<Exp> eSegments = eAsDotExp.segments.iterator();
                while (retval && thisSegments.hasNext() && eSegments.hasNext()) {

                    retval = thisSegments.next().equivalent(eSegments.next());
                }

                retval =
                        retval
                                && !(thisSegments.hasNext() || eSegments
                                        .hasNext());
            }
        }

        return retval;
    }

    public void prettyPrint() {
        Iterator<Exp> it = segments.iterator();
        if (it.hasNext()) {
            it.next().prettyPrint();
        }
        while (it.hasNext()) {
            System.out.print(".");
            it.next().prettyPrint();
        }
    }

    public Exp copy() {
        Exp retval;
        Iterator<Exp> it = segments.iterator();
        List<Exp> newSegments = new List<Exp>();
        while (it.hasNext()) {
            newSegments.add(Exp.copy(it.next()));
        }

        Exp newSemanticExp = null;

        if (semanticExp != null) {
            newSemanticExp = Exp.copy(semanticExp);
        }

        retval = new DotExp(null, newSegments, newSemanticExp);
        retval.setMathType(myMathType);
        retval.setMathTypeValue(myMathTypeValue);

        return retval;
    }

    public String toString(int indent) {
        StringBuffer sb = new StringBuffer();
        printSpace(indent, sb);

        sb.append(segmentsToString(this.segments));

        return sb.toString();
    }

    @Override
    public String toString() {
        return toString(0);
    }

    private String segmentsToString(List<Exp> segments) {
        StringBuffer sb = new StringBuffer();
        //Environment env = Environment.getInstance();
        if (segments != null) {
            Iterator<Exp> i = segments.iterator();

            while (i.hasNext()) {
                sb.append(i.next().toString(0));
                if (i.hasNext())// && !env.isabelle())
                    sb.append(".");
            }
        }
        return sb.toString();
    }

    public Exp replace(Exp old, Exp replacement) {

        if (old instanceof DotExp) {
            if (old.equals(this)) {
                return replacement;
            }
        }
        for (int count = 0; count < this.getSegments().size(); count++) {
            Exp oldExp = this.getSegments().get(count);
            if (oldExp instanceof FunctionExp) {
                Exp newExp = Exp.replace(oldExp, old, replacement);
                if (newExp != null) {
                    segments.remove(count);
                    segments.add(count, newExp);
                }
            }
        }

        if ((old instanceof VarExp || old instanceof OldExp)) {
            Iterator<Exp> it = segments.iterator();
            while (it.hasNext()) {
                Exp name = it.next();
                if (name instanceof FunctionExp) {
                    int index = it.nextIndex();
                    Exp newName = Exp.replace(name, old, replacement);

                    if (!newName.equals(name)) {
                        /* Weird way of doing it. Replaced it with the following. - YS
                        segments.remove(it.nextIndex()-1);
                        segments.add(it.nextIndex()-1, newName);
                         */
                        segments.remove(index - 1);
                        segments.add(index - 1, newName);
                        it = segments.iterator(); // Start Over. Inefficient, but works for now
                    }

                }
                else if (name instanceof VariableNameExp) {
                    int index = it.nextIndex();
                    VariableExp newName = (VariableExp) Exp.clone(name);
                    /* Was:
                     * new VarExp(null, null, 
                    		((VariableNameExp)name).getName());
                     */

                    if (!newName.equals(name)) {
                        /* Weird way of doing it. Replaced it with the following. - YS
                        segments.remove(it.nextIndex()-1);
                        segments.add(it.nextIndex()-1, newName);
                         */
                        segments.remove(index - 1);
                        segments.add(index - 1, newName);
                        it = segments.iterator(); // Start Over. Inefficient, but works for now
                    }
                }
            }
            it = segments.iterator();
            if (it.hasNext()) {
                Exp name = it.next();

                if (old instanceof VarExp && name instanceof VarExp) {
                    if (((VarExp) old).getName().toString().equals(
                            ((VarExp) name).getName().toString())
                            && (replacement instanceof DotExp)) {
                        segments.remove(0);
                        segments
                                .addAll(0, ((DotExp) replacement).getSegments());

                        return this;
                    }
                    else if (((VarExp) old).getName().toString().equals(
                            ((VarExp) name).getName().toString()) /*&& (replacement instanceof VarExp)*/) {
                        segments.remove(0);
                        segments.add(0, (Exp) (Exp.clone(replacement)));

                        return this;
                    }
                }
                else if (old instanceof OldExp && name instanceof OldExp/* && replacement instanceof VarExp*/) {
                    if (replacement instanceof DotExp) {
                        name = Exp.replace(name, old, replacement);
                        if (name != null) {
                            segments.remove(0);
                            segments.addAll(0, ((DotExp) replacement)
                                    .getSegments());
                            return this;
                        }
                    }
                    else {
                        name = Exp.replace(name, old, replacement);
                        if (name != null) {
                            segments.remove(0);
                            segments.add(0, (Exp) (Exp.clone(name)));
                            return this;
                        }
                    }
                }
            }

            //        	if(it.hasNext()){
            //        		Exp name = it.next();
            //        		name = name.replace(old, replacement);
            //        		if(name != null){
            //        			segments.remove(1);
            //        			segments.add(1, (Exp)(name.clone()));
            //        			return this;
            //        		}
            //        	}
        }
        else if (old instanceof DotExp && replacement instanceof DotExp) {
            if (all_segments_match((DotExp) old, this)) {
                return replacement;
            }
            else {
                update_matching_segments((DotExp) old, this, replacement);
            }

        }
        else if (old instanceof DotExp && replacement instanceof VarExp) {

            if (all_segments_match((DotExp) old, this)) {
                return replacement;
            }
            else {
                update_matching_segments((DotExp) old, this, replacement);
            }

        }

        return this;
    }

    public boolean all_segments_match(DotExp old, DotExp current) {
        if (old.getSegments().size() != current.getSegments().size()) {
            return false;
        }
        else {
            for (int count = 0; count < old.getSegments().size(); count++) {
                if (!old.getSegments().get(count).equals(
                        current.getSegments().get(count))) {
                    return false;
                }
            }
        }
        return true;
    }

    public void update_matching_segments(DotExp old, DotExp current, Exp newExp) {

        if (old.getSegments().size() <= current.getSegments().size()) {

            for (int count = 0; count < old.getSegments().size(); count++) {
                Exp oldExp = old.getSegments().get(count);
                Exp curExp = current.getSegments().get(count);
                if (curExp instanceof FunctionExp
                        && oldExp instanceof VarExp
                        && ((FunctionExp) curExp).getName().getName().equals(
                                ((VarExp) oldExp).getName().getName())) {
                    // This matches.
                }
                else if (!oldExp.equals(curExp)) {
                    return;
                }
            }
        }

        if (old.getSegments().size() <= current.getSegments().size()) {
            List<Exp> newSegments = new List<Exp>();
            if (newExp instanceof DotExp) {

                newSegments.addAll(0, ((DotExp) newExp).getSegments());
            }

            for (int count = 0; count < old.getSegments().size(); count++) {

                Exp oldExp = old.getSegments().get(count);
                Exp curExp = current.getSegments().get(0);

                if (old.getSegments().get(count).equals(
                        current.getSegments().get(0))) {
                    current.segments.remove(0);
                }
                else if (curExp instanceof FunctionExp
                        && oldExp instanceof VarExp
                        && ((FunctionExp) curExp).getName().getName().equals(
                                ((VarExp) oldExp).getName().getName())) {
                    // This matches.
                    if (newExp instanceof DotExp) {
                        newSegments.remove(count);
                        ((FunctionExp) curExp).setName(((VarExp) oldExp)
                                .getName());
                    }
                    else if (newExp instanceof VarExp) {
                        current.segments.remove(0);
                        ((FunctionExp) curExp).setName(((VarExp) newExp)
                                .getName());
                        newExp = curExp;
                    }

                }
            }

            if (newExp instanceof DotExp) {
                current.segments.addAll(0, newSegments);
            }
            else {
                current.segments.add(0, newExp);
            }
        }

    }

    public Exp remember() {

        rememberVariablesInExpList(this.getSegments());
        return this;
    }

    private void rememberVariablesInExpList(List<Exp> list) {
        ListIterator<Exp> i = list.listIterator();
        while (i.hasNext()) {
            Exp exp = (Exp) i.next();
            if (exp instanceof OldExp) {
                exp = ((OldExp) exp).getExp();
                i.set(exp);
            }
            else
                exp = exp.remember();
        }
    }

}

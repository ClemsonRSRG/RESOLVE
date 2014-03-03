/**
 * ProgramDotExp.java
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
import edu.clemson.cs.r2jt.type.Type;
import edu.clemson.cs.r2jt.analysis.TypeResolutionException;
import edu.clemson.cs.r2jt.collections.Iterator;

public class ProgramDotExp extends ProgramExp {

    // ===========================================================
    // Variables
    // ===========================================================

    /** The location member. */
    private Location location;

    /** The segments member. */
    private List<ProgramExp> segments;

    /** The semanticExp member. */
    private ProgramExp semanticExp;

    // ===========================================================
    // Constructors
    // ===========================================================

    public ProgramDotExp() {};

    public ProgramDotExp(Location location, List<ProgramExp> segments,
            ProgramExp semanticExp) {
        this.location = location;
        this.segments = segments;
        this.semanticExp = semanticExp;
    }

    public Exp substituteChildren(java.util.Map<Exp, Exp> substitutions) {
        List<ProgramExp> newSegments = new List<ProgramExp>();
        for (ProgramExp e : segments) {
            newSegments.add((ProgramExp) substitute(e, substitutions));
        }

        return new ProgramDotExp(location, newSegments,
                (ProgramExp) substitute(semanticExp, substitutions));
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

    /** Returns the value of the segments variable. */
    public List<ProgramExp> getSegments() {
        return segments;
    }

    /** Returns the value of the semanticExp variable. */
    public ProgramExp getSemanticExp() {
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
    public void setSegments(List<ProgramExp> segments) {
        this.segments = segments;
    }

    /** Sets the semanticExp variable to the specified value. */
    public void setSemanticExp(ProgramExp semanticExp) {
        this.semanticExp = semanticExp;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /** Accepts a ResolveConceptualVisitor. */
    public void accept(ResolveConceptualVisitor v) {
        v.visitProgramDotExp(this);
    }

    /** Accepts a TypeResolutionVisitor. */
    public Type accept(TypeResolutionVisitor v) throws TypeResolutionException {
        return v.getProgramDotExpType(this);
    }

    /** Returns a formatted text string of this class. */
    public String asString(int indent, int increment) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);
        sb.append("ProgramDotExp\n");

        if (segments != null) {
            sb.append(segments.asString(indent + increment, increment));
        }

        if (semanticExp != null) {
            sb.append(semanticExp.asString(indent + increment, increment));
        }

        return sb.toString();
    }

    /** Returns a formatted text string of this class. */
    public String toString(int indent) {

        StringBuffer sb = new StringBuffer();
        printSpace(indent, sb);

        sb.append(segmentsToString(this.segments));

        return sb.toString();
    }

    private String segmentsToString(List<ProgramExp> segments) {
        StringBuffer sb = new StringBuffer();
        if (segments != null) {
            Iterator<ProgramExp> i = segments.iterator();

            while (i.hasNext()) {
                sb.append(i.next().toString(0));
                if (i.hasNext())
                    sb.append(".");
            }
        }
        return sb.toString();
    }

    /** Returns true if the variable is found in any sub expression   
        of this one. **/
    public boolean containsVar(String varName, boolean IsOldExp) {
        if (segments != null) {
            Iterator<ProgramExp> i = segments.iterator();
            while (i.hasNext()) {
                ProgramExp temp = i.next();
                if (temp != null) {
                    if (temp.containsVar(varName, IsOldExp)) {
                        return true;
                    }
                }
            }
        }
        if (semanticExp != null) {
            if (semanticExp.containsVar(varName, IsOldExp)) {
                return true;
            }
        }
        return false;
    }

    public Object clone() {
        ProgramDotExp clone = new ProgramDotExp();
        clone.setSemanticExp((ProgramExp) Exp.clone(this.getSemanticExp()));
        clone.setLocation(this.getLocation());
        if (segments != null) {
            Iterator<ProgramExp> i = segments.iterator();
            List<ProgramExp> newSegments = new List<ProgramExp>();
            while (i.hasNext()) {
                newSegments.add((ProgramExp) Exp.clone(i.next()));
            }
            clone.setSegments(newSegments);
        }
        return clone;
    }

    public List<Exp> getSubExpressions() {
        List<Exp> list = new List<Exp>();
        Iterator<ProgramExp> segmentsIt = segments.iterator();
        while (segmentsIt.hasNext()) {
            list.add((Exp) (segmentsIt.next()));
        }
        return list;
    }

    public void setSubExpression(int index, Exp e) {
        segments.set(index, (ProgramExp) e);
    }

    public Exp replace(Exp old, Exp replacement) {
        if (old instanceof ProgramDotExp) {
            if (old.equals(this)) {
                return replacement;
            }
        }

        if ((old instanceof VarExp || old instanceof OldExp)) {
            Iterator<ProgramExp> it = segments.iterator();

            if (it.hasNext()) {
                Exp name = it.next();
                if (old instanceof VarExp && name instanceof VarExp) {
                    if (((VarExp) old).getName().toString().equals(
                            ((VarExp) name).getName().toString())) {
                        segments.remove(0);
                        segments.add(0, (ProgramExp) (Exp.clone(replacement)));

                        return this;
                    }
                }
                else if (old instanceof OldExp && name instanceof OldExp) {
                    name = Exp.replace(name, old, replacement);
                    if (name != null) {
                        segments.remove(0);
                        segments.add(0, (ProgramExp) (Exp.clone(name)));
                        return this;
                    }
                }
            }

            if (it.hasNext()) {
                Exp name = it.next();
                name = Exp.replace(name, old, replacement);
                if (name != null) {
                    segments.remove(1);
                    segments.add(1, (ProgramExp) (Exp.clone(name)));
                    return this;
                }
            }
        }

        return this;
    }

}

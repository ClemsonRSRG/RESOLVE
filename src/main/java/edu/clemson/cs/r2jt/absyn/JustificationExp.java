/*
 * JustificationExp.java
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

public class JustificationExp extends Exp {

    // ===========================================================
    // Variables
    // ===========================================================

    /** The location member. */
    private Location location;

    /** The hypDesig1 member. */
    private HypDesigExp hypDesig1;

    /** The hypDesig2 member. */
    private HypDesigExp hypDesig2;

    /** The rule member. */
    private PosSymbol rule;

    /** The index of the rule if referencing a part of an inductive 
     *  definition.*/
    private PosSymbol index;

    /** The name of the source module from which to receive the definition. */
    private PosSymbol sourceModule;

    /** The isDef member. */
    private boolean isDef;

    // ===========================================================
    // Constructors
    // ===========================================================

    public JustificationExp() {};

    public JustificationExp(Location location, HypDesigExp hypDesig1,
            HypDesigExp hypDesig2, PosSymbol rule, boolean isDef) {
        this.location = location;
        this.hypDesig1 = hypDesig1;
        this.hypDesig2 = hypDesig2;
        this.rule = rule;
        this.isDef = isDef;
    }

    public JustificationExp(Location location, HypDesigExp hypDesig1,
            HypDesigExp hypDesig2, PosSymbol rule, PosSymbol index,
            PosSymbol sourceModule, boolean isDef) {
        this.location = location;
        this.hypDesig1 = hypDesig1;
        this.hypDesig2 = hypDesig2;
        this.rule = rule;
        this.index = index;
        this.sourceModule = sourceModule;
        this.isDef = isDef;

        prettyPrint();
    }

    public Exp substituteChildren(java.util.Map<Exp, Exp> substitutions) {
        return new JustificationExp(location, (HypDesigExp) substitute(
                hypDesig1, substitutions), (HypDesigExp) substitute(hypDesig2,
                substitutions), rule, index, sourceModule, isDef);
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

    /** Returns the value of the hypDesig1 variable. */
    public HypDesigExp getHypDesig1() {
        return hypDesig1;
    }

    /** Returns the value of the hypDesig2 variable. */
    public HypDesigExp getHypDesig2() {
        return hypDesig2;
    }

    /** Returns the value of the rule variable. */
    public PosSymbol getRule() {
        return rule;
    }

    public PosSymbol getIndex() {
        return index;
    }

    public PosSymbol getSourceModule() {
        return sourceModule;
    }

    /** Returns the value of the isDef variable. */
    public boolean getIsDef() {
        return isDef;
    }

    // -----------------------------------------------------------
    // Set Methods
    // -----------------------------------------------------------

    /** Sets the value of the location variable. */
    public void setLocation(Location location) {
        this.location = location;
    }

    /** Sets the value of the hypDesig1 variable. */
    public void setHypDesig1(HypDesigExp hypDesig1) {
        this.hypDesig1 = hypDesig1;
    }

    /** Sets the value of the hypDesig2 variable. */
    public void setHypDesig2(HypDesigExp hypDesig2) {
        this.hypDesig2 = hypDesig2;
    }

    /** Sets the value of the rule variable. */
    public void setRule(PosSymbol rule) {
        this.rule = rule;
    }

    public void setIndex(PosSymbol index) {
        this.index = index;
    }

    public void setSourceModule(PosSymbol sourceModule) {
        this.sourceModule = sourceModule;
    }

    /** Sets the value of the isDef variable. */
    public void setIsDef(boolean isDef) {
        this.isDef = isDef;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /** Accepts a ResolveConceptualVisitor. */
    public void accept(ResolveConceptualVisitor v) {
        v.visitJustificationExp(this);
    }

    /** Returns a formatted text string of this class. */
    public String asString(int indent, int increment) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);
        sb.append("JustificationExp\n");

        if (hypDesig1 != null) {
            printSpace(indent, sb);
            sb.append("(hypDesig1:) \n"
                    + hypDesig1.asString(indent + increment, increment));
        }

        if (hypDesig2 != null) {
            printSpace(indent, sb);
            sb.append("(hypDesig2:) \n"
                    + hypDesig2.asString(indent + increment, increment));
        }

        if (index != null) {
            sb.append("(" + index.asString(indent + increment, increment)
                    + ") of ");
        }

        if (rule != null) {
            sb.append(rule.asString(indent + increment, increment));
        }

        if (sourceModule != null) {
            sb.append(" from "
                    + sourceModule.asString(indent + increment, increment));
        }

        printSpace(indent, sb);
        sb.append(isDef + "\n");

        return sb.toString();
    }

    public boolean containsVar(String varName, boolean IsOldExp) {
        return false;
    }

    public List<Exp> getSubExpressions() {
        return new List<Exp>();
    }

    public void setSubExpression(int index, Exp e) {

    }

    public void prettyPrint() {
        System.out.print("by ");
        boolean needComma = false;
        if (hypDesig1 != null) {
            hypDesig1.prettyPrint();
            needComma = true;
            if (hypDesig2 != null) {
                System.out.print(", ");
                hypDesig2.prettyPrint();
            }
        }
        if (isDef) {
            if (needComma)
                System.out.print(", ");

            if (index != null) {
                System.out.print("(" + index.getName() + ") of ");
            }

            System.out.print("Def. ");

            needComma = false;
        }
        if (rule != null) {
            if (needComma)
                System.out.print(", " + rule.getName());
            else
                System.out.print(rule.getName());
        }
        if (sourceModule != null) {
            System.out.print(" from " + sourceModule.getName());
        }
    }

    public Exp copy() {
        HypDesigExp newHypDesig1 = (HypDesigExp) (Exp.copy(hypDesig1));
        HypDesigExp newHypDesig2 = (HypDesigExp) (Exp.copy(hypDesig2));
        PosSymbol newRule = null;
        if (rule != null) {
            newRule = rule.copy();
        }

        PosSymbol newIndex = null;
        if (index != null) {
            newIndex = index.copy();
        }

        PosSymbol newSourceModule = null;
        if (sourceModule != null) {
            newSourceModule = sourceModule.copy();
        }

        boolean newIsDef = isDef;
        return new JustificationExp(null, newHypDesig1, newHypDesig2, newRule,
                newIndex, newSourceModule, newIsDef);
    }

}
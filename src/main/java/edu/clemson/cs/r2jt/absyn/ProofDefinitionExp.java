/*
 * ProofDefinitionExp.java
 * ---------------------------------
 * Copyright (c) 2018
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

public class ProofDefinitionExp extends LineNumberedExp {

    // ===========================================================
    // Variables
    // ===========================================================

    /** The location member. */
    private Location location;

    /** The exp member. */
    private DefinitionDec exp;

    // ===========================================================
    // Constructors
    // ===========================================================

    public ProofDefinitionExp() {
        super(null);
    }

    public ProofDefinitionExp(Location location, PosSymbol lineNum,
            DefinitionDec exp) {
        super(lineNum);
        this.location = location;
        this.exp = exp;
    }

    public Exp substituteChildren(java.util.Map<Exp, Exp> substitutions) {
        return new ProofDefinitionExp(location, this.getLineNum(), exp);
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
    public DefinitionDec getExp() {
        return exp;
    }

    // -----------------------------------------------------------
    // Set Methods
    // -----------------------------------------------------------

    /** Sets the value of the location variable. */
    public void setLocation(Location location) {
        this.location = location;
    }

    /** Sets the value of the exp variable. */
    public void setExp(DefinitionDec exp) {
        this.exp = exp;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /** Accepts a ResolveConceptualVisitor. */
    public void accept(ResolveConceptualVisitor v) {
        v.visitProofDefinitionExp(this);
    }

    /** Returns a formatted text string of this class. */
    public String asString(int indent, int increment) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);
        sb.append("ProofDefinitionExp\n");

        if (myLineNumber != null) {
            printSpace(indent + increment, sb);
            sb.append("Line: " + myLineNumber.asString(0, increment));
        }

        if (exp != null) {
            sb.append(exp.asString(indent + increment, increment));
        }

        return sb.toString();
    }

    public boolean containsVar(String varName, boolean IsOldExp) {
        return false;
    }

    public List<Exp> getSubExpressions() {
        List<Exp> list = new List<Exp>();
        list.add(exp.getBase());
        list.add(exp.getHypothesis());
        list.add(exp.getDefinition());
        return list;
    }

    public void setSubExpression(int index, Exp e) {
        throw new UnsupportedOperationException(
                "Proof definition expression is immutable.");
        /*switch (index) {
        case 0:
        	exp.setBase(e);
        	break;
        case 1:
        	exp.setHypothesis(e);
        	break;
        case 2:
        	exp.setDefinition(e);
        	break;
        }*/
    }

    public boolean shallowCompare(Exp e2) {
        if (!(e2 instanceof ProofDefinitionExp)) {
            return false;
        }
        return true;
    }

}
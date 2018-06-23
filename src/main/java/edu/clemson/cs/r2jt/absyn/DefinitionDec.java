/*
 * DefinitionDec.java
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

import edu.clemson.cs.r2jt.collections.Iterator;
import edu.clemson.cs.r2jt.collections.List;
import edu.clemson.cs.r2jt.data.PosSymbol;

public class DefinitionDec extends Dec implements ModuleParameter {

    // ===========================================================
    // Variables
    // ===========================================================

    /** The implicit member. */
    private boolean implicit;

    /** The name member. */
    private PosSymbol name;

    /** The parameters member. */
    private List<MathVarDec> parameters;

    /** The returnTy member. */
    private Ty returnTy;

    private DefinitionBody body;

    // ===========================================================
    // Constructors
    // ===========================================================

    public DefinitionDec() {};

    public DefinitionDec(boolean implicit, PosSymbol name,
            List<MathVarDec> parameters, Ty returnTy, Exp base, Exp hypothesis,
            Exp definition) {
        this.implicit = implicit;
        this.name = name;
        this.parameters = parameters;
        this.returnTy = returnTy;
        if (!(base == null && hypothesis == null && definition == null)) {
            this.body = new DefinitionBody(base, hypothesis, definition);
        }
    }

    // ===========================================================
    // Accessor Methods
    // ===========================================================

    // -----------------------------------------------------------
    // Get Methods
    // -----------------------------------------------------------

    /** Returns the value of the implicit variable. */
    public boolean isImplicit() {
        return implicit;
    }

    public boolean isInductive() {
        return body == null ? false : body.isInductive();
    }

    /** Returns the value of the name variable. */
    public PosSymbol getName() {
        return name;
    }

    /** Returns the value of the parameters variable. */
    public List<MathVarDec> getParameters() {
        return parameters;
    }

    /** Returns the value of the returnTy variable. */
    public Ty getReturnTy() {
        return returnTy;
    }

    /** Returns the value of the base variable. */
    public Exp getBase() {
        return body == null ? null : body.getBase();
    }

    /** Returns the value of the hypothesis variable. */
    public Exp getHypothesis() {
        return body == null ? null : body.getHypothesis();
    }

    /** Returns the value of the definition variable. */
    public Exp getDefinition() {
        return body == null ? null : body.getDefinition();
    }

    // -----------------------------------------------------------
    // Set Methods
    // -----------------------------------------------------------

    /** Sets the implicit variable to the specified value. */
    public void setImplicit(boolean implicit) {
        this.implicit = implicit;
    }

    /** Sets the name variable to the specified value. */
    public void setName(PosSymbol name) {
        this.name = name;
    }

    /** Sets the parameters variable to the specified value. */
    public void setParameters(List<MathVarDec> parameters) {
        this.parameters = parameters;
    }

    /** Sets the returnTy variable to the specified value. */
    public void setReturnTy(Ty returnTy) {
        this.returnTy = returnTy;
    }

    /** Sets the base variable to the specified value. */
    public void setBase(Exp base) {
        body.setBase(base);
    }

    /** Sets the hypothesis variable to the specified value. */
    public void setHypothesis(Exp hypothesis) {
        body.setHypothesis(hypothesis);
    }

    /** Sets the definition variable to the specified value. */
    public void setDefinition(Exp definition) {
        body.setDefinition(definition);
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /** Accepts a ResolveConceptualVisitor. */
    public void accept(ResolveConceptualVisitor v) {
        v.visitDefinitionDec(this);
    }

    /** Returns a formatted text string of this class. */
    public String asString(int indent, int increment) {

        StringBuffer sb = new StringBuffer();

        printSpace(indent, sb);
        sb.append("DefinitionDec\n");

        printSpace(indent + increment, sb);
        sb.append(implicit + "\n");

        if (name != null) {
            sb.append(name.asString(indent + increment, increment));
        }

        if (parameters != null) {
            sb.append(parameters.asString(indent + increment, increment));
        }

        if (returnTy != null) {
            sb.append(returnTy.asString(indent + increment, increment));
        }

        if (body != null) {
            sb.append(body.toString());
        }

        return sb.toString();
    }

    public void prettyPrint() {
        if (implicit) {
            System.out.print("Implicit ");
        }
        System.out.print("Definition " + name.getName());
        System.out.print("(");
        Iterator<MathVarDec> it = parameters.iterator();
        if (it.hasNext()) {
            it.next().prettyPrint();
        }
        System.out.print(") : ");
        returnTy.prettyPrint();
        if (implicit)
            System.out.print(" is ");
        else
            System.out.print(" = ");

        if (getBase() != null) {
            getBase().prettyPrint();
            System.out.println();
            getHypothesis().prettyPrint();
        }
        else {
            getDefinition().prettyPrint();
        }
    }

}

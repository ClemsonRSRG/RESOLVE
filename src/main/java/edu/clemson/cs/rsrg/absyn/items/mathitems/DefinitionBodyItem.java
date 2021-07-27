/*
 * DefinitionBodyItem.java
 * ---------------------------------
 * Copyright (c) 2021
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.absyn.items.mathitems;

import edu.clemson.cs.rsrg.absyn.ResolveConceptualElement;
import edu.clemson.cs.rsrg.absyn.expressions.Exp;

/**
 * <p>
 * This is the class for all the definition body objects that the compiler
 * builds using the ANTLR4
 * AST nodes.
 * </p>
 *
 * @version 2.0
 */
public class DefinitionBodyItem extends ResolveConceptualElement {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * The base expression for an inductive definition
     * </p>
     */
    private final Exp myBase;

    /**
     * <p>
     * The hypothesis expression for an inductive definition
     * </p>
     */
    private final Exp myHypothesis;

    /**
     * <p>
     * The base expression for a standard definition
     * </p>
     */
    private final Exp myDefinition;

    /**
     * <p>
     * Boolean indicating if this is an inductive definition or not.
     * </p>
     */
    private final boolean myIsInductiveFlag;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This constructs a standard definition body.
     * </p>
     *
     * @param definition The definition expression.
     */
    public DefinitionBodyItem(Exp definition) {
        super(definition.getLocation());
        myBase = null;
        myHypothesis = null;
        myDefinition = definition;
        myIsInductiveFlag = false;
    }

    /**
     * <p>
     * This constructs an inductive definition body.
     * </p>
     *
     * @param base The base case expression.
     * @param hypothesis The inductive hypothesis expression.
     */
    public DefinitionBodyItem(Exp base, Exp hypothesis) {
        super(base.getLocation());
        myBase = base;
        myHypothesis = hypothesis;
        myDefinition = null;
        myIsInductiveFlag = true;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * {@inheritDoc}
     */
    @Override
    public final String asString(int indentSize, int innerIndentInc) {
        StringBuffer sb = new StringBuffer();

        if (myIsInductiveFlag) {
            sb.append(" is\n");

            // base case
            printSpace(indentSize, sb);
            sb.append("(i.) ");
            sb.append(myBase.asString(0, innerIndentInc));
            sb.append("\n");

            // inductive hypothesis
            printSpace(indentSize, sb);
            sb.append("(ii.) ");
            sb.append(myHypothesis.asString(0, innerIndentInc));
        }
        else {
            sb.append(" =\n");
            sb.append(myDefinition.asString(indentSize, innerIndentInc));
        }

        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final DefinitionBodyItem clone() {
        DefinitionBodyItem newItem;
        if (myIsInductiveFlag) {
            newItem = new DefinitionBodyItem(myBase.clone(),
                    myHypothesis.clone());
        }
        else {
            newItem = new DefinitionBodyItem(myDefinition.clone());
        }

        return newItem;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        DefinitionBodyItem that = (DefinitionBodyItem) o;

        if (myIsInductiveFlag != that.myIsInductiveFlag)
            return false;
        if (myBase != null ? !myBase.equals(that.myBase) : that.myBase != null)
            return false;
        if (myHypothesis != null ? !myHypothesis.equals(that.myHypothesis)
                : that.myHypothesis != null)
            return false;
        return myDefinition != null ? myDefinition.equals(that.myDefinition)
                : that.myDefinition == null;

    }

    /**
     * <p>
     * This method returns the base case of an inductive definition.
     * </p>
     *
     * @return An {@code Exp} representing the base case or {@code null}.
     */
    public final Exp getBase() {
        return myBase;
    }

    /**
     * <p>
     * This method returns the definition expression of a standard definition.
     * </p>
     *
     * @return An {@code Exp} representing the definition expression or
     *         {@code null}.
     */
    public final Exp getDefinition() {
        return myDefinition;
    }

    /**
     * <p>
     * This method returns the hypothesis of an inductive definition.
     * </p>
     *
     * @return An {@code Exp} representing the inductive hypothesis or
     *         {@code null}.
     */
    public final Exp getHypothesis() {
        return myHypothesis;
    }

    /**
     * <p>
     * This method returns whether or not this is an inductive definition.
     * </p>
     *
     * @return {@code true} if it is inductive, {@code false} otherwise.
     */
    public final boolean getIsInductiveFlag() {
        return myIsInductiveFlag;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        int result = myBase != null ? myBase.hashCode() : 0;
        result = 31 * result
                + (myHypothesis != null ? myHypothesis.hashCode() : 0);
        result = 31 * result
                + (myDefinition != null ? myDefinition.hashCode() : 0);
        result = 31 * result + (myIsInductiveFlag ? 1 : 0);
        return result;
    }

}

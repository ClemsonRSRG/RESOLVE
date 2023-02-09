/*
 * MathDefinitionDec.java
 * ---------------------------------
 * Copyright (c) 2023
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.rsrg.absyn.declarations.mathdecl;

import edu.clemson.rsrg.absyn.declarations.Dec;
import edu.clemson.rsrg.absyn.declarations.paramdecl.ModuleParameter;
import edu.clemson.rsrg.absyn.declarations.variabledecl.MathVarDec;
import edu.clemson.rsrg.absyn.expressions.Exp;
import edu.clemson.rsrg.absyn.items.mathitems.DefinitionBodyItem;
import edu.clemson.rsrg.absyn.rawtypes.Ty;
import edu.clemson.rsrg.parsing.data.PosSymbol;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <p>
 * This is the class for all the mathematical definition declarations that the compiler builds using the ANTLR4 AST
 * nodes.
 * </p>
 *
 * @version 2.0
 */
public class MathDefinitionDec extends Dec implements ModuleParameter {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * Boolean indicating if this is an implicit definition or not.
     * </p>
     */
    private final boolean myIsImplicitFlag;

    /**
     * <p>
     * The list of math parameter variable declarations
     * </p>
     */
    private final List<MathVarDec> myParameters;

    /**
     * <p>
     * The type model for the return value.
     * </p>
     */
    private final Ty myReturnTy;

    /**
     * <p>
     * The definition body.
     * </p>
     */
    private final DefinitionBodyItem myBodyItem;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This constructs a mathematical definition.
     * </p>
     *
     * @param name
     *            Name of the definition.
     * @param parameters
     *            List of parameters for this definition.
     * @param returnTy
     *            The return type for this definition.
     * @param bodyItem
     *            The definition body (if any).
     * @param implicitFlag
     *            A boolean indicating if this definition is implicit or not.
     */
    public MathDefinitionDec(PosSymbol name, List<MathVarDec> parameters, Ty returnTy, DefinitionBodyItem bodyItem,
            boolean implicitFlag) {
        super(name.getLocation(), name);
        myIsImplicitFlag = implicitFlag;
        myParameters = parameters;
        myReturnTy = returnTy;
        myBodyItem = bodyItem;
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

        printSpace(indentSize, sb);
        if (myIsImplicitFlag) {
            sb.append("Implicit ");
        } else if (myBodyItem != null && myBodyItem.getIsInductiveFlag()) {
            sb.append("Inductive ");
        }
        sb.append("Definition ");
        sb.append(myName.asString(0, innerIndentInc));

        // any parameters
        if (myParameters.size() > 0) {
            sb.append("(");
            Iterator<MathVarDec> it = myParameters.iterator();
            while (it.hasNext()) {
                sb.append(it.next().asString(0, innerIndentInc));

                if (it.hasNext()) {
                    sb.append(", ");
                }
            }
            sb.append(")");
        }

        // return type
        sb.append(" : ");
        sb.append(myReturnTy.asString(0, innerIndentInc));

        // any definition body
        if (myBodyItem != null) {
            sb.append(myBodyItem.asString(indentSize + innerIndentInc, innerIndentInc));
        }
        sb.append(";");

        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;

        MathDefinitionDec that = (MathDefinitionDec) o;

        if (myIsImplicitFlag != that.myIsImplicitFlag)
            return false;
        if (!myParameters.equals(that.myParameters))
            return false;
        if (!myReturnTy.equals(that.myReturnTy))
            return false;
        return myBodyItem != null ? myBodyItem.equals(that.myBodyItem) : that.myBodyItem == null;
    }

    /**
     * <p>
     * This method returns the base case of an inductive definition.
     * </p>
     *
     * @return An {@code Exp} representing the base case or {@code null}.
     */
    public final Exp getBase() {
        return myBodyItem == null ? null : myBodyItem.getBase();
    }

    /**
     * <p>
     * This method returns the definition expression of a standard definition.
     * </p>
     *
     * @return An {@code Exp} representing the definition expression or {@code null}.
     */
    public final Exp getDefinition() {
        return myBodyItem == null ? null : myBodyItem.getDefinition();
    }

    /**
     * <p>
     * This method returns the hypothesis of an inductive definition.
     * </p>
     *
     * @return An {@code Exp} representing the inductive hypothesis or {@code null}.
     */
    public final Exp getHypothesis() {
        return myBodyItem == null ? null : myBodyItem.getHypothesis();
    }

    /**
     * <p>
     * This method returns whether or not this is an implicit definition.
     * </p>
     *
     * @return {@code true} if it is inductive, {@code false} otherwise.
     */
    public final boolean getIsImplicitFlag() {
        return myIsImplicitFlag;
    }

    /**
     * <p>
     * This method returns whether or not this is an inductive definition.
     * </p>
     *
     * @return {@code true} if it is inductive, {@code false} otherwise.
     */
    public final boolean getIsInductiveFlag() {
        return myBodyItem != null && myBodyItem.getIsInductiveFlag();
    }

    /**
     * <p>
     * This method returns the math variable declarations for this definition declaration.
     * </p>
     *
     * @return A list of {@link MathVarDec} representation objects.
     */
    public final List<MathVarDec> getParameters() {
        return myParameters;
    }

    /**
     * <p>
     * This method returns the raw return type for this definition declaration.
     * </p>
     *
     * @return The {@link Ty} representation object.
     */
    public final Ty getReturnTy() {
        return myReturnTy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (myIsImplicitFlag ? 1 : 0);
        result = 31 * result + myParameters.hashCode();
        result = 31 * result + myReturnTy.hashCode();
        result = 31 * result + (myBodyItem != null ? myBodyItem.hashCode() : 0);
        return result;
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * {@inheritDoc}
     */
    @Override
    protected final MathDefinitionDec copy() {
        List<MathVarDec> newParameters = new ArrayList<>();
        for (MathVarDec varDec : myParameters) {
            newParameters.add((MathVarDec) varDec.clone());
        }

        DefinitionBodyItem newItem = null;
        if (myBodyItem != null) {
            newItem = myBodyItem.clone();
        }

        return new MathDefinitionDec(myName.clone(), newParameters, myReturnTy.clone(), newItem, myIsImplicitFlag);
    }
}

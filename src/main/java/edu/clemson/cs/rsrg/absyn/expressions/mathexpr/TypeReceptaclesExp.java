/*
 * TypeReceptaclesExp.java
 * ---------------------------------
 * Copyright (c) 2020
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.absyn.expressions.mathexpr;

import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.parsing.data.Location;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * This is the class represents a programming type {@code Receptacles} as an
 * expression that the
 * compiler builds using the ANTLR4 AST nodes.
 * </p>
 *
 * <p>
 * As an example a programming type: {@code Stack}'s {@code Receptacles}
 * ({@code Stack.Receptacles}
 * indicate the named and unnamed {@code Stack}s declared in the current
 * execution context.
 * </p>
 *
 * @author Yu-Shan Sun
 * @version 1.0
 */
public class TypeReceptaclesExp extends MathExp {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * The expression's type represented as an {@link VarExp}.
     * </p>
     */
    private final VarExp myTypeAsVarExp;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     *
     * @param l A {@link Location} representation object.
     * @param typeNameExp A {@link VarExp} indicating the type name.
     */
    public TypeReceptaclesExp(Location l, VarExp typeNameExp) {
        super(l);
        myTypeAsVarExp = typeNameExp;
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
        sb.append(myTypeAsVarExp.asString(0, innerIndentInc));
        sb.append(".Receptacles");

        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean containsExp(Exp exp) {
        return myTypeAsVarExp.equivalent(exp);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean containsVar(String varName, boolean IsOldExp) {
        boolean retval = false;
        if (!IsOldExp && myTypeAsVarExp.containsVar(varName, false)) {
            retval = true;
        }

        return retval;
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

        TypeReceptaclesExp that = (TypeReceptaclesExp) o;

        return myTypeAsVarExp.equals(that.myTypeAsVarExp);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean equivalent(Exp e) {
        boolean retval = false;
        if (e instanceof TypeReceptaclesExp) {
            TypeReceptaclesExp eAsTypeReceptaclesExp = (TypeReceptaclesExp) e;
            retval = (myTypeAsVarExp
                    .equivalent(eAsTypeReceptaclesExp.myTypeAsVarExp));
        }

        return retval;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final List<Exp> getSubExpressions() {
        return Collections.singletonList(myTypeAsVarExp.copy());
    }

    /**
     * <p>
     * This method returns the type name with any qualifiers.
     * </p>
     *
     * @return The {@link VarExp} representation object.
     */
    public final VarExp getTypeAsVarExp() {
        return myTypeAsVarExp;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        int result = super.hashCode();
        result = 31 * result + myTypeAsVarExp.hashCode();
        return result;
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * {@inheritDoc}
     */
    @Override
    protected final Exp copy() {
        return new TypeReceptaclesExp(cloneLocation(),
                (VarExp) myTypeAsVarExp.clone());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final Exp substituteChildren(Map<Exp, Exp> substitutions) {
        return new TypeReceptaclesExp(cloneLocation(),
                (VarExp) substitute(myTypeAsVarExp, substitutions));
    }

}

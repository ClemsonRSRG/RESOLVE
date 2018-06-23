/*
 * DummyExp.java
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
package edu.clemson.cs.rsrg.typeandpopulate.typereasoning;

import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.absyn.expressions.mathexpr.MathExp;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.statushandling.exception.MiscErrorException;
import edu.clemson.cs.rsrg.typeandpopulate.mathtypes.MTType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>A {@code DummyExp} is an {@link MathExp} guaranteed not to arise
 * from any actual RESOLVE source code. Its only property is that it has an
 * {@link MTType}. It can be bound normally to quantified variables whose
 * declared type it inhabits (i.e., a {@code DummyExp} of type
 * {@code N} is acceptable for a quantified variable of type
 * {@code Z},) but nothing will bind to it.</p>
 *
 * <p>Mostly useful for representing "a unique variable of type X" without
 * having to worry if its name is truly unique.</p>
 *
 * @author Hampton Smith
 * @author Yu-Shan Sun
 * @version 2.0
 */
public class DummyExp extends MathExp {

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs a {@code DummyExp} from the given mathematical
     * type.</p>
     *
     * @param l A {@link Location} representation object.
     * @param t A {@link MTType} representation object.
     */
    public DummyExp(Location l, MTType t) {
        super(l);
        myMathType = t;
    }

    /**
     * <p>This is a copy constructor that constructs a copy of the
     * {@code DummyExp} passed in.</p>
     *
     * @param original A {@code DummyExp} representation object.
     */
    public DummyExp(DummyExp original) {
        super(original.cloneLocation());
        myMathType = original.myMathType;
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
        sb.append("(some ");
        sb.append(myMathType);
        sb.append(")");

        return sb.toString();
    }

    /**
     * <p>This method is not supported, so any caller of this method will
     * get a {@link UnsupportedOperationException} wrapped inside a
     * {@link MiscErrorException}.</p>
     */
    @Override
    public final boolean containsExp(Exp exp) {
        throw new MiscErrorException("A DummyExp cannot contain an Exp.",
                new UnsupportedOperationException());
    }

    /**
     * <p>This method is not supported, so any caller of this method will
     * get a {@link UnsupportedOperationException} wrapped inside a
     * {@link MiscErrorException}.</p>
     */
    @Override
    public final boolean containsVar(String varName, boolean IsOldExp) {
        throw new MiscErrorException("A DummyExp cannot contain a variable.",
                new UnsupportedOperationException());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final List<Exp> getSubExpressions() {
        return new ArrayList<>();
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * {@inheritDoc}
     */
    @Override
    protected final Exp copy() {
        return new DummyExp(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final Exp substituteChildren(Map<Exp, Exp> substitutions) {
        return new DummyExp(this);
    }

}
/*
 * AffectsClause.java
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
package edu.clemson.cs.rsrg.absyn.clauses;

import edu.clemson.cs.rsrg.absyn.ResolveConceptualElement;
import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.parsing.data.Location;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <p>
 * This is the class for all the affected variable expressions that the compiler
 * builds from the
 * ANTLR4 AST tree.
 * </p>
 *
 * @version 2.0
 */
public class AffectsClause extends ResolveConceptualElement {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * The list of affected expressions.
     * </p>
     */
    private final List<Exp> myAffectedExps;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This constructs an {@code affects} clause.
     * </p>
     *
     * @param l A {@link Location} representation object.
     * @param affectedExps The list of expressions that are listed to be
     *        affected by an
     *        initialization/finalization item or an operation.
     */
    public AffectsClause(Location l, List<Exp> affectedExps) {
        super(l);
        myAffectedExps = affectedExps;
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
        sb.append("affects ");

        Iterator<Exp> it = myAffectedExps.iterator();
        while (it.hasNext()) {
            Exp exp = it.next();
            sb.append(exp.asString(0, innerIndentInc));

            if (it.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append(";");

        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final AffectsClause clone() {
        List<Exp> newAffectedExps = new ArrayList<>();
        for (Exp exp : myAffectedExps) {
            newAffectedExps.add(exp.clone());
        }

        return new AffectsClause(cloneLocation(), newAffectedExps);
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

        AffectsClause that = (AffectsClause) o;

        return myAffectedExps.equals(that.myAffectedExps);

    }

    /**
     * <p>
     * Returns the list of expressions that are affected by this clause.
     * </p>
     *
     * @return The list of affected {@link Exp}s.
     */
    public final List<Exp> getAffectedExps() {
        return myAffectedExps;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        return myAffectedExps.hashCode();
    }

}

/*
 * TupleExp.java
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
import edu.clemson.cs.rsrg.statushandling.exception.MiscErrorException;
import edu.clemson.cs.rsrg.typeandpopulate.entry.SymbolTableEntry;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <p>
 * This is the class for all the mathematical tuple expression objects that the
 * compiler builds
 * using the ANTLR4 AST nodes.
 * </p>
 *
 * <p>
 * Making {@code TupleExp} extend from {@link AbstractFunctionExp} was
 * considered and explicitly
 * decided against during the great math-type-overhaul of 2012. If we chose to
 * admit the presence of
 * some function that builds tuples for us, how would we pass it its parameters
 * if not via a tuple?
 * Thus, TupleExp is a built-in notion, and not imagined as the result of the
 * application of a
 * function.
 * </p>
 *
 * @version 2.0
 */
public class TupleExp extends MathExp {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * The expression's cartesian product fields
     * </p>
     */
    private final List<Exp> myFields;

    /**
     * <p>
     * The number of cartesian product fields
     * </p>
     */
    private final int mySize;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This constructs a tuple expression that contains exactly two elements per
     * field.
     * </p>
     *
     * @param l A {@link Location} representation object.
     * @param fields A list of {@link Exp} object.
     */
    public TupleExp(Location l, List<Exp> fields) {
        super(l);

        // We assert this isn't possible, but who knows?
        if (fields.size() != 2) {
            throw new MiscErrorException("Unexpected cartesian product size.",
                    new IllegalArgumentException());
        }

        myFields = fields;
        mySize = fields.size();
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
        sb.append("(");
        sb.append(myFields.get(0).asString(0, innerIndentInc));
        sb.append(", ");
        sb.append(myFields.get(1).asString(0, innerIndentInc));
        sb.append(")");

        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean containsExp(Exp exp) {
        boolean found = false;
        if (myFields != null) {
            Iterator<Exp> i = myFields.iterator();
            while (i.hasNext() && !found) {
                Exp temp = i.next();
                if (temp != null) {
                    if (temp.containsExp(exp)) {
                        found = true;
                    }
                }
            }
        }

        return found;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean containsVar(String varName, boolean IsOldExp) {
        boolean found = false;
        if (myFields != null) {
            Iterator<Exp> i = myFields.iterator();
            while (i.hasNext() && !found) {
                Exp temp = i.next();
                if (temp != null) {
                    if (temp.containsVar(varName, IsOldExp)) {
                        found = true;
                    }
                }
            }
        }

        return found;
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

        TupleExp tupleExp = (TupleExp) o;

        if (mySize != tupleExp.mySize)
            return false;
        return myFields.equals(tupleExp.myFields);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean equivalent(Exp e) {
        boolean result = (e instanceof TupleExp);

        if (result) {
            TupleExp eAsTupleExp = (TupleExp) e;

            if (myFields != null && eAsTupleExp.myFields != null) {
                Iterator<Exp> thisFieldExps = myFields.iterator();
                Iterator<Exp> eFieldExps = eAsTupleExp.myFields.iterator();
                while (result && thisFieldExps.hasNext()
                        && eFieldExps.hasNext()) {

                    result &=
                            thisFieldExps.next().equivalent(eFieldExps.next());
                }

                // Both had better have run out at the same time
                result &= (!thisFieldExps.hasNext()) && (!eFieldExps.hasNext());
            }
        }

        return result;
    }

    /**
     * <p>
     * This method returns the specified field expression.
     * </p>
     *
     * @param index The index of the field expression.
     *
     * @return A {@link Exp} representation object.
     */
    public final Exp getField(int index) {
        Exp result;

        if (index < 0 || index >= mySize) {
            throw new MiscErrorException("Index out of bounds.",
                    new IndexOutOfBoundsException("" + index));
        }

        if (index == (mySize - 1)) {
            result = myFields.get(1);
        }
        else {
            if (mySize == 2) {
                // ASSERT: !(myElements.get(0) instanceof MTCartesian)
                if (index != 0) {
                    throw new IndexOutOfBoundsException("" + index);
                }

                result = myFields.get(0);
            }
            else {
                // ASSERT: myElements.get(0) instanceof MTCartesian
                result = ((TupleExp) myFields.get(0)).getField(index);
            }
        }

        return result;
    }

    /**
     * <p>
     * This method returns all the inner field expressions.
     * </p>
     *
     * @return A list containing all the segmented {@link Exp}s.
     */
    public final List<Exp> getFields() {
        return myFields;
    }

    /**
     * <p>
     * This method returns the number of field elements in this tuple.
     * </p>
     *
     * @return The size of this {@link TupleExp}.
     */
    public final int getSize() {
        return mySize;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final List<Exp> getSubExpressions() {
        return copyExps();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        int result = super.hashCode();
        result = 31 * result + myFields.hashCode();
        result = 31 * result + mySize;
        return result;
    }

    /**
     * <p>
     * This method checks to see if all the field expressions inside this tuple
     * expression are
     * universally quantified.
     * </p>
     *
     * @return {@code true} if all {@link Exp}s are universally quantified,
     *         {@code false} otherwise.
     */
    public final boolean isUniversallyQuantified() {
        boolean soFar = true;

        for (Exp field : myFields) {
            soFar = soFar && ((field instanceof VarExp && ((VarExp) field)
                    .getQuantification() == SymbolTableEntry.Quantification.UNIVERSAL)
                    || (field instanceof TupleExp
                            && ((TupleExp) field).isUniversallyQuantified()));
        }

        return soFar;
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * {@inheritDoc}
     */
    @Override
    protected final Exp copy() {
        return new TupleExp(cloneLocation(), copyExps());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final Exp
            substituteChildren(java.util.Map<Exp, Exp> substitutions) {
        List<Exp> newFields = new ArrayList<>();
        for (Exp f : myFields) {
            newFields.add(substitute(f, substitutions));
        }

        return new TupleExp(cloneLocation(), newFields);
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>
     * This is a helper method that makes a copy of the list containing all the
     * field expressions.
     * </p>
     *
     * @return A list containing {@link Exp}s.
     */
    private List<Exp> copyExps() {
        List<Exp> copyFieldExps = new ArrayList<>();
        for (Exp exp : myFields) {
            copyFieldExps.add(exp.clone());
        }

        return copyFieldExps;
    }
}

/*
 * AbstractTypeRepresentationDec.java
 * ---------------------------------
 * Copyright (c) 2019
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.absyn.declarations.typedecl;

import edu.clemson.cs.rsrg.absyn.clauses.AssertionClause;
import edu.clemson.cs.rsrg.absyn.declarations.Dec;
import edu.clemson.cs.rsrg.absyn.rawtypes.RecordTy;
import edu.clemson.cs.rsrg.absyn.rawtypes.Ty;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import edu.clemson.cs.rsrg.statushandling.exception.MiscErrorException;

/**
 * <p>This is the abstract base class for both the type representation and
 * facility type representation objects that the compiler builds
 * using the ANTLR4 AST nodes.</p>
 *
 * @version 1.0
 */
public abstract class AbstractTypeRepresentationDec extends Dec {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The raw type for the new type.</p> */
    protected final Ty myTy;

    /** <p>The convention clause for the new type.</p> */
    protected final AssertionClause myConvention;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>An helper constructor that allow us to store the name,
     * raw type and convention for any objects created from a class
     * that inherits from {@code AbstractTypeRepresentationDec}.</p>
     *
     * @param name Name of the new type.
     * @param ty Raw type used to implement this new type.
     * @param convention Type convention.
     */
    protected AbstractTypeRepresentationDec(PosSymbol name, Ty ty,
            AssertionClause convention) {
        super(name.getLocation(), name);
        myConvention = convention;
        myTy = ty;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;

        AbstractTypeRepresentationDec that = (AbstractTypeRepresentationDec) o;

        if (!myTy.equals(that.myTy))
            return false;
        return myConvention.equals(that.myConvention);
    }

    /**
     * <p>Returns the convention for this type representation.</p>
     *
     * @return The type convention in {@link AssertionClause} format.
     */
    public final AssertionClause getConvention() {
        return myConvention;
    }

    /**
     * <p>Returns the raw type model representation
     * used to implement this type.</p>
     *
     * @return The raw type in {@link Ty} format.
     */
    public final Ty getRepresentation() {
        return myTy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + myTy.hashCode();
        result = 31 * result + myConvention.hashCode();
        return result;
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * <p>Implemented by concrete subclasses of {@link AbstractTypeRepresentationDec}
     * to manufacture a copy of themselves.</p>
     *
     * @return A new {@link AbstractTypeRepresentationDec} that is a
     * deep copy of the original.
     */
    protected AbstractTypeRepresentationDec copy() {
        throw new MiscErrorException("Shouldn't be calling copy()!  Type: "
                + this.getClass(), new CloneNotSupportedException());
    }

    /**
     * <p>A helper method to form the string with type
     * the module's uses items.</p>
     *
     * @param indentSize The base indentation to the first line
     *                   of the text.
     * @param innerIndentInc The additional indentation increment
     *                       for the subsequent lines.
     *
     * @return A formatted text string.
     */
    protected final String formRepresentationTy(int indentSize,
            int innerIndentInc) {
        StringBuffer sb = new StringBuffer();
        if (myTy instanceof RecordTy) {
            sb.append(" is represented by Record\n");
            sb.append(myTy
                    .asString(indentSize + innerIndentInc, innerIndentInc));
        }
        else {
            sb.append(" = ");
            sb.append(myTy.asString(0, innerIndentInc));
        }

        return sb.toString();
    }

}
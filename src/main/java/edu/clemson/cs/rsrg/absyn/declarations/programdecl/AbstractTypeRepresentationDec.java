/**
 * AbstractTypeRepresentationDec.java
 * ---------------------------------
 * Copyright (c) 2016
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.absyn.declarations.programdecl;

import edu.clemson.cs.rsrg.absyn.blocks.code.TypeInitFinalItem;
import edu.clemson.cs.rsrg.absyn.clauses.AssertionClause;
import edu.clemson.cs.rsrg.absyn.declarations.Dec;
import edu.clemson.cs.rsrg.absyn.rawtypes.Ty;
import edu.clemson.cs.rsrg.errorhandling.exception.MiscErrorException;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;

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

    /** <p>The initialization block for the new type.</p> */
    protected final TypeInitFinalItem myTypeInitItem;

    /** <p>The finalization block for the new type.</p> */
    protected final TypeInitFinalItem myTypeFinalItem;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>An helper constructor that allow us to store the name,
     * raw type, convention, initialization and finalization blocks
     * for any objects created from a class that inherits from
     * {@code AbstractTypeRepresentationDec}.</p>
     *
     * @param name Name of the new type.
     * @param ty Raw type used to implement this new type.
     * @param convention Type convention.
     * @param initItem Initialization block for this new type.
     * @param finalItem Finalization block for this new type.
     */
    protected AbstractTypeRepresentationDec(PosSymbol name, Ty ty,
            AssertionClause convention, TypeInitFinalItem initItem,
            TypeInitFinalItem finalItem) {
        super(name.getLocation(), name);
        myConvention = convention;
        myTy = ty;
        myTypeFinalItem = finalItem;
        myTypeInitItem = initItem;
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
        if (!myConvention.equals(that.myConvention))
            return false;
        if (!myTypeInitItem.equals(that.myTypeInitItem))
            return false;
        return myTypeFinalItem.equals(that.myTypeFinalItem);

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
     * <p>Returns the finalization block for this type representation.</p>
     *
     * @return The code block used for finalization
     * in {@link TypeInitFinalItem} format.
     */
    public final TypeInitFinalItem getTypeFinalItem() {
        return myTypeFinalItem;
    }

    /**
     * <p>Returns the initialization block for this type representation.</p>
     *
     * @return The code block used for initialization
     * in {@link TypeInitFinalItem} format.
     */
    public final TypeInitFinalItem getTypeInitItem() {
        return myTypeInitItem;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + myTy.hashCode();
        result = 31 * result + myConvention.hashCode();
        result = 31 * result + myTypeInitItem.hashCode();
        result = 31 * result + myTypeFinalItem.hashCode();
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

}
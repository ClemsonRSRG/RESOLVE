/**
 * TypeRepresentationDec.java
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
package edu.clemson.cs.rsrg.absyn.declarations.typedecl;

import edu.clemson.cs.rsrg.absyn.items.programitems.TypeInitFinalItem;
import edu.clemson.cs.rsrg.absyn.clauses.AssertionClause;
import edu.clemson.cs.rsrg.absyn.rawtypes.Ty;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;

/**
 * <p>This is the class for all the type representation declaration objects
 * that the compiler builds using the ANTLR4 AST nodes.</p>
 *
 * @version 2.0
 */
public class TypeRepresentationDec extends AbstractTypeRepresentationDec {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The correspondence clause for the new type.</p> */
    private final AssertionClause myCorrespondence;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs a type representation declaration.</p>
     *
     * @param name Name of the new type.
     * @param ty Raw type used to implement this new type.
     * @param convention Type convention.
     * @param correspondence Type correspondence.
     * @param initItem Initialization block for this new type.
     * @param finalItem Finalization block for this new type.
     */
    public TypeRepresentationDec(PosSymbol name, Ty ty,
            AssertionClause convention, AssertionClause correspondence,
            TypeInitFinalItem initItem, TypeInitFinalItem finalItem) {
        super(name, ty, convention, initItem, finalItem);
        myCorrespondence = correspondence;
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
        sb.append("Type ");
        sb.append(myName.asString(0, innerIndentInc));
        sb.append(" = ");
        sb.append(myTy.asString(0, innerIndentInc));

        // convention
        sb.append(myConvention.asString(indentSize + innerIndentInc,
                innerIndentInc));

        // correspondence
        sb.append(myCorrespondence.asString(indentSize + innerIndentInc,
                innerIndentInc));

        // initialization/finalization
        sb.append(myTypeInitItem.asString(indentSize + innerIndentInc,
                innerIndentInc));
        sb.append(myTypeFinalItem.asString(indentSize + innerIndentInc,
                innerIndentInc));

        sb.append("end\n");

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

        TypeRepresentationDec that = (TypeRepresentationDec) o;

        return myCorrespondence.equals(that.myCorrespondence);

    }

    /**
     * <p>Returns the correspondence for this type representation.</p>
     *
     * @return The type correspondence in {@link AssertionClause} format.
     */
    public final AssertionClause getCorrespondence() {
        return myCorrespondence;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        int result = super.hashCode();
        result = 31 * result + myCorrespondence.hashCode();
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Type ");
        sb.append(myName.toString());
        sb.append(" = ");
        sb.append(myTy.toString());

        // convention
        sb.append("\t");
        sb.append(myConvention.toString());

        // correspondence
        sb.append("\t");
        sb.append(myCorrespondence.toString());

        // initialization/finalization
        sb.append("\t");
        sb.append(myTypeInitItem.toString());
        sb.append("\t");
        sb.append(myTypeFinalItem.toString());

        sb.append("end\n");

        return sb.toString();
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * {@inheritDoc}
     */
    @Override
    protected final TypeRepresentationDec copy() {
        return new TypeRepresentationDec(myName.clone(), myTy.clone(),
                myConvention.clone(), myCorrespondence.clone(), myTypeInitItem
                        .clone(), myTypeFinalItem.clone());
    }

}
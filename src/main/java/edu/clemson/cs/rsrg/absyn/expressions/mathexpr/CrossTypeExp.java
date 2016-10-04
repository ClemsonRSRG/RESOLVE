/**
 * CrossTypeExp.java
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
package edu.clemson.cs.rsrg.absyn.expressions.mathexpr;

import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.absyn.rawtypes.ArbitraryExpTy;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import java.util.*;

/**
 * <p>This is the class for all the mathematical cross type expression objects
 * that the compiler builds using the ANTLR4 AST nodes.</p>
 *
 * <p>The value of a type that looks like:</p>
 *
 * <pre>
 * CART_PROD
 *     X1 : T1;
 *     X2 : T2;
 *     ...
 *     Xn : Tn;
 * END;
 * </pre>
 *
 * @version 2.0
 */
public class CrossTypeExp extends MathExp {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>A map from names to raw types.</p> */
    private final Map<PosSymbol, ArbitraryExpTy> myTagsToFields;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs a mathematical cross type expression.</p>
     *
     * @param l A {@link Location} representation object.
     * @param tagsToFieldsMap A map containing all the tags to fields mapping.
     */
    public CrossTypeExp(Location l,
            Map<PosSymbol, ArbitraryExpTy> tagsToFieldsMap) {
        super(l);
        myTagsToFields = tagsToFieldsMap;
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
        sb.append("Cart_Prod\n");

        Set<PosSymbol> tags = myTagsToFields.keySet();
        for (PosSymbol tag : tags) {
            sb
                    .append(tag.asString(indentSize + innerIndentInc,
                            innerIndentInc));
            sb.append(": ");
            sb.append(myTagsToFields.get(tag).asString(0, innerIndentInc));
            sb.append("\n");
        }

        printSpace(indentSize, sb);
        sb.append("end\n");

        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean containsExp(Exp exp) {
        boolean found = false;

        Set<PosSymbol> tags = myTagsToFields.keySet();
        Iterator<PosSymbol> it = tags.iterator();
        while (it.hasNext() && !found) {
            ArbitraryExpTy fieldTy = myTagsToFields.get(it.next());
            Exp fieldExp = fieldTy.getArbitraryExp();
            found = fieldExp.containsExp(exp);
        }

        return found;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean containsVar(String varName, boolean IsOldExp) {
        boolean found = false;

        Set<PosSymbol> tags = myTagsToFields.keySet();
        Iterator<PosSymbol> it = tags.iterator();
        while (it.hasNext() && !found) {
            ArbitraryExpTy fieldTy = myTagsToFields.get(it.next());
            Exp fieldExp = fieldTy.getArbitraryExp();
            found = fieldExp.containsVar(varName, IsOldExp);
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

        CrossTypeExp that = (CrossTypeExp) o;

        return myTagsToFields.equals(that.myTagsToFields);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean equivalent(Exp e) {
        boolean result = e instanceof CrossTypeExp;

        if (result) {
            CrossTypeExp eAsCrossTypeExp = (CrossTypeExp) e;

            Set<PosSymbol> tags = myTagsToFields.keySet();
            Set<PosSymbol> eTags = eAsCrossTypeExp.myTagsToFields.keySet();
            result = tags.equals(eTags);

            Iterator<PosSymbol> thisTagsIt = tags.iterator();
            Iterator<PosSymbol> eTagsIt = eTags.iterator();
            while (result && thisTagsIt.hasNext() && eTagsIt.hasNext()) {
                ArbitraryExpTy thisTy = myTagsToFields.get(thisTagsIt.next());
                ArbitraryExpTy eTy =
                        eAsCrossTypeExp.myTagsToFields.get(eTagsIt.next());
                result &=
                        thisTy.getArbitraryExp().equivalent(
                                eTy.getArbitraryExp());
            }

            //Both had better have run out at the same time
            result &= (!thisTagsIt.hasNext()) && (!eTagsIt.hasNext());
        }

        return result;
    }

    /**
     * <p>This returns the number of elements in this cross type
     * expression.</p>
     *
     * @return The size of the map.
     */
    public final int getFieldCount() {
        return myTagsToFields.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final List<Exp> getSubExpressions() {
        return new ArrayList<>();
    }

    /**
     * <p>This returns the tag/field pairs in this cross type
     * expression.</p>
     *
     * @return The {@link Map} containing the tags to field pairs.
     */
    public final Map<PosSymbol, ArbitraryExpTy> getTagsToFieldsMap() {
        return myTagsToFields;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        int result = super.hashCode();
        result = 31 * result + myTagsToFields.hashCode();
        return result;
    }

    /**
     * <p>This method applies VC Generator's remember rule.
     * For all inherited programming expression classes, this method
     * should throw an exception.</p>
     *
     * @return The resulting {@link CrossTypeExp} from applying the remember rule.
     */
    @Override
    public final CrossTypeExp remember() {
        throw new UnsupportedOperationException(
                "Remember rule application is currently not supported.");
    }

    /**
     * <p>This method applies the VC Generator's simplification step.</p>
     *
     * @return The resulting {@link MathExp} from applying the simplification step.
     */
    @Override
    public final MathExp simplify() {
        throw new UnsupportedOperationException(
                "Remember rule application is currently not supported.");
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * {@inheritDoc}
     */
    @Override
    protected final Exp copy() {
        Map<PosSymbol, ArbitraryExpTy> newTagsToFields = new HashMap<>();
        Set<PosSymbol> tags = myTagsToFields.keySet();
        for (PosSymbol tag : tags) {
            PosSymbol newTag = tag.clone();
            ArbitraryExpTy field = myTagsToFields.get(tag);

            newTagsToFields.put(newTag, (ArbitraryExpTy) field.clone());
        }

        return new CrossTypeExp(cloneLocation(), newTagsToFields);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final Exp substituteChildren(Map<Exp, Exp> substitutions) {
        throw new UnsupportedOperationException(
                "Cannot substitute in a cross type.");
    }

}
/*
 * EnhancementSpecRealizItemMap.java
 * ---------------------------------
 * Copyright (c) 2017
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.vcgeneration.utilities.formaltoactual;

import edu.clemson.cs.rsrg.absyn.declarations.facilitydecl.FacilityDec;
import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.absyn.items.programitems.EnhancementSpecRealizItem;
import java.util.Map;

/**
 * <p>This class stores mapping for an {@link EnhancementSpecRealizItem EnhancementSpecRealizItem's}
 * formal parameters in the specifications/implementations to their actual arguments in the
 * {@code Facility} instantiation.</p>
 *
 * @author Yu-Shan Sun
 * @version 1.0
 */
public class EnhancementSpecRealizItemMap {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>The {@code Enhancement} and {@code Enhancement Realization}
     * instantiation from a {@link FacilityDec}.</p>
     */
    private final EnhancementSpecRealizItem myEnhancementSpecRealizItem;

    /**
     * <p>This maps all {@code Enhancement} formal arguments to the instantiated
     * actual arguments.</p>
     */
    private final Map<Exp, Exp> myEnhancementArgMap;

    /**
     * <p>This maps all {@code Enhancement Realization} formal arguments to the instantiated
     * actual arguments.</p>
     */
    private final Map<Exp, Exp> myEnhancementRealizArgMap;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This creates an object that stores the various pieces of
     * information related to the instantiated {@code Facility}'s
     * {@code Enhancement} and {@code Enhancement Realization}.</p>
     *
     * @param enhancementSpecRealizItem The {@link EnhancementSpecRealizItem} from
     *                                  the instantiated {@code Facility}.
     * @param eArgMap Argument mapping for the instantiating {@code Enhancement}.
     * @param erArgMap Argument mapping for the instantiating {@code Enhancement Realization}.
     */
    public EnhancementSpecRealizItemMap(
            EnhancementSpecRealizItem enhancementSpecRealizItem,
            Map<Exp, Exp> eArgMap, Map<Exp, Exp> erArgMap) {
        myEnhancementSpecRealizItem = enhancementSpecRealizItem;
        myEnhancementArgMap = eArgMap;
        myEnhancementRealizArgMap = erArgMap;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This method overrides the default {@code equals} method implementation.</p>
     *
     * @param o Object to be compared.
     *
     * @return {@code true} if all the fields are equal, {@code false} otherwise.
     */
    @Override
    public final boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        EnhancementSpecRealizItemMap that = (EnhancementSpecRealizItemMap) o;

        if (!myEnhancementSpecRealizItem
                .equals(that.myEnhancementSpecRealizItem))
            return false;
        if (!myEnhancementArgMap.equals(that.myEnhancementArgMap))
            return false;
        return myEnhancementRealizArgMap.equals(that.myEnhancementRealizArgMap);
    }

    /**
     * <p>This method returns a map containing the {@code Enhancement's}
     * formal to actual arguments for the instantiated {@code Facility}.</p>
     *
     * @return A {@link Map} containing the formal to actual mapping.
     */
    public final Map<Exp, Exp> getEnhacementArgMap() {
        return myEnhancementArgMap;
    }

    /**
     * <p>This method returns a map containing the {@code Enhancement Realization's}
     * formal to actual arguments for the instantiated {@code Facility}.</p>
     *
     * @return A {@link Map} containing the formal to actual mapping.
     */
    public final Map<Exp, Exp> getEnhancementRealizArgMap() {
        return myEnhancementRealizArgMap;
    }

    /**
     * <p>This method returns the {@link EnhancementSpecRealizItem} from
     * the instantiated {@code Facility} declaration.</p>
     *
     * @return A {@link EnhancementSpecRealizItem}.
     */
    public final EnhancementSpecRealizItem getEnhancementSpecRealizItem() {
        return myEnhancementSpecRealizItem;
    }

    /**
     * <p>This method overrides the default {@code hashCode} method implementation.</p>
     *
     * @return The hash code associated with the object.
     */
    @Override
    public final int hashCode() {
        int result = myEnhancementSpecRealizItem.hashCode();
        result = 31 * result + myEnhancementArgMap.hashCode();
        result = 31 * result + myEnhancementRealizArgMap.hashCode();
        return result;
    }

}
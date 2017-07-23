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
package edu.clemson.cs.rsrg.vcgeneration.utilities;

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

}
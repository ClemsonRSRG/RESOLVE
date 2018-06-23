/*
 * SpecRealizationPairing.java
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
package edu.clemson.cs.rsrg.typeandpopulate.utilities;

import edu.clemson.cs.rsrg.absyn.declarations.moduledecl.*;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.typeandpopulate.exception.NoneProvidedException;

/**
 * <p>A <code>SpecRealizationPairing</code> pairs both a specification
 * and a realization together to indicate that the realization implements
 * the given specification.</p>
 *
 * @version 2.0
 */
public class SpecRealizationPairing {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The location that created this object.</p> */
    private final Location myLocation;

    /** <p>Specification parameter object.</p> */
    private final ModuleParameterization mySpec;

    /** <p>Realization parameter object.</p> */
    private final ModuleParameterization myRealization;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs a specification with no realization.</p>
     *
     * @param l A {@link Location} representation object.
     * @param spec Specification parameter object.
     */
    public SpecRealizationPairing(Location l, ModuleParameterization spec) {
        this(l, spec, null);
    }

    /**
     * <p>This constructs a specification/realization pairing.</p>
     *
     * @param l A {@link Location} representation object.
     * @param spec Specification parameter object.
     * @param realization Realization parameter object.
     */
    public SpecRealizationPairing(Location l, ModuleParameterization spec,
            ModuleParameterization realization) {
        if (spec == null) {
            throw new IllegalArgumentException("Null spec!");
        }

        myLocation = l;
        mySpec = spec;
        myRealization = realization;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This method returns the realization module parameter.</p>
     *
     * @return A {@link ModuleParameterization} object that refers either
     * to a {@link ConceptRealizModuleDec} or a {@link EnhancementRealizModuleDec}.
     */
    public final ModuleParameterization getRealization()
            throws NoneProvidedException {
        if (myRealization == null) {
            throw new NoneProvidedException("Null realization!", myLocation);
        }

        return myRealization;
    }

    /**
     * <p>This method returns the specification module parameter.</p>
     *
     * @return A {@link ModuleParameterization} object that refers either
     * to a {@link ConceptModuleDec} or a {@link EnhancementModuleDec}.
     */
    public final ModuleParameterization getSpecification() {
        return mySpec;
    }

}
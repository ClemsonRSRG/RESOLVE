/*
 * ShortFacilityModuleDec.java
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
package edu.clemson.rsrg.absyn.declarations.moduledecl;

import edu.clemson.rsrg.absyn.declarations.Dec;
import edu.clemson.rsrg.absyn.declarations.facilitydecl.FacilityDec;
import edu.clemson.rsrg.absyn.declarations.paramdecl.ModuleParameterDec;
import edu.clemson.rsrg.absyn.items.programitems.UsesItem;
import edu.clemson.rsrg.init.file.ResolveFileBasicInfo;
import edu.clemson.rsrg.parsing.data.Location;
import edu.clemson.rsrg.parsing.data.PosSymbol;
import java.util.*;

/**
 * <p>
 * This is the class for the short facility module declarations that the compiler builds using the ANTLR4 AST nodes.
 * </p>
 *
 * @version 2.0
 */
public class ShortFacilityModuleDec extends ModuleDec {

    // ===========================================================
    // Constructor
    // ===========================================================

    /**
     * <p>
     * This constructor creates a short facility module representation.
     * </p>
     *
     * @param l
     *            A {@link Location} representation object.
     * @param name
     *            The name in {@link PosSymbol} format.
     * @param facilityDec
     *            A {@link FacilityDec} representation object.
     * @param moduleDependencies
     *            A map of {@link ResolveFileBasicInfo} to externally realized flags that indicates all the modules that
     *            this module declaration depends on.
     */
    public ShortFacilityModuleDec(Location l, PosSymbol name, FacilityDec facilityDec,
            Map<ResolveFileBasicInfo, Boolean> moduleDependencies) {
        super(l, name, new ArrayList<ModuleParameterDec>(), new ArrayList<UsesItem>(),
                new ArrayList<Dec>(Collections.singletonList(facilityDec)), moduleDependencies);
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

        FacilityDec dec = (FacilityDec) myDecs.get(0);
        sb.append(dec.asString(indentSize, innerIndentInc));
        sb.append("\n");

        return sb.toString();
    }

    /**
     * <p>
     * Returns the facility declaration.
     * </p>
     *
     * @return A {@link FacilityDec} representation object.
     */
    public final FacilityDec getDec() {
        return (FacilityDec) myDecs.get(0);
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * {@inheritDoc}
     */
    @Override
    protected final ShortFacilityModuleDec copy() {
        Map<ResolveFileBasicInfo, Boolean> newModuleDependencies = copyModuleDependencies();

        return new ShortFacilityModuleDec(cloneLocation(), myName.clone(), (FacilityDec) myDecs.get(0).clone(),
                newModuleDependencies);
    }
}

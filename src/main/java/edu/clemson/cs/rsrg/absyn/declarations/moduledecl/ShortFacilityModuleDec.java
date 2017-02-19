/**
 * ShortFacilityModuleDec.java
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
package edu.clemson.cs.rsrg.absyn.declarations.moduledecl;

import edu.clemson.cs.rsrg.absyn.declarations.Dec;
import edu.clemson.cs.rsrg.absyn.declarations.facilitydecl.FacilityDec;
import edu.clemson.cs.rsrg.absyn.declarations.paramdecl.ModuleParameterDec;
import edu.clemson.cs.rsrg.absyn.items.programitems.UsesItem;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * <p>This is the class for the short facility module declarations
 * that the compiler builds using the ANTLR4 AST nodes.</p>
 *
 * @version 2.0
 */
public class ShortFacilityModuleDec extends ModuleDec {

    // ===========================================================
    // Constructor
    // ===========================================================

    /**
     * <p>This constructor creates a short facility module representation.</p>
     *
     * @param l A {@link Location} representation object.
     * @param name The name in {@link PosSymbol} format.
     * @param facilityDec A {@link FacilityDec} representation object.
     * @param moduleDependencyList A list of {@link PosSymbol} that indicates
     *                             all the modules that this module declaration
     *                             depends on.
     */
    public ShortFacilityModuleDec(Location l, PosSymbol name,
            FacilityDec facilityDec, List<PosSymbol> moduleDependencyList) {
        super(l, name, new ArrayList<ModuleParameterDec>(),
                new ArrayList<UsesItem>(), new ArrayList<Dec>(Arrays
                        .asList(facilityDec)), moduleDependencyList);
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
     * <p>Returns the facility declaration.</p>
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
        List<PosSymbol> newModuleDependencies = new ArrayList<>(myModuleDependencyList.size());
        Collections.copy(newModuleDependencies, myModuleDependencyList);

        return new ShortFacilityModuleDec(cloneLocation(), myName.clone(),
                (FacilityDec) myDecs.get(0).clone(), newModuleDependencies);
    }
}
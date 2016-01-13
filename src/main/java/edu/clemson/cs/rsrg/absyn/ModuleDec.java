/**
 * ModuleDec.java
 * ---------------------------------
 * Copyright (c) 2015
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.absyn;

import edu.clemson.cs.rsrg.absyn.modules.parameters.ModuleParameterDec;
import edu.clemson.cs.rsrg.absyn.items.UsesItem;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>This is the abstract base class for all the module declaration type
 * intermediate objects that the compiler builds from the ANTLR4 AST tree.</p>
 *
 * @version 2.0
 */
public abstract class ModuleDec extends Dec {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The current module's declaration objects.</p> */
    protected final List<Dec> myDecs = new ArrayList<>();

    /** <p>The current module's parameter declaration objects.</p> */
    protected final List<ModuleParameterDec> myParameterDecs = new ArrayList<>();

    /** <p>The current module's import objects.</p> */
    protected final List<UsesItem> myUsesItems = new ArrayList<>();

    // ===========================================================
    // Constructor
    // ===========================================================

    /**
     * <p>A helper constructor that allow us to store the parameter
     * uses and general declarations of the created object directly
     * in the this class.</p>
     *
     * @param l A {@link Location} representation object.
     * @param name The name in {@link PosSymbol} format.
     * @param parameterDecs The list of {@link ModuleParameterDec} objects.
     * @param usesItems The list of {@link UsesItem} objects.
     * @param decs The list of {@link Dec} objects.
     */
    protected ModuleDec(Location l, PosSymbol name, List<ModuleParameterDec> parameterDecs, List<UsesItem> usesItems,
            List<Dec> decs) {
        super(l, name);
        myParameterDecs.addAll(parameterDecs);
        myUsesItems.addAll(usesItems);
        myDecs.addAll(decs);
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This method must be implemented by all inherited classes
     * to override the default clone method implementation.</p>
     *
     * @return A deep copy of the object.
     */
    @Override
    public abstract ModuleDec clone();

    /**
     * <p>This method gets all the object declarations associated
     * with this module.</p>
     *
     * @return A list of {link Dec} objects.
     */
    public List<Dec> getDecList() {
        return myDecs;
    }

    /**
     * <p>This method gets all the object parameter declarations associated
     * with this module.</p>
     *
     * @return A list of {@link ModuleParameterDec} objects.
     */
    public List<ModuleParameterDec> getParameterDecs() {
        return myParameterDecs;
    }

    /**
     * <p>This method gets all the import objects associated
     * with this module.</p>
     *
     * @return A list of {@link UsesItem} objects.
     */
    public List<UsesItem> getUsesItems() {
        return myUsesItems;
    }

}
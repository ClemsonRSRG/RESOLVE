/**
 * ModuleDec.java
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
import edu.clemson.cs.rsrg.absyn.declarations.paramdecl.ModuleParameterDec;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>This is the abstract base class for all the module declaration objects
 * that the compiler builds using the ANTLR4 AST nodes.</p>
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
     * <p>A helper constructor that allow us to store common member fields
     * for objects created from a class that inherits from
     * {@code ModuleDec}.</p>
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
     * <p>This method overrides the default clone method implementation
     * for all the classes that extend from {@link ModuleDec}.</p>
     *
     * @return A deep copy of the object.
     */
    @Override
    public abstract ModuleDec clone();

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        ModuleDec moduleDec = (ModuleDec) o;

        if (!myDecs.equals(moduleDec.myDecs)) return false;
        if (!myParameterDecs.equals(moduleDec.myParameterDecs)) return false;
        return myUsesItems.equals(moduleDec.myUsesItems);

    }

    /**
     * <p>This method gets all the object declarations associated
     * with this module.</p>
     *
     * @return A list of {@link Dec} objects.
     */
    public final List<Dec> getDecList() {
        return myDecs;
    }

    /**
     * <p>This method gets all the object parameter declarations associated
     * with this module.</p>
     *
     * @return A list of {@link ModuleParameterDec} objects.
     */
    public final List<ModuleParameterDec> getParameterDecs() {
        return myParameterDecs;
    }

    /**
     * <p>This method gets all the import objects associated
     * with this module.</p>
     *
     * @return A list of {@link UsesItem} objects.
     */
    public final List<UsesItem> getUsesItems() {
        return myUsesItems;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + myDecs.hashCode();
        result = 31 * result + myParameterDecs.hashCode();
        result = 31 * result + myUsesItems.hashCode();
        return result;
    }

}
/**
 * MathSymbolTableBuilder.java
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
package edu.clemson.cs.rsrg.typeandpopulate.symboltables;

import edu.clemson.cs.rsrg.absyn.ResolveConceptualElement;
import edu.clemson.cs.rsrg.typeandpopulate.ModuleIdentifier;
import edu.clemson.cs.rsrg.typeandpopulate.exception.NoSuchSymbolException;
import edu.clemson.cs.rsrg.typeandpopulate.typereasoning.TypeGraph;

/**
 * TODO: Refactor this class
 */
public class MathSymbolTableBuilder extends ScopeRepository {

    /**
     * <p>Returns the {@link ModuleScope} associated with the given
     * {@link ModuleIdentifier}.</p>
     *
     * @param module The module identifier.
     * @throws NoSuchSymbolException If no scope has been opened for the named
     *                               module.
     * @returns The associated module scope.
     */
    @Override
    public ModuleScope getModuleScope(ModuleIdentifier module)
            throws NoSuchSymbolException {
        return null;
    }

    /**
     * <p>Returns the {@link Scope} introduced and bounded by the given
     * defining element.</p>
     *
     * @param e defining element.
     * @throws NoSuchSymbolException If no scope has been opened for the given
     *                               defining element.
     * @returns The associated scope.
     */
    @Override
    public Scope getScope(ResolveConceptualElement e) {
        return null;
    }

    public TypeGraph getTypeGraph() {
        return null;
    }

}
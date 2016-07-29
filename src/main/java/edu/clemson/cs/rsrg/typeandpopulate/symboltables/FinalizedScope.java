/**
 * FinalizedScope.java
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
import edu.clemson.cs.rsrg.typeandpopulate.utilities.ModuleIdentifier;

/**
 * <p>A <code>FinalizedScope</code> is an immutable realization of
 * {@link Scope}.</p>
 *
 * <p>Note that <code>FinalizedScope</code> has no public constructor.
 * <code>FinalizedScope</code>s are acquired through calls to some of the
 * methods of {@link MathSymbolTable}.</p>
 *
 * @version 2.0
 */
public class FinalizedScope extends SyntacticScope {

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This creates an immutable scope for a {@link ResolveConceptualElement}.</p>
     *
     * @param symbolTable The source scope repository.
     * @param definingElement The element that created this scope.
     * @param parent The parent scope.
     * @param enclosingModule The module identifier for the module
     *                        that this scope belongs to.
     * @param bindings The symbol table bindings.
     */
    FinalizedScope(MathSymbolTable symbolTable,
            ResolveConceptualElement definingElement, Scope parent,
            ModuleIdentifier enclosingModule, BaseSymbolTable bindings) {
        super(symbolTable, definingElement, parent, enclosingModule,
                new BaseSymbolTable(bindings));
    }

}
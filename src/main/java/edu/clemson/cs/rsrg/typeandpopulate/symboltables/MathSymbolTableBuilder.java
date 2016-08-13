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
import edu.clemson.cs.rsrg.absyn.declarations.moduledecl.ModuleDec;
import edu.clemson.cs.rsrg.typeandpopulate.exception.NoSuchScopeException;
import edu.clemson.cs.rsrg.typeandpopulate.exception.NoSuchSymbolException;
import edu.clemson.cs.rsrg.typeandpopulate.typereasoning.TypeGraph;
import edu.clemson.cs.rsrg.typeandpopulate.utilities.ModuleIdentifier;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * <p>A <code>MathSymbolTableBuilder</code> is a factory for producing immutable
 * {@link MathSymbolTable MathSymbolTables}. It's behavior directly mirrors
 * <code>MathSymbolTable</code>, so that it can be used as a working symbol
 * table while it is built.</p>
 *
 * <p>Once the building process is complete, {@link #seal()} should
 * be called to return a <code>MathSymbolTable</code> that is equivalent to
 * the working symbol table represented by this
 * <code>MathSymbolTableBuilder</code>.</p>
 *
 * @version 2.0
 */
public class MathSymbolTableBuilder extends ScopeRepository {

    // ===========================================================
    // Member Fields
    // ===========================================================

    private static final Scope DUMMY_RESOLVER = new DummyIdentifierResolver();

    private final Deque<ScopeBuilder> myLexicalScopeStack =
            new LinkedList<>();

    private final Map<ResolveConceptualElement, ScopeBuilder> myScopes =
            new HashMap<>();

    private final Map<ModuleIdentifier, ModuleScopeBuilder> myModuleScopes =
            new HashMap<>();

    private ModuleScopeBuilder myCurModuleScope = null;

    private final TypeGraph myTypeGraph;

    // ===========================================================
    // Constructors
    // ===========================================================

    // ===========================================================
    // Public Methods
    // ===========================================================

    public final ScopeBuilder endScope() {
        return null;
    }

    /**
     * <p>Returns the {@link ModuleScope} associated with the given
     * {@link ModuleIdentifier}.</p>
     *
     * @param module The module identifier.
     *
     * @return The associated module scope.
     *
     * @throws NoSuchSymbolException If no scope has been opened for
     * the named module.
     */
    @Override
    public final ModuleScope getModuleScope(ModuleIdentifier module)
            throws NoSuchSymbolException {
        return null;
    }

    /**
     * <p>Returns the {@link Scope} introduced and bounded by the given
     * defining element.</p>
     *
     * @param e defining element.
     *
     * @return The associated scope.
     *
     * @throws NoSuchScopeException If no scope has been opened for
     * the given defining element.
     */
    @Override
    public final Scope getScope(ResolveConceptualElement e)
            throws NoSuchScopeException {
        return null;
    }

    /**
     * <p>Returns the {@link TypeGraph} that relates the types found in this
     * <code>MathSymbolTable</code>.</p>
     *
     * @return The {@link TypeGraph} object.
     */
    @Override
    public final TypeGraph getTypeGraph() {
        return null;
    }

    public final ModuleScopeBuilder startModuleScope(ModuleDec definingElement) {
        return null;
    }

}
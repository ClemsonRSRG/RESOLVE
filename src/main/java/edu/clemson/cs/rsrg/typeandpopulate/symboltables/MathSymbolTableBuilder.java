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
import edu.clemson.cs.rsrg.typeandpopulate.utilities.HardCoded;
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

    /** <p>A scope for built-in objects.</p> */
    private static final Scope DUMMY_RESOLVER = new DummyIdentifierResolver();

    /** <p>A list of current open scopes.</p> */
    private final Deque<ScopeBuilder> myLexicalScopeStack =
            new LinkedList<>();

    /** <p>A map of non-module scope builders.</p> */
    private final Map<ResolveConceptualElement, ScopeBuilder> myScopes =
            new HashMap<>();

    /** <p>A map of module scope builders.</p> */
    private final Map<ModuleIdentifier, ModuleScopeBuilder> myModuleScopes =
            new HashMap<>();

    /** <p>The current module scope.</p> */
    private ModuleScopeBuilder myCurModuleScope = null;

    /** <p>The current type graph.</p> */
    private final TypeGraph myTypeGraph;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This creates a new, empty <code>MathSymbolTableBuilder</code> with no
     * open scopes.</p>
     */
    public MathSymbolTableBuilder() {
        myTypeGraph = new TypeGraph();

        //The only things in global scope are built-in things
        ScopeBuilder globalScope =
                new ScopeBuilder(this, myTypeGraph, null, DUMMY_RESOLVER,
                        ModuleIdentifier.GLOBAL);

        HardCoded.addBuiltInSymbols(myTypeGraph, globalScope);

        myLexicalScopeStack.push(globalScope);

        //Some IDEs (rightly) complain about leaking a "this" pointer inside the
        //constructor, but we know what we're doing--this is the last thing in
        //the constructor and thus the object is fully initialized.  The weird
        //intermediate variable suppresses the warning
        MathSymbolTableBuilder thisObject = this;
        HardCoded.addBuiltInRelationships(myTypeGraph, thisObject);
    }

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
        return myTypeGraph;
    }

    public final ModuleScopeBuilder startModuleScope(ModuleDec definingElement) {
        return null;
    }

}
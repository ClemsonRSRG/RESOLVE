/*
 * MathSymbolTableBuilder.java
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
package edu.clemson.rsrg.typeandpopulate.symboltables;

import edu.clemson.rsrg.absyn.ResolveConceptualElement;
import edu.clemson.rsrg.absyn.declarations.moduledecl.ModuleDec;
import edu.clemson.rsrg.init.CompileEnvironment;
import edu.clemson.rsrg.typeandpopulate.exception.NoSuchModuleException;
import edu.clemson.rsrg.typeandpopulate.exception.NoSuchScopeException;
import edu.clemson.rsrg.typeandpopulate.exception.NoSuchSymbolException;
import edu.clemson.rsrg.typeandpopulate.typereasoning.TypeGraph;
import edu.clemson.rsrg.typeandpopulate.utilities.HardCoded;
import edu.clemson.rsrg.typeandpopulate.utilities.ModuleIdentifier;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * <p>
 * A <code>MathSymbolTableBuilder</code> is a factory for producing immutable {@link MathSymbolTable MathSymbolTables}.
 * It's behavior directly mirrors <code>MathSymbolTable</code>, so that it can be used as a working symbol table while
 * it is built.
 * </p>
 *
 * <p>
 * Once the building process is complete, {@link #seal()} should be called to return a <code>MathSymbolTable</code> that
 * is equivalent to the working symbol table represented by this <code>MathSymbolTableBuilder</code>.
 * </p>
 *
 * @version 2.0
 */
public class MathSymbolTableBuilder extends ScopeRepository {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * A scope for built-in objects.
     * </p>
     */
    private static final Scope DUMMY_RESOLVER = new DummyIdentifierResolver();

    /**
     * <p>
     * A list of current open scopes.
     * </p>
     */
    private final Deque<ScopeBuilder> myLexicalScopeStack = new LinkedList<>();

    /**
     * <p>
     * A map of non-module scope builders.
     * </p>
     */
    private final Map<ResolveConceptualElement, ScopeBuilder> myScopes = new HashMap<>();

    /**
     * <p>
     * A map of module scope builders.
     * </p>
     */
    private final Map<ModuleIdentifier, ModuleScopeBuilder> myModuleScopes = new HashMap<>();

    /**
     * <p>
     * The current module scope.
     * </p>
     */
    private ModuleScopeBuilder myCurModuleScope = null;

    /**
     * <p>
     * The current type graph.
     * </p>
     */
    private final TypeGraph myTypeGraph;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This creates a new, empty <code>MathSymbolTableBuilder</code> with no open scopes.
     * </p>
     *
     * @param compileEnvironment
     *            The current job's compilation environment that stores all necessary objects and flags.
     */
    public MathSymbolTableBuilder(CompileEnvironment compileEnvironment) {
        myTypeGraph = new TypeGraph(compileEnvironment);

        // The only things in global scope are built-in things
        ScopeBuilder globalScope = new ScopeBuilder(this, myTypeGraph, null, DUMMY_RESOLVER, ModuleIdentifier.GLOBAL);

        HardCoded.addBuiltInSymbols(myTypeGraph, globalScope);

        myLexicalScopeStack.push(globalScope);

        // Some IDEs (rightly) complain about leaking a "this" pointer inside the
        // constructor, but we know what we're doing--this is the last thing in
        // the constructor and thus the object is fully initialized. The weird
        // intermediate variable suppresses the warning
        MathSymbolTableBuilder thisObject = this;
        HardCoded.addBuiltInRelationships(myTypeGraph, thisObject);
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * Closes the most recently opened, unclosed working scope, including those opened with
     * {@link #startModuleScope(ModuleDec)}.
     * </p>
     *
     * @return The new innermost active scope after the former one was closed by this call. If the scope that was closed
     *         was the module scope, returns <code>null</code>.
     */
    public final ScopeBuilder endScope() {
        checkScopeOpen();
        myLexicalScopeStack.pop();

        ScopeBuilder result;

        if (myLexicalScopeStack.size() == 1) {
            result = null;
            myCurModuleScope = null;
        } else {
            result = myLexicalScopeStack.peek();
        }

        return result;
    }

    /**
     * <p>
     * Returns the most recently opened, unclosed working scope.
     * </p>
     *
     * @return The most recently opened, unclosed working scope.
     *
     * @throws IllegalStateException
     *             If there are no open scopes.
     */
    public final ScopeBuilder getInnermostActiveScope() {
        checkScopeOpen();
        return myLexicalScopeStack.peek();
    }

    /**
     * <p>
     * Returns the {@link ModuleScope} associated with the given {@link ModuleIdentifier}.
     * </p>
     *
     * @param module
     *            The module identifier.
     *
     * @return The associated module scope.
     *
     * @throws NoSuchSymbolException
     *             If no scope has been opened for the named module.
     */
    @Override
    public final ModuleScope getModuleScope(ModuleIdentifier module) throws NoSuchSymbolException {
        if (!myModuleScopes.containsKey(module)) {
            throw new NoSuchSymbolException("" + module, null);
        }

        return myModuleScopes.get(module);
    }

    /**
     * <p>
     * Returns the {@link Scope} introduced and bounded by the given defining element.
     * </p>
     *
     * @param e
     *            defining element.
     *
     * @return The associated scope.
     *
     * @throws NoSuchScopeException
     *             If no scope has been opened for the given defining element.
     */
    @Override
    public final Scope getScope(ResolveConceptualElement e) throws NoSuchScopeException {
        if (!myScopes.containsKey(e)) {
            throw new NoSuchScopeException(e);
        }

        return myScopes.get(e);
    }

    /**
     * <p>
     * Returns the {@link TypeGraph} that relates the types found in this <code>MathSymbolTable</code>.
     * </p>
     *
     * @return The {@link TypeGraph} object.
     */
    @Override
    public final TypeGraph getTypeGraph() {
        return myTypeGraph;
    }

    /**
     * <p>
     * Returns an immutable snapshot of the working symbol table represented by this <code>MathSymbolTableBuilder</code>
     * as a <code>MathSymbolTable</code>.
     * </p>
     *
     * @return The snapshot.
     *
     * @throws IllegalStateException
     *             If there are any open scopes.
     * @throws NoSuchModuleException
     *             If any module claims to import a module for which there is no associated scope.
     */
    public final MathSymbolTable seal() throws NoSuchModuleException {
        if (myLexicalScopeStack.size() > 1) {
            throw new IllegalStateException("There are open scopes.");
        }

        return new MathSymbolTable(myTypeGraph, myLexicalScopeStack.peek());
    }

    /**
     * <p>
     * Opens a new working module scope defined by the given <code>ModuleDec</code>.
     * </p>
     *
     * @param definingElement
     *            The <code>ModuleDec</code> that defines this scope.
     *
     * @return The newly opened {@link ModuleScopeBuilder}.
     *
     * @throws IllegalStateException
     *             If a module scope is already open.
     * @throws IllegalArgumentException
     *             If <code>definingElement</code> is <code>null</code>.
     */
    public final ModuleScopeBuilder startModuleScope(ModuleDec definingElement) {
        if (definingElement == null) {
            throw new IllegalArgumentException("definingElement may not be " + "null.");
        }

        if (myCurModuleScope != null) {
            throw new IllegalStateException("Module scope already open.");
        }

        ScopeBuilder parent = myLexicalScopeStack.peek();

        ModuleScopeBuilder s = new ModuleScopeBuilder(myTypeGraph, definingElement, parent, this);

        myCurModuleScope = s;

        addScope(s, parent);
        myModuleScopes.put(s.getModuleIdentifier(), s);

        return s;
    }

    /**
     * <p>
     * Starts a new working scope to represent the scope defined by <code>definingElement</code>. It's parent will be
     * the last unclosed working scope (including unclosed working module scopes) and who's root parent is the currently
     * open working module scope.
     * </p>
     *
     * @param definingElement
     *            The AST node that defined this scope.
     *
     * @return The newly opened working scope.
     *
     * @throws IllegalArgumentException
     *             If <code>definingElement</code> is <code>null</code>.
     * @throws IllegalStateException
     *             If no module scope is currently open.
     */
    public final ScopeBuilder startScope(ResolveConceptualElement definingElement) {
        if (definingElement == null) {
            throw new IllegalArgumentException("definingElement may not be " + "null.");
        }

        checkModuleScopeOpen();

        ScopeBuilder parent = myLexicalScopeStack.peek();

        ScopeBuilder s = new ScopeBuilder(this, myTypeGraph, definingElement, parent,
                myCurModuleScope.getModuleIdentifier());

        addScope(s, parent);

        return s;
    }

    /**
     * <p>
     * This method returns the object in string format.
     * </p>
     *
     * <p>
     * <strong>Note:</strong> The {@code toString} method is intended for printing debugging messages. Do not use its
     * value to perform compiler actions.
     * </p>
     *
     * @return Object as a string.
     */
    @Override
    public final String toString() {
        StringBuilder result = new StringBuilder();

        boolean first = true;
        for (ScopeBuilder b : myLexicalScopeStack) {
            if (first) {
                first = false;
            } else {
                result.append(",\n");
            }

            result.append(b.toString());
        }

        return result.toString();
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>
     * This adds a new scope to the parent scope.
     * </p>
     *
     * @param s
     *            The new scope to be added.
     * @param parent
     *            The parent scope.
     */
    private void addScope(ScopeBuilder s, ScopeBuilder parent) {
        parent.addChild(s);
        myLexicalScopeStack.push(s);
        myScopes.put(s.getDefiningElement(), s);
    }

    /**
     * <p>
     * This checks to see if we have any open module scopes.
     * </p>
     */
    private void checkModuleScopeOpen() {
        if (myCurModuleScope == null) {
            throw new IllegalStateException("No open module scope.");
        }
    }

    /**
     * <p>
     * This checks to see if we have any open scopes.
     * </p>
     */
    private void checkScopeOpen() {
        if (myLexicalScopeStack.size() == 1) {
            throw new IllegalStateException("No open scope.");
        }
    }

}

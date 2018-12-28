/*
 * SyntacticScope.java
 * ---------------------------------
 * Copyright (c) 2019
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
import edu.clemson.cs.rsrg.typeandpopulate.entry.FacilityEntry;
import edu.clemson.cs.rsrg.typeandpopulate.entry.ProgramParameterEntry;
import edu.clemson.cs.rsrg.typeandpopulate.entry.SymbolTableEntry;
import edu.clemson.cs.rsrg.typeandpopulate.exception.DuplicateSymbolException;
import edu.clemson.cs.rsrg.typeandpopulate.exception.NoSuchSymbolException;
import edu.clemson.cs.rsrg.typeandpopulate.programtypes.PTType;
import edu.clemson.cs.rsrg.typeandpopulate.query.MultimatchSymbolQuery;
import edu.clemson.cs.rsrg.typeandpopulate.query.SymbolQuery;
import edu.clemson.cs.rsrg.typeandpopulate.query.searcher.TableSearcher;
import edu.clemson.cs.rsrg.typeandpopulate.query.searcher.TableSearcher.SearchContext;
import edu.clemson.cs.rsrg.typeandpopulate.utilities.ModuleIdentifier;
import java.util.*;

/**
 * <p>A <code>SyntacticScope</code> corresponds to a RESOLVE scope that
 * arises because of a concrete piece of source code (rather than, for example,
 * the built-in global scope, or the top-level dummy scope), represented by
 * a {@link ResolveConceptualElement} called its <em>defining element</em>. Such a
 * scope therefore exists in a <em>lexical hierarchy</em> of those scopes
 * introduced by its defining element's parent elements, necessarily passing
 * through a {@link ModuleScope} (belonging to this scope's
 * <em>source module</em>) before reaching the top-level dummy scope. If this
 * scope corresponds to a module, it is its own source module.</p>
 *
 * <p>The symbols in this scope's lexical parents are therefore implicitly
 * available from within this scope. Optionally, symbols from the transitive
 * closure of all modules imported by this scope's source module (its
 * <em>recursive imports</em>) are available, as are any symbols found in
 * facilities defined in the source module or any modules directly imported by
 * the source module (its <em>named imports</em>, which do not recursively
 * include any modules imported from within named imports).</p>
 *
 * <p>Note that this class has no public constructor. Instances of this class
 * can be retrieved from a {@link ScopeRepository} or constructed via
 * some of the methods of {@link MathSymbolTableBuilder}.</p>
 *
 * @version 2.0
 */
public abstract class SyntacticScope extends AbstractScope {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The element that created this scope.</p> */
    final ResolveConceptualElement myDefiningElement;

    /** <p>The parent scope.</p> */
    Scope myParent;

    /** <p>The module identifier for the module that this scope belongs to.</p> */
    protected final ModuleIdentifier myRootModule;

    /** <p>The symbol table bindings.</p> */
    protected final BaseSymbolTable myBindings;

    /** <p>The source scope repository.</p> */
    private final ScopeRepository mySource;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This creates a new scope for a {@link ResolveConceptualElement}.</p>
     *
     * @param source The source scope repository.
     * @param definingElement The element that created this scope.
     * @param parent The parent scope.
     * @param enclosingModule The module identifier for the module
     *                        that this scope belongs to.
     * @param bindings The symbol table bindings.
     */
    SyntacticScope(ScopeRepository source,
            ResolveConceptualElement definingElement, Scope parent,
            ModuleIdentifier enclosingModule, BaseSymbolTable bindings) {
        mySource = source;
        myDefiningElement = definingElement;
        myParent = parent;
        myRootModule = enclosingModule;
        myBindings = bindings;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>Begins applying the given {@link TableSearcher} up the
     * lexical hierarchy that ends in this scope, starting with this scope, thus
     * adding any matches to <code>matches</code>.  For the purposes of the
     * search and the returned results, any generics will be instantiated
     * appropriately according to <code>genericInstantiations</code>, but this
     * scope will not be permanently modified.</p>
     *
     * <p>The search will continue upward toward the top-level global scope
     * until one of the following happens:</p>
     *
     * <ul>
     * 		<li>The top-level scope is searched.</li>
     * 		<li>A scope is reached that is already in
     * 		    <code>searchedScopes</code>.</li>
     * 		<li><code>searcher</code>'s {@link
     * 		    TableSearcher#addMatches(SymbolTable, List, TableSearcher.SearchContext)} method
     * 		    returns <code>true</code>, indicating the list is complete.</li>
     * 		<li>The <code>addMatches()</code> method throws a
     * 		    {@link DuplicateSymbolException}.</li>
     * </ul>
     *
     * <p>In the first three cases, the method returns normally, with any
     * matches added to <code>matches</code>. If no matches are found,
     * <code>matches</code> will simply be left unchanged.</p>
     *
     * <p>In the last case, this method will throw a
     * <code>DuplicateSymbolException</code>.</p>
     *
     * <p>Regardless of how this method terminates, any searched scopes will be
     * added to <code>searchedScopes</code>.</p>
     *
     * @param searcher The searcher to be used to match symbol table entries.
     * @param matches A non-<code>null</code> accumulator of matches.
     * @param searchedScopes A set of already-searched scopes.
     * @param genericInstantiations A mapping from generic names to instantiated
     *            types, which will be applied when searching and returning
     *            results.
     * @param instantiatingFacility A pointer to the symbol table entry
     * 			  corresponding to the facility that provided the instantiation
     * 			  of the generics given by <code>genericInstantiations</code>,
     *            or <code>null</code> if we are not searching an instantiated
     *            scope.  If this parameter is <code>null</code> then
     *            <code>genericInstantiations.isEmpty() == true</code>, but the
     *            reverse is not necessarily the case.
     *
     * @return <code>true</code> if <code>matches</code> now represents a
     *         final list of search results; i.e., no further scopes
     *         should be considered. <code>false</code> indicates that
     *         the search should continue, provided there are additional
     *         un-searched scopes.
     */
    @Override
    public final <E extends SymbolTableEntry> boolean addMatches(
            TableSearcher<E> searcher, List<E> matches,
            Set<Scope> searchedScopes,
            Map<String, PTType> genericInstantiations,
            FacilityEntry instantiatingFacility, SearchContext l)
            throws DuplicateSymbolException {
        boolean finished = false;

        if (!searchedScopes.contains(this)) {
            searchedScopes.add(this);

            SymbolTable symbolTableView = myBindings;

            if (instantiatingFacility != null) {
                symbolTableView =
                        new InstantiatedSymbolTable(myBindings,
                                genericInstantiations, instantiatingFacility);
            }

            finished = searcher.addMatches(symbolTableView, matches, l);

            if (!finished) {
                finished =
                        myParent
                                .addMatches(searcher, matches, searchedScopes,
                                        genericInstantiations,
                                        instantiatingFacility, l);
            }
        }

        return finished;
    }

    /**
     * <p>Returns a list of {@link ProgramParameterEntry}s
     * contained directly in this scope. These correspond to the formal
     * parameters defined by the syntactic element that introduced the scope.
     * </p>
     *
     * <p>If there are no parameters, or the syntactic element is not of the
     * sort that can define parameters, returns an empty list.</p>
     *
     * @return Entries for the parameters of the current scope.
     */
    @Override
    public final List<ProgramParameterEntry> getFormalParameterEntries() {
        List<ProgramParameterEntry> result =
                new LinkedList<>();

        Iterator<ProgramParameterEntry> formalBindings =
                myBindings.iterateByType(ProgramParameterEntry.class);

        while (formalBindings.hasNext()) {
            result.add(formalBindings.next());
        }

        return result;
    }

    /**
     * <p>Returns this scopes defining element.</p>
     *
     * @return The defining element.
     */
    public ResolveConceptualElement getDefiningElement() {
        return myDefiningElement;
    }

    /**
     * <p>Returns the module identifier for the module that instantiated
     * this scope.</p>
     *
     * @return The {@link ModuleIdentifier}.
     */
    public final ModuleIdentifier getRootModule() {
        return myRootModule;
    }

    /**
     * <p>Searches for symbols by the given query, using this <code>Scope</code>
     * as the source scope of the search, i.e. the scope that is the context
     * from which the search was triggered.</p>
     *
     * @param query The query to use.
     *
     * @return A list of all symbols matching the given query.
     */
    @Override
    public final <E extends SymbolTableEntry> List<E> query(
            MultimatchSymbolQuery<E> query) {
        return query.searchFromContext(this, mySource);
    }

    /**
     * <p>Searches for a symbol using the given query, using this
     * <code>Scope</code> as the source scope of the search, i.e. the scope
     * that is the context from which the search was triggered.  This method
     * works much like {@link #query(MultimatchSymbolQuery)} except that
     * it expects exactly one match, as determined by the given query. If the
     * given query matches more than one entry, or itself throws a
     * {@link DuplicateSymbolException}, then this method will throw
     * a <code>DuplicateSymbolException</code>.</p>
     *
     * @param query The query to use.
     *
     * @return The single symbol that is matched.
     *
     * @throws NoSuchSymbolException If the query matches no symbol table
     * 		       entries.
     * @throws DuplicateSymbolException If the query matches more than one
     * 			   symbol table entry.
     */
    @Override
    public final <E extends SymbolTableEntry> E queryForOne(SymbolQuery<E> query)
            throws NoSuchSymbolException,
                DuplicateSymbolException {
        List<E> results = query.searchFromContext(this, mySource);

        if (results.isEmpty()) {
            throw new NoSuchSymbolException("No entries found!",
                    new IllegalStateException());
        }
        else if (results.size() > 1) {
            throw new DuplicateSymbolException("Found duplicate entries!",
                    results.get(0));
        }

        return results.get(0);
    }

    /**
     * <p>This method returns the object in string format.</p>
     *
     * @return Object as a string.
     */
    @Override
    public final String toString() {
        return myDefiningElement + " {" + myBindings.toString() + "}";
    }

    // ===========================================================
    // Package Private Methods
    // ===========================================================

    /**
     * <p>Returns the parent scope that contains this scope.</p>
     *
     * @return The {@link Scope}.
     */
    final Scope getParent() {
        return myParent;
    }

    /**
     * <p>Returns the source repository that contains this scope.</p>
     *
     * @return The {@link ScopeRepository}.
     */
    final ScopeRepository getSourceRepository() {
        return mySource;
    }

}
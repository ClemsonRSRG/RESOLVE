/*
 * [The "BSD license"]
 * Copyright (c) 2015 Clemson University
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 
 * 3. The name of the author may not be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package edu.clemson.cs.r2jt.typeandpopulate;

import edu.clemson.cs.r2jt.typeandpopulate.programtypes.PTType;
import edu.clemson.cs.r2jt.typeandpopulate.searchers.TableSearcher;
import edu.clemson.cs.r2jt.typeandpopulate.query.MultimatchSymbolQuery;
import edu.clemson.cs.r2jt.typeandpopulate.query.SymbolQuery;
import edu.clemson.cs.r2jt.typeandpopulate.entry.FacilityEntry;
import edu.clemson.cs.r2jt.typeandpopulate.entry.SymbolTableEntry;
import edu.clemson.cs.r2jt.typeandpopulate.entry.ProgramParameterEntry;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.clemson.cs.r2jt.absyn.ResolveConceptualElement;
import edu.clemson.cs.r2jt.typeandpopulate.searchers.TableSearcher.SearchContext;

/**
 * <p>A <code>SyntacticScope</code> corresponds to a RESOLVE scope that
 * arises because of a concrete piece of source code (rather than, for example,
 * the built-in global scope, or the top-level dummy scope), represented by
 * a {@link edu.clemson.cs.r2jt.absyn.ResolveConceptualElement 
 *     ResolveConceptualElement} called its <em>defining element</em>.  Such a
 * scope therefore exists in a <em>lexical hierarchy</em> of those scopes
 * introduced by its defining element's parent elements, necessarily passing
 * through a {@link ModuleScope ModuleScope} (belonging to this scope's 
 * <em>source module</em>) before reaching the top-level dummy scope.  If this
 * scope corresponds to a module, it is its own source module.</p>
 *     
 * <p>The symbols in this scope's lexical parents are therefore implicitly 
 * available from within this scope.  Optionally, symbols from the transitive
 * closure of all modules imported by this scope's source module (its 
 * <em>recursive imports</em>) are available, as are any symbols found in 
 * facilities defined in the source module or any modules directly imported by
 * the source module (its <em>named imports</em>, which do not recursively 
 * include any modules imported from within named imports).</p>
 * 
 * <p>Note that this class has no public constructor.  Instances of this class
 * can be retrieved from a {@link ScopeRepository SymbolTable} or constructed via
 * some of the methods of {@link MathSymbolTableBuilder MathSymbolTableBuilder}.
 * </p>
 */
public abstract class SyntacticScope extends AbstractScope {

    protected final ResolveConceptualElement myDefiningElement;
    protected Scope myParent;
    protected final ModuleIdentifier myRootModule;
    protected final BaseSymbolTable myBindings;
    private final ScopeRepository mySource;

    /*package private*/SyntacticScope(ScopeRepository source,
            ResolveConceptualElement definingElement, Scope parent,
            ModuleIdentifier enclosingModule, BaseSymbolTable bindings) {

        mySource = source;
        myDefiningElement = definingElement;
        myParent = parent;
        myRootModule = enclosingModule;
        myBindings = bindings;
    }

    @Override
    public <E extends SymbolTableEntry> List<E> query(
            MultimatchSymbolQuery<E> query) {

        return query.searchFromContext(this, mySource);
    }

    @Override
    public <E extends SymbolTableEntry> E queryForOne(SymbolQuery<E> query)
            throws NoSuchSymbolException,
                DuplicateSymbolException {

        List<E> results = query.searchFromContext(this, mySource);

        if (results.isEmpty()) {
            throw new NoSuchSymbolException();
        }
        else if (results.size() > 1) {
            throw new DuplicateSymbolException();
        }

        return results.get(0);
    }

    /**
     * <p>Returns this scopes defining element.</p>
     * 
     * @return The defining element.
     */
    public ResolveConceptualElement getDefiningElement() {
        return myDefiningElement;
    }

    /*package private*/final Scope getParent() {
        return myParent;
    }

    public ModuleIdentifier getRootModule() {
        return myRootModule;
    }

    public ScopeRepository getSourceRepository() {
        return mySource;
    }

    @Override
    public <E extends SymbolTableEntry> boolean addMatches(
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

    @Override
    public List<ProgramParameterEntry> getFormalParameterEntries() {
        List<ProgramParameterEntry> result =
                new LinkedList<ProgramParameterEntry>();

        Iterator<ProgramParameterEntry> formalBindings =
                myBindings.iterateByType(ProgramParameterEntry.class);

        while (formalBindings.hasNext()) {
            result.add(formalBindings.next());
        }

        return result;
    }

    @Override
    public String toString() {
        return myDefiningElement + " {" + myBindings.toString() + "}";
    }
}

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
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
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
import edu.clemson.cs.r2jt.typeandpopulate.entry.FacilityEntry;
import edu.clemson.cs.r2jt.typeandpopulate.entry.ProgramParameterEntry;
import edu.clemson.cs.r2jt.typeandpopulate.entry.SymbolTableEntry;
import edu.clemson.cs.r2jt.typeandpopulate.query.MultimatchSymbolQuery;
import edu.clemson.cs.r2jt.typeandpopulate.query.SymbolQuery;
import edu.clemson.cs.r2jt.typeandpopulate.searchers.TableSearcher;
import edu.clemson.cs.r2jt.typeandpopulate.searchers.TableSearcher.SearchContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>An <code>InstantiatedScope</code> decorates an existing 
 * {@link Scope Scope} such that calls to {@link Scope#addMatches addMatches()},
 * the search method to which all others defer, are augmented with an additional
 * set of generic instantiations and an instantiating facility.</p>
 */
public class InstantiatedScope extends AbstractScope {

    private final Scope myBaseScope;
    private final FacilityEntry myInstantiatingFacility;
    private final Map<String, PTType> myAdditionalGenericInstantiations =
            new HashMap<String, PTType>();

    public InstantiatedScope(Scope baseScope,
            Map<String, PTType> genericInstantiations,
            FacilityEntry instantiatingFacility) {
        myBaseScope = baseScope;
        myAdditionalGenericInstantiations.putAll(genericInstantiations);
        myInstantiatingFacility = instantiatingFacility;
    }

    @Override
    public <E extends SymbolTableEntry> List<E> query(
            MultimatchSymbolQuery<E> query) {
        return myBaseScope.query(query);
    }

    @Override
    public <E extends SymbolTableEntry> E queryForOne(SymbolQuery<E> query)
            throws NoSuchSymbolException,
                DuplicateSymbolException {
        return myBaseScope.queryForOne(query);
    }

    @Override
    public <E extends SymbolTableEntry> boolean addMatches(
            TableSearcher<E> searcher, List<E> matches,
            Set<Scope> searchedScopes,
            Map<String, PTType> genericInstantiations,
            FacilityEntry facilityInstantiation, SearchContext l)
            throws DuplicateSymbolException {

        if (facilityInstantiation != null) {
            //It's unclear how this could happen or what it would mean, so we
            //fail fast.  If an example triggers this, we need to think 
            //carefully about what it would mean.
            throw new RuntimeException("Duplicate instantiation???");
        }

        return myBaseScope.addMatches(searcher, matches, searchedScopes,
                myAdditionalGenericInstantiations, myInstantiatingFacility, l);
    }

    @Override
    public List<ProgramParameterEntry> getFormalParameterEntries() {
        return myBaseScope.getFormalParameterEntries();
    }

}

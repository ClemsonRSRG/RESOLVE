/**
 * DummyIdentifierResolver.java
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
package edu.clemson.cs.r2jt.typeandpopulate2;

import edu.clemson.cs.r2jt.typeandpopulate2.entry.FacilityEntry;
import edu.clemson.cs.r2jt.typeandpopulate2.entry.ProgramParameterEntry;
import edu.clemson.cs.r2jt.typeandpopulate2.entry.SymbolTableEntry;
import edu.clemson.cs.r2jt.typeandpopulate2.programtypes.PTType;
import edu.clemson.cs.r2jt.typeandpopulate2.query.MultimatchSymbolQuery;
import edu.clemson.cs.r2jt.typeandpopulate2.query.SymbolQuery;
import edu.clemson.cs.r2jt.typeandpopulate2.searchers.TableSearcher;

import java.util.*;

public class DummyIdentifierResolver extends AbstractScope {

    @Override
    public List<ProgramParameterEntry> getFormalParameterEntries() {
        return Collections.emptyList();
    }

    @Override
    public <E extends SymbolTableEntry> List<E> query(
            MultimatchSymbolQuery<E> query) {

        return new LinkedList<E>();
    }

    @Override
    public <E extends SymbolTableEntry> E queryForOne(SymbolQuery<E> query)
            throws NoSuchSymbolException,
                DuplicateSymbolException {

        throw new NoSuchSymbolException();
    }

    @Override
    public <E extends SymbolTableEntry> boolean addMatches(
            TableSearcher<E> searcher, List<E> matches,
            Set<Scope> searchedScopes,
            Map<String, PTType> genericInstantiations,
            FacilityEntry instantiatingFacility, TableSearcher.SearchContext l)
            throws DuplicateSymbolException {

        return false;
    }
}

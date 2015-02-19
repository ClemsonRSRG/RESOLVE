/**
 * AbstractScope.java
 * ---------------------------------
 * Copyright (c) 2014
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.typeandpopulate2;

import edu.clemson.cs.r2jt.typeandpopulate2.entry.SymbolTableEntry;
import edu.clemson.cs.r2jt.typeandpopulate2.programtypes.PTType;
import edu.clemson.cs.r2jt.typeandpopulate2.searchers.TableSearcher;

import java.util.*;

public abstract class AbstractScope implements Scope {

    @Override
    public final <E extends SymbolTableEntry> List<E> getMatches(
            TableSearcher<E> searcher, TableSearcher.SearchContext l)
            throws DuplicateSymbolException {
        List<E> result = new LinkedList<E>();
        Set<Scope> searchedScopes = new HashSet<Scope>();
        Map<String, PTType> genericInstantiations =
                new HashMap<String, PTType>();
        addMatches(searcher, result, searchedScopes, genericInstantiations,
                null, l);
        return result;
    }
}

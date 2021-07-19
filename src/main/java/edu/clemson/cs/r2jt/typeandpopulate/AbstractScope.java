/*
 * AbstractScope.java
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
package edu.clemson.cs.r2jt.typeandpopulate;

import edu.clemson.cs.r2jt.typeandpopulate.programtypes.PTType;
import edu.clemson.cs.r2jt.typeandpopulate.searchers.TableSearcher;
import edu.clemson.cs.r2jt.typeandpopulate.entry.SymbolTableEntry;
import edu.clemson.cs.r2jt.typeandpopulate.searchers.TableSearcher.SearchContext;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class AbstractScope implements Scope {

    @Override
    public final <E extends SymbolTableEntry> List<E>
            getMatches(TableSearcher<E> searcher, SearchContext l)
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

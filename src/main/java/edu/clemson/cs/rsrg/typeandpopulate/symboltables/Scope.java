/**
 * Scope.java
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

import edu.clemson.cs.rsrg.typeandpopulate.entry.SymbolTableEntry;
import edu.clemson.cs.rsrg.typeandpopulate.query.MultimatchSymbolQuery;
import java.util.List;

/**
 * TODO: Refactor this class
 */
public interface Scope {

    /**
     * <p>Searches for symbols by the given query, using this <code>Scope</code>
     * as the source scope of the search, i.e. the scope that is the context
     * from which the search was triggered.</p>
     *
     * @param query The query to use.
     *
     * @return A list of all symbols matching the given query.
     */
    <E extends SymbolTableEntry> List<E> query(MultimatchSymbolQuery<E> query);

}
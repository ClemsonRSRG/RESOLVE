/**
 * MultimatchTableSearcher.java
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
package edu.clemson.cs.r2jt.typeandpopulate2.searchers;

import edu.clemson.cs.r2jt.typeandpopulate2.DuplicateSymbolException;
import edu.clemson.cs.r2jt.typeandpopulate2.SymbolTable;
import edu.clemson.cs.r2jt.typeandpopulate2.entry.SymbolTableEntry;

import java.util.List;

/**
 * <p>A simple refinement on {@link TableSearcher} that guarantees
 * its method will not throw a {@link DuplicateSymbolException}.</p>
 */
public interface MultimatchTableSearcher<E extends SymbolTableEntry>
        extends
            TableSearcher<E> {

    /**
     * <p>Refines {@link TableSearcher#addMatches} to guarantee that it will not
     * throw a {@link DuplicateSymbolException}.  Otherwise, behaves
     * identically.</p>
     */
    @Override
    public boolean addMatches(SymbolTable entries, List<E> matches,
            SearchContext l);
}

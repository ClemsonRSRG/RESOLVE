/*
 * MultimatchTableSearcher.java
 * ---------------------------------
 * Copyright (c) 2018
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.typeandpopulate.searchers;

import edu.clemson.cs.r2jt.typeandpopulate.SymbolTable;
import edu.clemson.cs.r2jt.typeandpopulate.searchers.TableSearcher;
import edu.clemson.cs.r2jt.typeandpopulate.entry.SymbolTableEntry;
import java.util.List;

/**
 * <p>A simple refinement on {@link TableSearch TableSearch} that guarantees
 * its method will not throw a {@link DuplicateSymbolException 
 * DuplicateSymbolException}.</p>
 */
public interface MultimatchTableSearcher<E extends SymbolTableEntry>
        extends
            TableSearcher<E> {

    /**
     * <p>Refines {@link TableSearcher#addMatches(SymbolTable, List) 
     * TableSearcher.addMatches()} to guarantee that it will not throw a
     * {@link DuplicateSymbolException DuplicateSymbolException}.  Otherwise,
     * behaves identically.</p>
     */
    @Override
    public boolean addMatches(SymbolTable entries, List<E> matches,
            SearchContext l);
}

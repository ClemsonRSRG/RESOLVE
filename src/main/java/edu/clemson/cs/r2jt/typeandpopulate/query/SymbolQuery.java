/*
 * SymbolQuery.java
 * ---------------------------------
 * Copyright (c) 2017
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.typeandpopulate.query;

import edu.clemson.cs.r2jt.typeandpopulate.DuplicateSymbolException;
import edu.clemson.cs.r2jt.typeandpopulate.Scope;
import edu.clemson.cs.r2jt.typeandpopulate.ScopeRepository;
import edu.clemson.cs.r2jt.typeandpopulate.entry.SymbolTableEntry;
import java.util.List;

/**
 * <p>A <code>SymbolQuery</code> defines a strategy for returning a list of
 * {@link SymbolTableEntry SymbolTableEntry}s that meet a certain set of 
 * criteria starting from some <em>source scope</em>.
 */
public interface SymbolQuery<E extends SymbolTableEntry> {

    /**
     * <p>Given a source {@link Scope Scope} and a 
     * {@link ScopeRepository ScopeRepository} containing any imports, from
     * which <code>source</code> is drawn, searches them appropriately, 
     * returning a list of matching {@link SymbolTableEntry SymbolTableEntry}s 
     * that are subtypes of <code>E</code>.</p>
     * 
     * <p>If there are no matches, returns an empty list.  If more than one 
     * match is found where no more than one was expected, throws a
     * {@link DuplicateSymbolException DuplicateSymbolException}.</p>
     * 
     * @param source The source scope from which the search was spawned.
     * @param repo A repository of any referenced modules.
     * 
     * @return A list of matches.
     */
    public List<E> searchFromContext(Scope source, ScopeRepository repo)
            throws DuplicateSymbolException;
}

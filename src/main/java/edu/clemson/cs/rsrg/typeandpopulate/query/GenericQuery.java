/**
 * GenericQuery.java
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
package edu.clemson.cs.rsrg.typeandpopulate.query;

import edu.clemson.cs.rsrg.typeandpopulate.entry.ProgramTypeEntry;
import edu.clemson.cs.rsrg.typeandpopulate.entry.SymbolTableEntry;
import edu.clemson.cs.rsrg.typeandpopulate.exception.DuplicateSymbolException;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.ScopeRepository;
import edu.clemson.cs.rsrg.typeandpopulate.symboltables.Scope;
import java.util.List;

/**
 * TODO: Refactor this class
 */
public class GenericQuery<E extends SymbolTableEntry>
        implements
            MultimatchSymbolQuery<ProgramTypeEntry> {

    public static final GenericQuery INSTANCE = new GenericQuery();

    private GenericQuery() {}

    /**
     * <p>Given a source {@link Scope} and a
     * {@link ScopeRepository} containing any imports, from
     * which <code>source</code> is drawn, searches them appropriately,
     * returning a list of matching {@link SymbolTableEntry SymbolTableEntry}s
     * that are subtypes of <code>E</code>.</p>
     * <p>
     * <p>If there are no matches, returns an empty list. If more than one
     * match is found where no more than one was expected, throws a
     * {@link DuplicateSymbolException}.</p>
     *
     * @param source The source scope from which the search was spawned.
     * @param repo   A repository of any referenced modules.
     *
     * @return A list of matches.
     */
    @Override
    public List<ProgramTypeEntry> searchFromContext(Scope source,
            ScopeRepository repo) {
        return null;
    }

}
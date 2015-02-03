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
package edu.clemson.cs.r2jt.typeandpopulate.searchers;

import edu.clemson.cs.r2jt.typeandpopulate.DuplicateSymbolException;
import edu.clemson.cs.r2jt.typeandpopulate.SymbolTable;
import edu.clemson.cs.r2jt.typeandpopulate.entry.SymbolTableEntry;
import java.util.List;

/**
 * <p>A <code>TableSearcher</code> is a strategy for searching a 
 * {@link SymbolTable SymbolTable}, adding any 
 * {@link SymbolTableEntry SymbolTableEntry}s that match the search to an
 * accumulator.</p>
 *
 * @param <E> Permits concrete implementations of this interface to refine the
 *            type of <code>SymbolTableEntry</code> they will match.  This 
 *            searcher guarantees that any entry it matches will descend from 
 *            <code>E</code>.  Put another way: no matched entry will not be 
 *            a subtype of <code>E</code>.
 */
public interface TableSearcher<E extends SymbolTableEntry> {

    public static enum SearchContext {
        GLOBAL, SOURCE_MODULE, IMPORT, FACILITY
    };

    /**
     * <p>Adds any symbol table entries from <code>entries</code> that match
     * this search to <code>matches</code>.  The order that they are added is
     * determined by the concrete base-class.</p>
     * 
     * <p>If no matches exist, the method will simply leave <code>matches</code>
     * unmodified.</p>
     * 
     * <p>The semantics of the incoming accumulator are only that it is the
     * appropriate place to add new matches, not that it will necessarily 
     * contain all matches so far.  This allows intermediate accumulators to
     * be created and passed without causing strange behavior.  <em>No concrete
     * subclass should depend on the incoming value of the accumulator, save
     * that it will be non-<code>null</code> and mutable.</em></p>
     * 
     * @param entries The set of symbol table entries to consider.
     * @param matches A non-<code>null</code> accumulator of matches.
     * @param l The context from which <code>entries</code> was drawn.
     * 
     * @return <code>true</code> if <code>matches</code> now represents a
     *         final list of search results&mdash;i.e., no further symbol table
     *         entries should be considered.  <code>false</code> indicates that
     *         the search should continue, provided there are additional
     *         un-searched scopes.
     *         
     * @throws DuplicateSymbolException If more than one match is found in
     *         <code>entries</code> where no more than one was expected.
     */
    public boolean addMatches(SymbolTable entries, List<E> matches,
            SearchContext l) throws DuplicateSymbolException;
}

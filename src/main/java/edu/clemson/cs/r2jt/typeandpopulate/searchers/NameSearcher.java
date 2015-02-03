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

import edu.clemson.cs.r2jt.typeandpopulate.SymbolTable;
import edu.clemson.cs.r2jt.typeandpopulate.entry.ProgramParameterEntry;
import edu.clemson.cs.r2jt.typeandpopulate.entry.SymbolTableEntry;
import java.util.List;

/**
 * <p>A <code>NameSearcher</code> returns entries in a {@link SymbolTable SymbolTable}
 * that have the specified name.</p>
 */
public class NameSearcher implements MultimatchTableSearcher<SymbolTableEntry> {

    private final String mySearchString;
    private final boolean myStopAfterFirstFlag;

    public NameSearcher(String searchString, boolean stopAfterFirst) {
        mySearchString = searchString;
        myStopAfterFirstFlag = stopAfterFirst;
    }

    public NameSearcher(String searchString) {
        this(searchString, true);
    }

    @Override
    public boolean addMatches(SymbolTable entries,
            List<SymbolTableEntry> matches, SearchContext l) {

        boolean result = entries.containsKey(mySearchString);

        if (result) {
            SymbolTableEntry e = entries.get(mySearchString);

            //Parameters of imported modules or facility instantiations ar not
            //exported and therefore should not be considered for results
            if (l.equals(SearchContext.SOURCE_MODULE)
                    || !(e instanceof ProgramParameterEntry)) {
                matches.add(entries.get(mySearchString));
            }
        }

        return myStopAfterFirstFlag && result;
    }
}

/**
 * NameSearcher.java
 * ---------------------------------
 * Copyright (c) 2015
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.typeandpopulate2.searchers;

import edu.clemson.cs.r2jt.typeandpopulate2.SymbolTable;
import edu.clemson.cs.r2jt.typeandpopulate2.entry.ProgramParameterEntry;
import edu.clemson.cs.r2jt.typeandpopulate2.entry.SymbolTableEntry;

import java.util.List;

/**
 * <p>A <code>NameSearcher</code> returns entries in a {@link SymbolTable}
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

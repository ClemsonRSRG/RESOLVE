/**
 * EntryTypeSearcher.java
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
package edu.clemson.cs.r2jt.typeandpopulate.searchers;

import edu.clemson.cs.r2jt.typeandpopulate.SymbolTable;
import edu.clemson.cs.r2jt.typeandpopulate.searchers.MultimatchTableSearcher;
import edu.clemson.cs.r2jt.typeandpopulate.entry.FacilityEntry;
import edu.clemson.cs.r2jt.typeandpopulate.entry.SymbolTableEntry;
import java.util.Iterator;
import java.util.List;

public class EntryTypeSearcher<E extends SymbolTableEntry>
        implements
            MultimatchTableSearcher<E> {

    public static final EntryTypeSearcher<FacilityEntry> FACILITY_SEARCHER =
            new EntryTypeSearcher<FacilityEntry>(FacilityEntry.class);

    private final Class<E> myTargetClass;

    public EntryTypeSearcher(Class<E> targetClass) {
        myTargetClass = targetClass;
    }

    @Override
    public boolean addMatches(SymbolTable entries, List<E> matches,
            SearchContext l) {
        Iterator<E> matchesIter = entries.iterateByType(myTargetClass);

        while (matchesIter.hasNext()) {
            matches.add(matchesIter.next());
        }

        return false;
    }

}

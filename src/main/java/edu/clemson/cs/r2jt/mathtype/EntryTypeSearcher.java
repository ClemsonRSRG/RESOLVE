package edu.clemson.cs.r2jt.mathtype;

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
    public boolean addMatches(SymbolTable entries, List<E> matches) {
        Iterator<E> matchesIter = entries.iterateByType(myTargetClass);

        while (matchesIter.hasNext()) {
            matches.add(matchesIter.next());
        }

        return false;
    }

}

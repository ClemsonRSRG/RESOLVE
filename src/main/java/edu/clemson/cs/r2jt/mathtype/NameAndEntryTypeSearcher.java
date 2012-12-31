/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.mathtype;

import java.util.Iterator;
import java.util.List;

/**
 *
 * @author hamptos
 */
public class NameAndEntryTypeSearcher<E extends SymbolTableEntry>
        implements
            MultimatchTableSearcher<E> {

    public static final EntryTypeSearcher<FacilityEntry> FACILITY_SEARCHER =
            new EntryTypeSearcher<FacilityEntry>(FacilityEntry.class);

    private final Class<E> myTargetClass;
    private final String myTargetName;
    private final boolean myStopAfterFirstFlag;

    public NameAndEntryTypeSearcher(String name, Class<E> targetClass,
            boolean stopAfterFirst) {

        myTargetClass = targetClass;
        myTargetName = name;
        myStopAfterFirstFlag = stopAfterFirst;
    }

    @Override
    public boolean addMatches(SymbolTable entries, List<E> matches) {
        SymbolTableEntry match = entries.get(myTargetName);

        boolean foundOne =
                (match != null)
                        && myTargetClass.isAssignableFrom(match.getClass());

        if (foundOne) {
            matches.add((E) match);
        }

        return myStopAfterFirstFlag && foundOne;
    }
}
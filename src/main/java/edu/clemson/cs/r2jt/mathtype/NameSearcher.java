package edu.clemson.cs.r2jt.mathtype;

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
            List<SymbolTableEntry> matches) {

        boolean result = entries.containsKey(mySearchString);

        if (result) {
            matches.add(entries.get(mySearchString));
        }

        return myStopAfterFirstFlag && result;
    }
}

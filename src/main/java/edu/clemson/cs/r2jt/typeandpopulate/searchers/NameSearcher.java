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

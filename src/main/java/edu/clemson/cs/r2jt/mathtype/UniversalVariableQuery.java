package edu.clemson.cs.r2jt.mathtype;

import java.util.Iterator;
import java.util.List;

import edu.clemson.cs.r2jt.mathtype.MathSymbolTable.FacilityStrategy;
import edu.clemson.cs.r2jt.mathtype.MathSymbolTable.ImportStrategy;

public class UniversalVariableQuery
        implements
            MultimatchSymbolQuery<MathSymbolEntry> {

    public static final MultimatchSymbolQuery<MathSymbolEntry> INSTANCE =
            (MultimatchSymbolQuery<MathSymbolEntry>) new UniversalVariableQuery();

    private final BaseSymbolQuery<MathSymbolEntry> myBaseQuery;

    private UniversalVariableQuery() {
        myBaseQuery =
                new BaseSymbolQuery<MathSymbolEntry>(new UnqualifiedPath(
                        ImportStrategy.IMPORT_NONE,
                        FacilityStrategy.FACILITY_IGNORE, false),
                        new UniversalVariableSearcher());
    }

    @Override
    public List<MathSymbolEntry> searchFromContext(Scope source,
            ScopeRepository repo) {

        List<MathSymbolEntry> result;
        try {
            result = myBaseQuery.searchFromContext(source, repo);
        }
        catch (DuplicateSymbolException dse) {
            //Can't happen--our base query is a name matcher
            throw new RuntimeException(dse);
        }

        return result;
    }

    private static class UniversalVariableSearcher
            implements
                MultimatchTableSearcher<MathSymbolEntry> {

        @Override
        public boolean addMatches(SymbolTable entries,
                List<MathSymbolEntry> matches) {

            Iterator<MathSymbolEntry> mathSymbols =
                    entries.iterateByType(MathSymbolEntry.class);

            MathSymbolEntry curSymbol;
            while (mathSymbols.hasNext()) {
                curSymbol = mathSymbols.next();

                if (curSymbol.getQuantification() == SymbolTableEntry.Quantification.UNIVERSAL) {
                    matches.add(curSymbol);
                }
            }

            return false;
        }
    }
}

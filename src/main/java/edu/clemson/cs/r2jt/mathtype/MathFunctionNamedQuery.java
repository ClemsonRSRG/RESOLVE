package edu.clemson.cs.r2jt.mathtype;

import java.util.LinkedList;
import java.util.List;

import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.mathtype.MathSymbolTable.FacilityStrategy;
import edu.clemson.cs.r2jt.mathtype.MathSymbolTable.ImportStrategy;

public class MathFunctionNamedQuery
        implements
            MultimatchSymbolQuery<MathSymbolEntry> {

    private final SymbolQuery<SymbolTableEntry> myNameQuery;

    public MathFunctionNamedQuery(PosSymbol qualifier, PosSymbol name) {
        myNameQuery =
                new NameQuery(qualifier, name, ImportStrategy.IMPORT_RECURSIVE,
                        FacilityStrategy.FACILITY_IGNORE, false);
    }

    @Override
    public List<MathSymbolEntry> searchFromContext(Scope source,
            ScopeRepository repo) {

        List<SymbolTableEntry> intermediateList;
        try {
            intermediateList = myNameQuery.searchFromContext(source, repo);
        }
        catch (DuplicateSymbolException dse) {
            //Shouldn't be possible
            throw new RuntimeException(dse);
        }

        List<MathSymbolEntry> finalList = new LinkedList<MathSymbolEntry>();
        for (SymbolTableEntry intermediateEntry : intermediateList) {
            try {
                //Note that we don't use toMathSymbolEntry() here because we
                //only want things that are actually defined directly as math
                //functions--not things that can be "thought of" as math 
                //functions
                finalList.add((MathSymbolEntry) intermediateEntry);
            }
            catch (ClassCastException cce) {
                //This is ok, just don't add it to the final list
            }
        }

        return finalList;
    }
}

package edu.clemson.rsrg.nProver.utilities.theorems;

import edu.clemson.rsrg.typeandpopulate.entry.TheoremEntry;
import edu.clemson.rsrg.typeandpopulate.query.EntryTypeQuery;
import edu.clemson.rsrg.typeandpopulate.symboltables.MathSymbolTable;
import edu.clemson.rsrg.typeandpopulate.symboltables.ModuleScope;

import java.util.List;

public class RelevantTheoremExtractor {

    private final ModuleScope myCurrentModuleScope;


    public RelevantTheoremExtractor( ModuleScope scope){
        myCurrentModuleScope = scope;
    }

    public List<TheoremEntry> theoremEntryQuery(){
        List<TheoremEntry> te = null;
        te = myCurrentModuleScope.query(new EntryTypeQuery<TheoremEntry>(TheoremEntry.class, MathSymbolTable.ImportStrategy.IMPORT_NAMED, MathSymbolTable.FacilityStrategy.FACILITY_IGNORE));
        System.err.println(te.size());
        return te;
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.mathtype;

import edu.clemson.cs.r2jt.data.PosSymbol;

/**
 * <p>A <code>NameAndEntryQuery</code> takes a (possibly-null) qualifier, a 
 * name, and an entry type descended from <code>SymbolTableEntry</code>, and
 * searched for entries that match, disregarding any entries with the correct
 * name but incorrect type.  If the qualifier is non-null, the
 * appropriate facility or module is searched.  If it <em>is</em> null, a
 * search is performed using the provided <code>ImportStrategy</code> and
 * <code>FacilityStrategy</code>.</p>
 */
public class NameAndEntryTypeQuery
        extends
            BaseMultimatchSymbolQuery<SymbolTableEntry> {

    public NameAndEntryTypeQuery(PosSymbol qualifier, String name,
            Class<? extends SymbolTableEntry> entryType,
            MathSymbolTable.ImportStrategy importStrategy,
            MathSymbolTable.FacilityStrategy facilityStrategy,
            boolean localPriority) {
        super(new PossiblyQualifiedPath(qualifier, importStrategy,
                facilityStrategy, localPriority), new NameAndEntryTypeSearcher(
                name, entryType, false));
    }

    public NameAndEntryTypeQuery(PosSymbol qualifier, PosSymbol name,
            Class<? extends SymbolTableEntry> entryType,
            MathSymbolTable.ImportStrategy importStrategy,
            MathSymbolTable.FacilityStrategy facilityStrategy,
            boolean localPriority) {
        this(qualifier, name.getName(), entryType, importStrategy,
                facilityStrategy, localPriority);
    }

    public NameAndEntryTypeQuery(PosSymbol qualifier, String name,
            Class<? extends SymbolTableEntry> entryType) {
        this(qualifier, name, entryType,
                MathSymbolTable.ImportStrategy.IMPORT_NONE,
                MathSymbolTable.FacilityStrategy.FACILITY_IGNORE, false);
    }

    public NameAndEntryTypeQuery(PosSymbol qualifier, PosSymbol name,
            Class<? extends SymbolTableEntry> entryType) {
        this(qualifier, name, entryType,
                MathSymbolTable.ImportStrategy.IMPORT_NONE,
                MathSymbolTable.FacilityStrategy.FACILITY_IGNORE, false);
    }
}
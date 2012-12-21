/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.mathtype;

import edu.clemson.cs.r2jt.mathtype.MathSymbolTable.FacilityStrategy;
import edu.clemson.cs.r2jt.mathtype.MathSymbolTable.ImportStrategy;

/**
 *
 * @author hamptos
 */
public class EntryTypeQuery<T extends SymbolTableEntry> extends BaseMultimatchSymbolQuery<T> implements MultimatchSymbolQuery<T> {
    
    public EntryTypeQuery(Class<? extends SymbolTableEntry> entryType, 
            ImportStrategy importStrategy, 
            FacilityStrategy facilityStrategy) {
        super(new UnqualifiedPath(importStrategy, facilityStrategy, false),
                new EntryTypeSearcher(entryType));
    }
}

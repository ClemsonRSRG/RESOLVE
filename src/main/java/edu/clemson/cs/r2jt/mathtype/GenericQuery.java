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
public class GenericQuery extends BaseMultimatchSymbolQuery<ProgramTypeEntry>
        implements
            MultimatchSymbolQuery<ProgramTypeEntry> {

    public static final GenericQuery INSTANCE = new GenericQuery();

    private GenericQuery() {
        super(new UnqualifiedPath(ImportStrategy.IMPORT_NAMED,
                FacilityStrategy.FACILITY_IGNORE, true),
                GenericSearcher.INSTANCE);
    }
}

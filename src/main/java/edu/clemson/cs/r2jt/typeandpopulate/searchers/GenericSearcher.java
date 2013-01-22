/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.typeandpopulate.searchers;

import edu.clemson.cs.r2jt.typeandpopulate.SymbolTable;
import edu.clemson.cs.r2jt.typeandpopulate.entry.ProgramParameterEntry;
import edu.clemson.cs.r2jt.typeandpopulate.entry.ProgramTypeEntry;
import edu.clemson.cs.r2jt.utilities.SourceErrorException;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author hamptos
 */
public class GenericSearcher
        implements
            MultimatchTableSearcher<ProgramTypeEntry> {

    public static final GenericSearcher INSTANCE = new GenericSearcher();

    private GenericSearcher() {

    }

    @Override
    public boolean addMatches(SymbolTable entries,
            List<ProgramTypeEntry> matches, SearchContext l) {

        Iterator<ProgramParameterEntry> parameters =
                entries.iterateByType(ProgramParameterEntry.class);
        ProgramParameterEntry parameter;
        while (parameters.hasNext()) {
            parameter = parameters.next();

            try {
                matches.add(parameter.toProgramTypeEntry(null));
            }
            catch (SourceErrorException see) {
                //No problem, just not what we're looking for
            }
        }

        return false;
    }

}

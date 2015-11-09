/**
 * GenericSearcher.java
 * ---------------------------------
 * Copyright (c) 2015
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.typeandpopulate2.searchers;

import edu.clemson.cs.r2jt.typeandpopulate2.SymbolTable;
import edu.clemson.cs.r2jt.typeandpopulate2.entry.ProgramParameterEntry;
import edu.clemson.cs.r2jt.typeandpopulate2.entry.ProgramTypeEntry;

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

    private GenericSearcher() {}

    @Override
    public boolean addMatches(SymbolTable entries,
            List<ProgramTypeEntry> matches, SearchContext l) {
        Iterator<ProgramParameterEntry> parameters =
                entries.iterateByType(ProgramParameterEntry.class);

        ProgramParameterEntry parameter;
        while (parameters.hasNext()) {
            parameter = parameters.next();

            if (parameter.getParameterMode().equals(
                    ProgramParameterEntry.ParameterMode.TYPE)) {
                matches.add(parameter.toProgramTypeEntry(null));
            }
        }
        return false;
    }

}

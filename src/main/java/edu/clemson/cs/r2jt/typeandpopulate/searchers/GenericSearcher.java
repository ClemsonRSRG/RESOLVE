/*
 * [The "BSD license"]
 * Copyright (c) 2015 Clemson University
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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

            if (parameter.getParameterMode().equals(
                    ProgramParameterEntry.ParameterMode.TYPE)) {
                matches.add(parameter.toProgramTypeEntry(null));
            }
        }

        return false;
    }

}

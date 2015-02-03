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

import edu.clemson.cs.r2jt.typeandpopulate.entry.OperationEntry;
import edu.clemson.cs.r2jt.typeandpopulate.entry.ProgramParameterEntry;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.proving.immutableadts.ImmutableList;
import edu.clemson.cs.r2jt.typeandpopulate.DuplicateSymbolException;
import edu.clemson.cs.r2jt.typeandpopulate.programtypes.PTType;
import edu.clemson.cs.r2jt.typeandpopulate.SymbolTable;
import edu.clemson.cs.r2jt.utilities.SourceErrorException;

public class OperationSearcher implements TableSearcher<OperationEntry> {

    private final String myQueryName;
    private final Location myQueryLocation;
    private final List<PTType> myActualArgumentTypes;

    public OperationSearcher(PosSymbol name, List<PTType> argumentTypes) {
        myQueryName = name.getName();
        myQueryLocation = name.getLocation();
        myActualArgumentTypes = new LinkedList<PTType>(argumentTypes);
    }

    @Override
    public boolean addMatches(SymbolTable entries,
            List<OperationEntry> matches, SearchContext l)
            throws DuplicateSymbolException {

        if (entries.containsKey(myQueryName)) {
            try {
                OperationEntry operation =
                        entries.get(myQueryName).toOperationEntry(
                                myQueryLocation);

                if (argumentsMatch(operation.getParameters())) {
                    //We have a match at this point
                    if (!matches.isEmpty()) {
                        throw new DuplicateSymbolException();
                    }

                    matches.add(operation);
                }
            }
            catch (SourceErrorException see) {
                //No problem, just don't include it in the result
            }
        }

        return false;
    }

    private boolean argumentsMatch(
            ImmutableList<ProgramParameterEntry> formalParameters) {

        boolean result =
                (formalParameters.size() == myActualArgumentTypes.size());

        if (result) {
            Iterator<ProgramParameterEntry> formalParametersIter =
                    formalParameters.iterator();
            Iterator<PTType> actualArgumentTypeIter =
                    myActualArgumentTypes.iterator();

            PTType actualArgumentType, formalParameterType;
            while (result && formalParametersIter.hasNext()) {
                actualArgumentType = actualArgumentTypeIter.next();
                formalParameterType =
                        formalParametersIter.next().getDeclaredType();

                result = actualArgumentType.acceptableFor(formalParameterType);
            }
        }

        return result;
    }
}

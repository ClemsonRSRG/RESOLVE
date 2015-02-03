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
 * 
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 
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
package edu.clemson.cs.r2jt.typeandpopulate;

import edu.clemson.cs.r2jt.typeandpopulate.query.UnqualifiedNameQuery;
import edu.clemson.cs.r2jt.typeandpopulate.entry.MathSymbolEntry;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 *
 * @author hamptos
 */
public class UnboundTypeAccumulator extends BoundVariableVisitor {

    private final Set<String> myUnboundTypeNames = new HashSet<String>();
    private final Scope myEnvironment;

    public UnboundTypeAccumulator(Scope environment) {
        myEnvironment = environment;
    }

    public Set<String> getFinalUnboundNamedTypes() {
        return new HashSet<String>(myUnboundTypeNames);
    }

    @Override
    public void beginMTNamed(MTNamed namedType) {

        boolean universal;
        try {
            getInnermostBinding(namedType.name);
            universal = true;
        }
        catch (NoSuchElementException e) {

            try {
                //We cast rather than call toMathSymbolEntry() because this 
                //would represent an error in the compiler code rather than the
                //RESOLVE source: we're looking at math things here only
                MathSymbolEntry entry =
                        (MathSymbolEntry) myEnvironment
                                .queryForOne(new UnqualifiedNameQuery(
                                        namedType.name));
                universal =
                        entry.getQuantification().equals(
                                MathSymbolEntry.Quantification.UNIVERSAL);
            }
            catch (NoSuchSymbolException nsse) {
                //Shouldn't be possible--we'd have dealt with it by now
                throw new RuntimeException(nsse);
            }
            catch (DuplicateSymbolException dse) {
                //Shouldn't be possible--we'd have dealt with it by now
                throw new RuntimeException(dse);
            }
        }

        if (universal) {
            myUnboundTypeNames.add(namedType.name);
        }
    }
}

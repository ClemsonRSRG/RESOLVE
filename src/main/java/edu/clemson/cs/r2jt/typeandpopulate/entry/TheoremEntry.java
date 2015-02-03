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
package edu.clemson.cs.r2jt.typeandpopulate.entry;

import edu.clemson.cs.r2jt.absyn.MathAssertionDec;
import edu.clemson.cs.r2jt.absyn.ResolveConceptualElement;
import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.proving.absyn.PExp;
import edu.clemson.cs.r2jt.typeandpopulate.ModuleIdentifier;
import edu.clemson.cs.r2jt.typeandpopulate.programtypes.PTType;
import edu.clemson.cs.r2jt.typeandpopulate.entry.FacilityEntry;
import edu.clemson.cs.r2jt.typeandpopulate.entry.MathSymbolEntry;
import edu.clemson.cs.r2jt.typeandpopulate.entry.SymbolTableEntry;
import edu.clemson.cs.r2jt.typereasoning.TypeGraph;
import java.util.Map;

/**
 *
 * @author hamptos
 */
public class TheoremEntry extends SymbolTableEntry {

    private PExp myAssertionAsPExp;
    private MathSymbolEntry myMathSymbolAlterEgo;

    public TheoremEntry(TypeGraph g, String name,
            MathAssertionDec definingElement, ModuleIdentifier sourceModule) {
        super(name, definingElement, sourceModule);

        myAssertionAsPExp = PExp.buildPExp(definingElement.getAssertion());

        myMathSymbolAlterEgo =
                new MathSymbolEntry(g, name, Quantification.NONE,
                        definingElement, g.BOOLEAN, null, null, null,
                        sourceModule);
    }

    public PExp getAssertion() {
        return myAssertionAsPExp;
    }

    @Override
    public TheoremEntry toTheoremEntry(Location l) {
        return this;
    }

    @Override
    public MathSymbolEntry toMathSymbolEntry(Location l) {
        return myMathSymbolAlterEgo;
    }

    @Override
    public String getEntryTypeDescription() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public SymbolTableEntry instantiateGenerics(
            Map<String, PTType> genericInstantiations,
            FacilityEntry instantiatingFacility) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}

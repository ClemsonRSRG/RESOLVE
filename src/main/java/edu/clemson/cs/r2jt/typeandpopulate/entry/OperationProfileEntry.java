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
package edu.clemson.cs.r2jt.typeandpopulate.entry;

import edu.clemson.cs.r2jt.absyn.Exp;
import edu.clemson.cs.r2jt.absyn.PerformanceOperationDec;
import edu.clemson.cs.r2jt.absyn.ResolveConceptualElement;
import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.typeandpopulate.ModuleIdentifier;
import edu.clemson.cs.r2jt.typeandpopulate.programtypes.PTType;
import java.util.Map;

/**
 *
 * @author ys
 */
public class OperationProfileEntry extends SymbolTableEntry {

    private final OperationEntry myCorrespondingOperation;
    private final Exp myEnsures;
    private final Exp myDuration;
    private final Exp myManipDisp;

    public OperationProfileEntry(String name,
            ResolveConceptualElement definingElement,
            ModuleIdentifier sourceModule, OperationEntry correspondingOperation) {

        super(name, definingElement, sourceModule);

        PerformanceOperationDec dec = (PerformanceOperationDec) definingElement;
        myCorrespondingOperation = correspondingOperation;
        myEnsures = dec.getEnsures();
        myDuration = dec.getDuration();
        myManipDisp = dec.getMainp_disp();
    }

    public OperationEntry getCorrespondingOperation() {
        return myCorrespondingOperation;
    }

    public Exp getEnsuresClause() {
        return myEnsures;
    }

    public Exp getDurationClause() {
        return myDuration;
    }

    public Exp getManipDispClause() {
        return myManipDisp;
    }

    @Override
    public String getEntryTypeDescription() {
        return "the profile of an operation";
    }

    @Override
    public SymbolTableEntry instantiateGenerics(
            Map<String, PTType> genericInstantiations,
            FacilityEntry instantiatingFacility) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public OperationProfileEntry toOperationProfileEntry(Location l) {
        return this;
    }
}

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
package edu.clemson.cs.r2jt.typeandpopulate.programtypes;

import edu.clemson.cs.r2jt.typeandpopulate.MTType;
import edu.clemson.cs.r2jt.typeandpopulate.entry.FacilityEntry;
import java.util.Map;

import edu.clemson.cs.r2jt.typereasoning.TypeGraph;

/**
 * <p>A <code>PTInstantiated</code> represents a <code>PTFamily</code> that has
 * been instantiated via a facility.</p>
 * 
 * <p>Note that, while an instantiated type must have all parameters "filled 
 * in", it's possible that some have been filled in with constant parameters
 * or type parameters from the facility's source module.</p>
 */
public class PTInstantiated extends PTType {

    /**
     * <p>A pointer to the entry in the symbol table corresponding to the 
     * facility that instantiated this type.</p>
     */
    private final FacilityEntry mySourceFacility;

    /**
     * <p>The name of the original type family.</p>
     */
    private final String myName;

    /**
     * <p>The mathematical model corresponding to this instantiated program
     * type.</p>
     */
    private final MTType myModel;

    public PTInstantiated(TypeGraph g, FacilityEntry facility,
            String familyName, MTType model) {
        super(g);

        mySourceFacility = facility;
        myModel = model;
        myName = familyName;
    }

    public FacilityEntry getInstantiatingFacility() {
        return mySourceFacility;
    }

    public String getFamilyName() {
        return myName;
    }

    @Override
    public MTType toMath() {
        return myModel;
    }

    @Override
    public PTType instantiateGenerics(
            Map<String, PTType> genericInstantiations,
            FacilityEntry instantiatingFacility) {

        //I'm already instantiated!
        return this;
    }

    @Override
    public boolean equals(Object o) {

        boolean result = (o instanceof PTInstantiated);

        if (result) {
            PTInstantiated oAsPTInstantiated = (PTInstantiated) o;

            result =
                    (mySourceFacility.equals(oAsPTInstantiated
                            .getInstantiatingFacility()))
                            && myName.equals(oAsPTInstantiated.getFamilyName());
        }

        return result;
    }
}

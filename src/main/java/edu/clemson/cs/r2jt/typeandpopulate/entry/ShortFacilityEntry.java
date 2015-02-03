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

import edu.clemson.cs.r2jt.absyn.ResolveConceptualElement;
import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.typeandpopulate.programtypes.PTType;
import java.util.Map;

public class ShortFacilityEntry extends ModuleEntry {

    private final FacilityEntry myEnclosedFacility;

    public ShortFacilityEntry(String name,
            ResolveConceptualElement definingElement,
            FacilityEntry enclosedFacility) {
        super(name, definingElement);

        myEnclosedFacility = enclosedFacility;
    }

    @Override
    public ShortFacilityEntry toShortFacilityEntry(Location l) {
        return this;
    }

    /**
     * <p>A short facility module contains exactly one facility declaration.
     * This method returns the entry corresponding to that declaration.</p>
     * 
     * @return The entry corresponding to the single facility enclosed in the
     *         short facility module.
     */
    public FacilityEntry getEnclosedFacility() {
        return myEnclosedFacility;
    }

    @Override
    public String getEntryTypeDescription() {
        return "a short facility module";
    }

    @Override
    public SymbolTableEntry instantiateGenerics(
            Map<String, PTType> genericInstantiations,
            FacilityEntry instantiatingFacility) {
        return new ShortFacilityEntry(getName(), getDefiningElement(),
                myEnclosedFacility.instantiateGenerics(genericInstantiations,
                        instantiatingFacility));
    }
}

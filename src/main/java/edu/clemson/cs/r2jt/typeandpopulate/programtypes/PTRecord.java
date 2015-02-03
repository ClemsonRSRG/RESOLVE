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
package edu.clemson.cs.r2jt.typeandpopulate.programtypes;

import edu.clemson.cs.r2jt.typeandpopulate.MTCartesian;
import edu.clemson.cs.r2jt.typeandpopulate.MTCartesian.Element;
import edu.clemson.cs.r2jt.typeandpopulate.MTType;
import edu.clemson.cs.r2jt.typeandpopulate.entry.FacilityEntry;
import edu.clemson.cs.r2jt.typereasoning.TypeGraph;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author hamptos
 */
public class PTRecord extends PTType {

    private final Map<String, PTType> myFields = new HashMap<String, PTType>();

    private final MTType myMathTypeAlterEgo;

    public PTRecord(TypeGraph g, Map<String, PTType> types) {
        super(g);

        myFields.putAll(types);

        Element[] elements = new Element[types.size()];
        int index = 0;
        for (Map.Entry<String, PTType> field : types.entrySet()) {
            elements[index] =
                    new Element(field.getKey(), field.getValue().toMath());
            index++;
        }
        myMathTypeAlterEgo = new MTCartesian(g, elements);
    }

    public PTType getFieldType(String name) {
        return myFields.get(name);
    }

    @Override
    public MTType toMath() {
        return myMathTypeAlterEgo;
    }

    @Override
    public PTType instantiateGenerics(
            Map<String, PTType> genericInstantiations,
            FacilityEntry instantiatingFacility) {

        Map<String, PTType> newFields = new HashMap<String, PTType>();
        for (Map.Entry<String, PTType> type : myFields.entrySet()) {
            newFields.put(type.getKey(), type.getValue().instantiateGenerics(
                    genericInstantiations, instantiatingFacility));
        }

        return new PTRecord(getTypeGraph(), newFields);
    }

    @Override
    public String toString() {
        return "Record " + myFields;
    }
}

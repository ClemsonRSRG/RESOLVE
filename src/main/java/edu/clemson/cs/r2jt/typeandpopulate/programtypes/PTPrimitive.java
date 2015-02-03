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
import edu.clemson.cs.r2jt.typereasoning.TypeGraph;
import java.util.Map;
import java.util.WeakHashMap;

/**
 *
 * @author hamptos
 */
public class PTPrimitive extends PTType {

    public static enum PrimitiveTypeName {
        INTEGER, BOOLEAN
    };

    private static WeakHashMap<GraphTypeKey, PTPrimitive> instances =
            new WeakHashMap<GraphTypeKey, PTPrimitive>();

    public static PTPrimitive getInstance(TypeGraph g, PrimitiveTypeName t) {
        PTPrimitive result = instances.get(new GraphTypeKey(g, t));

        if (result == null) {
            result = new PTPrimitive(g, t);
            instances.put(new GraphTypeKey(g, t), result);
        }

        return result;
    }

    private PTPrimitive(TypeGraph g, PrimitiveTypeName t) {
        super(g);
    }

    @Override
    public MTType toMath() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public PTType instantiateGenerics(
            Map<String, PTType> genericInstantiations,
            FacilityEntry instantiatingFacility) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private static class GraphTypeKey {

        private final TypeGraph myTypeGraph;
        private final PrimitiveTypeName myName;

        public GraphTypeKey(TypeGraph g, PrimitiveTypeName name) {
            myName = name;
            myTypeGraph = g;
        }

        @Override
        public int hashCode() {
            return myTypeGraph.hashCode() + myName.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            boolean result = (o instanceof GraphTypeKey);

            if (result) {
                GraphTypeKey oAsGTK = (GraphTypeKey) o;
                result =
                        (myName.equals(oAsGTK.myName))
                                && (myTypeGraph.equals(oAsGTK.myTypeGraph));
            }

            return result;
        }
    }
}

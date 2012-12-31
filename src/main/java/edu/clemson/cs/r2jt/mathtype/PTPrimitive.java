/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.mathtype;

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

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
public class PTInteger extends PTType {

    private static WeakHashMap<TypeGraph, PTInteger> instances =
            new WeakHashMap<TypeGraph, PTInteger>();

    public static PTInteger getInstance(TypeGraph g) {
        PTInteger result = instances.get(g);

        if (result == null) {
            result = new PTInteger(g);
            instances.put(g, result);
        }

        return result;
    }

    private PTInteger(TypeGraph g) {
        super(g);
    }

    @Override
    public MTType toMath() {
        return getTypeGraph().Z;
    }

    @Override
    public PTType instantiateGenerics(
            Map<String, PTType> genericInstantiations,
            FacilityEntry instantiatingFacility) {
        return this;
    }

}

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
public class PTBoolean extends PTType {

    private static WeakHashMap<TypeGraph, PTBoolean> instances =
            new WeakHashMap<TypeGraph, PTBoolean>();

    public static PTBoolean getInstance(TypeGraph g) {
        PTBoolean result = instances.get(g);

        if (result == null) {
            result = new PTBoolean(g);
            instances.put(g, result);
        }

        return result;
    }

    private PTBoolean(TypeGraph g) {
        super(g);
    }

    @Override
    public MTType toMath() {
        return getTypeGraph().BOOLEAN;
    }

    @Override
    public PTType instantiateGenerics(
            Map<String, PTType> genericInstantiations,
            FacilityEntry instantiatingFacility) {
        return this;
    }

}

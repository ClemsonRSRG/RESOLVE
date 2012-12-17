package edu.clemson.cs.r2jt.mathtype;

import java.util.Map;

import edu.clemson.cs.r2jt.typereasoning.TypeGraph;

public class PTGeneric extends PTType {

    private final String myName;

    public PTGeneric(TypeGraph g, String name) {
        super(g);

        myName = name;
    }

    public String getName() {
        return myName;
    }

    @Override
    public MTType toMath() {
        return new MTNamed(getTypeGraph(), myName);
    }

    @Override
    public PTType instantiateGenerics(
            Map<String, PTType> genericInstantiations,
            FacilityEntry instantiatingFacility) {

        PTType result = this;

        if (genericInstantiations.containsKey(myName)) {
            result = genericInstantiations.get(myName);
        }

        return result;
    }

    @Override
    public boolean equals(Object o) {
        boolean result = (o instanceof PTGeneric);

        if (result) {
            PTGeneric oAsPTGeneric = (PTGeneric) o;

            result = myName.equals(oAsPTGeneric.getName());
        }

        return result;
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.typeandpopulate.programtypes;

import edu.clemson.cs.r2jt.typeandpopulate.MTCartesian;
import edu.clemson.cs.r2jt.typeandpopulate.MTCartesian.Element;
import edu.clemson.cs.r2jt.typeandpopulate.MTType;
import edu.clemson.cs.r2jt.typeandpopulate.entry.FacilityEntry;
import edu.clemson.cs.r2jt.typereasoning.TypeGraph;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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

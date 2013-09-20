package edu.clemson.cs.r2jt.typeandpopulate.programtypes;

import edu.clemson.cs.r2jt.typeandpopulate.MTType;
import edu.clemson.cs.r2jt.typeandpopulate.entry.FacilityEntry;
import edu.clemson.cs.r2jt.typereasoning.TypeGraph;
import java.util.Map;

public abstract class PTType {

    private String myFacilityQualifier = null;
    private final TypeGraph myTypeGraph;

    public PTType(TypeGraph g) {
        myTypeGraph = g;
    }

    public final TypeGraph getTypeGraph() {
        return myTypeGraph;
    }

    public abstract MTType toMath();

    public abstract PTType instantiateGenerics(
            Map<String, PTType> genericInstantiations,
            FacilityEntry instantiatingFacility);

    public void setFacilityQualifier(String facilityName) {
        myFacilityQualifier = facilityName;
    }

    public String getFacilityQualifier() {
        return myFacilityQualifier;
    }

    /**
     * <p>Returns <code>true</code> <strong>iff</strong> an value of this type
     * would be acceptable where one of type <code>t</code> were required.</p>
     * 
     * @param t The required type.
     * 
     * @return <code>true</code> <strong>iff</strong> an value of this type
     *         would be acceptable where one of type <code>t</code> were 
     *         required.
     */
    public boolean acceptableFor(PTType t) {
        return equals(t);
    }
}

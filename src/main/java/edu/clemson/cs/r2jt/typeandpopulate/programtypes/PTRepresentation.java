/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.typeandpopulate.programtypes;

import edu.clemson.cs.r2jt.typeandpopulate.MTType;
import edu.clemson.cs.r2jt.typeandpopulate.entry.FacilityEntry;
import edu.clemson.cs.r2jt.typeandpopulate.entry.ProgramTypeDefinitionEntry;
import edu.clemson.cs.r2jt.typereasoning.TypeGraph;
import java.util.Map;

/**
 * <p>A <code>PTRepresentation</code> wraps an existing {@link PTType PTType} 
 * with additional information about a {@link PTFamily PTFamily} this type 
 * represents.  An instance of <code>PTRepresentation</code> is thus a special
 * case of its wrapped type that happens to be functioning as a representation
 * type.</p>
 */
public class PTRepresentation extends PTType {

    private final PTType myBaseType;
    private final ProgramTypeDefinitionEntry myFamily;

    public PTRepresentation(TypeGraph g, PTType baseType,
            ProgramTypeDefinitionEntry family) {
        super(g);

        myBaseType = baseType;
        myFamily = family;
    }

    public PTType getBaseType() {
        return myBaseType;
    }

    public ProgramTypeDefinitionEntry getFamily() {
        return myFamily;
    }

    @Override
    public MTType toMath() {
        return myBaseType.toMath();
    }

    @Override
    public PTType instantiateGenerics(
            Map<String, PTType> genericInstantiations,
            FacilityEntry instantiatingFacility) {

        throw new UnsupportedOperationException(this.getClass() + " cannot "
                + "be instantiated.");
    }

    @Override
    public boolean acceptableFor(PTType t) {
        boolean result = super.acceptableFor(t);

        if (!result) {
            result = myFamily.getProgramType().acceptableFor(t);
        }

        return result;
    }

    @Override
    public String toString() {
        return myFamily.getName() + " as " + myBaseType;
    }

    public String getFamilyName() {
        return myFamily.getName();
    }
}

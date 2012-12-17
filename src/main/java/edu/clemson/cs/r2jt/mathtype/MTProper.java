package edu.clemson.cs.r2jt.mathtype;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import edu.clemson.cs.r2jt.typereasoning.TypeGraph;

/**
 * <p>A proper type.  Any type that does not depend on other types.  I.e., it
 * is atomic.</p>
 */
public class MTProper extends MTType {

    private String myName;
    private MTType myType = null;
    private final boolean myKnownToContainOnlyMTypesFlag;

    public MTProper(TypeGraph g) {
        this(g, null, false, null);
    }

    public MTProper(TypeGraph g, boolean knownToContainOnlyMTypes) {
        this(g, null, knownToContainOnlyMTypes, null);
    }

    public MTProper(TypeGraph g, String name) {
        this(g, null, false, name);
    }

    public MTProper(TypeGraph g, MTType type, boolean knownToContainOnlyMTypes,
            String name) {
        super(g);
        myKnownToContainOnlyMTypesFlag = knownToContainOnlyMTypes;
        myType = type;
        myName = name;
    }

    @Override
    public boolean isKnownToContainOnlyMTypes() {
        return myKnownToContainOnlyMTypesFlag;
    }

    public String getName() {
        return myName;
    }

    public MTType getType() {
        return myType;
    }

    @Override
    public String toString() {
        String result;

        if (myName == null) {
            result = super.toString();
        }
        else {
            result = myName;
        }

        return result;
    }

    @Override
    public void accept(TypeVisitor v) {
        v.beginMTType(this);
        v.beginMTProper(this);

        v.beginChildren(this);
        v.endChildren(this);

        v.endMTProper(this);
        v.endMTType(this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<MTType> getComponentTypes() {
        return (List<MTType>) Collections.EMPTY_LIST;
    }

    @Override
    public MTType withComponentReplaced(int index, MTType newType) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public int getHashCode() {
        return objectReferenceHashCode();
    }
}

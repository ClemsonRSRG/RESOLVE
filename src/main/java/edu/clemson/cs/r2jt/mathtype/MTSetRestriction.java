package edu.clemson.cs.r2jt.mathtype;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

import edu.clemson.cs.r2jt.absyn.Exp;
import edu.clemson.cs.r2jt.typereasoning.TypeGraph;

public class MTSetRestriction extends MTAbstract<MTSetRestriction> {

    private MTType myBaseType;
    private String mySetVar;
    private Exp myRestriction;

    public MTSetRestriction(TypeGraph g, MTType baseType, String setVar,
            Exp restriction) {
        super(g);
        myBaseType = baseType;
        mySetVar = setVar;
        myRestriction = restriction;
    }

    @Override
    public boolean isKnownToContainOnlyMTypes() {
        return myBaseType.isKnownToContainOnlyMTypes();
    }

    @Override
    public boolean membersKnownToContainOnlyMTypes() {
        return myBaseType.membersKnownToContainOnlyMTypes();
    }

    @Override
    public String toString() {
        return "{" + mySetVar + " : " + myBaseType.toString() + " | "
                + myRestriction.toString() + "}";
    }

    @Override
    public void accept(TypeVisitor v) {
        v.beginMTType(this);
        v.beginMTAbstract(this);
        v.beginMTSetRestriction(this);

        v.beginChildren(this);

        myBaseType.accept(v);

        v.endChildren(this);

        v.endMTSetRestriction(this);
        v.endMTAbstract(this);
        v.endMTType(this);
    }

    @Override
    public List<MTType> getComponentTypes() {
        return Collections.unmodifiableList(Collections
                .singletonList(myBaseType));
    }

    @Override
    public MTType withComponentReplaced(int index, MTType newType) {
        if (index != 0) {
            throw new IndexOutOfBoundsException();
        }

        return new MTSetRestriction(getTypeGraph(), newType, mySetVar,
                myRestriction);
    }

    @Override
    public int getHashCode() {
        //This is fun.  At the moment MTSetRestrictions are not alpha-equivalent
        //to anything, including themselves, so the best thing we can do is
        //provide an integer that is maximally unlikely to be equal to any 
        //object's (including this one's!) hash.
        return (new Random()).nextInt();
    }
}

package edu.clemson.cs.r2jt.mathtype;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.clemson.cs.r2jt.typereasoning.TypeGraph;

public class MTIntersect extends MTAbstract<MTIntersect> {

    private final static int BASE_HASH = "MTIntersect".hashCode();

    private List<MTType> myMembers = new LinkedList<MTType>();

    public MTIntersect(TypeGraph g) {
        super(g);
    }

    public MTIntersect(TypeGraph g, List<MTType> elements) {
        this(g);
        myMembers.addAll(elements);
    }

    public MTIntersect(TypeGraph g, MTType... elements) {
        this(g, Arrays.asList(elements));
    }

    public void addMember(MTType t) {
        myMembers.add(t);
    }

    public boolean containsMember(MTType member) {
        return myMembers.contains(member);
    }

    @Override
    public boolean isKnownToContainOnlyMTypes() {
        Iterator<MTType> members = myMembers.iterator();
        while (members.hasNext()) {
            MTType member = members.next();
            if (!member.isKnownToContainOnlyMTypes()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean membersKnownToContainOnlyMTypes() {
        Iterator<MTType> members = myMembers.iterator();
        while (members.hasNext()) {
            MTType member = members.next();
            if (!member.membersKnownToContainOnlyMTypes()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("(");
        Iterator<MTType> members = myMembers.iterator();
        while (members.hasNext()) {
            MTType member = members.next();
            if (sb.length() > 1) {
                sb.append(" intersect ");
            }
            sb.append(member.toString());
        }
        sb.append(")");
        return sb.toString();
    }

    @Override
    public void accept(TypeVisitor v) {
        v.beginMTType(this);
        v.beginMTAbstract(this);
        v.beginMTIntersect(this);

        v.beginChildren(this);

        for (MTType t : myMembers) {
            t.accept(v);
        }

        v.endChildren(this);

        v.endMTIntersect(this);
        v.endMTAbstract(this);
        v.endMTType(this);
    }

    @Override
    public List<MTType> getComponentTypes() {
        return Collections.unmodifiableList(myMembers);
    }

    @Override
    public MTType withComponentReplaced(int index, MTType newType) {
        List<MTType> newMembers = new LinkedList<MTType>(myMembers);
        newMembers.set(index, newType);
        return new MTIntersect(getTypeGraph(), newMembers);
    }

    @Override
    public int getHashCode() {
        int result = BASE_HASH;

        for (MTType t : myMembers) {
            result *= 45;
            result += t.hashCode();
        }

        return result;
    }
}

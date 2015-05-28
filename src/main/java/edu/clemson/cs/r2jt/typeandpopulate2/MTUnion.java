/**
 * MTUnion.java
 * ---------------------------------
 * Copyright (c) 2015
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.typeandpopulate2;

import edu.clemson.cs.r2jt.typereasoning2.TypeGraph;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class MTUnion extends MTAbstract<MTUnion> {

    private final static int BASE_HASH = "MTUnion".hashCode();

    private List<MTType> myMembers = new LinkedList<MTType>();

    public MTUnion(TypeGraph g) {
        super(g);
    }

    public MTUnion(TypeGraph g, List<MTType> members) {
        super(g);

        myMembers.addAll(members);
    }

    public MTUnion(TypeGraph g, MTType... members) {
        this(g, Arrays.asList(members));
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
                sb.append(" union ");
            }
            sb.append(member.toString());
        }
        sb.append(")");
        return sb.toString();
    }

    @Override
    public void acceptOpen(TypeVisitor v) {
        v.beginMTType(this);
        v.beginMTAbstract(this);
        v.beginMTUnion(this);
    }

    @Override
    public void accept(TypeVisitor v) {
        acceptOpen(v);

        v.beginChildren(this);

        for (MTType t : myMembers) {
            t.accept(v);
        }

        v.endChildren(this);

        acceptClose(v);
    }

    @Override
    public void acceptClose(TypeVisitor v) {
        v.endMTUnion(this);
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
        return new MTUnion(getTypeGraph(), newMembers);
    }

    @Override
    public int getHashCode() {
        int result = BASE_HASH;

        for (MTType t : myMembers) {
            result *= 61;
            result += t.hashCode();
        }

        return result;
    }
}

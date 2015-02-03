/*
 * [The "BSD license"]
 * Copyright (c) 2015 Clemson University
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 
 * 3. The name of the author may not be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package edu.clemson.cs.r2jt.typeandpopulate;

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
    public void acceptOpen(TypeVisitor v) {
        v.beginMTType(this);
        v.beginMTAbstract(this);
        v.beginMTIntersect(this);
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

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
    public void acceptOpen(TypeVisitor v) {
        v.beginMTType(this);
        v.beginMTAbstract(this);
        v.beginMTSetRestriction(this);
    }

    @Override
    public void accept(TypeVisitor v) {
        acceptOpen(v);

        v.beginChildren(this);

        myBaseType.accept(v);

        v.endChildren(this);

        acceptClose(v);
    }

    @Override
    public void acceptClose(TypeVisitor v) {
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

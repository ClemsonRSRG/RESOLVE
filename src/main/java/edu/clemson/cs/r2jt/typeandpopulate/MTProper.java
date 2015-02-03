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
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
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
    public void acceptOpen(TypeVisitor v) {
        v.beginMTType(this);
        v.beginMTProper(this);
    }

    @Override
    public void accept(TypeVisitor v) {
        acceptOpen(v);

        v.beginChildren(this);
        v.endChildren(this);

        acceptClose(v);
    }

    @Override
    public void acceptClose(TypeVisitor v) {
        v.endMTProper(this);
        v.endMTType(this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<MTType> getComponentTypes() {
        return Collections.EMPTY_LIST;
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

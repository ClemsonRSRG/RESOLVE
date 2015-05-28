/**
 * MTSetRestriction.java
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

import edu.clemson.cs.r2jt.absynnew.expr.ExprAST;
import edu.clemson.cs.r2jt.typereasoning2.TypeGraph;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MTSetRestriction extends MTAbstract<MTSetRestriction> {

    private MTType myBaseType;
    private String mySetVar;
    private ExprAST myRestriction;

    public MTSetRestriction(TypeGraph g, MTType baseType, String setVar,
            ExprAST restriction) {
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

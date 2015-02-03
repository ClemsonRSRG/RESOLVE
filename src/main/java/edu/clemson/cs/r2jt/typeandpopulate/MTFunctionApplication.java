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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import edu.clemson.cs.r2jt.typereasoning.TypeGraph;

public class MTFunctionApplication extends MTAbstract<MTFunctionApplication> {

    private static final int BASE_HASH = "MTFunctionApplication".hashCode();

    private final MTFunction myFunction;
    private List<MTType> myArguments;
    private String myName;

    private List<MTType> myComponents;

    public MTFunctionApplication(TypeGraph g, MTFunction f, String name,
            MTType... arguments) {
        super(g);

        myFunction = f;
        myName = name;
        myArguments = new LinkedList<MTType>();
        for (int i = 0; i < arguments.length; ++i) {
            myArguments.add(arguments[i]);
        }

        setUpComponents();
    }

    public MTFunctionApplication(TypeGraph g, MTFunction f, String name,
            List<MTType> arguments) {
        super(g);

        myFunction = f;
        myName = name;
        myArguments = new LinkedList<MTType>();
        myArguments.addAll(arguments);

        setUpComponents();
    }

    public MTFunctionApplication(TypeGraph g, MTFunction f,
            List<MTType> arguments) {
        super(g);

        myFunction = f;
        myArguments = new LinkedList<MTType>(arguments);
        myName = "\\lambda";

        setUpComponents();
    }

    private void setUpComponents() {
        List<MTType> result = new LinkedList<MTType>();

        result.add(myFunction);
        result.addAll(myArguments);

        myComponents = Collections.unmodifiableList(result);
    }

    public void addArgument(MTType argument) {
        myArguments.add(argument);
    }

    public MTFunction getFunction() {
        return myFunction;
    }

    public MTType getArgument(int i) {
        return myArguments.get(i);
    }

    public int getArgumentCount() {
        return myArguments.size();
    }

    public List<MTType> getArguments() {
        return Collections.unmodifiableList(myArguments);
    }

    @Override
    public boolean isKnownToContainOnlyMTypes() {
        //Note that, effectively, we represent an instance of the range of our
        //function.  Thus, we're known to contain only MTypes if the function's
        //range's members are known only to contain MTypes.

        return myFunction.getRange().membersKnownToContainOnlyMTypes();
    }

    @Override
    public boolean membersKnownToContainOnlyMTypes() {
        boolean result = true;
        Iterator<MTType> arguments = myArguments.iterator();
        while (arguments.hasNext()) {
            result &= arguments.next().isKnownToContainOnlyMTypes();
        }
        return result
                && myFunction
                        .applicationResultsKnownToContainOnlyRestrictions();
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        if (myArguments.size() == 2) {
            sb.append(myArguments.get(0).toString());
            sb.append(" ");
            sb.append(myName);
            sb.append(" ");
            sb.append(myArguments.get(1).toString());
        }
        else if (myArguments.size() == 1 && myName.contains("_")) {
            // ^^^ super hacky way to detect outfix
            sb.append(myName.replace("_", myArguments.get(0).toString()));
        }
        else {
            sb.append(myName);
            sb.append("(");
            Iterator<MTType> arguments = myArguments.iterator();
            while (arguments.hasNext()) {
                MTType argument = arguments.next();
                if (argument != myArguments.get(0)) {
                    sb.append(", ");
                }
                sb.append(argument.toString());
            }
            sb.append(")");
        }
        return sb.toString();
    }

    @Override
    public void acceptOpen(TypeVisitor v) {
        v.beginMTType(this);
        v.beginMTAbstract(this);
        v.beginMTFunctionApplication(this);
    }

    @Override
    public void accept(TypeVisitor v) {
        acceptOpen(v);

        v.beginChildren(this);

        myFunction.accept(v);

        for (MTType arg : myArguments) {
            arg.accept(v);
        }

        v.endChildren(this);

        acceptClose(v);
    }

    @Override
    public void acceptClose(TypeVisitor v) {
        v.endMTFunctionApplication(this);
        v.endMTAbstract(this);
        v.endMTType(this);
    }

    @Override
    public List<MTType> getComponentTypes() {
        return myComponents;
    }

    public String getName() {
        return myName;
    }

    @Override
    public MTType withComponentReplaced(int index, MTType newType) {
        MTFunction newFunction = myFunction;
        List<MTType> newArguments = myArguments;

        if (index == 0) {
            newFunction = (MTFunction) newType;
        }
        else {
            newArguments = new LinkedList<MTType>(newArguments);
            newArguments.set(index - 1, newType);
        }

        return new MTFunctionApplication(getTypeGraph(), newFunction, myName,
                newArguments);
    }

    @Override
    public int getHashCode() {
        int result = BASE_HASH + myFunction.getHashCode() + myName.hashCode();

        for (MTType t : myArguments) {
            result *= 73;
            result += t.getHashCode();
        }

        return result;
    }
}

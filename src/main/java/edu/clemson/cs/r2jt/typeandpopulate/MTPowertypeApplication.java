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

import edu.clemson.cs.r2jt.typereasoning.TypeGraph;

public class MTPowertypeApplication extends MTFunctionApplication {

    public MTPowertypeApplication(TypeGraph g, MTType argument) {
        super(g, g.POWERTYPE, "Powerset", argument);
    }

    @Override
    public boolean isKnownToContainOnlyMTypes() {
        //The powertype is, by definition, a container of containers
        return true;
    }

    @Override
    public boolean membersKnownToContainOnlyMTypes() {
        //I'm the container of all sub-containers of my argument.  My members
        //are containers of members from the original argument.
        return getArgument(0).isKnownToContainOnlyMTypes();
    }

    @Override
    public void accept(TypeVisitor v) {
        v.beginMTType(this);
        v.beginMTAbstract(this);
        v.beginMTFunctionApplication(this);
        v.beginMTPowertypeApplication(this);

        v.beginChildren(this);

        getFunction().accept(v);

        for (MTType arg : getArguments()) {
            arg.accept(v);
        }

        v.endChildren(this);

        v.endMTPowertypeApplication(this);
        v.endMTFunctionApplication(this);
        v.endMTAbstract(this);
        v.endMTType(this);
    }

    @Override
    public MTType withComponentReplaced(int index, MTType newType) {
        MTType result;

        switch (index) {
        case 0:
            result =
                    new MTFunctionApplication(getTypeGraph(),
                            (MTFunction) newType, getArguments());
            break;
        case 1:
            result = new MTPowertypeApplication(getTypeGraph(), newType);
            break;
        default:
            throw new IndexOutOfBoundsException("" + index);
        }

        return result;
    }

    /*@Override
    public boolean bindsTo(MTType type, Exp bindingExpr) {
    	return this.getArgument(0).bindsToWithCoercion(type, bindingExpr);
    }*/
}

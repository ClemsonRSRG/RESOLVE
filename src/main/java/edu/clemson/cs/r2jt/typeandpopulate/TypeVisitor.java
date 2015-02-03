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

public abstract class TypeVisitor {

    public void beginMTType(MTType t) {}

    public void beginMTAbstract(MTAbstract<?> t) {}

    public void beginMTBigUnion(MTBigUnion t) {}

    public void beginMTCartesian(MTCartesian t) {}

    public void beginMTFunction(MTFunction t) {}

    public void beginMTFunctionApplication(MTFunctionApplication t) {}

    public void beginMTIntersect(MTIntersect t) {}

    public void beginMTPowertypeApplication(MTPowertypeApplication t) {}

    public void beginMTProper(MTProper t) {}

    public void beginMTSetRestriction(MTSetRestriction t) {}

    public void beginMTUnion(MTUnion t) {}

    public void beginMTNamed(MTNamed t) {}

    public void beginMTGeneric(MTGeneric t) {}

    public void beginChildren(MTType t) {}

    public void endChildren(MTType t) {}

    public void endMTType(MTType t) {}

    public void endMTAbstract(MTAbstract<?> t) {}

    public void endMTBigUnion(MTBigUnion t) {}

    public void endMTCartesian(MTCartesian t) {}

    public void endMTFunction(MTFunction t) {}

    public void endMTFunctionApplication(MTFunctionApplication t) {}

    public void endMTIntersect(MTIntersect t) {}

    public void endMTPowertypeApplication(MTPowertypeApplication t) {}

    public void endMTProper(MTProper t) {}

    public void endMTSetRestriction(MTSetRestriction t) {}

    public void endMTUnion(MTUnion t) {}

    public void endMTNamed(MTNamed t) {}

    public void endMTGeneric(MTGeneric t) {}
}

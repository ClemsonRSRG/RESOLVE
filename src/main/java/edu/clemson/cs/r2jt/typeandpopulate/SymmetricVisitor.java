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

import java.util.Iterator;
import java.util.List;

/**
 * <p>Note that instances of SymmetricVisitor are not thread-safe.</p>
 */
public class SymmetricVisitor {

    private static final ClassCastException CLASS_CAST_EXCEPTION =
            new ClassCastException();

    private final Multiplexer myMultiplexer = new Multiplexer();
    private final MidMultiplexer myMidMultiplexer = new MidMultiplexer();

    public final boolean visit(MTType t1, MTType t2) {
        boolean visitSiblings = true;

        myMultiplexer.setOtherType(t2);
        myMidMultiplexer.setOtherType(t2);
        try {
            t1.acceptOpen(myMultiplexer);

            if (t1.getClass() != t2.getClass()) {
                throw CLASS_CAST_EXCEPTION;
            }

            if (myMultiplexer.getReturn()) {
                List<MTType> t1Components = t1.getComponentTypes();
                List<MTType> t2Components = t2.getComponentTypes();
                if (t1Components.size() != t2Components.size()) {
                    mismatch(t1, t2);
                }
                else {
                    boolean first = true;

                    Iterator<MTType> t1ComponentIter = t1Components.iterator();
                    Iterator<MTType> t2ComponentIter = t2Components.iterator();
                    while (visitSiblings && t1ComponentIter.hasNext()) {
                        if (first) {
                            first = false;
                        }
                        else {
                            t1.acceptOpen(myMidMultiplexer);
                        }

                        visitSiblings =
                                visit(t1ComponentIter.next(), t2ComponentIter
                                        .next());

                        myMultiplexer.setOtherType(t2);
                        myMidMultiplexer.setOtherType(t2);
                    }
                }
            }

            t1.acceptClose(myMultiplexer);
            visitSiblings = myMultiplexer.getReturn();
        }
        catch (ClassCastException cce) {
            visitSiblings = mismatch(t1, t2);
        }

        return visitSiblings;
    }

    private class Multiplexer extends TypeVisitor {

        private MTType myOtherType;
        private boolean myReturn;

        public Multiplexer(MTType otherType) {
            myOtherType = otherType;
        }

        public Multiplexer() {

        }

        public void setOtherType(MTType t) {
            myOtherType = t;
        }

        public boolean getReturn() {
            return myReturn;
        }

        public void setReturn(boolean returnVal) {
            myReturn = returnVal;
        }

        public void beginMTType(MTType t) {
            myReturn = SymmetricVisitor.this.beginMTType(t, myOtherType);
        }

        public void beginMTAbstract(MTAbstract<?> t) {
            myReturn =
                    SymmetricVisitor.this.beginMTAbstract(t,
                            (MTAbstract) myOtherType);
        }

        public void beginMTBigUnion(MTBigUnion t) {
            myReturn =
                    SymmetricVisitor.this.beginMTBigUnion(t,
                            (MTBigUnion) myOtherType);
        }

        public void beginMTCartesian(MTCartesian t) {
            myReturn =
                    SymmetricVisitor.this.beginMTCartesian(t,
                            (MTCartesian) myOtherType);
        }

        public void beginMTFunction(MTFunction t) {
            myReturn =
                    SymmetricVisitor.this.beginMTFunction(t,
                            (MTFunction) myOtherType);
        }

        public void beginMTFunctionApplication(MTFunctionApplication t) {
            myReturn =
                    SymmetricVisitor.this.beginMTFunctionApplication(t,
                            (MTFunctionApplication) myOtherType);
        }

        public void beginMTIntersect(MTIntersect t) {
            myReturn =
                    SymmetricVisitor.this.beginMTIntersect(t,
                            (MTIntersect) myOtherType);
        }

        public void beginMTPowertypeApplication(MTPowertypeApplication t) {
            myReturn =
                    SymmetricVisitor.this.beginMTPowertypeApplication(t,
                            (MTPowertypeApplication) myOtherType);
        }

        public void beginMTProper(MTProper t) {
            myReturn =
                    SymmetricVisitor.this.beginMTProper(t,
                            (MTProper) myOtherType);
        }

        public void beginMTSetRestriction(MTSetRestriction t) {
            myReturn =
                    SymmetricVisitor.this.beginMTSetRestriction(t,
                            (MTSetRestriction) myOtherType);
        }

        public void beginMTUnion(MTUnion t) {
            myReturn =
                    SymmetricVisitor.this
                            .beginMTUnion(t, (MTUnion) myOtherType);
        }

        public void beginMTNamed(MTNamed t) {
            myReturn =
                    SymmetricVisitor.this
                            .beginMTNamed(t, (MTNamed) myOtherType);
        }

        public void beginMTGeneric(MTGeneric t) {
            myReturn =
                    SymmetricVisitor.this.beginMTGeneric(t,
                            (MTGeneric) myOtherType);
        }

        public void endMTType(MTType t) {
            myReturn = SymmetricVisitor.this.endMTType(t, (MTType) myOtherType);
        }

        public void endMTAbstract(MTAbstract<?> t) {
            myReturn =
                    SymmetricVisitor.this.endMTAbstract(t,
                            (MTAbstract) myOtherType);
        }

        public void endMTBigUnion(MTBigUnion t) {
            myReturn =
                    SymmetricVisitor.this.endMTBigUnion(t,
                            (MTBigUnion) myOtherType);
        }

        public void endMTCartesian(MTCartesian t) {
            myReturn =
                    SymmetricVisitor.this.endMTCartesian(t,
                            (MTCartesian) myOtherType);
        }

        public void endMTFunction(MTFunction t) {
            myReturn =
                    SymmetricVisitor.this.endMTFunction(t,
                            (MTFunction) myOtherType);
        }

        public void endMTFunctionApplication(MTFunctionApplication t) {
            myReturn =
                    SymmetricVisitor.this.endMTFunctionApplication(t,
                            (MTFunctionApplication) myOtherType);
        }

        public void endMTIntersect(MTIntersect t) {
            myReturn =
                    SymmetricVisitor.this.endMTIntersect(t,
                            (MTIntersect) myOtherType);
        }

        public void endMTPowertypeApplication(MTPowertypeApplication t) {
            myReturn =
                    SymmetricVisitor.this.endMTPowertypeApplication(t,
                            (MTPowertypeApplication) myOtherType);
        }

        public void endMTProper(MTProper t) {
            myReturn =
                    SymmetricVisitor.this
                            .endMTProper(t, (MTProper) myOtherType);
        }

        public void endMTSetRestriction(MTSetRestriction t) {
            myReturn =
                    SymmetricVisitor.this.endMTSetRestriction(t,
                            (MTSetRestriction) myOtherType);
        }

        public void endMTUnion(MTUnion t) {
            myReturn =
                    SymmetricVisitor.this.endMTUnion(t, (MTUnion) myOtherType);
        }

        public void endMTNamed(MTNamed t) {
            myReturn =
                    SymmetricVisitor.this.endMTNamed(t, (MTNamed) myOtherType);
        }

        public void endMTGeneric(MTGeneric t) {
            myReturn =
                    SymmetricVisitor.this.endMTGeneric(t,
                            (MTGeneric) myOtherType);
        }
    }

    private class MidMultiplexer extends TypeVisitor {

        private MTType myOtherType;
        private boolean myReturn;

        public MidMultiplexer(MTType otherType) {
            myOtherType = otherType;
        }

        public MidMultiplexer() {

        }

        public void setOtherType(MTType t) {
            myOtherType = t;
        }

        public boolean getReturn() {
            return myReturn;
        }

        public void setReturn(boolean returnVal) {
            myReturn = returnVal;
        }

        public void beginMTType(MTType t) {
            myReturn = midMTType(t, myOtherType);
        }

        public void beginMTAbstract(MTAbstract<?> t) {
            myReturn = midMTAbstract(t, (MTAbstract) myOtherType);
        }

        public void beginMTBigUnion(MTBigUnion t) {
            myReturn = midMTBigUnion(t, (MTBigUnion) myOtherType);
        }

        public void beginMTCartesian(MTCartesian t) {
            myReturn = midMTCartesian(t, (MTCartesian) myOtherType);
        }

        public void beginMTFunction(MTFunction t) {
            myReturn = midMTFunction(t, (MTFunction) myOtherType);
        }

        public void beginMTFunctionApplication(MTFunctionApplication t) {
            myReturn =
                    midMTFunctionApplication(t,
                            (MTFunctionApplication) myOtherType);
        }

        public void beginMTIntersect(MTIntersect t) {
            myReturn = midMTIntersect(t, (MTIntersect) myOtherType);
        }

        public void beginMTPowertypeApplication(MTPowertypeApplication t) {
            myReturn =
                    midMTPowertypeApplication(t,
                            (MTPowertypeApplication) myOtherType);
        }

        public void beginMTProper(MTProper t) {
            myReturn = midMTProper(t, (MTProper) myOtherType);
        }

        public void beginMTSetRestriction(MTSetRestriction t) {
            myReturn = midMTSetRestriction(t, (MTSetRestriction) myOtherType);
        }

        public void beginMTUnion(MTUnion t) {
            myReturn = midMTUnion(t, (MTUnion) myOtherType);
        }

        public void beginMTNamed(MTNamed t) {
            myReturn = midMTNamed(t, (MTNamed) myOtherType);
        }

        public void beginMTGeneric(MTGeneric t) {
            myReturn = midMTGeneric(t, (MTGeneric) myOtherType);
        }
    }

    public boolean beginMTType(MTType t1, MTType t2) {
        return true;
    }

    public boolean beginMTAbstract(MTAbstract<?> t1, MTAbstract<?> t2) {
        return true;
    }

    public boolean beginMTBigUnion(MTBigUnion t1, MTBigUnion t2) {
        return true;
    }

    public boolean beginMTCartesian(MTCartesian t1, MTCartesian t2) {
        return true;
    }

    public boolean beginMTFunction(MTFunction t1, MTFunction t2) {
        return true;
    }

    public boolean beginMTFunctionApplication(MTFunctionApplication t1,
            MTFunctionApplication t2) {
        return true;
    }

    public boolean beginMTIntersect(MTIntersect t1, MTIntersect t2) {
        return true;
    }

    public boolean beginMTPowertypeApplication(MTPowertypeApplication t1,
            MTPowertypeApplication t2) {
        return true;
    }

    public boolean beginMTProper(MTProper t1, MTProper t2) {
        return true;
    }

    public boolean beginMTSetRestriction(MTSetRestriction t1,
            MTSetRestriction t2) {
        return true;
    }

    public boolean beginMTUnion(MTUnion t1, MTUnion t2) {
        return true;
    }

    public boolean beginMTNamed(MTNamed t1, MTNamed t2) {
        return true;
    }

    public boolean beginMTGeneric(MTGeneric t1, MTGeneric t2) {
        return true;
    }

    public boolean midMTType(MTType t1, MTType t2) {
        return true;
    }

    public boolean midMTAbstract(MTAbstract<?> t1, MTAbstract<?> t2) {
        return true;
    }

    public boolean midMTBigUnion(MTBigUnion t1, MTBigUnion t2) {
        return true;
    }

    public boolean midMTCartesian(MTCartesian t1, MTCartesian t2) {
        return true;
    }

    public boolean midMTFunction(MTFunction t1, MTFunction t2) {
        return true;
    }

    public boolean midMTFunctionApplication(MTFunctionApplication t1,
            MTFunctionApplication t2) {
        return true;
    }

    public boolean midMTIntersect(MTIntersect t1, MTIntersect t2) {
        return true;
    }

    public boolean midMTPowertypeApplication(MTPowertypeApplication t1,
            MTPowertypeApplication t2) {
        return true;
    }

    public boolean midMTProper(MTProper t1, MTProper t2) {
        return true;
    }

    public boolean midMTSetRestriction(MTSetRestriction t1, MTSetRestriction t2) {
        return true;
    }

    public boolean midMTUnion(MTUnion t1, MTUnion t2) {
        return true;
    }

    public boolean midMTNamed(MTNamed t1, MTNamed t2) {
        return true;
    }

    public boolean midMTGeneric(MTGeneric t1, MTGeneric t2) {
        return true;
    }

    public boolean mismatch(MTType t1, MTType t2) {
        return true;
    }

    public boolean endMTType(MTType t1, MTType t2) {
        return true;
    }

    public boolean endMTAbstract(MTAbstract<?> t1, MTAbstract<?> t2) {
        return true;
    }

    public boolean endMTBigUnion(MTBigUnion t1, MTBigUnion t2) {
        return true;
    }

    public boolean endMTCartesian(MTCartesian t1, MTCartesian t2) {
        return true;
    }

    public boolean endMTFunction(MTFunction t1, MTFunction t2) {
        return true;
    }

    public boolean endMTFunctionApplication(MTFunctionApplication t1,
            MTFunctionApplication t2) {
        return true;
    }

    public boolean endMTIntersect(MTIntersect t1, MTIntersect t2) {
        return true;
    }

    public boolean endMTPowertypeApplication(MTPowertypeApplication t1,
            MTPowertypeApplication t2) {
        return true;
    }

    public boolean endMTProper(MTProper t1, MTProper t2) {
        return true;
    }

    public boolean endMTSetRestriction(MTSetRestriction t1, MTSetRestriction t2) {
        return true;
    }

    public boolean endMTUnion(MTUnion t1, MTUnion t2) {
        return true;
    }

    public boolean endMTNamed(MTNamed t1, MTNamed t2) {
        return true;
    }

    public boolean endMTGeneric(MTGeneric t1, MTGeneric t2) {
        return true;
    }

    private interface Procedure {

        public void execute();
    }

    private static class NonSymmetricalNodeException extends RuntimeException {

        private static final long serialVersionUID = 1L;
    }
}

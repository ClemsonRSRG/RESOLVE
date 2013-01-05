/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.mathtype;

/**
 *
 * @author hamptos
 */
public class SymmetricVisitor2 {
    
    public final boolean visit(MTType t1, MTType t2) {
        beginMTType(t1, t2);

        Stage2Visitor v = new Stage2Visitor(t2);
        t1.accept(v);
        
        endMTType(t1, t2);
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
    
    private class Stage2Visitor extends TypeVisitor {
        
        private final MTType myOtherType;
        
        private Stage2Visitor(MTType other) {
            myOtherType = other;
        }
        
        public void beginMTType(MTType t) {
            beginMTType
        }

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
    
    private static class Stage1Visitor extends TypeVisitor {
        
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
}

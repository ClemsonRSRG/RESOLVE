/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.clemson.cs.r2jt.typeandpopulate;

import edu.clemson.cs.r2jt.typeandpopulate.MTCartesian.Element;
import edu.clemson.cs.r2jt.typereasoning.TypeGraph;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author hamptos
 */
public class SymmetricVisitor2Test {

    @Test
    public void printOrder() {
        TypeGraph g = new TypeGraph();
        MTFunction f =
                new MTFunction(g, g.BOOLEAN, new MTCartesian(g, new Element(
                        new MTFunction(g, g.SET, g.Z)), new Element(g.BOOLEAN)));

        System.out.println("SYMMETRIC VISITOR");
        new Printer().visit(f, f);

        System.out.println("SYMMETRIC VISITOR 2");
        new Printer2().visit(f, f);
    }

    private class Printer extends SymmetricVisitor {

        public boolean beginMTType(MTType t1, MTType t2) {
            System.out.println("beginMTType");

            return true;
        }

        public boolean beginMTAbstract(MTAbstract<?> t1, MTAbstract<?> t2) {
            System.out.println("beginMTAbstract");

            return true;
        }

        public boolean beginMTBigUnion(MTBigUnion t1, MTBigUnion t2) {
            System.out.println("beginMTBigUnion");

            return true;
        }

        public boolean beginMTCartesian(MTCartesian t1, MTCartesian t2) {
            System.out.println("beginMTCartesian");

            return true;
        }

        public boolean beginMTFunction(MTFunction t1, MTFunction t2) {
            System.out.println("beginMTFunction");

            return true;
        }

        public boolean beginMTFunctionApplication(MTFunctionApplication t1,
                MTFunctionApplication t2) {
            System.out.println("beginMTFunctionApplication");

            return true;
        }

        public boolean beginMTIntersect(MTIntersect t1, MTIntersect t2) {
            System.out.println("beginMTIntersect");

            return true;
        }

        public boolean beginMTPowertypeApplication(MTPowertypeApplication t1,
                MTPowertypeApplication t2) {
            System.out.println("beginMTPowertypeApplication");

            return true;
        }

        public boolean beginMTProper(MTProper t1, MTProper t2) {
            System.out.println("beginMTProper");

            return true;
        }

        public boolean beginMTSetRestriction(MTSetRestriction t1,
                MTSetRestriction t2) {
            System.out.println("beginMTSetRestriction");

            return true;
        }

        public boolean beginMTUnion(MTUnion t1, MTUnion t2) {
            System.out.println("beginMTUnion");

            return true;
        }

        public boolean beginMTNamed(MTNamed t1, MTNamed t2) {
            System.out.println("beginMTNamed");

            return true;
        }

        public boolean beginMTGeneric(MTGeneric t1, MTGeneric t2) {
            System.out.println("beginMTGeneric");

            return true;
        }

        public boolean midMTType(MTType t1, MTType t2) {
            System.out.println("midMTType");

            return true;
        }

        public boolean midMTAbstract(MTAbstract<?> t1, MTAbstract<?> t2) {
            System.out.println("midMTAbstract");

            return true;
        }

        public boolean midMTBigUnion(MTBigUnion t1, MTBigUnion t2) {
            System.out.println("midMTBigUnion");

            return true;
        }

        public boolean midMTCartesian(MTCartesian t1, MTCartesian t2) {
            System.out.println("midMTCartesian");

            return true;
        }

        public boolean midMTFunction(MTFunction t1, MTFunction t2) {
            System.out.println("midMTFunction");

            return true;
        }

        public boolean midMTFunctionApplication(MTFunctionApplication t1,
                MTFunctionApplication t2) {
            System.out.println("midMTFunctionApplication");

            return true;
        }

        public boolean midMTIntersect(MTIntersect t1, MTIntersect t2) {
            System.out.println("midMTIntersect");

            return true;
        }

        public boolean midMTPowertypeApplication(MTPowertypeApplication t1,
                MTPowertypeApplication t2) {
            System.out.println("midMTPowertypeApplication");

            return true;
        }

        public boolean midMTProper(MTProper t1, MTProper t2) {
            System.out.println("midMTProper");

            return true;
        }

        public boolean midMTSetRestriction(MTSetRestriction t1,
                MTSetRestriction t2) {
            System.out.println("midMTSetRestriction");

            return true;
        }

        public boolean midMTUnion(MTUnion t1, MTUnion t2) {
            System.out.println("midMTUnion");

            return true;
        }

        public boolean midMTNamed(MTNamed t1, MTNamed t2) {
            System.out.println("midMTNamed");

            return true;
        }

        public boolean midMTGeneric(MTGeneric t1, MTGeneric t2) {
            System.out.println("midMTGeneric");

            return true;
        }

        public boolean mismatch(MTType t1, MTType t2) {
            System.out.println("mismatch");

            return true;
        }

        public boolean endMTType(MTType t1, MTType t2) {
            System.out.println("endMTType");

            return true;
        }

        public boolean endMTAbstract(MTAbstract<?> t1, MTAbstract<?> t2) {
            System.out.println("endMTAbstract");

            return true;
        }

        public boolean endMTBigUnion(MTBigUnion t1, MTBigUnion t2) {
            System.out.println("endMTBigUnion");

            return true;
        }

        public boolean endMTCartesian(MTCartesian t1, MTCartesian t2) {
            System.out.println("endMTCartesian");

            return true;
        }

        public boolean endMTFunction(MTFunction t1, MTFunction t2) {
            System.out.println("endMTFunction");

            return true;
        }

        public boolean endMTFunctionApplication(MTFunctionApplication t1,
                MTFunctionApplication t2) {
            System.out.println("endMTFunctionApplication");

            return true;
        }

        public boolean endMTIntersect(MTIntersect t1, MTIntersect t2) {
            System.out.println("endMTIntersect");

            return true;
        }

        public boolean endMTPowertypeApplication(MTPowertypeApplication t1,
                MTPowertypeApplication t2) {
            System.out.println("endMTPowertypeApplication");

            return true;
        }

        public boolean endMTProper(MTProper t1, MTProper t2) {
            System.out.println("endMTProper");

            return true;
        }

        public boolean endMTSetRestriction(MTSetRestriction t1,
                MTSetRestriction t2) {
            System.out.println("endMTSetRestriction");

            return true;
        }

        public boolean endMTUnion(MTUnion t1, MTUnion t2) {
            System.out.println("endMTUnion");

            return true;
        }

        public boolean endMTNamed(MTNamed t1, MTNamed t2) {
            System.out.println("endMTNamed");

            return true;
        }

        public boolean endMTGeneric(MTGeneric t1, MTGeneric t2) {
            System.out.println("endMTGeneric");

            return true;
        }
    }

    private class Printer2 extends SymmetricVisitor {

        public boolean beginMTType(MTType t1, MTType t2) {
            System.out.println("beginMTType");

            return true;
        }

        public boolean beginMTAbstract(MTAbstract<?> t1, MTAbstract<?> t2) {
            System.out.println("beginMTAbstract");

            return true;
        }

        public boolean beginMTBigUnion(MTBigUnion t1, MTBigUnion t2) {
            System.out.println("beginMTBigUnion");

            return true;
        }

        public boolean beginMTCartesian(MTCartesian t1, MTCartesian t2) {
            System.out.println("beginMTCartesian");

            return true;
        }

        public boolean beginMTFunction(MTFunction t1, MTFunction t2) {
            System.out.println("beginMTFunction");

            return true;
        }

        public boolean beginMTFunctionApplication(MTFunctionApplication t1,
                MTFunctionApplication t2) {
            System.out.println("beginMTFunctionApplication");

            return true;
        }

        public boolean beginMTIntersect(MTIntersect t1, MTIntersect t2) {
            System.out.println("beginMTIntersect");

            return true;
        }

        public boolean beginMTPowertypeApplication(MTPowertypeApplication t1,
                MTPowertypeApplication t2) {
            System.out.println("beginMTPowertypeApplication");

            return true;
        }

        public boolean beginMTProper(MTProper t1, MTProper t2) {
            System.out.println("beginMTProper");

            return true;
        }

        public boolean beginMTSetRestriction(MTSetRestriction t1,
                MTSetRestriction t2) {
            System.out.println("beginMTSetRestriction");

            return true;
        }

        public boolean beginMTUnion(MTUnion t1, MTUnion t2) {
            System.out.println("beginMTUnion");

            return true;
        }

        public boolean beginMTNamed(MTNamed t1, MTNamed t2) {
            System.out.println("beginMTNamed");

            return true;
        }

        public boolean beginMTGeneric(MTGeneric t1, MTGeneric t2) {
            System.out.println("beginMTGeneric");

            return true;
        }

        public boolean midMTType(MTType t1, MTType t2) {
            System.out.println("midMTType");

            return true;
        }

        public boolean midMTAbstract(MTAbstract<?> t1, MTAbstract<?> t2) {
            System.out.println("midMTAbstract");

            return true;
        }

        public boolean midMTBigUnion(MTBigUnion t1, MTBigUnion t2) {
            System.out.println("midMTBigUnion");

            return true;
        }

        public boolean midMTCartesian(MTCartesian t1, MTCartesian t2) {
            System.out.println("midMTCartesian");

            return true;
        }

        public boolean midMTFunction(MTFunction t1, MTFunction t2) {
            System.out.println("midMTFunction");

            return true;
        }

        public boolean midMTFunctionApplication(MTFunctionApplication t1,
                MTFunctionApplication t2) {
            System.out.println("midMTFunctionApplication");

            return true;
        }

        public boolean midMTIntersect(MTIntersect t1, MTIntersect t2) {
            System.out.println("midMTIntersect");

            return true;
        }

        public boolean midMTPowertypeApplication(MTPowertypeApplication t1,
                MTPowertypeApplication t2) {
            System.out.println("midMTPowertypeApplication");

            return true;
        }

        public boolean midMTProper(MTProper t1, MTProper t2) {
            System.out.println("midMTProper");

            return true;
        }

        public boolean midMTSetRestriction(MTSetRestriction t1,
                MTSetRestriction t2) {
            System.out.println("midMTSetRestriction");

            return true;
        }

        public boolean midMTUnion(MTUnion t1, MTUnion t2) {
            System.out.println("midMTUnion");

            return true;
        }

        public boolean midMTNamed(MTNamed t1, MTNamed t2) {
            System.out.println("midMTNamed");

            return true;
        }

        public boolean midMTGeneric(MTGeneric t1, MTGeneric t2) {
            System.out.println("midMTGeneric");

            return true;
        }

        public boolean mismatch(MTType t1, MTType t2) {
            System.out.println("mismatch");

            return true;
        }

        public boolean endMTType(MTType t1, MTType t2) {
            System.out.println("endMTType");

            return true;
        }

        public boolean endMTAbstract(MTAbstract<?> t1, MTAbstract<?> t2) {
            System.out.println("endMTAbstract");

            return true;
        }

        public boolean endMTBigUnion(MTBigUnion t1, MTBigUnion t2) {
            System.out.println("endMTBigUnion");

            return true;
        }

        public boolean endMTCartesian(MTCartesian t1, MTCartesian t2) {
            System.out.println("endMTCartesian");

            return true;
        }

        public boolean endMTFunction(MTFunction t1, MTFunction t2) {
            System.out.println("endMTFunction");

            return true;
        }

        public boolean endMTFunctionApplication(MTFunctionApplication t1,
                MTFunctionApplication t2) {
            System.out.println("endMTFunctionApplication");

            return true;
        }

        public boolean endMTIntersect(MTIntersect t1, MTIntersect t2) {
            System.out.println("endMTIntersect");

            return true;
        }

        public boolean endMTPowertypeApplication(MTPowertypeApplication t1,
                MTPowertypeApplication t2) {
            System.out.println("endMTPowertypeApplication");

            return true;
        }

        public boolean endMTProper(MTProper t1, MTProper t2) {
            System.out.println("endMTProper");

            return true;
        }

        public boolean endMTSetRestriction(MTSetRestriction t1,
                MTSetRestriction t2) {
            System.out.println("endMTSetRestriction");

            return true;
        }

        public boolean endMTUnion(MTUnion t1, MTUnion t2) {
            System.out.println("endMTUnion");

            return true;
        }

        public boolean endMTNamed(MTNamed t1, MTNamed t2) {
            System.out.println("endMTNamed");

            return true;
        }

        public boolean endMTGeneric(MTGeneric t1, MTGeneric t2) {
            System.out.println("endMTGeneric");

            return true;
        }
    }
}

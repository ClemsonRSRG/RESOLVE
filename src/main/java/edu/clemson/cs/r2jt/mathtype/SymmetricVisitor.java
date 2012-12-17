package edu.clemson.cs.r2jt.mathtype;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class SymmetricVisitor {

    private final static Class<?> SYMMETRIC_VISITOR = SymmetricVisitor.class;

    private final static Procedure DO_NOTHING = new Procedure() {

        public void execute() {}
    };

    private final static Procedure THROW_ILLEGAL_ARGUMENT_EXCEPTION =
            new Procedure() {

                public void execute() {
                    throw new NonSymmetricalNodeException();
                }
            };

    private boolean callClassVisitMethods(String prefix, MTType t1,
            Iterator<Class<?>> t1ClassIter, MTType t2,
            Iterator<Class<?>> t2ClassIter, Procedure mismatchBehavior) {

        boolean result = true;

        Class<?> curT1SuperType, curT2SuperType;
        while (t1ClassIter.hasNext() && t2ClassIter.hasNext()) {
            curT1SuperType = t1ClassIter.next();
            curT2SuperType = t2ClassIter.next();

            if (curT1SuperType.equals(curT2SuperType)) {
                String preMethodName = prefix + curT1SuperType.getSimpleName();
                try {
                    //System.out.println(SYMMETRIC_VISITOR + " calling " + preMethodName + "()...");
                    Method preMethod =
                            SYMMETRIC_VISITOR.getMethod(preMethodName,
                                    curT1SuperType, curT1SuperType);
                    result &= (Boolean) preMethod.invoke(this, t1, t2);
                }
                catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                catch (InvocationTargetException e) {
                    Throwable eCause = e.getCause();

                    if (eCause instanceof RuntimeException) {
                        throw (RuntimeException) eCause;
                    }

                    throw new RuntimeException(eCause);
                }
                catch (NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            }
            else {
                mismatchBehavior.execute();
            }
        }

        if (t1ClassIter.hasNext() || t2ClassIter.hasNext()) {
            mismatchBehavior.execute();
        }

        return result;
    }

    public final boolean visit(MTType t1, MTType t2) {
        Deque<Class<?>> t1Classes = getClassHierarchy(t1.getClass());
        Deque<Class<?>> t2Classes = getClassHierarchy(t2.getClass());

        boolean visitChildren, visitSiblings;
        try {
            //Call all the "begin" methods from least to most specific, throwing
            //an illegal argument exception the moment you hit something that
            //doesn't match
            visitChildren =
                    callClassVisitMethods("begin", t1, t1Classes.iterator(),
                            t2, t2Classes.iterator(),
                            THROW_ILLEGAL_ARGUMENT_EXCEPTION);

            if (visitChildren) {
                //Inductively visit the next level
                List<MTType> t1Components = t1.getComponentTypes();
                List<MTType> t2Components = t2.getComponentTypes();

                if (t1Components.size() == t2Components.size()) {
                    Iterator<MTType> t1ComponentsIter = t1Components.iterator();
                    Iterator<MTType> t2ComponentsIter = t2Components.iterator();

                    boolean first = true;
                    while (visitChildren && t1ComponentsIter.hasNext()) {

                        if (first) {
                            first = false;
                        }
                        else {
                            //Note that we couldn't have gotten here if there
                            //was going to be a mismatch
                            visitChildren =
                                    callClassVisitMethods("mid", t1, t1Classes
                                            .iterator(), t2, t2Classes
                                            .iterator(),
                                            THROW_ILLEGAL_ARGUMENT_EXCEPTION);
                        }

                        visitChildren =
                                visit(t1ComponentsIter.next(), t2ComponentsIter
                                        .next());
                    }
                }
                else {
                    visitSiblings = mismatch(t1, t2); //Argument count mismatch
                }
            }
        }
        catch (NonSymmetricalNodeException e) {
            visitSiblings = mismatch(t1, t2); //Node type mismatch
        }

        //Call all the "end" methods from most to least specific, skipping any
        //level that doesn't match
        visitSiblings =
                callClassVisitMethods("end", t1,
                        t1Classes.descendingIterator(), t2, t2Classes
                                .descendingIterator(), DO_NOTHING);

        return visitSiblings;
    }

    private static Deque<Class<?>> getClassHierarchy(Class<?> c) {
        LinkedList<Class<?>> result = new LinkedList<Class<?>>();

        do {
            result.push(c);
            c = c.getSuperclass();
        } while (!c.equals(MTType.class));
        result.push(MTType.class);

        return result;
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

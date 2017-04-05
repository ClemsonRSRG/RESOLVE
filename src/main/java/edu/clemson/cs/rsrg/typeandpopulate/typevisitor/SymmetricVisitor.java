/**
 * SymmetricVisitor.java
 * ---------------------------------
 * Copyright (c) 2016
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.typeandpopulate.typevisitor;

import edu.clemson.cs.rsrg.typeandpopulate.mathtypes.*;
import java.util.Iterator;
import java.util.List;

/**
 * <p>This is the abstract base class that contains empty implementations
 * for begin, mid and end symmetric visit methods for each of the classes
 * that inherit from {@link MTType}.</p>
 *
 * <p>Note that instances of <code>SymmetricVisitor</code> are not thread-safe.</p>
 *
 * @version 2.0
 */
abstract class SymmetricVisitor {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>A type visitor for handling symmetric visits.</p> */
    private final Multiplexer myMultiplexer = new Multiplexer();

    /** <p>A type visitor for handling mid-symmetric visits.</p> */
    private final MidMultiplexer myMidMultiplexer = new MidMultiplexer();

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This method adds additional logic before we visit
     * two {@link MTType}.</p>
     *
     * <p>The default implementation does nothing.</p>
     *
     * @param t1 A math type.
     * @param t2 A math type.
     *
     * @return The default implementation always returns {@code true}.
     */
    public boolean beginMTType(MTType t1, MTType t2) {
        return true;
    }

    /**
     * <p>This method adds additional logic before we visit
     * two {@link MTAbstract}.</p>
     *
     * <p>The default implementation does nothing.</p>
     *
     * @param t1 A math type.
     * @param t2 A math type.
     *
     * @return The default implementation always returns {@code true}.
     */
    public boolean beginMTAbstract(MTAbstract<?> t1, MTAbstract<?> t2) {
        return true;
    }

    /**
     * <p>This method adds additional logic before we visit
     * two {@link MTBigUnion}.</p>
     *
     * <p>The default implementation does nothing.</p>
     *
     * @param t1 A math type.
     * @param t2 A math type.
     *
     * @return The default implementation always returns {@code true}.
     */
    public boolean beginMTBigUnion(MTBigUnion t1, MTBigUnion t2) {
        return true;
    }

    /**
     * <p>This method adds additional logic before we visit
     * two {@link MTCartesian}.</p>
     *
     * <p>The default implementation does nothing.</p>
     *
     * @param t1 A math type.
     * @param t2 A math type.
     *
     * @return The default implementation always returns {@code true}.
     */
    public boolean beginMTCartesian(MTCartesian t1, MTCartesian t2) {
        return true;
    }

    /**
     * <p>This method adds additional logic before we visit
     * two {@link MTFunction}.</p>
     *
     * <p>The default implementation does nothing.</p>
     *
     * @param t1 A math type.
     * @param t2 A math type.
     *
     * @return The default implementation always returns {@code true}.
     */
    public boolean beginMTFunction(MTFunction t1, MTFunction t2) {
        return true;
    }

    /**
     * <p>This method adds additional logic before we visit
     * two {@link MTFunctionApplication}.</p>
     *
     * <p>The default implementation does nothing.</p>
     *
     * @param t1 A math type.
     * @param t2 A math type.
     *
     * @return The default implementation always returns {@code true}.
     */
    public boolean beginMTFunctionApplication(MTFunctionApplication t1,
            MTFunctionApplication t2) {
        return true;
    }

    /**
     * <p>This method adds additional logic before we visit
     * two {@link MTIntersect}.</p>
     *
     * <p>The default implementation does nothing.</p>
     *
     * @param t1 A math type.
     * @param t2 A math type.
     *
     * @return The default implementation always returns {@code true}.
     */
    public boolean beginMTIntersect(MTIntersect t1, MTIntersect t2) {
        return true;
    }

    /**
     * <p>This method adds additional logic before we visit
     * two {@link MTPowerclassApplication}.</p>
     *
     * <p>The default implementation does nothing.</p>
     *
     * @param t1 A math type.
     * @param t2 A math type.
     *
     * @return The default implementation always returns {@code true}.
     */
    public boolean beginMTPowerclassApplication(MTPowerclassApplication t1,
            MTPowerclassApplication t2) {
        return true;
    }

    /**
     * <p>This method adds additional logic before we visit
     * two {@link MTPowersetApplication}.</p>
     *
     * <p>The default implementation does nothing.</p>
     *
     * @param t1 A math type.
     * @param t2 A math type.
     *
     * @return The default implementation always returns {@code true}.
     */
    public boolean beginMTPowersetApplication(MTPowersetApplication t1,
            MTPowersetApplication t2) {
        return true;
    }

    /**
     * <p>This method adds additional logic before we visit
     * two {@link MTProper}.</p>
     *
     * <p>The default implementation does nothing.</p>
     *
     * @param t1 A math type.
     * @param t2 A math type.
     *
     * @return The default implementation always returns {@code true}.
     */
    public boolean beginMTProper(MTProper t1, MTProper t2) {
        return true;
    }

    /**
     * <p>This method adds additional logic before we visit
     * two {@link MTSetRestriction}.</p>
     *
     * <p>The default implementation does nothing.</p>
     *
     * @param t1 A math type.
     * @param t2 A math type.
     *
     * @return The default implementation always returns {@code true}.
     */
    public boolean beginMTSetRestriction(MTSetRestriction t1,
            MTSetRestriction t2) {
        return true;
    }

    /**
     * <p>This method adds additional logic before we visit
     * two {@link MTUnion}.</p>
     *
     * <p>The default implementation does nothing.</p>
     *
     * @param t1 A math type.
     * @param t2 A math type.
     *
     * @return The default implementation always returns {@code true}.
     */
    public boolean beginMTUnion(MTUnion t1, MTUnion t2) {
        return true;
    }

    /**
     * <p>This method adds additional logic before we visit
     * two {@link MTNamed}.</p>
     *
     * <p>The default implementation does nothing.</p>
     *
     * @param t1 A math type.
     * @param t2 A math type.
     *
     * @return The default implementation always returns {@code true}.
     */
    public boolean beginMTNamed(MTNamed t1, MTNamed t2) {
        return true;
    }

    /**
     * <p>This method adds additional logic before we visit
     * two {@link MTGeneric}.</p>
     *
     * <p>The default implementation does nothing.</p>
     *
     * @param t1 A math type.
     * @param t2 A math type.
     *
     * @return The default implementation always returns {@code true}.
     */
    public boolean beginMTGeneric(MTGeneric t1, MTGeneric t2) {
        return true;
    }

    /**
     * <p>This method adds additional logic after we visit <code>t1</code>
     * and before we visit <code>t2</code>.</p>
     *
     * <p>The default implementation does nothing.</p>
     *
     * @param t1 A math type.
     * @param t2 A math type.
     *
     * @return The default implementation always returns {@code true}.
     */
    public boolean midMTType(MTType t1, MTType t2) {
        return true;
    }

    /**
     * <p>This method adds additional logic after we visit <code>t1</code>
     * and before we visit <code>t2</code>.</p>
     *
     * <p>The default implementation does nothing.</p>
     *
     * @param t1 A math type.
     * @param t2 A math type.
     *
     * @return The default implementation always returns {@code true}.
     */
    public boolean midMTAbstract(MTAbstract<?> t1, MTAbstract<?> t2) {
        return true;
    }

    /**
     * <p>This method adds additional logic after we visit <code>t1</code>
     * and before we visit <code>t2</code>.</p>
     *
     * <p>The default implementation does nothing.</p>
     *
     * @param t1 A math type.
     * @param t2 A math type.
     *
     * @return The default implementation always returns {@code true}.
     */
    public boolean midMTBigUnion(MTBigUnion t1, MTBigUnion t2) {
        return true;
    }

    /**
     * <p>This method adds additional logic after we visit <code>t1</code>
     * and before we visit <code>t2</code>.</p>
     *
     * <p>The default implementation does nothing.</p>
     *
     * @param t1 A math type.
     * @param t2 A math type.
     *
     * @return The default implementation always returns {@code true}.
     */
    public boolean midMTCartesian(MTCartesian t1, MTCartesian t2) {
        return true;
    }

    /**
     * <p>This method adds additional logic after we visit <code>t1</code>
     * and before we visit <code>t2</code>.</p>
     *
     * <p>The default implementation does nothing.</p>
     *
     * @param t1 A math type.
     * @param t2 A math type.
     *
     * @return The default implementation always returns {@code true}.
     */
    public boolean midMTFunction(MTFunction t1, MTFunction t2) {
        return true;
    }

    /**
     * <p>This method adds additional logic after we visit <code>t1</code>
     * and before we visit <code>t2</code>.</p>
     *
     * <p>The default implementation does nothing.</p>
     *
     * @param t1 A math type.
     * @param t2 A math type.
     *
     * @return The default implementation always returns {@code true}.
     */
    public boolean midMTFunctionApplication(MTFunctionApplication t1,
            MTFunctionApplication t2) {
        return true;
    }

    /**
     * <p>This method adds additional logic after we visit <code>t1</code>
     * and before we visit <code>t2</code>.</p>
     *
     * <p>The default implementation does nothing.</p>
     *
     * @param t1 A math type.
     * @param t2 A math type.
     *
     * @return The default implementation always returns {@code true}.
     */
    public boolean midMTIntersect(MTIntersect t1, MTIntersect t2) {
        return true;
    }

    /**
     * <p>This method adds additional logic after we visit <code>t1</code>
     * and before we visit <code>t2</code>.</p>
     *
     * <p>The default implementation does nothing.</p>
     *
     * @param t1 A math type.
     * @param t2 A math type.
     *
     * @return The default implementation always returns {@code true}.
     */
    public boolean midMTPowerclassApplication(MTPowerclassApplication t1,
            MTPowerclassApplication t2) {
        return true;
    }

    /**
     * <p>This method adds additional logic after we visit <code>t1</code>
     * and before we visit <code>t2</code>.</p>
     *
     * <p>The default implementation does nothing.</p>
     *
     * @param t1 A math type.
     * @param t2 A math type.
     *
     * @return The default implementation always returns {@code true}.
     */
    public boolean midMTPowersetApplication(MTPowersetApplication t1,
            MTPowersetApplication t2) {
        return true;
    }

    /**
     * <p>This method adds additional logic after we visit <code>t1</code>
     * and before we visit <code>t2</code>.</p>
     *
     * <p>The default implementation does nothing.</p>
     *
     * @param t1 A math type.
     * @param t2 A math type.
     *
     * @return The default implementation always returns {@code true}.
     */
    public boolean midMTProper(MTProper t1, MTProper t2) {
        return true;
    }

    /**
     * <p>This method adds additional logic after we visit <code>t1</code>
     * and before we visit <code>t2</code>.</p>
     *
     * <p>The default implementation does nothing.</p>
     *
     * @param t1 A math type.
     * @param t2 A math type.
     *
     * @return The default implementation always returns {@code true}.
     */
    public boolean midMTSetRestriction(MTSetRestriction t1, MTSetRestriction t2) {
        return true;
    }

    /**
     * <p>This method adds additional logic after we visit <code>t1</code>
     * and before we visit <code>t2</code>.</p>
     *
     * <p>The default implementation does nothing.</p>
     *
     * @param t1 A math type.
     * @param t2 A math type.
     *
     * @return The default implementation always returns {@code true}.
     */
    public boolean midMTUnion(MTUnion t1, MTUnion t2) {
        return true;
    }

    /**
     * <p>This method adds additional logic after we visit <code>t1</code>
     * and before we visit <code>t2</code>.</p>
     *
     * <p>The default implementation does nothing.</p>
     *
     * @param t1 A math type.
     * @param t2 A math type.
     *
     * @return The default implementation always returns {@code true}.
     */
    public boolean midMTNamed(MTNamed t1, MTNamed t2) {
        return true;
    }

    /**
     * <p>This method adds additional logic after we visit <code>t1</code>
     * and before we visit <code>t2</code>.</p>
     *
     * <p>The default implementation does nothing.</p>
     *
     * @param t1 A math type.
     * @param t2 A math type.
     *
     * @return The default implementation always returns {@code true}.
     */
    public boolean midMTGeneric(MTGeneric t1, MTGeneric t2) {
        return true;
    }

    /**
     * <p>This method provides logic for handling type mismatches.</p>
     *
     * <p>The default implementation does nothing.</p>
     *
     * @param t1 A math type.
     * @param t2 A math type.
     *
     * @return The default implementation always returns {@code true}.
     */
    public boolean mismatch(MTType t1, MTType t2) {
        return true;
    }

    /**
     * <p>This method adds additional logic after we visit
     * two {@link MTType}.</p>
     *
     * <p>The default implementation does nothing.</p>
     *
     * @param t1 A math type.
     * @param t2 A math type.
     *
     * @return The default implementation always returns {@code true}.
     */
    public boolean endMTType(MTType t1, MTType t2) {
        return true;
    }

    /**
     * <p>This method adds additional logic after we visit
     * two {@link MTAbstract}.</p>
     *
     * <p>The default implementation does nothing.</p>
     *
     * @param t1 A math type.
     * @param t2 A math type.
     *
     * @return The default implementation always returns {@code true}.
     */
    public boolean endMTAbstract(MTAbstract<?> t1, MTAbstract<?> t2) {
        return true;
    }

    /**
     * <p>This method adds additional logic after we visit
     * two {@link MTBigUnion}.</p>
     *
     * <p>The default implementation does nothing.</p>
     *
     * @param t1 A math type.
     * @param t2 A math type.
     *
     * @return The default implementation always returns {@code true}.
     */
    public boolean endMTBigUnion(MTBigUnion t1, MTBigUnion t2) {
        return true;
    }

    /**
     * <p>This method adds additional logic after we visit
     * two {@link MTCartesian}.</p>
     *
     * <p>The default implementation does nothing.</p>
     *
     * @param t1 A math type.
     * @param t2 A math type.
     *
     * @return The default implementation always returns {@code true}.
     */
    public boolean endMTCartesian(MTCartesian t1, MTCartesian t2) {
        return true;
    }

    /**
     * <p>This method adds additional logic after we visit
     * two {@link MTFunction}.</p>
     *
     * <p>The default implementation does nothing.</p>
     *
     * @param t1 A math type.
     * @param t2 A math type.
     *
     * @return The default implementation always returns {@code true}.
     */
    public boolean endMTFunction(MTFunction t1, MTFunction t2) {
        return true;
    }

    /**
     * <p>This method adds additional logic after we visit
     * two {@link MTFunctionApplication}.</p>
     *
     * <p>The default implementation does nothing.</p>
     *
     * @param t1 A math type.
     * @param t2 A math type.
     *
     * @return The default implementation always returns {@code true}.
     */
    public boolean endMTFunctionApplication(MTFunctionApplication t1,
            MTFunctionApplication t2) {
        return true;
    }

    /**
     * <p>This method adds additional logic after we visit
     * two {@link MTIntersect}.</p>
     *
     * <p>The default implementation does nothing.</p>
     *
     * @param t1 A math type.
     * @param t2 A math type.
     *
     * @return The default implementation always returns {@code true}.
     */
    public boolean endMTIntersect(MTIntersect t1, MTIntersect t2) {
        return true;
    }

    /**
     * <p>This method adds additional logic after we visit
     * two {@link MTPowerclassApplication}.</p>
     *
     * <p>The default implementation does nothing.</p>
     *
     * @param t1 A math type.
     * @param t2 A math type.
     *
     * @return The default implementation always returns {@code true}.
     */
    public boolean endMTPowerclassApplication(MTPowerclassApplication t1,
            MTPowerclassApplication t2) {
        return true;
    }

    /**
     * <p>This method adds additional logic after we visit
     * two {@link MTPowersetApplication}.</p>
     *
     * <p>The default implementation does nothing.</p>
     *
     * @param t1 A math type.
     * @param t2 A math type.
     *
     * @return The default implementation always returns {@code true}.
     */
    public boolean endMTPowersetApplication(MTPowersetApplication t1,
            MTPowersetApplication t2) {
        return true;
    }

    /**
     * <p>This method adds additional logic after we visit
     * two {@link MTProper}.</p>
     *
     * <p>The default implementation does nothing.</p>
     *
     * @param t1 A math type.
     * @param t2 A math type.
     *
     * @return The default implementation always returns {@code true}.
     */
    public boolean endMTProper(MTProper t1, MTProper t2) {
        return true;
    }

    /**
     * <p>This method adds additional logic after we visit
     * two {@link MTSetRestriction}.</p>
     *
     * <p>The default implementation does nothing.</p>
     *
     * @param t1 A math type.
     * @param t2 A math type.
     *
     * @return The default implementation always returns {@code true}.
     */
    public boolean endMTSetRestriction(MTSetRestriction t1, MTSetRestriction t2) {
        return true;
    }

    /**
     * <p>This method adds additional logic after we visit
     * two {@link MTUnion}.</p>
     *
     * <p>The default implementation does nothing.</p>
     *
     * @param t1 A math type.
     * @param t2 A math type.
     *
     * @return The default implementation always returns {@code true}.
     */
    public boolean endMTUnion(MTUnion t1, MTUnion t2) {
        return true;
    }

    /**
     * <p>This method adds additional logic after we visit
     * two {@link MTNamed}.</p>
     *
     * <p>The default implementation does nothing.</p>
     *
     * @param t1 A math type.
     * @param t2 A math type.
     *
     * @return The default implementation always returns {@code true}.
     */
    public boolean endMTNamed(MTNamed t1, MTNamed t2) {
        return true;
    }

    /**
     * <p>This method adds additional logic after we visit
     * two {@link MTGeneric}.</p>
     *
     * <p>The default implementation does nothing.</p>
     *
     * @param t1 A math type.
     * @param t2 A math type.
     *
     * @return The default implementation always returns {@code true}.
     */
    public boolean endMTGeneric(MTGeneric t1, MTGeneric t2) {
        return true;
    }

    /**
     * <p>This method visits two mathematical types to perform
     * the logic implemented by the begin, mid and end methods.</p>
     *
     * @param t1 A mathematical type.
     * @param t2 A mathematical type.
     *
     * @return The result from visiting <code>t1</code> and
     * <code>t2</code>.
     */
    public final boolean visit(MTType t1, MTType t2) {
        boolean visitSiblings = true;

        myMultiplexer.setOtherType(t2);
        myMidMultiplexer.setOtherType(t2);
        try {
            t1.acceptOpen(myMultiplexer);

            if (t1.getClass() != t2.getClass()) {
                throw new ClassCastException();
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

    // ===========================================================
    // Helper Constructs
    // ===========================================================

    /**
     * <p>An implementation of {@link TypeVisitor} that invokes the
     * symmetric visit methods.</p>
     */
    private class Multiplexer extends TypeVisitor {

        // ===========================================================
        // Member Fields
        // ===========================================================

        /** <p>The symmetric {@link MTType} to be visited.</p> */
        private MTType myOtherType;

        /** <p>The boolean return flag value.</p> */
        private boolean myReturn;

        // ===========================================================
        // Public Methods
        // ===========================================================

        /**
         * <p>This method adds additional logic before we visit
         * a {@link MTType} by adding all the logic implemented by the
         * {@link #beginMTType(MTType, MTType)} method.</p>
         *
         * @param t A math type.
         */
        @Override
        public final void beginMTType(MTType t) {
            myReturn = SymmetricVisitor.this.beginMTType(t, myOtherType);
        }

        /**
         * <p>This method adds additional logic before we visit
         * a {@link MTAbstract} by adding all the logic implemented by the
         * {@link #beginMTAbstract(MTAbstract, MTAbstract)} method.</p>
         *
         * @param t A math type.
         */
        @Override
        public final void beginMTAbstract(MTAbstract<?> t) {
            myReturn =
                    SymmetricVisitor.this.beginMTAbstract(t,
                            (MTAbstract) myOtherType);
        }

        /**
         * <p>This method adds additional logic before we visit
         * a {@link MTBigUnion} by adding all the logic implemented by the
         * {@link #beginMTBigUnion(MTBigUnion, MTBigUnion)} method.</p>
         *
         * @param t A math type.
         */
        @Override
        public final void beginMTBigUnion(MTBigUnion t) {
            myReturn =
                    SymmetricVisitor.this.beginMTBigUnion(t,
                            (MTBigUnion) myOtherType);
        }

        /**
         * <p>This method adds additional logic before we visit
         * a {@link MTCartesian} by adding all the logic implemented by the
         * {@link #beginMTCartesian(MTCartesian, MTCartesian)} method.</p>
         *
         * @param t A math type.
         */
        @Override
        public final void beginMTCartesian(MTCartesian t) {
            myReturn =
                    SymmetricVisitor.this.beginMTCartesian(t,
                            (MTCartesian) myOtherType);
        }

        /**
         * <p>This method adds additional logic before we visit
         * a {@link MTFunction} by adding all the logic implemented by the
         * {@link #beginMTFunction(MTFunction, MTFunction)} method.</p>
         *
         * @param t A math type.
         */
        @Override
        public final void beginMTFunction(MTFunction t) {
            myReturn =
                    SymmetricVisitor.this.beginMTFunction(t,
                            (MTFunction) myOtherType);
        }

        /**
         * <p>This method adds additional logic before we visit
         * a {@link MTFunctionApplication} by adding all the logic implemented by the
         * {@link #beginMTFunctionApplication(MTFunctionApplication, MTFunctionApplication)} method.</p>
         *
         * @param t A math type.
         */
        @Override
        public final void beginMTFunctionApplication(MTFunctionApplication t) {
            myReturn =
                    SymmetricVisitor.this.beginMTFunctionApplication(t,
                            (MTFunctionApplication) myOtherType);
        }

        /**
         * <p>This method adds additional logic before we visit
         * a {@link MTIntersect} by adding all the logic implemented by the
         * {@link #beginMTIntersect(MTIntersect, MTIntersect)} method.</p>
         *
         * @param t A math type.
         */
        @Override
        public final void beginMTIntersect(MTIntersect t) {
            myReturn =
                    SymmetricVisitor.this.beginMTIntersect(t,
                            (MTIntersect) myOtherType);
        }

        /**
         * <p>This method adds additional logic before we visit
         * a {@link MTPowerclassApplication} by adding all the logic implemented by the
         * {@link #beginMTPowerclassApplication(MTPowerclassApplication, MTPowerclassApplication)} method.</p>
         *
         * @param t A math type.
         */
        @Override
        public final void beginMTPowerclassApplication(MTPowerclassApplication t) {
            myReturn =
                    SymmetricVisitor.this.beginMTPowerclassApplication(t,
                            (MTPowerclassApplication) myOtherType);
        }

        /**
         * <p>This method adds additional logic before we visit
         * a {@link MTPowersetApplication} by adding all the logic implemented by the
         * {@link #beginMTPowersetApplication(MTPowersetApplication, MTPowersetApplication)} method.</p>
         *
         * @param t A math type.
         */
        @Override
        public final void beginMTPowersetApplication(MTPowersetApplication t) {
            myReturn =
                    SymmetricVisitor.this.beginMTPowersetApplication(t,
                            (MTPowersetApplication) myOtherType);
        }

        /**
         * <p>This method adds additional logic before we visit
         * a {@link MTProper} by adding all the logic implemented by the
         * {@link #beginMTProper(MTProper, MTProper)} method.</p>
         *
         * @param t A math type.
         */
        @Override
        public final void beginMTProper(MTProper t) {
            myReturn =
                    SymmetricVisitor.this.beginMTProper(t,
                            (MTProper) myOtherType);
        }

        /**
         * <p>This method adds additional logic before we visit
         * a {@link MTSetRestriction} by adding all the logic implemented by the
         * {@link #beginMTSetRestriction(MTSetRestriction, MTSetRestriction)} method.</p>
         *
         * @param t A math type.
         */
        @Override
        public final void beginMTSetRestriction(MTSetRestriction t) {
            myReturn =
                    SymmetricVisitor.this.beginMTSetRestriction(t,
                            (MTSetRestriction) myOtherType);
        }

        /**
         * <p>This method adds additional logic before we visit
         * a {@link MTUnion} by adding all the logic implemented by the
         * {@link #beginMTUnion(MTUnion, MTUnion)} method.</p>
         *
         * @param t A math type.
         */
        @Override
        public final void beginMTUnion(MTUnion t) {
            myReturn =
                    SymmetricVisitor.this
                            .beginMTUnion(t, (MTUnion) myOtherType);
        }

        /**
         * <p>This method adds additional logic before we visit
         * a {@link MTNamed} by adding all the logic implemented by the
         * {@link #beginMTNamed(MTNamed, MTNamed)} method.</p>
         *
         * @param t A math type.
         */
        @Override
        public final void beginMTNamed(MTNamed t) {
            myReturn =
                    SymmetricVisitor.this
                            .beginMTNamed(t, (MTNamed) myOtherType);
        }

        /**
         * <p>This method adds additional logic before we visit
         * a {@link MTGeneric} by adding all the logic implemented by the
         * {@link #beginMTGeneric(MTGeneric, MTGeneric)} method.</p>
         *
         * @param t A math type.
         */
        @Override
        public final void beginMTGeneric(MTGeneric t) {
            myReturn =
                    SymmetricVisitor.this.beginMTGeneric(t,
                            (MTGeneric) myOtherType);
        }

        /**
         * <p>This method adds additional logic after we visit
         * a {@link MTType} by adding all the logic implemented by the
         * {@link #endMTType(MTType, MTType)} method.</p>
         *
         * @param t A math type.
         */
        @Override
        public final void endMTType(MTType t) {
            myReturn = SymmetricVisitor.this.endMTType(t, myOtherType);
        }

        /**
         * <p>This method adds additional logic after we visit
         * a {@link MTAbstract} by adding all the logic implemented by the
         * {@link #endMTAbstract(MTAbstract, MTAbstract)} method.</p>
         *
         * @param t A math type.
         */
        @Override
        public final void endMTAbstract(MTAbstract<?> t) {
            myReturn =
                    SymmetricVisitor.this.endMTAbstract(t,
                            (MTAbstract) myOtherType);
        }

        /**
         * <p>This method adds additional logic after we visit
         * a {@link MTBigUnion} by adding all the logic implemented by the
         * {@link #endMTBigUnion(MTBigUnion, MTBigUnion)} method.</p>
         *
         * @param t A math type.
         */
        @Override
        public final void endMTBigUnion(MTBigUnion t) {
            myReturn =
                    SymmetricVisitor.this.endMTBigUnion(t,
                            (MTBigUnion) myOtherType);
        }

        /**
         * <p>This method adds additional logic after we visit
         * a {@link MTCartesian} by adding all the logic implemented by the
         * {@link #endMTCartesian(MTCartesian, MTCartesian)} method.</p>
         *
         * @param t A math type.
         */
        @Override
        public final void endMTCartesian(MTCartesian t) {
            myReturn =
                    SymmetricVisitor.this.endMTCartesian(t,
                            (MTCartesian) myOtherType);
        }

        /**
         * <p>This method adds additional logic after we visit
         * a {@link MTFunction} by adding all the logic implemented by the
         * {@link #endMTFunction(MTFunction, MTFunction)} method.</p>
         *
         * @param t A math type.
         */
        @Override
        public final void endMTFunction(MTFunction t) {
            myReturn =
                    SymmetricVisitor.this.endMTFunction(t,
                            (MTFunction) myOtherType);
        }

        /**
         * <p>This method adds additional logic after we visit
         * a {@link MTFunctionApplication} by adding all the logic implemented by the
         * {@link #endMTFunctionApplication(MTFunctionApplication, MTFunctionApplication)} method.</p>
         *
         * @param t A math type.
         */
        @Override
        public final void endMTFunctionApplication(MTFunctionApplication t) {
            myReturn =
                    SymmetricVisitor.this.endMTFunctionApplication(t,
                            (MTFunctionApplication) myOtherType);
        }

        /**
         * <p>This method adds additional logic after we visit
         * a {@link MTIntersect} by adding all the logic implemented by the
         * {@link #endMTIntersect(MTIntersect, MTIntersect)} method.</p>
         *
         * @param t A math type.
         */
        @Override
        public final void endMTIntersect(MTIntersect t) {
            myReturn =
                    SymmetricVisitor.this.endMTIntersect(t,
                            (MTIntersect) myOtherType);
        }

        /**
         * <p>This method adds additional logic after we visit
         * a {@link MTPowerclassApplication} by adding all the logic implemented by the
         * {@link #endMTPowerclassApplication(MTPowerclassApplication, MTPowerclassApplication)} method.</p>
         *
         * @param t A math type.
         */
        @Override
        public final void endMTPowerclassApplication(MTPowerclassApplication t) {
            myReturn =
                    SymmetricVisitor.this.endMTPowerclassApplication(t,
                            (MTPowerclassApplication) myOtherType);
        }

        /**
         * <p>This method adds additional logic after we visit
         * a {@link MTPowersetApplication} by adding all the logic implemented by the
         * {@link #endMTPowersetApplication(MTPowersetApplication, MTPowersetApplication)} method.</p>
         *
         * @param t A math type.
         */
        @Override
        public final void endMTPowersetApplication(MTPowersetApplication t) {
            myReturn =
                    SymmetricVisitor.this.endMTPowersetApplication(t,
                            (MTPowersetApplication) myOtherType);
        }

        /**
         * <p>This method adds additional logic after we visit
         * a {@link MTProper} by adding all the logic implemented by the
         * {@link #endMTProper(MTProper, MTProper)} method.</p>
         *
         * @param t A math type.
         */
        @Override
        public final void endMTProper(MTProper t) {
            myReturn =
                    SymmetricVisitor.this
                            .endMTProper(t, (MTProper) myOtherType);
        }

        /**
         * <p>This method adds additional logic after we visit
         * a {@link MTSetRestriction} by adding all the logic implemented by the
         * {@link #endMTSetRestriction(MTSetRestriction, MTSetRestriction)} method.</p>
         *
         * @param t A math type.
         */
        @Override
        public final void endMTSetRestriction(MTSetRestriction t) {
            myReturn =
                    SymmetricVisitor.this.endMTSetRestriction(t,
                            (MTSetRestriction) myOtherType);
        }

        /**
         * <p>This method adds additional logic after we visit
         * a {@link MTUnion} by adding all the logic implemented by the
         * {@link #endMTUnion(MTUnion, MTUnion)} method.</p>
         *
         * @param t A math type.
         */
        @Override
        public final void endMTUnion(MTUnion t) {
            myReturn =
                    SymmetricVisitor.this.endMTUnion(t, (MTUnion) myOtherType);
        }

        /**
         * <p>This method adds additional logic after we visit
         * a {@link MTNamed} by adding all the logic implemented by the
         * {@link #endMTNamed(MTNamed, MTNamed)} method.</p>
         *
         * @param t A math type.
         */
        @Override
        public final void endMTNamed(MTNamed t) {
            myReturn =
                    SymmetricVisitor.this.endMTNamed(t, (MTNamed) myOtherType);
        }

        /**
         * <p>This method adds additional logic after we visit
         * a {@link MTGeneric} by adding all the logic implemented by the
         * {@link #endMTGeneric(MTGeneric, MTGeneric)} method.</p>
         *
         * @param t A math type.
         */
        @Override
        public final void endMTGeneric(MTGeneric t) {
            myReturn =
                    SymmetricVisitor.this.endMTGeneric(t,
                            (MTGeneric) myOtherType);
        }

        /**
         * <p>This method returns the final return value
         * provided by the symmetric visitors.</p>
         *
         * @return A boolean flag provided by instances of
         * <code>SymmetricVisitor</code>.
         */
        public final boolean getReturn() {
            return myReturn;
        }

        /**
         * <p>This method sets the other type to be visited
         * by the <code>SymmetricVisitor</code>.</p>
         *
         * @param t A math type.
         */
        public final void setOtherType(MTType t) {
            myOtherType = t;
        }

        /**
         * <p>This method sets the final return value provided
         * by the symmetric visitors.</p>
         *
         * @param returnVal A boolean flag to be returned.
         */
        public final void setReturn(boolean returnVal) {
            myReturn = returnVal;
        }

    }

    /**
     * <p>An implementation of {@link TypeVisitor} that adds the ability
     * to perform additional logic in between mid-visits.</p>
     */
    private class MidMultiplexer extends TypeVisitor {

        // ===========================================================
        // Member Fields
        // ===========================================================

        /** <p>The symmetric {@link MTType} to be visited.</p> */
        private MTType myOtherType;

        /** <p>The boolean return flag value.</p> */
        private boolean myReturn;

        // ===========================================================
        // Public Methods
        // ===========================================================

        /**
         * <p>This method adds additional logic before we visit
         * a {@link MTType} by adding all the logic implemented by the
         * {@link #midMTType(MTType, MTType)} method.</p>
         *
         * @param t A math type.
         */
        @Override
        public final void beginMTType(MTType t) {
            myReturn = midMTType(t, myOtherType);
        }

        /**
         * <p>This method adds additional logic before we visit
         * a {@link MTAbstract} by adding all the logic implemented by the
         * {@link #midMTAbstract(MTAbstract, MTAbstract)} method.</p>
         *
         * @param t A math type.
         */
        @Override
        public final void beginMTAbstract(MTAbstract<?> t) {
            myReturn = midMTAbstract(t, (MTAbstract) myOtherType);
        }

        /**
         * <p>This method adds additional logic before we visit
         * a {@link MTBigUnion} by adding all the logic implemented by the
         * {@link #midMTBigUnion(MTBigUnion, MTBigUnion)} method.</p>
         *
         * @param t A math type.
         */
        @Override
        public final void beginMTBigUnion(MTBigUnion t) {
            myReturn = midMTBigUnion(t, (MTBigUnion) myOtherType);
        }

        /**
         * <p>This method adds additional logic before we visit
         * a {@link MTCartesian} by adding all the logic implemented by the
         * {@link #midMTCartesian(MTCartesian, MTCartesian)} method.</p>
         *
         * @param t A math type.
         */
        @Override
        public final void beginMTCartesian(MTCartesian t) {
            myReturn = midMTCartesian(t, (MTCartesian) myOtherType);
        }

        /**
         * <p>This method adds additional logic before we visit
         * a {@link MTFunction} by adding all the logic implemented by the
         * {@link #midMTFunction(MTFunction, MTFunction)} method.</p>
         *
         * @param t A math type.
         */
        @Override
        public final void beginMTFunction(MTFunction t) {
            myReturn = midMTFunction(t, (MTFunction) myOtherType);
        }

        /**
         * <p>This method adds additional logic before we visit
         * a {@link MTFunctionApplication} by adding all the logic implemented by the
         * {@link #midMTFunctionApplication(MTFunctionApplication, MTFunctionApplication)} method.</p>
         *
         * @param t A math type.
         */
        @Override
        public final void beginMTFunctionApplication(MTFunctionApplication t) {
            myReturn =
                    midMTFunctionApplication(t,
                            (MTFunctionApplication) myOtherType);
        }

        /**
         * <p>This method adds additional logic before we visit
         * a {@link MTIntersect} by adding all the logic implemented by the
         * {@link #midMTIntersect(MTIntersect, MTIntersect)} method.</p>
         *
         * @param t A math type.
         */
        @Override
        public final void beginMTIntersect(MTIntersect t) {
            myReturn = midMTIntersect(t, (MTIntersect) myOtherType);
        }

        /**
         * <p>This method adds additional logic before we visit
         * a {@link MTPowerclassApplication} by adding all the logic implemented by the
         * {@link #midMTPowerclassApplication(MTPowerclassApplication, MTPowerclassApplication)} method.</p>
         *
         * @param t A math type.
         */
        @Override
        public final void beginMTPowerclassApplication(MTPowerclassApplication t) {
            myReturn =
                    midMTPowerclassApplication(t,
                            (MTPowerclassApplication) myOtherType);
        }

        /**
         * <p>This method adds additional logic before we visit
         * a {@link MTPowersetApplication} by adding all the logic implemented by the
         * {@link #midMTPowersetApplication(MTPowersetApplication, MTPowersetApplication)} method.</p>
         *
         * @param t A math type.
         */
        @Override
        public final void beginMTPowersetApplication(MTPowersetApplication t) {
            myReturn =
                    midMTPowersetApplication(t,
                            (MTPowersetApplication) myOtherType);
        }

        /**
         * <p>This method adds additional logic before we visit
         * a {@link MTProper} by adding all the logic implemented by the
         * {@link #midMTProper(MTProper, MTProper)} method.</p>
         *
         * @param t A math type.
         */
        @Override
        public final void beginMTProper(MTProper t) {
            myReturn = midMTProper(t, (MTProper) myOtherType);
        }

        /**
         * <p>This method adds additional logic before we visit
         * a {@link MTSetRestriction} by adding all the logic implemented by the
         * {@link #midMTSetRestriction(MTSetRestriction, MTSetRestriction)} method.</p>
         *
         * @param t A math type.
         */
        @Override
        public final void beginMTSetRestriction(MTSetRestriction t) {
            myReturn = midMTSetRestriction(t, (MTSetRestriction) myOtherType);
        }

        /**
         * <p>This method adds additional logic before we visit
         * a {@link MTUnion} by adding all the logic implemented by the
         * {@link #midMTUnion(MTUnion, MTUnion)} method.</p>
         *
         * @param t A math type.
         */
        @Override
        public final void beginMTUnion(MTUnion t) {
            myReturn = midMTUnion(t, (MTUnion) myOtherType);
        }

        /**
         * <p>This method adds additional logic before we visit
         * a {@link MTNamed} by adding all the logic implemented by the
         * {@link #midMTNamed(MTNamed, MTNamed)} method.</p>
         *
         * @param t A math type.
         */
        @Override
        public final void beginMTNamed(MTNamed t) {
            myReturn = midMTNamed(t, (MTNamed) myOtherType);
        }

        /**
         * <p>This method adds additional logic before we visit
         * a {@link MTGeneric} by adding all the logic implemented by the
         * {@link #midMTGeneric(MTGeneric, MTGeneric)} method.</p>
         *
         * @param t A math type.
         */
        @Override
        public final void beginMTGeneric(MTGeneric t) {
            myReturn = midMTGeneric(t, (MTGeneric) myOtherType);
        }

        /**
         * <p>This method returns the final return value
         * provided by the symmetric visitors.</p>
         *
         * @return A boolean flag provided by instances of
         * <code>SymmetricVisitor</code>.
         */
        public final boolean getReturn() {
            return myReturn;
        }

        /**
         * <p>This method sets the other type to be visited
         * by the <code>SymmetricVisitor</code>.</p>
         *
         * @param t A math type.
         */
        public final void setOtherType(MTType t) {
            myOtherType = t;
        }

        /**
         * <p>This method sets the final return value provided
         * by the symmetric visitors.</p>
         *
         * @param returnVal A boolean flag to be returned.
         */
        public final void setReturn(boolean returnVal) {
            myReturn = returnVal;
        }

    }

}
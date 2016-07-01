/**
 * TypeVisitor.java
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

/**
 * <p>This is the abstract base class that contains empty implementations
 * for begin and end visit methods for each of the classes that inherit from
 * {@link MTType}.</p>
 *
 * @version 2.0
 */
public abstract class TypeVisitor {

    /**
     * <p>This method adds additional logic before we visit
     * a {@link MTType}.</p>
     *
     * <p>The default implementation does nothing.</p>
     *
     * @param t A math type.
     */
    public void beginMTType(MTType t) {}

    /**
     * <p>This method adds additional logic after we visit
     * a {@link MTType}.</p>
     *
     * <p>The default implementation does nothing.</p>
     *
     * @param t A math type.
     */
    public void endMTType(MTType t) {}

    /**
     * <p>This method adds additional logic before we visit
     * a {@link MTAbstract}.</p>
     *
     * <p>The default implementation does nothing.</p>
     *
     * @param t A math type.
     */
    public void beginMTAbstract(MTAbstract<?> t) {}

    /**
     * <p>This method adds additional logic after we visit
     * a {@link MTAbstract}.</p>
     *
     * <p>The default implementation does nothing.</p>
     *
     * @param t A math type.
     */
    public void endMTAbstract(MTAbstract<?> t) {}

    /**
     * <p>This method adds additional logic before we visit
     * a {@link MTBigUnion}.</p>
     *
     * <p>The default implementation does nothing.</p>
     *
     * @param t A math type.
     */
    //public void beginMTBigUnion(MTBigUnion t) {}

    /**
     * <p>This method adds additional logic after we visit
     * a {@link MTBigUnion}.</p>
     *
     * <p>The default implementation does nothing.</p>
     *
     * @param t A math type.
     */
    //public void endMTBigUnion(MTBigUnion t) {}

    /**
     * <p>This method adds additional logic before we visit
     * a {@link MTCartesian}.</p>
     *
     * <p>The default implementation does nothing.</p>
     *
     * @param t A math type.
     */
    //public void beginMTCartesian(MTCartesian t) {}

    /**
     * <p>This method adds additional logic after we visit
     * a {@link MTCartesian}.</p>
     *
     * <p>The default implementation does nothing.</p>
     *
     * @param t A math type.
     */
    //public void endMTCartesian(MTCartesian t) {}

    /**
     * <p>This method adds additional logic before we visit
     * a {@link MTFunction}.</p>
     *
     * <p>The default implementation does nothing.</p>
     *
     * @param t A math type.
     */
    public void beginMTFunction(MTFunction t) {}

    /**
     * <p>This method adds additional logic after we visit
     * a {@link MTFunction}.</p>
     *
     * <p>The default implementation does nothing.</p>
     *
     * @param t A math type.
     */
    public void endMTFunction(MTFunction t) {}

    /**
     * <p>This method adds additional logic before we visit
     * a {@link MTFunctionApplication}.</p>
     *
     * <p>The default implementation does nothing.</p>
     *
     * @param t A math type.
     */
    //public void beginMTFunctionApplication(MTFunctionApplication t) {}

    /**
     * <p>This method adds additional logic after we visit
     * a {@link MTFunctionApplication}.</p>
     *
     * <p>The default implementation does nothing.</p>
     *
     * @param t A math type.
     */
    //public void endMTFunctionApplication(MTFunctionApplication t) {}

    /**
     * <p>This method adds additional logic before we visit
     * a {@link MTIntersect}.</p>
     *
     * <p>The default implementation does nothing.</p>
     *
     * @param t A math type.
     */
    public void beginMTIntersect(MTIntersect t) {}

    /**
     * <p>This method adds additional logic after we visit
     * a {@link MTIntersect}.</p>
     *
     * <p>The default implementation does nothing.</p>
     *
     * @param t A math type.
     */
    public void endMTIntersect(MTIntersect t) {}

    /**
     * <p>This method adds additional logic before we visit
     * a {@link MTPowertypeApplication}.</p>
     *
     * <p>The default implementation does nothing.</p>
     *
     * @param t A math type.
     */
    //public void beginMTPowertypeApplication(MTPowertypeApplication t) {}

    /**
     * <p>This method adds additional logic after we visit
     * a {@link MTPowertypeApplication}.</p>
     *
     * <p>The default implementation does nothing.</p>
     *
     * @param t A math type.
     */
    //public void endMTPowertypeApplication(MTPowertypeApplication t) {}

    /**
     * <p>This method adds additional logic before we visit
     * a {@link MTProper}.</p>
     *
     * <p>The default implementation does nothing.</p>
     *
     * @param t A math type.
     */
    public void beginMTProper(MTProper t) {}

    /**
     * <p>This method adds additional logic after we visit
     * a {@link MTProper}.</p>
     *
     * <p>The default implementation does nothing.</p>
     *
     * @param t A math type.
     */
    public void endMTProper(MTProper t) {}

    /**
     * <p>This method adds additional logic before we visit
     * a {@link MTSetRestriction}.</p>
     *
     * <p>The default implementation does nothing.</p>
     *
     * @param t A math type.
     */
    public void beginMTSetRestriction(MTSetRestriction t) {}

    /**
     * <p>This method adds additional logic after we visit
     * a {@link MTSetRestriction}.</p>
     *
     * <p>The default implementation does nothing.</p>
     *
     * @param t A math type.
     */
    public void endMTSetRestriction(MTSetRestriction t) {}

    /**
     * <p>This method adds additional logic before we visit
     * a {@link MTUnion}.</p>
     *
     * <p>The default implementation does nothing.</p>
     *
     * @param t A math type.
     */
    public void beginMTUnion(MTUnion t) {}

    /**
     * <p>This method adds additional logic after we visit
     * a {@link MTUnion}.</p>
     *
     * <p>The default implementation does nothing.</p>
     *
     * @param t A math type.
     */
    public void endMTUnion(MTUnion t) {}

    /**
     * <p>This method adds additional logic before we visit
     * a {@link MTNamed}.</p>
     *
     * <p>The default implementation does nothing.</p>
     *
     * @param t A math type.
     */
    public void beginMTNamed(MTNamed t) {}

    /**
     * <p>This method adds additional logic after we visit
     * a {@link MTNamed}.</p>
     *
     * <p>The default implementation does nothing.</p>
     *
     * @param t A math type.
     */
    public void endMTNamed(MTNamed t) {}

    /**
     * <p>This method adds additional logic before we visit
     * a {@link MTGeneric}.</p>
     *
     * <p>The default implementation does nothing.</p>
     *
     * @param t A math type.
     */
    public void beginMTGeneric(MTGeneric t) {}

    /**
     * <p>This method adds additional logic after we visit
     * a {@link MTGeneric}.</p>
     *
     * <p>The default implementation does nothing.</p>
     *
     * @param t A math type.
     */
    public void endMTGeneric(MTGeneric t) {}

    /**
     * <p>This method adds additional logic before we visit
     * the children of a {@link MTType}.</p>
     *
     * <p>The default implementation does nothing.</p>
     *
     * @param t A math type.
     */
    public void beginChildren(MTType t) {}

    /**
     * <p>This method adds additional logic after we visit
     * the children of a {@link MTType}.</p>
     *
     * <p>The default implementation does nothing.</p>
     *
     * @param t A math type.
     */
    public void endChildren(MTType t) {}

}
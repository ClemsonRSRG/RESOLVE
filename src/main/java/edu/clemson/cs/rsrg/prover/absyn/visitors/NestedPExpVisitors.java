/*
 * NestedPExpVisitors.java
 * ---------------------------------
 * Copyright (c) 2019
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.prover.absyn.visitors;

import edu.clemson.cs.rsrg.prover.absyn.PExp;
import edu.clemson.cs.rsrg.prover.absyn.expressions.*;

/**
 * <p>This is a visitor that uses both an external and internal
 * visitor to perform actions simultaneously.</p>
 *
 * @author Hampton Smith
 * @author Mike Kabbani
 * @author Daniel Welch
 * @version 2.0
 */
public class NestedPExpVisitors extends PExpVisitor {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>This is an external visitor for {@link PExp} expressions.</p> */
    private final PExpVisitor myOuterVisitor;

    /** <p>This is an internal visitor for {@link PExp} expressions.</p> */
    private final PExpVisitor myInnerVisitor;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This creates a visitor with both external and internal
     * visitors to perform actions simultaneously.</p>
     *
     * @param outer An external visitor.
     * @param inner An internal visitor.
     */
    public NestedPExpVisitors(PExpVisitor outer, PExpVisitor inner) {
        myOuterVisitor = outer;
        myInnerVisitor = inner;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This method is called before visiting a {@link PExp}.</p>
     *
     * @param p A prover expression.
     */
    @Override
    public final void beginPExp(PExp p) {
        myOuterVisitor.beginPExp(p);
        myInnerVisitor.beginPExp(p);
    }

    /**
     * <p>This method is called before visiting a {@link PSymbol}.</p>
     *
     * @param p A prover symbol expression.
     */
    @Override
    public final void beginPSymbol(PSymbol p) {
        myOuterVisitor.beginPSymbol(p);
        myInnerVisitor.beginPSymbol(p);
    }

    /**
     * <p>This method is called before visiting a prefix form
     * {@link PSymbol}.</p>
     *
     * @param p A prover symbol expression.
     */
    @Override
    public final void beginPrefixPSymbol(PSymbol p) {
        myOuterVisitor.beginPrefixPSymbol(p);
        myInnerVisitor.beginPrefixPSymbol(p);
    }

    /**
     * <p>This method is called before visiting an infix form
     * {@link PSymbol}.</p>
     *
     * @param p A prover symbol expression.
     */
    @Override
    public final void beginInfixPSymbol(PSymbol p) {
        myOuterVisitor.beginInfixPSymbol(p);
        myInnerVisitor.beginInfixPSymbol(p);
    }

    /**
     * <p>This method is called before visiting an outfix form
     * {@link PSymbol}.</p>
     *
     * @param p A prover symbol expression.
     */
    @Override
    public final void beginOutfixPSymbol(PSymbol p) {
        myOuterVisitor.beginOutfixPSymbol(p);
        myInnerVisitor.beginOutfixPSymbol(p);
    }

    /**
     * <p>This method is called before visiting a postfix form
     * {@link PSymbol}.</p>
     *
     * @param p A prover symbol expression.
     */
    @Override
    public final void beginPostfixPSymbol(PSymbol p) {
        myOuterVisitor.beginPostfixPSymbol(p);
        myInnerVisitor.beginPostfixPSymbol(p);
    }

    /**
     * <p>This method is called before visiting a {@link PAlternatives}.</p>
     *
     * @param p A prover alternative expression.
     */
    @Override
    public final void beginPAlternatives(PAlternatives p) {
        myOuterVisitor.beginPAlternatives(p);
        myInnerVisitor.beginPAlternatives(p);
    }

    /**
     * <p>This method is called before visiting a {@link PLambda}.</p>
     *
     * @param p A prover lambda expression.
     */
    @Override
    public final void beginPLambda(PLambda p) {
        myOuterVisitor.beginPLambda(p);
        myInnerVisitor.beginPLambda(p);
    }

    /**
     * <p>This method is called right before we end the visit for
     * a {@link PSymbol}.</p>
     *
     * @param p A prover symbol expression.
     */
    @Override
    public final void fencepostPSymbol(PSymbol p) {
        myInnerVisitor.fencepostPSymbol(p);
        myOuterVisitor.fencepostPSymbol(p);
    }

    /**
     * <p>This method is called right before we end the visit for
     * a prefix form {@link PSymbol}.</p>
     *
     * @param p A prover symbol expression.
     */
    @Override
    public final void fencepostPrefixPSymbol(PSymbol p) {
        myInnerVisitor.fencepostPrefixPSymbol(p);
        myOuterVisitor.fencepostPrefixPSymbol(p);
    }

    /**
     * <p>This method is called right before we end the visit for
     * an infix form {@link PSymbol}.</p>
     *
     * @param p A prover symbol expression.
     */
    @Override
    public final void fencepostInfixPSymbol(PSymbol p) {
        myInnerVisitor.fencepostInfixPSymbol(p);
        myOuterVisitor.fencepostInfixPSymbol(p);
    }

    /**
     * <p>This method is called right before we end the visit for
     * an outfix form {@link PSymbol}.</p>
     *
     * @param p A prover symbol expression.
     */
    @Override
    public final void fencepostOutfixPSymbol(PSymbol p) {
        myInnerVisitor.fencepostOutfixPSymbol(p);
        myOuterVisitor.fencepostOutfixPSymbol(p);
    }

    /**
     * <p>This method is called right before we end the visit for
     * a postfix form {@link PSymbol}.</p>
     *
     * @param p A prover symbol expression.
     */
    @Override
    public final void fencepostPostfixPSymbol(PSymbol p) {
        myInnerVisitor.fencepostPostfixPSymbol(p);
        myOuterVisitor.fencepostPostfixPSymbol(p);
    }

    /**
     * <p>This method is called right before we end the visit for
     * a {@link PAlternatives}.</p>
     *
     * @param p A prover alternative expression.
     */
    @Override
    public final void fencepostPAlternatives(PAlternatives p) {
        myInnerVisitor.fencepostPAlternatives(p);
        myOuterVisitor.fencepostPAlternatives(p);
    }

    /**
     * <p>This method is called after visiting a {@link PExp}.</p>
     *
     * @param p A prover expression.
     */
    @Override
    public final void endPExp(PExp p) {
        myInnerVisitor.endPExp(p);
        myOuterVisitor.endPExp(p);
    }

    /**
     * <p>This method is called after visiting a {@link PSymbol}.</p>
     *
     * @param p A prover symbol expression.
     */
    @Override
    public final void endPSymbol(PSymbol p) {
        myInnerVisitor.endPSymbol(p);
        myOuterVisitor.endPSymbol(p);
    }

    /**
     * <p>This method is called after visiting a prefix form
     * {@link PSymbol}.</p>
     *
     * @param p A prover symbol expression.
     */
    @Override
    public final void endPrefixPSymbol(PSymbol p) {
        myInnerVisitor.endPrefixPSymbol(p);
        myOuterVisitor.endPrefixPSymbol(p);
    }

    /**
     * <p>This method is called after visiting an infix form
     * {@link PSymbol}.</p>
     *
     * @param p A prover symbol expression.
     */
    @Override
    public final void endInfixPSymbol(PSymbol p) {
        myInnerVisitor.endInfixPSymbol(p);
        myOuterVisitor.endInfixPSymbol(p);
    }

    /**
     * <p>This method is called after visiting an outfix form
     * {@link PSymbol}.</p>
     *
     * @param p A prover symbol expression.
     */
    @Override
    public final void endOutfixPSymbol(PSymbol p) {
        myInnerVisitor.endOutfixPSymbol(p);
        myOuterVisitor.endOutfixPSymbol(p);
    }

    /**
     * <p>This method is called after visiting a postfix form
     * {@link PSymbol}.</p>
     *
     * @param p A prover symbol expression.
     */
    @Override
    public final void endPostfixPSymbol(PSymbol p) {
        myInnerVisitor.endPostfixPSymbol(p);
        myOuterVisitor.endPostfixPSymbol(p);
    }

    /**
     * <p>This method is called after visiting a {@link PAlternatives}.</p>
     *
     * @param p A prover alternative expression.
     */
    @Override
    public final void endPAlternatives(PAlternatives p) {
        myInnerVisitor.endPAlternatives(p);
        myOuterVisitor.endPAlternatives(p);
    }

    /**
     * <p>This method is called after visiting a {@link PLambda}.</p>
     *
     * @param p A prover lambda expression.
     */
    @Override
    public final void endPLambda(PLambda p) {
        myInnerVisitor.endPLambda(p);
        myOuterVisitor.endPLambda(p);
    }

}
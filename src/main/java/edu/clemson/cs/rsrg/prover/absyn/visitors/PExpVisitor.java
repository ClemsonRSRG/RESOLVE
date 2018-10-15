/*
 * PExpVisitor.java
 * ---------------------------------
 * Copyright (c) 2018
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
 * <p>This abstract class defines all the different visit methods
 * for each class that inherits from {@link PExp}.</p>
 *
 * @author Hampton Smith
 * @version 2.0
 */
public abstract class PExpVisitor {

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This method is called before visiting a {@link PExp}.</p>
     *
     * @param p A prover expression.
     */
    public void beginPExp(PExp p) {}

    /**
     * <p>This method is called before visiting a {@link PSymbol}.</p>
     *
     * @param p A prover symbol expression.
     */
    public void beginPSymbol(PSymbol p) {}

    /**
     * <p>This method is called before visiting a prefix form
     * {@link PSymbol}.</p>
     *
     * @param p A prover symbol expression.
     */
    public void beginPrefixPSymbol(PSymbol p) {}

    /**
     * <p>This method is called before visiting an infix form
     * {@link PSymbol}.</p>
     *
     * @param p A prover symbol expression.
     */
    public void beginInfixPSymbol(PSymbol p) {}

    /**
     * <p>This method is called before visiting an outfix form
     * {@link PSymbol}.</p>
     *
     * @param p A prover symbol expression.
     */
    public void beginOutfixPSymbol(PSymbol p) {}

    /**
     * <p>This method is called before visiting a postfix form
     * {@link PSymbol}.</p>
     *
     * @param p A prover symbol expression.
     */
    public void beginPostfixPSymbol(PSymbol p) {}

    /**
     * <p>This method is called before visiting a {@link PAlternatives}.</p>
     *
     * @param p A prover alternative expression.
     */
    public void beginPAlternatives(PAlternatives p) {}

    /**
     * <p>This method is called before visiting a {@link PLambda}.</p>
     *
     * @param p A prover lambda expression.
     */
    public void beginPLambda(PLambda p) {}

    /**
     * <p>This method is called before visiting a {@link PExp PExp's}
     * children.</p>
     *
     * @param p A prover expression.
     */
    public void beginChildren(PExp p) {}

    /**
     * <p>This method is called right before we end the visit for
     * a {@link PSymbol}.</p>
     *
     * @param p A prover symbol expression.
     */
    public void fencepostPSymbol(PSymbol p) {}

    /**
     * <p>This method is called right before we end the visit for
     * a prefix form {@link PSymbol}.</p>
     *
     * @param p A prover symbol expression.
     */
    public void fencepostPrefixPSymbol(PSymbol p) {}

    /**
     * <p>This method is called right before we end the visit for
     * an infix form {@link PSymbol}.</p>
     *
     * @param p A prover symbol expression.
     */
    public void fencepostInfixPSymbol(PSymbol p) {}

    /**
     * <p>This method is called right before we end the visit for
     * an outfix form {@link PSymbol}.</p>
     *
     * @param p A prover symbol expression.
     */
    public void fencepostOutfixPSymbol(PSymbol p) {}

    /**
     * <p>This method is called right before we end the visit for
     * a postfix form {@link PSymbol}.</p>
     *
     * @param p A prover symbol expression.
     */
    public void fencepostPostfixPSymbol(PSymbol p) {}

    /**
     * <p>This method is called right before we end the visit for
     * a {@link PAlternatives}.</p>
     *
     * @param p A prover alternative expression.
     */
    public void fencepostPAlternatives(PAlternatives p) {}

    /**
     * <p>This method is called right before we end the visit for
     * a {@link PLambda}.</p>
     *
     * @param p A prover lambda expression.
     */
    public void fencepostPLambda(PLambda p) {}

    /**
     * <p>This method is called after visiting a {@link PExp}.</p>
     *
     * @param p A prover expression.
     */
    public void endPExp(PExp p) {}

    /**
     * <p>This method is called after visiting a {@link PSymbol}.</p>
     *
     * @param p A prover symbol expression.
     */
    public void endPSymbol(PSymbol p) {}

    /**
     * <p>This method is called after visiting a prefix form
     * {@link PSymbol}.</p>
     *
     * @param p A prover symbol expression.
     */
    public void endPrefixPSymbol(PSymbol p) {}

    /**
     * <p>This method is called after visiting an infix form
     * {@link PSymbol}.</p>
     *
     * @param p A prover symbol expression.
     */
    public void endInfixPSymbol(PSymbol p) {}

    /**
     * <p>This method is called after visiting an outfix form
     * {@link PSymbol}.</p>
     *
     * @param p A prover symbol expression.
     */
    public void endOutfixPSymbol(PSymbol p) {}

    /**
     * <p>This method is called after visiting a postfix form
     * {@link PSymbol}.</p>
     *
     * @param p A prover symbol expression.
     */
    public void endPostfixPSymbol(PSymbol p) {}

    /**
     * <p>This method is called after visiting a {@link PAlternatives}.</p>
     *
     * @param p A prover alternative expression.
     */
    public void endPAlternatives(PAlternatives p) {}

    /**
     * <p>This method is called after visiting a {@link PLambda}.</p>
     *
     * @param p A prover lambda expression.
     */
    public void endPLambda(PLambda p) {}

    /**
     * <p>This method is called after visiting a {@link PExp PExp's}
     * children.</p>
     *
     * @param p A prover expression.
     */
    public void endChildren(PExp p) {}

}
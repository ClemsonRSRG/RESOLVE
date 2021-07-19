/*
 * PExpTextRenderingVisitor.java
 * ---------------------------------
 * Copyright (c) 2021
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
import java.io.IOException;

/**
 * <p>
 * This class visits a {@link PExp PExp} and its sub-expressions to generate a
 * text representation.
 * </p>
 *
 * @author Hampton Smith
 * @author Mike Kabbani
 * @author Daniel Welch
 * @version 2.0
 */
public class PExpTextRenderingVisitor extends PExpVisitor {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * An object used to append the output.
     * </p>
     */
    private final Appendable myOutput;

    /**
     * <p>
     * An alternative sub-expression that we have encountered..
     * </p>
     */
    private PAlternatives myEncounteredAlternative;

    /**
     * <p>
     * An sub-expression encountered during out visit.
     * </p>
     */
    private PExp myEncounteredResult;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This creates a visitor for rendering a {@link PExp}.
     * </p>
     *
     * @param w An appendable object used to store the output string.
     */
    public PExpTextRenderingVisitor(Appendable w) {
        myOutput = w;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * This method is called before visiting a {@link PExp}.
     * </p>
     *
     * @param p A prover expression.
     */
    @Override
    public final void beginPExp(PExp p) {
        if (myEncounteredAlternative != null) {
            if (myEncounteredResult == null) {
                myEncounteredResult = p;
            }
            else {
                try {
                    myEncounteredResult = null;
                    myOutput.append(", if ");
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * <p>
     * This method is called before visiting a prefix form {@link PSymbol}.
     * </p>
     *
     * @param p A prover symbol expression.
     */
    @Override
    public final void beginPrefixPSymbol(PSymbol p) {
        try {
            myOutput.append(p.name);

            if (p.arguments.size() > 0) {
                myOutput.append("(");
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * <p>
     * This method is called before visiting an infix form {@link PSymbol}.
     * </p>
     *
     * @param p A prover symbol expression.
     */
    @Override
    public final void beginInfixPSymbol(PSymbol p) {
        try {
            myOutput.append("(");
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * <p>
     * This method is called before visiting an outfix form {@link PSymbol}.
     * </p>
     *
     * @param p A prover symbol expression.
     */
    @Override
    public final void beginOutfixPSymbol(PSymbol p) {
        try {
            myOutput.append(p.leftPrint);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * <p>
     * This method is called before visiting a postfix form {@link PSymbol}.
     * </p>
     *
     * @param p A prover symbol expression.
     */
    @Override
    public final void beginPostfixPSymbol(PSymbol p) {
        try {
            if (p.arguments.size() > 0) {
                myOutput.append("(");
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * <p>
     * This method is called before visiting a {@link PAlternatives}.
     * </p>
     *
     * @param p A prover alternative expression.
     */
    @Override
    public final void beginPAlternatives(PAlternatives p) {
        try {
            myOutput.append("{");
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * <p>
     * This method is called before visiting a {@link PLambda}.
     * </p>
     *
     * @param l A prover lambda expression.
     */
    @Override
    public final void beginPLambda(PLambda l) {
        try {
            myOutput.append("lambda (");

            boolean first = true;
            for (PLambda.Parameter p : l.parameters) {
                if (first) {
                    first = false;
                }
                else {
                    myOutput.append(", ");
                }

                myOutput.append("" + p);
            }

            myOutput.append(").");
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * <p>
     * This method is called right before we end the visit for a prefix form
     * {@link PSymbol}.
     * </p>
     *
     * @param p A prover symbol expression.
     */
    @Override
    public final void fencepostPrefixPSymbol(PSymbol p) {
        try {
            myOutput.append(", ");
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * <p>
     * This method is called right before we end the visit for an infix form
     * {@link PSymbol}.
     * </p>
     *
     * @param p A prover symbol expression.
     */
    @Override
    public final void fencepostInfixPSymbol(PSymbol p) {
        try {
            myOutput.append(" " + p.name + " ");
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * <p>
     * This method is called right before we end the visit for an outfix form
     * {@link PSymbol}.
     * </p>
     *
     * @param p A prover symbol expression.
     */
    @Override
    public final void fencepostOutfixPSymbol(PSymbol p) {
        try {
            myOutput.append(", ");
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * <p>
     * This method is called right before we end the visit for a postfix form
     * {@link PSymbol}.
     * </p>
     *
     * @param p A prover symbol expression.
     */
    @Override
    public final void fencepostPostfixPSymbol(PSymbol p) {
        try {
            myOutput.append(", ");
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * <p>
     * This method is called right before we end the visit for a
     * {@link PAlternatives}.
     * </p>
     *
     * @param p A prover alternative expression.
     */
    @Override
    public final void fencepostPAlternatives(PAlternatives p) {
        try {
            myOutput.append("; ");
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * <p>
     * This method is called after visiting a prefix form {@link PSymbol}.
     * </p>
     *
     * @param p A prover symbol expression.
     */
    @Override
    public final void endPrefixPSymbol(PSymbol p) {
        try {
            if (p.arguments.size() > 0) {
                myOutput.append(")");
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * <p>
     * This method is called after visiting an infix form {@link PSymbol}.
     * </p>
     *
     * @param p A prover symbol expression.
     */
    @Override
    public final void endInfixPSymbol(PSymbol p) {
        try {
            myOutput.append(")");
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * <p>
     * This method is called after visiting an outfix form {@link PSymbol}.
     * </p>
     *
     * @param p A prover symbol expression.
     */
    @Override
    public final void endOutfixPSymbol(PSymbol p) {
        try {
            myOutput.append(p.rightPrint);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * <p>
     * This method is called after visiting a postfix form {@link PSymbol}.
     * </p>
     *
     * @param p A prover symbol expression.
     */
    @Override
    public final void endPostfixPSymbol(PSymbol p) {
        try {
            if (p.arguments.size() > 0) {
                myOutput.append(")");
            }
            myOutput.append(p.name);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * <p>
     * This method is called after visiting a {@link PAlternatives}.
     * </p>
     *
     * @param p A prover alternative expression.
     */
    @Override
    public final void endPAlternatives(PAlternatives p) {
        try {
            myOutput.append(", otherwise}");
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}

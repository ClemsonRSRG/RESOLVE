/*
 * PSymbol.java
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
package edu.clemson.cs.rsrg.prover.absyn.expressions;

import edu.clemson.cs.rsrg.prover.absyn.PExp;
import edu.clemson.cs.rsrg.prover.absyn.treewalkers.PExpVisitor;
import edu.clemson.cs.rsrg.prover.immutableadts.ArrayBackedImmutableList;
import edu.clemson.cs.rsrg.prover.immutableadts.ImmutableList;
import edu.clemson.cs.rsrg.typeandpopulate.mathtypes.MTType;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * <p>A {@code PSymbol} represents a reference to a named element such as
 * a variable, constant, or function.  More specifically, all three are
 * represented as function calls, with the former two represented as functions
 * with no arguments.</p>
 *
 * @author Hampton Smith
 * @author Mike Kabbani
 * @version 2.0
 */
public class PSymbol extends PExp {

    // ===========================================================
    // ClauseType
    // ===========================================================

    /**
     * <p>This defines the various different assertion clause types.</p>
     *
     * @author Hampton Smith
     * @version 2.0
     */
    public enum Quantification {
        NONE {

            @Override
            protected Quantification flipped() {
                return NONE;
            }
        },
        FOR_ALL {

            @Override
            protected Quantification flipped() {
                return THERE_EXISTS;
            }
        },
        THERE_EXISTS {

            @Override
            protected Quantification flipped() {
                return FOR_ALL;
            }
        };

        /**
         * <p>This method returns the inverse quantification.</p>
         *
         * @return A quantifier.
         */
        protected abstract Quantification flipped();
    }

    /**
     * <p>This indicates how a {@link PExp} will be displayed.</p>
     *
     * @author Hampton Smith
     * @version 2.0
     */
    public enum DisplayType {
        PREFIX {

            @Override
            protected String toString(PSymbol s) {
                String argumentsAsString;

                if (s.arguments.size() == 0) {
                    argumentsAsString = "";
                }
                else {
                    argumentsAsString =
                            "(" + delimit(s.arguments.iterator(), ", ") + ")";
                }

                return s.name + argumentsAsString;
            }

            @Override
            protected void beginAccept(PExpVisitor v, PSymbol s) {
                v.beginPrefixPSymbol(s);
            }

            @Override
            protected void fencepostAccept(PExpVisitor v, PSymbol s) {
                v.fencepostPrefixPSymbol(s);
            }

            @Override
            protected void endAccept(PExpVisitor v, PSymbol s) {
                v.endPrefixPSymbol(s);
            }
        },
        INFIX {

            @Override
            protected String toString(PSymbol s) {
                return "("
                        + delimit(s.arguments.iterator(), " " + s.name + " ")
                        + ")";
            }

            @Override
            protected void beginAccept(PExpVisitor v, PSymbol s) {
                v.beginInfixPSymbol(s);
            }

            @Override
            protected void fencepostAccept(PExpVisitor v, PSymbol s) {
                v.fencepostInfixPSymbol(s);
            }

            @Override
            protected void endAccept(PExpVisitor v, PSymbol s) {
                v.endInfixPSymbol(s);
            }
        },
        POSTFIX {

            @Override
            protected String toString(PSymbol s) {
                String retval = delimit(s.arguments.iterator(), ", ");

                if (s.arguments.size() > 1) {
                    retval = "(" + retval + ")";
                }

                return retval + s.name;
            }

            @Override
            protected void beginAccept(PExpVisitor v, PSymbol s) {
                v.beginPostfixPSymbol(s);
            }

            @Override
            protected void fencepostAccept(PExpVisitor v, PSymbol s) {
                v.fencepostPostfixPSymbol(s);
            }

            @Override
            protected void endAccept(PExpVisitor v, PSymbol s) {
                v.endPostfixPSymbol(s);
            }
        },
        OUTFIX {

            @Override
            protected String toString(PSymbol s) {
                return s.leftPrint + delimit(s.arguments.iterator(), ", ")
                        + s.rightPrint;
            }

            @Override
            protected void beginAccept(PExpVisitor v, PSymbol s) {
                v.beginOutfixPSymbol(s);
            }

            @Override
            protected void fencepostAccept(PExpVisitor v, PSymbol s) {
                v.fencepostOutfixPSymbol(s);
            }

            @Override
            protected void endAccept(PExpVisitor v, PSymbol s) {
                v.endOutfixPSymbol(s);
            }
        };

        /**
         * <p>This method returns a {@code PSymbol} in string format.</p>
         *
         * @param s A prover symbol expression.
         *
         * @return {@code PSymbol} as a string.
         */
        protected abstract String toString(PSymbol s);

        /**
         * <p>This method is called to before visiting a {@link PSymbol}
         * using the specified {@link PExpVisitor}.</p>
         *
         * @param v A prover expression visitor.
         * @param s A prover symbol expression.
         */
        protected abstract void beginAccept(PExpVisitor v, PSymbol s);

        /**
         * <p>This method is called right before we end the visit for
         * a {@link PSymbol} using the specified {@link PExpVisitor}.</p></p>
         *
         * @param v A prover expression visitor.
         * @param s A prover symbol expression.
         */
        protected abstract void fencepostAccept(PExpVisitor v, PSymbol s);

        /**
         * <p>This method is called to after visiting a {@link PSymbol}
         * using the specified {@link PExpVisitor}.</p>
         *
         * @param v A prover expression visitor.
         * @param s A prover symbol expression.
         */
        protected abstract void endAccept(PExpVisitor v, PSymbol s);
    }

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The expression's name.</p> */
    public final String name;

    /** <p>The expression's arguments.</p> */
    public final ImmutableList<PExp> arguments;

    /** <p>The expression's quantification.</p> */
    public final Quantification quantification;

    /** <p>The expression's display type.</p> */
    private final DisplayType displayType;

    /** <p>The left and right hand side of the expression.</p> */
    public final String leftPrint, rightPrint;

    /** <p>The expression's pre-application mathematical type.</p> */
    private MTType myPreApplicationType;

    /** <p>The expression's number of arguments size.</p> */
    private int myArgumentsSize;

    /** <p>The expression's scratch space used internally.</p> */
    private final PExp[] myScratchSpace;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This creates a prover representation of a reference to a
     * named element such as a variable, constant, or function.</p>
     *
     * @param type The expression's mathematical type.
     * @param typeValue The expression's mathematical type value.
     * @param leftPrint The expression's left hand side as a string.
     * @param rightPrint The expression's right hand side as a string.
     * @param arguments The expression's arguments.
     * @param quantification The expression's quantification.
     * @param display The expression's display type.
     */
    public PSymbol(MTType type, MTType typeValue, String leftPrint,
            String rightPrint, Collection<PExp> arguments,
            Quantification quantification, DisplayType display) {
        this(type, typeValue, leftPrint, rightPrint,
                new ArrayBackedImmutableList<>(arguments), quantification,
                display);
    }

    /**
     * <p>This creates a prover representation of a reference to a
     * named element such as a variable, constant, or function.</p>
     *
     * @param type The expression's mathematical type.
     * @param typeValue The expression's mathematical type value.
     * @param leftPrint The expression's left hand side as a string.
     * @param rightPrint The expression's right hand side as a string.
     * @param arguments The expression's arguments.
     * @param quantification The expression's quantification.
     * @param display The expression's display type.
     */
    public PSymbol(MTType type, MTType typeValue, String leftPrint,
            String rightPrint, ImmutableList<PExp> arguments,
            Quantification quantification, DisplayType display) {
        super(calculateHashes(leftPrint, rightPrint, arguments.iterator()),
                type, typeValue);

        if (rightPrint == null || leftPrint.equals(rightPrint)) {
            rightPrint = leftPrint;
            this.name = leftPrint;
        }
        else {
            this.name = leftPrint + rightPrint;
        }

        this.arguments = arguments;
        myArgumentsSize = arguments.size();
        myScratchSpace = new PExp[myArgumentsSize];

        this.quantification = quantification;
        this.leftPrint = leftPrint;
        this.rightPrint = rightPrint;

        displayType = display;
    }

    /**
     * <p>This creates a prover representation of a reference to a
     * named element such as a variable, constant, or function.</p>
     *
     * @param type The expression's mathematical type.
     * @param typeValue The expression's mathematical type value.
     * @param leftPrint The expression's left hand side as a string.
     * @param rightPrint The expression's right hand side as a string.
     * @param arguments The expression's arguments.
     * @param display The expression's display type.
     */
    public PSymbol(MTType type, MTType typeValue, String leftPrint,
            String rightPrint, Collection<PExp> arguments, DisplayType display) {
        this(type, typeValue, leftPrint, rightPrint, arguments,
                Quantification.NONE, display);
    }

    /**
     * <p>This creates a prover representation of a reference to a
     * named element such as a variable, constant, or function.</p>
     *
     * @param type The expression's mathematical type.
     * @param typeValue The expression's mathematical type value.
     * @param arguments The expression's arguments.
     * @param quantification The expression's quantification.
     * @param display The expression's display type.
     */
    public PSymbol(MTType type, MTType typeValue, String name,
            Collection<PExp> arguments, Quantification quantification,
            DisplayType display) {
        this(type, typeValue, name, null, arguments, quantification, display);
    }

    /**
     * <p>This creates a prover representation of a reference to a
     * named element such as a variable, constant, or function.</p>
     *
     * @param type The expression's mathematical type.
     * @param typeValue The expression's mathematical type value.
     * @param arguments The expression's arguments.
     * @param quantification The expression's quantification.
     */
    public PSymbol(MTType type, MTType typeValue, String name,
            Collection<PExp> arguments, Quantification quantification) {
        this(type, typeValue, name, null, arguments, quantification,
                DisplayType.PREFIX);
    }

    /**
     * <p>This creates a prover representation of a reference to a
     * named element such as a variable, constant, or function.</p>
     *
     * @param type The expression's mathematical type.
     * @param typeValue The expression's mathematical type value.
     * @param arguments The expression's arguments.
     * @param display The expression's display type.
     */
    public PSymbol(MTType type, MTType typeValue, String name,
            Collection<PExp> arguments, DisplayType display) {
        this(type, typeValue, name, arguments, Quantification.NONE, display);
    }

    /**
     * <p>This creates a prover representation of a reference to a
     * named element such as a variable, constant, or function.</p>
     *
     * @param type The expression's mathematical type.
     * @param typeValue The expression's mathematical type value.
     * @param arguments The expression's arguments.
     */
    public PSymbol(MTType type, MTType typeValue, String name,
            Collection<PExp> arguments) {
        this(type, typeValue, name, arguments, Quantification.NONE,
                DisplayType.PREFIX);
    }

    /**
     * <p>This creates a prover representation of a reference to a
     * named element such as a variable, constant, or function.</p>
     *
     * @param type The expression's mathematical type.
     * @param typeValue The expression's mathematical type value.
     * @param quantification The expression's quantification.
     */
    public PSymbol(MTType type, MTType typeValue, String name,
            Quantification quantification) {
        this(type, typeValue, name, new LinkedList<PExp>(), quantification,
                DisplayType.PREFIX);
    }

    /**
     * <p>This creates a prover representation of a reference to a
     * named element such as a variable, constant, or function.</p>
     *
     * @param type The expression's mathematical type.
     * @param typeValue The expression's mathematical type value.
     */
    public PSymbol(MTType type, MTType typeValue, String name) {
        this(type, typeValue, name, new LinkedList<PExp>(),
                Quantification.NONE, DisplayType.PREFIX);
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    // ===========================================================
    // Protected Methods
    // ===========================================================

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>This helper method computes a structure and value hash given
     * the specified arguments.</p>
     *
     * @param left The expression's left hand side as a string.
     * @param right The expression's right hand side as a string.
     * @param args The expression's arguments.
     *
     * @return This returns structure hash and value hash for a {@code PExp}
     */
    private static PExp.HashDuple calculateHashes(String left, String right,
            Iterator<PExp> args) {
        int structureHash;
        int leftHashCode = left.hashCode();
        int valueHash = leftHashCode;

        valueHash *= 59;
        if (right == null) {
            valueHash += leftHashCode;
        }
        else {
            valueHash += right.hashCode();
        }

        if (args.hasNext()) {
            structureHash = 17;

            int argMod = 2;
            PExp arg;
            while (args.hasNext()) {
                arg = args.next();
                structureHash += arg.structureHash * argMod;
                valueHash += arg.valueHash * argMod;
                argMod++;
            }

        }
        else {
            structureHash = 0;
        }

        return new PExp.HashDuple(structureHash, valueHash);
    }

    /**
     * <p>This helper method creates a string output with
     * the delimiter.</p>
     *
     * @param i An iterator.
     * @param delimiter The delimiter symbol.
     *
     * @return The expression with the delimiter symbol.
     */
    private static String delimit(Iterator<?> i, String delimiter) {
        StringBuilder retval = new StringBuilder();

        boolean first = true;
        while (i.hasNext()) {
            if (!first) {
                retval.append(delimiter);
            }
            else {
                first = false;
            }

            retval.append(i.next().toString());
        }

        return retval.toString();
    }

}
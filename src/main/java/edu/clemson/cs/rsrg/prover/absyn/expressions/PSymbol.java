/*
 * PSymbol.java
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
package edu.clemson.cs.rsrg.prover.absyn.expressions;

import edu.clemson.cs.rsrg.prover.absyn.PExp;
import edu.clemson.cs.rsrg.prover.absyn.iterators.PExpSubexpressionIterator;
import edu.clemson.cs.rsrg.prover.absyn.iterators.PSymbolArgumentIterator;
import edu.clemson.cs.rsrg.prover.absyn.visitors.PExpVisitor;
import edu.clemson.cs.rsrg.prover.exception.BindingException;
import edu.clemson.cs.rsrg.prover.immutableadts.ArrayBackedImmutableList;
import edu.clemson.cs.rsrg.prover.immutableadts.ImmutableList;
import edu.clemson.cs.rsrg.typeandpopulate.mathtypes.MTFunction;
import edu.clemson.cs.rsrg.typeandpopulate.mathtypes.MTType;
import java.util.*;

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

    /**
     * {@inheritDoc}
     */
    @Override
    public final void accept(PExpVisitor v) {
        v.beginPExp(this);
        v.beginPSymbol(this);
        displayType.beginAccept(v, this);

        v.beginChildren(this);

        boolean first = true;
        for (PExp arg : arguments) {
            if (!first) {
                displayType.fencepostAccept(v, this);
                v.fencepostPSymbol(this);
            }
            first = false;

            arg.accept(v);
        }

        v.endChildren(this);

        displayType.endAccept(v, this);
        v.endPSymbol(this);
        v.endPExp(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void bindTo(PExp target, Map<PExp, PExp> accumulator)
            throws BindingException {

        PSymbol sTarget;
        try {
            sTarget = (PSymbol) target;
        }
        catch (ClassCastException e) {
            //We can only bind against other instances of PSymbol
            throw BINDING_EXCEPTION;
        }

        //Note that at this point we're guaranteed that target is of the same
        //type as us
        if (quantification == Quantification.FOR_ALL) {
            if (!typeMatches(target)) {
                //We can only bind against something in a subset of us
                throw BINDING_EXCEPTION;
            }

            if (myArgumentsSize == 0) {
                accumulator.put(this, target);
            }
            else {
                if (myArgumentsSize != sTarget.arguments.size()) {
                    //If we're a function, we can only bind against another
                    //function with the same number of arguments
                    throw BINDING_EXCEPTION;
                }

                accumulator.put(new PSymbol(myMathType, myMathTypeValue, name),
                        new PSymbol(sTarget.getPreApplicationType(), null,
                                sTarget.name));

                Iterator<PExp> thisArgumentsIter = arguments.iterator();
                Iterator<PExp> targetArgumentsIter =
                        sTarget.arguments.iterator();
                while (thisArgumentsIter.hasNext()) {
                    thisArgumentsIter.next().substitute(accumulator).bindTo(
                            targetArgumentsIter.next(), accumulator);
                }
            }
        }
        else {
            //TODO : This isn't right.  The real logic should be "is the
            //       expression I represent is in the type of target", but right
            //       now "isKnownToBeIn" in TypeGraph doesn't operate on PExps
            if (!(myMathType.isSubtypeOf(target.getMathType()) || target
                    .getMathType().isSubtypeOf(myMathType))) {
                //We can only match something we're a subset of
                throw BINDING_EXCEPTION;
            }

            if (!name.equals(sTarget.name)) {
                throw BINDING_EXCEPTION;
            }

            if (myArgumentsSize != sTarget.arguments.size()) {
                //We aren't a "for all", so everything better be exact
                throw BINDING_EXCEPTION;
            }

            Iterator<PExp> thisArgumentsIter = arguments.iterator();
            Iterator<PExp> targetArgumentsIter = sTarget.arguments.iterator();
            while (thisArgumentsIter.hasNext()) {
                thisArgumentsIter.next().substitute(accumulator).bindTo(
                        targetArgumentsIter.next(), accumulator);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean containsExistential() {
        boolean retval = (quantification == Quantification.THERE_EXISTS);

        Iterator<PExp> argumentIter = arguments.iterator();
        while (!retval && argumentIter.hasNext()) {
            retval = argumentIter.next().containsExistential();
        }

        return retval;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean containsName(String name) {
        boolean retval = this.name.equals(name);

        Iterator<PExp> argumentIterator = arguments.iterator();
        while (!retval && argumentIterator.hasNext()) {
            retval = argumentIterator.next().containsName(name);
        }

        return retval;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean equals(Object o) {
        boolean retval = (o instanceof PSymbol);

        if (retval) {
            PSymbol oAsPSymbol = (PSymbol) o;

            retval =
                    (oAsPSymbol.valueHash == valueHash)
                            && name.equals(oAsPSymbol.name);

            if (retval) {
                Iterator<PExp> localArgs = arguments.iterator();
                Iterator<PExp> oArgs = oAsPSymbol.arguments.iterator();

                while (retval && localArgs.hasNext() && oArgs.hasNext()) {
                    retval = localArgs.next().equals(oArgs.next());
                }

                if (retval) {
                    retval = !(localArgs.hasNext() || oArgs.hasNext());
                }
            }
        }

        return retval;
    }

    /**
     * <p>This method attempts to flip all quantifiers to generate
     * a new expression.</p>
     *
     * @return A new {@link PExp}.
     */
    @Override
    public final PExp flipQuantifiers() {
        PExp retval;

        boolean argumentChanged = false;
        int argIndex = 0;
        Iterator<PExp> argumentsIter = arguments.iterator();

        PExp argument;
        while (argumentsIter.hasNext()) {
            argument = argumentsIter.next();

            myScratchSpace[argIndex] = argument.flipQuantifiers();

            argumentChanged |= (myScratchSpace[argIndex] != argument);
            argIndex++;
        }

        if (argumentChanged) {
            retval =
                    new PSymbol(myMathType, myMathTypeValue, leftPrint,
                            rightPrint, Arrays.asList(myScratchSpace),
                            quantification.flipped(), displayType);
        }
        else {
            Quantification flipped = quantification.flipped();

            if (flipped == quantification) {
                retval = this;
            }
            else {
                retval =
                        new PSymbol(myMathType, myMathTypeValue, leftPrint,
                                rightPrint, arguments, flipped, displayType);
            }
        }

        return retval;
    }

    /**
     * <p>This method returns a set of symbols that are not quantified.</p>
     *
     * @return A set of symbol names.
     */
    public final Set<String> getNonQuantifiedSymbols() {
        Set<String> result = new HashSet<>();

        if (quantification == Quantification.NONE) {
            result.add(getTopLevelOperation());
        }

        Iterator<PExp> argumentIter = arguments.iterator();
        while (argumentIter.hasNext()) {
            Set<String> r =
                    ((PSymbol) argumentIter.next()).getNonQuantifiedSymbols();
            result.addAll(r);
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final ImmutableList<PExp> getSubExpressions() {
        return arguments;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final PExpSubexpressionIterator getSubExpressionIterator() {
        return new PSymbolArgumentIterator(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String getTopLevelOperation() {
        return getCanonicalName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean isEquality() {
        return (myArgumentsSize == 2 && name.equals("="));
    }

    /**
     * <p>This method checks to see if this expression represents
     * some kind of function.</p>
     *
     * @return {@code true} if it is some kind of function expression,
     * {@code false} otherwise.
     */
    public final boolean isFunction() {
        // Function symbols do not always have arguments
        return myArgumentsSize > 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean isLiteral() {
        //XXX : All PExps originally come from Exps.  Currently there is no way
        //      to tell if an Exp is a literal.  I.e., in an expression like
        //      "S'' = empty_string", the left and right sides of the equality
        //      are indistinguishable except for their names.  Until this
        //      situation is resolved, literals should be hard coded here.
        return (name.equalsIgnoreCase("empty_string"))
                || (name.equals("0") || name.equals("1") || name.equals("true") || name
                        .equals("false"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean isObviouslyTrue() {
        return (myArgumentsSize == 0 && name.equalsIgnoreCase("true"))
                || (myArgumentsSize == 2 && name.equals("=") && arguments
                        .get(0).equals(arguments.get(1)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean isVariable() {
        return !isFunction();
    }

    /**
     * <p>This creates a new {@link PSymbol} by replacing the specified
     * index with the new argument.</p>
     *
     * @param index The index to be replaced.
     * @param newArgument A new argument expression.
     *
     * @return A new {@link PSymbol}.
     */
    public final PSymbol setArgument(int index, PExp newArgument) {
        ImmutableList<PExp> newArguments = arguments.set(index, newArgument);

        return new PSymbol(myMathType, myMathTypeValue, leftPrint, rightPrint,
                newArguments, quantification, displayType);
    }

    /**
     * <p>This creates a new {@link PSymbol} by replacing all the arguments.</p>
     *
     * @param newArguments A new list of arguments.
     *
     * @return A new {@link PSymbol}.
     */
    public final PSymbol setArguments(Collection<PExp> newArguments) {
        return new PSymbol(myMathType, myMathTypeValue, leftPrint, rightPrint,
                newArguments, quantification, displayType);
    }

    /**
     * <p>This creates a new {@link PSymbol} by replacing its name.</p>
     *
     * @param newName A new name.
     *
     * @return A new {@link PSymbol}.
     */
    public final PSymbol setName(String newName) {
        return new PSymbol(myMathType, myMathTypeValue, newName, rightPrint,
                arguments, quantification, displayType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final PExp substitute(Map<PExp, PExp> substitutions) {
        PExp retval = substitutions.get(this);

        if (retval == null) {
            String newLeft = leftPrint, newRight = rightPrint;
            Quantification newQuantification = quantification;

            if (arguments.size() > 0 && displayType.equals(DisplayType.PREFIX)) {
                PExp asVar =
                        new PSymbol(getMathType(), getMathTypeValue(), leftPrint,
                                quantification);

                PExp functionSubstitution = substitutions.get(asVar);

                if (functionSubstitution != null) {
                    newLeft = ((PSymbol) functionSubstitution).leftPrint;
                    newRight = ((PSymbol) functionSubstitution).rightPrint;
                    newQuantification =
                            ((PSymbol) functionSubstitution).quantification;
                }
            }

            boolean argumentChanged = false;
            int argIndex = 0;
            Iterator<PExp> argumentsIter = arguments.iterator();

            PExp argument;
            while (argumentsIter.hasNext()) {
                argument = argumentsIter.next();

                myScratchSpace[argIndex] = argument.substitute(substitutions);

                argumentChanged |= (myScratchSpace[argIndex] != argument);
                argIndex++;
            }

            if (argumentChanged) {
                retval =
                        new PSymbol(myMathType, myMathTypeValue, newLeft, newRight,
                                new ArrayBackedImmutableList<>(
                                        myScratchSpace), newQuantification,
                                displayType);
            }
            else {
                // changed this to handle case where func name changes but args don't -- mike
                retval =
                        new PSymbol(myMathType, myMathTypeValue, newLeft, newRight,
                                arguments, newQuantification, displayType);
            }
        }

        return retval;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final PSymbol withSubExpressionReplaced(int i, PExp e) {
        if (i < 0 && i >= arguments.size()) {
            throw new IndexOutOfBoundsException("" + i);
        }

        ImmutableList<PExp> newArguments = arguments.set(i, e);

        return new PSymbol(myMathType, myMathTypeValue, leftPrint, rightPrint,
                newArguments, quantification, displayType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final PSymbol withTypeReplaced(MTType t) {
        return new PSymbol(t, myMathTypeValue, leftPrint, rightPrint,
                arguments, quantification, displayType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final PSymbol withTypeValueReplaced(MTType t) {
        return new PSymbol(myMathType, t, leftPrint, rightPrint, arguments,
                quantification, displayType);
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * {@inheritDoc}
     */
    @Override
    protected final List<PExp> getFunctionApplicationsNoCache() {
        List<PExp> result = new LinkedList<>();

        if (myArgumentsSize > 0) {
            result.add(this);
        }

        Iterator<PExp> argumentIter = arguments.iterator();
        List<PExp> argumentFunctions;
        while (argumentIter.hasNext()) {
            argumentFunctions = argumentIter.next().getFunctionApplications();
            result.addAll(argumentFunctions);
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final Set<PSymbol> getQuantifiedVariablesNoCache() {
        Set<PSymbol> result = new HashSet<>();

        if (quantification != Quantification.NONE) {
            if (arguments.size() == 0) {
                result.add(this);
            }
            else {
                result.add(new PSymbol(getMathType(), null, name, quantification));
            }
        }

        Iterator<PExp> argumentIter = arguments.iterator();
        Set<PSymbol> argumentVariables;
        while (argumentIter.hasNext()) {
            argumentVariables = argumentIter.next().getQuantifiedVariables();
            result.addAll(argumentVariables);
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final Set<String> getSymbolNamesNoCache() {
        Set<String> result = new HashSet<>();

        if (quantification == Quantification.NONE) {
            result.add(getCanonicalName());
        }

        Iterator<PExp> argumentIter = arguments.iterator();
        Set<String> argumentSymbols;
        while (argumentIter.hasNext()) {
            argumentSymbols = argumentIter.next().getSymbolNames();
            result.addAll(argumentSymbols);
        }

        return result;
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>An helper method that computes a structure and value hash given
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

    /**
     * <p>An helper method for retrieving this expression's canonical name.</p>
     *
     * @return A canonical representation of this expression.
     */
    private String getCanonicalName() {
        String result;

        if (displayType.equals(DisplayType.OUTFIX)) {
            result = leftPrint + "_" + rightPrint;
        }
        else {
            result = name;
        }

        return result;
    }

    /**
     * <p>An helper method that gets the pre-application mathematical type
     * associated with this expression.</p>
     *
     * @return A {@link MTType} type object.
     */
    private MTType getPreApplicationType() {
        if (myPreApplicationType == null) {
            List<MTType> argTypes = new LinkedList<>();
            for (PExp arg : arguments) {
                argTypes.add(arg.getMathType());
            }

            myPreApplicationType =
                    new MTFunction(getMathType().getTypeGraph(), getMathType(),
                            argTypes);
        }

        return myPreApplicationType;
    }
}
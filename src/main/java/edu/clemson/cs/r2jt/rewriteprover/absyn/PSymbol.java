/**
 * PSymbol.java
 * ---------------------------------
 * Copyright (c) 2014
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.rewriteprover.absyn;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.clemson.cs.r2jt.typeandpopulate.MTType;
import edu.clemson.cs.r2jt.rewriteprover.immutableadts.ArrayBackedImmutableList;
import edu.clemson.cs.r2jt.rewriteprover.immutableadts.ImmutableList;
import edu.clemson.cs.r2jt.typeandpopulate.MTFunction;

/**
 * <p>A <code>PSymbol</code> represents a reference to a named element such as
 * a variable, constant, or function.  More specifically, all three are 
 * represented as function calls, with the former two represented as functions 
 * with no arguments.</p>
 */
public class PSymbol extends PExp {

    public static enum Quantification {

        NONE {

            protected Quantification flipped() {
                return NONE;
            }
        },
        FOR_ALL {

            protected Quantification flipped() {
                return THERE_EXISTS;
            }
        },
        THERE_EXISTS {

            protected Quantification flipped() {
                return FOR_ALL;
            }
        };

        protected abstract Quantification flipped();
    }

    public static enum DisplayType {

        PREFIX {

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

            protected void beginAccept(PExpVisitor v, PSymbol s) {
                v.beginPrefixPSymbol(s);
            }

            protected void fencepostAccept(PExpVisitor v, PSymbol s) {
                v.fencepostPrefixPSymbol(s);
            }

            protected void endAccept(PExpVisitor v, PSymbol s) {
                v.endPrefixPSymbol(s);
            }
        },
        INFIX {

            protected String toString(PSymbol s) {
                return "("
                        + delimit(s.arguments.iterator(), " " + s.name + " ")
                        + ")";
            }

            protected void beginAccept(PExpVisitor v, PSymbol s) {
                v.beginInfixPSymbol(s);
            }

            protected void fencepostAccept(PExpVisitor v, PSymbol s) {
                v.fencepostInfixPSymbol(s);
            }

            protected void endAccept(PExpVisitor v, PSymbol s) {
                v.endInfixPSymbol(s);
            }
        },
        POSTFIX {

            protected String toString(PSymbol s) {
                String retval = delimit(s.arguments.iterator(), ", ");

                if (s.arguments.size() > 1) {
                    retval = "(" + retval + ")";
                }

                return retval + s.name;
            }

            protected void beginAccept(PExpVisitor v, PSymbol s) {
                v.beginPostfixPSymbol(s);
            }

            protected void fencepostAccept(PExpVisitor v, PSymbol s) {
                v.fencepostPostfixPSymbol(s);
            }

            protected void endAccept(PExpVisitor v, PSymbol s) {
                v.endPostfixPSymbol(s);
            }
        },
        OUTFIX {

            protected String toString(PSymbol s) {
                return s.leftPrint + delimit(s.arguments.iterator(), ", ")
                        + s.rightPrint;
            }

            protected void beginAccept(PExpVisitor v, PSymbol s) {
                v.beginOutfixPSymbol(s);
            }

            protected void fencepostAccept(PExpVisitor v, PSymbol s) {
                v.fencepostOutfixPSymbol(s);
            }

            protected void endAccept(PExpVisitor v, PSymbol s) {
                v.endOutfixPSymbol(s);
            }
        };

        protected abstract String toString(PSymbol s);

        protected abstract void beginAccept(PExpVisitor v, PSymbol s);

        protected abstract void fencepostAccept(PExpVisitor v, PSymbol s);

        protected abstract void endAccept(PExpVisitor v, PSymbol s);
    }

    public final String name;
    public final ImmutableList<PExp> arguments;
    public final Quantification quantification;

    final DisplayType displayType;
    final String leftPrint, rightPrint;

    private MTType myPreApplicationType;

    private int myArgumentsSize;
    private final PExp[] myScratchSpace;

    public PSymbol(MTType type, MTType typeValue, String leftPrint,
            String rightPrint, Collection<PExp> arguments,
            Quantification quantification, DisplayType display) {

        this(type, typeValue, leftPrint, rightPrint,
                new ArrayBackedImmutableList<PExp>(arguments), quantification,
                display);
    }

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

    public PSymbol(MTType type, MTType typeValue, String leftPrint,
            String rightPrint, Collection<PExp> arguments, DisplayType display) {
        this(type, typeValue, leftPrint, rightPrint, arguments,
                Quantification.NONE, display);
    }

    public PSymbol(MTType type, MTType typeValue, String name,
            Collection<PExp> arguments, Quantification quantification,
            DisplayType display) {
        this(type, typeValue, name, null, arguments, quantification, display);
    }

    public PSymbol(MTType type, MTType typeValue, String name,
            Collection<PExp> arguments, Quantification quantification) {
        this(type, typeValue, name, null, arguments, quantification,
                DisplayType.PREFIX);
    }

    public PSymbol(MTType type, MTType typeValue, String name,
            Collection<PExp> arguments, DisplayType display) {
        this(type, typeValue, name, arguments, Quantification.NONE, display);
    }

    public PSymbol(MTType type, MTType typeValue, String name,
            Collection<PExp> arguments) {
        this(type, typeValue, name, arguments, Quantification.NONE,
                DisplayType.PREFIX);
    }

    public PSymbol(MTType type, MTType typeValue, String name,
            Quantification quantification) {
        this(type, typeValue, name, new LinkedList<PExp>(), quantification,
                DisplayType.PREFIX);
    }

    public PSymbol(MTType type, MTType typeValue, String name) {
        this(type, typeValue, name, new LinkedList<PExp>(),
                Quantification.NONE, DisplayType.PREFIX);
    }

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

    public void accept(PExpVisitor v) {
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

    public final MTType getPreApplicationType() {
        if (myPreApplicationType == null) {
            List<MTType> argTypes = new LinkedList<MTType>();
            for (PExp arg : arguments) {
                argTypes.add(arg.getType());
            }

            myPreApplicationType =
                    new MTFunction(getType().getTypeGraph(), getType(),
                            argTypes);
        }

        return myPreApplicationType;
    }

    public boolean isFunction() {
        // Function symbols do not always have arguments
        return myArgumentsSize > 0;
        //return (myType.getClass().getSimpleName().contains("MTFunction"));
    }

    @Override
    public boolean isVariable() {
        return !isFunction();
    }

    public PSymbol withTypeReplaced(MTType t) {
        return new PSymbol(t, myTypeValue, leftPrint, rightPrint, arguments,
                quantification, displayType);
    }

    public PSymbol withTypeValueReplaced(MTType t) {
        return new PSymbol(myType, t, leftPrint, rightPrint, arguments,
                quantification, displayType);
    }

    public PSymbol withSubExpressionReplaced(int i, PExp e) {
        if (i < 0 && i >= arguments.size()) {
            throw new IndexOutOfBoundsException("" + i);
        }

        ImmutableList<PExp> newArguments = arguments.set(i, e);

        return new PSymbol(myType, myTypeValue, leftPrint, rightPrint,
                newArguments, quantification, displayType);
    }

    public ImmutableList<PExp> getSubExpressions() {
        return arguments;
    }

    public boolean equals(Object o) {
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

    private static String delimit(Iterator<?> i, String delimiter) {
        String retval = "";

        boolean first = true;
        while (i.hasNext()) {
            if (!first) {
                retval += delimiter;
            }
            else {
                first = false;
            }

            retval += i.next().toString();
        }

        return retval;
    }

    @Override
    public boolean isObviouslyTrue() {
        return (myArgumentsSize == 0 && name.equalsIgnoreCase("true"))
                || (myArgumentsSize == 2 && name.equals("=") && arguments
                        .get(0).equals(arguments.get(1)));
    }

    @Override
    public PExp substitute(Map<PExp, PExp> substitutions) {
        PExp retval = substitutions.get(this);

        if (retval == null) {

            String newLeft = leftPrint, newRight = rightPrint;
            Quantification newQuantification = quantification;

            if (arguments.size() > 0 && displayType.equals(DisplayType.PREFIX)) {
                PExp asVar =
                        new PSymbol(getType(), getTypeValue(), leftPrint,
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
                        new PSymbol(myType, myTypeValue, newLeft, newRight,
                                new ArrayBackedImmutableList<PExp>(
                                        myScratchSpace), newQuantification,
                                displayType);
            }
            else {
                // changed this to handle case where func name changes but args don't -- mike
                retval =
                        new PSymbol(myType, myTypeValue, newLeft, newRight,
                                arguments, newQuantification, displayType);
            }
        }

        return retval;
    }

    @Override
    protected void splitIntoConjuncts(List<PExp> accumulator) {
        if (myArgumentsSize == 2 && name.equals("and")) {
            arguments.get(0).splitIntoConjuncts(accumulator);
            arguments.get(1).splitIntoConjuncts(accumulator);
        }
        else {
            accumulator.add(this);
        }
    }

    public PSymbol setArgument(int index, PExp newArgument) {
        ImmutableList<PExp> newArguments = arguments.set(index, newArgument);

        return new PSymbol(myType, myTypeValue, leftPrint, rightPrint,
                newArguments, quantification, displayType);
    }

    public PSymbol setArguments(Collection<PExp> newArguments) {
        return new PSymbol(myType, myTypeValue, leftPrint, rightPrint,
                newArguments, quantification, displayType);
    }

    public PSymbol setName(String newName) {
        return new PSymbol(myType, myTypeValue, newName, rightPrint, arguments,
                quantification, displayType);
    }

    @Override
    public PExp flipQuantifiers() {
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
                    new PSymbol(myType, myTypeValue, leftPrint, rightPrint,
                            Arrays.asList(myScratchSpace), quantification
                                    .flipped(), displayType);
        }
        else {
            Quantification flipped = quantification.flipped();

            if (flipped == quantification) {
                retval = this;
            }
            else {
                retval =
                        new PSymbol(myType, myTypeValue, leftPrint, rightPrint,
                                arguments, flipped, displayType);
            }
        }

        return retval;
    }

    @Override
    public String getTopLevelOperation() {
        return getCanonicalName();
    }

    @Override
    public void bindTo(PExp target, Map<PExp, PExp> accumulator)
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

                accumulator.put(new PSymbol(myType, myTypeValue, name),
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
            //       expression I reresent is in the type of target", but right
            //       now "isKnownToBeIn" in TypeGraph doesn't operate on PExps
            if (!(myType.isSubtypeOf(target.myType) || target.myType
                    .isSubtypeOf(myType))) {
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

    @Override
    public PExpSubexpressionIterator getSubExpressionIterator() {
        return new PSymbolArgumentIterator(this);
    }

    @Override
    public boolean containsName(String name) {
        boolean retval = this.name.equals(name);

        Iterator<PExp> argumentIterator = arguments.iterator();
        while (!retval && argumentIterator.hasNext()) {
            retval = argumentIterator.next().containsName(name);
        }

        return retval;
    }

    @Override
    public Set<PSymbol> getQuantifiedVariablesNoCache() {
        Set<PSymbol> result = new HashSet<PSymbol>();

        if (quantification != Quantification.NONE) {
            if (arguments.size() == 0) {
                result.add(this);
            }
            else {
                result.add(new PSymbol(getType(), null, name, quantification));
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

    @Override
    public boolean containsExistential() {
        boolean retval = (quantification == Quantification.THERE_EXISTS);

        Iterator<PExp> argumentIter = arguments.iterator();
        while (!retval && argumentIter.hasNext()) {
            retval = argumentIter.next().containsExistential();
        }

        return retval;
    }

    @Override
    public List<PExp> getFunctionApplicationsNoCache() {
        List<PExp> result = new LinkedList<PExp>();

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

    @Override
    public Set<String> getSymbolNamesNoCache() {

        Set<String> result = new HashSet<String>();

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

    @Override
    public boolean isEquality() {
        return (myArgumentsSize == 2 && name.equals("="));
    }

    @Override
    public boolean isLiteral() {
        //XXX : All PExps originally come from Exps.  Currently there is no way
        //      to tell if an Exp is a literal.  I.e., in an expression like
        //      "S'' = empty_string", the left and right sides of the equality
        //      are indistinguishable except for their names.  Until this
        //      situation is resolved, literals should be hard coded here.
        return (name.equalsIgnoreCase("empty_string"))
                || (name.equals("0") || name.equals("1") || name.equals("true") || name
                        .equals("false"));
    }

    public String toSMTLIB(Map<String, MTType> typeMap) {
        String opString = getTopLevelOperation();
        boolean negate = false;
        if (opString == "/=") {
            opString = "=";
            negate = true;
        }
        else if (opString == "implies")
            opString = "=>";
        else if (opString.contains("_"))
            opString = opString.replace("_", "");
        String argsString = "";
        PExpSubexpressionIterator subIt = getSubExpressionIterator();
        while (subIt.hasNext()) {
            PExp cur = subIt.next();

            if (cur.getSubExpressions().size() > 0)
                argsString += "( " + cur.toSMTLIB(typeMap) + " ) ";
            else {
                String op = cur.getTopLevelOperation();
                if (op.contains("_"))
                    op = op.replace("_", "");
                argsString += " " + op + " ";
                if (typeMap != null)
                    typeMap.put(op, cur.getType());
            }
        }
        String combined = opString + " " + argsString;
        if (negate)
            return "not ( " + combined + ")";
        else
            return combined;
    }
}

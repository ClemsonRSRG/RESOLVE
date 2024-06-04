/*
 * MTFunctionApplication.java
 * ---------------------------------
 * Copyright (c) 2024
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.rsrg.typeandpopulate.mathtypes;

import edu.clemson.rsrg.typeandpopulate.typereasoning.TypeGraph;
import edu.clemson.rsrg.typeandpopulate.typevisitor.TypeVisitor;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>
 * A constructed type consisting of a function application over one or more argument types.
 * </p>
 *
 * @version 2.0
 */
public class MTFunctionApplication extends MTAbstract<MTFunctionApplication> {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * An integer value that helps us retrieve the hashcode for this class.
     * </p>
     */
    private static final int BASE_HASH = "MTFunctionApplication".hashCode();

    /**
     * <p>
     * The function type to be applied.
     * </p>
     */
    private final MTFunction myFunction;

    /**
     * <p>
     * The arguments for the function type.
     * </p>
     */
    private final List<MTType> myArguments;

    /**
     * <p>
     * The name used to describe this function application
     * </p>
     */
    private final String myName;

    /**
     * <p>
     * List of {@link MTType}s that are in this function application type.
     * </p>
     */
    private List<MTType> myComponents;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This constructs a function application type.
     * </p>
     *
     * @param g
     *            The current type graph.
     * @param f
     *            The function type to be applied.
     * @param name
     *            The name describing this function application.
     * @param arguments
     *            The arguments for the function type.
     */
    public MTFunctionApplication(TypeGraph g, MTFunction f, String name, MTType... arguments) {
        super(g);

        myFunction = f;
        myName = name;
        myArguments = new LinkedList<>();
        for (int i = 0; i < arguments.length; ++i) {
            myArguments.add(arguments[i]);
        }

        setUpComponents();
    }

    /**
     * <p>
     * This constructs a function application type.
     * </p>
     *
     * @param g
     *            The current type graph.
     * @param f
     *            The function type to be applied.
     * @param name
     *            The name describing this function application.
     * @param arguments
     *            The list of arguments for the function type.
     */
    public MTFunctionApplication(TypeGraph g, MTFunction f, String name, List<MTType> arguments) {
        super(g);

        myFunction = f;
        myName = name;
        myArguments = new LinkedList<>();
        myArguments.addAll(arguments);

        setUpComponents();
    }

    /**
     * <p>
     * This constructs a function application type using the {@code lambda} describe this function application.
     * </p>
     *
     * @param g
     *            The current type graph.
     * @param f
     *            The function type to be applied.
     * @param arguments
     *            The list of arguments for the function type.
     */
    public MTFunctionApplication(TypeGraph g, MTFunction f, List<MTType> arguments) {
        super(g);

        myFunction = f;
        myArguments = new LinkedList<>(arguments);
        myName = "\\lambda";

        setUpComponents();
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * This method is the {@code accept()} method in a visitor pattern for invoking an instance of {@link TypeVisitor}.
     * </p>
     *
     * @param v
     *            A visitor for types.
     */
    @Override
    public void accept(TypeVisitor v) {
        acceptOpen(v);

        v.beginChildren(this);

        myFunction.accept(v);

        for (MTType arg : myArguments) {
            arg.accept(v);
        }

        v.endChildren(this);

        acceptClose(v);
    }

    /**
     * <p>
     * This method implements the post-visit method for invoking an instance of {@link TypeVisitor}.
     * </p>
     *
     * @param v
     *            A visitor for types.
     */
    @Override
    public final void acceptClose(TypeVisitor v) {
        v.endMTFunctionApplication(this);
        v.endMTAbstract(this);
        v.endMTType(this);
    }

    /**
     * <p>
     * This method implements the pre-visit method for invoking an instance of {@link TypeVisitor}.
     * </p>
     *
     * @param v
     *            A visitor for types.
     */
    @Override
    public final void acceptOpen(TypeVisitor v) {
        v.beginMTType(this);
        v.beginMTAbstract(this);
        v.beginMTFunctionApplication(this);
    }

    /**
     * <p>
     * This method returns the argument at the specified index.
     * </p>
     *
     * @param i
     *            An index.
     *
     * @return The {@link MTType} at the specified index.
     */
    public final MTType getArgument(int i) {
        return myArguments.get(i);
    }

    /**
     * <p>
     * This method returns the list of {@link MTType} arguments used in this function application type.
     * </p>
     *
     * @return The list of {@link MTType} arguments.
     */
    public final List<MTType> getArguments() {
        return Collections.unmodifiableList(myArguments);
    }

    /**
     * <p>
     * This method returns a list of {@link MTType}s that are part of this type.
     * </p>
     *
     * @return The list of {@link MTType}s in this big union type.
     */
    @Override
    public final List<MTType> getComponentTypes() {
        return myComponents;
    }

    /**
     * <p>
     * This method returns the {@link MTFunction} that was used in this function application type.
     * </p>
     *
     * @return The {@link MTFunction} used.
     */
    public final MTFunction getFunction() {
        return myFunction;
    }

    /**
     * <p>
     * This method returns the name used to this describe this function application type.
     * </p>
     *
     * @return A string representing the name.
     */
    public final String getName() {
        return myName;
    }

    /**
     * <p>
     * Indicates that this type is known to contain only elements <em>that are themselves</em> types. Practically, this
     * answers the question, "can an instance of this type itself be used as a type?"
     * </p>
     *
     * @return {@code true} if it can, {@code false} otherwise.
     */
    @Override
    public boolean isKnownToContainOnlyMTypes() {
        // Note that, effectively, we represent an instance of the range of our
        // function. Thus, we're known to contain only MTypes if the function's
        // range's members are known only to contain MTypes.

        return myFunction.getRange().membersKnownToContainOnlyMTypes();
    }

    /**
     * <p>
     * Indicates that every instance of this type is itself known to contain only elements that are types. Practically,
     * this answers the question, "if a function returns an instance of this type, can that instance itself be said to
     * contain only types?"
     * </p>
     *
     * @return {@code true} if it can, {@code false} otherwise.
     */
    @Override
    public boolean membersKnownToContainOnlyMTypes() {
        boolean result = true;
        Iterator<MTType> arguments = myArguments.iterator();
        while (arguments.hasNext()) {
            result &= arguments.next().isKnownToContainOnlyMTypes();
        }
        return result && myFunction.applicationResultsKnownToContainOnlyRestrictions();
    }

    /**
     * <p>
     * This method returns the object in string format.
     * </p>
     *
     * @return Object as a string.
     */
    @Override
    public final String toString() {
        StringBuffer sb = new StringBuffer();
        if (myArguments.size() == 2) {
            sb.append(myArguments.get(0).toString());
            sb.append(" ");
            sb.append(myName);
            sb.append(" ");
            sb.append(myArguments.get(1).toString());
        } else if (myArguments.size() == 1 && myName.contains("_")) {
            // ^^^ super hacky way to detect outfix
            sb.append(myName.replace("_", myArguments.get(0).toString()));
        } else {
            sb.append(myName);
            sb.append("(");
            Iterator<MTType> arguments = myArguments.iterator();
            while (arguments.hasNext()) {
                MTType argument = arguments.next();
                if (argument != myArguments.get(0)) {
                    sb.append(", ");
                }
                sb.append(argument.toString());
            }
            sb.append(")");
        }
        return sb.toString();
    }

    /**
     * <p>
     * This method attempts to replace a component type at the specified index.
     * </p>
     *
     * @param index
     *            Index to a component type.
     * @param newType
     *            The {@link MTType} to replace the one in our component list.
     *
     * @return A new {@link MTFunctionApplication} with the type at the specified index replaced with {@code newType}.
     */
    @Override
    public MTType withComponentReplaced(int index, MTType newType) {
        MTFunction newFunction = myFunction;
        List<MTType> newArguments = myArguments;

        if (index == 0) {
            newFunction = (MTFunction) newType;
        } else {
            newArguments = new LinkedList<>(newArguments);
            newArguments.set(index - 1, newType);
        }

        return new MTFunctionApplication(getTypeGraph(), newFunction, myName, newArguments);
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * <p>
     * This is just a template method to <em>force</em> all concrete subclasses of {@link MTType} to implement
     * <code>hashCode()</code>, as the type resolution algorithm depends on it being implemented sensibly.
     * </p>
     *
     * @return A hashcode consistent with <code>equals()</code> and thus alpha-equivalency.
     */
    @Override
    protected final int getHashCode() {
        int result = BASE_HASH + myFunction.getHashCode() + myName.hashCode();

        for (MTType t : myArguments) {
            result *= 73;
            result += t.getHashCode();
        }

        return result;
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>
     * An helper method to create an immutable list using the function type and the arguments.
     * </p>
     */
    private void setUpComponents() {
        List<MTType> result = new LinkedList<>();

        result.add(myFunction);
        result.addAll(myArguments);

        myComponents = Collections.unmodifiableList(result);
    }
}

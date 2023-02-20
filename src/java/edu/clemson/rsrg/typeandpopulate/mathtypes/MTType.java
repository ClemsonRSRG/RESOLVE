/*
 * MTType.java
 * ---------------------------------
 * Copyright (c) 2023
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.rsrg.typeandpopulate.mathtypes;

import edu.clemson.rsrg.typeandpopulate.exception.BindingException;
import edu.clemson.rsrg.typeandpopulate.exception.NoSolutionException;
import edu.clemson.rsrg.typeandpopulate.exception.TypeMismatchException;
import edu.clemson.rsrg.typeandpopulate.symboltables.FinalizedScope;
import edu.clemson.rsrg.typeandpopulate.typereasoning.TypeGraph;
import edu.clemson.rsrg.typeandpopulate.typevisitor.*;
import java.util.*;

/**
 * <p>
 * This abstract class serves as the parent class of all mathematical types.
 * </p>
 *
 * @version 2.0
 */
public abstract class MTType {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * The current type graph object in use.
     * </p>
     */
    protected final TypeGraph myTypeGraph;

    /**
     * <p>
     * Known alpha equivalent types.
     * </p>
     */
    private final Set<Object> myKnownAlphaEquivalencies = new HashSet<>();

    /**
     * <p>
     * Known syntactic subtypes.
     * </p>
     */
    private final Map<MTType, Map<String, MTType>> myKnownSyntacticSubtypeBindings = new HashMap<>();

    /**
     * <p>
     * Allows us to detect if we're getting into an equals-loop.
     * </p>
     */
    private int myEqualsDepth = 0;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * An helper constructor that allow us to store the type graph for any objects created from a class that inherits
     * from {@code MTType}.
     * </p>
     *
     * @param g
     *            The current type graph.
     */
    protected MTType(TypeGraph g) {
        myTypeGraph = g;
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
    public abstract void accept(TypeVisitor v);

    /**
     * <p>
     * This method implements the post-visit method for invoking an instance of {@link TypeVisitor}.
     * </p>
     *
     * @param v
     *            A visitor for types.
     */
    public abstract void acceptClose(TypeVisitor v);

    /**
     * <p>
     * This method implements the pre-visit method for invoking an instance of {@link TypeVisitor}.
     * </p>
     *
     * @param v
     *            A visitor for types.
     */
    public abstract void acceptOpen(TypeVisitor v);

    /**
     * <p>
     * This method attempts to bind {@code o} to a map of types for the current context.
     * </p>
     *
     * @param o
     *            The type to bind.
     * @param context
     *            The finalized scope context.
     *
     * @return The modified context type map if bind is successful, otherwise it throws an exception.
     *
     * @throws BindingException
     *             Some error occurred during binding.
     */
    public final Map<String, MTType> bindTo(MTType o, FinalizedScope context) throws BindingException {
        BindingVisitor bind = new BindingVisitor(myTypeGraph, context);
        bind.visit(this, o);

        if (!bind.binds()) {
            throw new BindingException(this, o);
        }

        return bind.getBindings();
    }

    /**
     * <p>
     * This method attempts to bind {@code o} to a map of types for the current context.
     * </p>
     *
     * @param o
     *            The type to bind.
     * @param context
     *            The context map of types.
     *
     * @return The modified context type map if bind is successful, otherwise it throws an exception.
     *
     * @throws BindingException
     *             Some error occurred during binding.
     */
    public final Map<String, MTType> bindTo(MTType o, Map<String, MTType> context) throws BindingException {
        BindingVisitor bind = new BindingVisitor(myTypeGraph, context);
        bind.visit(this, o);

        if (!bind.binds()) {
            throw new BindingException(this, o);
        }

        return bind.getBindings();
    }

    /**
     * <p>
     * This method attempts to bind {@code template} to a map of types for the current context using a template context.
     * </p>
     *
     * @param template
     *            The template type to bind.
     * @param thisContext
     *            The current context map of types.
     * @param templateContext
     *            The template context map of types.
     *
     * @return The modified context type map if bind is successful, otherwise it throws an exception.
     *
     * @throws BindingException
     *             Some error occurred during binding.
     */
    public final Map<String, MTType> bindTo(MTType template, Map<String, MTType> thisContext,
            Map<String, MTType> templateContext) throws BindingException {
        BindingVisitor bind = new BindingVisitor(myTypeGraph, thisContext, templateContext);
        bind.visit(this, template);

        if (!bind.binds()) {
            throw new BindingException(this, template);
        }

        return bind.getBindings();
    }

    /**
     * <p>
     * Returns <code>true</code> <strong>iff</strong> <code>o</code> is an <code>MTType</code> that is <em>alpha
     * equivalent</em> to this type. I.e., it must be exactly the same with the sole exception that quantified variables
     * may have different names if they are otherwise identical. So, <code>BigUnion{t : MType}{t}</code>
     * <code>equals</code> <code>BigUnion{r : MType}{r}</code>. However, <code>BigUnion{t : MType}{t}</code> <em>does
     * not</em> <code>equals</code> <code>BigUnion{r : Power(MType)}{r}</code>.
     * </p>
     *
     * @param o
     *            The object to compare with this <code>MTType</code>.
     *
     * @return <code>true</code> <strong>iff</strong> this <code>MTType</code> is alpha equivalent to <code>o</code>.
     */
    @Override
    public final boolean equals(Object o) {
        myEqualsDepth++;

        boolean result;

        if (this == o) {
            result = true;
        } else {
            // We only check our cache if we're at the first level of equals
            // comparison to avoid an infinite recursive loop
            result = (myEqualsDepth == 1) && myKnownAlphaEquivalencies.contains(o);

            if (!result) {
                try {
                    // All 'equals' logic should be put into AlphaEquivalencyChecker!
                    // Don't override equals!
                    AlphaEquivalencyChecker alphaEq = myTypeGraph.threadResources.alphaChecker;
                    alphaEq.reset();

                    alphaEq.visit(this, (MTType) o);

                    result = alphaEq.getResult();
                } catch (ClassCastException cce) {
                    result = false;
                }

                // We only cache our answer at the first level to avoid an
                // infinite equals loop
                if ((myEqualsDepth == 1) && result) {
                    myKnownAlphaEquivalencies.add(o);
                }
            }
        }

        myEqualsDepth--;

        return result;
    }

    /**
     * <p>
     * This method returns a list of {@code MTType}s that are part of this type.
     * </p>
     *
     * @return A list of {@code MTType}s.
     */
    public abstract List<MTType> getComponentTypes();

    /**
     * <p>
     * This method returns a new {@link MTType} with the substitutions specified by the map.
     * </p>
     *
     * @param substitutions
     *            A map of substituting types.
     *
     * @return The modified {@link MTType}.
     */
    public final MTType getCopyWithVariablesSubstituted(Map<String, MTType> substitutions) {
        VariableReplacingVisitor renamer = new VariableReplacingVisitor(substitutions);
        accept(renamer);
        return renamer.getFinalExpression();
    }

    /**
     * <p>
     * This method returns a map fo syntactic subtype bindings for {@code o}.
     * </p>
     *
     * @param o
     *            A mathematical type.
     *
     * @return The collection of syntactic subtypes.
     */
    public final Map<String, MTType> getSyntacticSubtypeBindings(MTType o) throws NoSolutionException {
        Map<String, MTType> result;

        if (myKnownSyntacticSubtypeBindings.containsKey(o)) {
            result = myKnownSyntacticSubtypeBindings.get(o);
        } else {
            SyntacticSubtypeChecker checker = new SyntacticSubtypeChecker(myTypeGraph);

            try {
                checker.visit(this, o);
            } catch (RuntimeException e) {

                Throwable cause = e;
                while (cause != null && !(cause instanceof TypeMismatchException)) {
                    cause = cause.getCause();
                }

                if (cause == null) {
                    throw e;
                }

                throw new NoSolutionException("Error while attempting to establish syntactic subtype.",
                        new IllegalStateException());
            }

            result = Collections.unmodifiableMap(checker.getBindings());
            myKnownSyntacticSubtypeBindings.put(o, result);
        }

        return result;
    }

    /**
     * <p>
     * Returns the type stored inside this type.
     * </p>
     *
     * @return The {@link MTType} type object.
     */
    public MTType getType() {
        // TODO : Each MTType should really contain it's declared type. I.e.,
        // if I say "Definition X : Set", I should store that X is
        // of type Set someplace. That's not currently available, so for
        // the moment we say that all types are of type MType, the parent
        // type of all types.
        return myTypeGraph.CLS;
    }

    /**
     * <p>
     * The type graph containing all the type relationships.
     * </p>
     *
     * @return The type graph for the compiler.
     */
    public final TypeGraph getTypeGraph() {
        return myTypeGraph;
    }

    /**
     * <p>
     * This method overrides the default {@code hashCode} method implementation.
     * </p>
     *
     * @return The hash code associated with the object.
     */
    @Override
    public final int hashCode() {
        return getHashCode();
    }

    /**
     * <p>
     * Indicates that this type is {@link TypeGraph#BOOLEAN}.
     * </p>
     *
     * @return {@code true} if it is, {@code false} otherwise.
     */
    public final boolean isBoolean() {
        return (myTypeGraph.BOOLEAN == this);
    }

    /**
     * <p>
     * Indicates that this type is known to contain only elements <em>that are themselves</em> types. Practically, this
     * answers the question, "can an instance of this type itself be used as a type?"
     * </p>
     *
     * @return {@code true} if it can, {@code false} otherwise.
     */
    public boolean isKnownToContainOnlyMTypes() {
        return false;
    }

    /**
     * <p>
     * Indicates that this type is known to be a subtype of {@code o}.
     * </p>
     *
     * @param o
     *            An {@link MTType}.
     *
     * @return {@code true} if it is a subtype, {@code false} otherwise.
     */
    public final boolean isSubtypeOf(MTType o) {
        return myTypeGraph.isSubtype(this, o);
    }

    /**
     * <p>
     * Indicates that this type is known to be a syntactic subtype of {@code o}.
     * </p>
     *
     * @param o
     *            An {@link MTType}.
     *
     * @return {@code true} if it is a syntactic subtype, {@code false} otherwise.
     */
    public final boolean isSyntacticSubtypeOf(MTType o) {

        boolean result;

        try {
            getSyntacticSubtypeBindings(o);
            result = true;
        } catch (NoSolutionException e) {
            result = false;
        }

        return result;
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
    public boolean membersKnownToContainOnlyMTypes() {
        return false;
    }

    /**
     * <p>
     * This method attempts to replace a component type at the specified index.
     * </p>
     *
     * @param index
     *            Index to a component type.
     * @param newType
     *            The {@code MTType} to replace the one in our component list.
     *
     * @return A new {@code MTType} with the component type replaced.
     */
    public abstract MTType withComponentReplaced(int index, MTType newType);

    /**
     * <p>
     * This method attempts to replace a component type for all the entries in the map.
     * </p>
     *
     * @param newTypes
     *            A map of replace the one in our component list.
     *
     * @return A new {@code MTType} with the component type replaced.
     */
    public final MTType withComponentsReplaced(Map<Integer, MTType> newTypes) {
        MTType target = this;
        for (Map.Entry<Integer, MTType> entry : newTypes.entrySet()) {
            target = target.withComponentReplaced(entry.getKey(), entry.getValue());
        }

        return target;
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * <p>
     * This is just a template method to <em>force</em> all concrete subclasses of <code>MTType</code> to implement
     * <code>hashCode()</code>, as the type resolution algorithm depends on it being implemented sensibly.
     * </p>
     *
     * @return A hashcode consistent with <code>equals()</code> and thus alpha-equivalency.
     */
    protected abstract int getHashCode();

    // ===========================================================
    // Package Private Methods
    // ===========================================================

    /**
     * <p>
     * Returns the object-reference hash.
     * </p>
     *
     * @return A hashcode consistent with the object reference.
     */
    final int objectReferenceHashCode() {
        return super.hashCode();
    }

}

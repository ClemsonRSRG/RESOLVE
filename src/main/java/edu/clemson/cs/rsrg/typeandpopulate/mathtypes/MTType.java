/**
 * MTType.java
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
package edu.clemson.cs.rsrg.typeandpopulate.mathtypes;

import edu.clemson.cs.rsrg.typeandpopulate.exception.BindingException;
import edu.clemson.cs.rsrg.typeandpopulate.typereasoning.TypeGraph;
import edu.clemson.cs.rsrg.typeandpopulate.typevisitor.BindingVisitor;
import edu.clemson.cs.rsrg.typeandpopulate.typevisitor.TypeVisitor;
import edu.clemson.cs.rsrg.typeandpopulate.typevisitor.VariableReplacingVisitor;
import java.util.List;
import java.util.Map;

/**
 * <p>This abstract class serves as the parent class of all
 * mathematical types.</p>
 *
 * @version 2.0
 */
public abstract class MTType {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The current type graph object in use.</p> */
    protected final TypeGraph myTypeGraph;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>An helper constructor that allow us to store the type graph
     * for any objects created from a class that inherits from
     * {@code MTType}.</p>
     *
     * @param g The current type graph.
     */
    protected MTType(TypeGraph g) {
        myTypeGraph = g;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This method is the {@code accept()} method in a visitor pattern
     * for invoking an instance of {@link TypeVisitor}.</p>
     *
     * @param v A visitor for types.
     */
    public abstract void accept(TypeVisitor v);

    /**
     * <p>This method implements the post-visit method
     * for invoking an instance of {@link TypeVisitor}.</p>
     *
     * @param v A visitor for types.
     */
    public abstract void acceptClose(TypeVisitor v);

    /**
     * <p>This method implements the pre-visit method
     * for invoking an instance of {@link TypeVisitor}.</p>
     *
     * @param v A visitor for types.
     */
    public abstract void acceptOpen(TypeVisitor v);

    /**
     * <p>This method attempts to bind {@code o} to a map of types for the current
     * context.</p>
     *
     * @param o The type to bind.
     * @param context The context map of types.
     *
     * @return The modified context type map if bind is successful, otherwise it throws
     * an exception.
     *
     * @throws BindingException
     */
    public final Map<String, MTType> bindTo(MTType o,
            Map<String, MTType> context) throws BindingException {
        BindingVisitor bind = new BindingVisitor(myTypeGraph, context);
        bind.visit(this, o);

        if (!bind.binds()) {
            throw new BindingException(this, o);
        }

        return bind.getBindings();
    }

    /**
     * <p>This method attempts to bind {@code template} to a map of types for the current
     * context using a template context.</p>
     *
     * @param template The template type to bind.
     * @param thisContext The current context map of types.
     * @param templateContext The template context map of types.
     *
     * @return The modified context type map if bind is successful, otherwise it throws
     * an exception.
     *
     * @throws BindingException
     */
    public final Map<String, MTType> bindTo(MTType template,
            Map<String, MTType> thisContext, Map<String, MTType> templateContext)
            throws BindingException {
        BindingVisitor bind =
                new BindingVisitor(myTypeGraph, thisContext, templateContext);
        bind.visit(this, template);

        if (!bind.binds()) {
            throw new BindingException(this, template);
        }

        return bind.getBindings();
    }

    /**
     * <p>This method returns a list of {@code MTType}s
     * that are part of this type.</p>
     *
     * @return A list of {@code MTType}s.
     */
    public abstract List<MTType> getComponentTypes();

    /**
     * <p>Returns the type stored inside this type.</p>
     *
     * @return The {@link MTType} type object.
     */
    public MTType getType() {
        //TODO : Each MTType should really contain it's declared type.  I.e.,
        //       if I say "Definition X : Set", I should store that X is
        //       of type Set someplace.  That's not currently available, so for
        //       the moment we say that all types are of type MType, the parent
        //       type of all types.
        return myTypeGraph.CLS;
    }

    /**
     * <p>The type graph containing all the type relationships.</p>
     *
     * @return The type graph for the compiler.
     */
    public final TypeGraph getTypeGraph() {
        return myTypeGraph;
    }

    public final MTType getCopyWithVariablesSubstituted(
            Map<String, MTType> substitutions) {
        VariableReplacingVisitor renamer =
                new VariableReplacingVisitor(substitutions);
        accept(renamer);
        return renamer.getFinalExpression();
    }

    /**
     * <p>This method overrides the default {@code hashCode} method implementation.</p>
     *
     * @return The hash code associated with the object.
     */
    @Override
    public final int hashCode() {
        return getHashCode();
    }

    /**
     * <p>Indicates that this type is known to contain only elements <em>that
     * are themselves</em> types. Practically, this answers the question, "can
     * an instance of this type itself be used as a type?"</p>
     *
     * @return {@code true} if it can, {@code false} otherwise.
     */
    public boolean isKnownToContainOnlyMTypes() {
        return false;
    }

    /**
     * <p>Indicates that every instance of this type is itself known to contain
     * only elements that are types.  Practically, this answers the question,
     * "if a function returns an instance of this type, can that instance itself
     * be said to contain only types?"</p>
     *
     * @return {@code true} if it can, {@code false} otherwise.
     */
    public boolean membersKnownToContainOnlyMTypes() {
        return false;
    }

    /**
     * <p>This method attempts to replace a component type at the specified
     * index.</p>
     *
     * @param index Index to a component type.
     * @param newType The {@code MTType} to replace the one in our component list.
     *
     * @return A new {@code MTType} with the component type replaced.
     */
    public abstract MTType withComponentReplaced(int index, MTType newType);

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * <p>This is just a template method to <em>force</em> all concrete
     * subclasses of <code>MTType</code> to implement <code>hashCode()</code>,
     * as the type resolution algorithm depends on it being implemented
     * sensibly.</p>
     *
     * @return A hashcode consistent with <code>equals()</code> and thus
     * alpha-equivalency.
     */
    protected abstract int getHashCode();

    // ===========================================================
    // Package Private Methods
    // ===========================================================

    /**
     * <p>Returns the object-reference hash.</p>
     *
     * @return A hashcode consistent with the object reference.
     */
    final int objectReferenceHashCode() {
        return super.hashCode();
    }

}
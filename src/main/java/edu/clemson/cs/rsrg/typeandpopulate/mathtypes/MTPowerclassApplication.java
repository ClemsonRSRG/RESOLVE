/**
 * MTPowerclassApplication.java
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

import edu.clemson.cs.rsrg.typeandpopulate.typereasoning.TypeGraph;
import edu.clemson.cs.rsrg.typeandpopulate.typevisitor.TypeVisitor;

/**
 * An application of the {@code Powerclass} function.
 *
 * @version 2.0
 */
public class MTPowerclassApplication extends MTFunctionApplication {

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs an application of {@code Powerclass} function.</p>
     *
     * @param g The current type graph.
     * @param argument The {@link MTType} to apply the {@code Powerclass} function.
     */
    public MTPowerclassApplication(TypeGraph g, MTType argument) {
        super(g, g.POWERCLASS, "Powerclass", argument);
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
    @Override
    public final void accept(TypeVisitor v) {
        v.beginMTType(this);
        v.beginMTAbstract(this);
        v.beginMTFunctionApplication(this);
        v.beginMTPowerclassApplication(this);

        v.beginChildren(this);

        getFunction().accept(v);

        for (MTType arg : getArguments()) {
            arg.accept(v);
        }

        v.endChildren(this);

        v.endMTPowerclassApplication(this);
        v.endMTFunctionApplication(this);
        v.endMTAbstract(this);
        v.endMTType(this);
    }

    /**
     * <p>Indicates that this type is known to contain only elements <em>that
     * are themselves</em> types. Practically, this answers the question, "can
     * an instance of this type itself be used as a type?"</p>
     *
     * @return {@code true} if it can, {@code false} otherwise.
     */
    @Override
    public final boolean isKnownToContainOnlyMTypes() {
        //The powerclass is, by definition, a container of containers
        return true;
    }

    /**
     * <p>Indicates that every instance of this type is itself known to contain
     * only elements that are types. Practically, this answers the question,
     * "if a function returns an instance of this type, can that instance itself
     * be said to contain only types?"</p>
     *
     * @return {@code true} if it can, {@code false} otherwise.
     */
    @Override
    public final boolean membersKnownToContainOnlyMTypes() {
        //I'm the container of all sub-containers of my argument.  My members
        //are containers of members from the original argument.
        return getArgument(0).isKnownToContainOnlyMTypes();
    }

    /**
     * <p>This method attempts to replace a component type at the specified
     * index.</p>
     *
     * @param index Index to a component type.
     * @param newType The {@link MTType} to replace the one in our component list.
     *
     * @return A new {@link MTFunctionApplication} with the type at the specified index
     * replaced with {@code newType}.
     */
    @Override
    public final MTType withComponentReplaced(int index, MTType newType) {
        MTType result;

        switch (index) {
        case 0:
            result =
                    new MTFunctionApplication(getTypeGraph(),
                            (MTFunction) newType, getArguments());
            break;
        case 1:
            result = new MTPowerclassApplication(getTypeGraph(), newType);
            break;
        default:
            throw new IndexOutOfBoundsException("" + index);
        }

        return result;
    }
}
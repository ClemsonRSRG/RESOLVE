/*
 * BoundVariableVisitor.java
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
package edu.clemson.cs.rsrg.typeandpopulate.typevisitor;

import edu.clemson.cs.rsrg.typeandpopulate.mathtypes.MTBigUnion;
import edu.clemson.cs.rsrg.typeandpopulate.mathtypes.MTType;
import java.util.*;

/**
 * <p>
 * This is the abstract base class for typing bounded variables.
 * </p>
 *
 * @version 2.0
 */
abstract class BoundVariableVisitor extends TypeVisitor {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * A container to store all the information about the bounded variables.
     * </p>
     */
    private final Deque<Map<String, BindingInfo>> myBoundVariables =
            new LinkedList<>();

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * This method adds additional logic before we visit a {@link MTBigUnion} by
     * adding the binding
     * information for all the quantified variables.
     * </p>
     *
     * @param u A math type.
     */
    @Override
    public final void beginMTBigUnion(MTBigUnion u) {
        myBoundVariables.push(toBindingInfoMap(u.getQuantifiedVariables()));
        boundBeginMTBigUnion(u);
    }

    /**
     * <p>
     * This method adds additional logic after we visit a {@link MTBigUnion} by
     * removing all the
     * binding information.
     * </p>
     *
     * @param u A math type.
     */
    @Override
    public final void endMTBigUnion(MTBigUnion u) {
        boundEndMTBigUnion(u);
        myBoundVariables.pop();
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * <p>
     * This method annotates the inner most binding for the given variable name.
     * </p>
     *
     * @param name A variable name.
     * @param key The binding key.
     * @param value The binding value.
     */
    protected void annotateInnermostBinding(String name, Object key,
            Object value) {
        getInnermostBindingInfo(name).annotations.put(key, value);
    }

    /**
     * <p>
     * This method adds additional logic to bound {@code u} before we visit it.
     * </p>
     *
     * @param u A math type.
     */
    protected void boundBeginMTBigUnion(MTBigUnion u) {}

    /**
     * <p>
     * This method adds additional logic to bound {@code u} after we visit it.
     * </p>
     *
     * @param u A math type.
     */
    protected void boundEndMTBigUnion(MTBigUnion u) {}

    /**
     * <p>
     * This method returns the mathematical type used to bind the given variable
     * name.
     * </p>
     *
     * @param name A variable name.
     *
     * @return The {@link MTType} type used for binding.
     */
    protected final MTType getInnermostBinding(String name) {
        return getInnermostBindingInfo(name).type;
    }

    /**
     * <p>
     * This method returns the inner most binding object for the given variable
     * name and key.
     * </p>
     *
     * @param name A variable name.
     * @param key The binding key.
     *
     * @return The object bound by the key for this variable.
     */
    protected Object getInnermostBindingAnnotation(String name, Object key) {
        return getInnermostBindingInfo(name).annotations.get(key);
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>
     * This method returns information about the inner most binding for the
     * given variable name.
     * </p>
     *
     * @param name A variable name.
     *
     * @return The {@link BindingInfo} representation object.
     */
    private BindingInfo getInnermostBindingInfo(String name) {
        BindingInfo binding = null;
        Iterator<Map<String, BindingInfo>> scopes = myBoundVariables.iterator();
        while (binding == null && scopes.hasNext()) {
            binding = scopes.next().get(name);
        }

        if (binding == null) {
            throw new NoSuchElementException();
        }

        return binding;
    }

    /**
     * <p>
     * This method creates a map of {@link BindingInfo} for each of the
     * variables.
     * </p>
     *
     * @param vars A map from variable names to {@link MTType}.
     *
     * @return The corresponding map of {@link BindingInfo}.
     */
    private Map<String, BindingInfo>
            toBindingInfoMap(Map<String, MTType> vars) {
        Map<String, BindingInfo> result = new HashMap<>();

        for (Map.Entry<String, MTType> entry : vars.entrySet()) {
            result.put(entry.getKey(), new BindingInfo(entry.getValue()));
        }

        return result;
    }

    // ===========================================================
    // Helper Constructs
    // ===========================================================

    /**
     * <p>
     * An helper class that allow us to store all binding annotations for a
     * mathematical type.
     * </p>
     */
    private class BindingInfo {

        // ===========================================================
        // Member Fields
        // ===========================================================

        /**
         * <p>
         * A mathematical type.
         * </p>
         */
        final MTType type;

        /**
         * <p>
         * A map containing all the annotations for this type.
         * </p>
         */
        final Map<Object, Object> annotations;

        // ===========================================================
        // Constructors
        // ===========================================================

        /**
         * <p>
         * This constructs an object that is used to store all binding
         * information for this type.
         * </p>
         *
         * @param type A mathematical type.
         */
        BindingInfo(MTType type) {
            this.type = type;
            this.annotations = new HashMap<>();
        }
    }

}

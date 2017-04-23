/*
 * VerificationCondition.java
 * ---------------------------------
 * Copyright (c) 2017
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.vcgeneration.utilities;

import edu.clemson.cs.rsrg.parsing.data.BasicCapabilities;
import edu.clemson.cs.rsrg.vcgeneration.sequents.Sequent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>This class represents a possibly-named {@code verification condition} ({@code VC})
 * with a list of associated {@link Sequent Sequents}.</p>
 *
 * <p>When proving a {@link VerificationCondition}, only one of the associated {@link Sequent}
 * needs to be {@code true}. This is because the associated {@link Sequent Sequents} share
 * one or more {@code consequents}. This means that proving one of the {@link Sequent} is
 * good enough for us.</p>
 *
 * @author Yu-Shan Sun
 * @version 1.0
 */
public class VerificationCondition implements BasicCapabilities, Cloneable {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>Name given to the {@code VC}.</p> */
    private final String myName;

    /**
     * <p>List of {@link Sequent Sequents} associated with
     * this {@code VC}.</p>
     */
    private final List<Sequent> myAssociatedSequents;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This creates a {@code VC} with a name and associated
     * {@link Sequent Sequents}.</p>
     *
     * @param name Name given to this {@code VC}.
     * @param sequents List of {@link Sequent Sequents}
     *                 associated with this {@code VC}.
     */
    public VerificationCondition(String name, List<Sequent> sequents) {
        myName = name;
        myAssociatedSequents = sequents;
    }

    /**
     * <p>This creates a nameless {@code VC} with the associated
     * {@link Sequent Sequents}.</p>
     *
     * @param sequents List of {@link Sequent Sequents}
     *                 associated with this {@code VC}.
     */
    public VerificationCondition(List<Sequent> sequents) {
        this(null, sequents);
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This method creates a special indented
     * text version of the instantiated object.</p>
     *
     * @param indentSize The base indentation to the first line
     *                   of the text.
     * @param innerIndentInc The additional indentation increment
     *                       for the subsequent lines.
     *
     * @return A formatted text string of the class.
     */
    @Override
    public final String asString(int indentSize, int innerIndentInc) {
        StringBuffer sb = new StringBuffer();

        for (Sequent sequent : myAssociatedSequents) {
            sb.append(sequent.asString(indentSize + innerIndentInc,
                    innerIndentInc));
            sb.append("\n");
        }

        return sb.toString();
    }

    /**
     * <p>This method overrides the default {@code clone} method implementation.</p>
     *
     * @return A deep copy of the object.
     */
    @Override
    public final VerificationCondition clone() {
        VerificationCondition newVerificationCondition =
                new VerificationCondition(myName, new ArrayList<Sequent>());

        Collections.copy(newVerificationCondition.myAssociatedSequents,
                myAssociatedSequents);

        return newVerificationCondition;
    }

    /**
     * <p>This method overrides the default {@code equals} method implementation.</p>
     *
     * @param o Object to be compared.
     *
     * @return {@code true} if all the fields are equal, {@code false} otherwise.
     */
    @Override
    public final boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        VerificationCondition that = (VerificationCondition) o;

        return (myName != null ? myName.equals(that.myName)
                : that.myName == null)
                && myAssociatedSequents.equals(that.myAssociatedSequents);
    }

    /**
     * <p>This method returns the name for this {@code VC}.</p>
     *
     * @return A string.
     */
    public final String getName() {
        return myName;
    }

    /**
     * <p>This method returns the list of {@code sequents} stored inside
     * this {@code VC}.</p>
     *
     * @return A list of {@link Sequent Sequents}.
     */
    public final List<Sequent> getAssociatedSequents() {
        return myAssociatedSequents;
    }

    /**
     * <p>This method overrides the default {@code hashCode} method implementation.</p>
     *
     * @return The hash code associated with the object.
     */
    @Override
    public final int hashCode() {
        int result = myName != null ? myName.hashCode() : 0;
        result = 31 * result + myAssociatedSequents.hashCode();
        return result;
    }

    /**
     * <p>This method returns the object in string format.</p>
     *
     * @return Object as a string.
     */
    @Override
    public final String toString() {
        return asString(0, 4);
    }

}
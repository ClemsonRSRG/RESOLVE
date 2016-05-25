/**
 * ProgramStringExp.java
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
package edu.clemson.cs.rsrg.absyn.programexpr;

import edu.clemson.cs.rsrg.absyn.Exp;
import edu.clemson.cs.rsrg.parsing.data.Location;
import java.util.Map;

/**
 * <p>This is the class for all the programming string expression objects
 * that the compiler builds using the ANTLR4 AST nodes.</p>
 *
 * @version 2.0
 */
public class ProgramStringExp extends ProgramLiteralExp {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The inner representation for this programming string</p>*/
    private final String myString;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs a programming string expression.</p>
     *
     * @param l A {@link Location} representation object.
     * @param s A {@link String} expression.
     */
    public ProgramStringExp(Location l, String s) {
        super(l);
        myString = s;
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * {@inheritDoc}
     */
    @Override
    public final String asString(int indentSize, int innerIndentInc) {
        StringBuffer sb = new StringBuffer();
        printSpace(indentSize, sb);

        printSpace(indentSize + innerIndentInc, sb);
        sb.append(myString);
        sb.append("\n");

        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        ProgramStringExp that = (ProgramStringExp) o;

        return myString.equals(that.myString);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean equivalent(Exp e) {
        boolean retval = (e instanceof ProgramStringExp);
        if (retval) {
            ProgramStringExp eAsProgramStringExp = (ProgramStringExp) e;
            retval = myString.equals(eAsProgramStringExp.myString);
        }

        return retval;
    }

    /**
     * <p>This method returns the string value.</p>
     *
     * @return The {@link String} value.
     */
    public final String getValue() {
        return myString;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        return myString.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final String toString() {
        StringBuffer sb = new StringBuffer();
        if (myString != null) {
            sb.append(myString);
        }

        return sb.toString();
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * {@inheritDoc}
     */
    @Override
    protected final Exp copy() {
        return new ProgramStringExp(new Location(myLoc), myString);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final Exp substituteChildren(Map<Exp, Exp> substitutions) {
        return new ProgramStringExp(new Location(myLoc), myString);
    }

}
/**
 * CharExp.java
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
package edu.clemson.cs.rsrg.absyn.expressions.mathexpr;

import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.parsing.data.Location;
import java.util.Map;

/**
 * <p>This is the class for all the mathematical character expression objects
 * that the compiler builds using the ANTLR4 AST nodes.</p>
 *
 * @version 2.0
 */
public class CharExp extends LiteralExp {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The character representing this mathematical character</p> */
    private final Character myCharacter;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs a mathematical character expression.</p>
     *
     * @param l A {@link Location} representation object.
     * @param c A {@link Character} expression.
     */
    public CharExp(Location l, Character c) {
        super(l);
        myCharacter = c;
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
        sb.append(myCharacter.toString());

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
        if (!super.equals(o))
            return false;

        CharExp charExp = (CharExp) o;

        return myCharacter.equals(charExp.myCharacter);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean equivalent(Exp e) {
        boolean retval = (e instanceof CharExp);
        if (retval) {
            CharExp eAsCharExp = (CharExp) e;
            retval = myCharacter.equals(eAsCharExp.myCharacter);
        }

        return retval;
    }

    /**
     * <p>This method returns the character value.</p>
     *
     * @return The {@link Character} value.
     */
    public final Character getValue() {
        return myCharacter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        int result = super.hashCode();
        result = 31 * result + myCharacter.hashCode();
        return result;
    }

    /**
     * <p>This method applies VC Generator's remember rule.
     * For all inherited programming expression classes, this method
     * should throw an exception.</p>
     *
     * @return The resulting {@link CharExp} from applying the remember rule.
     */
    @Override
    public final CharExp remember() {
        return (CharExp) this.clone();
    }

    /**
     * <p>This method applies the VC Generator's simplification step.</p>
     *
     * @return The resulting {@link MathExp} from applying the simplification step.
     */
    @Override
    public final MathExp simplify() {
        return this.clone();
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * {@inheritDoc}
     */
    @Override
    protected final Exp copy() {
        return new CharExp(cloneLocation(), myCharacter.charValue());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final Exp substituteChildren(Map<Exp, Exp> substitutions) {
        return new CharExp(cloneLocation(), myCharacter.charValue());
    }

}
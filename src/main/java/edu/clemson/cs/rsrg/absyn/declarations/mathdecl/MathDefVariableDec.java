/**
 * MathDefVariableDec.java
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
package edu.clemson.cs.rsrg.absyn.declarations.mathdecl;

import edu.clemson.cs.rsrg.absyn.declarations.Dec;
import edu.clemson.cs.rsrg.absyn.declarations.variabledecl.MathVarDec;
import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.absyn.items.mathitems.DefinitionBodyItem;

/**
 * <p>This is the class for all the mathematical definition variable declarations
 * that the compiler builds using the ANTLR4 AST nodes.</p>
 *
 * @version 2.0
 */
public class MathDefVariableDec extends Dec {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The math variable declaration</p> */
    private final MathVarDec myVariable;

    /** <p>The definition body.</p> */
    private final DefinitionBodyItem myBodyItem;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs a mathematical definition variable.</p>
     *
     * @param variable The mathematical variable we are defining.
     * @param bodyItem The definition body (if any).
     */
    public MathDefVariableDec(MathVarDec variable, DefinitionBodyItem bodyItem) {
        super(variable.getLocation(), variable.getName());
        myVariable = variable;
        myBodyItem = bodyItem;
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
        sb.append("Def Var ");
        sb.append(myName.asString(0, innerIndentInc));

        // any definition body
        if (myBodyItem != null) {
            sb.append(myBodyItem.asString(indentSize + innerIndentInc,
                    innerIndentInc));
        }
        sb.append(";");

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

        MathDefVariableDec that = (MathDefVariableDec) o;

        if (!myVariable.equals(that.myVariable))
            return false;
        return myBodyItem != null ? myBodyItem.equals(that.myBodyItem)
                : that.myBodyItem == null;
    }

    /**
     * <p>This method returns the definition expression of a standard definition.</p>
     *
     * @return An {@code Exp} representing the definition expression or {@code null}.
     */
    public final Exp getDefinition() {
        return myBodyItem == null ? null : myBodyItem.getDefinition();
    }

    /**
     * <p>This method returns the math variable associated with this
     * definition declaration.</p>
     *
     * @return A {@link MathVarDec} representation object.
     */
    public final MathVarDec getVariable() {
        return myVariable;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        int result = super.hashCode();
        result = 31 * result + myVariable.hashCode();
        result = 31 * result + (myBodyItem != null ? myBodyItem.hashCode() : 0);
        return result;
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * {@inheritDoc}
     */
    @Override
    protected final MathDefVariableDec copy() {
        DefinitionBodyItem newItem = null;
        if (myBodyItem != null) {
            newItem = myBodyItem.clone();
        }

        return new MathDefVariableDec((MathVarDec) myVariable.clone(), newItem);
    }

}
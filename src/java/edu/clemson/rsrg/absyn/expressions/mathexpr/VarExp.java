/*
 * VarExp.java
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
package edu.clemson.rsrg.absyn.expressions.mathexpr;

import edu.clemson.rsrg.absyn.expressions.Exp;
import edu.clemson.rsrg.parsing.data.Location;
import edu.clemson.rsrg.parsing.data.PosSymbol;
import edu.clemson.rsrg.typeandpopulate.entry.SymbolTableEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * This is the class for all the mathematical variable expression objects that the compiler builds using the ANTLR4 AST
 * nodes.
 * </p>
 *
 * @version 2.0
 */
public class VarExp extends MathExp {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>
     * The expression's qualifier
     * </p>
     */
    private PosSymbol myQualifier;

    /**
     * <p>
     * The expression's name
     * </p>
     */
    private final PosSymbol myName;

    /**
     * <p>
     * A boolean that indicates whether or not this {@code VarExp} is referencing a definition name from a
     * {@code Precis}.
     * </p>
     */
    private boolean myIsPrecisDefinitionName;

    /**
     * <p>
     * The object's quantification (if any).
     * </p>
     */
    private SymbolTableEntry.Quantification myQuantification;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>
     * This constructs a variable expression with "None" as the default quantification.
     * </p>
     *
     * @param l
     *            A {@link Location} representation object.
     * @param qualifier
     *            A {@link PosSymbol} qualifier object.
     * @param name
     *            A {@link PosSymbol} name object.
     */
    public VarExp(Location l, PosSymbol qualifier, PosSymbol name) {
        this(l, qualifier, name, SymbolTableEntry.Quantification.NONE, false);
    }

    /**
     * <p>
     * This constructs a variable expression with the passed in quantification.
     * </p>
     *
     * @param l
     *            A {@link Location} representation object.
     * @param qualifier
     *            A {@link PosSymbol} qualifier object.
     * @param name
     *            A {@link PosSymbol} name object.
     * @param quantifier
     *            A {@link SymbolTableEntry.Quantification} quantifier object.
     */
    public VarExp(Location l, PosSymbol qualifier, PosSymbol name, SymbolTableEntry.Quantification quantifier) {
        this(l, qualifier, name, quantifier, false);
    }

    /**
     * <p>
     * This constructs a variable expression with the passed in quantification.
     * </p>
     *
     * @param l
     *            A {@link Location} representation object.
     * @param qualifier
     *            A {@link PosSymbol} qualifier object.
     * @param name
     *            A {@link PosSymbol} name object.
     * @param quantifier
     *            A {@link SymbolTableEntry.Quantification} quantifier object.
     * @param isPrecisDefinitionName
     *            A boolean that indicates whether or not this {@code VarExp} is referencing a definition name from a
     *            {@code Precis}.
     */
    private VarExp(Location l, PosSymbol qualifier, PosSymbol name, SymbolTableEntry.Quantification quantifier,
            boolean isPrecisDefinitionName) {
        super(l);
        myQualifier = qualifier;
        myName = name;
        myQuantification = quantifier;
        myIsPrecisDefinitionName = isPrecisDefinitionName;
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

        if (myQuantification != SymbolTableEntry.Quantification.NONE) {
            sb.append("[");
            sb.append(myQuantification);
            sb.append("] ");
        }

        if (myQualifier != null) {
            sb.append(myQualifier.asString(0, innerIndentInc));
            sb.append("::");
        }

        sb.append(myName.asString(0, innerIndentInc));

        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean containsExp(Exp exp) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean containsVar(String varName, boolean IsOldExp) {
        boolean retval = false;
        if (!IsOldExp && myName.getName().equals(varName)) {
            retval = true;
        }

        return retval;
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

        VarExp varExp = (VarExp) o;

        if (myIsPrecisDefinitionName != varExp.myIsPrecisDefinitionName)
            return false;
        if (myQualifier != null ? !myQualifier.equals(varExp.myQualifier) : varExp.myQualifier != null)
            return false;
        if (!myName.equals(varExp.myName))
            return false;
        return myQuantification == varExp.myQuantification;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean equivalent(Exp e) {
        boolean retval = false;
        if (e instanceof VarExp) {
            VarExp eAsVarExp = (VarExp) e;
            retval = (posSymbolEquivalent(myQualifier, eAsVarExp.myQualifier)
                    && (posSymbolEquivalent(myName, eAsVarExp.myName)));
        }

        return retval;
    }

    /**
     * <p>
     * This method returns the name.
     * </p>
     *
     * @return The {@link PosSymbol} representation object.
     */
    public final PosSymbol getName() {
        return myName;
    }

    /**
     * <p>
     * This method returns the qualifier name.
     * </p>
     *
     * @return The {@link PosSymbol} representation object.
     */
    public final PosSymbol getQualifier() {
        return myQualifier;
    }

    /**
     * <p>
     * This method returns this variable expression's quantification.
     * </p>
     *
     * @return The {@link SymbolTableEntry.Quantification} object.
     */
    public final SymbolTableEntry.Quantification getQuantification() {
        return myQuantification;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final List<Exp> getSubExpressions() {
        return new ArrayList<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (myQualifier != null ? myQualifier.hashCode() : 0);
        result = 31 * result + myName.hashCode();
        result = 31 * result + (myIsPrecisDefinitionName ? 1 : 0);
        result = 31 * result + myQuantification.hashCode();
        return result;
    }

    /**
     * <p>
     * This method checks to see if this variable expression refers to a definition from a {@code Precis}.
     * </p>
     *
     * @return {@code true} if it is a {@code Precis} definition name, {@code false} otherwise.
     */
    public final boolean isIsPrecisDefinitionName() {
        return myIsPrecisDefinitionName;
    }

    /**
     * <p>
     * Sets the flag for whether or not this refers to a definition name.
     * </p>
     */
    public final void setIsPrecisDefinitionName() {
        myIsPrecisDefinitionName = true;
    }

    /**
     * <p>
     * Sets the qualifier for this expression.
     * </p>
     *
     * @param qualifier
     *            The qualifier for this expression.
     */
    public final void setQualifier(PosSymbol qualifier) {
        myQualifier = qualifier;
    }

    /**
     * <p>
     * Sets the quantification for this expression.
     * </p>
     *
     * @param q
     *            The quantification type for this expression.
     */
    public final void setQuantification(SymbolTableEntry.Quantification q) {
        myQuantification = q;
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * {@inheritDoc}
     */
    @Override
    protected final Exp copy() {
        PosSymbol newQualifier = null;
        if (myQualifier != null) {
            newQualifier = myQualifier.clone();
        }
        PosSymbol newName = myName.clone();

        return new VarExp(cloneLocation(), newQualifier, newName, myQuantification, myIsPrecisDefinitionName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final Exp substituteChildren(Map<Exp, Exp> substitutions) {
        PosSymbol newQualifier = null;
        if (myQualifier != null) {
            newQualifier = myQualifier.clone();
        }
        PosSymbol newName = myName.clone();

        return new VarExp(cloneLocation(), newQualifier, newName, myQuantification, myIsPrecisDefinitionName);
    }

}

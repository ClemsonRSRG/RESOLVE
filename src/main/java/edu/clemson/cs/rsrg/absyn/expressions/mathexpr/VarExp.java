/**
 * VarExp.java
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

import edu.clemson.cs.r2jt.typeandpopulate2.entry.SymbolTableEntry;
import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>This is the class for all the mathematical variable expression objects
 * that the compiler builds using the ANTLR4 AST nodes.</p>
 *
 * @version 2.0
 */
public class VarExp extends MathExp {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The expression's qualifier</p> */
    private PosSymbol myQualifier;

    /** <p>The expression's name</p> */
    private final PosSymbol myName;

    /** <p>The object's quantification (if any).</p> */
    private final SymbolTableEntry.Quantification myQuantification;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs a variable expression with "None"
     * as the default quantification.</p>
     *
     * @param l A {@link Location} representation object.
     * @param qualifier A {@link PosSymbol} qualifier object.
     * @param name A {@link PosSymbol} name object.
     */
    public VarExp(Location l, PosSymbol qualifier, PosSymbol name) {
        this(l, qualifier, name, SymbolTableEntry.Quantification.NONE);
    }

    /**
     * <p>This constructs a variable expression with the
     * passed in quantification.</p>
     *
     * @param l A {@link Location} representation object.
     * @param qualifier A {@link PosSymbol} qualifier object.
     * @param name A {@link PosSymbol} name object.
     * @param quantifier A {@link SymbolTableEntry.Quantification} quantifier object.
     */
    public VarExp(Location l, PosSymbol qualifier, PosSymbol name,
            SymbolTableEntry.Quantification quantifier) {
        super(l);
        myQualifier = qualifier;
        myName = name;
        myQuantification = quantifier;
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
            sb.append(myQuantification);
        }

        if (myQualifier != null) {
            sb.append(myQualifier.asString(indentSize + innerIndentInc,
                    innerIndentInc));
            sb.append("::");
        }

        if (myName != null) {
            sb.append(myName.asString(indentSize + innerIndentInc,
                    innerIndentInc));
        }

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
        if (myName != null) {
            if (!IsOldExp && myName.equals(varName)) {
                retval = true;
            }
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

        if (myQualifier != null ? !myQualifier.equals(varExp.myQualifier)
                : varExp.myQualifier != null)
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
            retval =
                    (posSymbolEquivalent(myQualifier, eAsVarExp.myQualifier) && (posSymbolEquivalent(
                            myName, eAsVarExp.myName)));
        }

        return retval;
    }

    /**
     * <p>This method returns the name.</p>
     *
     * @return The {@link PosSymbol} representation object.
     */
    public final PosSymbol getName() {
        return myName;
    }

    /**
     * <p>This method returns the qualifier name.</p>
     *
     * @return The {@link PosSymbol} representation object.
     */
    public final PosSymbol getQualifier() {
        return myQualifier;
    }

    /**
     * <p>This method returns this variable expression's quantification.</p>
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
        result =
                31 * result
                        + (myQualifier != null ? myQualifier.hashCode() : 0);
        result = 31 * result + myName.hashCode();
        result = 31 * result + myQuantification.hashCode();
        return result;
    }

    /**
     * <p>This method applies VC Generator's remember rule.
     * For all inherited programming expression classes, this method
     * should throw an exception.</p>
     *
     * @return The resulting {@link VarExp} from applying the remember rule.
     */
    @Override
    public final VarExp remember() {
        return (VarExp) this.clone();
    }

    /**
     * <p>Sets the qualifier for this expression.</p>
     *
     * @param qualifier The qualifier for this expression.
     */
    public final void setQualifier(PosSymbol qualifier) {
        myQualifier = qualifier;
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

    /**
     * {@inheritDoc}
     */
    @Override
    public final String toString() {
        StringBuffer sb = new StringBuffer();

        if (myQuantification != SymbolTableEntry.Quantification.NONE) {
            sb.append(myQuantification);
        }

        if (myQualifier != null) {
            sb.append(myQualifier.toString());
            sb.append("::");
        }

        if (myName != null) {
            String strName = myName.toString();
            int index = 0;
            int num = 0;
            while ((strName.charAt(index)) == '?') {
                num++;
                index++;
            }
            if (strName.substring(num).startsWith("Conc_")) {
                strName = strName.replace("Conc_", "Conc.");
            }
            sb.append(strName.substring(index, strName.length()));
            for (int i = 0; i < num; i++) {
                sb.append("'");
            }
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
        PosSymbol newQualifier = null;
        if (myQualifier != null) {
            newQualifier = myQualifier.clone();
        }
        PosSymbol newName = myName.clone();

        return new VarExp(new Location(myLoc), newQualifier, newName,
                myQuantification);
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

        return new VarExp(new Location(myLoc), newQualifier, newName,
                myQuantification);
    }

}
/**
 * VarExp.java
 * ---------------------------------
 * Copyright (c) 2015
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.absyn.mathexpr;

import edu.clemson.cs.r2jt.typeandpopulate2.entry.SymbolTableEntry;
import edu.clemson.cs.r2jt.typereasoning2.TypeGraph;
import edu.clemson.cs.rsrg.absyn.Exp;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>This is the class for all the mathematical variable expression
 * intermediate objects that the compiler builds from the ANTLR4 AST tree.</p>
 *
 * @version 2.0
 */
public class VarExp extends MathExp {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The expression's qualifier</p> */
    private final PosSymbol myQualifier;

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
     * <p>This method creates a special indented
     * text version of the class as a string.</p>
     *
     * @param indentSize The base indentation to the first line
     *                   of the text.
     * @param innerIndentSize The additional indentation increment
     *                        for the subsequent lines.
     *
     * @return A formatted text string of the class.
     */
    @Override
    public String asString(int indentSize, int innerIndentSize) {
        StringBuffer sb = new StringBuffer();
        printSpace(indentSize, sb);
        sb.append("VarExp\n");

        if (myQualifier != null) {
            sb.append(myQualifier.asString(indentSize + innerIndentSize,
                    innerIndentSize));
        }

        if (myName != null) {
            sb.append(myName.asString(indentSize + innerIndentSize,
                    innerIndentSize));
        }

        return sb.toString();
    }

    /**
     * <p>This method attempts to find the provided expression in our
     * subexpressions. The result of this calling this method should
     * always be false, because we can not contain an expression.</p>
     *
     * @param exp The expression we wish to locate.
     *
     * @return False.
     */
    @Override
    public boolean containsExp(Exp exp) {
        return false;
    }

    /**
     *  <p>This method attempts to find an expression with the given name in our
     * subexpressions. The result of this calling this method should
     * always be false, because we can not contain an expression.</p>
     *
     * @param varName Expression name.
     * @param IsOldExp Flag to indicate if the given name is of the form
     *                 "#[varName]"
     *
     * @return False.
     */
    @Override
    public boolean containsVar(String varName, boolean IsOldExp) {
        boolean retval = false;
        if (myName != null) {
            if (!IsOldExp && myName.equals(varName)) {
                retval = true;
            }
        }

        return retval;
    }

    /**
     * <p>This method overrides the default equals method implementation
     * for the {@link VarExp} class.</p>
     *
     * @param o Object to be compared.
     *
     * @return True if all the fields are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if (o instanceof VarExp) {
            VarExp eAsVarExp = (VarExp) o;
            result = myLoc.equals(eAsVarExp.myLoc);

            if (result) {
                result =
                        (posSymbolEquivalent(myQualifier, eAsVarExp.myQualifier) && (posSymbolEquivalent(
                                myName, eAsVarExp.myName)));
            }
        }

        return result;
    }

    /**
     * <p>Shallow compare is too weak for many things, and equals() is too
     * strict. This method returns <code>true</code> <strong>iff</code> this
     * expression and the provided expression, <code>e</code>, are equivalent
     * with respect to structure and all function and variable names.</p>
     *
     * @param e The expression to compare this one to.
     *
     * @return True <strong>iff</strong> this expression and the provided
     *         expression are equivalent with respect to structure and all
     *         function and variable names.
     */
    @Override
    public boolean equivalent(Exp e) {
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
     * <p>This method returns a deep copy of the name.</p>
     *
     * @return The {@link PosSymbol} representation object.
     */
    public PosSymbol getName() {
        return myName.clone();
    }

    /**
     * <p>This method returns a deep copy of the qualifier name.</p>
     *
     * @return The {@link PosSymbol} representation object.
     */
    public PosSymbol getQualifier() {
        return myQualifier.clone();
    }

    /**
     * <p>This method returns this variable expression's quantification.</p>
     *
     * @return The {@link SymbolTableEntry.Quantification} object.
     */
    public SymbolTableEntry.Quantification getQuantification() {
        return myQuantification;
    }

    /**
     * <p>This method method returns a deep copy of the list of
     * subexpressions. The result of this calling this method should
     * always be an empty list, because we can not contain an expression.</p>
     *
     * @return A list containing {@link Exp} type objects.
     */
    @Override
    public List<Exp> getSubExpressions() {
        return new ArrayList<>();
    }

    /**
     * <p>This method applies VC Generator's remember rule.
     * For all inherited programming expression classes, this method
     * should throw an exception.</p>
     *
     * @return The resulting {@link VarExp} from applying the remember rule.
     */
    @Override
    public VarExp remember() {
        return (VarExp) this.clone();
    }

    /**
     * <p>This method adds a new expression to our list of subexpressions.</p>
     *
     * @param index The index in our subexpression list.
     * @param e The new {@link Exp} to be added.
     */
    // TODO: See the message in Exp.
    //public void setSubExpression(int index, Exp e) {}

    /**
     * <p>This method applies the VC Generator's simplification step.</p>
     *
     * @return The resulting {@link MathExp} from applying the simplification step.
     */
    @Override
    public MathExp simplify() {
        return this.clone();
    }

    /**
     * <p>Returns the expression in string format.</p>
     *
     * @return Expression as a string.
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(myQuantification);

        if (myQualifier != null) {
            sb.append(myQualifier);
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
     * <p>Implemented by this concrete subclass of {@link Exp} to manufacture
     * a copy of themselves.</p>
     *
     * @return A new {@link Exp} that is a deep copy of the original.
     */
    @Override
    protected Exp copy() {
        PosSymbol newQualifier = null;
        if (myQualifier != null) {
            newQualifier = myQualifier.clone();
        }
        PosSymbol newName = myName.clone();

        return new VarExp(new Location(myLoc), newQualifier, newName,
                myQuantification);
    }

    /**
     * <p>Implemented by this concrete subclass of {@link Exp} to manufacture
     * a copy of themselves where all subexpressions have been appropriately
     * substituted. This class is assuming that <code>this</code>
     * does not match any key in <code>substitutions</code> and thus need only
     * concern itself with performing substitutions in its children.</p>
     *
     * @param substitutions A mapping from {@link Exp}s that should be
     *                      substituted out to the {@link Exp} that should
     *                      replace them.
     *
     * @return A new {@link Exp} that is a deep copy of the original with
     *         the provided substitutions made.
     */
    @Override
    protected Exp substituteChildren(Map<Exp, Exp> substitutions) {
        PosSymbol newQualifier = null;
        if (myQualifier != null) {
            newQualifier = myQualifier.clone();
        }
        PosSymbol newName = myName.clone();

        return new VarExp(new Location(myLoc), newQualifier, newName,
                myQuantification);
    }

}
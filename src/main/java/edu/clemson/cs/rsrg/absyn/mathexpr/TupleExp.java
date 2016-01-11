/**
 * TupleExp.java
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
import edu.clemson.cs.rsrg.absyn.Exp;
import edu.clemson.cs.rsrg.errorhandling.exception.MiscErrorException;
import edu.clemson.cs.rsrg.parsing.data.Location;
import java.io.InvalidClassException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <p>This is the class for all the mathematical tuple expressions
 * that the compiler builds from the ANTLR4 AST tree.</p>
 *
 * <p>Making TupleExp extend from AbstractFunctionExp was considered and
 * explicitly decided against during the great math-type-overhaul of 2012.
 * If we chose to admit the presence of some function that builds tuples for us,
 * how would we pass it its parameters if not via a tuple?  Thus, TupleExp is
 * a built-in notion, and not imagined as the result of the application of a
 * function.</p>
 *
 * @version 2.0
 */
public class TupleExp extends MathExp {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The expression's cartesian product fields</p> */
    private final List<Exp> myFields;

    /** <p>The number of cartesian product fields</p> */
    private final int mySize;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs a tuple expression that contains
     * exactly two elements per field.</p>
     *
     * @param l A {@link Location} representation object.
     * @param fields A list of {@link Exp} object.
     */
    public TupleExp(Location l, List<Exp> fields) {
        this(l, fields.toArray(new Exp[0]), fields.size());
    }

    /**
     * <p>This helper method helps construct inner tuple representations
     * if any.</p>
     *
     * @param l A {@link Location} representation object.
     * @param fields A array of {@link Exp} objects.
     * @param elementCount The number of elements in the array.
     */
    private TupleExp(Location l, Exp[] fields, int elementCount) {
        super(l);

        //We assert this isn't possible, but who knows?
        if (elementCount < 2) {
            throw new MiscErrorException("Unexpected cartesian product size.", new IllegalArgumentException());
        }

        int workingSize = 0;

        Exp first;
        if (elementCount == 2) {
            first = fields[0];
        }
        else {
            first = new TupleExp(l, fields, elementCount - 1);
        }

        if (first instanceof TupleExp) {
            workingSize += ((TupleExp) first).getSize();
        }
        else {
            workingSize += 1;
        }

        Exp second = fields[elementCount - 1];
        workingSize += 1;

        myFields = new ArrayList<>();
        myFields.add(first);
        myFields.add(second);

        mySize = workingSize;
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
        sb.append("TupleExp\n");

        if (myFields != null) {
            for (Exp e : myFields) {
                sb.append(e.asString(indentSize + innerIndentSize,
                        innerIndentSize));
            }
        }

        return sb.toString();
    }

    /**
     * <p>This method attempts to find the provided expression in our
     * subexpressions.</p>
     *
     * @param exp The expression we wish to locate.
     *
     * @return True if there is an instance of <code>exp</code>
     * within this object's subexpressions. False otherwise.
     */
    @Override
    public boolean containsExp(Exp exp) {
        boolean found = false;
        if (myFields != null) {
            Iterator<Exp> i = myFields.iterator();
            while (i.hasNext() && !found) {
                Exp temp = i.next();
                if (temp != null) {
                    if (temp.containsExp(exp)) {
                        found = true;
                    }
                }
            }
        }

        return found;
    }

    /**
     *  <p>This method attempts to find an expression with the given name in our
     * subexpressions.</p>
     *
     * @param varName Expression name.
     * @param IsOldExp Flag to indicate if the given name is of the form
     *                 "#[varName]"
     *
     * @return True if there is a {@link Exp} within this object's
     * subexpressions that matches <code>varName</code>. False otherwise.
     */
    @Override
    public boolean containsVar(String varName, boolean IsOldExp) {
        boolean found = false;
        if (myFields != null) {
            Iterator<Exp> i = myFields.iterator();
            while (i.hasNext() && !found) {
                Exp temp = i.next();
                if (temp != null) {
                    if (temp.containsVar(varName, IsOldExp)) {
                        found = true;
                    }
                }
            }
        }

        return found;
    }

    /**
     * <p>This method overrides the default equals method implementation
     * for the {@link TupleExp} class.</p>
     *
     * @param o Object to be compared.
     *
     * @return True if all the fields are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if (o instanceof DotExp) {
            TupleExp eAsTupleExp = (TupleExp) o;
            result = myLoc.equals(eAsTupleExp.myLoc);

            if (result) {
                if (myFields != null && eAsTupleExp.myFields != null) {
                    Iterator<Exp> thisFieldExps = myFields.iterator();
                    Iterator<Exp> eFieldExps = eAsTupleExp.myFields.iterator();

                    while (result && thisFieldExps.hasNext()
                            && eFieldExps.hasNext()) {
                        result &=
                                thisFieldExps.next().equals(eFieldExps.next());
                    }

                    //Both had better have run out at the same time
                    result &=
                            (!thisFieldExps.hasNext())
                                    && (!eFieldExps.hasNext());
                }
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
        boolean result = (e instanceof DotExp);

        if (result) {
            TupleExp eAsTupleExp = (TupleExp) e;

            if (myFields != null && eAsTupleExp.myFields != null) {
                Iterator<Exp> thisFieldExps = myFields.iterator();
                Iterator<Exp> eFieldExps = eAsTupleExp.myFields.iterator();
                while (result && thisFieldExps.hasNext()
                        && eFieldExps.hasNext()) {

                    result &=
                            thisFieldExps.next().equivalent(eFieldExps.next());
                }

                //Both had better have run out at the same time
                result &= (!thisFieldExps.hasNext()) && (!eFieldExps.hasNext());
            }
        }

        return result;
    }

    /**
     * <p>This method returns a deep copy of the specified field expression.</p>
     *
     * @param index The index of the field expression.
     *
     * @return A {@link Exp} representation object.
     */
    public Exp getField(int index) {
        Exp result;

        if (index < 0 || index >= mySize) {
            throw new MiscErrorException("Index out of bounds.",
                    new IndexOutOfBoundsException("" + index));
        }

        if (index == (mySize - 1)) {
            result = myFields.get(1);
        }
        else {
            if (mySize == 2) {
                //ASSERT: !(myElements.get(0) instanceof MTCartesian)
                if (index != 0) {
                    throw new IndexOutOfBoundsException("" + index);
                }

                result = myFields.get(0);
            }
            else {
                //ASSERT: myElements.get(0) instanceof MTCartesian
                result = ((TupleExp) myFields.get(0)).getField(index);
            }
        }

        return result.clone();
    }

    /**
     * <p>This method returns a deep copy of all the inner field expressions.</p>
     *
     * @return A list containing all the segmented {@link Exp}s.
     */
    public List<Exp> getFields() {
        return copyExps();
    }

    /**
     * <p>This method returns the number of field elements in this tuple.</p>
     *
     * @return The size of this {@link TupleExp}.
     */
    public int getSize() {
        return mySize;
    }

    /**
     * <p>This method method returns a deep copy of the list of
     * subexpressions. This method will return the same result
     * as calling the {@link TupleExp#getFields()} method.</p>
     *
     * @return A list containing subexpressions ({@link Exp}s).
     */
    @Override
    public List<Exp> getSubExpressions() {
        return getFields();
    }

    /**
     * <p>This method checks to see if all the field expressions
     * inside this tuple expression are universally quantified.</p>
     *
     * @return True if all {@link Exp}s are universally quantified,
     * false otherwise.
     */
    public boolean isUniversallyQuantified() {
        boolean soFar = true;

        for (Exp field : myFields) {
            soFar =
                    soFar
                            && ((field instanceof VarExp && ((VarExp) field)
                                    .getQuantification() == SymbolTableEntry.Quantification.UNIVERSAL) || (field instanceof TupleExp && ((TupleExp) field)
                                    .isUniversallyQuantified()));
        }

        return soFar;
    }

    /**
     * <p>This method applies VC Generator's remember rule.
     * For all inherited programming expression classes, this method
     * should throw an exception.</p>
     *
     * @return The resulting {@link TupleExp} from applying the remember rule.
     */
    @Override
    public TupleExp remember() {
        List<Exp> newFieldExps = new ArrayList<>();
        for (Exp e : myFields) {
            Exp copyExp;
            if (e instanceof MathExp){
                copyExp = ((MathExp) e).remember();
            }
            else {
                throw new MiscErrorException("We encountered an expression of the type " +
                        e.getClass().getName(),
                        new InvalidClassException(""));
            }

            newFieldExps.add(copyExp);
        }

        return new TupleExp(new Location(myLoc), newFieldExps);
    }

    /**
     * <p>This method adds a new expression to our list of subexpressions.</p>
     *
     * @param index The index in our subexpression list.
     * @param e The new {@link Exp} to be added.
     */
    // TODO: See the message in Exp.
    /*public void setSubExpression(int index, Exp e) {
        myFields.set(index, e);
    }*/

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
        sb.append("(");

        boolean first = true;
        for (Exp member : myFields) {
            if (!first) {
                sb.append(", ");
            }
            else {
                first = false;
            }

            sb.append(member.toString());
        }
        sb.append(")");

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
        return new TupleExp(new Location(myLoc), copyExps());
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
    protected Exp substituteChildren(java.util.Map<Exp, Exp> substitutions) {
        List<Exp> newFields = new ArrayList<>();
        for (Exp f : myFields) {
            newFields.add(substitute(f, substitutions));
        }

        return new TupleExp(new Location(myLoc), newFields);
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>This is a helper method that makes a copy of the
     * list containing all the field expressions.</p>
     *
     * @return A list containing {@link Exp}s.
     */
    private List<Exp> copyExps() {
        List<Exp> copyFieldExps = new ArrayList<>();
        for (Exp exp : myFields) {
            copyFieldExps.add(exp.clone());
        }

        return copyFieldExps;
    }
}
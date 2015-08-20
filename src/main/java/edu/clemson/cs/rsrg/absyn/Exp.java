/**
 * Exp.java
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
package edu.clemson.cs.rsrg.absyn;

import edu.clemson.cs.r2jt.typeandpopulate2.MTType;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import java.util.*;

/**
 * <p>This is the abstract base class for all the expression
 * intermediate objects that the compiler builds from the ANTLR4 AST tree.</p>
 *
 * @version 2.0
 */
public abstract class Exp extends ResolveConceptualElement {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The object's mathematical type.</p> */
    protected MTType myMathType = null;

    /** <p>The object's mathematical type value.</p> */
    protected MTType myMathTypeValue = null;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>A helper constructor that allow us to store the location
     * of the created object directly in the this class.</p>
     *
     * @param l A {link Location} representation object.
     */
    protected Exp(Location l) {
        super(l);
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This method overrides the default clone method implementation
     * for all the classes that extend from {link Exp}.</p>
     *
     * @return A deep copy of the object.
     */
    @Override
    public final Exp clone() {
        Exp result = this.copy();
        result.setMathType(myMathType);
        result.setMathTypeValue(myMathTypeValue);

        return result;
    }

    /**
     * <p>This method must be implemented by all inherited classes
     * to attempt to find the provided expression in our
     * subexpressions.</p>
     *
     * @param exp The expression we wish to locate.
     *
     * @return True if there is an instance of <code>exp</code>
     * within this object's subexpressions. False otherwise.
     */
    public abstract boolean containsExp(Exp exp);

    /**
     *  <p>This method must be implemented by all inherited classes
     * to attempt to find an expression with the given name in our
     * subexpressions.</p>
     *
     * @param varName Expression name.
     * @param IsOldExp Flag to indicate if the given name is of the form
     *                 "#[varName]"
     *
     * @return True if there is a {link Exp} within this object's
     * subexpressions that matches <code>varName</code>. False otherwise.
     */
    public abstract boolean containsVar(String varName, boolean IsOldExp);

    /**
     * <p>Shallow compare is too weak for many things, and equals() is too
     * strict.  This method returns <code>true</code> <strong>iff</code> this
     * expression and the provided expression, <code>e</code>, are equivalent
     * with respect to structure and all function and variable names.</p>
     *
     * @param e The expression to compare this one to.
     *
     * @return True <strong>iff</strong> this expression and the provided
     *         expression are equivalent with respect to structure and all
     *         function and variable names.
     */
    public boolean equivalent(Exp e) {
        System.out.println(e.toString());
        throw new UnsupportedOperationException(
                "Equivalence for classes of type " + this.getClass()
                        + " is not currently supported.");
    }

    /**
     * <p>Helper method to deal with {link Exp}s that need to be
     * compared but might be null.  Returns true <strong>iff</strong>
     * <code>e1</code> and <code>e2</code> are both <code>null</code> or both
     * are not <code>null</code> and equivalent.</p>
     *
     * @param e1 The first <code>Exp</code>.
     * @param e2 The second <code>Exp</code>.
     *
     * @return <code>true</code> <strong>iff</strong> both
     * 		   {link Exp}ss are null; or both are not null and are
     *         equivalent.
     */
    public static boolean equivalent(Exp e1, Exp e2) {
        return !((e1 == null ^ e2 == null))
                && ((e1 == null && e2 == null) || e1.equivalent(e2));
    }

    /**
     * <p>This method gets the mathematical type associated
     * with this object.</p>
     *
     * @return The {link MTType} type object.
     */
    public final MTType getMathType() {
        return myMathType;
    }

    /**
     * <p>This method gets the mathematical type value associated
     * with this object.</p>
     *
     * @return The {link MTType} type object.
     */
    public final MTType getMathTypeValue() {
        return myMathTypeValue;
    }

    /**
     * <p>This method must be implemented by all inherited classes
     * to return the list of subexpressions.</p>
     *
     * @return A list containing {link Exp} type objects.
     */
    public abstract List<Exp> getSubExpressions();

    /**
     * <p>This method returns the default behavior for an
     * arbitrary {link Exp} when checking to see if we are
     * simply the literal "true". The only class that needs
     * to override this method is {link VarExp}.</p>
     *
     * @return Always false, because we are not the right class.
     */
    public boolean isLiteralTrue() {
        return false;
    }

    /**
     * <p>This method returns the default behavior for an
     * arbitrary {link Exp} when checking to see if we are
     * simply the literal "false". The only class that needs
     * to override this method is {link VarExp}.</p>
     *
     * @return Always false, because we are not the right class.
     */
    public boolean isLiteralFalse() {
        return false;
    }

    /**
     * <p>Helper method to deal with {link PosSymbol}s that need to be
     * compared but might be null.  Returns true <strong>iff</strong>
     * <code>s1</code> and <code>s2</code> are both <code>null</code> or both
     * are not <code>null</code> and have names that are equivalent strings (see
     * <code>stringEquivalent</code>())</p>
     *
     * @param s1 The first {link PosSymbol}.
     * @param s2 The second {link PosSymbol}.
     *
     * @return <code>true</code> <strong>iff</strong> both
     * {link PosSymbol}s are null; or both are not null and have names
     * that are equivalent strings (see {link Exp#stringEquivalent}).
     */
    public static boolean posSymbolEquivalent(PosSymbol s1, PosSymbol s2) {
        //The first line makes sure that either both s1 and s2 are null or
        //neither is.  If not, we short circuit with "false".
        //The second line short circuits and returns "true" if both are null.
        //The third line performs the string comparison.
        return !((s1 == null) ^ (s2 == null))
                && ((s1 == null && s2 == null) || (stringEquivalent(s1
                .getName(), s2.getName())));
    }

    /**
     * <p>This method must be implemented by the inherited mathematical
     * expression classes to apply VC Generator's remember rule.
     * For all inherited programming expression classes, this method
     * should throw an exception.</p>
     *
     * @return The resulting {link Exp} from applying the remember rule.
     */
    public abstract Exp remember();

    /**
     * <p>This method sets the mathematical type associated
     * with this object.</p>
     *
     * @param mathType The {link MTType} type object.
     */
    public final void setMathType(MTType mathType) {
        if (mathType == null) {
            System.err.println(this.toString());
            throw new RuntimeException("Null Math Type on: " + this.getClass());

        }

        myMathType = mathType;
    }

    /**
     * <p>This method sets the mathematical type value associated
     * with this object.</p>
     *
     * @param mathTypeValue The {link MTType} type object.
     */
    public final void setMathTypeValue(MTType mathTypeValue) {
        myMathTypeValue = mathTypeValue;
    }

    /**
     *  <p>This method must be implemented by all inherited classes
     * to add a new expression to our list of subexpressions.</p>
     *
     * @param index The index in our subexpression list.
     * @param e The new {link Exp} to be added.
     */
    public abstract void setSubExpression(int index, Exp e);

    /**
     * <p>This method must be implemented by the inherited mathematical
     * expression classes to apply VC Generator's simplification step.
     * For all inherited programming expression classes, this method
     * should throw an exception.</p>
     *
     * @return The resulting {link Exp} from applying the simplification step.
     */
    public abstract Exp simplify();

    /**
     * <p>This method is used to convert a {link Exp} into the prover's
     * version of {link PExp}. The key to this method is figuring out
     * where the different implications occur within the expression.</p>
     *
     * @param assumpts The assumption expressions for this expression.
     * @param single Boolean flag to indicate whether or not this is a
     *               standalone expression.
     *
     * @return A list of {link Exp} objects.
     */
    public List<InfixExp> split(Exp assumpts, boolean single) {
        if (this instanceof InfixExp) {
            if (((InfixExp) this).getOpName().toString().equals("implies"))
                return this.split(null, false);
            else
                return this.split(null, single);
        }
        else if (single) {
            List<InfixExp> lst = new ArrayList<InfixExp>();
            if (assumpts == null) {
                lst.add(new InfixExp(null, null, new PosSymbol(this.getLocation(), "implies"),
                        this));
            }
            else {
                lst.add(new InfixExp(null, assumpts,
                        new PosSymbol(this.getLocation(), "implies"), this));
            }
            return lst;
        }
        else
            return new ArrayList<InfixExp>();
    }

    /**
     * <p>This method is used to convert a {link Exp} into the prover's
     * version of {link PExp}. The key to this method is figuring out
     * where the different implications occur within the expression.</p>
     *
     * @return A list of {link Exp} objects.
     */
    public List<InfixExp> split() {
        return this.split(null, true);
    }

    /**
     * <p>Helper method to deal with strings that need to be compared but might
     * be null.  Returns true <strong>iff</strong> <code>s1</code> and
     * <code>s2</code> are both <code>null</code> or both are not null and
     * represent the same string (case sensitive).</p>
     *
     * @param s1 The first string.
     * @param s2 The second string.
     *
     * @return <code>true</code> <strong>iff</strong> both string are null;
     * or both are not null and represent the same string.
     */
    public static boolean stringEquivalent(String s1, String s2) {
        //The first line makes sure that either both s1 and s2 are null or
        //neither is.  If not, we short circuit with "false".
        //The second line short circuits and returns "true" if both are null.
        //The third line performs the string comparison.
        return !((s1 == null) ^ (s2 == null))
                && ((s1 == null && s2 == null) || (s1.equals(s2)));
    }

    /**
     * <p>Returns a DEEP COPY of this expression, with all instances of 
     * {link Exp}s that occur as keys in <code>substitutions</code>
     * replaced with their corresponding values.</p>
     * 
     * <p>In general, a key {link Exp} "occurs" in this {link Exp}
     * if either this {link Exp} or some subexpression is
     * <code>equivalent()</code>. However, if the key is a {link VarExp}
     * function names are additionally matched, even though they would not
     * ordinarily match via <code>equivalent()</code>, so function names can
     * be substituted without affecting their arguments.</p>
     *   
     * @param substitutions A mapping from {link Exp}s that should be
     *                      substituted out to the {link Exp} that should
     *                      replace them.
     *
     * @return A new {link Exp} that is a deep copy of the original with
     *         the provided substitutions made.
     */
    public final Exp substitute(Map<Exp, Exp> substitutions) {
        Exp retval;

        boolean match = false;

        Map.Entry<Exp, Exp> curEntry = null;
        if (substitutions.size() > 0) {
            Set<Map.Entry<Exp, Exp>> entries =
                    substitutions.entrySet();
            Iterator<Map.Entry<Exp, Exp>> entryIter =
                    entries.iterator();
            while (entryIter.hasNext() && !match) {
                curEntry = entryIter.next();
                match = curEntry.getKey().equivalent(this);
            }

            if (match) {
                retval = curEntry.getValue();
            }
            else {
                retval = substituteChildren(substitutions);
                retval.setMathType(myMathType);
                retval.setMathTypeValue(myMathTypeValue);
            }
        }
        else {
            retval = this.clone();
        }

        return retval;
    }

    // ===========================================================
    // Protected Methods
    // ===========================================================

    /**
     * <p>Implemented by concrete subclasses of {link Exp} to manufacture
     * a copy of themselves.</p>
     *
     * @return A new {link Exp} that is a deep copy of the original.
     */
    protected Exp copy() {
        System.out.println("Shouldn't be calling Exp.copy() from type "
                + this.getClass());
        throw new RuntimeException();
    }

    /**
     * <p>Implemented by concrete subclasses of {link Exp} to manufacture
     * a copy of themselves where all the fields have been appropriately
     * substituted.</p>
     *
     * @param e The original {link Exp}.
     * @param substitutions A mapping from {link Exp}s that should be
     *                      substituted out to the {link Exp} that should
     *                      replace them.
     *
     * @return A new {link Exp} that is a deep copy of the original with
     *         the provided substitutions made.
     */
    protected static Exp substitute(Exp e, Map<Exp, Exp> substitutions) {
        Exp retval;

        if (e == null) {
            retval = null;
        }
        else {
            retval = e.substitute(substitutions);
        }

        return retval;
    }

    /**
     * <p>Implemented by concrete subclasses of {link Exp} to manufacture
     * a copy of themselves where all subexpressions have been appropriately
     * substituted.  The concrete subclass may assume that <code>this</code>
     * does not match any key in <code>substitutions</code> and thus need only
     * concern itself with performing substitutions in its children.</p>
     * 
     * @param substitutions A mapping from {link Exp}s that should be
     *                      substituted out to the {link Exp} that should
     *                      replace them.
     *
     * @return A new {link Exp} that is a deep copy of the original with
     *         the provided substitutions made.
     */
    protected abstract Exp substituteChildren(Map<Exp, Exp> substitutions);

}
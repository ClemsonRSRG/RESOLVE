/*
 * Exp.java
 * ---------------------------------
 * Copyright (c) 2018
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.absyn.expressions;

import edu.clemson.cs.rsrg.absyn.ResolveConceptualElement;
import edu.clemson.cs.rsrg.absyn.expressions.mathexpr.VarExp;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.parsing.data.LocationDetailModel;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import edu.clemson.cs.rsrg.statushandling.exception.MiscErrorException;
import edu.clemson.cs.rsrg.typeandpopulate.mathtypes.MTType;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>This is the abstract base class for all the expression objects
 * that the compiler builds using the ANTLR4 AST nodes.</p>
 *
 * @version 2.0
 */
public abstract class Exp extends ResolveConceptualElement {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /**
     * <p>An object that contains additional information on where this
     * expression came from. This should be added by the {@code VCGenerator}
     * when applying the various different {@code proof rules}.</p>
     *
     * <p>Note: We were trying to hard to make things immutable. There are
     * things that simply can't be immutable until we are done creating objects.
     * This is probably the best compromise given the design decision of the
     * {@code Exp} hierarchy.</p>
     */
    private LocationDetailModel myLocationDetailModel = null;

    /** <p>The object's mathematical type.</p> */
    protected MTType myMathType = null;

    /** <p>The object's mathematical type value.</p> */
    protected MTType myMathTypeValue = null;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>An helper constructor that allow us to store the location
     * of any objects created from a class that inherits from
     * {@code Exp}.</p>
     *
     * @param l A {@link Location} representation object.
     */
    protected Exp(Location l) {
        super(l);
    }

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>This method overrides the default clone method implementation
     * for all the classes that extend from {@link Exp}.</p>
     *
     * @return A deep copy of the object.
     */
    @Override
    public Exp clone() {
        Exp result = this.copy();
        result.setMathType(myMathType);
        result.setMathTypeValue(myMathTypeValue);

        // Copy any location detail models
        if (myLocationDetailModel != null) {
            result.setLocationDetailModel(myLocationDetailModel.clone());
        }

        return result;
    }

    /**
     * <p>Compares to see if the expression matches this object.</p>
     *
     * @param exp A {@link Exp} to compare.
     *
     * @return A {@link VarExp} containing {@code true} if it is exactly the same,
     * otherwise just return a deep copy our ourselves.
     */
    public Exp compareWithAssumptions(Exp exp) {
        Exp retExp;
        if (this.equivalent(exp)) {
            retExp = VarExp.getTrueVarExp(myLoc, myMathType.getTypeGraph());
        }
        else {
            retExp = this.clone();
        }

        return retExp;
    }

    /**
     * <p>This method must be implemented by all inherited classes
     * to attempt to find the provided expression in our
     * sub-expressions.</p>
     *
     * @param exp The expression we wish to locate.
     *
     * @return {@code true} if there is an instance of {@code exp}
     * within this object's sub-expressions. {@code false} otherwise.
     */
    public abstract boolean containsExp(Exp exp);

    /**
     * <p>This method attempts to find an expression with the given name in our
     * sub-expressions. This method is only invoked by a mathematical expression,
     * but since we could have either mathematical or programming
     * expressions, the default behavior is to return {@code false}.</p>
     *
     * <p>Any inherited mathematical expressions must override this method
     * to make this method work.</p>
     *
     * @param varName Expression name.
     * @param IsOldExp Flag to indicate if the given name is of the form
     *                 {@code #[varName]}
     *
     * @return False.
     */
    public boolean containsVar(String varName, boolean IsOldExp) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Exp exp = (Exp) o;

        // YS: Note that this check should be in here for completeness,
        //     but so many things have been built without checking for
        //     location equality. At some point someone should add this
        //     back in and make sure everything still works as intended!
        /*if (myLoc != null ? !myLoc.equals(exp.myLoc) : exp.myLoc != null)
            return false;
         */

        if (myLocationDetailModel != null ? !myLocationDetailModel
                .equals(exp.myLocationDetailModel)
                : exp.myLocationDetailModel != null)
            return false;
        if (myMathType != null ? !myMathType.equals(exp.myMathType)
                : exp.myMathType != null)
            return false;
        return myMathTypeValue != null ? myMathTypeValue
                .equals(exp.myMathTypeValue) : exp.myMathTypeValue == null;
    }

    /**
     * <p>Shallow compare is too weak for many things, and equals() is too
     * strict. This method returns {@code true} <strong>iff</strong> this
     * expression and the provided expression, {@code e}, are equivalent
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
     * <p>Helper method to deal with {@link Exp}s that need to be
     * compared but might be {@code null}. Returns {@code true} <strong>iff</strong>
     * {@code e1} and {@code e2} are both {@code null} or both
     * are not {@code null} and equivalent.</p>
     *
     * @param e1 The first {@link Exp}.
     * @param e2 The second {@link Exp}.
     *
     * @return {@code true} <strong>iff</strong> both
     * 		   {@link Exp}s are {@code null}; or both are not {@code null} and are
     *         equivalent.
     */
    public static boolean equivalent(Exp e1, Exp e2) {
        return !((e1 == null ^ e2 == null))
                && ((e1 == null && e2 == null) || e1.equivalent(e2));
    }

    /**
     * <p>This method gets the location details associated
     * with this object.</p>
     *
     * @return A {@link LocationDetailModel} object.
     */
    public final LocationDetailModel getLocationDetailModel() {
        return myLocationDetailModel;
    }

    /**
     * <p>This method gets the mathematical type associated
     * with this object.</p>
     *
     * @return The {@link MTType} type object.
     */
    public final MTType getMathType() {
        return myMathType;
    }

    /**
     * <p>This method gets the mathematical type value associated
     * with this object.</p>
     *
     * @return The {@link MTType} type object.
     */
    public final MTType getMathTypeValue() {
        return myMathTypeValue;
    }

    /**
     * <p>This method must be implemented by all inherited classes
     * to return the list of sub-expressions.</p>
     *
     * @return A list containing {@link Exp} type objects.
     */
    public abstract List<Exp> getSubExpressions();

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result =
                myLocationDetailModel != null ? myLocationDetailModel
                        .hashCode() : 0;
        result = 31 * result + (myMathType != null ? myMathType.hashCode() : 0);
        result =
                31
                        * result
                        + (myMathTypeValue != null ? myMathTypeValue.hashCode()
                                : 0);
        return result;
    }

    /**
     * <p>Helper method to deal with {@link PosSymbol}s that need to be
     * compared but might be {@code null}. Returns {@code true} <strong>iff</strong>
     * {@code s1} and {@code s2} are both {@code null} or both
     * are not {@code null} and have names that are equivalent strings (see
     * {@link Exp#stringEquivalent(String, String)})</p>
     *
     * @param s1 The first {@link PosSymbol}.
     * @param s2 The second {@link PosSymbol}.
     *
     * @return {@code true} <strong>iff</strong> both
     * {@link PosSymbol}s are {@code null}; or both are not {@code null} and have names
     * that are equivalent strings (see {@link Exp#stringEquivalent(String, String)}).
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
     * <p>This method sets the location details associated
     * with this object.</p>
     *
     * @param locationDetailModel A {@link LocationDetailModel} object.
     */
    public final void setLocationDetailModel(
            LocationDetailModel locationDetailModel) {
        myLocationDetailModel = locationDetailModel;
    }

    /**
     * <p>This method sets the mathematical type associated
     * with this object.</p>
     *
     * @param mathType The {@link MTType} type object.
     */
    public void setMathType(MTType mathType) {
        myMathType = mathType;
    }

    /**
     * <p>This method sets the mathematical type value associated
     * with this object.</p>
     *
     * @param mathTypeValue The {@link MTType} type object.
     */
    public void setMathTypeValue(MTType mathTypeValue) {
        myMathTypeValue = mathTypeValue;
    }

    /**
     * <p>Helper method to deal with strings that need to be compared but might
     * be {@code null}. Returns {@code true} <strong>iff</strong> {@code s1} and
     * {@code s2} are both {@code null} or both are not {@code null} and
     * represent the same string (case sensitive).</p>
     *
     * @param s1 The first string.
     * @param s2 The second string.
     *
     * @return {@code true} <strong>iff</strong> both string are {@code null};
     * or both are not {@code null} and represent the same string.
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
     * {@link Exp}s that occur as keys in {@code substitutions}
     * replaced with their corresponding values.</p>
     * 
     * <p>In general, a key {@link Exp} "occurs" in this {@link Exp}
     * if either this {@link Exp} or some sub-expression is
     * {@link Exp#equivalent(Exp)}. However, if the key is a {@link VarExp}
     * function names are additionally matched, even though they would not
     * ordinarily match via {@link Exp#equivalent(Exp)}, so function names can
     * be substituted without affecting their arguments.</p>
     *   
     * @param substitutions A mapping from {@link Exp}s that should be
     *                      substituted out to the {@link Exp} that should
     *                      replace them.
     *
     * @return A new {@link Exp} that is a deep copy of the original with
     *         the provided substitutions made.
     */
    public final Exp substitute(Map<Exp, Exp> substitutions) {
        Exp retval;

        boolean match = false;

        Map.Entry<Exp, Exp> curEntry = null;
        if (substitutions.size() > 0) {
            Set<Map.Entry<Exp, Exp>> entries = substitutions.entrySet();
            Iterator<Map.Entry<Exp, Exp>> entryIter = entries.iterator();
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

                // Copy the location detail model if it is not null
                if (myLocationDetailModel != null) {
                    retval
                            .setLocationDetailModel(myLocationDetailModel
                                    .clone());
                }
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
     * <p>Implemented by concrete subclasses of {@link Exp} to manufacture
     * a copy of themselves.</p>
     *
     * @return A new {@link Exp} that is a deep copy of the original.
     */
    protected Exp copy() {
        throw new MiscErrorException("Shouldn't be calling copy() from type "
                + this.getClass(), new CloneNotSupportedException());
    }

    /**
     * <p>A static helper method that calls substitute method using
     * {@code e}.</p>
     *
     * @param e The original {@link Exp}.
     * @param substitutions A mapping from {@link Exp}s that should be
     *                      substituted out to the {@link Exp} that should
     *                      replace them.
     *
     * @return A new {@link Exp} that is a deep copy of the original with
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
     * <p>Implemented by concrete subclasses of {@link Exp} to manufacture
     * a copy of themselves where all sub-expressions have been appropriately
     * substituted. The concrete subclass may assume that {@code this}
     * does not match any key in {@code substitutions} and thus need only
     * concern itself with performing substitutions in its children.</p>
     * 
     * @param substitutions A mapping from {@link Exp}s that should be
     *                      substituted out to the {@link Exp} that should
     *                      replace them.
     *
     * @return A new {@link Exp} that is a deep copy of the original with
     *         the provided substitutions made.
     */
    protected abstract Exp substituteChildren(Map<Exp, Exp> substitutions);

}
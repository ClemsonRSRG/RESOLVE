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
package edu.clemson.cs.r2jt.absyn;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import edu.clemson.cs.r2jt.collections.List;
import edu.clemson.cs.r2jt.data.Location;
import edu.clemson.cs.r2jt.data.PosSymbol;
import edu.clemson.cs.r2jt.data.Symbol;
import edu.clemson.cs.r2jt.typeandpopulate.MTType;
import edu.clemson.cs.r2jt.typereasoning.TypeGraph;
import edu.clemson.cs.r2jt.typeandpopulate.MTProper;

public abstract class Exp extends ResolveConceptualElement implements Cloneable {

    /*
     * These variables are useful to the proof checking classes and
     * will only be set if the -proofcheck flag in the environment is ON --
     *  Addendum HwS: But type should ultimately be set always!  And it is now
     *  set if you turn on -prove as well!
     */
    protected MTType myMathType = null;
    protected MTType myMathTypeValue = null;

    //private boolean isLocal = false;
    private int marker = 0;

    public abstract void accept(ResolveConceptualVisitor v);

    public abstract String asString(int indent, int increment);

    public abstract Location getLocation();

    public abstract boolean containsVar(String varName, boolean IsOldExp);

    public abstract List<Exp> getSubExpressions();

    public abstract void setSubExpression(int index, Exp e);

    //    public abstract boolean  equals(Exp exp, TypeMatcher tm);
    public String toString(int indent, int increment) {

        return new String();
    }

    public String toString(int indent) {
        String exp = "";
        //return this.toString();
        return exp;
    }

    protected Exp replace(Exp old, Exp replacement) {

        throw new UnsupportedOperationException("Replace not implemented for "
                + this.getClass() + ".");
        //return new VarExp();
    }

    public String toString() {
        return toString(0);
    }

    /**
     * <p>Returns a DEEP COPY of this expression, with all instances of 
     * <code>Exp</code>s that occur as keys in <code>substitutions</code> 
     * replaced with their corresponding values.</p>
     * 
     * <p>In general, a key <code>Exp</code> "occurs" in this <code>Exp</code>
     * if either this <code>Exp</code> or some subexpression is 
     * <code>equivalent()</code>.  However, if the key is a <code>VarExp</code>
     * function names are additionally matched, even though they would not
     * ordinarily match via <code>equivalent()</code>, so function names can
     * be substituted without affecting their arguments.</p>
     *   
     * @param substitutions A mapping from <code>Exp</code>s that should be
     *                      substituted out to the <code>Exp</code> that should
     *                      replace them.
     * @return A new <code>Exp</code> that is a deep copy of the original with
     *         the provided substitutions made.
     */
    public final Exp substitute(java.util.Map<Exp, Exp> substitutions) {
        Exp retval;

        boolean match = false;

        java.util.Map.Entry<Exp, Exp> curEntry = null;
        if (substitutions.size() > 0) {
            Set<java.util.Map.Entry<Exp, Exp>> entries =
                    substitutions.entrySet();
            Iterator<java.util.Map.Entry<Exp, Exp>> entryIter =
                    entries.iterator();
            //System.out.println("Recursing: " + this.toString(0) + " : " + this.getClass());
            while (entryIter.hasNext() && !match) {
                curEntry = entryIter.next();
                //System.out.print(curEntry.getKey().toString(0) + " --?-> " + curEntry.getValue().toString(0));
                match = curEntry.getKey().equivalent(this);

                /*if (match) {
                	System.out.println(" [Yes] ");
                }
                else {
                	System.out.println(" [ No] ");
                }*/
            }

            if (match) {
                //System.out.println(curEntry.getKey().toString(0) + " --> " + curEntry.getValue().toString(0));
                retval = curEntry.getValue();
            }
            else {
                retval = Exp.substituteChildren(this, substitutions);
            }
        }
        else {
            retval = Exp.copy(this);
        }

        return retval;
    }

    //XXX : For the benefit of making the old prover work with the new type
    //      system, we make the assumption that performing substitutions does
    //      not change the type of the expression.  In general, this is a 
    //      terrible assumption, but it shouldn't cause any unsoundness in the
    //      examples we're looking at.  When the new prover is ready, this
    //      method will become unnecessary, because substitutions will occur
    //      on PExps rather than Exps.
    public static final Exp substituteChildren(Exp target,
            java.util.Map<Exp, Exp> substitutions) {

        MTType originalType = target.getMathType();
        MTType originalTypeValue = target.getMathTypeValue();

        Exp result = target.substituteChildren(substitutions);

        result.setMathType(originalType);
        result.setMathTypeValue(originalTypeValue);

        return result;
    }

    public final Exp substituteNames(java.util.Map<String, Exp> substitutions) {
        java.util.Map<Exp, Exp> finalSubstitutions = new HashMap<Exp, Exp>();

        for (java.util.Map.Entry<String, Exp> substitution : substitutions
                .entrySet()) {

            finalSubstitutions.put(new VarExp(null, null, new PosSymbol(null,
                    Symbol.symbol(substitution.getKey()))), substitution
                    .getValue());
        }

        return substitute(finalSubstitutions);
    }

    protected static Exp substitute(Exp e, java.util.Map<Exp, Exp> substitutions) {
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
     * <p>Implemented by concrete subclasses of <code>Exp</code> to manufacture
     * a copy of themselves where all subexpressions have been appropriately
     * substituted.  The concrete subclass may assume that <code>this</code>
     * does not match any key in <code>substitutions</code> and thus need only
     * concern itself with performing substitutions in its children.</p>
     * 
     * @param substitutions A mapping from <code>Exp</code>s that should be
     *                      substituted out to the <code>Exp</code> that should
     *                      replace them.
     * @return A new <code>Exp</code> that is a deep copy of the original with
     *         the provided substitutions made.
     */
    protected abstract Exp substituteChildren(
            java.util.Map<Exp, Exp> substitutions);

    public Exp simplify() {
        return this;
    }

    public boolean equals(Exp exp) {
        return exp.toString(1).equals(this.toString(1));
    }

    public List<InfixExp> split(Exp assumpts, boolean single) {
        if (this instanceof InfixExp) {
            if (((InfixExp) this).getOpName().toString().equals("implies"))
                return this.split(null, false);
            else
                return this.split(null, single);
        }
        else if (single) {
            List<InfixExp> lst = new List<InfixExp>();
            if (assumpts == null) {
                lst.add(new InfixExp(null, null, createPosSymbol("implies"),
                        this));
            }
            else {
                lst.add(new InfixExp(null, assumpts,
                        createPosSymbol("implies"), this));
            }
            return lst;
        }
        else
            return new List<InfixExp>();
    }

    public List<InfixExp> split() {
        return this.split(null, true);
    }

    Exp getAssumptions() {
        return this;
    }

    /**
     * Builds a sequence of numSpaces spaces and returns that
     * sequence.
     */
    protected void printSpace(int numSpaces, StringBuffer buffer) {
        for (int i = 0; i < numSpaces; ++i) {
            buffer.append(" ");
        }
    }

    @Deprecated
    public Object clone() {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new InternalError("But we are Cloneable!!!");
        }
    }

    protected Exp copy() {
        System.out.println("Shouldn't be calling Exp.copy() from type "
                + this.getClass());
        throw new RuntimeException();
        //return null;
    }

    public void prettyPrint() {
        System.out.println("Shouldn't be calling Exp.prettyPrint()!");
    }

    public MTType getMathType() {
        return myMathType;
    }

    public void setMathType(MTType mathType) {
        if (mathType == null) {
            System.err.println(this.toString());
            throw new RuntimeException("Null Math Type on: " + this.getClass());

        }

        myMathType = mathType;
    }

    public MTType getMathTypeValue() {
        return myMathTypeValue;
    }

    public void setMathTypeValue(MTType mathTypeValue) {
        myMathTypeValue = mathTypeValue;
    }

    //    public boolean isLocal() { return isLocal; }

    //    public void setIsLocal(boolean i) { isLocal = i; }

    public int getMarker() {
        return marker;
    }

    public void setMarker(int i) {
        marker = i;
    }

    /**
     * <p>Shallow compare is too weak for many things, and equals() is too
     * strict.  This method returns <code>true</code> <strong>iff</code> this
     * expression and the provided expression, <code>e</code>, are equivalent
     * with respect to structure and all function and variable names.</p>
     * 
     * @param e The expression to compare this one to.
     * @return True <strong>iff</strong> this expression and the provided
     *         expression are equivalent with respect to structure and all
     *         function and variable names.
     */
    public boolean equivalent(Exp e) {
        System.out.println(e.toString(1));
        throw new UnsupportedOperationException(
                "Equivalence for classes of type " + this.getClass()
                        + " is not currently supported.");
    }

    /**
     * <p>Helper method to deal with <code>Exps</code>s that need to be 
     * compared but might be null.  Returns true <strong>iff</strong> 
     * <code>e1</code> and <code>e2</code> are both <code>null</code> or both 
     * are not <code>null</code> and equivalent.</p>
     * 
     * @param e1 The first <code>Exp</code>.
     * @param e2 The second <code>Exp</code>.
     * @return <code>true</code> <strong>iff</strong> both 
     * 		   <code>Exps</code>s are null; or both are not null and are
     *         equivalent.
     */
    public static boolean equivalent(Exp e1, Exp e2) {
        return !((e1 == null ^ e2 == null))
                && ((e1 == null && e2 == null) || e1.equivalent(e2));
    }

    /**
     * <p>Helper method to deal with <code>PosSymbol</code>s that need to be 
     * compared but might be null.  Returns true <strong>iff</strong> 
     * <code>s1</code> and <code>s2</code> are both <code>null</code> or both 
     * are not <code>null</code> and have names that are equivalent strings (see
     * <code>stringEquivalent</code>())</p>
     * 
     * @param s1 The first <code>PosSymbol</code>.
     * @param s2 The second <code>PosSymbol</code>.
     * @return <code>true</code> <strong>iff</strong> both 
     * <code>PosSymbol</code>s are null; or both are not null and have names
     * that are equivalent strigns (see <code>stringEquivalent</code>()).
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
     * <p>Helper method to deal with strings that need to be compared but might
     * be null.  Returns true <strong>iff</strong> <code>s1</code> and 
     * <code>s2</code> are both <code>null</code> or both are not null and
     * represent the same string (case sensitive).</p>
     * 
     * @param s1 The first string.
     * @param s2 The second string.
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

    public boolean shallowCompare(Exp e2) {
        return false;
    }

    public Exp remember() {
        return this;
    }

    public boolean containsExp(Exp exp) {
        return false;
    }

    public boolean containsExistential() {
        boolean retval = false;

        for (Exp e : getSubExpressions()) {
            retval = e.containsExistential();
        }

        return retval;
    }

    public Exp compareWithAssumptions(Exp exp) {
        if (this.equals(exp))
            return getTrueVarExp(exp.getMathType().getTypeGraph());
        return this;
    }

    private PosSymbol createPosSymbol(String name) {
        PosSymbol posSym = new PosSymbol();
        posSym.setSymbol(Symbol.symbol(name));
        return posSym;
    }

    public static InfixExp buildImplication(Exp antecedent, Exp consequent) {
        return new InfixExp(antecedent.getLocation(), antecedent,
                new PosSymbol(antecedent.getLocation(), Symbol
                        .symbol("implies")), consequent);
    }

    public static InfixExp buildConjunction(Exp left, Exp right) {
        return new InfixExp(left.getLocation(), left, new PosSymbol(left
                .getLocation(), Symbol.symbol("and")), right);
    }

    public static VarExp getTrueVarExp(TypeGraph tg) {
        Symbol trueSym = Symbol.symbol("true");
        PosSymbol truePosSym = new PosSymbol();
        truePosSym.setSymbol(trueSym);
        VarExp trueExp = new VarExp(null, null, truePosSym);
        trueExp.setMathType(tg.BOOLEAN);

        return trueExp;
    }

    /**
     * <p>Oh, you poor bastard, you need to use <code>replace</code>.  Keep in
     * mind that if <code>replace</code> doesn't need to make any internal
     * replacements, it returns <code>null</code> to indicate "no change" rather
     * than returning the unmodified object.  Other code depends on this 
     * functionality so it can't be changed--if you return the unmodified 
     * object, things will start to misbehave.</p>
     * @param exp
     * @param old
     * @param replacement
     * @return 
     */
    public static Exp replace(Exp exp, Exp old, Exp replacement) {
        MTType originalType = exp.getMathType();
        MTType originalTypeValue = exp.getMathTypeValue();

        Exp result = exp.replace(old, replacement);

        if (result != null) {
            //If the subclass has set the internal types, we don't overwrite 
            //them--it's theoretically possible that the replacement changed the
            //type, but in most cases we just want to set the type to be the 
            //same
            if (originalType != null && result.getMathType() == null) {
                result.setMathType(originalType);
            }

            if (originalTypeValue != null && result.getMathTypeValue() == null) {

                result.setMathTypeValue(originalTypeValue);
            }
        }

        return result;
    }

    public static Exp copy(Exp exp) {
        MTType originalType = exp.getMathType();
        MTType originalTypeValue = exp.getMathTypeValue();
        Location originalLocation = exp.getLocation();

        Exp result = exp.copy();

        result.setMathType(originalType);
        result.setMathTypeValue(originalTypeValue);
        result.setLocation(originalLocation);

        return result;
    }

    /**
     * @deprecated Use {@link Exp#copy() Exp.copy()} instead.
     */
    @Deprecated
    public static Object clone(Exp object) {
        return Exp.copy(object);
    }

    public boolean isLiteralTrue() {
        boolean result = (this instanceof VarExp);

        result =
                result
                        && ((VarExp) this).getName().getName().equals("true")
                        && this.getMathType().equals(
                                this.getMathType().getTypeGraph().BOOLEAN);

        return result;
    }

    public boolean isLiteralFalse() {
        boolean result = (this instanceof VarExp);

        result =
                result
                        && ((VarExp) this).getName().getName().equals("false")
                        && this.getMathType().equals(
                                this.getMathType().getTypeGraph().BOOLEAN);

        return result;
    }

    public void setLocation(Location locatoin) {

    }
}

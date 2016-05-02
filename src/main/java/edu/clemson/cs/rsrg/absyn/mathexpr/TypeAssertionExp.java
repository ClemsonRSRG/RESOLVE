/**
 * TypeAssertionExp.java
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
package edu.clemson.cs.rsrg.absyn.mathexpr;

import edu.clemson.cs.rsrg.absyn.Exp;
import edu.clemson.cs.rsrg.absyn.rawtypes.ArbitraryExpTy;
import edu.clemson.cs.rsrg.parsing.data.Location;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>A <code>TypeAssertionExp</code>, generally, allows a sub-expression to be
 * asserted as belonging to some type.  It is permitted only in two places:
 * 
 * <ul>
 *      <li>Inside the hierarchy of an {@link ArbitraryExpTy ArbitraryExpTy}
 *          when that ty appears in the context of the type of a formal 
 *          parameter to a mathematical definition.</li>
 *      <li>Inside a {@link TypeTheoremDec TypeTheoremDec}, either at the top
 *          level of the overall assertion, or at the top level of the 
 *          consequent of a top-level <em>implies</em> expression.</li>
 * </ul>
 * 
 * <p>In the first case, it defines a slot to be bound implicitly by some 
 * component of the type of the actual parameter.  In this case, the expression
 * must be a {@link VarExp VarExp}.  Some examples:</p>
 * 
 * <pre>
 * Definition Foo(S : Str(T : Powerset(Z)) : T;
 * </pre>
 * 
 * <p>Here, the <code>T : Powerset(Z)</code> part is an implicit type parameter.
 * If one were to pass a <code>Str(N)</code> to <code>Foo</code>, then the 
 * return type would be <code>T</code>.  If one were to pass <code>Str(B)</code>
 * to <code>Foo</code> that would be a typechecking error, since <code>B</code>
 * is not in <code>Powerset(Z)</code>.</p>
 * 
 * <pre>
 * Definition Bar(L : Set(T : MType), E : Powerset(T)) : T;
 * </pre>
 * 
 * <p>Here, <code>T : MType</code> is an implicit type parameter, and the type
 * of both the second parameter and the return value are dependent on the 
 * binding of that parameter.  Implicit parameters are always bound left to 
 * right, so the first parameter's type may not depend on the second's.</p>
 * 
 * <p>In the second case, it asserts that any expression on the left side of the
 * colon is of the type on the right side, provided the antecedents of any
 * top-level implication are fulfilled.  In this case, the expression may be any
 * kind of expression.</p>
 *
 * @version 2.0
 */
public class TypeAssertionExp extends MathExp {

    // ===========================================================
    // Member Fields
    // ===========================================================

    /** <p>The type assertion's name</p> */
    private final Exp myExp;

    /** <p>The type assertion's raw type</p> */
    private final ArbitraryExpTy myAssertedTy;

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>This constructs a type assertion expression.</p>
     *
     * @param l A {@link Location} representation object.
     * @param name Name as an {@link Exp} representation object.
     * @param ty A raw type {@link ArbitraryExpTy} representation object.
     */
    public TypeAssertionExp(Location l, Exp name, ArbitraryExpTy ty) {
        super(l);
        myExp = name;
        myAssertedTy = ty;
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
        return myExp.asString(indentSize + innerIndentSize, innerIndentSize)
                + " : "
                + myAssertedTy.asString(indentSize + innerIndentSize,
                        innerIndentSize);
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
    public final boolean containsExp(Exp exp) {
        return false;
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
        return myExp.containsVar(varName, IsOldExp);
    }

    /**
     * <p>This method overrides the default equals method implementation
     * for the {@link TypeAssertionExp} class.</p>
     *
     * @param o Object to be compared.
     * @return True if all the fields are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        boolean result = false;
        if (o instanceof TypeAssertionExp) {
            TypeAssertionExp eAsTypeAssertionExp = (TypeAssertionExp) o;
            result = myLoc.equals(eAsTypeAssertionExp.myLoc);

            if (result) {
                result = myExp.equals(eAsTypeAssertionExp.myExp);
                result &= myAssertedTy.equals(eAsTypeAssertionExp.myAssertedTy);
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
        boolean retval = (e instanceof TypeAssertionExp);
        if (retval) {
            TypeAssertionExp eAsTypeAssertionExp = (TypeAssertionExp) e;
            retval = myExp.equivalent(eAsTypeAssertionExp.myExp);
            retval &= myAssertedTy.equals(eAsTypeAssertionExp.myAssertedTy);
        }

        return retval;
    }

    /**
     * <p>This method returns the type assertion's raw type.</p>
     *
     * @return The {@link ArbitraryExpTy} raw type.
     */
    public final ArbitraryExpTy getAssertedTy() {
        return (ArbitraryExpTy) myAssertedTy.clone();
    }

    /**
     * <p>This method returns the type assertion's expression.</p>
     *
     * @return The {@link Exp} object.
     */
    public final Exp getExp() {
        return myExp.clone();
    }

    /**
     * <p>This method method returns a deep copy of the list of
     * subexpressions.</p>
     *
     * @return A list containing subexpressions ({@link Exp}s).
     */
    @Override
    public List<Exp> getSubExpressions() {
        return new ArrayList(Collections.singletonList(myExp));
    }

    /**
     * <p>This method applies VC Generator's remember rule.
     * For all inherited programming expression classes, this method
     * should throw an exception.</p>
     *
     * @return The resulting {@link TypeAssertionExp} from applying the remember rule.
     */
    @Override
    public TypeAssertionExp remember() {
        return (TypeAssertionExp) this.clone();
    }

    /**
     * <p>This method adds a new expression to our list of subexpressions.</p>
     *
     * @param index The index in our subexpression list.
     * @param e The new {@link Exp} to be added.
     */
    // TODO: See the message in Exp.
    /*public void setSubExpression(int index, Exp e) {
        myExp = e;
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
        sb.append(myExp.toString());
        sb.append("\t");
        sb.append(myAssertedTy.toString());

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
        return new TypeAssertionExp(new Location(myLoc), getExp(),
                getAssertedTy());
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
        return new TypeAssertionExp(new Location(myLoc), substitute(myExp,
                substitutions), new ArbitraryExpTy(myAssertedTy
                .getArbitraryExp().substitute(substitutions)));
    }

}
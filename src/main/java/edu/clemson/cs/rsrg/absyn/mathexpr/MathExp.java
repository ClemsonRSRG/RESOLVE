/**
 * MathExp.java
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

import edu.clemson.cs.r2jt.typereasoning2.TypeGraph;
import edu.clemson.cs.rsrg.absyn.Exp;
import edu.clemson.cs.rsrg.errorhandling.exception.MiscErrorException;
import edu.clemson.cs.rsrg.errorhandling.exception.NullMathTypeException;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import java.util.*;

/**
 * <p>This is the abstract base class for all the mathematical expressions
 * that the compiler builds from the ANTLR4 AST tree.</p>
 *
 * @version 2.0
 */
public abstract class MathExp extends Exp {

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>A helper constructor that allow us to store the location
     * of the created object directly in the this class.</p>
     *
     * @param l A {@link Location} representation object.
     */
    protected MathExp(Location l) {
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
    public final MathExp clone() {
        return (MathExp) super.clone();
    }

    /**
     * <p>This static method makes sure that our types are set,
     * before attempting to form a conjunct.</p>
     *
     * @param e1 The first {@link Exp} representation object.
     * @param e2 The second {@link Exp} representation object.
     *
     * @return The resulting {@link Exp}.
     */
    public static MathExp formConjunct(Location l, Exp e1, Exp e2) {
        if (e1.getMathType() == null) {
            throw new NullMathTypeException("The math type for "
                    + e1.toString() + " is null.");
        }
        else if (e2.getMathType() == null) {
            throw new NullMathTypeException("The math type for "
                    + e2.toString() + " is null.");
        }

        TypeGraph typeGraph = e1.getMathType().getTypeGraph();
        if (typeGraph == null) {
            throw new MiscErrorException("The type graph is null.",
                    new NullPointerException());
        }

        MathExp retval =
                new InfixExp(new Location(l), null, e1.clone(), new PosSymbol(
                        new Location(l), "and"), e2.clone());
        retval.setMathType(typeGraph.BOOLEAN);

        return retval;
    }

    /**
     * <p>This static method makes sure that our types are set,
     * before attempting to form a disjunct.</p>
     *
     * @param e1 The first {@link Exp} representation object.
     * @param e2 The second {@link Exp} representation object.
     *
     * @return The resulting {@link Exp}.
     */
    public static MathExp formDisjunct(Location l, Exp e1, Exp e2) {
        if (e1.getMathType() == null) {
            throw new NullMathTypeException("The math type for "
                    + e1.toString() + " is null.");
        }
        else if (e2.getMathType() == null) {
            throw new NullMathTypeException("The math type for "
                    + e2.toString() + " is null.");
        }

        TypeGraph typeGraph = e1.getMathType().getTypeGraph();
        if (typeGraph == null) {
            throw new MiscErrorException("The type graph is null.",
                    new NullPointerException());
        }

        MathExp retval =
                new InfixExp(new Location(l), null, e1.clone(), new PosSymbol(
                        new Location(l), "or"), e2.clone());
        retval.setMathType(typeGraph.BOOLEAN);

        return retval;
    }

    /**
     * <p>This static method makes sure that our types are set,
     * before attempting to form an implication.</p>
     *
     * @param e1 The first {@link Exp} representation object.
     * @param e2 The second {@link Exp} representation object.
     *
     * @return The resulting {@link Exp}.
     */
    public static MathExp formImplies(Location l, Exp e1, Exp e2) {
        if (e1.getMathType() == null) {
            throw new NullMathTypeException("The math type for "
                    + e1.toString() + " is null.");
        }
        else if (e2.getMathType() == null) {
            throw new NullMathTypeException("The math type for "
                    + e2.toString() + " is null.");
        }

        TypeGraph typeGraph = e1.getMathType().getTypeGraph();
        if (typeGraph == null) {
            throw new MiscErrorException("The type graph is null.",
                    new NullPointerException());
        }

        MathExp retval =
                new InfixExp(new Location(l), null, e1.clone(), new PosSymbol(
                        new Location(l), "implies"), e2.clone());
        retval.setMathType(typeGraph.BOOLEAN);

        return retval;
    }

    /**
     * <p>This static method method creates a variable expression that
     * matches the boolean "true".</p>
     *
     * @param l A {@link Location} where the representation object is created from.
     * @param tg A {@link TypeGraph} to retrieve the mathematical boolean type.
     *
     * @return The {@link VarExp} representation object.
     */
    public static VarExp getTrueVarExp(Location l, TypeGraph tg) {
        VarExp retval =
                new VarExp(new Location(l), null, new PosSymbol(
                        new Location(l), "true"));
        retval.setMathType(tg.BOOLEAN);

        return retval;
    }

    /**
     * <p>This method returns the default behavior for an
     * arbitrary {@link Exp} when checking to see if we are
     * simply the literal "true".</p>
     *
     * @return True if <code>exp</code> contains "true",
     * false otherwise.
     */
    public static boolean isLiteralTrue(Exp exp) {
        boolean retval = (exp instanceof VarExp);
        if (retval) {
            VarExp eAsVarExp = (VarExp) exp;
            retval = stringEquivalent(eAsVarExp.getName().getName(), "true");
        }

        return retval;
    }

    /**
     * <p>This method returns the default behavior for an
     * arbitrary {@link Exp} when checking to see if we are
     * simply the literal "false".</p>
     *
     * @return True if <code>exp</code> contains "false",
     * false otherwise.
     */
    public static boolean isLiteralFalse(Exp exp) {
        boolean retval = (exp instanceof VarExp);
        if (retval) {
            VarExp eAsVarExp = (VarExp) exp;
            retval = stringEquivalent(eAsVarExp.getName().getName(), "false");
        }

        return retval;
    }

    /**
     * <p>This method must be implemented by the inherited mathematical
     * expression classes to apply VC Generator's remember rule.
     * For all inherited programming expression classes, this method
     * should throw an exception.</p>
     *
     * @return The resulting {@link Exp} from applying the remember rule.
     */
    public abstract Exp remember();

    /**
     * <p>This method must be implemented by the inherited mathematical
     * expression classes to apply VC Generator's simplification step.
     * For all inherited programming expression classes, this method
     * should throw an exception.</p>
     *
     * @return The resulting {@link Exp} from applying the simplification step.
     */
    public abstract Exp simplify();

    /**
     * <p>This method is used to convert a {@link Exp} into the prover's
     * version of {@link PExp}. By default, this method throws an exception
     * unless the concrete subclass overrides this method.</p>
     *
     * @param assumpts The assumption expressions for this expression.
     * @param single Boolean flag to indicate whether or not this is a
     *               standalone expression.
     *
     * @return A list of {link Exp} objects.
     */
    public List<InfixExp> split(MathExp assumpts, boolean single) {
        throw new UnsupportedOperationException("Split for classes of type "
                + this.getClass() + " is not currently supported.");
    }

}
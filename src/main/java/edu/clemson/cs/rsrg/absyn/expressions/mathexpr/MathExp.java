/*
 * MathExp.java
 * ---------------------------------
 * Copyright (c) 2017
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.rsrg.absyn.expressions.mathexpr;

import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import edu.clemson.cs.rsrg.statushandling.exception.MiscErrorException;
import edu.clemson.cs.rsrg.typeandpopulate.exception.NullMathTypeException;
import edu.clemson.cs.rsrg.typeandpopulate.typereasoning.TypeGraph;

/**
 * <p>This is the abstract base class for all the mathematical expression objects
 * that the compiler builds using the ANTLR4 AST nodes.</p>
 *
 * @version 1.0
 */
public abstract class MathExp extends Exp {

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * <p>An helper constructor that allow us to store the location
     * of any objects created from a class that inherits from
     * {@code MathExp}.</p>
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
     * for all the classes that extend from {@link MathExp}.</p>
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
     * @param l A {@link Location} for the new conjuncted expression.
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

        // Attempt to find a location for the conjunct
        Location newExpLoc = cloneLocation(l);
        if (newExpLoc == null) {
            newExpLoc = cloneLocation(e1.getLocation());

            if (newExpLoc == null) {
                newExpLoc = cloneLocation(e2.getLocation());
            }
        }
        Location newPosSymbolLoc = cloneLocation(newExpLoc);

        MathExp retval =
                new InfixExp(newExpLoc, e1.clone(), null, new PosSymbol(
                        newPosSymbolLoc, "and"), e2.clone());
        retval.setMathType(typeGraph.BOOLEAN);

        return retval;
    }

    /**
     * <p>This static method makes sure that our types are set,
     * before attempting to form a disjunct.</p>
     *
     * @param l A {@link Location} for the new disjuncted expression.
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

        // Attempt to find a location for the conjunct
        Location newExpLoc = cloneLocation(l);
        if (newExpLoc == null) {
            newExpLoc = cloneLocation(e1.getLocation());

            if (newExpLoc == null) {
                newExpLoc = cloneLocation(e2.getLocation());
            }
        }
        Location newPosSymbolLoc = cloneLocation(newExpLoc);

        MathExp retval =
                new InfixExp(newExpLoc, e1.clone(), null, new PosSymbol(
                        newPosSymbolLoc, "or"), e2.clone());
        retval.setMathType(typeGraph.BOOLEAN);

        return retval;
    }

    /**
     * <p>This static method makes sure that our types are set,
     * before attempting to form an implication.</p>
     *
     * @param l A {@link Location} for the new implied expression.
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

        // Attempt to find a location for the conjunct
        Location newExpLoc = cloneLocation(l);
        if (newExpLoc == null) {
            newExpLoc = cloneLocation(e1.getLocation());

            if (newExpLoc == null) {
                newExpLoc = cloneLocation(e2.getLocation());
            }
        }
        Location newPosSymbolLoc = cloneLocation(newExpLoc);

        MathExp retval =
                new InfixExp(newExpLoc, e1.clone(), null, new PosSymbol(
                        newPosSymbolLoc, "implies"), e2.clone());
        retval.setMathType(typeGraph.BOOLEAN);

        return retval;
    }

    /**
     * <p>This static method method creates a variable expression that
     * matches the boolean {@code false}.</p>
     *
     * @param l A {@link Location} for the new {@code false} expression.
     * @param tg A {@link TypeGraph} to retrieve the mathematical boolean type.
     *
     * @return The {@link VarExp} representation object.
     */
    public static VarExp getFalseVarExp(Location l, TypeGraph tg) {
        // Attempt to find a location for the conjunct
        Location newExpLoc = cloneLocation(l);
        Location newPosSymbolLoc = cloneLocation(l);

        VarExp retval =
                new VarExp(newExpLoc, null, new PosSymbol(newPosSymbolLoc,
                        "false"));
        retval.setMathType(tg.BOOLEAN);

        return retval;
    }

    /**
     * <p>This static method method creates a variable expression that
     * matches the boolean {@code true}.</p>
     *
     * @param l A {@link Location} for the new {@code true} expression.
     * @param tg A {@link TypeGraph} to retrieve the mathematical boolean type.
     *
     * @return The {@link VarExp} representation object.
     */
    public static VarExp getTrueVarExp(Location l, TypeGraph tg) {
        // Attempt to find a location for the conjunct
        Location newExpLoc = cloneLocation(l);
        Location newPosSymbolLoc = cloneLocation(l);

        VarExp retval =
                new VarExp(newExpLoc, null, new PosSymbol(newPosSymbolLoc,
                        "true"));
        retval.setMathType(tg.BOOLEAN);

        return retval;
    }

    /**
     * <p>This method returns the default behavior for an
     * arbitrary {@link Exp} when checking to see if we are
     * simply the literal {@code true}.</p>
     *
     * @param exp An {@link Exp} to be evaluated.
     *
     * @return {@code true} if {@code exp} is the literal true expression,
     * {@code false} otherwise.
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
     * simply the literal {@code false}.</p>
     *
     * @param exp An {@link Exp} to be evaluated.
     *
     * @return {@code true} if {@code exp} is the literal false expression,
     * {@code false} otherwise.
     */
    public static boolean isLiteralFalse(Exp exp) {
        boolean retval = (exp instanceof VarExp);
        if (retval) {
            VarExp eAsVarExp = (VarExp) exp;
            retval = stringEquivalent(eAsVarExp.getName().getName(), "false");
        }

        return retval;
    }

    // ===========================================================
    // Private Methods
    // ===========================================================

    /**
     * <p>An helper method to clone a {@link Location}.</p>
     *
     * @param l The location we want to make a copy of.
     *
     * @return A deep copy of {@code l} or {@code null} if {@code l}
     * is {@code null}.
     */
    private static Location cloneLocation(Location l) {
        return l != null ? l.clone() : null;
    }

}

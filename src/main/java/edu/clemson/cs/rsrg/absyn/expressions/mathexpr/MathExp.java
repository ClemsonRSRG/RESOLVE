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
package edu.clemson.cs.rsrg.absyn.expressions.mathexpr;

import edu.clemson.cs.r2jt.rewriteprover.absyn.PExp;
import edu.clemson.cs.rsrg.absyn.expressions.Exp;
import edu.clemson.cs.rsrg.parsing.data.Location;
import edu.clemson.cs.rsrg.parsing.data.PosSymbol;
import edu.clemson.cs.rsrg.statushandling.exception.MiscErrorException;
import edu.clemson.cs.rsrg.typeandpopulate.exception.NullMathTypeException;
import edu.clemson.cs.rsrg.typeandpopulate.typereasoning.TypeGraph;
import java.util.*;

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
     * @param e1 The first {@link Exp} representation object.
     * @param e2 The second {@link Exp} representation object.
     *
     * @return The resulting {@link Exp}.
     */
    public final static MathExp formConjunct(Location l, Exp e1, Exp e2) {
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
                new InfixExp(new Location(l), e1.clone(), null, new PosSymbol(
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
    public final static MathExp formDisjunct(Location l, Exp e1, Exp e2) {
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
                new InfixExp(new Location(l), e1.clone(), null, new PosSymbol(
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
    public final static MathExp formImplies(Location l, Exp e1, Exp e2) {
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
                new InfixExp(new Location(l), e1.clone(), null, new PosSymbol(
                        new Location(l), "implies"), e2.clone());
        retval.setMathType(typeGraph.BOOLEAN);

        return retval;
    }

    /**
     * <p>This static method method creates a variable expression that
     * matches the boolean {@code false}.</p>
     *
     * @param l A {@link Location} where the representation object is created from.
     * @param tg A {@link TypeGraph} to retrieve the mathematical boolean type.
     *
     * @return The {@link VarExp} representation object.
     */
    public final static VarExp getFalseVarExp(Location l, TypeGraph tg) {
        VarExp retval =
                new VarExp(new Location(l), null, new PosSymbol(
                        new Location(l), "false"));
        retval.setMathType(tg.BOOLEAN);

        return retval;
    }

    /**
     * <p>This static method method creates a variable expression that
     * matches the boolean {@code true}.</p>
     *
     * @param l A {@link Location} where the representation object is created from.
     * @param tg A {@link TypeGraph} to retrieve the mathematical boolean type.
     *
     * @return The {@link VarExp} representation object.
     */
    public final static VarExp getTrueVarExp(Location l, TypeGraph tg) {
        VarExp retval =
                new VarExp(new Location(l), null, new PosSymbol(
                        new Location(l), "true"));
        retval.setMathType(tg.BOOLEAN);

        return retval;
    }

    /**
     * <p>This method returns the default behavior for an
     * arbitrary {@link Exp} when checking to see if we are
     * simply the literal {@code true}.</p>
     *
     * @return True if {@code exp}contains {@code true},
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
     * simply the literal {@code false}.</p>
     *
     * @return True if {@code exp} contains {@code false},
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
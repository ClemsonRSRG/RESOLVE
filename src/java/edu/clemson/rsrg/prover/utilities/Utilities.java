/*
 * Utilities.java
 * ---------------------------------
 * Copyright (c) 2022
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.rsrg.prover.utilities;

import edu.clemson.rsrg.prover.absyn.PExp;
import edu.clemson.rsrg.prover.absyn.expressions.PSymbol;
import edu.clemson.rsrg.typeandpopulate.mathtypes.MTType;
import edu.clemson.rsrg.typeandpopulate.typereasoning.TypeGraph;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * This class contains transformations that apply to both expressions in both {@code VC} and {@code Theorems}.
 * </p>
 *
 * @author Mike Khabbani
 *
 * @version 2.0
 */
public class Utilities {

    // ===========================================================
    // Public Methods
    // ===========================================================

    /**
     * <p>
     * This method creates an expression with the proper substitutions.
     * </p>
     *
     * @param p
     *            A prover expression.
     * @param g
     *            The current type graph.
     * @param z
     *            Mathematical type representing integers.
     * @param n
     *            Mathematical type representing natural numbers.
     *
     * @return The proper prover expression with any substitutions.
     */
    public static PExp replacePExp(PExp p, TypeGraph g, MTType z, MTType n) {
        List<PExp> argList = new ArrayList<>();
        List<PExp> argsTemp = new ArrayList<>();
        for (PExp pa : p.getSubExpressions()) {
            argList.add(replacePExp(pa, g, z, n));
        }

        String pTop = p.getTopLevelOperation();
        if (pTop.equals("/=")) {
            PSymbol eqExp = new PSymbol(g.BOOLEAN, null, "=B", argList);
            argList.clear();
            argList.add(eqExp);
            argList.add(new PSymbol(g.BOOLEAN, null, "false"));

            return new PSymbol(g.BOOLEAN, null, "=B", argList);
        } else if (pTop.equals("not")) {
            argList.add(new PSymbol(g.BOOLEAN, null, "false"));
            PSymbol pEqFalse = new PSymbol(g.BOOLEAN, null, "=B", argList);

            return pEqFalse;
        } else if (pTop.equals(">=")) {
            argsTemp.add(argList.get(1));
            argsTemp.add(argList.get(0));

            return new PSymbol(g.BOOLEAN, null, "<=B", argsTemp);
        } else if (pTop.equals("<") && z != null && n != null && argList.get(0).getMathType().isSubtypeOf(z)
                && argList.get(1).getMathType().isSubtypeOf(z)) {
            // x < y to x + 1 <= y
            argsTemp.add(argList.get(0));
            argsTemp.add(new PSymbol(n, null, "1"));
            PSymbol plus1 = new PSymbol(argList.get(0).getMathType(), null,
                    "+" + argList.get(0).getMathType().toString(), argsTemp);
            argsTemp.clear();
            argsTemp.add(plus1);
            argsTemp.add(argList.get(1));

            return new PSymbol(p.getMathType(), p.getMathTypeValue(), "<=B", argsTemp);
        } else if (pTop.equals(">") && z != null && n != null && argList.get(0).getMathType().isSubtypeOf(z)
                && argList.get(1).getMathType().isSubtypeOf(z)) {
            // x > y to y + 1 <= x
            argsTemp.add(argList.get(1));
            argsTemp.add(new PSymbol(n, null, "1"));
            PSymbol plus1 = new PSymbol(argList.get(1).getMathType(), null,
                    "+" + argList.get(1).getMathType().toString(), argsTemp);
            argsTemp.clear();
            argsTemp.add(plus1);
            argsTemp.add(argList.get(0));

            return new PSymbol(p.getMathType(), p.getMathTypeValue(), "<=B", argsTemp);
        } else if (z != null && pTop.equals("-") && p.getSubExpressions().size() == 2) {
            // x - y to x + (-y)
            argsTemp.add(argList.get(1));
            PSymbol minusY = new PSymbol(p.getMathType(), null, "-" + p.getMathType().toString(), argsTemp);
            argsTemp.clear();
            argsTemp.add(argList.get(0));
            argsTemp.add(minusY);

            return new PSymbol(p.getMathType(), null, "+" + p.getMathType().toString(), argsTemp);
        }
        // New: 5/8/16. Tag operators with range type if they aren't quantified.
        else if (argList.size() > 0) {
            if (((PSymbol) p).quantification.equals(PSymbol.Quantification.NONE))
                pTop += p.getMathType().toString();

            return new PSymbol(p.getMathType(), p.getMathTypeValue(), pTop, argList, ((PSymbol) p).quantification);
        }

        return p;
    }
}

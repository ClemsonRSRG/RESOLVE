/**
 * Utilities.java
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
package edu.clemson.cs.r2jt.congruenceclassprover;

import edu.clemson.cs.r2jt.rewriteprover.absyn.PExp;
import edu.clemson.cs.r2jt.rewriteprover.absyn.PSymbol;
import edu.clemson.cs.r2jt.typeandpopulate.MTType;
import edu.clemson.cs.r2jt.typereasoning.TypeGraph;

import java.util.ArrayList;

/**
 * Created by Mike on 2/1/2016.
 */
public class Utilities {

    public static PExp replacePExp(PExp p, TypeGraph g, MTType z, MTType n) {
        ArrayList<PExp> argList = new ArrayList<PExp>();
        ArrayList<PExp> argsTemp = new ArrayList<PExp>();
        for (PExp pa : p.getSubExpressions()) {
            argList.add(replacePExp(pa, g, z, n));
        }
        String pTop = p.getTopLevelOperation();
        if (pTop.equals("/=")) {
            PSymbol eqExp = new PSymbol(g.BOOLEAN, null, "=", argList);
            argList.clear();
            argList.add(eqExp);
            PSymbol notEqExp = new PSymbol(g.BOOLEAN, null, "not", argList);
            return notEqExp;
        }
        else if (pTop.equals(">=")) {
            argsTemp.add(argList.get(1));
            argsTemp.add(argList.get(0));
            return new PSymbol(g.BOOLEAN, null, "<=", argsTemp);
        }
        else if (pTop.equals("<") && z != null && n != null
                && argList.get(0).getType().isSubtypeOf(z)
                && argList.get(1).getType().isSubtypeOf(z)) {
            // x < y to x + 1 <= y
            argsTemp.add(argList.get(0));
            argsTemp.add(new PSymbol(n, null, "1"));
            PSymbol plus1 =
                    new PSymbol(p.getType(), p.getTypeValue(), "+", argsTemp);
            argsTemp.clear();
            argsTemp.add(plus1);
            argsTemp.add(argList.get(1));
            return new PSymbol(p.getType(), p.getTypeValue(), "<=", argsTemp);
        }
        else if (pTop.equals(">") && z != null && n != null
                && argList.get(0).getType().isSubtypeOf(z)
                && argList.get(1).getType().isSubtypeOf(z)) {
            // x > y to y + 1 <= x
            argsTemp.add(argList.get(1));
            argsTemp.add(new PSymbol(n, null, "1"));
            PSymbol plus1 =
                    new PSymbol(p.getType(), p.getTypeValue(), "+", argsTemp);
            argsTemp.clear();
            argsTemp.add(plus1);
            argsTemp.add(argList.get(0));
            return new PSymbol(p.getType(), p.getTypeValue(), "<=", argsTemp);
        }
        else if (z != null && pTop.equals("-")
                && p.getSubExpressions().size() == 2) {
            // x - y to x + (-y)
            argsTemp.add(argList.get(1));
            PSymbol minusY =
                    new PSymbol(z, null, "-", argsTemp);
            argsTemp.clear();
            argsTemp.add(argList.get(0));
            argsTemp.add(minusY);
            return new PSymbol(z, null, "+", argsTemp);
        }
        return new PSymbol(p.getType(), p.getTypeValue(), p
                .getTopLevelOperation(), argList, ((PSymbol) p).quantification);
    }
}

/**
 * HardCoded2.java
 * ---------------------------------
 * Copyright (c) 2014
 * RESOLVE Software Research Group
 * School of Computing
 * Clemson University
 * All rights reserved.
 * ---------------------------------
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package edu.clemson.cs.r2jt.misc;

import edu.clemson.cs.r2jt.absynnew.expr.MathSymbolAST;
import edu.clemson.cs.r2jt.absynnew.expr.MathSymbolAST.MathSymbolExprBuilder;
import edu.clemson.cs.r2jt.typeandpopulate2.DuplicateSymbolException;
import edu.clemson.cs.r2jt.typeandpopulate2.MTFunction;
import edu.clemson.cs.r2jt.typeandpopulate2.ScopeBuilder;
import edu.clemson.cs.r2jt.typereasoning2.TypeGraph;

public class HardCoded2 {

    /**
     * <p>This method establishes all built-in symbols of the symbol table.</p>
     */
    public static void addBuiltInSymbols(TypeGraph g, ScopeBuilder b) {
        MathSymbolAST v = new MathSymbolExprBuilder("native").build();

        try {
            b.addBinding("Entity", v, g.CLS, g.ENTITY);
            b.addBinding("MType", v, g.CLS, g.CLS);
            b.addBinding("Cls", v, g.CLS, g.CLS);

            b.addBinding("Instance_Of", v, new MTFunction(g, g.BOOLEAN, g.CLS,
                    g.ENTITY));

            b.addBinding("SSet", v, g.CLS, g.CLS);
            b.addBinding("B", v, g.CLS, g.BOOLEAN);

            b.addBinding("Card", v, g.CLS, g.CARD);
            b.addBinding("union", v, g.UNION);
            b.addBinding("intersect", v, g.INTERSECT);

            b.addBinding("||...||", v, new MTFunction(g, g.CARD, g.CLS));
            b.addBinding("~", v, new MTFunction(g, g.SET, g.CLS, g.ENTITY));
            b.addBinding("is_not_in", v, new MTFunction(g, g.BOOLEAN, g.ENTITY,
                    g.ENTITY));
            b.addBinding("is_in", v, new MTFunction(g, g.BOOLEAN, g.ENTITY,
                    g.ENTITY));

            b.addBinding("Empty_Set", v, g.CLS, g.EMPTY_SET);
            b.addBinding("Powerset", v, g.POWERTYPE);
            b.addBinding("Powerclass", v, g.POWERCLASS);
            b.addBinding("true", v, g.BOOLEAN);
            b.addBinding("false", v, g.BOOLEAN);
            b.addBinding("->", v, g.FUNCTION);
            b.addBinding("and", v, g.AND);
            b.addBinding("not", v, g.NOT);
            b.addBinding("*", v, g.CROSS);

            b.addBinding("=", v, new MTFunction(g, g.BOOLEAN, g.ENTITY,
                    g.ENTITY));
            b.addBinding("/=", v, new MTFunction(g, g.BOOLEAN, g.ENTITY,
                    g.ENTITY));
            b.addBinding("or", v, new MTFunction(g, g.BOOLEAN, g.BOOLEAN,
                    g.BOOLEAN));

            b.addBinding("Z", v, g.CLS, g.Z);
            b.addBinding("-", v, new MTFunction(g, g.Z, g.Z));
        }
        catch (DuplicateSymbolException dse) {
            //Not possible--we're the first ones to add anything
            throw new RuntimeException(dse);
        }
    }
}
